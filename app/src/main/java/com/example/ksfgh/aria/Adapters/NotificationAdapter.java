package com.example.ksfgh.aria.Adapters;

import android.databinding.DataBindingUtil;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableList;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ksfgh.aria.Model.NotificationModel;
import com.example.ksfgh.aria.R;
import com.example.ksfgh.aria.View.fragments.NotificationFragment;
import com.example.ksfgh.aria.databinding.ItemNotificationBinding;

/**
 * Created by ksfgh on 11/03/2018.
 */

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private ObservableArrayList<NotificationModel> notifs;
    private NotificationFragment fragment;

    public NotificationAdapter(NotificationFragment fragment) {
        this.fragment = fragment;
        notifs = fragment.notifs;
        notifs.addOnListChangedCallback(new ObservableList.OnListChangedCallback<ObservableList<NotificationModel>>() {
            @Override
            public void onChanged(ObservableList<NotificationModel> notificationModels) {
                notifyDataSetChanged();
            }

            @Override
            public void onItemRangeChanged(ObservableList<NotificationModel> notificationModels, int i, int i1) {
                notifyItemRangeChanged(i,i1);
            }

            @Override
            public void onItemRangeInserted(ObservableList<NotificationModel> notificationModels, int i, int i1) {
                notifyItemRangeInserted(i,i1);
            }

            @Override
            public void onItemRangeMoved(ObservableList<NotificationModel> notificationModels, int i, int i1, int i2) {
                notifyDataSetChanged();
            }

            @Override
            public void onItemRangeRemoved(ObservableList<NotificationModel> notificationModels, int i, int i1) {
                notifyItemRangeRemoved(i,i1);
            }
        });
    }

    @Override
    public NotificationAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_notifs, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(NotificationAdapter.ViewHolder holder, int position) {
        NotificationModel model = notifs.get(position);
        holder.binding.setFrag(fragment);
        holder.binding.setModel(model);
    }

    @Override
    public int getItemCount() {
        return notifs.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ItemNotificationBinding binding;
        public ViewHolder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }
    }
}
