package com.said.oubella.so.player.data;

import android.widget.EditText;

import com.said.oubella.so.player.helpers.ResultCallback;
import com.said.oubella.so.player.helpers.Task;
import com.said.oubella.so.player.models.Album;
import com.said.oubella.so.player.models.Track;

import java.util.List;

public interface DataRepository {

    Task searchForTracks(EditText textView, boolean postOnMainThread, ResultCallback<List<Track>> callback);

    Task getTracks(boolean postOnMainThread, ResultCallback<List<Track>> callback);

    Task getTrack(long trackId, boolean postOnMainThread, ResultCallback<Track> callback);

    Task getAlbumTracks(long albumId, boolean postOnMainThread, ResultCallback<List<Track>> callback);

    Task getAlbum(long albumId, boolean postOnMainThread, ResultCallback<Album> callback);

    Task getAlbums(boolean postOnMainThread, ResultCallback<List<Album>> callback);
}
