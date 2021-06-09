package com.loucaskreger.draggableui.client.gui.widget;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableMap;
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
	private ImmutableMap<String, Consumer<Button>> buttonInfo;
	// Map of consumers for each button to be called on button click.
	{
		Map<String, Consumer<Button>> map = new HashMap<String, Consumer<Button>>();
		map.put("Remove", (button) -> {
			if (this.selectedWidget != null) {
				this.screen.widgets.remove(this.selectedWidget);
			}
			this.genericPress();
		});
		map.put("Add", (button) -> {
			if (this.selectedWidget != null) {
				this.screen.widgets.remove(this.selectedWidget);
			}
			this.genericPress();
		});
		map.put("Another", (button) -> {
			if (this.selectedWidget != null) {
				this.screen.widgets.remove(this.selectedWidget);
			}
			this.genericPress();
		});
		map.put("Click Me", (button) -> {
			if (this.selectedWidget != null) {
				this.screen.widgets.remove(this.selectedWidget);
			}
			this.genericPress();
		});
		this.buttonInfo = ImmutableMap.copyOf(map);
	}
	private boolean mouseRightClicked;
	private List<Button> buttons;

	public RightClickWidget(BoundingBox2D boundingBox, DraggableScreen screen) {
		super(boundingBox, screen);
		this.mouseRightClicked = false;
		this.selectedWidget = null;
		this.buttons = new ArrayList<Button>(MAX_BUTTONS);
		for (int i = 0; i < MAX_BUTTONS; i++) {
			this.buttons.add(i,
					new Button(0, 0, 0, 0, "", button -> this.buttonInfo.get(button.getMessage()).accept(button)));
		}
	}

	public void mouseClicked(double mouseX, double mouseY, int mouseButton, DraggableScreen screen) {
		if (mouseButton == 1
				&& !(Util.isWithinBounds(new Vec2i(mouseX, mouseY), this.getBoundingBox()) && this.mouseRightClicked)) {
			this.selectedWidget = isOverWidget(mouseX, mouseY, screen);

			this.mouseRightClicked = true;
			Vec2i pos = new Vec2i(mouseX - 4, mouseY - this.getBoundingBox().getHeight() + 4);
			// If pos will be outside of the screen, don't be.
			if (!Util.isWithinBoundsY(pos.y, new BoundingBox2D(0, 0, screen.width, screen.height))) {
				pos = new Vec2i(pos.x, mouseY - 4);
			}
			if (!Util.isWithinBoundsX(pos.x + this.getBoundingBox().getWidth(),
					new BoundingBox2D(0, 0, screen.width, screen.height))) {
				pos = new Vec2i(mouseX - this.getBoundingBox().getWidth() + 4, pos.y);
			}
			this.getBoundingBox().setPos(pos);
			Iterator<String> it = this.buttonInfo.keySet().iterator();
			int index = 0;
			while (it.hasNext()) {
				Button button = this.buttons.get(index);
				button.x = this.getBoundingBox().getPos().x;
				button.y = this.getBoundingBox().getPos().y + (index * BUTTON_HEIGHT);
				button.setWidth(this.getBoundingBox().getWidth());
				button.setHeight(BUTTON_HEIGHT);
				button.setMessage(it.next());
				button.visible = true;
				index++;
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
			super.render(mouseX, mouseY, partialTicks, screen);
			this.getBoundingBox().setVisible(true);
			this.getBoundingBox().setColor(new Color4f(1.0f, 0.0f, 1.0f, 1.0f));
			this.getBoundingBox().drawBoundingBoxOutline();
		}
	}

	public void genericPress() {
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
