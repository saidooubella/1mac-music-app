package com.said.oubella.so.player.data;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.said.oubella.so.player.data.columns.AlbumColumns;
import com.said.oubella.so.player.data.columns.TrackColumns;
import com.said.oubella.so.player.helpers.Helper;
import com.said.oubella.so.player.models.Album;
import com.said.oubella.so.player.models.Track;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@SuppressLint("InlinedApi")
public final class DataSourceImpl implements DataSource {

    private static final Uri MEDIA_CONTENT_URI = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
    private static final Uri ALBUMS_CONTENT_URI = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;

    private static final Uri ARTWORK_URI = Uri.parse("content://media/external/audio/albumart");

    private static final String TRACKS_SORT_ORDER = MediaStore.Audio.Media.TITLE + " ASC";

    private static final String[] TRACK_PROJECTION = {
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.ARTIST_ID,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.SIZE,
    };

    private static final String[] ALBUM_PROJECTION = {
            MediaStore.Audio.Albums._ID,
            MediaStore.Audio.Albums.ALBUM,
            MediaStore.Audio.Albums.ARTIST,
            MediaStore.Audio.Albums.NUMBER_OF_SONGS
    };

    private final ContentResolver resolver;

    public DataSourceImpl(ContentResolver resolver) {
        this.resolver = resolver;
    }

    @Override
    public List<Track> searchForTracks(final String query) {

        if (query == null || query.isEmpty()) return Helper.emptyList();

        final List<Track> tracksList = new ArrayList<>();
        final String selection = MediaStore.Audio.Media.IS_MUSIC + " == 1 and " + MediaStore.Audio.Media.TITLE + " like ?";
        final String[] selectionArgs = {query + "%"};

        try (final Cursor cursor = resolver.query(MEDIA_CONTENT_URI, TRACK_PROJECTION, selection, selectionArgs, TRACKS_SORT_ORDER)) {
            addTracks(cursor, tracksList);
        }

        return tracksList;
    }

    @Override
    public List<Track> getAlbumTracks(long albumId) {

        final List<Track> tracksList = new ArrayList<>();

        final String selection = MediaStore.Audio.Media.IS_MUSIC + " == 1 and " + MediaStore.Audio.Media.ALBUM_ID + " == ?";
        final String[] selectionArgs = {String.valueOf(albumId)};

        try (final Cursor cursor = resolver.query(MEDIA_CONTENT_URI, TRACK_PROJECTION, selection, selectionArgs, TRACKS_SORT_ORDER)) {
            addTracks(cursor, tracksList);
        }

        return tracksList;
    }

    @Override
    public List<Track> getTracks() {

        final List<Track> tracksList = new ArrayList<>();
        final String selection = MediaStore.Audio.Media.IS_MUSIC + " == 1";

        try (final Cursor cursor = resolver.query(MEDIA_CONTENT_URI, TRACK_PROJECTION, selection, null, TRACKS_SORT_ORDER)) {
            addTracks(cursor, tracksList);
        }

        return tracksList;
    }

    @Override
    public List<Album> getAlbums() {

        final List<Album> albumsList = new ArrayList<>();
        final String sortOrder = MediaStore.Audio.Albums.ALBUM + " ASC";

        try (final Cursor cursor = resolver.query(ALBUMS_CONTENT_URI, ALBUM_PROJECTION, null, null, sortOrder)) {

            final AlbumColumns columns = new AlbumColumns(Objects.requireNonNull(cursor));

            while (cursor.moveToNext()) {
                albumsList.add(generateAlbum(cursor, columns));
            }
        }

        return albumsList;
    }

    @Override
    public Track getTrack(final long trackId) {

        final String selection = MediaStore.Audio.Media._ID + " == ?";
        final String[] selectionArgs = {String.valueOf(trackId)};

        try (final Cursor cursor = resolver.query(MEDIA_CONTENT_URI, TRACK_PROJECTION, selection, selectionArgs, null)) {

            final TrackColumns columns = new TrackColumns(Objects.requireNonNull(cursor));

            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                return generateTrack(cursor, columns);
            }
        }

        return null;
    }

    @Override
    public Album getAlbum(long albumId) {

        final String selection = MediaStore.Audio.Albums._ID + " == ?";
        final String[] selectionArgs = {String.valueOf(albumId)};

        try (final Cursor cursor = resolver.query(ALBUMS_CONTENT_URI, ALBUM_PROJECTION, selection, selectionArgs, null)) {

            final AlbumColumns columns = new AlbumColumns(Objects.requireNonNull(cursor));

            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                return generateAlbum(cursor, columns);
            }
        }

        return null;
    }

    private static void addTracks(final Cursor cursor, final List<Track> tracksList) {

        final TrackColumns columns = new TrackColumns(cursor);

        while (cursor.moveToNext()) {
            tracksList.add(generateTrack(cursor, columns));
        }
    }

    private static Album generateAlbum(final Cursor cursor, final AlbumColumns columns) {

        final long id = cursor.getLong(columns.idColumn());
        final String title = cursor.getString(columns.titleColumn());
        final String artist = cursor.getString(columns.artistColumn());
        final int count = cursor.getInt(columns.songsCountColumn());

        final Uri art = ContentUris.withAppendedId(ARTWORK_URI, id);

        return new Album(id, art, title, artist, count);
    }

    private static Track generateTrack(final Cursor cursor, final TrackColumns columns) {

        final long trackId = cursor.getLong(columns.idColumn());
        final long albumId = cursor.getLong(columns.albumIdColumn());
        final long artistId = cursor.getLong(columns.artistIdColumn());

        final String title = cursor.getString(columns.titleColumn());
        final String album = cursor.getString(columns.albumColumn());
        final String artist = cursor.getString(columns.artistColumn());

        final int duration = cursor.getInt(columns.durationColumn());
        final int size = cursor.getInt(columns.sizeColumn());

        final Uri uri = ContentUris.withAppendedId(MEDIA_CONTENT_URI, trackId);
        final Uri art = ContentUris.withAppendedId(ARTWORK_URI, albumId);

        return new Track(trackId, title, artistId, artist, albumId, album, uri, art, duration, size);
    }
}
