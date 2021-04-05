package com.loucaskreger.draggableui.client.gui.widget;

import com.loucaskreger.draggableui.client.gui.GuiRenderer;
import com.loucaskreger.draggableui.util.DefaultWidgetConstants;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.gui.AbstractGui;
import net.minecraft.item.ItemStack;
import net.minecraft.util.HandSide;

public class OffhandWidget extends LinkableWidget {

	public OffhandWidget() {
		super(0, 0, 22, 22);
		this.defaultPosition = DefaultWidgetConstants.getOffhandPos();
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks, AbstractGui screen) {
		super.render(mouseX, mouseY, partialTicks, screen);
		ItemStack offhandItemStack = mc.player.getHeldItemOffhand();
		int x = this.getBoundingBox().getPos().x;
		int y = this.getBoundingBox().getPos().y;
		HandSide handside = mc.player.getPrimaryHand().opposite();
		RenderSystem.pushMatrix();
		RenderSystem.enableBlend();
		GuiRenderer.Hotbar.renderOffhandSlot(offhandItemStack, x, y, handside, screen);

		GuiRenderer.Hotbar.renderOffhandItem(offhandItemStack, mc.getMainWindow().getScaledWidth(),
				mc.getMainWindow().getScaledHeight(), partialTicks, mc.player, handside, x + 3, y);
		RenderSystem.popMatrix();
		RenderSystem.disableBlend();
	}

}
