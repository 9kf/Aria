package com.example.ksfgh.aria.Adapters;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableList;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ksfgh.aria.Model.PlaylistModel;
import com.example.ksfgh.aria.R;
import com.example.ksfgh.aria.ViewModel.HomeViewModel;
import com.example.ksfgh.aria.databinding.FragmentHomeBinding;
import com.example.ksfgh.aria.databinding.PlaylistBinding;

import java.util.ArrayList;

/**
 * Created by ksfgh on 28/01/2018.
 */

public class HomePlaylistAdapter extends RecyclerView.Adapter<HomePlaylistAdapter.ViewHolder> {

    private ObservableArrayList<PlaylistModel> playlists;
    private HomeViewModel homeViewModel;

    public HomePlaylistAdapter(HomeViewModel homeViewModel) {
        this.playlists = homeViewModel.playlistModels;
        this.homeViewModel = homeViewModel;

        playlists.addOnListChangedCallback(new ObservableList.OnListChangedCallback<ObservableList<PlaylistModel>>() {
            @Override
            public void onChanged(ObservableList<PlaylistModel> playlistModels) {
                notifyDataSetChanged();
            }

            @Override
            public void onItemRangeChanged(ObservableList<PlaylistModel> playlistModels, int i, int i1) {
                notifyItemRangeChanged(i, i1);
            }

            @Override
            public void onItemRangeInserted(ObservableList<PlaylistModel> playlistModels, int i, int i1) {
                notifyItemRangeInserted(i, i1);
            }

            @Override
            public void onItemRangeMoved(ObservableList<PlaylistModel> playlistModels, int i, int i1, int i2) {
                notifyDataSetChanged();
            }

            @Override
            public void onItemRangeRemoved(ObservableList<PlaylistModel> playlistModels, int i, int i1) {
                notifyItemRangeRemoved(i, i1);
            }
        });
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_playlist_recommendation, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        PlaylistModel model = playlists.get(position);
        holder.playlistBinding.setModel(model);
        holder.playlistBinding.setViewmodel(homeViewModel);
    }

    @Override
    public int getItemCount() {
        return playlists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private PlaylistBinding playlistBinding;

        public ViewHolder(View itemView) {
            super(itemView);
            playlistBinding = DataBindingUtil.bind(itemView);
        }

    }
}
