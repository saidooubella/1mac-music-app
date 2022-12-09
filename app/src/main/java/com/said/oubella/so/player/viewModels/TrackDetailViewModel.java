package com.said.oubella.so.player.viewModels;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.said.oubella.so.player.data.DataRepository;
import com.said.oubella.so.player.helpers.Task;
import com.said.oubella.so.player.models.Track;

public final class TrackDetailViewModel extends ViewModel {

    private final MutableLiveData<Track> track = new MutableLiveData<>();

    private final DataRepository repository;
    private final long trackId;

    private Task trackTask;

    private TrackDetailViewModel(DataRepository repository, long trackId) {
        this.repository = repository;
        this.trackId = trackId;
        this.init();
    }

    private void init() {
        trackTask = repository.getTrack(trackId, true, track::setValue);
    }

    public LiveData<Track> getTrack() {
        return track;
    }

    @Override
    protected void onCleared() {
        if (trackTask != null)
            trackTask.cancel();
    }

    public static final class Factory implements ViewModelProvider.Factory {

        private final DataRepository repository;
        private final long trackId;

        public Factory(DataRepository repository, long trackId) {
            this.repository = repository;
            this.trackId = trackId;
        }

        @NonNull
        @Override
        @SuppressWarnings("unchecked")
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            return (T) new TrackDetailViewModel(repository, trackId);
        }
    }
}
