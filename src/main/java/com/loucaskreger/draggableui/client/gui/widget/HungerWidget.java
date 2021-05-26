package com.loucaskreger.draggableui.client.gui.widget;

import com.loucaskreger.draggableui.util.DefaultWidgetConstants;
import com.loucaskreger.draggableui.util.WidgetType;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.IngameGui;
import net.minecraftforge.client.gui.ForgeIngameGui;

public class HungerWidget extends DraggableWidget {

	private static final Minecraft mc = Minecraft.getInstance();

	public HungerWidget() {
		super(0, 0, DefaultWidgetConstants.HUNGER_WIDTH, DefaultWidgetConstants.HUNGER_HEIGHT);
		this.defaultPosition = DefaultWidgetConstants.getHungerPos();
		this.type = WidgetType.SURVIVAL_AND_ADVENTURE;
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks, AbstractGui screen) {
		super.render(mouseX, mouseY, partialTicks, screen);
		if (this.isEnabled()) {
			mc.getTextureManager().bindTexture(AbstractGui.GUI_ICONS_LOCATION);
			IngameGui ingameGui = mc.ingameGUI;
			if (ingameGui instanceof ForgeIngameGui) {
				ForgeIngameGui gui = (ForgeIngameGui) ingameGui;
				gui.renderFood(2 * (this.getBoundingBox().getPos().x - 91 + DefaultWidgetConstants.HUNGER_WIDTH),
						this.getBoundingBox().getPos().y + ForgeIngameGui.right_height);
			}
		}
	}

	@Override
	public void onClose() {
		super.onClose();
	}
}
