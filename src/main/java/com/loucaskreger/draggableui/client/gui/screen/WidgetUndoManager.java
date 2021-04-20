package com.loucaskreger.draggableui.client.gui.screen;

import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;
import com.loucaskreger.draggableui.client.gui.widget.DraggableWidget;
import net.minecraft.nbt.CompoundNBT;

public class WidgetUndoManager {

	private Stack<List<CompoundNBT>> undoStack;
	private Stack<List<CompoundNBT>> redoStack;
	// Reference to the screen widgets
	private List<DraggableWidget> widgets;

	public WidgetUndoManager(List<DraggableWidget> widgets) {
		this.widgets = widgets;
		this.undoStack = new Stack<List<CompoundNBT>>();
		this.redoStack = new Stack<List<CompoundNBT>>();
	}

	public void addState() {
		for (DraggableWidget widget : widgets) {
			if (widget.isSelected()) {
				this.undoStack.push(this.widgets.stream().map(wid -> wid.serializeNBT()).collect(Collectors.toList()));
				break;
			}
		}
	}

	public void undo() {
		if (!this.undoStack.isEmpty()) {
			List<CompoundNBT> nbt = this.undoStack.pop();
			this.redoStack.push(nbt);
			this.widgets = nbt.stream().map(compNbt -> DraggableWidget.read(compNbt)).collect(Collectors.toList());
		}
	}

	public void redo() {
		if (!this.redoStack.isEmpty()) {
			List<CompoundNBT> nbt = this.redoStack.pop();
			this.undoStack.push(nbt);
			this.widgets = nbt.stream().map(compNbt -> DraggableWidget.read(compNbt)).collect(Collectors.toList());
		}
	}

}
