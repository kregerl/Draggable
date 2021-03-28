package com.loucaskreger.draggableui.client.gui.widget;

import com.loucaskreger.draggableui.client.gui.GuiRenderer;
import com.loucaskreger.draggableui.init.WidgetRegistry;
import com.loucaskreger.draggableui.util.DefaultWidgetConstants;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraftforge.client.gui.ForgeIngameGui;

public class ExperienceWidget extends LinkedWidget {

	private static final Minecraft mc = Minecraft.getInstance();

	public ExperienceWidget() {
		super(0, 0, DefaultWidgetConstants.EXPERIENCE_WIDTH, DefaultWidgetConstants.EXPERIENCE_HEIGHT,
				WidgetRegistry.EXPERIENCE_LEVEL_WIDGET);
		this.defaultPosition = DefaultWidgetConstants.getExperiencePos();
	}

	@Override
	public void tick() {
		super.tick();
		if (this.linkedWidget.get() instanceof ExperienceLevelWidget) {
			this.linked = ((ExperienceLevelWidget) this.linkedWidget.get()).isLinked();
		}
	}

	@Override
	public void mouseDragged(int mouseX, int mouseY) {
		super.mouseDragged(mouseX, mouseY);
		if (this.linked) {
			this.linkedWidget.get().getBoundingBox().setPos(this.linkedWidget.get().getBoundingBox().getPos()
					.add((this.getBoundingBox().getWidth() - 12) / 2, -7));
		}

	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks, AbstractGui screen) {
		super.render(mouseX, mouseY, partialTicks, screen);
		if (this.isEnabled()) {
			if (this.parentScreen != null) {
				GuiRenderer.renderExpBar(this.getBoundingBox().getPos().x, this.getBoundingBox().getPos().y,
						this.parentScreen.width, this.parentScreen.height, screen);
			} else {
				GuiRenderer.renderExpBar(this.getBoundingBox().getPos().x, this.getBoundingBox().getPos().y,
						mc.getMainWindow().getScaledWidth(), mc.getMainWindow().getScaledHeight(), screen);
			}
		}
	}

	@Override
	public void mouseReleased() {
		super.mouseReleased();
	}

	@Override
	public void onClose() {
		super.onClose();
		ForgeIngameGui.renderExperiance = false;
	}

}
