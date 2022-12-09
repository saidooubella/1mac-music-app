package com.said.oubella.so.player.data.columns;

import android.database.*;
import android.provider.*;

public final class AlbumColumns {
	
	private final int idColumn;
	private final int titleColumn;
	private final int artistColumn;
	private final int songsCountColumn;
	
	public AlbumColumns(final Cursor cursor) {
		this.songsCountColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.NUMBER_OF_SONGS);
		this.artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.ARTIST);
		this.titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.ALBUM);
		this.idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums._ID);
	}

	public int idColumn() {
		return idColumn;
	}

	public int titleColumn() {
		return titleColumn;
	}

	public int artistColumn() {
		return artistColumn;
	}

	public int songsCountColumn() {
		return songsCountColumn;
	}
}
