package com.loucaskreger.draggableui.client.gui.widget;

import com.loucaskreger.draggableui.client.gui.GuiRenderer;
import com.loucaskreger.draggableui.init.WidgetRegistry;
import com.loucaskreger.draggableui.util.DefaultWidgetConstants;
import com.loucaskreger.draggableui.util.WidgetType;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;

public class ExperienceWidget extends LinkingWidget {

	private static final Minecraft mc = Minecraft.getInstance();

	public ExperienceWidget() {
		super(0, 0, DefaultWidgetConstants.EXPERIENCE_WIDTH, DefaultWidgetConstants.EXPERIENCE_HEIGHT);
		this.defaultPosition = DefaultWidgetConstants.getExperiencePos();
		this.hasDefaultLinks = true;
		this.defaultLinks.add(WidgetRegistry.EXPERIENCE_LEVEL_WIDGET);
		this.type = WidgetType.SURVIVAL_AND_ADVENTURE;

	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks, AbstractGui screen) {
		super.render(mouseX, mouseY, partialTicks, screen);
		if (this.isEnabled()) {
			if (this.parentScreen != null) {
				GuiRenderer.Expbar.renderExpBar(this.getBoundingBox().getPos().x, this.getBoundingBox().getPos().y,
						this.parentScreen.width, this.parentScreen.height, screen);
			} else {
				GuiRenderer.Expbar.renderExpBar(this.getBoundingBox().getPos().x, this.getBoundingBox().getPos().y,
						mc.getMainWindow().getScaledWidth(), mc.getMainWindow().getScaledHeight(), screen);
			}
		}

	}

	@Override
	public void mouseReleased(double mouseX, double mouseY, int scrollDelta) {
		super.mouseReleased(mouseX, mouseY, scrollDelta);
	}

	@Override
	public void onClose() {
		super.onClose();
	}

}
