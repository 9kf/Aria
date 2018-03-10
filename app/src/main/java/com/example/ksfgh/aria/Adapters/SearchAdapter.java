package com.example.ksfgh.aria.Adapters;

import android.databinding.DataBindingUtil;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableList;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ksfgh.aria.Model.AlbumModel;
import com.example.ksfgh.aria.Model.BandModel;
import com.example.ksfgh.aria.Model.PlaylistModel;
import com.example.ksfgh.aria.Model.SongModel;
import com.example.ksfgh.aria.Model.UserModel;
import com.example.ksfgh.aria.Model.VideoModel;
import com.example.ksfgh.aria.R;
import com.example.ksfgh.aria.View.fragments.SearchDialogFragment;
import com.example.ksfgh.aria.databinding.ItemSearchAlbumBinding;
import com.example.ksfgh.aria.databinding.ItemSearchBandBinding;
import com.example.ksfgh.aria.databinding.ItemSearchPlaylistBinding;
import com.example.ksfgh.aria.databinding.ItemSearchSongBinding;
import com.example.ksfgh.aria.databinding.ItemSearchUserBinding;
import com.example.ksfgh.aria.databinding.ItemSearchVideoBinding;

import java.util.ArrayList;

/**
 * Created by ksfgh on 09/03/2018.
 */

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {

    private ObservableArrayList<Object> results;
    private SearchDialogFragment dialogFragment;

    public SearchAdapter(ObservableArrayList<Object> results, SearchDialogFragment dialogFragment) {
        this.results = results;
        this.dialogFragment = dialogFragment;
        results.addOnListChangedCallback(new ObservableList.OnListChangedCallback<ObservableList<Object>>() {
            @Override
            public void onChanged(ObservableList<Object> objects) {
                notifyDataSetChanged();
            }

            @Override
            public void onItemRangeChanged(ObservableList<Object> objects, int i, int i1) {
                notifyItemRangeChanged(i,i1);
            }

            @Override
            public void onItemRangeInserted(ObservableList<Object> objects, int i, int i1) {
                notifyItemRangeInserted(i,i1);
            }

            @Override
            public void onItemRangeMoved(ObservableList<Object> objects, int i, int i1, int i2) {
                notifyDataSetChanged();
            }

            @Override
            public void onItemRangeRemoved(ObservableList<Object> objects, int i, int i1) {
                notifyItemRangeRemoved(i,i1);
            }
        });
    }

    @Override
    public SearchAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        switch (viewType){
            case R.layout.item_search_band:
                return new BandResultViewHolder(view);

            case R.layout.item_search_user:
                return new UserResultViewHolder(view);

            case R.layout.item_search_playlist:
                return new PlaylistResultViewHolder(view);

            case R.layout.item_search_song:
                return new SongResultViewHolder(view);

            case R.layout.item_search_album:
                return new AlbumResultViewHolder(view);

            case R.layout.item_search_video:
                return new VideoResultViewHolder(view);

                default: return null;
        }

    }

    @Override
    public int getItemViewType(int position) {

        Object item = results.get(position);
        if(item instanceof BandModel){
            return R.layout.item_search_band;
        }
        else if(item instanceof UserModel){
            return R.layout.item_search_user;
        }
        else if(item instanceof PlaylistModel){
            return R.layout.item_search_playlist;
        }
        else if(item instanceof AlbumModel){
            return R.layout.item_search_album;
        }
        else if(item instanceof SongModel){
            return R.layout.item_search_song;
        }
        else if(item instanceof VideoModel){
            return R.layout.item_search_video;
        }
        return 0;

    }

    @Override
    public void onBindViewHolder(SearchAdapter.ViewHolder holder, int position) {
        Object object = results.get(position);
        holder.bind(object);
    }

    @Override
    public int getItemCount() {
        return results.size();
    }

    public abstract class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(View itemView) {
            super(itemView);
        }

        public abstract void bind(Object object);
    }

    private class BandResultViewHolder extends ViewHolder{

        private ItemSearchBandBinding binding;
        public BandResultViewHolder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }

        @Override
        public void bind(Object object) {
            binding.setModel((BandModel) object);
            binding.setFrag(dialogFragment);
        }

    }

    private class UserResultViewHolder extends ViewHolder{

        private ItemSearchUserBinding binding;
        public UserResultViewHolder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }

        @Override
        public void bind(Object object) {
            binding.setModel((UserModel) object);
            binding.setFrag(dialogFragment);
        }

    }

    private class PlaylistResultViewHolder extends ViewHolder{

        private ItemSearchPlaylistBinding binding;
        public PlaylistResultViewHolder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }

        @Override
        public void bind(Object object) {
            binding.setModel((PlaylistModel) object);
            binding.setFrag(dialogFragment);
        }
    }

    private class SongResultViewHolder extends ViewHolder{

        private ItemSearchSongBinding binding;
        public SongResultViewHolder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }

        @Override
        public void bind(Object object) {
            binding.setModel((SongModel) object);
            binding.setFrag(dialogFragment);
        }
    }

    private class AlbumResultViewHolder extends ViewHolder{

        private ItemSearchAlbumBinding binding;
        public AlbumResultViewHolder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }

        @Override
        public void bind(Object object) {
            binding.setModel((AlbumModel) object);
            binding.setFrag(dialogFragment);
        }
    }

    private class VideoResultViewHolder extends ViewHolder{

        private ItemSearchVideoBinding binding;
        public VideoResultViewHolder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }

        @Override
        public void bind(Object object) {
            binding.setModel((VideoModel) object);
            binding.setFrag(dialogFragment);
        }
    }
}
