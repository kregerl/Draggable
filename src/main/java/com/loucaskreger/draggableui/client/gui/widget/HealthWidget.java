package com.loucaskreger.draggableui.client.gui.widget;

import com.loucaskreger.draggableui.util.DefaultWidgetConstants;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.IngameGui;
import net.minecraftforge.client.gui.ForgeIngameGui;

public class HealthWidget extends DraggableWidget {

	private static final Minecraft mc = Minecraft.getInstance();

	public HealthWidget() {
		super(0, 0, DefaultWidgetConstants.HEALTH_WIDTH, DefaultWidgetConstants.HEALTH_HEIGHT);
		this.defaultPosition = DefaultWidgetConstants.getHealthPos();
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks, AbstractGui screen) {
		super.render(mouseX, mouseY, partialTicks, screen);
		if (this.isEnabled()) {
			IngameGui ingameGui = mc.ingameGUI;
			if (ingameGui instanceof ForgeIngameGui) {
				ForgeIngameGui gui = (ForgeIngameGui) ingameGui;
				gui.renderHealth(2 * (this.getBoundingBox().getPos().x + 91),
						this.getBoundingBox().getPos().y + ForgeIngameGui.left_height);
			}

		}
	}

	@Override
	public void onClose() {
		super.onClose();
		ForgeIngameGui.renderHealth = false;
	}
}
