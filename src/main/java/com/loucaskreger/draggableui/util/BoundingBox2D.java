package com.loucaskreger.draggableui.util;

import java.util.List;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;

public class BoundingBox2D implements INBTSerializable<CompoundNBT> {

	private Vec2i pos;
	private Vec2i velocity;
	private int width;
	private int height;
	private boolean isVisible;
	private Color4f color;

	public BoundingBox2D(Vec2i pos, int width, int height, Color4f color) {
		this.pos = pos;
		this.velocity = Vec2i.ZERO;
		this.width = width;
		this.height = height;
		this.isVisible = false;
		this.color = color;
	}

	public BoundingBox2D(Vec2i pos, int width, int height) {
		this(pos, width, height, new Color4f(1f, 0f, 0f, 1f));
	}

	BoundingBox2D(CompoundNBT nbt) {
		this.deserializeNBT(nbt);
	}

	@Override
	public String toString() {
		return String.format("X: %d, Y: %d, Width: %d, Height: %d", this.pos.x, this.pos.y, this.width, this.height);
	}

	public boolean areSameDimension(BoundingBox2D box) {
		return box.getPos().x == this.getPos().x && box.getPos().y == this.getPos().y
				&& box.getWidth() == this.getWidth() && box.getHeight() == this.getHeight();

	}

	public boolean collidesAny(List<BoundingBox2D> boxes) {
		for (BoundingBox2D box : boxes) {
			if (this.collidesAny(box)) {
				return true;
			}
		}

		return false;
	}

	public boolean collidesAny(BoundingBox2D boundingBox) {
		return collidesOnTop(boundingBox) || collidesOnRight(boundingBox) || collidesOnBottom(boundingBox)
				|| collidesOnLeft(boundingBox);
	}

	public boolean collidesOnTop(BoundingBox2D boundingBox) {
		return this.getBottom() + this.velocity.y >= boundingBox.getTop() && this.getTop() <= boundingBox.getTop()
				&& this.getRight() >= boundingBox.getLeft() && this.getLeft() <= boundingBox.getRight();
	}

	public boolean collidesOnRight(BoundingBox2D boundingBox) {
		return this.getLeft() + this.velocity.x <= boundingBox.getRight() && this.getRight() >= boundingBox.getRight()
				&& this.getBottom() >= boundingBox.getTop() && this.getTop() <= boundingBox.getBottom();
	}

	public boolean collidesOnBottom(BoundingBox2D boundingBox) {
		return this.getTop() + this.velocity.y <= boundingBox.getBottom() && this.getBottom() >= boundingBox.getBottom()
				&& this.getRight() >= boundingBox.getLeft() && this.getLeft() <= boundingBox.getRight();
	}

	public boolean collidesOnLeft(BoundingBox2D boundingBox) {
		return this.getRight() + this.velocity.x >= boundingBox.getLeft() && this.getLeft() <= boundingBox.getLeft()
				&& this.getBottom() >= boundingBox.getTop() && this.getTop() <= boundingBox.getBottom();
	}

	public boolean isWithinAny(List<BoundingBox2D> boxes) {
		for (BoundingBox2D box : boxes) {
			if (this.isWithin(box)) {
				return true;
			}
		}
		return false;
	}

	public boolean isWithin(BoundingBox2D boundingBox) {
		return this.getTop() > boundingBox.getTop() && this.getRight() < boundingBox.getRight()
				&& this.getBottom() < boundingBox.getBottom() && this.getLeft() > boundingBox.getLeft();
	}

	public int getTop() {
		return this.pos.y;
	}

	public int getRight() {
		return this.pos.x + this.width;
	}

	public int getBottom() {
		return this.pos.y + this.height;
	}

	public int getLeft() {
		return this.pos.x;
	}

	public void drawBoundingBoxOutline() {
		if (this.isVisible()) {
			RenderSystem.pushMatrix();
			IRenderTypeBuffer.Impl buffer = Minecraft.getInstance().getRenderTypeBuffers().getBufferSource();
			IVertexBuilder builder = buffer.getBuffer(RenderType.LINES);

			// topLeft -> topRight
			this.drawLine(builder, new Vec2i(this.getLeft(), this.getTop()), new Vec2i(this.getRight(), this.getTop()));
			// topRight -> bottomRight
			this.drawLine(builder, new Vec2i(this.getRight(), this.getTop()),
					new Vec2i(this.getRight(), this.getBottom()));
			// bottomRight -> bottomLeft
			this.drawLine(builder, new Vec2i(this.getRight(), this.getBottom()),
					new Vec2i(this.getLeft(), this.getBottom()));
			// bottomLeft -> topLeft
			this.drawLine(builder, new Vec2i(this.getLeft(), this.getBottom()),
					new Vec2i(this.getLeft(), this.getTop()));
			buffer.finish(RenderType.LINES);
			RenderSystem.popMatrix();
		}
	}

	private void drawLine(IVertexBuilder builder, Vec2i point1, Vec2i point2) {
		this.drawLine(builder, point1.x, point1.y, point2.x, point2.y);
	}

	private void drawLine(IVertexBuilder builder, int x1, int y1, int x2, int y2) {
		builder.pos(x1, y1, 0).color(this.color.red, this.color.green, this.color.blue, this.color.alpha).endVertex();
		builder.pos(x2, y2, 0).color(this.color.red, this.color.green, this.color.blue, this.color.alpha).endVertex();
	}

	public Vec2i getPos() {
		return pos;
	}

	public Vec2i getVelocity() {
		return this.velocity;
	}

	public void setVelocity(Vec2i velocity) {
		this.velocity = velocity;
	}

	public void addVelocity(Vec2i velocity) {
		this.velocity = this.velocity.add(velocity);
	}

	public void setPos(Vec2i pos) {
		this.pos = pos;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public boolean isVisible() {
		return isVisible;
	}

	public void setVisible(boolean isVisible) {
		this.isVisible = isVisible;
	}

	public Color4f getColor() {
		return color;
	}

	public void setColor(Color4f color) {
		this.color = color;
	}

	@Override
	public CompoundNBT serializeNBT() {
		CompoundNBT nbt = new CompoundNBT();
		nbt.put("pos", this.getPos().serializeNBT());
		nbt.put("velocity", this.getVelocity().serializeNBT());
		nbt.putInt("width", this.getWidth());
		nbt.putInt("height", this.getHeight());
		nbt.putBoolean("visible", this.isVisible());
		nbt.put("color", this.color.serializeNBT());
		return nbt;
	}

	@Override
	public void deserializeNBT(CompoundNBT nbt) {
		this.setPos(Vec2i.read((CompoundNBT) nbt.get("pos")));
		this.setVelocity(Vec2i.read((CompoundNBT) nbt.getCompound("velocity")));
		this.setWidth(nbt.getInt("width"));
		this.setHeight(nbt.getInt("height"));
		this.setVisible(nbt.getBoolean("visible"));
		this.setColor(new Color4f((CompoundNBT) nbt.get("color")));
	}

	public static BoundingBox2D read(CompoundNBT nbt) {
		return new BoundingBox2D(nbt);
	}

}
