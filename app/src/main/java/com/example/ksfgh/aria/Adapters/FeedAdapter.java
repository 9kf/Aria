package com.example.ksfgh.aria.Adapters;

import android.databinding.DataBindingUtil;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableList;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ksfgh.aria.Model.AlbumModel;
import com.example.ksfgh.aria.Model.BandModel;
import com.example.ksfgh.aria.Model.CustomModelForBandPage;
import com.example.ksfgh.aria.Model.EventModel;
import com.example.ksfgh.aria.Model.MemberModel;
import com.example.ksfgh.aria.Model.UserModel;
import com.example.ksfgh.aria.Model.VideoModel;
import com.example.ksfgh.aria.R;
import com.example.ksfgh.aria.View.fragments.FeedFragment;
import com.example.ksfgh.aria.databinding.ItemFeedBandEventsBinding;


import java.util.ArrayList;

/**
 * Created by ksfgh on 12/03/2018.
 */

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.ViewHolder> {

    private ObservableArrayList<EventModel> feedList;
    private FeedFragment fragment;
    private ArrayList<CustomModelForBandPage> followedBands;

    public FeedAdapter(FeedFragment fragment) {
        this.fragment = fragment;
        feedList = fragment.feedList;
        followedBands = fragment.userFollowedBands;
        feedList.addOnListChangedCallback(new ObservableList.OnListChangedCallback<ObservableList<Object>>() {
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
    public FeedAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_feed_band_events, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FeedAdapter.ViewHolder holder, int position) {
        EventModel object = feedList.get(position);
        for(CustomModelForBandPage bands: followedBands){
            for(EventModel eventModel: bands.events){
                if(eventModel.eventId == ((EventModel)object).eventId){
                    holder.binding.setBand(bands.band);
                    holder.binding.setFrag(fragment);
                    holder.binding.setModel(object);
                    break;
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return feedList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ItemFeedBandEventsBinding binding;
        public ViewHolder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }

    }

}
