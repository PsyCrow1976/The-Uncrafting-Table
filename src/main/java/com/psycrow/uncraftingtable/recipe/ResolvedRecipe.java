package com.psycrow.uncraftingtable.recipe;

import java.util.List;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;

public record ResolvedRecipe(
        RecipeHolder<CraftingRecipe> holder,
        ItemStack[] previewGrid,
        List<ItemStack> outputs) {}