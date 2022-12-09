package com.said.oubella.so.player.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.said.oubella.so.player.R;
import com.said.oubella.so.player.databinding.TrackItemBinding;
import com.said.oubella.so.player.models.Track;

public final class TracksAdapter extends ListAdapter<Track, TracksAdapter.TrackViewHolder> {

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
	private final RequestManager requestManager;

	public TracksAdapter(OnMoreClickListener listener, RequestManager requestManager) {
		super(TRACK_DIFF_CALLBACK);
		this.listener = listener;
		this.requestManager = requestManager;
	}
	
	@Override
	public TrackViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {
		return new TrackViewHolder(TrackItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
	}

	@Override
	public void onBindViewHolder(@NonNull TrackViewHolder holder, int position) {
		holder.bind(getItem(position), listener, requestManager);
	}

	static final class TrackViewHolder extends RecyclerView.ViewHolder {

		private final TrackItemBinding binding;

		TrackViewHolder(@NonNull TrackItemBinding binding) {
			super(binding.getRoot());
			this.binding = binding;
		}

		void bind(@NonNull final Track track, @NonNull final OnMoreClickListener listener, @NonNull final RequestManager requestManager) {

			final PopupMenu popupMenu = new PopupMenu(itemView.getContext(), binding.popupBtn);
			popupMenu.getMenu().add(R.string.tracks_details);
			popupMenu.setOnMenuItemClickListener(item1 -> {
				listener.onTrackDescriptionClick(track);
				return true;
			});

			binding.artist.setText(track.artist());
			binding.title.setText(track.title());

			binding.popupBtn.setOnClickListener(p1 -> popupMenu.show());
			itemView.setOnClickListener(p1 -> listener.onItemClick(track, getAdapterPosition()));
			requestManager.load(track.art()).placeholder(R.drawable.ic_track).error(R.drawable.ic_track).into(binding.art);
		}
	}
	
	public interface OnMoreClickListener {
		void onItemClick(Track item, int position);
		void onTrackDescriptionClick(Track item);
	}
}
