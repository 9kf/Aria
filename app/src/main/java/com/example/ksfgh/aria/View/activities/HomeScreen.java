package com.example.ksfgh.aria.View.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableBoolean;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.media.session.PlaybackState;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.ksfgh.aria.Model.AlbumModel;
import com.example.ksfgh.aria.Model.CustomSongModelForPlaylist;
import com.example.ksfgh.aria.Model.FacebookUserModel;
import com.example.ksfgh.aria.Model.PlaylistModel;
import com.example.ksfgh.aria.R;
import com.example.ksfgh.aria.Rest.RetrofitClient;
import com.example.ksfgh.aria.Singleton;
import com.example.ksfgh.aria.View.fragments.SearchDialogFragment;
import com.example.ksfgh.aria.ViewModel.HomeScreenViewModel;
import com.example.ksfgh.aria.databinding.ActivityHomeScreenBinding;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.DynamicConcatenatingMediaSource;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ShuffleOrder;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.gson.Gson;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;
import org.simple.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import nl.psdcompany.duonavigationdrawer.views.DuoDrawerLayout;
import nl.psdcompany.duonavigationdrawer.widgets.DuoDrawerToggle;

public class HomeScreen extends AppCompatActivity implements Player.EventListener {

    private ActivityHomeScreenBinding activityHomeScreenBinding;
    private CompositeDisposable compositeDisposable;
    public FacebookUserModel user;

    private DuoDrawerLayout duoDrawerLayout;
    private DuoDrawerToggle duoDrawerToggle;
    private BottomSheetBehavior bottomSheetBehavior;
    Toolbar toolbar;
    public HomeScreenViewModel viewModel;

    //Exoplayer or Music and Video Player
    public SimpleExoPlayer exoPlayer;
    public DataSource.Factory dataSourceFactory;
    public ExtractorsFactory extractorsFactory;
    private DynamicConcatenatingMediaSource dynamicConcatenatingMediaSource;
    public ArrayList<CustomSongModelForPlaylist> songList;
    public boolean isPlaying;
    public PlaylistModel plist;
    public AlbumModel currentAlbumPlaying;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //set content view and data binding
        activityHomeScreenBinding = DataBindingUtil.setContentView(this, R.layout.activity_home_screen);

        //initialize disposable
        compositeDisposable = new CompositeDisposable();

        //set the toolbar
        toolbar = activityHomeScreenBinding.toolbar;
        setSupportActionBar(toolbar);

        //register to event bus
        EventBus.getDefault().register(this);

        //assign this to the singleton for the reference of other activities
        Singleton.homeScreen = this;

        bottomSheetBehavior = BottomSheetBehavior.from(activityHomeScreenBinding.llPersistentBar);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        //initialize exoplayer
        initPlayer("");
        exoPlayer.addListener(this);
        dataSourceFactory = new DefaultDataSourceFactory(this, Util.getUserAgent(this, "Aria"), null);
        extractorsFactory = new DefaultExtractorsFactory();
        isPlaying = false;

        //initialize list ong songs
        dynamicConcatenatingMediaSource = new DynamicConcatenatingMediaSource();
        songList = new ArrayList<>();

        //initialize the drawer and drawertoggle
        duoDrawerLayout = activityHomeScreenBinding.duoDrawerLayout;
        duoDrawerToggle = new DuoDrawerToggle(this, duoDrawerLayout, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        duoDrawerLayout.setDrawerListener(duoDrawerToggle);
        duoDrawerToggle.syncState();

        //set the viewmodel
        user = new Gson().fromJson(getSharedPreferences(Singleton.getInstance().PREFERENCE_NAME, MODE_PRIVATE).getString("user", null),
                FacebookUserModel.class);
        viewModel = new HomeScreenViewModel(user, this, duoDrawerLayout);
        activityHomeScreenBinding.setViewModel(viewModel);
    }

