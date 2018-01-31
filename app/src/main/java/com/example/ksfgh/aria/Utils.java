package com.example.ksfgh.aria;

import android.database.Cursor;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableList;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.example.ksfgh.aria.Model.FacebookUserModel;
import com.example.ksfgh.aria.Model.PlaylistModel;
import com.example.ksfgh.aria.Rest.RetrofitClient;
import com.example.ksfgh.aria.View.activities.PlaylistActivity;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.upstream.DataSource;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import java.net.MalformedURLException;
import java.net.URL;

import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by ksfgh on 29/01/2018.
 */

public class Utils {

    public String findUserById(String id){
        String name = "";

        for (FacebookUserModel model: Singleton.getInstance().facebookUserModels){
            if(id.equals(model.getId())){
                name = model.getFname() + " " + model.getLname();
                break;
            }
        }
        return name;
    }

    public String getAudioAbsolutePath(Uri audioFileUrl, PlaylistActivity playlistActivity){
        String[] audioPathColumn = {MediaStore.Audio.Media.DATA};
        Cursor audioCursor = playlistActivity.getContentResolver().query(audioFileUrl, audioPathColumn, null, null, null);
        audioCursor.moveToFirst();
        int audioColumnIndex = audioCursor.getColumnIndex(audioPathColumn[0]);
        String audioPath = audioCursor.getString(audioColumnIndex);

        return audioPath;
    }

    public URL buildAudioURL(String base, String path){

        Uri uri = Uri.parse(base)
                .buildUpon()
                .path("Aria/public/assets/music/"+path)
                .build();

        try {
            return new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public MediaSource createMediaSource(String song, DataSource.Factory dataSourceFactory, ExtractorsFactory extractorsFactory){
        return new ExtractorMediaSource(Uri.parse(song), dataSourceFactory, extractorsFactory, null, null);
    }
}
