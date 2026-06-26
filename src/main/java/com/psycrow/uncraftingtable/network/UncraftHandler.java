package com.psycrow.uncraftingtable.network;

import com.psycrow.uncraftingtable.blockentity.UncraftingTableBlockEntity;
import com.psycrow.uncraftingtable.menu.UncraftingTableMenu;
import com.psycrow.uncraftingtable.recipe.ResolvedRecipe;
import java.util.List;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

public final class UncraftHandler {
    private UncraftHandler() {}

    public static void tryUncraft(ServerPlayer player, UncraftingTableMenu menu) {
        UncraftingTableBlockEntity blockEntity = menu.getBlockEntity();
        if (!menu.stillValid(player)) {
            return;
        }

        ResolvedRecipe selected = blockEntity.getSelectedRecipe();
        if (selected == null) {
            return;
        }

        ItemStack input = blockEntity.getItem(UncraftingTableBlockEntity.INPUT_SLOT);
        if (input.isEmpty()) {
            return;
        }

        List<ItemStack> outputs = selected.outputs().stream().map(ItemStack::copy).toList();
        if (!canFitAll(player.getInventory(), outputs)) {
            return;
        }

        input.shrink(1);
        if (input.isEmpty()) {
            blockEntity.setItem(UncraftingTableBlockEntity.INPUT_SLOT, ItemStack.EMPTY);
        } else {
            blockEntity.setItem(UncraftingTableBlockEntity.INPUT_SLOT, input);
        }

        for (ItemStack output : outputs) {
            player.getInventory().placeItemBackInInventory(output);
        }

        blockEntity.refreshRecipes();
        blockEntity.setChanged();
    }

    public static void cycleRecipe(ServerPlayer player, UncraftingTableMenu menu) {
        if (!menu.stillValid(player)) {
            return;
        }

        menu.getBlockEntity().cycleRecipe();
    }

    private static boolean canFitAll(Inventory inventory, List<ItemStack> outputs) {
        List<ItemStack> remaining = outputs.stream().map(ItemStack::copy).toList();

        for (ItemStack stack : remaining) {
            while (!stack.isEmpty()) {
                if (!addSimulated(inventory, stack)) {
                    return false;
                }
            }
        }

        return true;
    }

    private static boolean addSimulated(Inventory inventory, ItemStack stack) {
        for (int slot = 0; slot < inventory.getContainerSize(); slot++) {
            ItemStack existing = inventory.getItem(slot);
            if (!existing.isEmpty()
                    && ItemStack.isSameItemSameComponents(existing, stack)
                    && existing.getCount() < existing.getMaxStackSize()) {
                int transferable = Math.min(stack.getCount(), existing.getMaxStackSize() - existing.getCount());
                stack.shrink(transferable);
                return true;
            }
        }

        for (int slot = 0; slot < inventory.getContainerSize(); slot++) {
            if (inventory.getItem(slot).isEmpty()) {
                stack.shrink(Math.min(stack.getCount(), stack.getMaxStackSize()));
                return true;
            }
        }

        return stack.isEmpty();
    }
}