    //
    //EventBus Functions
    //
    @Subscriber(tag = "switchFragment")
    private void switchFragment(Fragment fragment){
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.layoutContainer, fragment)
                .commit();
    }

    @Subscriber(tag = "homeScreen")
    private HomeScreen getHomeActivity(){
        return this;
    }

    @Subscriber(tag = "homeDisposables", mode = ThreadMode.ASYNC)
    private void addDisposable(Disposable disposable){
        compositeDisposable.add(disposable);
    }

    //
    //MusicPlayer Functions
    //

    @Subscriber(tag = "initPlayer")
    private void initPlayer(String empty){
        exoPlayer = ExoPlayerFactory.newSimpleInstance(new DefaultRenderersFactory(this),  new DefaultTrackSelector(), new DefaultLoadControl());
    }

    @Subscriber(tag = "addSongToQueue")
    private void addSongToQueue(CustomSongModelForPlaylist song){
        dynamicConcatenatingMediaSource.addMediaSource(
                Singleton.getInstance().utilities.createMediaSource(
                        Singleton.getInstance().utilities.buildAudioURL(Singleton.getInstance().BASE,song.getSong().songAudio).toString(),
                        dataSourceFactory,
                        extractorsFactory
                )
        );
        songList.add(song);

        exoPlayer.prepare(dynamicConcatenatingMediaSource,false,false);
    }

    @Subscriber(tag = "removeSongToQueue")
    private void removeSongToQueue(CustomSongModelForPlaylist song){
        for(int i = 0; i < songList.size(); i++){
            if(songList.get(i).getSong().songId == song.getSong().songId){
                dynamicConcatenatingMediaSource.removeMediaSource(i);
                break;
            }
        }
        songList.remove(song);
    }

    @Subscriber(tag = "addSongsInPlaylist")
    public void addSongsInPlaylist(ArrayList<CustomSongModelForPlaylist> songs){

        Log.d("playlist", "adding songs");
        boolean resetPosition = false;
        if(songList.size() != songs.size()){
            resetPosition = true;
        }
        else {
            for(int i = 0; i < songList.size(); i++){
                if(songList.get(i).getSong().songId != songs.get(i).getSong().songId){
                    resetPosition = true;
                    break;
                }
            }
        }

        //dynamicConcatenatingMediaSource.releaseSource();
        DynamicConcatenatingMediaSource dynamicConcatenatingMediaSource = new DynamicConcatenatingMediaSource();
        songList.clear();

        for(CustomSongModelForPlaylist song: songs){
            dynamicConcatenatingMediaSource.addMediaSource(
                    Singleton.getInstance().utilities.createMediaSource(
                            Singleton.getInstance().utilities.buildAudioURL(Singleton.getInstance().BASE,song.getSong().songAudio).toString(),
                            dataSourceFactory,
                            extractorsFactory
                    )
            );

            songList.add(song);
        }

        for(CustomSongModelForPlaylist song:songList){
            Log.d("skip", song.getSong().songAudio);
        }

        exoPlayer.prepare(dynamicConcatenatingMediaSource, resetPosition, true);
        Singleton.getInstance().isPlayerPrepared = true;

    }

