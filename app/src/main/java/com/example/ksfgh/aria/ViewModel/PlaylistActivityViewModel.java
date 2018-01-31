package com.example.ksfgh.aria.ViewModel;

import android.annotation.SuppressLint;
import android.databinding.BindingAdapter;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableField;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.ksfgh.aria.Adapters.PlaylistSongsAdapter;
import com.example.ksfgh.aria.Model.AlbumModel;
import com.example.ksfgh.aria.Model.BandModel;
import com.example.ksfgh.aria.Model.CustomSongModelForPlaylist;
import com.example.ksfgh.aria.Model.PlaylistModel;
import com.example.ksfgh.aria.Model.PlistModel;
import com.example.ksfgh.aria.Model.SongModel;
import com.example.ksfgh.aria.Rest.RetrofitClient;
import com.example.ksfgh.aria.Singleton;
import com.example.ksfgh.aria.View.activities.PlaylistActivity;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.DynamicConcatenatingMediaSource;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import org.simple.eventbus.EventBus;


import java.net.URL;
import java.util.ArrayList;


import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function3;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by ksfgh on 29/01/2018.
 */

public class PlaylistActivityViewModel {

    public PlaylistModel playlistModel;
    public ObservableField<String> creatorName = new ObservableField<>();
    public PlaylistActivity playlistActivity;
    private ObservableArrayList<CustomSongModelForPlaylist> playlistSongs;
    private PlaylistSongsAdapter adapter;


    public PlaylistActivityViewModel(PlaylistActivity playlistActivity) {
        this.playlistActivity = playlistActivity;
        this.playlistModel = Singleton.getInstance().currentPlaylistId;
        creatorName.set(Singleton.getInstance().utilities.findUserById(playlistModel.getPlCreator()));

        playlistSongs = new ObservableArrayList<>();

        adapter = new PlaylistSongsAdapter(playlistSongs, this);
        EventBus.getDefault().post(adapter, "setRecyclerView");
        getPlists();

    }

    @BindingAdapter("bind:imgUrl")
    public static void setImage(ImageView view, String image){
        Glide.with(view.getContext()).load(image).into(view);
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
                        EventBus.getDefault().post(playlistSongs, "addSongsInPlaylist");
                    }
                });

        EventBus.getDefault().post(disposable, "playlistDisposables");

    }

    public void dummyFunction(View view, CustomSongModelForPlaylist model){

    }

    static View currentSong;
    @BindingAdapter("bind:alreadyPlaying")
    public static void isAlreadyPlaying(View view, CustomSongModelForPlaylist model){
        if(Singleton.getInstance().song != null){
            if(model.getSong().songId == Singleton.getInstance().song.getSong().songId){
                currentSong = view;
                currentSong.setBackgroundColor(Color.parseColor("#E57C1F"));
            }
        }
    }

    @SuppressLint("ResourceAsColor")
    public void onClickSong(View view, CustomSongModelForPlaylist item){

        EventBus.getDefault().post(item, "skipSong");

        if(currentSong == null){
            currentSong = view;
            currentSong.setBackgroundColor(Color.parseColor("#E57C1F"));
        }
        else {
            currentSong.setBackgroundColor(android.R.color.transparent);
            currentSong = view;
            currentSong.setBackgroundColor(Color.parseColor("#E57C1F"));
        }

    }

    public void onOptionsClick(CustomSongModelForPlaylist item){

    }

//    public void addSongToAlbum(){
//        Intent intent = new Intent();
//        intent.setAction(Intent.ACTION_GET_CONTENT);
//        intent.setType("audio/*");
//        playlistActivity.startActivityForResult(intent, Singleton.getInstance().PICK_AUDIO);
//    }

//    public void uploadSongToAlbum(Uri audioFileUrl, String audioPath){
//
//        RequestBody albumId = RequestBody.create(MultipartBody.FORM, "2");
//        RequestBody songTitle = RequestBody.create(MultipartBody.FORM, "first song yeah");
//        RequestBody songDesc = RequestBody.create(MultipartBody.FORM, "this is a song for the lit shit");
//        RequestBody genreId = RequestBody.create(MultipartBody.FORM, "3");
//        RequestBody bandId = RequestBody.create(MultipartBody.FORM, "1");
//
//        File originalFile = new File(audioPath);
//        RequestBody filePart = RequestBody.create(MediaType.parse(playlistActivity.getContentResolver().getType(audioFileUrl)), originalFile);
//        MultipartBody.Part file = MultipartBody.Part.createFormData("song_audio", originalFile.getName(), filePart);
//
//        Disposable disposable = RetrofitClient.getClient().addSong(albumId, songTitle, songDesc, genreId, bandId, file)
//                .subscribeOn(Schedulers.newThread())
//                .subscribeWith(new DisposableObserver<SongModel>() {
//                    @Override
//                    public void onNext(SongModel songModel) {
//                        Log.d("song", songModel.songAudio);
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        Log.d("song error", e.getMessage());
//                    }
//
//                    @Override
//                    public void onComplete() {
//
//                    }
//                });
//    }

//    public void addSongToPlaylist(){
//
//        Disposable disposable = RetrofitClient.getClient().addSongToPlaylist("3", "6", "2")
//                .subscribeOn(Schedulers.newThread())
//                .subscribeWith(new DisposableObserver<ResponseBody>() {
//                    @Override
//                    public void onNext(ResponseBody responseBody) {
//                        try {
//                            Log.d("playlist", responseBody.string());
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        Log.d("playlist", e.getMessage());
//                    }
//
//                    @Override
//                    public void onComplete() {
//
//                    }
//                });
//
//    }



}
