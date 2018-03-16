package com.example.ksfgh.aria.ViewModel;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.media.session.PlaybackState;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.ksfgh.aria.Adapters.AlbumAdapter;
import com.example.ksfgh.aria.Adapters.AlbumSongsAdapter;
import com.example.ksfgh.aria.Adapters.BandEventsAdapter;
import com.example.ksfgh.aria.Adapters.BandMemberAdapter;
import com.example.ksfgh.aria.Adapters.BandVideosAdapter;
import com.example.ksfgh.aria.Model.AlbumModel;
import com.example.ksfgh.aria.Model.CustomModelForAlbum;
import com.example.ksfgh.aria.Model.CustomSongModelForPlaylist;
import com.example.ksfgh.aria.Model.EventModel;
import com.example.ksfgh.aria.Model.FacebookUserModel;
import com.example.ksfgh.aria.Model.MemberModel;
import com.example.ksfgh.aria.Model.PlaylistModel;
import com.example.ksfgh.aria.Model.PreferenceModel;
import com.example.ksfgh.aria.Model.SongModel;
import com.example.ksfgh.aria.Model.UserModel;
import com.example.ksfgh.aria.Model.VideoModel;
import com.example.ksfgh.aria.R;
import com.example.ksfgh.aria.Rest.RetrofitClient;
import com.example.ksfgh.aria.Singleton;
import com.example.ksfgh.aria.View.activities.BandActivity;
import com.example.ksfgh.aria.databinding.AddSongToPlaylistBinding;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.hrskrs.instadotlib.InstaDotView;
import com.yarolegovich.discretescrollview.DiscreteScrollView;
import com.yarolegovich.discretescrollview.transform.Pivot;
import com.yarolegovich.discretescrollview.transform.ScaleTransformer;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.Url;

/**
 * Created by ksfgh on 15/02/2018.
 */

public class BandActivityViewModel implements Player.EventListener {

    private BandActivity activity;
    private ArrayList<MemberModel> memberInfo;
    public ArrayList<SimpleExoPlayer> exoPlayers;
    private ArrayList<PreferenceModel> userPreferences;
    public ObservableArrayList<UserModel> members;
    public ObservableArrayList<AlbumModel> albums;
    public ObservableArrayList<EventModel> events;
    public ObservableArrayList<VideoModel> videos;
    public ObservableArrayList<CustomModelForAlbum> albumSongs;
    public ObservableField<AlbumModel>  selectedAlbum;
    public ObservableArrayList<CustomSongModelForPlaylist> selectedAlbumSongs;
    public ObservableArrayList<View> albumViews;
    private BottomSheetBehavior bottomSheetBehavior;
    public View currentView;
    public TextView currentTextView;
    public ObservableBoolean isFollowing;
    public ObservableInt bandFollowers;
    public ObservableBoolean isAlbumLiked;

    public BandActivityViewModel(BandActivity activity) {
        this.activity = activity;
        EventBus.getDefault().register(this);
        members = new ObservableArrayList<>();
        albums = new ObservableArrayList<>();
        events = new ObservableArrayList<>();
        videos = new ObservableArrayList<>();
        albumSongs = new ObservableArrayList<>();
        selectedAlbum = new ObservableField<>();
        selectedAlbumSongs = new ObservableArrayList<>();
        albumViews = new ObservableArrayList<>();
        isFollowing = new ObservableBoolean();
        bandFollowers = new ObservableInt();
        bandFollowers.set(Singleton.getInstance().currentBand.band.getNumFollowers());
        isAlbumLiked = new ObservableBoolean();
        memberInfo = new ArrayList<>();
        exoPlayers = new ArrayList<>();
        userPreferences = new ArrayList<>();
        memberInfo.addAll(Singleton.getInstance().currentBand.members);
        albums.addAll(Singleton.getInstance().currentBand.albums);
        events.addAll(Singleton.getInstance().currentBand.events);
        videos.addAll(Singleton.getInstance().currentBand.videos);
        for(VideoModel videoModel: videos){
            exoPlayers.add(ExoPlayerFactory.newSimpleInstance(new DefaultRenderersFactory(activity), new DefaultTrackSelector(), new DefaultLoadControl()));
        }
        for(SimpleExoPlayer exoPlayer: exoPlayers){
            exoPlayer.addListener(this);
        }
        getMemberInfo();
        getAlbumSongs();
        getIsFollowing();
        if(Singleton.getInstance().userPlaylists.size() == 0)
            getUserPlaylist();

        visitPage();
    }

