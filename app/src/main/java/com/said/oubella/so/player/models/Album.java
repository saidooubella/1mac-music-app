package com.said.oubella.so.player.models;

import android.net.*;
import java.util.*;

public final class Album {

	private final long id;
	private final Uri art;
	private final String title;
	private final String artist;
	private final int songsCount;

	public Album(long id, Uri art, String title, String artist, int songsCount) {
		this.artist = Objects.requireNonNull(artist);
		this.title = Objects.requireNonNull(title);
		this.art = Objects.requireNonNull(art);
		this.songsCount = songsCount;
		this.id = id;
	}

	public long id() {
		return id;
	}

	public Uri art() {
		return art;
	}

	public String title() {
		return title;
	}

	public String artist() {
		return artist;
	}

	public int songsCount() {
		return songsCount;
	}

	@Override
	public int hashCode() {
		int result = songsCount;
		result = 31 * result + Objects.hashCode(artist);
		result = 31 * result + Objects.hashCode(title);
		result = 31 * result + Objects.hashCode(art);
		result = 31 * result + ((int)(id ^ (id >>> 32)));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (!(obj instanceof Album)) return false;
		final Album other = (Album) obj;
		return id == other.id && art.equals(other.art) && 
			title.equals(other.title) && artist.equals(other.artist) &&
			songsCount == other.songsCount;
	}

	@Override
	public String toString() {
		return title + " : " + artist;
	}
}