//    @Subscriber(tag = "addSongsInPlaylist2")
//    public void addSongsInPlaylist2(ArrayList<CustomSongModelForPlaylist> songs){
//
//        Log.d("playlist", "adding songs");
//        boolean resetPosition = false;
//        if(songList.size() != songs.size()){
//            resetPosition = true;
//        }
//        else {
//            for(int i = 0; i < songList.size(); i++){
//                if(songList.get(i).getSong().songId != songs.get(i).getSong().songId){
//                    resetPosition = true;
//                    break;
//                }
//            }
//        }
//
//
//        //dynamicConcatenatingMediaSource.releaseSource();
//        DynamicConcatenatingMediaSource dynamicConcatenatingMediaSource = new DynamicConcatenatingMediaSource();
//        songList.clear();
//
//        for(CustomSongModelForPlaylist song: songs){
//            dynamicConcatenatingMediaSource.addMediaSource(
//                    Singleton.getInstance().utilities.createMediaSource(
//                            Singleton.getInstance().utilities.buildAudioURL(Singleton.getInstance().BASE,song.getSong().songAudio).toString(),
//                            dataSourceFactory,
//                            extractorsFactory
//                    )
//            );
//
//            songList.add(song);
//        }
//
//        for(CustomSongModelForPlaylist song:songList){
//            Log.d("skip", song.getSong().songAudio);
//        }
//
//        exoPlayer.prepare(dynamicConcatenatingMediaSource, resetPosition, true);
//        Singleton.getInstance().isPlayerPrepared = true;
//
//    }

    public int windowIndex = -1;
    @Subscriber(tag = "skipSong")
    private void skipSong(CustomSongModelForPlaylist song){

        if(plist != Singleton.getInstance().playedPlist && Singleton.getInstance().playedPlist != null){
            EventBus.getDefault().post(song,"getSongs");
            Singleton.getInstance().playedPlist = plist;
        }
        else {

            for(int i = 0; i < songList.size(); i++){
                if(songList.get(i).getSong().songId == song.getSong().songId){
                    windowIndex = i;
                    break;
                }
            }

            if(isPlaying == true){
                exoPlayer.seekTo(windowIndex, 0);
                Singleton.getInstance().song = song;
            }
            else {
                exoPlayer.seekTo(windowIndex, 0);
                exoPlayer.setPlayWhenReady(true);
                Singleton.getInstance().isPlayerPlaying = true;
                Singleton.getInstance().song = song;
                isPlaying = true;
            }

            Singleton.getInstance().playedPlist = plist;

            if(bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_HIDDEN){
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                viewModel.isBottomsheetUp.set(true);
                bottomSheetBehavior.setHideable(false);

                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) activityHomeScreenBinding.layoutContainer.getLayoutParams();
                params.bottomMargin = 100;
                activityHomeScreenBinding.layoutContainer.requestLayout();
            }
            viewModel.persistentBarSong.set(Singleton.getInstance().song);
            viewModel.isPlayerPlaying.set(true);

            currentAlbumPlaying = null;
        }
    }

    @Subscriber(tag = "skipSong2")
    private void skipSong2(CustomSongModelForPlaylist song){

        if(Singleton.getInstance().playedPlist == null){
            if(currentAlbumPlaying == null){
                EventBus.getDefault().post(song,"getSongsInAlbum");
                currentAlbumPlaying = song.getAlbum();
            }
            else if(currentAlbumPlaying != null && currentAlbumPlaying.getAlbumId() != song.getAlbum().getAlbumId()){
                EventBus.getDefault().post(song,"getSongsInAlbum");
                currentAlbumPlaying = song.getAlbum();
            }
            else if(Singleton.getInstance().isNewSongAddedToAlbum){
                Singleton.getInstance().isNewSongAddedToAlbum = false;
                EventBus.getDefault().post(song,"getSongsInAlbum");
                currentAlbumPlaying = song.getAlbum();
            }
            else{
                for(int i = 0; i < songList.size(); i++){
                    if(songList.get(i).getSong().songId == song.getSong().songId){
                        windowIndex = i;
                        break;
                    }
                }

                if(isPlaying == true){
                    exoPlayer.seekTo(windowIndex, 0);
                    Singleton.getInstance().song = song;
                }
                else {
                    exoPlayer.seekTo(windowIndex, 0);
                    exoPlayer.setPlayWhenReady(true);
                    Singleton.getInstance().isPlayerPlaying = true;
                    Singleton.getInstance().song = song;
                    isPlaying = true;
                }

                if(bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_HIDDEN){
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    viewModel.isBottomsheetUp.set(true);
                    bottomSheetBehavior.setHideable(false);

                    ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) activityHomeScreenBinding.layoutContainer.getLayoutParams();
                    params.bottomMargin = 100;
                    activityHomeScreenBinding.layoutContainer.requestLayout();
                }
                viewModel.persistentBarSong.set(Singleton.getInstance().song);
                viewModel.isPlayerPlaying.set(true);
            }
        }
        else {
            Singleton.getInstance().playedPlist = null;
            skipSong2(song);
        }

    }

    @Subscriber(tag = "seekSongTo")
    private void seekSongTo(int time){
        exoPlayer.seekTo(windowIndex,time*1000);
        EventBus.getDefault().post("", "songTimer");
    }

    @Subscriber(tag = "nextSong")
    private void nextSong(String empty){
        if(windowIndex+1 < songList.size()){
            windowIndex = windowIndex +1;
            Singleton.getInstance().song = songList.get(windowIndex);
            exoPlayer.seekTo(windowIndex,0);
        }
    }

    @Subscriber(tag = "previousSong")
    private void previousSong(String empty){
        if(windowIndex-1 >= 0){
            windowIndex = windowIndex -1;
            Singleton.getInstance().song = songList.get(windowIndex);
            exoPlayer.seekTo(windowIndex,0);
        }
    }

    @Subscriber(tag = "playSongDirectly")
    private void playSong(CustomSongModelForPlaylist song){

        dynamicConcatenatingMediaSource.releaseSource();
        dynamicConcatenatingMediaSource = new DynamicConcatenatingMediaSource();
        songList.clear();

        dynamicConcatenatingMediaSource.addMediaSource(
                Singleton.getInstance().utilities.createMediaSource(
                        Singleton.getInstance().utilities.buildAudioURL(Singleton.getInstance().BASE,song.getSong().songAudio).toString(),
                        dataSourceFactory,
                        extractorsFactory
                )
        );
        songList.add(song);

        exoPlayer.prepare(dynamicConcatenatingMediaSource, true, false);
        exoPlayer.setPlayWhenReady(true);
        isPlaying = true;
        Singleton.getInstance().song = song;
    }

    @Subscriber(tag = "playOrPause")
    private void playOrPause(boolean play){

        if(exoPlayer.getPlaybackState() == Player.STATE_ENDED){
            Singleton.getInstance().song = songList.get(0);
            exoPlayer.seekToDefaultPosition(0);
            windowIndex = 0;
        }


        //play == false means that the user has requested the player to pause
        //play == true means that the user has requested the player to play
        if(play == false){
            exoPlayer.setPlayWhenReady(false);
            isPlaying = false;
            viewModel.isPlayerPlaying.set(false);
            Singleton.getInstance().isPlayerPlaying = false;
            if(disposable != null)
                disposable.dispose();
        }
        else {
            exoPlayer.setPlayWhenReady(play);
            isPlaying = true;
            Singleton.getInstance().isPlayerPlaying = true;
            viewModel.isPlayerPlaying.set(true);
            Singleton.getInstance().playedPlist = plist;

            if(disposable.isDisposed()){
                disposable = Observable.interval(1, TimeUnit.SECONDS)
                        .observeOn(AndroidSchedulers.mainThread())
                        .take((Singleton.homeScreen.exoPlayer.getDuration() / 1000))
                        .map(x -> 1+x)
                        .doOnNext(new Consumer<Long>() {
                            @Override
                            public void accept(Long aLong) throws Exception {
                                Log.d("songsplayed", aLong.toString() + " secs");
                                timePlayed += aLong.intValue();
                            }
                        })
                        .doOnError(new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {

                            }
                        })
                        .subscribe();
            }
        }

        if(!Singleton.getInstance().videoPlayed){
            if(bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_HIDDEN){
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                bottomSheetBehavior.setHideable(false);
                viewModel.isBottomsheetUp.set(true);

                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) activityHomeScreenBinding.layoutContainer.getLayoutParams();
                params.bottomMargin = 100;
                activityHomeScreenBinding.layoutContainer.requestLayout();
            }
            viewModel.persistentBarSong.set(Singleton.getInstance().song);
        }
    }

    @Subscriber(tag = "isPlayerPlaying")
    private void isPlayerPlaying(boolean playing){
        playing = exoPlayer.getPlayWhenReady();
    }

    @Subscriber(tag = "setPlist")
    private void setPlist(PlaylistModel plist){
        this.plist = plist;
    }

    @Subscriber(tag = "openPlayer")
    private void openPlayer(Activity activity){
        Intent intent = new Intent(activity, PlayerActivity.class);
        startActivity(intent);
    }

    @Subscriber(tag = "shufflePlaylist")
    private void shufflePlaylist(String empty){
        Collections.shuffle(songList);
        addSongsInPlaylist(songList);
        playOrPause(true);
    }

    @Subscriber(tag = "repeatPlaylist")
    private void repeatPlaylist(int mode){
        
        if(mode == Player.REPEAT_MODE_ALL)
            Toast.makeText(this, "Repeat mode on", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(this, "Repeat mode off", Toast.LENGTH_SHORT).show();
        
        exoPlayer.setRepeatMode(mode);
    }


    //
    //Exoplayer Listener
    //

    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest) {
        Log.d("exooplayer", "timeline changed");
    }

    Disposable disposable;
    boolean firstTrackChangeCatch = false;
    int timePlayed = 0;
    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
        Log.d("exooplayer", "tracks changed");

        if(disposable != null){
            if(!disposable.isDisposed())
                disposable.dispose();

            int category = 0;
            if(timePlayed < 40)
                category = 3;
            else if(timePlayed > 40 && timePlayed < (exoPlayer.getDuration()/1000)){
                category = 2;
            }
            else if(timePlayed >= (exoPlayer.getDuration()/1000))
                category = 1;

            timePlayed = 0;

            Disposable disposable = RetrofitClient.getClient().songPlayed(user.user_id, String.valueOf(Singleton.getInstance().song.getSong().songId-1), String.valueOf(category))
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableObserver<String>() {
                        @Override
                        public void onNext(String s) {
                            Log.d("songsho", "success " + s);
                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.d("songsho", "failed " + e.getMessage());
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        }

        if(firstTrackChangeCatch){
            disposable = Observable.interval(1, TimeUnit.SECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .take((Singleton.homeScreen.exoPlayer.getDuration() / 1000))
                    .map(x -> 1+x)
                    .doOnNext(new Consumer<Long>() {
                        @Override
                        public void accept(Long aLong) throws Exception {
                            Log.d("songsplayed", aLong.toString() + " secs");
                            timePlayed = aLong.intValue();
                        }
                    })
                    .doOnError(new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {

                        }
                    })
                    .subscribe();
        }

        firstTrackChangeCatch = true;
    }

    @Override
    public void onLoadingChanged(boolean isLoading) {
        Log.d("exooplayer", "loading changed");
    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        Log.d("exooplayer", "player state changed to " + String.valueOf(playbackState));
        if(playbackState == Player.STATE_ENDED){
            if(songList.size() != 0){
                EventBus.getDefault().post("final", "highlightPlayedSong");
                //EventBus.getDefault().post(false, "setFabSrc");
                EventBus.getDefault().post("","playerEndOfQueue");
                Singleton.getInstance().song = songList.get(0);
                Singleton.getInstance().isPlayerPlaying = false;
                viewModel.isPlayerPlaying.set(false);

                if(disposable != null)
                    disposable.dispose();
            }
        }
        else if(playbackState == Player.STATE_READY){
            EventBus.getDefault().post("","onNextSong");
        }

        Log.d("exooplayer", "playback State of player: " + exoPlayer.getPlaybackState());
        Log.d("exooplayer", "player State when ready: " + exoPlayer.getPlayWhenReady());

    }

    @Override
    public void onRepeatModeChanged(int repeatMode) {
        Log.d("exooplayer", "repeat mode changed to " + String.valueOf(repeatMode));
    }

    @Override
    public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {
        Log.d("exooplayer", "shuffle mode changed to: " + String.valueOf(shuffleModeEnabled));
    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {
        Log.d("exooplayer", "error: " + error.getMessage());
    }

    @Override
    public void onPositionDiscontinuity(int reason) {
        Log.d("exooplayer", "position disconuity: " + String.valueOf(reason));

        if(reason == Player.DISCONTINUITY_REASON_PERIOD_TRANSITION){
            if(windowIndex+1 < songList.size()){
                windowIndex = windowIndex +1;
                Singleton.getInstance().song = songList.get(windowIndex);
                EventBus.getDefault().post("","onNextSong");
                EventBus.getDefault().post("", "isThereAPrevious");
                EventBus.getDefault().post("", "isThereANext");
                viewModel.persistentBarSong.set(songList.get(windowIndex));
            }
            else if(exoPlayer.getRepeatMode() == 2){
                windowIndex = 0;
                Singleton.getInstance().song = songList.get(windowIndex);
                EventBus.getDefault().post("","onNextSong");
                EventBus.getDefault().post("", "isThereAPrevious");
                EventBus.getDefault().post("", "isThereANext");
                viewModel.persistentBarSong.set(songList.get(windowIndex));
            }
        }
        else if(reason == Player.DISCONTINUITY_REASON_SEEK){
            if(windowIndex <= songList.size()){
                Singleton.getInstance().song = songList.get(windowIndex);
                EventBus.getDefault().post("", "isThereAPrevious");
                EventBus.getDefault().post("", "isThereANext");
                viewModel.persistentBarSong.set(songList.get(windowIndex));
            }

        }

        EventBus.getDefault().post("", "highlightPlayedSong");

    }

    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
        Log.d("exooplayer", "playback parameters changed");
    }

    @Override
    public void onSeekProcessed() {
        Log.d("exooplayer", "seek processed");
    }

    //
    //Lifecycler Callbacks
    //

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 200:
                boolean writeAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                startActivityForResult(intent, Singleton.getInstance().PICK_PHOTO);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Singleton.getInstance().PICK_PHOTO && resultCode == Activity.RESULT_OK && data != null) {
            try {

                if(Singleton.getInstance().CHANGE_OR_ADD == 0){
                    EventBus.getDefault().post(Singleton.getInstance().utilities.getImageAbsolutePath(data.getData(), this),"changeBandPic");
                    EventBus.getDefault().post(data.getData(),"setSelectedImage");
                }
                else if(Singleton.getInstance().CHANGE_OR_ADD == 2){
                    EventBus.getDefault().post(Singleton.getInstance().utilities.getImageAbsolutePath(data.getData(), this),"changeBandPicUser");
                    EventBus.getDefault().post(data.getData(),"setSelectedImageUser");
                }
                else {
                    EventBus.getDefault().post(Singleton.getInstance().utilities.getImageAbsolutePath(data.getData(), this),"changeBandPic");
                    EventBus.getDefault().post(data.getData(),"setSelectedImage");
                    EventBus.getDefault().post("", "addBandCoverPhoto");
                }

            } catch (Exception e) {
                e.printStackTrace();
                Log.d("pick photo error", e.getMessage());
            }
        }
        else if(requestCode == Singleton.getInstance().PICK_VIDEO && resultCode == Activity.RESULT_OK && data != null){

            EventBus.getDefault().post(data.getData(), "setSelectedVideo");
            EventBus.getDefault().post(Singleton.getInstance().utilities.getImageAbsolutePath(data.getData(), this), "setVideoPath");
        }
        else if(requestCode == Singleton.getInstance().PICK_AUDIO && resultCode == Activity.RESULT_OK && data != null){
            EventBus.getDefault().post(data.getData(), "setSelectedAudio");
            EventBus.getDefault().post(Singleton.getInstance().utilities.getAudioAbsolutePath(data.getData(), this), "setAudioPath");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.searchbar_menu, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        Drawable drawable = item.getIcon();
        if(drawable != null){
            drawable.mutate();
            drawable.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        }

        item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                SearchDialogFragment dialogFragment = SearchDialogFragment.newInstance();
                dialogFragment.show(getSupportFragmentManager(), dialogFragment.getTag());
                return true;
            }
        });

        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.dispose();
        EventBus.getDefault().unregister(this);
        exoPlayer.release();
    }

}
