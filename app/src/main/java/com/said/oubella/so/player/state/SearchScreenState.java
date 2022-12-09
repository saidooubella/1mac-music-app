package com.said.oubella.so.player.state;

import com.said.oubella.so.player.models.Track;

import java.util.List;

public abstract class SearchScreenState {

    private SearchScreenState() {}

    public static final class Result extends SearchScreenState {

        private final List<Track> value;

        public Result(List<Track> value) {
            this.value = value;
        }

        public List<Track> getValue() {
            return value;
        }
    }

    public static final class Empty extends SearchScreenState {
        public Empty() { }
    }

    public static final class None extends SearchScreenState {
        public None() { }
    }
}
