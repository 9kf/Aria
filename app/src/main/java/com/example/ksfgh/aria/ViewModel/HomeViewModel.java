package com.example.ksfgh.aria.ViewModel;

import android.content.Intent;
import android.databinding.BindingAdapter;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableList;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.ksfgh.aria.Model.PlaylistModel;
import com.example.ksfgh.aria.Rest.RetrofitClient;
import com.example.ksfgh.aria.Singleton;
import com.example.ksfgh.aria.View.activities.HomeScreen;
import com.example.ksfgh.aria.View.activities.PlaylistActivity;

import org.simple.eventbus.EventBus;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by ksfgh on 28/01/2018.
 */

public class HomeViewModel {

    public ObservableArrayList<PlaylistModel> playlistModels;
    public HomeScreen homeScreen;

    public HomeViewModel(HomeScreen homeScreen) {
        this.playlistModels = new ObservableArrayList<>();
        this.homeScreen = homeScreen;
        EventBus.getDefault().register(this);
        getPlaylist();
    }

    @BindingAdapter("bind:imgUrl")
    public static void setImage(ImageView view, String image){
        Glide.with(Singleton.getInstance().homeFragment).load(image).into(view);
    }

    public void playlistClicked(PlaylistModel model){
//        if(Singleton.getInstance().currentPlaylistId != null){
//            if(Singleton.getInstance().currentPlaylistId.equals(model))
//                Singleton.getInstance().isTheSamePlaylist = true;
//            else {
//                Singleton.getInstance().isTheSamePlaylist = false
//            }
//        }
        EventBus.getDefault().post(model, "setPlist");
        Singleton.getInstance().currentPlaylistId = model;
        Intent intent = new Intent(homeScreen, PlaylistActivity.class);
        homeScreen.startActivity(intent);
    }

    public void getPlaylist(){
        final Disposable disposable = RetrofitClient.getClient().getPlaylists()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<PlaylistModel[]>() {
                    @Override
                    public void onNext(PlaylistModel[] playlistModel) {
                        for(PlaylistModel p:playlistModel){
                            playlistModels.add(p);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("playlists", e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });

        EventBus.getDefault().post(disposable, "homeDisposables");
    }
}
