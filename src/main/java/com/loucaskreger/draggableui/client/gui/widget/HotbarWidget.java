package com.loucaskreger.draggableui.client.gui.widget;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.ResourceLocation;

public class HotbarWidget extends DraggableWidget {

	private static final ResourceLocation WIDGETS_TEX_PATH = new ResourceLocation("textures/gui/widgets.png");
	private static final int WIDTH = 182;
	private static final int HEIGHT = 22;

	public HotbarWidget(int screenWidth, int screenHeight) {
		super((screenWidth / 2) - 91, screenHeight - 22, WIDTH, HEIGHT, true, "Hotbar");
	}
	// Rotate 90 degrees
	// RenderSystem.rotatef(90, 1, 1, 0);
	// Override render method to

	@Override
	public void render(int mouseX, int mouseY, Screen screen) {
		super.render(mouseX, mouseY, screen);
		RenderSystem.pushMatrix();
		RenderSystem.enableBlend();
		screen.getMinecraft().getTextureManager().bindTexture(WIDGETS_TEX_PATH);
		screen.blit(this.getPos().x, this.getPos().y, 0, 0, this.width, this.height);
		RenderSystem.disableBlend();
		RenderSystem.popMatrix();
	}
}
