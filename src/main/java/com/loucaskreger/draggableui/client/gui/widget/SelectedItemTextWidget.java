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
	private int hotbarCenterX;
	private ItemStack prevStack;
	protected BiFunction<Screen, Integer, Vec2i> defaultPosition;

	public SelectedItemTextWidget() {
		super(0, 0, 10, 12);
		this.name = "";
		this.hotbarCenterX = 0;
		this.prevStack = null;
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

		if (this.prevStack == null) {
			this.prevStack = mc.player.getHeldItemMainhand();
		} else {
			if (this.prevStack != mc.player.getHeldItemMainhand() && this.hotbarCenterX != 0) {
				this.getBoundingBox()
						.setPos(new Vec2i(this.hotbarCenterX - (this.getNameLength() / 2), this.getBoundingBox().getPos().y));
			}
		}
	}

	public int getNameLength() {
		return mc.fontRenderer.getStringWidth(name);
	}

	@Override
	protected void moveToDefaultPosition() {
		if (this.defaultPosition != null && this.isEnabled() && this.parentScreen != null && !name.isBlank()) {
			this.getBoundingBox().setPos(this.defaultPosition.apply(this.parentScreen, getNameLength()));
		}
	}

	public void setHotbarCenterX(int hotbarCenterX) {
		this.hotbarCenterX = hotbarCenterX;
	}

}
