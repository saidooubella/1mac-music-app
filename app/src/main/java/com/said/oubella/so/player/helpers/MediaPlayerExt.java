package com.said.oubella.so.player.helpers;

import android.media.*;
import android.util.*;

public final class MediaPlayerExt extends MediaPlayer {

	public static final Property<MediaPlayerExt, Float> VOLUME = new Property<MediaPlayerExt, Float>(Float.class, "volume") {

		@Override
		public Float get(MediaPlayerExt p1) {
			return p1.getVolume();
		}

		@Override
		public void set(MediaPlayerExt object, Float value) {
			object.setVolume(value);
		}
	};
	
	private float volume = 1f;
	
	private void setVolume(float volume) {
		this.setVolume(volume, volume);
		this.volume = volume;
	}
	
	private float getVolume() {
		return volume;
	}
}
