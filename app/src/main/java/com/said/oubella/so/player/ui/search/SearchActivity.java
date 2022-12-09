package com.said.oubella.so.player.ui.search;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.said.oubella.so.player.AppContainer;
import com.said.oubella.so.player.SOPlayerApp;
import com.said.oubella.so.player.adapters.TracksAdapter;
import com.said.oubella.so.player.databinding.ActivitySearchBinding;
import com.said.oubella.so.player.helpers.ItemDecorator;
import com.said.oubella.so.player.models.Track;
import com.said.oubella.so.player.state.SearchScreenState;
import com.said.oubella.so.player.ui.details.TrackDetailsActivity;
import com.said.oubella.so.player.viewModels.SearchViewModel;

import java.util.Objects;

public final class SearchActivity extends AppCompatActivity {

    private ActivitySearchBinding binding;
    private SearchViewModel viewModel;

    private final TextWatcher textChangedListener = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void afterTextChanged(Editable s) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            int visibility = s.length() == 0 ? View.GONE : View.VISIBLE;
            if (binding.clearTextBtn.getVisibility() != visibility) {
                binding.clearTextBtn.setVisibility(visibility);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        binding.toolbar.setNavigationOnClickListener(view -> onBackPressed());

        final AppContainer container = ((SOPlayerApp) getApplication()).container();
        final SearchViewModel.Factory factory = new SearchViewModel.Factory(container.repository(), container.musicPlayer());
        viewModel = new ViewModelProvider(this, factory).get(SearchViewModel.class);

        final TracksAdapter adapter = new TracksAdapter(new TracksAdapter.OnMoreClickListener() {

            @Override
            public void onItemClick(Track item, int position) {
                viewModel.getPlayer().play(item);
            }

            @Override
            public void onTrackDescriptionClick(Track item) {
                TrackDetailsActivity.startActivity(SearchActivity.this, item.trackId());
            }
        }, Glide.with(this));

        binding.tracksList.addItemDecoration(new ItemDecorator(32, ItemDecorator.VERTICAL));
        binding.tracksList.setAdapter(adapter);

        viewModel.searchForTracks(binding.searchQueryInput);

        binding.clearTextBtn.setOnClickListener(view -> binding.searchQueryInput.setText(""));
        binding.searchQueryInput.addTextChangedListener(textChangedListener);

        viewModel.getState().observe(this, state -> {
            final boolean isNone = state instanceof SearchScreenState.Empty && Objects.requireNonNull(binding.searchQueryInput.getText()).length() == 0;
            final boolean isEmpty = state instanceof SearchScreenState.Empty && Objects.requireNonNull(binding.searchQueryInput.getText()).length() > 0;
            final boolean isResult = state instanceof SearchScreenState.Result;
            adapter.submitList(isResult ? ((SearchScreenState.Result) state).getValue() : null);
            binding.tracksList.setVisibility(isResult ? View.VISIBLE : View.GONE);
            binding.emptyGroup.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
            binding.hintGroup.setVisibility(isNone ? View.VISIBLE : View.GONE);
        });
    }

    @Override
    protected void onDestroy() {
        binding.searchQueryInput.removeTextChangedListener(textChangedListener);
        super.onDestroy();
    }
}
