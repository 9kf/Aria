package com.example.ksfgh.aria.Adapters;

import android.databinding.DataBindingUtil;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableList;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ksfgh.aria.Model.CustomSongModelForPlaylist;
import com.example.ksfgh.aria.Model.SongModel;
import com.example.ksfgh.aria.R;
import com.example.ksfgh.aria.Singleton;
import com.example.ksfgh.aria.ViewModel.BandActivityViewModel;
import com.example.ksfgh.aria.databinding.AlbumSongsBinding;

/**
 * Created by ksfgh on 27/02/2018.
 */

public class AlbumSongsAdapter extends RecyclerView.Adapter<AlbumSongsAdapter.ViewHolder> {

    private ObservableArrayList<CustomSongModelForPlaylist> albumSongs;
    private BandActivityViewModel viewModel;

    public AlbumSongsAdapter(BandActivityViewModel viewModel) {
        this.viewModel = viewModel;
        albumSongs = viewModel.selectedAlbumSongs;
        albumSongs.addOnListChangedCallback(new ObservableList.OnListChangedCallback<ObservableList<SongModel>>() {
            @Override
            public void onChanged(ObservableList<SongModel> songModels) {
                notifyDataSetChanged();
            }

            @Override
            public void onItemRangeChanged(ObservableList<SongModel> songModels, int i, int i1) {
                notifyItemRangeChanged(i,i1);
            }

            @Override
            public void onItemRangeInserted(ObservableList<SongModel> songModels, int i, int i1) {
                notifyItemRangeInserted(i,i1);
            }

            @Override
            public void onItemRangeMoved(ObservableList<SongModel> songModels, int i, int i1, int i2) {
                notifyDataSetChanged();
            }

            @Override
            public void onItemRangeRemoved(ObservableList<SongModel> songModels, int i, int i1) {
                notifyItemRangeRemoved(i, i1);
            }
        });
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_album_songs,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        CustomSongModelForPlaylist model = albumSongs.get(position);
        holder.binding.setModel(model);
        holder.binding.setViewmodel(viewModel);

        if(Singleton.homeScreen.currentAlbumPlaying != null){
            if(model.getSong().songId == Singleton.getInstance().song.getSong().songId && Singleton.homeScreen.currentAlbumPlaying.getAlbumId() == model.getAlbum().getAlbumId()){
                viewModel.currentTextView = holder.binding.tvAlbumSongTitle;
                viewModel.currentView = holder.binding.rlAlbumSong;

                viewModel.currentView.setBackgroundColor(Color.parseColor("#000000"));
                viewModel.currentTextView.setTextColor(Color.parseColor("#E57C1F"));
            }
        }
    }

    @Override
    public int getItemCount() {
        return albumSongs.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private AlbumSongsBinding binding;

        public ViewHolder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }
    }
}
