package com.loucaskreger.draggableui.client.gui.widget;

import com.loucaskreger.draggableui.util.Color4f;
import com.loucaskreger.draggableui.util.Vec2i;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundNBT;

public class LinkableWidget extends DraggableWidget {

	protected static final Minecraft mc = Minecraft.getInstance();

	private boolean isLinked;
	private Vec2i offset;

	public LinkableWidget(Vec2i pos, int width, int height) {
		super(pos, width, height);
		this.isLinked = false;
		this.offset = new Vec2i(0, 0);

	}

	public LinkableWidget(int x, int y, int width, int height) {
		this(new Vec2i(x, y), width, height);
	}

	@Override
	public void mouseClicked(double mouseX, double mouseY) {
		super.mouseClicked(mouseX, mouseY);
		if (this.isSelected()) {
			this.setLinked(false);
		}
	}

	@Override
	public void tick() {
		super.tick();
		if (this.isSelected()) {
			this.setLinked(false);
		}
		if (!this.isLinked) {
			this.getBoundingBox().setColor(new Color4f(0.35f, 0.45f, 0.35f, 1.0f));
		} else {
			this.getBoundingBox().setColor(new Color4f(1f, 0f, 0f, 1f));
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
	public void mouseReleased(double mouseX, double mouseY, int scrollDelta) {
		super.mouseReleased(mouseX, mouseY, scrollDelta);
	}

	protected void updateOffset(Vec2i cursorPos) {
		this.offset = cursorPos.subtract(this.getBoundingBox().getPos());
	}

	public boolean isLinked() {
		return isLinked;
	}

	public void setLinked(boolean linked) {
		this.isLinked = linked;
	}

	protected Vec2i getOffset() {
		return offset;
	}

	protected void setOffset(Vec2i offset) {
		this.offset = offset;
	}

	private static final String LINKED_KEY = "linked";
	private static final String OFFSET_KEY = "offset";

	@Override
	public CompoundNBT serializeNBT() {
		CompoundNBT nbt = super.serializeNBT();
		nbt.putBoolean(LINKED_KEY, this.isLinked());
		nbt.put(OFFSET_KEY, this.offset.serializeNBT());
		return nbt;
	}

	@Override
	public void deserializeNBT(CompoundNBT nbt) {
		super.deserializeNBT(nbt);
		this.setLinked(nbt.getBoolean(LINKED_KEY));
		this.setOffset(Vec2i.read(nbt.getCompound(OFFSET_KEY)));
	}

}
