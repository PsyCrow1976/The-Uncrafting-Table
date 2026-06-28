package com.psycrow.uncraftingtable.client;

import com.psycrow.uncraftingtable.UncraftingTableMod;
import com.psycrow.uncraftingtable.menu.UncraftingTableMenu;
import com.psycrow.uncraftingtable.network.CycleRecipePayload;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

public class UncraftingTableScreen extends AbstractContainerScreen<UncraftingTableMenu> {
    private static final Identifier TEXTURE =
            Identifier.fromNamespaceAndPath(UncraftingTableMod.MOD_ID, "textures/gui/uncrafting_table.png");

    private static final int PREVIEW_GRID_X = 88;
    private static final int PREVIEW_GRID_Y = 17;
    private static final int PREVIEW_GRID_SIZE = 54;
    private static final int CYCLE_BUTTON_SIZE = 12;

    private static final WidgetSprites CYCLE_SPRITES = new WidgetSprites(
            Identifier.withDefaultNamespace("widget/page_forward"),
            Identifier.withDefaultNamespace("widget/page_forward_highlighted"));

    private ImageButton cycleButton;

    public UncraftingTableScreen(UncraftingTableMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title, 176, 166);
        this.titleLabelX = 29;
    }

    @Override
    protected void init() {
        super.init();
        int x = this.leftPos + PREVIEW_GRID_X + PREVIEW_GRID_SIZE - CYCLE_BUTTON_SIZE;
        int y = this.topPos + PREVIEW_GRID_Y;
        this.cycleButton = new ImageButton(
                x,
                y,
                CYCLE_BUTTON_SIZE,
                CYCLE_BUTTON_SIZE,
                CYCLE_SPRITES,
                button -> onCyclePressed(),
                Component.translatable("gui.uncraftingtable.cycle_recipe"));
        updateCycleButtonState();
        this.addRenderableWidget(this.cycleButton);
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        updateCycleButtonState();
    }

    private void onCyclePressed() {
        if (this.menu.getRecipeCount() <= 1) {
            return;
        }
        ClientPacketDistributor.sendToServer(new CycleRecipePayload());
    }

    private void updateCycleButtonState() {
        if (this.cycleButton == null) {
            return;
        }

        boolean canCycle = this.menu.getRecipeCount() > 1;
        this.cycleButton.visible = canCycle;
        this.cycleButton.active = canCycle;
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(graphics, mouseX, mouseY, partialTick);
        graphics.blit(
                RenderPipelines.GUI_TEXTURED,
                TEXTURE,
                this.leftPos,
                this.topPos,
                0,
                0,
                this.imageWidth,
                this.imageHeight,
                256,
                256);
    }
}