package com.psycrow.uncraftingtable.client;

import com.psycrow.uncraftingtable.menu.UncraftingTableMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

public class UncraftingTableScreen extends AbstractContainerScreen<UncraftingTableMenu> {
    private static final Identifier CRAFTING_TABLE_TEXTURE =
            Identifier.withDefaultNamespace("textures/gui/container/crafting_table.png");
    private static final Identifier CRAFTING_ARROW_SPRITE =
            Identifier.withDefaultNamespace("container/crafting_table/arrow");

    private static final int ARROW_X = 61;
    private static final int ARROW_Y = 18;

    public UncraftingTableScreen(UncraftingTableMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title, 176, 166);
        this.titleLabelX = 29;
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
        graphics.blitSprite(
                RenderPipelines.GUI_TEXTURED,
                CRAFTING_ARROW_SPRITE,
                this.leftPos + ARROW_X,
                this.topPos + ARROW_Y,
                24,
                17);

        // Cycle button disabled until recipe cycling is re-enabled
    }
}