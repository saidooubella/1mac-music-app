package com.said.oubella.so.player.viewModels;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.said.oubella.so.player.data.DataRepository;
import com.said.oubella.so.player.helpers.Task;
import com.said.oubella.so.player.player.MusicPlayer;
import com.said.oubella.so.player.state.TracksScreenState;

public final class TracksViewModel extends ViewModel {

    private final MutableLiveData<TracksScreenState> state = new MutableLiveData<>(new TracksScreenState.None());

    private final DataRepository repository;
    private final MusicPlayer player;

    private Task tracksTask;

    private TracksViewModel(DataRepository repository, MusicPlayer player) {
        this.repository = repository;
        this.player = player;
        this.init();
    }

    private void init() {
        tracksTask = repository.getTracks(true, value -> {
            if (value.isEmpty()) {
                state.setValue(new TracksScreenState.Empty());
            } else {
                state.setValue(new TracksScreenState.Result(value));
            }
        });
    }

    public LiveData<TracksScreenState> getState() {
        return state;
    }

    public MusicPlayer getPlayer() {
        return player;
    }

    @Override
    protected void onCleared() {
        if (tracksTask != null)
            tracksTask.cancel();
    }

    public static final class Factory implements ViewModelProvider.Factory {

        private final DataRepository repository;
        private final MusicPlayer player;

        public Factory(DataRepository repository, MusicPlayer player) {
            this.repository = repository;
            this.player = player;
        }

        @NonNull
        @Override
        @SuppressWarnings("unchecked")
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            return (T) new TracksViewModel(repository, player);
        }
    }
}

