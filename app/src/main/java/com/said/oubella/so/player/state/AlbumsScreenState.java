package com.said.oubella.so.player.state;

import com.said.oubella.so.player.models.Album;

import java.util.List;

public abstract class AlbumsScreenState {

    private AlbumsScreenState() {}

    public static final class Result extends AlbumsScreenState {

        private final List<Album> value;

        public Result(List<Album> value) {
            this.value = value;
        }

        public List<Album> getValue() {
            return value;
        }
    }

    public static final class Empty extends AlbumsScreenState {
        public Empty() { }
    }

    public static final class None extends AlbumsScreenState {
        public None() { }
    }
}
