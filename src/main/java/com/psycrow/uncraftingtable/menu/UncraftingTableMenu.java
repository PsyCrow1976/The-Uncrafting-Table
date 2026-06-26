package com.psycrow.uncraftingtable.menu;

import com.psycrow.uncraftingtable.blockentity.UncraftingTableBlockEntity;
import com.psycrow.uncraftingtable.registry.ModMenus;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

public class UncraftingTableMenu extends AbstractContainerMenu {
    public static final int INPUT_SLOT_INDEX = 0;
    public static final int PREVIEW_SLOT_START = 1;
    public static final int PLAYER_INVENTORY_START = 10;

    // Mirrored crafting table layout: input left, 3x3 preview right
    private static final int INPUT_SLOT_X = 30;
    private static final int INPUT_SLOT_Y = 35;
    private static final int PREVIEW_GRID_X = 88;
    private static final int PREVIEW_GRID_Y = 17;

    private final UncraftingTableBlockEntity blockEntity;
    private final ContainerLevelAccess access;
    private final ContainerData data;

    public UncraftingTableMenu(int containerId, Inventory playerInventory, RegistryFriendlyByteBuf buffer) {
        this(containerId, playerInventory, getBlockEntity(playerInventory, buffer), new SimpleContainerData(3));
    }

    public UncraftingTableMenu(int containerId, Inventory playerInventory, UncraftingTableBlockEntity blockEntity, ContainerData data) {
        super(ModMenus.UNCRAFTING_TABLE.get(), containerId);
        this.blockEntity = blockEntity;
        this.access = ContainerLevelAccess.create(blockEntity.getLevel(), blockEntity.getBlockPos());
        this.data = data;

        addSlot(new Slot(blockEntity, UncraftingTableBlockEntity.INPUT_SLOT, INPUT_SLOT_X, INPUT_SLOT_Y) {
            @Override
            public int getMaxStackSize() {
                return 1;
            }
        });

        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 3; column++) {
                int slot = UncraftingTableBlockEntity.PREVIEW_START + row * 3 + column;
                addSlot(new PreviewSlot(
                        blockEntity,
                        slot,
                        PREVIEW_GRID_X + column * 18,
                        PREVIEW_GRID_Y + row * 18));
            }
        }

        addPlayerInventory(playerInventory);
        addDataSlots(data);

        // blockEntity.refreshRecipes(); // disabled — recipe preview not active yet
    }

    private static UncraftingTableBlockEntity getBlockEntity(Inventory playerInventory, RegistryFriendlyByteBuf buffer) {
        BlockEntity blockEntity = playerInventory.player.level().getBlockEntity(buffer.readBlockPos());
        if (blockEntity instanceof UncraftingTableBlockEntity uncraftingTable) {
            return uncraftingTable;
        }
        throw new IllegalStateException("Uncrafting table block entity not found");
    }

    private void addPlayerInventory(Inventory playerInventory) {
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                addSlot(new Slot(playerInventory, column + row * 9 + 9, 8 + column * 18, 84 + row * 18));
            }
        }

        for (int column = 0; column < 9; column++) {
            addSlot(new Slot(playerInventory, column, 8 + column * 18, 142));
        }
    }

    public UncraftingTableBlockEntity getBlockEntity() {
        return blockEntity;
    }

    public int getRecipeCount() {
        return data.get(0);
    }

    public int getSelectedRecipeIndex() {
        return data.get(1);
    }

    public boolean hasValidRecipe() {
        return data.get(2) == 1;
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
            } else if (slotIndex >= PLAYER_INVENTORY_START) {
                if (!moveItemStackTo(stackInSlot, INPUT_SLOT_INDEX, INPUT_SLOT_INDEX + 1, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (slotIndex >= PREVIEW_SLOT_START) {
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