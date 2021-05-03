package com.loucaskreger.draggableui.util;

public class Memento<T> {
	private T state;

	public Memento(T state) {
		this.state = state;
	}

	public T getState() {
		return this.state;
	}

	@Override
	public boolean equals(Object obj) {
		return this.state.equals(obj);
	}

}
