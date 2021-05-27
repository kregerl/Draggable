package com.loucaskreger.draggableui.util;

import com.loucaskreger.draggableui.DraggableUI;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.util.ResourceLocation;

public class Canvas {

	private static final Minecraft mc = Minecraft.getInstance();
	private final ResourceLocation texture;
	private BoundingBox2D boundingBox;
	private Color4f color;

	public Canvas(BoundingBox2D boundingBox, Color4f color, ResourceLocation texture) {
		this.boundingBox = boundingBox;
		this.color = color;
		this.texture = texture;
	}

	public Canvas(BoundingBox2D boundingBox, Color4f color) {
		this(boundingBox, color, new ResourceLocation(DraggableUI.MOD_ID, "textures/gui/background.png"));
	}

	public Canvas(BoundingBox2D boundingBox) {
		this(boundingBox, Color4f.BLANK);
	}

	public void render(AbstractGui screen) {
		RenderSystem.pushMatrix();
		RenderSystem.enableBlend();
		mc.getTextureManager().bindTexture(this.texture);
		RenderSystem.color4f(this.color.red, this.color.green, this.color.blue, this.color.alpha);
		screen.blit(this.boundingBox.getPos().x, this.boundingBox.getPos().y, 0, 0, this.boundingBox.getWidth(),
				this.boundingBox.getHeight());
		RenderSystem.popMatrix();
		RenderSystem.disableBlend();
	}

	public void render(int xPos, int yPos, AbstractGui screen) {
		RenderSystem.pushMatrix();
		RenderSystem.enableBlend();
		mc.getTextureManager().bindTexture(this.texture);
		RenderSystem.color4f(this.color.red, this.color.green, this.color.blue, this.color.alpha);
		screen.blit(xPos, yPos, 0, 0, this.boundingBox.getWidth(), this.boundingBox.getHeight());
		RenderSystem.popMatrix();
		RenderSystem.disableBlend();
	}
}
