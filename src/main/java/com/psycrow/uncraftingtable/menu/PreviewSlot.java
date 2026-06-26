package com.psycrow.uncraftingtable.menu;

import com.psycrow.uncraftingtable.network.UncraftHandler;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.server.level.ServerPlayer;

public class PreviewSlot extends Slot {
    private final UncraftingTableMenu menu;

    public PreviewSlot(UncraftingTableMenu menu, Container container, int slot, int x, int y) {
        super(container, slot, x, y);
        this.menu = menu;
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return false;
    }

    @Override
    public boolean mayPickup(Player player) {
        return !getItem().isEmpty();
    }

    @Override
    public void onTake(Player player, ItemStack stack) {
        if (player instanceof ServerPlayer serverPlayer) {
            UncraftHandler.tryUncraft(serverPlayer, menu);
        }
    }
}