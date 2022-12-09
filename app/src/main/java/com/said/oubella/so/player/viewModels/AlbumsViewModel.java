package com.said.oubella.so.player.viewModels;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.said.oubella.so.player.data.DataRepository;
import com.said.oubella.so.player.helpers.Task;
import com.said.oubella.so.player.state.AlbumsScreenState;

public final class AlbumsViewModel extends ViewModel {

    private final MutableLiveData<AlbumsScreenState> state = new MutableLiveData<>(new AlbumsScreenState.None());

    private final DataRepository repository;

    private Task albumsTask;

    private AlbumsViewModel(DataRepository repository) {
        this.repository = repository;
        this.init();
    }

    private void init() {
        albumsTask = repository.getAlbums(true, value -> {
            if (value.isEmpty()) {
                state.setValue(new AlbumsScreenState.Empty());
            } else {
                state.setValue(new AlbumsScreenState.Result(value));
            }
        });
    }

    public LiveData<AlbumsScreenState> getState() {
        return state;
    }

    @Override
    protected void onCleared() {
        if (albumsTask != null)
            albumsTask.cancel();
    }

    public static final class Factory implements ViewModelProvider.Factory {

        private final DataRepository repository;

        public Factory(DataRepository repository) {
            this.repository = repository;
        }

        @NonNull
        @Override
        @SuppressWarnings("unchecked")
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            return (T) new AlbumsViewModel(repository);
        }
    }
}
