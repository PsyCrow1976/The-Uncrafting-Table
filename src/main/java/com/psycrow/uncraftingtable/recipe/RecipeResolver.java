package com.psycrow.uncraftingtable.recipe;

import it.unimi.dsi.fastutil.ints.IntList;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.ShapedRecipe;

public final class RecipeResolver {
    private RecipeResolver() {}

    public static List<ResolvedRecipe> resolve(RecipeManager recipeManager, HolderLookup.Provider registries, ItemStack input) {
        if (input.isEmpty()) {
            return List.of();
        }

        List<ResolvedRecipe> results = new ArrayList<>();
        for (RecipeHolder<?> holder : recipeManager.getRecipes()) {
            if (!(holder.value() instanceof CraftingRecipe craftingRecipe)) {
                continue;
            }

            ItemStack result = craftingRecipe.assemble(CraftingInput.EMPTY);
            if (result.isEmpty() || !ItemStack.isSameItem(result, input)) {
                continue;
            }

            ItemStack[] previewGrid = toPreviewGrid(craftingRecipe);
            List<ItemStack> outputs = collectOutputs(craftingRecipe);
            if (outputs.isEmpty()) {
                continue;
            }

            @SuppressWarnings("unchecked")
            RecipeHolder<CraftingRecipe> craftingHolder = (RecipeHolder<CraftingRecipe>) holder;
            results.add(new ResolvedRecipe(craftingHolder, previewGrid, outputs));
        }

        return results;
    }

    private static ItemStack[] toPreviewGrid(CraftingRecipe recipe) {
        ItemStack[] grid = new ItemStack[9];
        PlacementInfo placement = recipe.placementInfo();

        if (recipe instanceof ShapedRecipe shaped) {
            IntList slots = placement.slotsToIngredientIndex();
            for (int slot = 0; slot < slots.size() && slot < grid.length; slot++) {
                int ingredientIndex = slots.getInt(slot);
                if (ingredientIndex >= 0) {
                    grid[slot] = representativeStack(placement.ingredients().get(ingredientIndex));
                }
            }
        } else {
            int index = 0;
            for (Ingredient ingredient : placement.ingredients()) {
                if (index >= grid.length) {
                    break;
                }
                grid[index++] = representativeStack(ingredient);
            }
        }

        return grid;
    }

    private static List<ItemStack> collectOutputs(CraftingRecipe recipe) {
        PlacementInfo placement = recipe.placementInfo();
        Map<Integer, ItemStack> merged = new LinkedHashMap<>();

        if (recipe instanceof ShapedRecipe) {
            IntList slots = placement.slotsToIngredientIndex();
            for (int slot = 0; slot < slots.size(); slot++) {
                int ingredientIndex = slots.getInt(slot);
                if (ingredientIndex < 0) {
                    continue;
                }

                ItemStack stack = representativeStack(placement.ingredients().get(ingredientIndex));
                if (stack.isEmpty()) {
                    continue;
                }

                int key = ItemStack.hashItemAndComponents(stack);
                merged.merge(key, stack.copy(), (left, right) -> {
                    left.grow(right.getCount());
                    return left;
                });
            }
        } else {
            for (Ingredient ingredient : placement.ingredients()) {
                ItemStack stack = representativeStack(ingredient);
                if (stack.isEmpty()) {
                    continue;
                }

                int key = ItemStack.hashItemAndComponents(stack);
                merged.merge(key, stack.copy(), (left, right) -> {
                    left.grow(right.getCount());
                    return left;
                });
            }
        }

        return new ArrayList<>(merged.values());
    }

    private static ItemStack representativeStack(Ingredient ingredient) {
        if (ingredient.isEmpty()) {
            return ItemStack.EMPTY;
        }

        return ingredient.items()
                .findFirst()
                .map(RecipeResolver::stackFromHolder)
                .orElse(ItemStack.EMPTY);
    }

    private static ItemStack stackFromHolder(Holder<Item> holder) {
        return new ItemStack(holder);
    }
}