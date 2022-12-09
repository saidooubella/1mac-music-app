package com.said.oubella.so.player.viewModels;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.said.oubella.so.player.player.MusicPlayer;

public final class MainViewModel extends ViewModel {

    private final MusicPlayer player;

    private MainViewModel(MusicPlayer player) {
        this.player = player;
    }

    public MusicPlayer getPlayer() {
        return player;
    }

    public static final class Factory implements ViewModelProvider.Factory {

        private final MusicPlayer player;

        public Factory(MusicPlayer player) {
            this.player = player;
        }

        @NonNull
        @Override
        @SuppressWarnings("unchecked")
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            return (T) new MainViewModel(player);
        }
    }
}
