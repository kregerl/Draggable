package com.loucaskreger.draggableui.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import com.loucaskreger.draggableui.DraggableUI;
import com.loucaskreger.draggableui.client.gui.widget.DraggableWidget;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.loading.FMLPaths;

public class WidgetManager {

	private static Path path = FMLPaths.GAMEDIR.get().resolve("draggableUI/state/save.nbt");
	private static final String LIST_KEY = "list";
	public static WidgetManager INSTANCE;
	public List<DraggableWidget> widgets;
	private boolean isDirty;

	public static void init() {
		INSTANCE = new WidgetManager();
	}

	public WidgetManager() {
		this.widgets = new ArrayList<DraggableWidget>();
		this.isDirty = true;
		File file = path.toFile();
		try {
			file.getParentFile().mkdirs();
			file.createNewFile();
		} catch (IOException e) {
			DraggableUI.LOGGER.error(e);
		}
	}

	public void loadWidgets() {
		File file = path.toFile();
		if (file.length() != 0) {
			try {
				this.widgets.clear();
				FileInputStream fileInputStream = new FileInputStream(file);
				CompoundNBT nbt = CompressedStreamTools.readCompressed(fileInputStream);
				ListNBT list = (ListNBT) nbt.get(LIST_KEY);
				for (int i = 0; i < list.size(); i++) {
					CompoundNBT tag = list.getCompound(i);
					widgets.add(this.read(tag));
				}
			} catch (IOException e) {
				DraggableUI.LOGGER.error(e);
			}
		}

	}

	public DraggableWidget read(CompoundNBT tag) {
		DraggableWidget wid = GameRegistry.findRegistry(DraggableWidget.class)
				.getValue(new ResourceLocation(tag.getString("id")));
		((INBTSerializable<CompoundNBT>) wid).deserializeNBT(tag);
		return wid;
	}

	public void saveState(List<DraggableWidget> widgets) {
		File file = path.toFile();
		CompoundNBT parent = new CompoundNBT();
		ListNBT nbt = new ListNBT();
		for (DraggableWidget widget : widgets) {
			nbt.add(widget.serializeNBT());
		}
		parent.put(LIST_KEY, nbt);
		FileOutputStream fileoutputstream;
		try {
			fileoutputstream = new FileOutputStream(file);
			CompressedStreamTools.writeCompressed(parent, fileoutputstream);
			fileoutputstream.close();
			// Mark dirty so so load can be called
			this.isDirty = true;
		} catch (IOException e) {
			DraggableUI.LOGGER.error(e);
		}

	}

	public boolean isDirty() {
		return this.isDirty;
	}

	public void setIsDirty(boolean dirty) {
		this.isDirty = dirty;
	}

	public List<DraggableWidget> getWidgets() {
		return this.widgets;
	}

}
