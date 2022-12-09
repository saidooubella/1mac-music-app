package com.said.oubella.so.player.data;

import com.said.oubella.so.player.models.Album;
import com.said.oubella.so.player.models.Track;

import java.util.List;

public interface DataSource {

    List<Track> getTracks();

    List<Track> searchForTracks(String query);

    Track getTrack(long trackId);

    List<Album> getAlbums();

    List<Track> getAlbumTracks(long albumId);

    Album getAlbum(long albumId);
}
