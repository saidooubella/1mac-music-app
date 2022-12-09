package com.said.oubella.so.player.player;

import android.animation.ObjectAnimator;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.widget.SeekBar;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.said.oubella.so.player.AppExecutors;
import com.said.oubella.so.player.data.DataRepository;
import com.said.oubella.so.player.helpers.Helper;
import com.said.oubella.so.player.helpers.MediaPlayerExt;
import com.said.oubella.so.player.helpers.ResultCallback;
import com.said.oubella.so.player.helpers.Task;
import com.said.oubella.so.player.models.Track;

import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

@SuppressWarnings("ConstantConditions")
public final class MusicPlayerImpl implements MusicPlayer, MediaPlayer.OnCompletionListener {

    private static final long PROGRESS_UPDATE_DELAY = 800;

    private final AudioAttributes audioAttributes = new AudioAttributes.Builder()
            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
            .setUsage(AudioAttributes.USAGE_MEDIA).build();

    private final MutableLiveData<MusicRepeatMode> repeatMode = new MutableLiveData<>(MusicRepeatMode.REPEAT_ALL);
    private final MutableLiveData<Boolean> randomMode = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> isPlaying = new MutableLiveData<>(false);
    private final MutableLiveData<Integer> progress = new MutableLiveData<>(0);
    private final MutableLiveData<Track> currentTrack = new MutableLiveData<>();

    private final AtomicReference<List<Track>> tracksList;
    private final AtomicInteger currentIndex;

    private final DataRepository repository;
    private final AppExecutors executors;
    private final Application context;

    private final MediaPlayerExt mediaPlayer;
    private final AudioManager audioManager;
    private final Random random;

    private Task tracksTask;

