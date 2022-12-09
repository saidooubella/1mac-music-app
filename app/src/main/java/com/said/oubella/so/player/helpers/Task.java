package com.said.oubella.so.player.helpers;

public abstract class Task {

	protected abstract void onInvalidate();
	protected abstract void onCancel();

	public final void invalidate() { onInvalidate(); }
	public final void cancel() { onCancel(); }
}
