package com.example.ksfgh.aria.Adapters;

import android.databinding.DataBindingUtil;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableList;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ksfgh.aria.Model.GenreModel;
import com.example.ksfgh.aria.R;
import com.example.ksfgh.aria.ViewModel.HomeViewModel;
import com.example.ksfgh.aria.databinding.GenreCatalogBinding;

/**
 * Created by ksfgh on 09/02/2018.
 */

public class HomeGenreCatalogsAdapter extends RecyclerView.Adapter<HomeGenreCatalogsAdapter.ViewHolder> {

    private HomeViewModel homeViewModel;
    private ObservableArrayList<GenreModel> genres;

    public HomeGenreCatalogsAdapter(HomeViewModel homeViewModel) {
        this.homeViewModel = homeViewModel;
        this.genres = homeViewModel.genres;
        genres.addOnListChangedCallback(new ObservableList.OnListChangedCallback<ObservableList<GenreModel>>() {
            @Override
            public void onChanged(ObservableList<GenreModel> genreModels) {
                notifyDataSetChanged();
            }

            @Override
            public void onItemRangeChanged(ObservableList<GenreModel> genreModels, int i, int i1) {
                notifyItemRangeChanged(i, i1);
            }

            @Override
            public void onItemRangeInserted(ObservableList<GenreModel> genreModels, int i, int i1) {
                notifyItemRangeInserted(i, i1);
            }

            @Override
            public void onItemRangeMoved(ObservableList<GenreModel> genreModels, int i, int i1, int i2) {
                notifyDataSetChanged();
            }

            @Override
            public void onItemRangeRemoved(ObservableList<GenreModel> genreModels, int i, int i1) {
                notifyItemRangeRemoved(i, i1);
            }
        });
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_genre_catalogs, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        GenreModel model = genres.get(position);
        holder.binding.setModel(model);
        holder.binding.setViewmodel(homeViewModel);
    }

    @Override
    public int getItemCount() {
        return genres.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private GenreCatalogBinding binding;

        public ViewHolder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }
    }
}
