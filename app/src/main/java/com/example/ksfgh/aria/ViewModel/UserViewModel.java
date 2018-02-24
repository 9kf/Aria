package com.example.ksfgh.aria.ViewModel;

import android.databinding.BindingAdapter;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableField;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.example.ksfgh.aria.Adapters.UserPlaylistsAdapter;
import com.example.ksfgh.aria.Model.FacebookUserModel;
import com.example.ksfgh.aria.Model.PlaylistModel;
import com.example.ksfgh.aria.Rest.RetrofitClient;
import com.example.ksfgh.aria.Singleton;
import com.example.ksfgh.aria.View.fragments.UserFragment;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by ksfgh on 25/02/2018.
 */

public class UserViewModel {

    public FacebookUserModel user;
    public ObservableArrayList<PlaylistModel> userPlaylists;
    private UserFragment fragment;

    public UserViewModel(UserFragment fragment) {
        user = Singleton.homeScreen.user;
        userPlaylists = new ObservableArrayList<>();
        this.fragment = fragment;
        getUserPlaylists();
    }

    @BindingAdapter("bind:userPlaylist")
    public static void userPlaylistAdapter(RecyclerView view, UserViewModel viewModel){
        view.setLayoutManager(new LinearLayoutManager(view.getContext(), LinearLayoutManager.HORIZONTAL, false));
        view.setAdapter(new UserPlaylistsAdapter(viewModel));
    }

    private void getUserPlaylists() {
        Disposable disposable = RetrofitClient.getClient().getPlaylists()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<PlaylistModel[]>() {
                    @Override
                    public void onNext(PlaylistModel[] playlistModels) {
                        for(PlaylistModel model:playlistModels){
                            if(model.getPlCreator().equals(user.getId())){
                                userPlaylists.add(model);
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("userSection", e.getMessage() + " ");
                    }

                    @Override
                    public void onComplete() {

                    }
                });
        fragment.addDisposables(disposable);
    }


}
