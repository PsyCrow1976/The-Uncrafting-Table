package com.psycrow.uncraftingtable.menu;

import com.psycrow.uncraftingtable.blockentity.UncraftingTableBlockEntity;
import com.psycrow.uncraftingtable.registry.ModMenus;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

public class UncraftingTableMenu extends AbstractContainerMenu {
    public static final int INPUT_SLOT_INDEX = 0;
    public static final int PLAYER_INVENTORY_START = 1;

    private static final int INPUT_SLOT_X = 80;
    private static final int INPUT_SLOT_Y = 20;

    private final UncraftingTableBlockEntity blockEntity;
    private final ContainerLevelAccess access;

    public UncraftingTableMenu(int containerId, Inventory playerInventory, RegistryFriendlyByteBuf buffer) {
        this(containerId, playerInventory, getBlockEntity(playerInventory, buffer));
    }

    public UncraftingTableMenu(int containerId, Inventory playerInventory, UncraftingTableBlockEntity blockEntity) {
        super(ModMenus.UNCRAFTING_TABLE.get(), containerId);
        this.blockEntity = blockEntity;
        this.access = ContainerLevelAccess.create(blockEntity.getLevel(), blockEntity.getBlockPos());

        addSlot(new Slot(blockEntity, UncraftingTableBlockEntity.INPUT_SLOT, INPUT_SLOT_X, INPUT_SLOT_Y) {
            @Override
            public int getMaxStackSize() {
                return 1;
            }
        });

        addStandardInventorySlots(playerInventory, 8, 51);
    }

    private static UncraftingTableBlockEntity getBlockEntity(Inventory playerInventory, RegistryFriendlyByteBuf buffer) {
        BlockEntity blockEntity = playerInventory.player.level().getBlockEntity(buffer.readBlockPos());
        if (blockEntity instanceof UncraftingTableBlockEntity uncraftingTable) {
            return uncraftingTable;
        }
        throw new IllegalStateException("Uncrafting table block entity not found");
    }

    public UncraftingTableBlockEntity getBlockEntity() {
        return blockEntity;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int slotIndex) {
        ItemStack moved = ItemStack.EMPTY;
        Slot slot = slots.get(slotIndex);

        if (slot.hasItem()) {
            ItemStack stackInSlot = slot.getItem();
            moved = stackInSlot.copy();

            if (slotIndex == INPUT_SLOT_INDEX) {
                if (!moveItemStackTo(stackInSlot, PLAYER_INVENTORY_START, slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!moveItemStackTo(stackInSlot, INPUT_SLOT_INDEX, INPUT_SLOT_INDEX + 1, false)) {
                return ItemStack.EMPTY;
            }

            if (stackInSlot.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return moved;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(access, player, blockEntity.getBlockState().getBlock());
    }
}