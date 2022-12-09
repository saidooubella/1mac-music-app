package com.said.oubella.so.player.ui.home.tracks;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.said.oubella.so.player.AppContainer;
import com.said.oubella.so.player.SOPlayerApp;
import com.said.oubella.so.player.adapters.TracksAdapter;
import com.said.oubella.so.player.databinding.FragmentTracksBinding;
import com.said.oubella.so.player.helpers.ItemDecorator;
import com.said.oubella.so.player.models.Track;
import com.said.oubella.so.player.state.TracksScreenState;
import com.said.oubella.so.player.ui.details.TrackDetailsActivity;
import com.said.oubella.so.player.viewModels.TracksViewModel;

public final class TracksFragment extends Fragment {

    private FragmentTracksBinding binding;
    private TracksViewModel viewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentTracksBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        AppContainer container = ((SOPlayerApp) requireActivity().getApplication()).container();
        TracksViewModel.Factory factory = new TracksViewModel.Factory(container.repository(), container.musicPlayer());
        viewModel = new ViewModelProvider(this, factory).get(TracksViewModel.class);

        final TracksAdapter adapter = new TracksAdapter(new TracksAdapter.OnMoreClickListener() {

            @Override
            public void onItemClick(Track item, int position) {
                viewModel.getPlayer().playAt(position);
            }

            @Override
            public void onTrackDescriptionClick(Track item) {
                TrackDetailsActivity.startActivity(requireActivity(), item.trackId());
            }
        }, Glide.with(this));

        binding.tracksList.addItemDecoration(new ItemDecorator(32, ItemDecorator.VERTICAL));
        binding.tracksList.setAdapter(adapter);

        viewModel.getState().observe(getViewLifecycleOwner(), (state) -> {
            final boolean isResult = state instanceof TracksScreenState.Result;
            final boolean isEmpty = state instanceof TracksScreenState.Empty;
            adapter.submitList(isResult ? ((TracksScreenState.Result) state).getValue() : null);
            binding.tracksList.setVisibility(isResult ? View.VISIBLE : View.GONE);
            binding.emptyGroup.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        });
    }
}
