package com.example.ksfgh.aria.ViewModel;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.databinding.BindingAdapter;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.graphics.Color;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.ksfgh.aria.Adapters.PlaylistSongsAdapter;
import com.example.ksfgh.aria.Model.AlbumModel;
import com.example.ksfgh.aria.Model.BandModel;
import com.example.ksfgh.aria.Model.CustomSongModelForPlaylist;
import com.example.ksfgh.aria.Model.PlaylistModel;
import com.example.ksfgh.aria.Model.PlistModel;
import com.example.ksfgh.aria.Model.PreferenceModel;
import com.example.ksfgh.aria.Model.SongModel;
import com.example.ksfgh.aria.R;
import com.example.ksfgh.aria.Rest.RetrofitClient;
import com.example.ksfgh.aria.Singleton;
import com.example.ksfgh.aria.View.activities.PlaylistActivity;
import com.google.android.exoplayer2.source.DynamicConcatenatingMediaSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;
import org.simple.eventbus.ThreadMode;

import java.io.IOException;
import java.util.ArrayList;


import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function3;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import jp.wasabeef.glide.transformations.BlurTransformation;
import okhttp3.ResponseBody;

/**
 * Created by ksfgh on 29/01/2018.
 */
public class PlaylistActivityViewModel{

    public PlaylistModel playlistModel;
    public ObservableField<String> creatorName = new ObservableField<>();
    public PlaylistActivity playlistActivity;
    private ObservableArrayList<CustomSongModelForPlaylist> playlistSongs;
    private PlaylistSongsAdapter adapter;
    private ArrayList<View> viewList;
    private boolean isPlaying;
    public ObservableBoolean isFollowing;
    public ObservableInt playlistFollowers;


    public PlaylistActivityViewModel(PlaylistActivity playlistActivity) {
        this.playlistActivity = playlistActivity;
        this.playlistModel = Singleton.getInstance().currentPlaylistId;
        creatorName.set(Singleton.getInstance().utilities.findUserById(playlistModel.getPlCreator()));
        viewList = new ArrayList<>();
        playlistSongs = new ObservableArrayList<>();
        isFollowing = new ObservableBoolean();
        playlistFollowers = new ObservableInt();
        playlistFollowers.set(playlistModel.getFollowers());
        EventBus.getDefault().register(this);
        adapter = new PlaylistSongsAdapter(playlistSongs, this);
        EventBus.getDefault().post(adapter, "setRecyclerView");
        isPlaying = Singleton.getInstance().isPlayerPlaying;

        getPlists();
        getIsFollowing();
    }

