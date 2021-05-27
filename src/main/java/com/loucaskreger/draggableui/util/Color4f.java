package com.loucaskreger.draggableui.util;

import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;

public class Color4f implements INBTSerializable<CompoundNBT> {

	public static final Color4f BLANK = new Color4f(1.0f, 1.0f, 1.0f, 0.0f);

	public float red;
	public float green;
	public float blue;
	public float alpha;

	private String redKey = "red";
	private String blueKey = "blue";
	private String greenKey = "green";
	private String alphaKey = "alpha";

	public Color4f(float red, float green, float blue, float alpha) {
		this.red = red;
		this.green = green;
		this.blue = blue;
		this.alpha = alpha;
	}

	Color4f(CompoundNBT nbt) {
		this.deserializeNBT(nbt);
	}

	@Override
	public CompoundNBT serializeNBT() {
		CompoundNBT nbt = new CompoundNBT();
		nbt.putFloat(redKey, this.red);
		nbt.putFloat(blueKey, this.blue);
		nbt.putFloat(greenKey, this.green);
		nbt.putFloat(alphaKey, this.alpha);
		return nbt;
	}

	@Override
	public void deserializeNBT(CompoundNBT nbt) {
		this.red = nbt.getFloat(redKey);
		this.green = nbt.getFloat(greenKey);
		this.blue = nbt.getFloat(blueKey);
		this.alpha = nbt.getFloat(alphaKey);
	}

	public static Color4f read(CompoundNBT nbt) {
		return new Color4f(nbt);
	}
}