    private void visitPage() {
        Disposable disposable = RetrofitClient.getClient().visitPage(String.valueOf(Singleton.getInstance().currentBand.band.bandId))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<String>() {
                    @Override
                    public void onNext(String aBoolean) {
                        Log.d("visit", "success");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("visit", "error kay" + e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                        Disposable disposable1 = RetrofitClient.getClient().scoringFunc()
                                .subscribeOn(Schedulers.newThread())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribeWith(new DisposableObserver<ResponseBody>() {
                                    @Override
                                    public void onNext(ResponseBody responseBody) {
                                        Log.d("scoring", "success");
                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        Log.d("scoring", "failed kay" + e.getMessage());
                                    }

                                    @Override
                                    public void onComplete() {

                                    }
                                });
                    }
                });
    }

    private void getIsFollowing() {
        Disposable disposable = RetrofitClient.getClient().getUserPreferences(Singleton.homeScreen.user.user_id)
                .subscribeOn(Schedulers.newThread())
                .subscribeWith(new DisposableObserver<PreferenceModel[]>() {
                    @Override
                    public void onNext(PreferenceModel[] preferenceModel) {
                        for(PreferenceModel model:preferenceModel){

                            if(model.bandId != null){
                                if(Integer.parseInt(model.bandId) == Singleton.getInstance().currentBand.band.bandId){
                                    isFollowing.set(true);
                                }
                                else {
                                    isFollowing.set(false);
                                }
                            }

                            if(model.userId.equals(Singleton.homeScreen.user.getId()))
                                userPreferences.add(model);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("followshit", e.getMessage() + " ");
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void getUserPlaylist() {
        Disposable disposable = RetrofitClient.getClient().getPlaylists()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<PlaylistModel[]>() {
                    @Override
                    public void onNext(PlaylistModel[] playlistModels) {
                        for(PlaylistModel model:playlistModels){
                            if(model.getPlCreator().equals(Singleton.homeScreen.user.getId())){
                                Singleton.getInstance().userPlaylists.add(model);
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("userSection", e.getMessage() + " ");
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @BindingAdapter("bind:memberAdapter")
    public static void memberAdapter(RecyclerView view, BandActivityViewModel viewModel){
        view.setLayoutManager(new LinearLayoutManager(view.getContext()){
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        view.setAdapter(new BandMemberAdapter(viewModel));
    }

    @BindingAdapter("bind:albumAdapter")
    public static void albumAdapter(RecyclerView view, BandActivityViewModel viewModel){
        view.setLayoutManager(new LinearLayoutManager(view.getContext(), LinearLayoutManager.HORIZONTAL, false));
        view.setAdapter(new AlbumAdapter(viewModel));
    }

    InstaDotView dotEvents;
    @BindingAdapter("bind:eventDots")
    public static void eventDots(InstaDotView view, BandActivityViewModel viewModel){
        view.setNoOfPages(viewModel.events.size());
        view.onPageChange(0);
        view.setVisibleDotCounts(6);
        viewModel.dotEvents = view;
    }

    InstaDotView dotVids;
    @BindingAdapter("bind:videoDots")
    public static void videoDots(InstaDotView view, BandActivityViewModel viewModel){
        view.setNoOfPages(viewModel.videos.size());
        view.onPageChange(0);
        view.setVisibleDotCounts(6);
        viewModel.dotVids = view;
    }

    @BindingAdapter("bind:videosAdapter")
    public static void videosAdapter(DiscreteScrollView view, BandActivityViewModel viewModel){
        view.setAdapter(new BandVideosAdapter(viewModel));
        view.setSlideOnFling(true);
        view.setOverScrollEnabled(false);
        view.setItemTransformer(new ScaleTransformer.Builder()
                .setMinScale(0.8f)
                .build());
        view.addOnItemChangedListener(new DiscreteScrollView.OnItemChangedListener<RecyclerView.ViewHolder>() {
            @Override
            public void onCurrentItemChanged(@Nullable RecyclerView.ViewHolder viewHolder, int adapterPosition) {
                viewModel.dotVids.onPageChange(adapterPosition);
            }
        });
    }

    @BindingAdapter("bind:eventAdapter")
    public static void eventAdapter(DiscreteScrollView view, BandActivityViewModel viewModel){
        view.setAdapter(new BandEventsAdapter(viewModel));
        view.setSlideOnFling(true);
        view.setOverScrollEnabled(false);
        view.setItemTransformer(new ScaleTransformer.Builder()
        .setMinScale(0.8f)
        .build());
        view.addOnItemChangedListener(new DiscreteScrollView.OnItemChangedListener<RecyclerView.ViewHolder>() {
            @Override
            public void onCurrentItemChanged(@Nullable RecyclerView.ViewHolder viewHolder, int adapterPosition) {
                viewModel.dotEvents.onPageChange(adapterPosition);
            }
        });
    }

    @BindingAdapter("bind:selectedAlbumSongsAdapter")
    public static void selectedAlbumSongsAdapter(RecyclerView view, BandActivityViewModel viewModel){
        view.setLayoutManager(new LinearLayoutManager(view.getContext(), LinearLayoutManager.VERTICAL, false));
        view.setAdapter(new AlbumSongsAdapter(viewModel));
    }

    @BindingAdapter({"bind:playerview", "bind:playerModel"})
    public static void setUpVideoPlayer(SimpleExoPlayerView videoPlayer, BandActivityViewModel viewModel, VideoModel model){
        for(int i = 0; i < viewModel.videos.size(); i++){
            if(viewModel.videos.get(i).videoId == model.videoId){
                videoPlayer.setPlayer(viewModel.exoPlayers.get(i));
                MediaSource mediaSource = Singleton.getInstance().utilities.createMediaSource(
                        Singleton.getInstance().utilities.buildVideoUrl(Singleton.getInstance().BASE, model.videoContent).toString(),
                        Singleton.homeScreen.dataSourceFactory,
                        Singleton.homeScreen.extractorsFactory
                );
                viewModel.exoPlayers.get(i).prepare(mediaSource, true, true);
                viewModel.exoPlayers.get(i).setRepeatMode(Player.REPEAT_MODE_ONE);
                break;
            }
        }
    }

    @BindingAdapter("bind:bandBottomSheet")
    public static void initBottomSheet(LinearLayout view, BandActivityViewModel viewModel){
        viewModel.bottomSheetBehavior = BottomSheetBehavior.from(view);
        viewModel.bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
    }

    public void addViews(View view){
        albumViews.add(view);
    }

    private void getMemberInfo() {

        Disposable disposable = RetrofitClient.getClient().getUsers2()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<UserModel[]>() {
                    @Override
                    public void onNext(UserModel[] userModels) {
                        for(UserModel model: userModels){
                            for(MemberModel membersz:Singleton.getInstance().currentBand.members){
                                if(membersz.userId.equals(model.userId)){
                                    members.add(model);
                                }
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });

        activity.addDisposables(disposable);

    }

    public void getAlbumSongs() {

        for(AlbumModel model: albums){

            Disposable disposable = RetrofitClient.getClient().getAlbumSongs(String.valueOf(model.getAlbumId()))
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableObserver<CustomModelForAlbum>() {
                        @Override
                        public void onNext(CustomModelForAlbum customModelForAlbum) {
                            albumSongs.add(customModelForAlbum);
                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        }
    }

    public String memberInformation(String userId){
        String info = "";

        for(MemberModel model:memberInfo){
            if(model.userId.equals(userId))
                info = model.bandrole;
        }

        return info;
    }

    public void goBack(){
        activity.finish();
    }

    public void albumClicked(AlbumModel albumModel){

        selectedAlbum.set(albumModel);
        albumViews.clear();
        isAlbumLiked.set(false);

        if(userPreferences.size() != 0){
            for(PreferenceModel model:userPreferences){
                if(model.albumId != null){
                    if(albumModel.getAlbumId() == Integer.parseInt(model.albumId)){
                        isAlbumLiked.set(true);
                        break;
                    }
                }
            }
        }

        for(CustomModelForAlbum customModelForAlbum:albumSongs){
           if(customModelForAlbum.album.getAlbumId() == selectedAlbum.get().getAlbumId()){

               selectedAlbumSongs.clear();
               for(SongModel song: customModelForAlbum.songs){
                   selectedAlbumSongs.add(new CustomSongModelForPlaylist(song, Singleton.getInstance().currentBand.band, selectedAlbum.get()));
               }
               break;
           }
        }

        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    @Subscriber(tag = "getSongsInAlbum")
    public void getSongsInAlbum(CustomSongModelForPlaylist song){

        EventBus.getDefault().post(selectedAlbumSongs, "addSongsInPlaylist");
        EventBus.getDefault().post(song, "skipSong2");
//        Disposable disposable = Observable.interval(1, TimeUnit.SECONDS)
//                .observeOn(AndroidSchedulers.mainThread())
//                .take(5)
//                .map(x -> 1+x)
//                .doOnNext(new Consumer<Long>() {
//                    @Override
//                    public void accept(Long aLong) throws Exception {
//
//                        if(aLong.intValue() == 2){
//                            EventBus.getDefault().post(song, "skipSong2");
//                        }
//                    }
//                })
//                .doOnError(new Consumer<Throwable>() {
//                    @Override
//                    public void accept(Throwable throwable) throws Exception {
//
//                    }
//                })
//                .subscribe();

    }

    @Subscriber(tag = "highlightPlayedSong")
    public void highlightNextSong(String empty){
        if(currentView != null && currentTextView != null){
            if(empty.equals("final")){
                currentView.setBackgroundColor(Color.parseColor("#161616"));
                currentTextView.setTextColor(Color.parseColor("#FFFFFF"));
                currentView = null;
                currentTextView = null;
            }
            else {
                currentView.setBackgroundColor(Color.parseColor("#161616"));
                currentTextView.setTextColor(Color.parseColor("#FFFFFF"));

                for(int i = 0; i < selectedAlbumSongs.size(); i++){
                    if(Singleton.getInstance().song.getSong().songId == selectedAlbumSongs.get(i).getSong().songId){
                        currentView = albumViews.get(i);
                        currentTextView = currentView.findViewById(R.id.tvAlbumSongTitle);
                        currentView.setBackgroundColor(Color.parseColor("#000000"));
                        currentTextView.setTextColor(Color.parseColor("#E57C1F"));
                        break;
                    }
                }
            }
        }
    }

    public void songClicked(View view, CustomSongModelForPlaylist song){

        if(currentView == null && currentTextView == null){
            if(Singleton.getInstance().isPlayerPlaying == false){
                currentView = view;
                currentView.setBackgroundColor(Color.parseColor("#000000"));
                currentTextView = currentView.findViewById(R.id.tvAlbumSongTitle);
                currentTextView.setTextColor(Color.parseColor("#E57C1F"));
            }
        }
        else {
            currentView.setBackgroundColor(Color.parseColor("#161616"));
            currentTextView.setTextColor(Color.parseColor("#FFFFFF"));
            currentView = view;
            currentView.setBackgroundColor(Color.parseColor("#000000"));
            currentTextView = currentView.findViewById(R.id.tvAlbumSongTitle);
            currentTextView.setTextColor(Color.parseColor("#E57C1F"));
        }

        EventBus.getDefault().post(song, "skipSong2");

    }

    public void songOptionsClicked(View view, CustomSongModelForPlaylist song){

        AddSongToPlaylistBinding binding = DataBindingUtil.inflate(activity.getLayoutInflater(), R.layout.dialog_user_playlists, null, false);
        binding.setViewmodel(this);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity, R.style.BlackAlertDialog);
        alertDialogBuilder.setView(binding.getRoot());

        AlertDialog dialog = alertDialogBuilder.create();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        ArrayList<String> userPlaylists = new ArrayList<>();
        for(PlaylistModel playlistModel : Singleton.getInstance().userPlaylists){
            userPlaylists.add(playlistModel.getPlTitle());
        }
        binding.lvUserPlaylists.setAdapter(new ArrayAdapter<String>(activity, android.R.layout.simple_list_item_1, android.R.id.text1, userPlaylists){
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view1 =  super.getView(position, convertView, parent);
                TextView text = (TextView) view1.findViewById(android.R.id.text1);
                text.setTextColor(Color.WHITE);
                return view1;
            }
        });
        binding.lvUserPlaylists.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                dialog.dismiss();
                Toast.makeText(activity, "Adding song to the playlist...", Toast.LENGTH_SHORT).show();
                Disposable disposable = RetrofitClient.getClient().addSongToPlaylist(song.getSong().genreId, String.valueOf(song.getSong().songId), String.valueOf(Singleton.getInstance().userPlaylists.get(i).getPlId()))
                .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<ResponseBody>() {
                    @Override
                    public void onNext(ResponseBody responseBody) {
                        Toast.makeText(activity, "Successfully added", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(activity, "There was an error adding the song to the playlist", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
            }
        });

        PopupMenu popupMenu = new PopupMenu(Singleton.homeScreen, view);
        popupMenu.getMenuInflater().inflate(R.menu.song_popup_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.itmAddToPlaylist:
                        dialog.show();
                        break;
//                    case R.id.itmAddToQueue:
//
//                        break;

                }

                return true;
            }
        });
        popupMenu.show();

    }

    public void followBand(){
        if(isFollowing.get()){
            Disposable disposable = RetrofitClient.getClient().unFollowBand(Singleton.homeScreen.user.user_id, String.valueOf(Singleton.getInstance().currentBand.band.bandId))
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableObserver<Integer>() {
                        @Override
                        public void onNext(Integer responseBody) {
                            isFollowing.set(false);
                            bandFollowers.set(responseBody);
                            Singleton.getInstance().currentBand.band.setNumFollowers(responseBody);
                            Log.d("follow", "success");
                        }

                        @Override
                        public void onError(Throwable e) {

                            Log.d("follow", "success" + e.getMessage());
                        }

                        @Override
                        public void onComplete() {
                            Disposable disposable1 = RetrofitClient.getClient().scoringFunc()
                                    .subscribeOn(Schedulers.newThread())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribeWith(new DisposableObserver<ResponseBody>() {
                                        @Override
                                        public void onNext(ResponseBody responseBody) {
                                            Log.d("scoring", "success");
                                        }

                                        @Override
                                        public void onError(Throwable e) {
                                            Log.d("scoring", "failed kay" + e.getMessage());
                                        }

                                        @Override
                                        public void onComplete() {

                                        }
                                    });
                        }
                    });
        }
        else {
            Disposable disposable = RetrofitClient.getClient().followBand(Singleton.homeScreen.user.user_id, String.valueOf(Singleton.getInstance().currentBand.band.bandId))
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableObserver<Integer>() {
                        @Override
                        public void onNext(Integer responseBody) {
                            isFollowing.set(true);
                            bandFollowers.set(responseBody);
                            Singleton.getInstance().currentBand.band.setNumFollowers(responseBody);
                            Log.d("follow", "success");
                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.d("follow", "success" + e.getMessage());
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        }
    }

    public void likeAlbum(){
        Disposable disposable;
        if(isAlbumLiked.get()){
            disposable = RetrofitClient.getClient().unLikeAlbum(Singleton.homeScreen.user.user_id, String.valueOf(selectedAlbum.get().getAlbumId()))
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableObserver<ResponseBody>() {
                        @Override
                        public void onNext(ResponseBody integer) {
                            Log.d("follow", "success");
                            isAlbumLiked.set(false);
                            for(PreferenceModel model:userPreferences){
                                if(model.albumId != null){
                                    if(model.albumId.equals(String.valueOf(selectedAlbum.get().getAlbumId()))){
                                        userPreferences.remove(model);
                                        break;
                                    }
                                }
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.d("follow", e.getMessage() + " ");
                        }

                        @Override
                        public void onComplete() {
                            Disposable disposable1 = RetrofitClient.getClient().scoringFunc()
                                    .subscribeOn(Schedulers.newThread())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribeWith(new DisposableObserver<ResponseBody>() {
                                        @Override
                                        public void onNext(ResponseBody responseBody) {
                                            Log.d("scoring", "success");
                                        }

                                        @Override
                                        public void onError(Throwable e) {
                                            Log.d("scoring", "failed kay" + e.getMessage());
                                        }

                                        @Override
                                        public void onComplete() {

                                        }
                                    });
                        }
                    });
        }
        else {
            disposable = RetrofitClient.getClient().likeAlbum(Singleton.homeScreen.user.user_id, String.valueOf(selectedAlbum.get().getAlbumId()))
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableObserver<PreferenceModel>() {
                        @Override
                        public void onNext(PreferenceModel integer) {
                            isAlbumLiked.set(true);
                            userPreferences.add(integer);
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
    }



    //Video Listener//

    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest) {
        Log.d("exovid", "timeline changed");
    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
        Log.d("exovid", "tracks changed");
    }

    @Override
    public void onLoadingChanged(boolean isLoading) {
        Log.d("exovid", "loading changed");
    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        Log.d("exovid", "player state changed to " + String.valueOf(playbackState) + "\nplaywhenready: " + playWhenReady);

        if(playWhenReady){
            EventBus.getDefault().post(false,"playOrPause");
            Singleton.getInstance().videoPlayed = true;
        }

        if(playbackState == Player.STATE_ENDED)
            Singleton.getInstance().videoPlayed = false;
    }

    @Override
    public void onRepeatModeChanged(int repeatMode) {

    }

    @Override
    public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {
        Log.d("exovid", "error: " + error.getMessage());
    }

    @Override
    public void onPositionDiscontinuity(int reason) {
        Log.d("exovid", "position disconuity: " + String.valueOf(reason));
    }

    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
        Log.d("exovid", "playback parameters changed");
    }

    @Override
    public void onSeekProcessed() {
        Log.d("exovid", "seek processed");
    }
}
