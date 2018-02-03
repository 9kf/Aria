package com.example.ksfgh.aria.Adapters;

import android.databinding.DataBindingUtil;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableList;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ksfgh.aria.Model.CustomSongModelForPlaylist;
import com.example.ksfgh.aria.Model.SongModel;
import com.example.ksfgh.aria.R;
import com.example.ksfgh.aria.ViewModel.PlaylistActivityViewModel;
import com.example.ksfgh.aria.databinding.PlaylistSongsBinding;

/**
 * Created by ksfgh on 30/01/2018.
 */

public class PlaylistSongsAdapter extends RecyclerView.Adapter<PlaylistSongsAdapter.ViewHolder> {

    private ObservableArrayList<CustomSongModelForPlaylist> songs;
    private PlaylistActivityViewModel playlistActivityViewModel;

    public PlaylistSongsAdapter(ObservableArrayList<CustomSongModelForPlaylist> songs, PlaylistActivityViewModel playlistActivityViewModel) {
        this.songs = songs;
        this.playlistActivityViewModel = playlistActivityViewModel;
        songs.addOnListChangedCallback(new ObservableList.OnListChangedCallback<ObservableList<CustomSongModelForPlaylist>>() {
            @Override
            public void onChanged(ObservableList<CustomSongModelForPlaylist> customSongModelForPlaylists) {
                notifyDataSetChanged();
            }

            @Override
            public void onItemRangeChanged(ObservableList<CustomSongModelForPlaylist> customSongModelForPlaylists, int i, int i1) {
                notifyItemRangeChanged(i, i1);
            }

            @Override
            public void onItemRangeInserted(ObservableList<CustomSongModelForPlaylist> customSongModelForPlaylists, int i, int i1) {
                notifyItemRangeInserted(i, i1);
            }

            @Override
            public void onItemRangeMoved(ObservableList<CustomSongModelForPlaylist> customSongModelForPlaylists, int i, int i1, int i2) {
                notifyDataSetChanged();
            }

            @Override
            public void onItemRangeRemoved(ObservableList<CustomSongModelForPlaylist> customSongModelForPlaylists, int i, int i1) {
                notifyItemRangeRemoved(i, i1);
            }
        });
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_playlist_songs, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        CustomSongModelForPlaylist song = songs.get(position);
        holder.binding.setModel(song);
        holder.binding.setViewmodel(playlistActivityViewModel);
        playlistActivityViewModel.addViews(holder.binding.rvWrapper);
    }

    @Override
    public int getItemCount() {
        return songs.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private PlaylistSongsBinding binding;

        public ViewHolder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }
    }
}
