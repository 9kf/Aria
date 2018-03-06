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
import com.example.ksfgh.aria.View.activities.GenreCatalogsActivity;
import com.example.ksfgh.aria.databinding.BandGenreViewsBinding;


/**
 * Created by ksfgh on 05/03/2018.
 */

public class GenreCatalogBandAdapter extends RecyclerView.Adapter<GenreCatalogBandAdapter.ViewHolder> {

    private ObservableArrayList<BandModel> bands;
    private GenreCatalogsActivity activity;

    public GenreCatalogBandAdapter(GenreCatalogsActivity activity) {
        this.activity = activity;
        bands = activity.genreBands;
        bands.addOnListChangedCallback(new ObservableList.OnListChangedCallback<ObservableList<BandModel>>() {
            @Override
            public void onChanged(ObservableList<BandModel> bandModels) {
                notifyDataSetChanged();
            }

            @Override
            public void onItemRangeChanged(ObservableList<BandModel> bandModels, int i, int i1) {
                notifyItemRangeChanged(i,i1);
            }

            @Override
            public void onItemRangeInserted(ObservableList<BandModel> bandModels, int i, int i1) {
                notifyItemRangeInserted(i,i1);
            }

            @Override
            public void onItemRangeMoved(ObservableList<BandModel> bandModels, int i, int i1, int i2) {
                notifyDataSetChanged();
            }

            @Override
            public void onItemRangeRemoved(ObservableList<BandModel> bandModels, int i, int i1) {
                notifyItemRangeRemoved(i,i1);
            }
        });
    }

    @Override
    public GenreCatalogBandAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_band_genre_views, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(GenreCatalogBandAdapter.ViewHolder holder, int position) {
        BandModel model = bands.get(position);
        holder.binding.setModel(model);
        holder.binding.setView(activity);
    }

    @Override
    public int getItemCount() {
        return bands.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private BandGenreViewsBinding binding;
        public ViewHolder(View itemView) {
            super(itemView);

            binding = DataBindingUtil.bind(itemView);
        }
    }
}
