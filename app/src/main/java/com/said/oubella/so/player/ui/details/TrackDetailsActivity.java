package com.said.oubella.so.player.ui.details;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateUtils;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.said.oubella.so.player.AppContainer;
import com.said.oubella.so.player.R;
import com.said.oubella.so.player.SOPlayerApp;
import com.said.oubella.so.player.databinding.ActivityTrackDetailsBinding;
import com.said.oubella.so.player.viewModels.TrackDetailViewModel;

import java.util.Objects;

public final class TrackDetailsActivity extends AppCompatActivity {

    private static final String TRACK_ID_KEY = "trackId";

    private ActivityTrackDetailsBinding binding;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTrackDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        final long trackId = getIntent().getLongExtra(TRACK_ID_KEY, -1);

        setSupportActionBar(binding.toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        binding.toolbar.setNavigationOnClickListener((v) -> onBackPressed());

        AppContainer container = ((SOPlayerApp) getApplication()).container();
        TrackDetailViewModel.Factory factory = new TrackDetailViewModel.Factory(container.repository(), trackId);
        TrackDetailViewModel viewModel = new ViewModelProvider(this, factory).get(TrackDetailViewModel.class);

        viewModel.getTrack().observe(this, track -> {

            binding.trackDuration.setText(track != null ? DateUtils.formatElapsedTime(track.duration() / 1000) : getString(R.string.no_longer_available));
            binding.trackArtist.setText(track != null ? track.artist() : getString(R.string.no_longer_available));
            binding.trackAlbum.setText(track != null ? track.album() : getString(R.string.no_longer_available));
            binding.trackTitle.setText(track != null ? track.title() : getString(R.string.no_longer_available));
            binding.trackSize.setText(track != null ? getString(R.string.track_size, track.size() / ((float) (1024 * 1024))) : getString(R.string.no_longer_available));

            if (track != null) {
                Glide.with(this).load(track.art()).placeholder(R.drawable.ic_track)
                        .error(R.drawable.ic_track).into(binding.trackArt);
            } else {
                binding.trackArt.setImageResource(R.drawable.ic_track);
            }
        });
    }

    public static void startActivity(@NonNull Activity activity, long trackId) {
        activity.startActivity(new Intent(activity, TrackDetailsActivity.class)
                .putExtra(TRACK_ID_KEY, trackId));
    }
}
