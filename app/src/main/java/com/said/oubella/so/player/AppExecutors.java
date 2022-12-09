package com.said.oubella.so.player;

import java.util.concurrent.*;
import android.os.*;

public final class AppExecutors {

	private final Executor defaultExecutor;
	private final Executor mainExecutor;
	private final Executor ioExecutor;

	private final Handler mainHandler;

	AppExecutors() {
		defaultExecutor = Executors.newSingleThreadExecutor();
		ioExecutor = Executors.newFixedThreadPool(3);
		mainHandler = new Handler(Looper.getMainLooper());
		mainExecutor = new MainExecutor(mainHandler);
	}

	public Executor defaultExecutor() {
		return defaultExecutor;
	}

	public Handler mainHandler() {
		return mainHandler;
	}

	public Executor mainExecutor() {
		return mainExecutor;
	}

	public Executor ioExecutor() {
		return ioExecutor;
	}

	private static final class MainExecutor implements Executor {

		private final Handler mainHandler;
		
		private MainExecutor(Handler mainHandler) {
			this.mainHandler = mainHandler;
		}

		@Override
		public void execute(Runnable runnable) {
			mainHandler.post(runnable);
		}
	}
}
