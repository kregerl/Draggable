package com.loucaskreger.draggableui.client.gui.widget;

import com.loucaskreger.draggableui.util.DefaultWidgetConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.gui.ForgeIngameGui;

public class HotbarWidget extends DraggableWidget {

	private static final ResourceLocation WIDGETS_TEX_PATH = new ResourceLocation("textures/gui/widgets.png");
	private static final Minecraft mc = Minecraft.getInstance();

	public HotbarWidget() {
		super(0, 0, DefaultWidgetConstants.HOTBAR_WIDTH, DefaultWidgetConstants.HOTBAR_HEIGHT);
		this.defaultPosition = DefaultWidgetConstants.getHotbarPos();

	}

	// Rotate 90 degrees
	// RenderSystem.rotatef(90, 1, 1, 0);
	// Override render method to

	@Override
	public void render(int mouseX, int mouseY, float partialTicks, AbstractGui screen) {
		super.render(mouseX, mouseY, partialTicks, screen);
		if (this.isEnabled()) {
			// Use vanilla render for the hotbar since it is opaque.
			RenderSystem.pushMatrix();
			RenderSystem.enableBlend();

			mc.getTextureManager().bindTexture(WIDGETS_TEX_PATH);
			screen.blit(this.getBoundingBox().getPos().x, this.getBoundingBox().getPos().y, 0, 0,
					this.getBoundingBox().getWidth(), this.getBoundingBox().getHeight());

			RenderSystem.disableBlend();
			RenderSystem.popMatrix();
		}
	}

	@Override
	public void onClose() {
		super.onClose();
		ForgeIngameGui.renderHotbar = false;
	}
}
