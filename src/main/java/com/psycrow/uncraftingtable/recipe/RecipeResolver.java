package com.psycrow.uncraftingtable.recipe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.core.HolderLookup;
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
            return List.of();
        }

        ContextMap context = new ContextMap.Builder()
                .withParameter(SlotDisplayContext.REGISTRIES, registries)
                .create(SlotDisplayContext.CONTEXT);

        List<ResolvedRecipe> results = new ArrayList<>();
        for (RecipeHolder<CraftingRecipe> holder : recipeManager.recipeMap().byType(RecipeType.CRAFTING)) {
            try {
                CraftingRecipe craftingRecipe = holder.value();
                if (!(craftingRecipe instanceof NormalCraftingRecipe normalRecipe)) {
                    continue;
                }

                if (normalRecipe.placementInfo().isImpossibleToPlace()) {
                    continue;
                }

                ResolvedRecipe resolved = resolveFromDisplay(holder, normalRecipe, input, context);
                if (resolved == null) {
                    resolved = resolveFromPlacement(holder, normalRecipe, input, context);
                }

                if (resolved != null) {
                    results.add(resolved);
                }
            } catch (RuntimeException ignored) {
                // Skip malformed or mod recipes that cannot be reversed safely
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
                    ItemStack[] previewGrid = previewFromShapedDisplay(shaped, context);
                    List<ItemStack> outputs = collectOutputs(previewGrid);
                    if (!outputs.isEmpty()) {
                        return new ResolvedRecipe(holder, previewGrid, outputs);
                    }
                }
            } else if (display instanceof ShapelessCraftingRecipeDisplay shapeless) {
                ItemStack result = shapeless.result().resolveForFirstStack(context);
                if (matchesInput(result, input)) {
                    ItemStack[] previewGrid = previewFromShapelessDisplay(shapeless, context);
                    List<ItemStack> outputs = collectOutputs(previewGrid);
                    if (!outputs.isEmpty()) {
                        return new ResolvedRecipe(holder, previewGrid, outputs);
                    }
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
        } catch (RuntimeException ignored) {
            return null;
        }

        if (!matchesInput(result, input)) {
            return null;
        }

        ItemStack[] previewGrid = previewFromPlacement(recipe, context);
        List<ItemStack> outputs = collectOutputs(previewGrid);
        if (outputs.isEmpty()) {
            return null;
        }

        return new ResolvedRecipe(holder, previewGrid, outputs);
    }

    private static boolean matchesInput(ItemStack result, ItemStack input) {
        return !result.isEmpty() && ItemStack.isSameItem(result, input);
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