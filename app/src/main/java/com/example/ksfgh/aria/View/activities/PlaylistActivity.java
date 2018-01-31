package com.example.ksfgh.aria.View.activities;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.generated.callback.OnClickListener;
import android.net.Uri;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        playlistBinding = DataBindingUtil.setContentView(this, R.layout.activity_playlist);

        EventBus.getDefault().register(this);
        compositeDisposable = new CompositeDisposable();

        Toolbar toolbar = playlistBinding.toolbar;
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        playlistActivityViewModel = new PlaylistActivityViewModel(this);
        playlistBinding.setViewmodel(playlistActivityViewModel);
    }

    @Subscriber(tag = "playlistDisposables")
    private void addDisposable(Disposable disposable){
        compositeDisposable.add(disposable);
    }

    @Subscriber(tag = "setRecyclerView")
    private void setRecyclerView(PlaylistSongsAdapter adapter){
        playlistBinding.rvPlaylistSongs.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        playlistBinding.rvPlaylistSongs.setAdapter(adapter);
    }

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
        compositeDisposable.dispose();
    }
}
