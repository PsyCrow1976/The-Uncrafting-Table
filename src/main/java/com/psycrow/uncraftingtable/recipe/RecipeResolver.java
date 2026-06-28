package com.psycrow.uncraftingtable.recipe;

import com.psycrow.uncraftingtable.config.ModConfig;
import com.psycrow.uncraftingtable.util.UncraftingDebug;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.NormalCraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.ShapedCraftingRecipeDisplay;
import net.minecraft.world.item.crafting.display.ShapelessCraftingRecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplayContext;

public final class RecipeResolver {
    private static final int PREVIEW_WIDTH = 3;

    private RecipeResolver() {}

    public static List<ResolvedRecipe> resolve(RecipeManager recipeManager, HolderLookup.Provider registries, ItemStack input) {
        if (input.isEmpty()) {
            UncraftingDebug.log("resolve: input is empty, skipping lookup");
            return List.of();
        }

        Identifier inputId = BuiltInRegistries.ITEM.getKey(input.getItem());
        var craftingRecipes = recipeManager.recipeMap().byType(RecipeType.CRAFTING);
        UncraftingDebug.log(
                "resolve: input={} craftingRecipeCount={}",
                inputId,
                craftingRecipes.size());

        ContextMap context = new ContextMap.Builder()
                .withParameter(SlotDisplayContext.REGISTRIES, registries)
                .create(SlotDisplayContext.CONTEXT);

        List<ResolvedRecipe> results = new ArrayList<>();
        int examined = 0;
        int skippedType = 0;
        int skippedPlacement = 0;
        int skippedException = 0;

        for (RecipeHolder<CraftingRecipe> holder : craftingRecipes) {
            try {
                CraftingRecipe craftingRecipe = holder.value();
                if (!(craftingRecipe instanceof NormalCraftingRecipe normalRecipe)) {
                    skippedType++;
                    continue;
                }

                if (normalRecipe.placementInfo().isImpossibleToPlace()) {
                    skippedPlacement++;
                    continue;
                }

                examined++;
                ResolvedRecipe resolved = resolveFromDisplay(holder, normalRecipe, input, context);
                String source = "display";
                if (resolved == null) {
                    resolved = resolveFromPlacement(holder, normalRecipe, input, context);
                    source = "placement";
                }

                if (resolved != null) {
                    if (!isMeaningfulUncraft(resolved, input)) {
                        UncraftingDebug.log(
                                "resolve: skipped recipe={} via={} reason=not a meaningful uncraft (outputs or ingredients match input)",
                                holder.id().identifier(),
                                source);
                        continue;
                    }
                    results.add(resolved);
                    UncraftingDebug.log(
                            "resolve: matched recipe={} via={} outputCount={}",
                            holder.id().identifier(),
                            source,
                            resolved.outputs().size());
                }
            } catch (RuntimeException exception) {
                skippedException++;
                if (ModConfig.DEBUG.get()) {
                    UncraftingDebug.log(
                            "resolve: skipped recipe={} reason={}",
                            holder.id().identifier(),
                            exception.toString());
                }
            }
        }

        UncraftingDebug.log(
                "resolve: finished input={} matches={} examined={} skippedType={} skippedPlacement={} skippedException={}",
                inputId,
                results.size(),
                examined,
                skippedType,
                skippedPlacement,
                skippedException);

        if (ModConfig.DEBUG.get() && !results.isEmpty()) {
            ResolvedRecipe first = results.getFirst();
            for (int slot = 0; slot < first.previewGrid().length; slot++) {
                ItemStack stack = first.previewGrid()[slot];
                if (stack != null && !stack.isEmpty()) {
                    UncraftingDebug.log(
                            "resolve: preview slot {} = {} x{}",
                            slot,
                            BuiltInRegistries.ITEM.getKey(stack.getItem()),
                            stack.getCount());
                }
            }
        }

        return results;
    }

    private static ResolvedRecipe resolveFromDisplay(
            RecipeHolder<CraftingRecipe> holder,
            NormalCraftingRecipe recipe,
            ItemStack input,
            ContextMap context) {
        for (RecipeDisplay display : recipe.display()) {
            if (display instanceof ShapedCraftingRecipeDisplay shaped) {
                ItemStack result = shaped.result().resolveForFirstStack(context);
                if (matchesInput(result, input)) {
                    logCandidate(holder, "shaped-display", result);
                    ItemStack[] previewGrid = previewFromShapedDisplay(shaped, context);
                    List<ItemStack> outputs = collectOutputs(previewGrid);
                    if (!outputs.isEmpty()) {
                        return new ResolvedRecipe(holder, previewGrid, outputs);
                    }
                    logCandidateFailure(holder, "shaped-display", "preview outputs empty");
                }
            } else if (display instanceof ShapelessCraftingRecipeDisplay shapeless) {
                ItemStack result = shapeless.result().resolveForFirstStack(context);
                if (matchesInput(result, input)) {
                    logCandidate(holder, "shapeless-display", result);
                    ItemStack[] previewGrid = previewFromShapelessDisplay(shapeless, context);
                    List<ItemStack> outputs = collectOutputs(previewGrid);
                    if (!outputs.isEmpty()) {
                        return new ResolvedRecipe(holder, previewGrid, outputs);
                    }
                    logCandidateFailure(holder, "shapeless-display", "preview outputs empty");
                }
            }
        }

        return null;
    }

