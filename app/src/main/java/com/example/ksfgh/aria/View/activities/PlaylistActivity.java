package com.example.ksfgh.aria.View.activities;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.example.ksfgh.aria.Adapters.PlaylistSongsAdapter;
import com.example.ksfgh.aria.R;
import com.example.ksfgh.aria.Singleton;
import com.example.ksfgh.aria.ViewModel.PlaylistActivityViewModel;
import com.example.ksfgh.aria.databinding.ActivityPlaylistBinding;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class PlaylistActivity extends AppCompatActivity {

    private ActivityPlaylistBinding playlistBinding;
    private PlaylistActivityViewModel playlistActivityViewModel;
    private CompositeDisposable compositeDisposable;
    private BottomSheetBehavior bottomSheetBehavior;

    //unecessary shit
    //FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        playlistBinding = DataBindingUtil.setContentView(this, R.layout.activity_playlist);

        EventBus.getDefault().register(this);
        compositeDisposable = new CompositeDisposable();

        bottomSheetBehavior = BottomSheetBehavior.from(playlistBinding.llBottomSheet);
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if(newState == BottomSheetBehavior.STATE_EXPANDED)
                    bottomSheetBehavior.setPeekHeight(520);
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        playlistActivityViewModel = new PlaylistActivityViewModel(this);
        playlistBinding.setViewmodel(playlistActivityViewModel);

        //Unecessary shits and I dont know why is this happening fml
        //fab = findViewById(R.id.playOrPause);
    }

    @Subscriber(tag = "playlistDisposables")
    private void addDisposable(Disposable disposable){
        compositeDisposable.add(disposable);
    }

    @Subscriber(tag = "setRecyclerView")
    private void setRecyclerView(PlaylistSongsAdapter adapter){
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        playlistBinding.rvPlaylistSongs.setLayoutManager(linearLayoutManager);
        playlistBinding.rvPlaylistSongs.setAdapter(adapter);
    }


//    public void setFabSrc(boolean playOrPause){
//        if(playOrPause){
//            fab.setImageResource(R.drawable.exo_controls_pause);
//        }
//        else {
//            fab.setImageResource(R.drawable.exo_controls_play);
//        }
//    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if(requestCode == Singleton.getInstance().PICK_AUDIO && resultCode == Activity.RESULT_OK && data != null){
//            Uri audioFileUrl = data.getData();
//            String audioAbsolutePath = Singleton.getInstance().utilities.getAudioAbsolutePath(audioFileUrl, this);
//            playlistActivityViewModel.uploadSongToAlbum(audioFileUrl, audioAbsolutePath);
//        }
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        EventBus.getDefault().unregister(playlistActivityViewModel);
        compositeDisposable.dispose();
    }
}
