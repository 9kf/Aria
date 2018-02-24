package com.example.ksfgh.aria.ViewModel;

import android.content.Intent;
import android.databinding.BindingAdapter;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableList;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.example.ksfgh.aria.Model.BandModel;
import com.example.ksfgh.aria.Model.GenreModel;
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
    public ObservableArrayList<BandModel> bands;
    public ObservableArrayList<GenreModel> genres;
    public HomeScreen homeScreen;

    public HomeViewModel(HomeScreen homeScreen) {
        this.playlistModels = new ObservableArrayList<>();
        bands = new ObservableArrayList<>();
        genres = new ObservableArrayList<>();
        this.homeScreen = homeScreen;
        EventBus.getDefault().register(this);
        getPlaylist();
        getBands();
        setGenres();
    }

    private void setGenres() {

        genres.add(new GenreModel(Singleton.getInstance().BASE + "/Aria/public/assets/img/genre/alternative.jpeg", "ALTERNATIVE"));
        genres.add(new GenreModel(Singleton.getInstance().BASE + "/Aria/public/assets/img/genre/blues.jpeg", "BLUES"));
        genres.add(new GenreModel(Singleton.getInstance().BASE + "/Aria/public/assets/img/genre/classical.jpeg", "CLASSICAL"));
        genres.add(new GenreModel(Singleton.getInstance().BASE + "/Aria/public/assets/img/genre/country.jpeg", "COUNTRY"));
        genres.add(new GenreModel(Singleton.getInstance().BASE + "/Aria/public/assets/img/genre/dance.jpeg", "DANCE"));
        genres.add(new GenreModel(Singleton.getInstance().BASE + "/Aria/public/assets/img/genre/electronic.jpeg", "ELECTRONIC"));
        genres.add(new GenreModel(Singleton.getInstance().BASE + "/Aria/public/assets/img/genre/hiphop.jpeg", "HIPHOP"));
        genres.add(new GenreModel(Singleton.getInstance().BASE + "/Aria/public/assets/img/genre/inspirational.jpeg", "INSPIRATIONAL"));
        genres.add(new GenreModel(Singleton.getInstance().BASE + "/Aria/public/assets/img/genre/jazz.jpeg", "JAZZ"));
        genres.add(new GenreModel(Singleton.getInstance().BASE + "/Aria/public/assets/img/genre/opera.jpeg", "OPERA"));
        genres.add(new GenreModel(Singleton.getInstance().BASE + "/Aria/public/assets/img/genre/pop.jpeg", "POP"));
        genres.add(new GenreModel(Singleton.getInstance().BASE + "/Aria/public/assets/img/genre/punk.jpeg", "PUNK"));
        genres.add(new GenreModel(Singleton.getInstance().BASE + "/Aria/public/assets/img/genre/R&B.jpeg", "R&B"));
        genres.add(new GenreModel(Singleton.getInstance().BASE + "/Aria/public/assets/img/genre/rap.jpeg", "RAP"));
        genres.add(new GenreModel(Singleton.getInstance().BASE + "/Aria/public/assets/img/genre/reggae.jpeg", "REGGAE"));
        genres.add(new GenreModel(Singleton.getInstance().BASE + "/Aria/public/assets/img/genre/rock.jpeg", "ROCK"));
        genres.add(new GenreModel(Singleton.getInstance().BASE + "/Aria/public/assets/img/genre/romance.jpeg", "ROMANCE"));
        genres.add(new GenreModel(Singleton.getInstance().BASE + "/Aria/public/assets/img/genre/soul.jpeg", "SOUL"));

    }

    private void getBands() {

        Disposable disposable = RetrofitClient.getClient().getbands()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<BandModel[]>() {
                    @Override
                    public void onNext(BandModel[] bandModels) {
                        for(BandModel band: bandModels){
                            bands.add(band);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("mybands", e.getMessage() + " ");
                    }

                    @Override
                    public void onComplete() {
                        Log.d("mybands",  "complete!");
                    }
                });

        EventBus.getDefault().post(disposable, "homeDisposables");
    }

    @BindingAdapter("bind:imgUrl")
    public static void setImage(ImageView view, String image){
        Glide.with(Singleton.getInstance().homeFragment).load(image)
                    .apply(RequestOptions.overrideOf(150,150))
                .apply(RequestOptions.centerCropTransform())
                .into(view);
    }

    public void playlistClicked(PlaylistModel model){

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
                        Log.d("playlists", e.getMessage() + "");
                    }

                    @Override
                    public void onComplete() {

                    }
                });

        EventBus.getDefault().post(disposable, "homeDisposables");
    }
}
