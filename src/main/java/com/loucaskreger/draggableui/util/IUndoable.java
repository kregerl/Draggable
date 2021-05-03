package com.loucaskreger.draggableui.util;

public interface IUndoable<T> {

	public Memento<T> saveState();

	public void restoreState(Memento<T> memento);
}
