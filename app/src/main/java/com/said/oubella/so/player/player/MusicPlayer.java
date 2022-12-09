package com.said.oubella.so.player.player;

import android.widget.SeekBar;

import androidx.lifecycle.LiveData;

import com.said.oubella.so.player.models.Track;

public interface MusicPlayer {

    LiveData<MusicRepeatMode> repeatMode();

    LiveData<Track> currentTrack();

    LiveData<Boolean> isPlaying();

    LiveData<Boolean> randomMode();

    LiveData<Integer> progress();

    void configProgressController(SeekBar progress);

    void toggleRepeatMode();

    void toggleRandomMode();

    void playAt(int index);

    void play(Track item);

    void togglePlaying();

    void nextTrack();

    void prevTrack();
}
