package com.said.oubella.so.player;

import android.app.*;

public final class SOPlayerApp extends Application {
	
	private AppContainer container;

	@Override
	public void onCreate() {
		super.onCreate();
		container = new AppContainer(this);
	}

	public AppContainer container() {
		return container;
	}
}
