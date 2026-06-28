package com.psycrow.uncraftingtable.blockentity;

import com.psycrow.uncraftingtable.menu.UncraftingTableMenu;
import com.psycrow.uncraftingtable.recipe.RecipeResolver;
import com.psycrow.uncraftingtable.recipe.ResolvedRecipe;
import com.psycrow.uncraftingtable.registry.ModBlockEntities;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.Nullable;

public class UncraftingTableBlockEntity extends BlockEntity implements MenuProvider, Container {
    public static final int INPUT_SLOT = 0;
    public static final int PREVIEW_START = 1;
    public static final int SLOT_COUNT = 10;

    private final NonNullList<ItemStack> items = NonNullList.withSize(SLOT_COUNT, ItemStack.EMPTY);
    private final List<ResolvedRecipe> resolvedRecipes = new ArrayList<>();
    private int selectedRecipeIndex = 0;

    private final ContainerData data = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> resolvedRecipes.size();
                case 1 -> selectedRecipeIndex;
                case 2 -> hasSelectedRecipe() ? 1 : 0;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            if (index == 1 && !resolvedRecipes.isEmpty()) {
                selectedRecipeIndex = Math.floorMod(value, resolvedRecipes.size());
                updatePreviewSlots();
            }
        }

        @Override
        public int getCount() {
            return 3;
        }
    };

    public UncraftingTableBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.UNCRAFTING_TABLE.get(), pos, state);
    }

    public ContainerData getContainerData() {
        return data;
    }

    public List<ResolvedRecipe> getResolvedRecipes() {
        return resolvedRecipes;
    }

    @Nullable
    public ResolvedRecipe getSelectedRecipe() {
        if (resolvedRecipes.isEmpty()) {
            return null;
        }
        return resolvedRecipes.get(Math.min(selectedRecipeIndex, resolvedRecipes.size() - 1));
    }

    public boolean hasSelectedRecipe() {
        return getSelectedRecipe() != null;
    }

    public void cycleRecipe() {
        if (resolvedRecipes.size() <= 1) {
            return;
        }
        selectedRecipeIndex = (selectedRecipeIndex + 1) % resolvedRecipes.size();
        updatePreviewSlots();
        setChanged();
        notifyOpenMenus();
    }

    public void refreshRecipes() {
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }

        resolvedRecipes.clear();
        selectedRecipeIndex = 0;

        ItemStack input = getItem(INPUT_SLOT);
        if (!input.isEmpty()) {
            try {
                resolvedRecipes.addAll(
                        RecipeResolver.resolve(serverLevel.recipeAccess(), serverLevel.registryAccess(), input));
            } catch (RuntimeException ignored) {
                // Leave preview empty if recipe lookup fails
            }
        }

        updatePreviewSlots();
        setChanged();
        notifyOpenMenus();
    }

    private void notifyOpenMenus() {
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }

        for (ServerPlayer player : serverLevel.getServer().getPlayerList().getPlayers()) {
            if (player.containerMenu instanceof UncraftingTableMenu menu && menu.getBlockEntity() == this) {
                menu.broadcastChanges();
            }
        }
    }

    private void updatePreviewSlots() {
        for (int slot = PREVIEW_START; slot < SLOT_COUNT; slot++) {
            items.set(slot, ItemStack.EMPTY);
        }

        ResolvedRecipe selected = getSelectedRecipe();
        if (selected == null) {
            return;
        }

        ItemStack[] previewGrid = selected.previewGrid();
        for (int index = 0; index < previewGrid.length; index++) {
            ItemStack stack = previewGrid[index];
            items.set(
                    PREVIEW_START + index,
                    stack == null || stack.isEmpty() ? ItemStack.EMPTY : stack.copy());
        }
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.uncraftingtable.uncrafting_table");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new UncraftingTableMenu(containerId, playerInventory, this);
    }

    @Override
    public int getContainerSize() {
        return SLOT_COUNT;
    }

    @Override
    public boolean isEmpty() {
        return getItem(INPUT_SLOT).isEmpty();
    }

    @Override
    public ItemStack getItem(int slot) {
        return items.get(slot);
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        if (slot != INPUT_SLOT) {
            return ItemStack.EMPTY;
        }
        ItemStack removed = ContainerHelper.removeItem(items, slot, amount);
        if (!removed.isEmpty()) {
            refreshRecipes();
        }
        return removed;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        if (slot != INPUT_SLOT) {
            return ItemStack.EMPTY;
        }
        ItemStack removed = ContainerHelper.takeItem(items, slot);
        if (!removed.isEmpty()) {
            refreshRecipes();
        }
        return removed;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        if (slot == INPUT_SLOT) {
            items.set(slot, stack.getCount() > 1 ? stack.copyWithCount(1) : stack);
            refreshRecipes();
            setChanged();
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return Container.stillValidBlockEntity(this, player);
    }

    @Override
    public void clearContent() {
        items.replaceAll(ignored -> ItemStack.EMPTY);
        resolvedRecipes.clear();
        selectedRecipeIndex = 0;
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        ContainerHelper.saveAllItems(output, NonNullList.of(ItemStack.EMPTY, getItem(INPUT_SLOT)));
        output.putInt("SelectedRecipe", selectedRecipeIndex);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        NonNullList<ItemStack> loaded = NonNullList.withSize(1, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(input, loaded);
        items.set(INPUT_SLOT, loaded.get(INPUT_SLOT));
        selectedRecipeIndex = input.getIntOr("SelectedRecipe", 0);
        refreshRecipes();
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveWithoutMetadata(registries);
    }
}