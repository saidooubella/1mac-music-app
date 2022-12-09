package com.said.oubella.so.player.ui.home.albums;

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
import com.said.oubella.so.player.adapters.AlbumsAdapter;
import com.said.oubella.so.player.databinding.FragmentAlbumsBinding;
import com.said.oubella.so.player.helpers.ItemDecorator;
import com.said.oubella.so.player.state.AlbumsScreenState;
import com.said.oubella.so.player.ui.albumsTracks.AlbumTracksActivity;
import com.said.oubella.so.player.viewModels.AlbumsViewModel;

public final class AlbumsFragment extends Fragment {

    private FragmentAlbumsBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAlbumsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final AppContainer container = ((SOPlayerApp) requireActivity().getApplication()).container();
        final AlbumsViewModel.Factory factory = new AlbumsViewModel.Factory(container.repository());
        final AlbumsViewModel viewModel = new ViewModelProvider(this, factory).get(AlbumsViewModel.class);

        final AlbumsAdapter adapter = new AlbumsAdapter(album ->
                AlbumTracksActivity.startActivity(requireActivity(), album.id()
                ), Glide.with(this));

        binding.albumsList.addItemDecoration(new ItemDecorator(16, ItemDecorator.GRID));
        binding.albumsList.setAdapter(adapter);

        viewModel.getState().observe(getViewLifecycleOwner(), state -> {
            final boolean isResult = state instanceof AlbumsScreenState.Result;
            final boolean isEmpty = state instanceof AlbumsScreenState.Empty;
            adapter.submitList(isResult ? ((AlbumsScreenState.Result) state).getValue() : null);
            binding.albumsList.setVisibility(isResult ? View.VISIBLE : View.GONE);
            binding.emptyGroup.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        });
    }
}
