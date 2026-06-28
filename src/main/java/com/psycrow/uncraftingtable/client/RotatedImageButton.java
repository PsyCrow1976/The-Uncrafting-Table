package com.psycrow.uncraftingtable.client;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

final class RotatedImageButton extends ImageButton {
    private final WidgetSprites sprites;
    private final float rotationRadians;

    RotatedImageButton(
            int x,
            int y,
            int width,
            int height,
            WidgetSprites sprites,
            Button.OnPress onPress,
            Component message,
            float rotationRadians) {
        super(x, y, width, height, sprites, onPress, message);
        this.sprites = sprites;
        this.rotationRadians = rotationRadians;
    }

    @Override
    public void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        Identifier sprite = this.sprites.get(this.active, this.isHoveredOrFocused());
        float centerX = this.getX() + this.width * 0.5f;
        float centerY = this.getY() + this.height * 0.5f;
        graphics.pose().pushMatrix();
        graphics.pose().translate(centerX, centerY);
        graphics.pose().rotate(this.rotationRadians);
        graphics.pose().translate(-this.width * 0.5f, -this.height * 0.5f);
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, sprite, 0, 0, this.width, this.height);
        graphics.pose().popMatrix();
    }
}