    private final BroadcastReceiver noisyBroadcast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context p1, Intent p2) {
            if (isPlaying.getValue()) togglePlaying();
        }
    };

    private final AudioManager.OnAudioFocusChangeListener audioFocusChangeListener = state -> {
        switch (state) {
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                animateVolumeTo(0.5f);
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
            case AudioManager.AUDIOFOCUS_LOSS:
                if (isPlaying.getValue()) togglePlaying();
                break;
            case AudioManager.AUDIOFOCUS_GAIN:
                animateVolumeTo(1.0f);
                break;
        }
    };

    private final AudioFocusRequest focusRequest = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
            ? new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
            .setOnAudioFocusChangeListener(audioFocusChangeListener)
            .setAudioAttributes(audioAttributes)
            .build() : null;

    private final Runnable progressRunnable = new Runnable() {
        @Override
        public void run() {
            progress.setValue(mediaPlayer.getCurrentPosition());
            executors.mainHandler().postDelayed(this, PROGRESS_UPDATE_DELAY);
        }
    };

    private final ResultCallback<List<Track>> resultCallback = new ResultCallback<List<Track>>() {
        @Override
        public void onResult(final List<Track> data) {

            if (data.isEmpty()) {
                resetMusicPlayer();
                return;
            }

            Track track = currentTrack.getValue();
            int index = currentIndex.get();

            out:
            if (track != null && !data.isEmpty() && !tracksList.get().isEmpty() && notExists(track.uri())) {
                while (!tracksList.get().isEmpty()) {
                    index = balanceIndexFor(index, tracksList.get());
                    Track item = tracksList.get().get(index);
                    if (notExists(item.uri())) {
                        tracksList.get().remove(index);
                    } else {
                        track = item;
                        break out;
                    }
                }
                track = null;
            }

            tracksList.set(data);

            final boolean resetPlayer = currentTrack.getValue() == null || !currentTrack.getValue().equals(track);

            if (track != null && !tracksList.get().isEmpty()) {
                index = tracksList.get().indexOf(track);
                index = index == -1 ? 0 : index;
            } else {
                if (tracksList.get().isEmpty()) currentTrack.postValue(null);
                index = tracksList.get().isEmpty() ? -1 : 0;
            }

            if (index != -1) {
                if (resetPlayer) {
                    playAt(index, false);
                } else {
                    final Track value = tracksList.get().get(index);
                    currentTrack.postValue(value);
                }
            }
        }
    };

    public MusicPlayerImpl(Application context, DataRepository repository, AppExecutors executors) {

        this.tracksList = new AtomicReference<>(Helper.emptyList());
        this.currentIndex = new AtomicInteger(-1);
        this.audioManager = getAudioManager(context);
        this.mediaPlayer = new MediaPlayerExt();
        this.random = new Random();

        this.repository = repository;
        this.executors = executors;
        this.context = context;

        final IntentFilter filter = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
        context.registerReceiver(noisyBroadcast, filter);

        initializePlayer();
    }

    private void initializePlayer() {
        mediaPlayer.setAudioAttributes(audioAttributes);
        mediaPlayer.setOnCompletionListener(this);
        tracksTask = repository.getTracks(false, resultCallback);
    }

    private void playAt(int index, boolean forcePlay) {
        if (!tracksList.get().isEmpty() && 0 <= index && index < tracksList.get().size()) {

            stopProgressObserver();
            progress.postValue(0);
            seekProgressTo(0);

            if (index != currentIndex.get()) {
                final Track value = tracksList.get().get(index);
                currentTrack.postValue(value);
                currentIndex.set(index);
                initializeMediaPlayerSource(value.uri());
            }

            if (forcePlay || isPlaying.getValue()) {
                if (requestAudioFocus()) {
                    isPlaying.postValue(true);
                    startProgressObserving();
                    mediaPlayer.start();
                }
            }
        }
    }

    private void seekProgressTo(int progress) {
        if (!tracksList.get().isEmpty()) {
            mediaPlayer.seekTo(progress);
        }
    }

    @Override
    public void play(final Track item) {
        executors.defaultExecutor().execute(() -> playAt(tracksList.get().indexOf(item)));
    }

    @Override
    public void playAt(int index) {
        playAt(index, true);
    }

    @Override
    public void toggleRepeatMode() {
        repeatMode.setValue(repeatMode.getValue().toggle());
    }

    @Override
    public void toggleRandomMode() {
        randomMode.setValue(!randomMode.getValue());
    }

    @Override
    public void togglePlaying() {
        if (!tracksList.get().isEmpty()) {
            if (!mediaPlayer.isPlaying()) {
                if (requestAudioFocus()) {
                    isPlaying.setValue(true);
                    startProgressObserving();
                    mediaPlayer.start();
                }
            } else {
                isPlaying.setValue(false);
                stopProgressObserver();
                mediaPlayer.pause();
            }
        }
    }

    @Override
    public void configProgressController(SeekBar progress) {
        progress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar p1, int p2, boolean p3) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar p1) {
                stopProgressObserver();
            }

            @Override
            public void onStopTrackingTouch(SeekBar p1) {
                seekProgressTo(p1.getProgress() * 1000);
                startProgressObserving();
            }
        });
    }

    @Override
    public void onCompletion(MediaPlayer p1) {
        switch (repeatMode.getValue()) {
            case REPEAT_OFF:
                nextUntilEnd();
                break;
            case REPEAT_ALL:
                nextTrack();
                break;
            case REPEAT_ONCE:
                nextOnce();
                break;
        }
    }

    @Override
    public void prevTrack() {
        if (!tracksList.get().isEmpty()) {
            if (randomMode.getValue()) {
                playAt(randomIndex(), false);
            } else {
                int index = currentIndex.get() - 1;
                if (index < 0) index = tracksList.get().size() - 1;
                playAt(index, false);
            }
        }
    }

    @Override
    public void nextTrack() {
        if (!tracksList.get().isEmpty()) {
            if (randomMode.getValue()) {
                playAt(randomIndex(), false);
            } else {
                int index = currentIndex.get() + 1;
                if (index >= tracksList.get().size()) index = 0;
                playAt(index, false);
            }
        }
    }

    @Override
    public LiveData<Boolean> isPlaying() {
        return isPlaying;
    }

    @Override
    public LiveData<Track> currentTrack() {
        return currentTrack;
    }

    @Override
    public LiveData<MusicRepeatMode> repeatMode() {
        return repeatMode;
    }

    @Override
    public LiveData<Integer> progress() {
        return progress;
    }

    @Override
    public LiveData<Boolean> randomMode() {
        return randomMode;
    }

    @Override
    protected void finalize() {
        abandonAudioFocus();
        if (tracksTask != null) tracksTask.cancel();
        context.unregisterReceiver(noisyBroadcast);
        mediaPlayer.release();
    }

    private void abandonAudioFocus() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            audioManager.abandonAudioFocusRequest(focusRequest);
        } else {
            audioManager.abandonAudioFocus(audioFocusChangeListener);
        }
    }

    @SuppressWarnings("deprecation")
    private boolean requestAudioFocus() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return audioManager.requestAudioFocus(
                    focusRequest
            ) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
        } else {
            return audioManager.requestAudioFocus(
                    audioFocusChangeListener,
                    AudioManager.STREAM_MUSIC,
                    AudioManager.AUDIOFOCUS_GAIN
            ) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
        }
    }

    private void initializeMediaPlayerSource(Uri source) {
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(context, source);
            mediaPlayer.prepare();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startProgressObserving() {
        executors.mainHandler().post(progressRunnable);
    }

    private void stopProgressObserver() {
        executors.mainHandler().removeCallbacks(progressRunnable);
    }

    private void nextUntilEnd() {
        if (randomMode.getValue()) {
            playAt(randomIndex(), false);
        } else {
            int index = currentIndex.get() + 1;
            if (index >= tracksList.get().size()) {
                isPlaying.setValue(false);
                stopProgressObserver();
                index = 0;
            }
            playAt(index, false);
        }
    }

    private void nextOnce() {
        stopProgressObserver();
        seekProgressTo(0);
        startProgressObserving();
        mediaPlayer.start();
    }

    private int randomIndex() {
        return random.nextInt(tracksList.get().size());
    }

    private void resetMusicPlayer() {
        tracksList.set(Helper.emptyList());
        isPlaying.postValue(false);
        currentTrack.postValue(null);
        progress.postValue(0);
        stopProgressObserver();
        currentIndex.set(-1);
        mediaPlayer.reset();
    }

    private void animateVolumeTo(float target) {
        ObjectAnimator.ofFloat(mediaPlayer, MediaPlayerExt.VOLUME, target).start();
    }

    private boolean notExists(Uri uri) {
        try {
            ContentResolver resolver = context.getContentResolver();
            resolver.openFileDescriptor(uri, "r").close();
            return false;
        } catch (IOException e) {
            return true;
        }
    }

    private static AudioManager getAudioManager(Context context) {
        return (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
    }

    private static int balanceIndexFor(int index, List<Track> list) {
        return index >= list.size() ? list.size() - 1 : index;
    }
}
