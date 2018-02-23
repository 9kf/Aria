package com.example.ksfgh.aria.Adapters;

import android.databinding.DataBindingUtil;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableList;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ksfgh.aria.Model.AlbumModel;
import com.example.ksfgh.aria.R;
import com.example.ksfgh.aria.ViewModel.BandActivityViewModel;
import com.example.ksfgh.aria.databinding.BandAlbumBinding;

/**
 * Created by ksfgh on 15/02/2018.
 */

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.ViewHolder> {

    private ObservableArrayList<AlbumModel> albums;
    private BandActivityViewModel viewModel;

    public AlbumAdapter(BandActivityViewModel viewModel) {
        this.viewModel = viewModel;
        albums = viewModel.albums;
        albums.addOnListChangedCallback(new ObservableList.OnListChangedCallback<ObservableList<AlbumModel>>() {
            @Override
            public void onChanged(ObservableList<AlbumModel> albumModels) {
                notifyDataSetChanged();
            }

            @Override
            public void onItemRangeChanged(ObservableList<AlbumModel> albumModels, int i, int i1) {
                notifyItemRangeChanged(i, i1);
            }

            @Override
            public void onItemRangeInserted(ObservableList<AlbumModel> albumModels, int i, int i1) {
                notifyItemRangeInserted(i, i1);
            }

            @Override
            public void onItemRangeMoved(ObservableList<AlbumModel> albumModels, int i, int i1, int i2) {
                notifyDataSetChanged();
            }

            @Override
            public void onItemRangeRemoved(ObservableList<AlbumModel> albumModels, int i, int i1) {
                notifyItemRangeRemoved(i, i1);
            }
        });
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_band_albums, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        AlbumModel model = albums.get(position);
        holder.binding.setModel(model);
        holder.binding.setViewmodel(viewModel);
    }

    @Override
    public int getItemCount() {
        return albums.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private BandAlbumBinding binding;
        public ViewHolder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }
    }
}
