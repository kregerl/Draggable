package com.loucaskreger.draggableui.client.gui.widget;

import com.loucaskreger.draggableui.util.Vec2i;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;

public class DraggableWidget {

	/*
	 * The x and y components of the distance between the widget pos and the cursor
	 * pos.
	 */
	private Vec2i mouseDistance;
	// Pos of the top left corner of the widget
	private Vec2i pos;
	// Last updated cursor position
	private Vec2i cursorPos;
	// Enabled by default
	private boolean enabled;
	// Disabled by default
	private boolean selected;
	private boolean drawBoundingBox;

	public int width;
	public int height;

	public DraggableWidget(int x, int y, int width, int height, boolean enabled) {
		this.pos = new Vec2i(x, y);
		this.width = width;
		this.height = height;
		this.setEnabled(enabled);
		this.setSelected(false);
		this.cursorPos = null;
		this.mouseDistance = null;
		this.drawBoundingBox = false;
	}

	public DraggableWidget(int width, int height) {
		this(0, 0, width, height, true);
	}

	public void mouseClicked(double mouseX, double mouseY) {
		if (this.cursorPos != null) {
		}

		this.cursorPos = new Vec2i(mouseX, mouseY);

		boolean isInBoundsX = this.cursorPos.x > this.pos.x && this.cursorPos.x < this.pos.x + this.width;
		boolean isInBoundsY = this.cursorPos.y > this.pos.y && this.cursorPos.y < this.pos.y + this.height;

		if (isInBoundsX && isInBoundsY) {
			this.setSelected(true);
			updatePosition((int) Math.round(mouseX), (int) Math.round(mouseY));
		}
	}

	public void updatePosition(int mouseX, int mouseY) {
		this.mouseDistance = new Vec2i((int) Math.round(mouseX) - this.pos.x, (int) Math.round(mouseY) - this.pos.y);
	}

	public void render(int mouseX, int mouseY, Screen screen) {
		if (this.enabled) {
			boolean flag = true;
			int x = this.pos.x;
			int y = this.pos.y;

			if (this.pos.x <= 0) {
				x = 0;
				clearCursorPos();
			}
			if (this.pos.y <= 0) {
				y = 0;
				clearCursorPos();
			}
			if (x + this.width >= screen.width) {
				x = screen.width - this.width;
				clearCursorPos();

			}
			if (y + this.height >= screen.height) {
				y = screen.height - this.height;
				clearCursorPos();
			}
			this.pos = new Vec2i(x, y);
			if (this.drawBoundingBox) {
				this.drawBoundingBox();
			}
		}

	}

	protected void drawBoundingBox() {
		Pair<Vec2i, Vec2i> box = this.getBoundingBox();
		IRenderTypeBuffer.Impl buffer = Minecraft.getInstance().getRenderTypeBuffers().getBufferSource();
		IVertexBuilder builder = buffer.getBuffer(RenderType.LINES);
		Vec2i topLeft = box.getFirst();
		Vec2i topRight = new Vec2i(box.getSecond().x, box.getFirst().y);
		Vec2i bottomLeft = new Vec2i(box.getFirst().x, box.getSecond().y);
		Vec2i bottomRight = box.getSecond();

		// topLeft -> topRight
		builder.pos(topLeft.x, topLeft.y, 0).color(1f, 0f, 0f, 1f).endVertex();
		builder.pos(topRight.x, topRight.y, 0).color(1f, 0, 0, 1f).endVertex();
		// topRight -> bottomRight
		builder.pos(topRight.x, topRight.y, 0).color(1f, 0f, 0f, 1f).endVertex();
		builder.pos(bottomRight.x, bottomRight.y, 0).color(1f, 0, 0, 1f).endVertex();
		// bottomRight -> bottomLeft
		builder.pos(bottomRight.x, bottomRight.y, 0).color(1f, 0f, 0f, 1f).endVertex();
		builder.pos(bottomLeft.x, bottomLeft.y, 0).color(1f, 0, 0, 1f).endVertex();
		// bottomLeft -> topLeft
		builder.pos(bottomLeft.x, bottomLeft.y, 0).color(1f, 0f, 0f, 1f).endVertex();
		builder.pos(topLeft.x, topLeft.y, 0).color(1f, 0, 0, 1f).endVertex();

		buffer.finish(RenderType.LINES);
	}

	public void mouseDragged(int mouseX, int mouseY) {
		if (this.isSelected()) {
			// Need to make sure this stops getting called when widget gets snapped to a
			// wall.
			this.setPos((int) Math.round(mouseX) - this.mouseDistance.x,
					(int) Math.round(mouseY) - this.mouseDistance.y);
		}
	}

	public Pair<Vec2i, Vec2i> getBoundingBox() {
		return new Pair<Vec2i, Vec2i>(new Vec2i(this.pos.x, this.pos.y),
				new Vec2i(this.pos.x + this.width, this.pos.y + this.height));
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public void setPos(Vec2i pos) {
		this.pos = pos;
	}

	public void setPos(int x, int y) {
		this.setPos(new Vec2i(x, y));
	}

	public Vec2i getPos() {
		return this.pos;
	}

	public Vec2i getDistance() {
		return this.mouseDistance;
	}

	private void clearCursorPos() {
		this.cursorPos = null;
	}

	public boolean isDrawingBoundingBox() {
		return this.drawBoundingBox;
	}

	public void setDrawingBoundingBox(boolean drawBoundingBox) {
		this.drawBoundingBox = drawBoundingBox;
	}

}
