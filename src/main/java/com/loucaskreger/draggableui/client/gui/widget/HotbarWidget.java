package com.loucaskreger.draggableui.client.gui.widget;

import com.loucaskreger.draggableui.client.gui.GuiRenderer;
import com.loucaskreger.draggableui.init.WidgetRegistry;
import com.loucaskreger.draggableui.util.DefaultWidgetConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.gui.ForgeIngameGui;

public class HotbarWidget extends LinkingWidget {

	private static final Minecraft mc = Minecraft.getInstance();

	public HotbarWidget() {
		super(0, 0, DefaultWidgetConstants.HOTBAR_WIDTH, DefaultWidgetConstants.HOTBAR_HEIGHT,
				WidgetRegistry.OFFHAND_WIDGET/* Offhand Widgets and selected item text here. */);
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
			GuiRenderer.Hotbar.renderHotbar(this.getBoundingBox().getPos().x, this.getBoundingBox().getPos().y,
					partialTicks, mc.getMainWindow().getScaledWidth(), mc.getMainWindow().getScaledHeight(), screen,
					screen == mc.ingameGUI);
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
		if (!(mc.player.getHeldItemOffhand() == ItemStack.EMPTY)) {
			this.linkedWidget.get().setEnabled(true);
		} else {
			this.linkedWidget.get().setEnabled(false);
		}
	}
}
