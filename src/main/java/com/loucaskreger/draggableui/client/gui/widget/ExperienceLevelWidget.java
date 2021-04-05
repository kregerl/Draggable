package com.loucaskreger.draggableui.client.gui.widget;

import com.loucaskreger.draggableui.client.gui.GuiRenderer;
import com.loucaskreger.draggableui.util.DefaultWidgetConstants;

import net.minecraft.client.gui.AbstractGui;

public class ExperienceLevelWidget extends LinkableWidget {

	public ExperienceLevelWidget() {
		super(0, 0, 12, 11);
		this.defaultPosition = DefaultWidgetConstants.getExperienceLevelPos();
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks, AbstractGui screen) {
		super.render(mouseX, mouseY, partialTicks, screen);
		GuiRenderer.Expbar.renderExpBarLevel(this.getBoundingBox().getPos().x, this.getBoundingBox().getPos().y + 8);
	}

}
