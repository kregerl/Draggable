package com.loucaskreger.draggableui.client.gui.widget;

import com.loucaskreger.draggableui.client.gui.GuiRenderer;
import com.loucaskreger.draggableui.util.DefaultWidgetConstants;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraftforge.client.gui.ForgeIngameGui;

public class HotbarWidget extends DraggableWidget {

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
			mc.gameSettings.heldItemTooltips = false;
			RenderSystem.pushMatrix();
			RenderSystem.enableBlend();
			GuiRenderer.renderHotbar(this.getBoundingBox().getPos().x, this.getBoundingBox().getPos().y, partialTicks,
					mc.getMainWindow().getScaledWidth(), mc.getMainWindow().getScaledHeight(), screen,
					this.parentScreen == null);

			GuiRenderer.renderSelectedItem(mc.player.getHeldItemMainhand(), this.getBoundingBox().getPos().x,
					this.getBoundingBox().getPos().y, mc.getMainWindow().getScaledWidth(),
					mc.getMainWindow().getScaledHeight());
			RenderSystem.popMatrix();
			RenderSystem.disableBlend();

		}
	}

	@Override
	public void onClose() {
		super.onClose();
		ForgeIngameGui.renderHotbar = false;
		mc.gameSettings.heldItemTooltips = true;
	}

	@Override
	public void tick() {
		super.tick();
		// Check offhand every tick, adjust boundingbox size according to which side the
		// offhand is on.
	}
}