    private static ResolvedRecipe resolveFromPlacement(
            RecipeHolder<CraftingRecipe> holder,
            NormalCraftingRecipe recipe,
            ItemStack input,
            ContextMap context) {
        ItemStack result;
        try {
            result = recipe.assemble(CraftingInput.EMPTY);
        } catch (RuntimeException exception) {
            logCandidateFailure(holder, "placement-assemble", exception.toString());
            return null;
        }

        if (!matchesInput(result, input)) {
            return null;
        }

        logCandidate(holder, "placement", result);
        ItemStack[] previewGrid = previewFromPlacement(recipe, context);
        List<ItemStack> outputs = collectOutputs(previewGrid);
        if (outputs.isEmpty()) {
            logCandidateFailure(holder, "placement", "preview outputs empty");
            return null;
        }

        return new ResolvedRecipe(holder, previewGrid, outputs);
    }

    private static void logCandidate(RecipeHolder<CraftingRecipe> holder, String source, ItemStack result) {
        if (!ModConfig.DEBUG.get()) {
            return;
        }

        UncraftingDebug.log(
                "resolve: candidate recipe={} source={} result={}",
                holder.id().identifier(),
                source,
                BuiltInRegistries.ITEM.getKey(result.getItem()));
    }

    private static void logCandidateFailure(RecipeHolder<CraftingRecipe> holder, String source, String reason) {
        if (!ModConfig.DEBUG.get()) {
            return;
        }

        UncraftingDebug.log(
                "resolve: candidate failed recipe={} source={} reason={}",
                holder.id().identifier(),
                source,
                reason);
    }

    private static boolean matchesInput(ItemStack result, ItemStack input) {
        return !result.isEmpty() && ItemStack.isSameItem(result, input);
    }

    /**
     * Reject recipes that would return the same item (e.g. input is only an ingredient display
     * resolved back to itself). Uncrafting requires recovering different ingredients than the input.
     */
    private static boolean isMeaningfulUncraft(ResolvedRecipe resolved, ItemStack input) {
        List<ItemStack> outputs = resolved.outputs();
        if (outputs.isEmpty()) {
            return false;
        }

        boolean hasDifferentOutput = false;
        for (ItemStack output : outputs) {
            if (!output.isEmpty() && !ItemStack.isSameItem(output, input)) {
                hasDifferentOutput = true;
                break;
            }
        }
        if (!hasDifferentOutput) {
            return false;
        }

        for (ItemStack stack : resolved.previewGrid()) {
            if (stack != null && !stack.isEmpty() && !ItemStack.isSameItem(stack, input)) {
                return true;
            }
        }

        return false;
    }

    private static ItemStack[] previewFromShapedDisplay(ShapedCraftingRecipeDisplay display, ContextMap context) {
        ItemStack[] grid = emptyGrid();
        int width = display.width();
        int height = display.height();
        List<SlotDisplay> ingredients = display.ingredients();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int patternIndex = x + y * width;
                int gridIndex = x + y * PREVIEW_WIDTH;
                if (patternIndex < ingredients.size() && gridIndex < grid.length) {
                    grid[gridIndex] = resolveDisplayStack(ingredients.get(patternIndex), context);
                }
            }
        }

        return grid;
    }

    private static ItemStack[] previewFromShapelessDisplay(ShapelessCraftingRecipeDisplay display, ContextMap context) {
        ItemStack[] grid = emptyGrid();
        int index = 0;

        for (SlotDisplay ingredient : display.ingredients()) {
            if (index >= grid.length) {
                break;
            }

            ItemStack stack = resolveDisplayStack(ingredient, context);
            if (!stack.isEmpty()) {
                grid[index++] = stack;
            }
        }

        return grid;
    }

    private static ItemStack[] previewFromPlacement(NormalCraftingRecipe recipe, ContextMap context) {
        ItemStack[] grid = emptyGrid();
        var placement = recipe.placementInfo();
        var slots = placement.slotsToIngredientIndex();
        var ingredients = placement.ingredients();

        for (int slot = 0; slot < slots.size() && slot < grid.length; slot++) {
            int ingredientIndex = slots.getInt(slot);
            if (ingredientIndex >= 0 && ingredientIndex < ingredients.size()) {
                grid[slot] = resolveIngredientStack(ingredients.get(ingredientIndex), context);
            }
        }

        return grid;
    }

    private static ItemStack resolveDisplayStack(SlotDisplay display, ContextMap context) {
        if (display == null || display == SlotDisplay.Empty.INSTANCE) {
            return ItemStack.EMPTY;
        }

        try {
            ItemStack stack = display.resolveForFirstStack(context);
            return stack == null ? ItemStack.EMPTY : stack;
        } catch (RuntimeException ignored) {
            return ItemStack.EMPTY;
        }
    }

    private static ItemStack resolveIngredientStack(
            net.minecraft.world.item.crafting.Ingredient ingredient, ContextMap context) {
        if (ingredient.isEmpty()) {
            return ItemStack.EMPTY;
        }

        try {
            ItemStack stack = ingredient.display().resolveForFirstStack(context);
            if (!stack.isEmpty()) {
                return stack;
            }
        } catch (RuntimeException ignored) {
            // Fall through to direct holder lookup
        }

        try {
            return ingredient.items()
                    .findFirst()
                    .map(ItemStack::new)
                    .orElse(ItemStack.EMPTY);
        } catch (RuntimeException ignored) {
            return ItemStack.EMPTY;
        }
    }

    private static List<ItemStack> collectOutputs(ItemStack[] previewGrid) {
        Map<Integer, ItemStack> merged = new LinkedHashMap<>();

        for (ItemStack stack : previewGrid) {
            if (stack == null || stack.isEmpty()) {
                continue;
            }

            int key = ItemStack.hashItemAndComponents(stack);
            merged.merge(key, stack.copy(), (left, right) -> {
                left.grow(right.getCount());
                return left;
            });
        }

        return new ArrayList<>(merged.values());
    }

    private static ItemStack[] emptyGrid() {
        ItemStack[] grid = new ItemStack[9];
        Arrays.fill(grid, ItemStack.EMPTY);
        return grid;
    }
}