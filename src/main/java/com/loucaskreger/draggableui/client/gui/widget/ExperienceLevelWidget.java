package com.loucaskreger.draggableui.client.gui.widget;

import com.loucaskreger.draggableui.client.gui.GuiRenderer;
import com.loucaskreger.draggableui.util.DefaultWidgetConstants;

import net.minecraft.client.gui.AbstractGui;

public class ExperienceLevelWidget extends DraggableWidget {

	private boolean linked;

	public ExperienceLevelWidget() {
		super(0, 0, 12, 11);
		this.defaultPosition = DefaultWidgetConstants.getExperienceLevelPos();
		this.linked = true;
	}

	@Override
	public void tick() {
		super.tick();
		if (this.isSelected()) {
			this.setLinked(false);
		}
	}

	@Override
	// make this better so it renders based on the text being displayed.
	public void render(int mouseX, int mouseY, float partialTicks, AbstractGui screen) {
		super.render(mouseX, mouseY, partialTicks, screen);
		if (this.isEnabled()) {
			GuiRenderer.renderExpBarLevel(this.getBoundingBox().getPos().x, this.getBoundingBox().getPos().y + 8);
		}
	}

	public boolean isLinked() {
		return linked;
	}

	public void setLinked(boolean linked) {
		this.linked = linked;
	}
}
