package com.example.ksfgh.aria.Adapters;

import android.databinding.DataBindingUtil;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableList;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ksfgh.aria.Model.CustomModelForBandPage;
import com.example.ksfgh.aria.R;
import com.example.ksfgh.aria.ViewModel.TopChartsViewModel;
import com.example.ksfgh.aria.databinding.ItemTopChartsBandBinding;

/**
 * Created by ksfgh on 07/03/2018.
 */

public class TopTwentyAdapter extends RecyclerView.Adapter<TopTwentyAdapter.ViewHolder> {

    private ObservableArrayList<CustomModelForBandPage> bands;
    private TopChartsViewModel viewModel;

    public TopTwentyAdapter(TopChartsViewModel viewModel) {
        this.viewModel = viewModel;
        bands = new ObservableArrayList<>();
        bands.addOnListChangedCallback(new ObservableList.OnListChangedCallback<ObservableList<CustomModelForBandPage>>() {
            @Override
            public void onChanged(ObservableList<CustomModelForBandPage> customModelForBandPages) {
                notifyDataSetChanged();
            }

            @Override
            public void onItemRangeChanged(ObservableList<CustomModelForBandPage> customModelForBandPages, int i, int i1) {
                notifyItemRangeChanged(i,i1);
            }

            @Override
            public void onItemRangeInserted(ObservableList<CustomModelForBandPage> customModelForBandPages, int i, int i1) {
                notifyItemRangeInserted(i,i1);
            }

            @Override
            public void onItemRangeMoved(ObservableList<CustomModelForBandPage> customModelForBandPages, int i, int i1, int i2) {
                notifyDataSetChanged();
            }

            @Override
            public void onItemRangeRemoved(ObservableList<CustomModelForBandPage> customModelForBandPages, int i, int i1) {
                notifyItemRangeRemoved(i,i1);
            }
        });
        bands.addAll(viewModel.topChartBands);

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_top_charts_band, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        CustomModelForBandPage model = bands.get(position);
        holder.bandBinding.setModel(model);
        holder.bandBinding.setViewmodel(viewModel);
        holder.bandBinding.setPosition(position);
    }

    @Override
    public int getItemCount() {
        if(bands.size() < 20)
            return bands.size();
        else
            return 20;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ItemTopChartsBandBinding bandBinding;

        public ViewHolder(View itemView) {
            super(itemView);
            bandBinding = DataBindingUtil.bind(itemView);
        }
    }
}
