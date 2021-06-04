package com.loucaskreger.draggableui.client.gui.widget;

import java.util.List;
import javax.annotation.Nullable;
import com.loucaskreger.draggableui.client.gui.screen.DraggableScreen;
import com.loucaskreger.draggableui.util.BoundingBox2D;
import com.loucaskreger.draggableui.util.Canvas;
import com.loucaskreger.draggableui.util.Color4f;
import com.loucaskreger.draggableui.util.Util;
import com.loucaskreger.draggableui.util.Vec2i;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;

public class StaticWidget implements INBTSerializable<CompoundNBT> {

	private Canvas canvas;
	private BoundingBox2D boundingBox;
	protected DraggableScreen screen;

	public StaticWidget(BoundingBox2D boundingBox, DraggableScreen screen) {
		this.boundingBox = boundingBox;
		this.screen = screen;
		this.canvas = new Canvas(this.boundingBox, new Color4f(1.0f, 0.0f, 1.0f, 1.0f));
	}

	public StaticWidget(Vec2i pos, int width, int height, DraggableScreen screen) {
		this(new BoundingBox2D(pos, width, height), screen);
	}

	public StaticWidget(int x, int y, int width, int height, DraggableScreen screen) {
		this(new BoundingBox2D(x, y, width, height), screen);
	}

	public void mouseClicked(double mouseX, double mouseY, int mouseButton, DraggableScreen screen) {
	}

	public void mouseMoved(double mouseX, double mouseY) {
	}

	public void render(int mouseX, int mouseY, float partialTicks, AbstractGui screen) {
	}

	@Nullable
	public DraggableWidget isOverWidget(double mouseX, double mouseY, List<DraggableWidget> widgets) {
		for (DraggableWidget widget : widgets) {
			if (Util.isWithinBounds(new Vec2i(mouseX, mouseY), widget.getBoundingBox())) {
				return widget;
			}
		}
		return null;
	}

	public DraggableWidget isOverWidget(double mouseX, double mouseY, DraggableScreen screen) {
		return isOverWidget(mouseX, mouseY, screen.widgets);
	}

	public BoundingBox2D getBoundingBox() {
		return this.boundingBox;
	}

	public Canvas getCanvas() {
		return this.canvas;
	}

	private static final String CANVAS_KEY = "texture";
	private static final String BOX_KEY = "boundingbox";

	@Override
	public CompoundNBT serializeNBT() {
		CompoundNBT nbt = new CompoundNBT();
		nbt.put(CANVAS_KEY, this.getCanvas().serializeNBT());
		nbt.put(BOX_KEY, this.getBoundingBox().serializeNBT());
		return nbt;
	}

	@Override
	public void deserializeNBT(CompoundNBT nbt) {
		this.canvas.deserializeNBT(nbt.getCompound(CANVAS_KEY));
		BoundingBox2D box = BoundingBox2D.read(nbt.getCompound(BOX_KEY));
		this.boundingBox.setPos(box.getPos());
		this.boundingBox.setWidth(box.getWidth());
		this.boundingBox.setHeight(box.getHeight());

	}

}
