package com.example.ksfgh.aria.View.activities;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableBoolean;
import android.media.session.PlaybackState;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.example.ksfgh.aria.Model.CustomSongModelForPlaylist;
import com.example.ksfgh.aria.Model.FacebookUserModel;
import com.example.ksfgh.aria.Model.PlaylistModel;
import com.example.ksfgh.aria.R;
import com.example.ksfgh.aria.Singleton;
import com.example.ksfgh.aria.ViewModel.HomeScreenViewModel;
import com.example.ksfgh.aria.databinding.ActivityHomeScreenBinding;
import com.google.android.exoplayer2.DefaultLoadControl;
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

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import nl.psdcompany.duonavigationdrawer.views.DuoDrawerLayout;
import nl.psdcompany.duonavigationdrawer.widgets.DuoDrawerToggle;

public class HomeScreen extends AppCompatActivity implements Player.EventListener {

    private ActivityHomeScreenBinding activityHomeScreenBinding;
    private CompositeDisposable compositeDisposable;
    public FacebookUserModel user;

    private DuoDrawerLayout duoDrawerLayout;
    private DuoDrawerToggle duoDrawerToggle;
    private BottomSheetBehavior bottomSheetBehavior;
    private HomeScreenViewModel viewModel;

    //Exoplayer or Music and Video Player
    public SimpleExoPlayer exoPlayer;
    private DataSource.Factory dataSourceFactory;
    private ExtractorsFactory extractorsFactory;
    private DynamicConcatenatingMediaSource dynamicConcatenatingMediaSource;
    public ArrayList<CustomSongModelForPlaylist> songList;
    public boolean isPlaying;
    private PlaylistModel plist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //set content view and data binding
        activityHomeScreenBinding = DataBindingUtil.setContentView(this, R.layout.activity_home_screen);

        //initialize disposable
        compositeDisposable = new CompositeDisposable();

        //set the toolbar
        Toolbar toolbar = activityHomeScreenBinding.toolbar;
        setSupportActionBar(toolbar);

        //register to event bus
        EventBus.getDefault().register(this);

        //assign this to the singleton for the reference of other activities
        Singleton.homeScreen = this;

        bottomSheetBehavior = BottomSheetBehavior.from(activityHomeScreenBinding.llPersistentBar);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {

            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        //initialize exoplayer
        TrackSelector trackSelector = new DefaultTrackSelector();
        LoadControl loadControl = new DefaultLoadControl();
        exoPlayer = ExoPlayerFactory.newSimpleInstance(this, trackSelector, loadControl);
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

        dynamicConcatenatingMediaSource.releaseSource();
        dynamicConcatenatingMediaSource = new DynamicConcatenatingMediaSource();
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

        exoPlayer.prepare(dynamicConcatenatingMediaSource, resetPosition, true);
        Singleton.getInstance().isPlayerPrepared = true;
    }

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
            EventBus.getDefault().post("","highlightPlayedSong");

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

        if(Singleton.getInstance().song == null || Singleton.getInstance().playedPlist != plist){
            Singleton.getInstance().song = songList.get(0);
            EventBus.getDefault().post("","highlightPlayedSong");
        }

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
        }
        else {
            exoPlayer.setPlayWhenReady(play);
            isPlaying = true;
            Singleton.getInstance().isPlayerPlaying = true;
            viewModel.isPlayerPlaying.set(true);
            Singleton.getInstance().playedPlist = plist;
        }

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
        exoPlayer.setRepeatMode(mode);
    }

    //
    //Exoplayer Listener
    //

    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest) {
        Log.d("exooplayer", "timeline changed");
    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
        Log.d("exooplayer", "tracks changed");
    }

    @Override
    public void onLoadingChanged(boolean isLoading) {
        Log.d("exooplayer", "loading changed");
    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        Log.d("exooplayer", "player state changed to " + String.valueOf(playbackState));
        if(playbackState == Player.STATE_ENDED){
            EventBus.getDefault().post("final", "highlightPlayedSong");
            EventBus.getDefault().post(false, "setFabSrc");
            EventBus.getDefault().post("","playerEndOfQueue");
            Singleton.getInstance().song = songList.get(0);
            Singleton.getInstance().isPlayerPlaying = false;
            viewModel.isPlayerPlaying.set(false);
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
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.dispose();
        EventBus.getDefault().unregister(this);
        exoPlayer.release();
    }
}
