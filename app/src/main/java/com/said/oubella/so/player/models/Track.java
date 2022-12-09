package com.said.oubella.so.player.models;

import android.net.*;

public final class Track {

	private final long trackId;
	private final String title;

	private final long artistId;
	private final String artist;

	private final long albumId;
	private final String album;

	private final Uri uri;
	private final Uri art;

	private final int duration;
	private final int size;

	public Track(long trackId, String title, long artistId, String artist, long albumId, String album, Uri uri, Uri art, int duration, int size) {
		this.trackId = trackId;
		this.title = title;
		this.artistId = artistId;
		this.artist = artist;
		this.albumId = albumId;
		this.album = album;
		this.uri = uri;
		this.art = art;
		this.duration = duration;
		this.size = size;
	}

	public long trackId() {
		return trackId;
	}

	public String title() {
		return title;
	}

	public String artist() {
		return artist;
	}

	public String album() {
		return album;
	}

	public Uri uri() {
		return uri;
	}

	public Uri art() {
		return art;
	}

	public int duration() {
		return duration;
	}

	public int size() {
		return size;
	}

	@Override
	public int hashCode() {
		int result = (int)(albumId ^ (albumId >>> 32));
		result = 31 * result + ((int)(trackId ^ (trackId >>> 32)));
		result = 31 * result + ((int)(artistId ^ (artistId >>> 32)));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (!(obj instanceof Track)) return false;
		final Track other = (Track) obj;
		return albumId == other.albumId &&
			artistId == other.artistId &&
			trackId == other.trackId;
	}

	@Override
	public String toString() {
		return title + " - " + artist;
	}
}
