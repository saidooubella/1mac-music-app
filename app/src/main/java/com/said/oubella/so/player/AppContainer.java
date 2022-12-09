package com.said.oubella.so.player;

import android.app.Application;

import androidx.annotation.UiThread;

import com.said.oubella.so.player.data.DataRepository;
import com.said.oubella.so.player.data.DataRepositoryImpl;
import com.said.oubella.so.player.data.DataSource;
import com.said.oubella.so.player.data.DataSourceImpl;
import com.said.oubella.so.player.player.MusicPlayer;
import com.said.oubella.so.player.player.MusicPlayerImpl;

public final class AppContainer {

    private Application context;

    private AppExecutors _executors;
    private DataRepository _repository;
    private DataSource _dataSource;
    private MusicPlayer _musicPlayer;

    AppContainer(Application context) {
        this.context = context;
    }

    private AppExecutors executors() {
        if (_executors == null) {
            _executors = new AppExecutors();
        }
        return _executors;
    }

    @UiThread
    private DataSource dataSource() {
        if (_dataSource == null) {
            _dataSource = new DataSourceImpl(context.getContentResolver());
        }
        return _dataSource;
    }

    @UiThread
    public MusicPlayer musicPlayer() {
        if (_musicPlayer == null) {
            _musicPlayer = new MusicPlayerImpl(context, repository(), executors());
        }
        return _musicPlayer;
    }

    @UiThread
    public DataRepository repository() {
        if (_repository == null) {
            _repository = new DataRepositoryImpl(context.getContentResolver(), executors(), dataSource());
        }
        return _repository;
    }
}
