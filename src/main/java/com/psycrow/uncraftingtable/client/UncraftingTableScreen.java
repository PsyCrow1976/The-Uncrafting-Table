package com.psycrow.uncraftingtable.client;

import com.psycrow.uncraftingtable.UncraftingTableMod;
import com.psycrow.uncraftingtable.menu.UncraftingTableMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

public class UncraftingTableScreen extends AbstractContainerScreen<UncraftingTableMenu> {
    private static final Identifier TEXTURE =
            Identifier.fromNamespaceAndPath(UncraftingTableMod.MOD_ID, "textures/gui/uncrafting_table.png");
    public UncraftingTableScreen(UncraftingTableMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title, 176, 166);
        this.titleLabelX = 29;
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