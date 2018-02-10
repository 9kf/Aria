package com.example.ksfgh.aria.Adapters;

import android.databinding.DataBindingUtil;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableList;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ksfgh.aria.Model.BandModel;
import com.example.ksfgh.aria.R;
import com.example.ksfgh.aria.ViewModel.HomeViewModel;
import com.example.ksfgh.aria.databinding.WeekTopBandBinding;

/**
 * Created by ksfgh on 09/02/2018.
 */

public class HomeWeekTopBandAdapter extends RecyclerView.Adapter<HomeWeekTopBandAdapter.ViewHolder> {

    private ObservableArrayList<BandModel> bands;
    private HomeViewModel homeViewModel;

    public HomeWeekTopBandAdapter(HomeViewModel homeViewModel) {
        this.bands = homeViewModel.bands;
        this.homeViewModel = homeViewModel;

        bands.addOnListChangedCallback(new ObservableList.OnListChangedCallback<ObservableList<BandModel>>() {
            @Override
            public void onChanged(ObservableList<BandModel> bandModels) {
                notifyDataSetChanged();
            }

            @Override
            public void onItemRangeChanged(ObservableList<BandModel> bandModels, int i, int i1) {
                notifyItemRangeChanged(i, i1);
            }

            @Override
            public void onItemRangeInserted(ObservableList<BandModel> bandModels, int i, int i1) {
                notifyItemRangeInserted(i, i1);
            }

            @Override
            public void onItemRangeMoved(ObservableList<BandModel> bandModels, int i, int i1, int i2) {
                notifyDataSetChanged();
            }

            @Override
            public void onItemRangeRemoved(ObservableList<BandModel> bandModels, int i, int i1) {
                notifyItemRangeRemoved(i, i1);
            }
        });
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_top_band_week, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        BandModel band = bands.get(position);
        holder.binding.setModel(band);
        holder.binding.setViewmodel(homeViewModel);

    }

    @Override
    public int getItemCount() {
        return bands.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private WeekTopBandBinding binding;

        public ViewHolder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }
    }
}