    private void getIsFollowing() {
        Disposable disposable = RetrofitClient.getClient().getUserPreferences(Singleton.homeScreen.user.user_id)
                .subscribeOn(Schedulers.newThread())
                .subscribeWith(new DisposableObserver<PreferenceModel[]>() {
                    @Override
                    public void onNext(PreferenceModel[] preferenceModel) {
                        for(PreferenceModel model:preferenceModel){
                            if(model.plId == playlistModel.getPlId()){
                                isFollowing.set(true);
                            }
                            else {
                                isFollowing.set(false);
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("follow", e.getMessage() + " ");
                    }

                    @Override
                    public void onComplete() {

                    }
                });

        EventBus.getDefault().post(disposable, "playlistDisposables");
    }

    @BindingAdapter("bind:imgUrlBlur")
    public static void blurImage(ImageView view, String image){
        Glide.with(view.getContext()).load(image)
                .apply(RequestOptions.bitmapTransform(new BlurTransformation(70)))
                .apply(RequestOptions.overrideOf(500,500))
                .into(view);
    }

    //used to store the identifiers on what songs are inside the playlist
    ArrayList<PlistModel> model = new ArrayList<>();

    public void getPlists(){

        Disposable disposable = RetrofitClient.getClient().getPlaylistSongsByPlaylistId(String.valueOf(Singleton.getInstance().currentPlaylistId.getPlId()))
                .subscribeOn(Schedulers.newThread())
                .subscribeWith(new DisposableObserver<PlistModel[]>() {
                    @Override
                    public void onNext(PlistModel[] plistModels) {
                        for(PlistModel plist : plistModels){
                            model.add(plist);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onComplete() {
                        getPlaylistSongs();
                    }
                });

        EventBus.getDefault().post(disposable, "playlistDisposables");
    }

    private void getPlaylistSongs() {

        Observable<SongModel[]> observable1 = RetrofitClient.getClient().getAllSongs();
        Observable<BandModel[]> observable2 = RetrofitClient.getClient().getbands();
        Observable<AlbumModel[]> observable3 = RetrofitClient.getClient().getAllAlbums();
        Observable<ArrayList<CustomSongModelForPlaylist>> observable = Observable.zip(observable1, observable2, observable3,
                new Function3<SongModel[], BandModel[], AlbumModel[], ArrayList<CustomSongModelForPlaylist>>() {
                    @Override
                    public ArrayList<CustomSongModelForPlaylist> apply(SongModel[] songModels, BandModel[] bandModels, AlbumModel[] albumModels) throws Exception {
                        ArrayList<CustomSongModelForPlaylist> customList = new ArrayList<>();
                        for(SongModel songs:songModels){
                            for(PlistModel plist: model){
                                if(songs.songId == plist.getSongId()){
                                    for(AlbumModel albums: albumModels){
                                        if(songs.albumId == String.valueOf(albums.getAlbumId())){
                                            for(BandModel bands: bandModels){
                                                if(albums.getBandId() == bands.getBandId()){
                                                    customList.add(new CustomSongModelForPlaylist(songs, bands, albums));
                                                    break;
                                                }
                                            }
                                            break;
                                        }
                                    }
                                    break;
                                }
                            }
                        }
                        return customList;
                    }
                });

        Disposable disposable = observable
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<ArrayList<CustomSongModelForPlaylist>>() {
                    @Override
                    public void onNext(ArrayList<CustomSongModelForPlaylist> customSongModelForPlaylists) {
                        playlistSongs.addAll(customSongModelForPlaylists);
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onComplete() {
                        if(!Singleton.getInstance().isPlayerPrepared)
                            EventBus.getDefault().post(playlistSongs, "addSongsInPlaylist");
                    }
                });

        EventBus.getDefault().post(disposable, "playlistDisposables");

    }

    public void playSelectedPlaylist(){
        if(!playlistSongs.isEmpty()){
            if(isPlaying == false){
                EventBus.getDefault().post(playlistSongs, "addSongsInPlaylist");
                EventBus.getDefault().post(true, "playOrPause");
                isPlaying = true;
                //playlistActivity.setFabSrc(true);
            }
            else {
                if(!Singleton.getInstance().currentPlaylistId.equals(Singleton.getInstance().playedPlist)){
                    EventBus.getDefault().post(playlistSongs, "addSongsInPlaylist");
                    EventBus.getDefault().post(true, "playOrPause");
                    isPlaying = true;
                    //playlistActivity.setFabSrc(true);
                }
                else {
                    EventBus.getDefault().post(false, "playOrPause");
                    isPlaying = false;
                    //playlistActivity.setFabSrc(false);
                }

            }
        }
    }

    @Subscriber(tag = "getSongs")
    public void getSongs(CustomSongModelForPlaylist song){
        EventBus.getDefault().post(playlistSongs, "addSongsInPlaylist");
        EventBus.getDefault().post(song, "skipSong");
    }


    public boolean isPlayerPlaying(){
        if(Singleton.getInstance().currentPlaylistId.equals(Singleton.getInstance().playedPlist)){
            return isPlaying;
        }
        else
            return false;

    }

    public void addViews(View view){
        viewList.add(view);
    }

    public View currentView;
    public TextView currentTextView;
    public TextView currentTextViewDetails;
    @Subscriber(tag = "highlightPlayedSong")
    @SuppressLint("ResourceAsColor")
    public void higlightPlayedSong(String empty){

        if(empty.equals("final")){
            currentView.setBackgroundColor(android.R.color.transparent);
            currentTextView.setTextColor(Color.parseColor("#FFFFFF"));
            currentTextViewDetails.setTextColor(Color.parseColor("#FFFFFF"));
            currentView = null;
            currentTextViewDetails = null;
            currentTextView = null;
        }
        else{

            if(Singleton.getInstance().song != null){

                for(int i = 0; i < playlistSongs.size(); i++){
                    if(Singleton.getInstance().song.getSong().songId == playlistSongs.get(i).getSong().songId){
                        if(currentView == null){
                            if(!viewList.isEmpty()){
                                currentView = viewList.get(i);
                                currentView.setBackgroundColor(Color.parseColor("#000000"));
                                currentTextView = viewList.get(i).findViewById(R.id.tvSongTitle);
                                currentTextView.setTextColor(Color.parseColor("#E57C1F"));
                                currentTextViewDetails = viewList.get(i).findViewById(R.id.tvSongDetails);
                                currentTextViewDetails.setTextColor(Color.parseColor("#E57C1F"));
                            }
                        }
                        else {
                            currentTextView.setTextColor(Color.parseColor("#FFFFFF"));
                            currentTextViewDetails.setTextColor(Color.parseColor("#FFFFFF"));
                            currentView.setBackgroundColor(android.R.color.transparent);
                            currentView = viewList.get(i);
                            currentView.setBackgroundColor(Color.parseColor("#000000"));
                            currentTextView = viewList.get(i).findViewById(R.id.tvSongTitle);
                            currentTextView.setTextColor(Color.parseColor("#E57C1F"));
                            currentTextViewDetails = viewList.get(i).findViewById(R.id.tvSongDetails);
                            currentTextViewDetails.setTextColor(Color.parseColor("#E57C1F"));
                        }
                    }
                }

            }

//            currentView.setBackgroundColor(Color.parseColor("#000000"));
//            currentTextView.setTextColor(Color.parseColor("#E57C1F"));
//            currentTextViewDetails.setTextColor(Color.parseColor("#E57C1F"));
        }

    }


    public void onClickSong(View view, CustomSongModelForPlaylist item){

        if(Singleton.getInstance().song == null){
            EventBus.getDefault().post(item, "skipSong");
            isPlaying = true;
        }
        else {
            if(Singleton.getInstance().song.getSong().songId == item.getSong().songId && Singleton.homeScreen.plist == Singleton.getInstance().playedPlist)
                return;
            else {
                EventBus.getDefault().post(item, "skipSong");
                isPlaying = true;
            }
        }

        //playlistActivity.setFabSrc(true);
    }

    @Subscriber(tag = "setFabSrc")
    public void setFabSrc(boolean playOrPause){
        //playlistActivity.setFabSrc(playOrPause);
    }

    public void openPlayer(){
        EventBus.getDefault().post(playlistActivity, "openPlayer");
    }

    public void onOptionsClick(View view, CustomSongModelForPlaylist item){

        PopupMenu popupMenu = new PopupMenu(Singleton.homeScreen, view);
        popupMenu.getMenuInflater().inflate(R.menu.song_popup_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.itmAddToPlaylist:

                        break;
                    case R.id.itmAddToQueue:

                        break;

                }

                return true;
            }
        });
        popupMenu.show();
    }

    public void followPlaylist(View view){

        Disposable disposable;
        if(isFollowing.get()){
             disposable = RetrofitClient.getClient().unFollowPlaylist(Singleton.homeScreen.user.user_id, String.valueOf(playlistModel.getPlId()))
                    .subscribeOn(Schedulers.newThread())
                    .subscribeWith(new DisposableObserver<Integer>() {
                        @Override
                        public void onNext(Integer responseBody) {
                            Log.d("follow", responseBody.toString() + " ");
                            isFollowing.set(false);
                            playlistModel.setFollowers(responseBody);
                            playlistFollowers.set(responseBody);
                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.d("follow", e.getMessage() + " ");
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        }
        else {
             disposable = RetrofitClient.getClient().followPlaylist(Singleton.homeScreen.user.user_id, String.valueOf(playlistModel.getPlId()))
                    .subscribeOn(Schedulers.newThread())
                    .subscribeWith(new DisposableObserver<Integer>() {
                        @Override
                        public void onNext(Integer responseBody) {
                            Log.d("follow", responseBody.toString() + " ");
                            isFollowing.set(true);
                            playlistModel.setFollowers(responseBody);
                            playlistFollowers.set(responseBody);
                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.d("follow", e.getMessage() + " ");
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        }

        EventBus.getDefault().post(disposable, "playlistDisposables");
    }

    public void destroyActivity(){
        playlistActivity.finish();
    }

}
