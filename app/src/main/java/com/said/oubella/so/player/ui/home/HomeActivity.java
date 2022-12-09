package com.said.oubella.so.player.ui.home;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.said.oubella.so.player.AppContainer;
import com.said.oubella.so.player.R;
import com.said.oubella.so.player.SOPlayerApp;
import com.said.oubella.so.player.databinding.ActivityHomeBinding;
import com.said.oubella.so.player.player.MusicRepeatMode;
import com.said.oubella.so.player.ui.search.SearchActivity;
import com.said.oubella.so.player.viewModels.MainViewModel;

public final class HomeActivity extends AppCompatActivity {

    private static final String BOTTOM_TRANSLATION_Y_KEY = "bottomTranslationY";
    private static final String TOP_TRANSLATION_Y_KEY = "topTranslationY";
    private static final String SHEET_STATE_KEY = "expandingOffset";
    private static final String CORNER_RADIUS_KEY = "cornerRadius";

    private static final int CORNER_RADIUS = 16;

    private BottomSheetBehavior<FrameLayout> behavior;
    private ActivityHomeBinding binding;
    private MainViewModel viewModel;

    private float radius = CORNER_RADIUS;

    private BottomSheetBehavior.BottomSheetCallback bottomSheetCallback = new BottomSheetBehavior.BottomSheetCallback() {

        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {

        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {
            GradientDrawable drawable = (GradientDrawable) binding.playNowSheet.getRoot().getBackground();
            radius = CORNER_RADIUS - (CORNER_RADIUS * slideOffset);
            drawable.setCornerRadius(radius);
            binding.bottomNavigation.setTranslationY(binding.bottomNavigation.getHeight() * slideOffset);
            binding.appBarLayout.setTranslationY(-(binding.appBarLayout.getHeight() * slideOffset));
            binding.playNowSheet.peekGroup.setAlpha(1 - slideOffset);
            binding.playNowSheet.peekGroup.setVisibility(slideOffset == 1f ? View.GONE : View.VISIBLE);
            binding.playNowSheet.sheetGroup.setAlpha(slideOffset);
            binding.playNowSheet.sheetGroup.setVisibility(slideOffset == 0f ? View.GONE : View.VISIBLE);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        behavior = BottomSheetBehavior.from((FrameLayout) binding.playNowSheet.getRoot());
        behavior.addBottomSheetCallback(bottomSheetCallback);

        NavHostFragment fragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        if (fragment == null) return;

        NavigationUI.setupWithNavController(binding.bottomNavigation, fragment.getNavController());

        final AppContainer container = ((SOPlayerApp) getApplication()).container();
        final MainViewModel.Factory factory = new MainViewModel.Factory(container.musicPlayer());
        viewModel = new ViewModelProvider(this, factory).get(MainViewModel.class);

        if (savedInstanceState != null) {
            restorePreviousState(savedInstanceState);
        }

        initViewModelObservers();

        initViewsCallbacks();
    }

    private void restorePreviousState(@NonNull Bundle savedInstanceState) {

        final int state = savedInstanceState.getInt(SHEET_STATE_KEY);
        final float slideOffset = state == BottomSheetBehavior.STATE_EXPANDED ? 1f : 0f;
        radius = savedInstanceState.getFloat(CORNER_RADIUS_KEY);

        GradientDrawable drawable = (GradientDrawable) binding.playNowSheet.getRoot().getBackground();
        drawable.setCornerRadius(radius);

        binding.bottomNavigation.setTranslationY(savedInstanceState.getFloat(BOTTOM_TRANSLATION_Y_KEY));
        binding.appBarLayout.setTranslationY(savedInstanceState.getFloat(TOP_TRANSLATION_Y_KEY));

        binding.playNowSheet.peekGroup.setVisibility(slideOffset == 1f ? View.GONE : View.VISIBLE);
        binding.playNowSheet.sheetGroup.setVisibility(slideOffset == 0f ? View.GONE : View.VISIBLE);
        binding.playNowSheet.peekGroup.setAlpha(1 - slideOffset);
        binding.playNowSheet.sheetGroup.setAlpha(slideOffset);
        behavior.setState(state);
    }

    private void initViewsCallbacks() {

        binding.playNowSheet.sheetPlayPauseBtn.setOnClickListener(view -> viewModel.getPlayer().togglePlaying());

        binding.playNowSheet.peekPlayPauseBtn.setOnClickListener(view -> viewModel.getPlayer().togglePlaying());

        binding.playNowSheet.sheetRepeatBtn.setOnClickListener(view -> viewModel.getPlayer().toggleRepeatMode());

        binding.playNowSheet.sheetRandomBtn.setOnClickListener(view -> viewModel.getPlayer().toggleRandomMode());

        binding.playNowSheet.sheetNextBtn.setOnClickListener(view -> viewModel.getPlayer().nextTrack());

        binding.playNowSheet.sheetPrevBtn.setOnClickListener(view -> viewModel.getPlayer().prevTrack());

        binding.playNowSheet.peekGroup.setOnClickListener(view -> {
            if (behavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });
    }

    private void initViewModelObservers() {

        viewModel.getPlayer().currentTrack().observe(this, value -> {

            binding.playNowSheet.sheetArtist.setText(value != null ? value.artist() : getString(R.string.no_artist));
            binding.playNowSheet.sheetTitle.setText(value != null ? value.title() : getString(R.string.no_queued_tracks));
            binding.playNowSheet.peekArtist.setText(value != null ? value.artist() : getString(R.string.no_artist));
            binding.playNowSheet.peekTitle.setText(value != null ? value.title() : getString(R.string.no_queued_tracks));
            binding.playNowSheet.sheetProgress.setMax(value != null ? value.duration() / 1000 : 0);
            binding.playNowSheet.sheetDurationText.setText(value == null ? getString(R.string.zero_timing)
                    : DateUtils.formatElapsedTime(value.duration() / 1000));

            if (value != null) {
                final RequestBuilder<Drawable> requestBuilder = Glide.with(this)
                        .load(value.art()).placeholder(R.drawable.ic_track).error(R.drawable.ic_track);
                requestBuilder.into(binding.playNowSheet.sheetArt);
                requestBuilder.into(binding.playNowSheet.peekArt);
            } else {
                binding.playNowSheet.sheetProgressText.setText(R.string.zero_timing);
                binding.playNowSheet.sheetArt.setImageResource(R.drawable.ic_track);
                binding.playNowSheet.peekArt.setImageResource(R.drawable.ic_track);
            }
        });

        viewModel.getPlayer().progress().observe(this, value -> {
            final int duration = value / 1000;
            binding.playNowSheet.sheetProgressText.setText(DateUtils.formatElapsedTime(duration));
            binding.playNowSheet.sheetProgress.setProgress(duration);
        });

        viewModel.getPlayer().isPlaying().observe(this, value -> {
            final int imageRes = value ? R.drawable.ic_pause : R.drawable.ic_play;
            binding.playNowSheet.peekPlayPauseBtn.setImageResource(imageRes);
            binding.playNowSheet.sheetPlayPauseBtn.setImageResource(imageRes);
        });

        viewModel.getPlayer().randomMode().observe(this, value ->
                binding.playNowSheet.sheetRandomBtn.setImageResource(value
                        ? R.drawable.ic_shuffle
                        : R.drawable.ic_shuffle_off));

        viewModel.getPlayer().repeatMode().observe(this, value ->
                binding.playNowSheet.sheetRepeatBtn.setImageResource(value == MusicRepeatMode.REPEAT_ALL
                        ? R.drawable.ic_repeat : value == MusicRepeatMode.REPEAT_ONCE
                        ? R.drawable.ic_repeat_once : R.drawable.ic_repeat_off));

        viewModel.getPlayer().configProgressController(binding.playNowSheet.sheetProgress);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putFloat(CORNER_RADIUS_KEY, radius);
        outState.putFloat(BOTTOM_TRANSLATION_Y_KEY, binding.bottomNavigation.getTranslationY());
        outState.putFloat(TOP_TRANSLATION_Y_KEY, binding.appBarLayout.getTranslationY());
        outState.putInt(SHEET_STATE_KEY, behavior.getState());
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_toolbar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.search_item) {
            startActivity(new Intent(this, SearchActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (behavior.getState() != BottomSheetBehavior.STATE_COLLAPSED) {
            behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        behavior.removeBottomSheetCallback(bottomSheetCallback);
        super.onDestroy();
    }
}
