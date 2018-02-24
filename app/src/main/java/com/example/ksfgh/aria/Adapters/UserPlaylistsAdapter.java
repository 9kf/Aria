package com.example.ksfgh.aria.Adapters;

import android.databinding.DataBindingUtil;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableList;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ksfgh.aria.Model.PlaylistModel;
import com.example.ksfgh.aria.R;
import com.example.ksfgh.aria.ViewModel.UserViewModel;
import com.example.ksfgh.aria.databinding.UserPlaylistBinding;

/**
 * Created by ksfgh on 25/02/2018.
 */

public class UserPlaylistsAdapter extends RecyclerView.Adapter<UserPlaylistsAdapter.ViewHolder> {

    private ObservableArrayList<PlaylistModel> playlistModels;
    private UserViewModel viewModel;

    public UserPlaylistsAdapter(UserViewModel viewModel) {
        this.viewModel = viewModel;
        this.playlistModels = viewModel.userPlaylists;

        playlistModels.addOnListChangedCallback(new ObservableList.OnListChangedCallback<ObservableList<PlaylistModel>>() {
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_playlist, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        PlaylistModel model = playlistModels.get(position);
        holder.binding.setModel(model);
        holder.binding.setViewmodel(viewModel);
    }

    @Override
    public int getItemCount() {
        return playlistModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private UserPlaylistBinding binding;

        public ViewHolder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }
    }
}
