package com.loucaskreger.draggableui.client.gui.widget;

import java.util.function.Supplier;

import com.loucaskreger.draggableui.util.Vec2i;
import com.loucaskreger.draggableui.util.WidgetType;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

public class ContainerScreenWidget extends DraggableWidget {

	private Supplier<ContainerScreen<?>> screenSup;
	private ContainerScreen<?> screen;
	protected boolean canBeDragged;

	public ContainerScreenWidget(Supplier<ContainerScreen<?>> screen) {
		super(0, 0, 0, 0);
		this.screenSup = screen;
		this.canBeDragged = true;
		this.isSerilizable = false;
		this.type = WidgetType.CONTAINER;
	}

	@Override
	public void init() {
		super.init();
		if (this.screenSup.get() != null) {
			this.screen = this.screenSup.get();
			this.getBoundingBox().setPos(new Vec2i(this.screen.getGuiLeft(), this.screen.getGuiTop()));
			this.getBoundingBox().setWidth(this.screen.getXSize());
			this.getBoundingBox().setHeight(this.screen.getYSize());
		}
	}

	@Override
	public void mouseClicked(double mouseX, double mouseY, int scrollDelta) {
		super.mouseClicked(mouseX, mouseY, scrollDelta);
		this.screen.mouseClicked(mouseX, mouseY, scrollDelta);
		if (this.screen.getSlotUnderMouse() != null) {
			this.canBeDragged = false;
		} else if (!this.canBeDragged) {
			this.canBeDragged = true;
		}
	}

	@Override
	public void mouseReleased(double mouseX, double mouseY, int scrollDelta) {
		super.mouseReleased(mouseX, mouseY, scrollDelta);
		this.screen.mouseReleased(mouseX, mouseY, scrollDelta);
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks, AbstractGui screen) {
		super.render(mouseX, mouseY, partialTicks, screen);
		this.screen.render(mouseX, mouseY, partialTicks);
		this.getBoundingBox().drawBoundingBoxOutline();
		this.getBoundingBox().setVisible(true);
	}

	@Override
	public void mouseDragged(int mouseX, int mouseY) {
		if (this.isSelected() && this.canBeDragged) {
			this.updateCursorPositions(mouseX, mouseY);
			this.moveCursorBounds(this.mouseOffset);
			// Apply velocity based on mouse movement
			Vec2i velocity = this.getCursorVelocity();
			this.getBoundingBox().addVelocity(velocity);

			Vec2i finalPos = this.getBoundingBox().getPos().add(this.getBoundingBox().getVelocity());
			this.getBoundingBox().setPos(finalPos);
			this.getBoundingBox().setVelocity(Vec2i.ZERO);

			System.out.println(this.getBoundingBox().toString());
			ObfuscationReflectionHelper.setPrivateValue(ContainerScreen.class, this.screen,
					this.getBoundingBox().getPos().x, "field_147003_i");
			ObfuscationReflectionHelper.setPrivateValue(ContainerScreen.class, this.screen,
					this.getBoundingBox().getPos().y, "field_147009_r");
			System.out.println(this.screen.getGuiLeft());
		}

	}
}
