package com.said.oubella.so.player.data.columns;

import android.annotation.SuppressLint;
import android.database.*;
import android.provider.*;

public final class TrackColumns {

	private final int idColumn;
	private final int titleColumn;
	private final int durationColumn;
	private final int artistIdColumn;
	private final int albumIdColumn;
	private final int albumColumn;
	private final int artistColumn;
	private final int sizeColumn;
	
	@SuppressLint("InlinedApi")
	public TrackColumns(final Cursor cursor) {
		idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID);
		titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE);
		durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION);
		albumIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID);
		artistIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST_ID);
		albumColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM);
		artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST);
		sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE);
	}

	public int artistIdColumn() {
		return artistIdColumn;
	}

	public int idColumn() {
		return idColumn;
	}

	public int titleColumn() {
		return titleColumn;
	}

	public int durationColumn() {
		return durationColumn;
	}

	public int albumIdColumn() {
		return albumIdColumn;
	}

	public int albumColumn() {
		return albumColumn;
	}

	public int artistColumn() {
		return artistColumn;
	}

	public int sizeColumn() {
		return sizeColumn;
	}
}
