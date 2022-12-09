package com.said.oubella.so.player.player;

public enum MusicRepeatMode {

    REPEAT_ALL, REPEAT_OFF, REPEAT_ONCE;

    public MusicRepeatMode toggle() {
        return this == REPEAT_OFF
                ? REPEAT_ALL : this == REPEAT_ALL
                ? REPEAT_ONCE : REPEAT_OFF;
    }
}
