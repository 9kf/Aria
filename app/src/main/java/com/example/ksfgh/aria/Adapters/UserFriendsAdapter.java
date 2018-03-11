package com.example.ksfgh.aria.Adapters;

import android.databinding.DataBindingUtil;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableList;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ksfgh.aria.Model.UserFacebookFriends;
import com.example.ksfgh.aria.R;
import com.example.ksfgh.aria.View.fragments.FindFriendsFragment;
import com.example.ksfgh.aria.View.fragments.NotificationFragment;
import com.example.ksfgh.aria.databinding.ItemUserFriendBinding;

/**
 * Created by ksfgh on 11/03/2018.
 */

public class UserFriendsAdapter extends RecyclerView.Adapter<UserFriendsAdapter.ViewHolder>{

    private ObservableArrayList<UserFacebookFriends> friends;
    private FindFriendsFragment fragment;

    public UserFriendsAdapter(FindFriendsFragment fragment) {
        this.fragment = fragment;
        friends = fragment.userFacebookFriends;
        friends.addOnListChangedCallback(new ObservableList.OnListChangedCallback<ObservableList<UserFacebookFriends>>() {
            @Override
            public void onChanged(ObservableList<UserFacebookFriends> userFacebookFriends) {
                notifyDataSetChanged();
            }

            @Override
            public void onItemRangeChanged(ObservableList<UserFacebookFriends> userFacebookFriends, int i, int i1) {
                notifyItemRangeChanged(i,i1);
            }

            @Override
            public void onItemRangeInserted(ObservableList<UserFacebookFriends> userFacebookFriends, int i, int i1) {
                notifyItemRangeInserted(i,i1);
            }

            @Override
            public void onItemRangeMoved(ObservableList<UserFacebookFriends> userFacebookFriends, int i, int i1, int i2) {
                notifyDataSetChanged();
            }

            @Override
            public void onItemRangeRemoved(ObservableList<UserFacebookFriends> userFacebookFriends, int i, int i1) {
                notifyItemRangeRemoved(i,i1);
            }
        });
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_friend, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        UserFacebookFriends model = friends.get(position);
        holder.binding.setModel(model);
        holder.binding.setFrag(fragment);
    }

    @Override
    public int getItemCount() {
        return friends.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ItemUserFriendBinding binding;

        public ViewHolder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }
    }
}
