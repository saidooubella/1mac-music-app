package com.said.oubella.so.player.data;

import android.content.ContentResolver;
import android.database.ContentObserver;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import com.said.oubella.so.player.AppExecutors;
import com.said.oubella.so.player.helpers.DataProvider;
import com.said.oubella.so.player.helpers.ResultCallback;
import com.said.oubella.so.player.helpers.Task;
import com.said.oubella.so.player.models.Album;
import com.said.oubella.so.player.models.Track;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public final class DataRepositoryImpl implements DataRepository {

    private final ContentResolver resolver;
    private final AppExecutors executors;
    private final DataSource dataSource;

    public DataRepositoryImpl(ContentResolver resolver, AppExecutors executors, DataSource dataSource) {
        this.resolver = resolver;
        this.executors = executors;
        this.dataSource = dataSource;
    }

    @Override
    public Task searchForTracks(final EditText textView, final boolean postOnMainThread, final ResultCallback<List<Track>> callback) {

        final AtomicReference<String> query = new AtomicReference<>("");
        final WeakReference<EditText> searchField = new WeakReference<>(textView);

        final Task searchTask = getAsync(() -> dataSource.searchForTracks(query.get()), postOnMainThread, callback);

        final TextWatcher textListener = new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence p1, int p2, int p3, int p4) {
            }

            @Override
            public void onTextChanged(CharSequence p1, int p2, int p3, int p4) {
            }

            @Override
            public void afterTextChanged(Editable p1) {
                query.set(p1.toString());
                searchTask.invalidate();
            }
        };

        if (searchField.get() != null) {
            searchField.get().addTextChangedListener(textListener);
        }

        return new Task() {

            @Override
            protected void onInvalidate() {
                searchTask.invalidate();
            }

            @Override
            protected void onCancel() {
                searchTask.cancel();
                if (searchField.get() != null) {
                    searchField.get().removeTextChangedListener(textListener);
                }
            }
        };
    }

    @Override
    public Task getTracks(final boolean postOnMainThread, final ResultCallback<List<Track>> callback) {
        return getAsync(dataSource::getTracks, postOnMainThread, callback);
    }

    @Override
    public Task getTrack(final long trackId, final boolean postOnMainThread, final ResultCallback<Track> callback) {
        return getAsync(() -> dataSource.getTrack(trackId), postOnMainThread, callback);
    }

    @Override
    public Task getAlbumTracks(final long albumId, final boolean postOnMainThread, final ResultCallback<List<Track>> callback) {
        return getAsync(() -> dataSource.getAlbumTracks(albumId), postOnMainThread, callback);
    }

    @Override
    public Task getAlbum(final long albumId, final boolean postOnMainThread, final ResultCallback<Album> callback) {
        return getAsync(() -> dataSource.getAlbum(albumId), postOnMainThread, callback);
    }

    @Override
    public Task getAlbums(boolean postOnMainThread, ResultCallback<List<Album>> callback) {
        return getAsync(dataSource::getAlbums, postOnMainThread, callback);
    }

    private <T> Task getAsync(final DataProvider<T> dataProvider, final boolean postOnMainThread, final ResultCallback<T> callback) {

        final AtomicReference<T> result = new AtomicReference<>();
        final AtomicBoolean isNotRunning = new AtomicBoolean();

        final Runnable resultRunnable = postOnMainThread ? () -> {
            if (isNotRunning.get()) return;
            callback.onResult(result.get());
        } : null;

        final Runnable taskRunnable = () -> {
            if (isNotRunning.get()) return;
            result.set(dataProvider.get());
            if (isNotRunning.get()) return;
            if (postOnMainThread) {
                executors.mainExecutor().execute(resultRunnable);
            } else {
                if (isNotRunning.get()) return;
                callback.onResult(result.get());
            }
        };

        executors.ioExecutor().execute(taskRunnable);

        final ContentObserver observer = new ContentObserver(executors.mainHandler()) {
            @Override
            public void onChange(boolean selfChange) {
                if (isNotRunning.get()) return;
                executors.ioExecutor().execute(taskRunnable);
            }
        };

        resolver.registerContentObserver(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, true, observer
        );

        return new Task() {

            @Override
            protected void onInvalidate() {
                if (isNotRunning.get()) return;
                executors.ioExecutor().execute(taskRunnable);
            }

            @Override
            public void onCancel() {
                isNotRunning.set(true);
                resolver.unregisterContentObserver(observer);
                result.set(null);
            }
        };
    }
}
