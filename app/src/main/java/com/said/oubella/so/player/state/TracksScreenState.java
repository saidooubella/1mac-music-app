package com.said.oubella.so.player.state;

import com.said.oubella.so.player.models.Track;

import java.util.List;

public abstract class TracksScreenState {

    private TracksScreenState() {}

    public static final class Result extends TracksScreenState {

        private final List<Track> value;

        public Result(List<Track> value) {
            this.value = value;
        }

        public List<Track> getValue() {
            return value;
        }
    }

    public static final class Empty extends TracksScreenState {
        public Empty() { }
    }

    public static final class None extends TracksScreenState {
        public None() { }
    }
}
