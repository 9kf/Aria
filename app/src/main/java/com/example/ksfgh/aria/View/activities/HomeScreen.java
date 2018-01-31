package com.example.ksfgh.aria.View.activities;

import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.example.ksfgh.aria.Model.CustomSongModelForPlaylist;
import com.example.ksfgh.aria.Model.FacebookUserModel;
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

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import nl.psdcompany.duonavigationdrawer.views.DuoDrawerLayout;
import nl.psdcompany.duonavigationdrawer.widgets.DuoDrawerToggle;

public class HomeScreen extends AppCompatActivity implements Player.EventListener {

    private ActivityHomeScreenBinding activityHomeScreenBinding;
    private CompositeDisposable compositeDisposable;

    private DuoDrawerLayout duoDrawerLayout;
    private DuoDrawerToggle duoDrawerToggle;

    //Exoplayer or Music and Video Player
    private SimpleExoPlayer exoPlayer;
    private DataSource.Factory dataSourceFactory;
    private ExtractorsFactory extractorsFactory;
    private DynamicConcatenatingMediaSource dynamicConcatenatingMediaSource;
    private ArrayList<CustomSongModelForPlaylist> songList;
    private boolean isPlaying;

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
        FacebookUserModel user = new Gson().fromJson(getSharedPreferences(Singleton.getInstance().PREFERENCE_NAME, MODE_PRIVATE).getString("user", null),
                FacebookUserModel.class);
        HomeScreenViewModel viewModel = new HomeScreenViewModel(user, this, duoDrawerLayout);
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
    private void addSongsInPlaylist(ArrayList<CustomSongModelForPlaylist> songs){

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
        exoPlayer.prepare(dynamicConcatenatingMediaSource, true, true);
    }

    int windowIndex = -1;
    @Subscriber(tag = "skipSong")
    private void skipSong(CustomSongModelForPlaylist song){

        for(int i = 0; i < songList.size(); i++){
            if(songList.get(i).getSong().songId == song.getSong().songId){
                windowIndex = i;
                break;
            }
        }
        if(isPlaying == true){
            exoPlayer.seekTo(windowIndex, 180000);
            Singleton.getInstance().song = song;
        }
        else {
            exoPlayer.seekTo(windowIndex, 180000);
            exoPlayer.setPlayWhenReady(true);
            Singleton.getInstance().song = song;
            isPlaying = true;
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

        exoPlayer.prepare(dynamicConcatenatingMediaSource, true, true);
        exoPlayer.setPlayWhenReady(true);
        Singleton.getInstance().song = song;
    }

    @Subscriber(tag = "playOrPause")
    private void playOrPause(boolean play){
        exoPlayer.setPlayWhenReady(play);
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
            Log.d("exooplayer", String.valueOf(exoPlayer.getDuration()/1000));
            if(windowIndex >= songList.size())
                Singleton.getInstance().song = songList.get(windowIndex++);
        }
        else if(reason == Player.DISCONTINUITY_REASON_SEEK){
            if(windowIndex >= songList.size())
                Singleton.getInstance().song = songList.get(windowIndex);
        }
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
