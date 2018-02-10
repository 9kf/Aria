package com.example.ksfgh.aria.Adapters;

import android.databinding.DataBindingUtil;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableList;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ksfgh.aria.Model.BandModel;
import com.example.ksfgh.aria.Model.CustomModelForBandPage;
import com.example.ksfgh.aria.R;
import com.example.ksfgh.aria.ViewModel.MyBandsViewModel;
import com.example.ksfgh.aria.databinding.ItemMyBandsBinding;

/**
 * Created by ksfgh on 10/02/2018.
 */

public class MyBandsAdapter extends RecyclerView.Adapter<MyBandsAdapter.ViewHolder> {

    private ObservableArrayList<CustomModelForBandPage> bandModels;
    private MyBandsViewModel viewModel;

    public MyBandsAdapter(MyBandsViewModel viewModel) {
        this.viewModel = viewModel;
        bandModels = viewModel.bandModels;
        bandModels.addOnListChangedCallback(new ObservableList.OnListChangedCallback<ObservableList<BandModel>>() {
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_my_bands, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        CustomModelForBandPage model = bandModels.get(position);
        holder.bandsBinding.setModel(model);
        holder.bandsBinding.setViewmodel(viewModel);
    }

    @Override
    public int getItemCount() {
        return bandModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ItemMyBandsBinding bandsBinding;

        public ViewHolder(View itemView) {
            super(itemView);
            bandsBinding = DataBindingUtil.bind(itemView);
        }
    }
}
