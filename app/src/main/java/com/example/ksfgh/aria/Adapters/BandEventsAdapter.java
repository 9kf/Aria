package com.example.ksfgh.aria.Adapters;

import android.databinding.DataBindingUtil;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableList;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ksfgh.aria.Model.EventModel;
import com.example.ksfgh.aria.R;
import com.example.ksfgh.aria.ViewModel.BandActivityViewModel;
import com.example.ksfgh.aria.databinding.BandEventsBinding;

/**
 * Created by ksfgh on 21/02/2018.
 */

public class BandEventsAdapter extends RecyclerView.Adapter<BandEventsAdapter.ViewHolder> {

    private ObservableArrayList<EventModel> events;
    private BandActivityViewModel viewModel;

    public BandEventsAdapter(BandActivityViewModel viewModel) {
        this.viewModel = viewModel;
        events = viewModel.events;
        events.addOnListChangedCallback(new ObservableList.OnListChangedCallback<ObservableList<EventModel>>() {
            @Override
            public void onChanged(ObservableList<EventModel> eventModels) {
                notifyDataSetChanged();
            }

            @Override
            public void onItemRangeChanged(ObservableList<EventModel> eventModels, int i, int i1) {
                notifyItemRangeChanged(i, i1);
            }

            @Override
            public void onItemRangeInserted(ObservableList<EventModel> eventModels, int i, int i1) {
                notifyItemRangeInserted(i, i1);
            }

            @Override
            public void onItemRangeMoved(ObservableList<EventModel> eventModels, int i, int i1, int i2) {
                notifyDataSetChanged();
            }

            @Override
            public void onItemRangeRemoved(ObservableList<EventModel> eventModels, int i, int i1) {
                notifyItemRangeRemoved(i, i1);
            }
        });
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_band_events, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        EventModel model = events.get(position);
        holder.binding.setModel(model);
        holder.binding.setViewmodel(viewModel);
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private BandEventsBinding binding;

        public ViewHolder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }
    }
}
