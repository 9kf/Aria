package com.example.ksfgh.aria.Adapters;

import android.databinding.DataBindingUtil;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableList;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ksfgh.aria.Model.FacebookUserModel;
import com.example.ksfgh.aria.Model.UserModel;
import com.example.ksfgh.aria.R;
import com.example.ksfgh.aria.ViewModel.BandActivityViewModel;
import com.example.ksfgh.aria.databinding.BandProfileMembersBinding;

/**
 * Created by ksfgh on 15/02/2018.
 */

public class BandMemberAdapter extends RecyclerView.Adapter<BandMemberAdapter.ViewHolder> {

    private BandActivityViewModel viewModel;
    private ObservableArrayList<UserModel> users;

    public BandMemberAdapter(BandActivityViewModel viewModel) {
        this.viewModel = viewModel;
        users = viewModel.members;
        users.addOnListChangedCallback(new ObservableList.OnListChangedCallback<ObservableList<FacebookUserModel>>() {
            @Override
            public void onChanged(ObservableList<FacebookUserModel> facebookUserModels) {
                notifyDataSetChanged();
            }

            @Override
            public void onItemRangeChanged(ObservableList<FacebookUserModel> facebookUserModels, int i, int i1) {
                notifyItemRangeChanged(i, i1);
            }

            @Override
            public void onItemRangeInserted(ObservableList<FacebookUserModel> facebookUserModels, int i, int i1) {
                notifyItemRangeInserted(i, i1);
            }

            @Override
            public void onItemRangeMoved(ObservableList<FacebookUserModel> facebookUserModels, int i, int i1, int i2) {
                notifyDataSetChanged();
            }

            @Override
            public void onItemRangeRemoved(ObservableList<FacebookUserModel> facebookUserModels, int i, int i1) {
                notifyItemRangeRemoved(i, i1);
            }
        });
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_band_members, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        UserModel model = users.get(position);
        holder.binding.setModel(model);
        holder.binding.setViewmodel(viewModel);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private BandProfileMembersBinding binding;

        public ViewHolder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }
    }
}
