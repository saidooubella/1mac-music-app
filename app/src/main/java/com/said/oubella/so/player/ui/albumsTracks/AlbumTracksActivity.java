package com.said.oubella.so.player.ui.albumsTracks;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.transition.TransitionManager;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.said.oubella.so.player.AppContainer;
import com.said.oubella.so.player.R;
import com.said.oubella.so.player.SOPlayerApp;
import com.said.oubella.so.player.adapters.AlbumTracksAdapter;
import com.said.oubella.so.player.databinding.ActivityAlbumTracksBinding;
import com.said.oubella.so.player.helpers.ItemDecorator;
import com.said.oubella.so.player.models.Track;
import com.said.oubella.so.player.ui.details.TrackDetailsActivity;
import com.said.oubella.so.player.viewModels.AlbumTracksViewModel;

import java.util.Objects;

public final class AlbumTracksActivity extends AppCompatActivity {

    private static final String ALBUM_ID_KEY = "albumId";

    private ActivityAlbumTracksBinding binding;
    private AlbumTracksViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAlbumTracksBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        final long albumId = getIntent().getLongExtra(ALBUM_ID_KEY, 0);

        setSupportActionBar(binding.toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(null);
        binding.toolbar.setNavigationOnClickListener(view -> onBackPressed());

        final AppContainer container = ((SOPlayerApp) getApplication()).container();
        final AlbumTracksViewModel.Factory factory = new AlbumTracksViewModel.Factory(container.repository(), container.musicPlayer(), albumId);
        viewModel = new ViewModelProvider(this, factory).get(AlbumTracksViewModel.class);

        final AlbumTracksAdapter adapter = new AlbumTracksAdapter(new AlbumTracksAdapter.OnMoreClickListener() {
            @Override
            public void onItemClick(Track item, int position) {
                viewModel.getPlayer().play(item);
            }

            @Override
            public void onTrackDescriptionClick(Track item) {
                TrackDetailsActivity.startActivity(AlbumTracksActivity.this, item.trackId());
            }
        });

        binding.tracksList.addItemDecoration(new ItemDecorator(32, ItemDecorator.VERTICAL));
        binding.tracksList.setAdapter(adapter);

        viewModel.getTracks().observe(this, list -> {
            adapter.submitList(list.isEmpty() ? null : list);
            animateVisibility(binding.tracksList, !list.isEmpty());
        });

        viewModel.getAlbum().observe(this, track -> {

            binding.albumArtist.setText(track != null ? track.artist() : getString(R.string.no_longer_available));
            binding.albumTitle.setText(track != null ? track.title() : getString(R.string.no_longer_available));

            if (track != null) {
                Glide.with(this).load(track.art()).placeholder(R.drawable.ic_track)
                        .error(R.drawable.ic_track).into(binding.albumArt);
            } else {
                binding.albumArt.setImageResource(R.drawable.ic_track);
            }
        });
    }

    private void animateVisibility(@NonNull View view, boolean isVisible) {
        int visibility = isVisible ? View.VISIBLE : View.GONE;
        if (view.getVisibility() == visibility) return;
        final ViewGroup rootViewGroup = findViewById(R.id.rootView);
        TransitionManager.beginDelayedTransition(rootViewGroup);
        view.setVisibility(visibility);
    }

    public static void startActivity(@NonNull Activity activity, long albumId) {
        activity.startActivity(new Intent(activity, AlbumTracksActivity.class)
                .putExtra(ALBUM_ID_KEY, albumId));
    }
}
