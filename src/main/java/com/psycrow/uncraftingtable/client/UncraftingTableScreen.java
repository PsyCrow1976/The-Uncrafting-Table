package com.psycrow.uncraftingtable.client;

import com.psycrow.uncraftingtable.menu.UncraftingTableMenu;
import com.psycrow.uncraftingtable.network.CycleRecipePayload;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

public class UncraftingTableScreen extends AbstractContainerScreen<UncraftingTableMenu> {
    private static final Identifier CRAFTING_TABLE_TEXTURE =
            Identifier.withDefaultNamespace("textures/gui/container/crafting_table.png");
    private static final Identifier CRAFTING_ARROW_SPRITE =
            Identifier.withDefaultNamespace("container/crafting_table/arrow");

    private Button cycleButton;

    public UncraftingTableScreen(UncraftingTableMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title, 176, 166);
        this.titleLabelX = 29;
    }

    @Override
    protected void init() {
        super.init();
        cycleButton = Button.builder(Component.literal(">"), button -> ClientPacketDistributor.sendToServer(new CycleRecipePayload()))
                .bounds(this.leftPos + 61, this.topPos + 53, 20, 20)
                .build();
        addRenderableWidget(cycleButton);
        updateCycleButton();
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        updateCycleButton();
    }

    private void updateCycleButton() {
        if (cycleButton != null) {
            cycleButton.visible = menu.getRecipeCount() > 1;
        }
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        graphics.blit(
                RenderPipelines.GUI_TEXTURED,
                CRAFTING_TABLE_TEXTURE,
                this.leftPos,
                this.topPos,
                0,
                0,
                this.imageWidth,
                this.imageHeight,
                256,
                256);
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, CRAFTING_ARROW_SPRITE, this.leftPos + 53, this.topPos + 18, 24, 17);
    }
}