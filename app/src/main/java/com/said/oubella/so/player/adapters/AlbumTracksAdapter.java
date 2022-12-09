package com.said.oubella.so.player.adapters;

import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.said.oubella.so.player.R;
import com.said.oubella.so.player.databinding.AlbumTrackItemBinding;
import com.said.oubella.so.player.models.Track;

public final class AlbumTracksAdapter extends ListAdapter<Track, AlbumTracksAdapter.TrackViewHolder> {

    private static final DiffUtil.ItemCallback<Track> TRACK_DIFF_CALLBACK = new DiffUtil.ItemCallback<Track>() {

        @Override
        public boolean areItemsTheSame(@NonNull Track oldItem, @NonNull Track newItem) {
            return oldItem.trackId() == newItem.trackId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Track oldItem, @NonNull Track newItem) {
            return oldItem.toString().equals(newItem.toString());
        }
    };

    private final OnMoreClickListener listener;

    public AlbumTracksAdapter(OnMoreClickListener listener) {
        super(TRACK_DIFF_CALLBACK);
        this.listener = listener;
    }

    @Override
    public TrackViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {
        return new TrackViewHolder(AlbumTrackItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull TrackViewHolder holder, int position) {
        holder.bind(getItem(position), listener);
    }

    static final class TrackViewHolder extends RecyclerView.ViewHolder {

        private final AlbumTrackItemBinding binding;

        TrackViewHolder(@NonNull AlbumTrackItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(@NonNull final Track track, @NonNull final OnMoreClickListener listener) {

            final PopupMenu popupMenu = new PopupMenu(itemView.getContext(), binding.popupBtn);
            popupMenu.getMenu().add(R.string.tracks_details);
            popupMenu.setOnMenuItemClickListener(item1 -> {
                listener.onTrackDescriptionClick(track);
                return true;
            });

            binding.duration.setText(DateUtils.formatElapsedTime(track.duration() / 1000));
            binding.artist.setText(track.artist());
            binding.title.setText(track.title());

            binding.popupBtn.setOnClickListener(p1 -> popupMenu.show());
            itemView.setOnClickListener(p1 -> listener.onItemClick(track, getAdapterPosition()));
        }
    }

    public interface OnMoreClickListener {
        void onItemClick(Track item, int position);

        void onTrackDescriptionClick(Track item);
    }
}
