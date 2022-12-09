package com.said.oubella.so.player.viewModels;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.said.oubella.so.player.data.DataRepository;
import com.said.oubella.so.player.helpers.Task;
import com.said.oubella.so.player.models.Album;
import com.said.oubella.so.player.models.Track;
import com.said.oubella.so.player.player.MusicPlayer;

import java.util.List;

public final class AlbumTracksViewModel extends ViewModel {

    private final MutableLiveData<Album> album = new MutableLiveData<>();
    private final MutableLiveData<List<Track>> tracks = new MutableLiveData<>();

    private final DataRepository repository;
    private final MusicPlayer player;
    private final long albumId;

    private Task albumTracksTask;
    private Task albumTask;

    private AlbumTracksViewModel(DataRepository repository, MusicPlayer player, long albumId) {
        this.repository = repository;
        this.player = player;
        this.albumId = albumId;
        this.init();
    }

    private void init() {
        albumTracksTask = repository.getAlbumTracks(albumId, true, tracks::setValue);
        albumTask = repository.getAlbum(albumId, true, album::setValue);
    }

    public MusicPlayer getPlayer() {
        return player;
    }

    public LiveData<Album> getAlbum() {
        return album;
    }

    public LiveData<List<Track>> getTracks() {
        return tracks;
    }

    @Override
    protected void onCleared() {
        if (albumTracksTask != null)
            albumTracksTask.cancel();
        if (albumTask != null)
            albumTask.cancel();
    }

    public static final class Factory implements ViewModelProvider.Factory {

        private final DataRepository repository;
        private final MusicPlayer player;
        private final long albumId;

        public Factory(DataRepository repository, MusicPlayer player, long albumId) {
            this.repository = repository;
            this.player = player;
            this.albumId = albumId;
        }

        @NonNull
        @Override
        @SuppressWarnings("unchecked")
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            return (T) new AlbumTracksViewModel(repository, player, albumId);
        }
    }
}
