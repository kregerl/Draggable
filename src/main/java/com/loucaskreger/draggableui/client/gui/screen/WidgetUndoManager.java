package com.loucaskreger.draggableui.client.gui.screen;

import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

import com.loucaskreger.draggableui.client.gui.widget.DraggableWidget;
import com.loucaskreger.draggableui.util.WidgetManager;
import net.minecraft.nbt.CompoundNBT;

public class WidgetUndoManager {

	private Stack<List<CompoundNBT>> undoStack;
	private Stack<List<CompoundNBT>> redoStack;
	// Reference to the screen
	private DraggableScreen screen;

	public WidgetUndoManager(DraggableScreen screen) {
		this.screen = screen;
		this.undoStack = new Stack<List<CompoundNBT>>();
		this.redoStack = new Stack<List<CompoundNBT>>();
	}

	public void addState() {
		this.undoStack.push(this.screen.widgets.stream().map(wid -> wid.serializeNBT()).collect(Collectors.toList()));

	}

	public void undo() {
		System.out.println("Outside: " + this.undoStack.size());
		if (!this.undoStack.isEmpty()) {
			List<CompoundNBT> nbt = this.undoStack.pop();
			System.out.println("Inside: " + this.undoStack.size());
			this.redoStack.push(nbt);
			this.screen.widgets = nbt.stream().map(compNbt -> WidgetManager.INSTANCE.read(compNbt))
					.collect(Collectors.toList());
		}
	}

	public void redo() {
		if (!this.redoStack.isEmpty()) {
			List<CompoundNBT> nbt = this.redoStack.pop();
			this.undoStack.push(nbt);
			this.screen.widgets = nbt.stream().map(compNbt -> WidgetManager.INSTANCE.read(compNbt))
					.collect(Collectors.toList());
		}
	}

}
