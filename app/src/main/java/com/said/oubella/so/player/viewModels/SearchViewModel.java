package com.said.oubella.so.player.viewModels;

import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.said.oubella.so.player.data.DataRepository;
import com.said.oubella.so.player.helpers.Task;
import com.said.oubella.so.player.player.MusicPlayer;
import com.said.oubella.so.player.state.SearchScreenState;

public final class SearchViewModel extends ViewModel {

    private final MutableLiveData<SearchScreenState> state = new MutableLiveData<>(new SearchScreenState.None());

    private final DataRepository repository;
    private final MusicPlayer player;

    private Task searchTask;

    private SearchViewModel(DataRepository repository, MusicPlayer player) {
        this.repository = repository;
        this.player = player;
    }

    // I've dealt with the EditText reference to avoid leaks and crashes. No worries :D !
    public void searchForTracks(EditText searchField) {
        if (searchTask != null) searchTask.cancel();
        searchTask = repository.searchForTracks(searchField, true, value ->
                state.setValue(value.isEmpty() ? new SearchScreenState.Empty() : new SearchScreenState.Result(value))
        );
    }

    public LiveData<SearchScreenState> getState() {
        return state;
    }

    public MusicPlayer getPlayer() {
        return player;
    }

    @Override
    protected void onCleared() {
        if (searchTask != null) searchTask.cancel();
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
            return (T) new SearchViewModel(repository, player);
        }
    }
}
