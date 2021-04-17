package com.loucaskreger.draggableui.client.gui.widget;

import java.util.function.BiFunction;
import com.loucaskreger.draggableui.client.gui.GuiRenderer;
import com.loucaskreger.draggableui.util.DefaultWidgetConstants;
import com.loucaskreger.draggableui.util.Vec2i;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.ItemStack;

public class SelectedItemTextWidget extends LinkableWidget {

	private String name;
	protected BiFunction<Screen, Integer, Vec2i> defaultPosition;

	public SelectedItemTextWidget() {
		super(0, 0, 100, 12);
		this.name = "";
		this.defaultPosition = DefaultWidgetConstants.getSelectedTextPos();
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks, AbstractGui screen) {
		super.render(mouseX, mouseY, partialTicks, screen);
		ItemStack stack = mc.player.getHeldItemMainhand();
		name = stack.getDisplayName().getFormattedText();
		GuiRenderer.Hotbar.renderSelectedItem(mc.player.getHeldItemMainhand(), this.getBoundingBox().getPos().x,
				this.getBoundingBox().getPos().y + 2, mc.getMainWindow().getScaledWidth(),
				mc.getMainWindow().getScaledHeight());
	}

	@Override
	public void tick() {
		super.tick();
		int stringLength = mc.fontRenderer.getStringWidth(name);
		if (this.getBoundingBox().getWidth() != stringLength && stringLength != 0)
			this.getBoundingBox().setWidth(mc.fontRenderer.getStringWidth(name));

		// Store a center point of the hotbar widget and use that to set the position of
		// the bounding box when linked
		
		
//		if (this.isLinked() && this.parentScreen != null) {
//			this.getBoundingBox().setPos(this.getBoundingBox().getPos().add(getNameLength(), 0));
//		}
	}

	public int getNameLength() {
		return mc.fontRenderer.getStringWidth(name);
	}

	@Override
	protected void moveToDefaultPosition() {
		System.out.println("Here");
		if (this.defaultPosition != null && this.isEnabled() && this.parentScreen != null && !name.isBlank()) {
			this.getBoundingBox().setPos(this.defaultPosition.apply(this.parentScreen, getNameLength()));
		}
	}

}
