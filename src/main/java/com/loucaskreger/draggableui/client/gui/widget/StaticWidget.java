package com.loucaskreger.draggableui.client.gui.widget;

import com.loucaskreger.draggableui.util.BoundingBox2D;
import com.loucaskreger.draggableui.util.Canvas;
import com.loucaskreger.draggableui.util.Util;
import com.loucaskreger.draggableui.util.Vec2i;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;

public class StaticWidget {

	private boolean mouseRightClicked;
	private BoundingBox2D boundingBox;
	private Canvas canvas;

	public StaticWidget(BoundingBox2D boundingBox) {
		this.boundingBox = boundingBox;
		this.mouseRightClicked = false;
		this.canvas = new Canvas(this.boundingBox);
	}

	public StaticWidget(Vec2i pos, int width, int height) {
		this(new BoundingBox2D(pos, width, height));
	}

	public StaticWidget(int x, int y, int width, int height) {
		this(new BoundingBox2D(x, y, width, height));
	}

	public void mouseClicked(double mouseX, double mouseY, int mouseButton) {
		if (mouseButton == 1) {
			this.mouseRightClicked = true;
			this.boundingBox.setPos(new Vec2i(mouseX - 5, mouseY - this.boundingBox.getHeight() + 5));
		}
	}

	public void mouseMoved(double mouseX, double mouseY) {
		if (!Util.isWithinBounds(new Vec2i(mouseX, mouseY), this.boundingBox) && this.mouseRightClicked) {
			this.mouseRightClicked = false;
		}
	}

	void render(int mouseX, int mouseY, float partialTicks, AbstractGui screen) {
		if (this.mouseRightClicked) {
			this.canvas.render(this.boundingBox.getPos().x, this.boundingBox.getPos().y, screen);
		}
	}

}
