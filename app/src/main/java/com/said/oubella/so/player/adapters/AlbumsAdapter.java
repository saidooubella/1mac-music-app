package com.said.oubella.so.player.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.said.oubella.so.player.R;
import com.said.oubella.so.player.databinding.AlbumItemBinding;
import com.said.oubella.so.player.models.Album;

public final class AlbumsAdapter extends ListAdapter<Album, AlbumsAdapter.AlbumViewHolder> {

    private static final DiffUtil.ItemCallback<Album> ALBUM_DIFF_CALLBACK = new DiffUtil.ItemCallback<Album>() {

        @Override
        public boolean areItemsTheSame(@NonNull Album oldItem, @NonNull Album newItem) {
            return oldItem.id() == newItem.id();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Album oldItem, @NonNull Album newItem) {
            return oldItem.toString().equals(newItem.toString());
        }
    };

    private final OnAlbumClickListener listener;
    private final RequestManager requestManager;

    public AlbumsAdapter(OnAlbumClickListener listener, RequestManager requestManager) {
        super(ALBUM_DIFF_CALLBACK);
        this.listener = listener;
        this.requestManager = requestManager;
    }

    @NonNull
    @Override
    public AlbumViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {
        return new AlbumViewHolder(AlbumItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull AlbumViewHolder holder, int position) {
        holder.bind(getItem(position), listener, requestManager);
    }

    static final class AlbumViewHolder extends RecyclerView.ViewHolder {

        private final AlbumItemBinding binding;

        AlbumViewHolder(@NonNull AlbumItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(@NonNull Album album, @NonNull OnAlbumClickListener listener, @NonNull RequestManager requestManager) {

            final String subtitle = album.songsCount() == 1
                    ? getString(R.string.album_subtitle_singular, album.artist(), album.songsCount())
                    : getString(R.string.album_subtitle_plural, album.artist(), album.songsCount());

            binding.title.setText(album.title());
            binding.artist.setText(subtitle);

            requestManager.load(album.art()).placeholder(R.drawable.ic_track).error(R.drawable.ic_track).into(binding.art);

            itemView.setOnClickListener((view) -> listener.onClick(album));
        }

        @NonNull
        private String getString(int id, Object... args) {
            return itemView.getContext().getResources().getString(id, args);
        }
    }

    public interface OnAlbumClickListener {
        void onClick(Album album);
    }
}
