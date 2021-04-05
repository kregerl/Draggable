package com.loucaskreger.draggableui.client.gui.widget;

import com.loucaskreger.draggableui.util.Vec2i;

import net.minecraft.client.Minecraft;

public class LinkableWidget extends DraggableWidget {

	protected static final Minecraft mc = Minecraft.getInstance();

	private boolean isLinked;

	public LinkableWidget(Vec2i pos, int width, int height) {
		super(pos, width, height);
		this.isLinked = true;
	}

	public LinkableWidget(int x, int y, int width, int height) {
		this(new Vec2i(x, y), width, height);
	}

	@Override
	public void tick() {
		super.tick();
		if (this.isSelected()) {
			this.setLinked(false);
		}
	}

	@Override
	protected void moveCursorBounds(Vec2i offset) {
		if (this.getCursorBoundingBox() != null) {
			this.getCursorBoundingBox().setPos(this.cursorPos.subtract(offset));
			this.getCursorBoundingBox().setVisible(true);
		}
	}

	@Override
	public void mouseReleased() {
		super.mouseReleased();
	}

	public boolean isLinked() {
		return isLinked;
	}

	public void setLinked(boolean linked) {
		this.isLinked = linked;
	}

}
