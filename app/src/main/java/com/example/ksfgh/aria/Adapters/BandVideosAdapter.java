package com.example.ksfgh.aria.Adapters;

import android.databinding.DataBindingUtil;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableList;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ksfgh.aria.Model.VideoModel;
import com.example.ksfgh.aria.R;
import com.example.ksfgh.aria.ViewModel.BandActivityViewModel;
import com.example.ksfgh.aria.databinding.BandVideosBinding;

/**
 * Created by ksfgh on 22/02/2018.
 */

public class BandVideosAdapter extends RecyclerView.Adapter<BandVideosAdapter.ViewHolder> {

    private ObservableArrayList<VideoModel> videoModels;
    private BandActivityViewModel viewModel;

    public BandVideosAdapter(BandActivityViewModel viewModel) {
        this.viewModel = viewModel;
        videoModels = viewModel.videos;
        videoModels.addOnListChangedCallback(new ObservableList.OnListChangedCallback<ObservableList<VideoModel>>() {
            @Override
            public void onChanged(ObservableList<VideoModel> videoModels) {
                notifyDataSetChanged();
            }

            @Override
            public void onItemRangeChanged(ObservableList<VideoModel> videoModels, int i, int i1) {
                notifyItemRangeChanged(i, i1);
            }

            @Override
            public void onItemRangeInserted(ObservableList<VideoModel> videoModels, int i, int i1) {
                notifyItemRangeInserted(i, i1);
            }

            @Override
            public void onItemRangeMoved(ObservableList<VideoModel> videoModels, int i, int i1, int i2) {
                notifyDataSetChanged();
            }

            @Override
            public void onItemRangeRemoved(ObservableList<VideoModel> videoModels, int i, int i1) {
                notifyItemRangeRemoved(i, i1);
            }
        });
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_band_videos, parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        VideoModel model = videoModels.get(position);
        holder.binding.setModel(model);
        holder.binding.setViewmodel(viewModel);
    }

    @Override
    public int getItemCount() {
        return videoModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private BandVideosBinding binding;

        public ViewHolder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }
    }
}
