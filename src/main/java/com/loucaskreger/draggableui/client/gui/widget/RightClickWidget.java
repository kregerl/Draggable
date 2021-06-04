package com.loucaskreger.draggableui.client.gui.widget;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import com.loucaskreger.draggableui.client.gui.screen.DraggableScreen;
import com.loucaskreger.draggableui.util.BoundingBox2D;
import com.loucaskreger.draggableui.util.Color4f;
import com.loucaskreger.draggableui.util.Util;
import com.loucaskreger.draggableui.util.Vec2i;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.widget.button.Button;

public class RightClickWidget extends StaticWidget {

	private static final int MAX_BUTTONS = 4;
	private static final int BUTTON_HEIGHT = 20;
	@Nullable
	private DraggableWidget selectedWidget;
	// Switch to immutable map <K = string, V = consumer>
	private final String[] buttonText = { "Remove", "Add", "Another", "Click Me" };
	private boolean mouseRightClicked;
	private List<Button> buttons;

	public RightClickWidget(BoundingBox2D boundingBox, DraggableScreen screen) {
		super(boundingBox, screen);
		this.mouseRightClicked = false;
		this.selectedWidget = null;
		this.buttons = new ArrayList<Button>(MAX_BUTTONS);
		for (int i = 0; i < MAX_BUTTONS; i++) {
			this.buttons.add(i, new Button(0, 0, 0, 0, "", button -> this.pressed(button)));
		}
	}

	public void mouseClicked(double mouseX, double mouseY, int mouseButton, DraggableScreen screen) {
		if (mouseButton == 1
				&& !(Util.isWithinBounds(new Vec2i(mouseX, mouseY), this.getBoundingBox()) && this.mouseRightClicked)) {
			this.selectedWidget = isOverWidget(mouseX, mouseY, screen);

			this.mouseRightClicked = true;

			this.getBoundingBox().setPos(new Vec2i(mouseX - 4, mouseY - this.getBoundingBox().getHeight() + 4));
			for (int i = 0; i < MAX_BUTTONS; i++) {
				Button button = this.buttons.get(i);
				button.x = this.getBoundingBox().getPos().x;
				button.y = this.getBoundingBox().getPos().y + (i * BUTTON_HEIGHT);
				button.setWidth(this.getBoundingBox().getWidth());
				button.setHeight(BUTTON_HEIGHT);
				button.setMessage(this.buttonText[i]);
				button.visible = true;
			}
			this.screen.addAllButtons(this.buttons);
		}
	}

	@Override
	public void mouseMoved(double mouseX, double mouseY) {
		if (!Util.isWithinBounds(new Vec2i(mouseX, mouseY), this.getBoundingBox()) && this.mouseRightClicked) {
			this.mouseRightClicked = false;
			this.screen.getButtons().removeAll(this.buttons);
		}
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks, AbstractGui screen) {
		if (this.mouseRightClicked) {
			this.getCanvas().render(this.getBoundingBox().getPos().x, this.getBoundingBox().getPos().y, screen);
			this.getBoundingBox().setVisible(true);
			this.getBoundingBox().setColor(new Color4f(1.0f, 0.0f, 1.0f, 1.0f));
			this.getBoundingBox().drawBoundingBoxOutline();
		}
	}

	// Change later, so each button has their own onPress
	public void pressed(Button button) {
		if (this.selectedWidget != null) {
			this.screen.widgets.remove(this.selectedWidget);
		}
		this.mouseRightClicked = false;
		this.buttons.forEach(but -> but.visible = false);
	}

	public boolean isMouseClicked() {
		return this.mouseRightClicked;
	}

	public List<Button> getButtons() {
		return this.buttons;
	}

}
