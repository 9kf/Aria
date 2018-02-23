package com.example.ksfgh.aria.ViewModel;

import android.databinding.BindingAdapter;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableField;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.ksfgh.aria.Adapters.AlbumAdapter;
import com.example.ksfgh.aria.Adapters.BandEventsAdapter;
import com.example.ksfgh.aria.Adapters.BandMemberAdapter;
import com.example.ksfgh.aria.Adapters.BandVideosAdapter;
import com.example.ksfgh.aria.Model.AlbumModel;
import com.example.ksfgh.aria.Model.EventModel;
import com.example.ksfgh.aria.Model.FacebookUserModel;
import com.example.ksfgh.aria.Model.MemberModel;
import com.example.ksfgh.aria.Model.UserModel;
import com.example.ksfgh.aria.Model.VideoModel;
import com.example.ksfgh.aria.Rest.RetrofitClient;
import com.example.ksfgh.aria.Singleton;
import com.example.ksfgh.aria.View.activities.BandActivity;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.hrskrs.instadotlib.InstaDotView;
import com.yarolegovich.discretescrollview.DiscreteScrollView;
import com.yarolegovich.discretescrollview.transform.Pivot;
import com.yarolegovich.discretescrollview.transform.ScaleTransformer;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Callable;

import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by ksfgh on 15/02/2018.
 */

public class BandActivityViewModel {

    private BandActivity activity;
    private ArrayList<MemberModel> memberInfo;
    public ObservableArrayList<UserModel> members;
    public ObservableArrayList<AlbumModel> albums;
    public ObservableArrayList<EventModel> events;
    public ObservableArrayList<VideoModel> videos;

    public BandActivityViewModel(BandActivity activity) {
        this.activity = activity;
        members = new ObservableArrayList<>();
        albums = new ObservableArrayList<>();
        events = new ObservableArrayList<>();
        videos = new ObservableArrayList<>();
        memberInfo = new ArrayList<>();
        memberInfo.addAll(Singleton.getInstance().currentBand.members);
        albums.addAll(Singleton.getInstance().currentBand.albums);
        events.addAll(Singleton.getInstance().currentBand.events);
        videos.addAll(Singleton.getInstance().currentBand.videos);
        getMemberInfo();
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

    @BindingAdapter("bind:playerview")
    public static void setUpVideoPlayer(SimpleExoPlayerView videoPlayer, BandActivityViewModel viewModel){
        videoPlayer.setPlayer(Singleton.homeScreen.exoPlayer);
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

    public String memberInformation(String userId){
        String info = "";

        for(MemberModel model:memberInfo){
            if(model.userId.equals(userId))
                info = model.bandrole;
        }

        return info;
    }

    public void onPlayerClicked(){

    }

//    @BindingAdapter({"bind:vidPath", "bind:viewModel"})
//    public static void vidThumbnail(ImageView view, String vidPath, BandActivityViewModel viewModel){
//
//        String path = Singleton.getInstance().BASE + "Aria/public/assets/video/" + vidPath;
//
//        Observable<Bitmap> observable = Observable.fromCallable(new Callable<Bitmap>() {
//            @Override
//            public Bitmap call() throws Exception {
//                return viewModel.getVidThumbnail(vidPath);
//            }
//        });
//
//        Disposable disposable = observable
//                .subscribeOn(Schedulers.newThread())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribeWith(new DisposableObserver<Bitmap>() {
//                    @Override
//                    public void onNext(Bitmap bitmap) {
//                        if(bitmap != null)
//                            view.setImageBitmap(bitmap);
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//
//                    }
//
//                    @Override
//                    public void onComplete() {
//
//                    }
//                });
//    }
//
//    public Bitmap getVidThumbnail(String path){
//
//        Bitmap bitmap = null;
//        MediaMetadataRetriever mediaMetadataRetriever = null;
//
//        try {
//            mediaMetadataRetriever = new MediaMetadataRetriever();
//            if (Build.VERSION.SDK_INT >= 14)
//                mediaMetadataRetriever.setDataSource(path, new HashMap<String, String>());
//            else
//                mediaMetadataRetriever.setDataSource(path);
//
//            bitmap = mediaMetadataRetriever.getFrameAtTime(1, MediaMetadataRetriever.OPTION_CLOSEST);
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            if (mediaMetadataRetriever != null) {
//                mediaMetadataRetriever.release();
//            }
//        }
//
//        if(bitmap != null){
//            bitmap = Bitmap.createScaledBitmap(bitmap, 240, 240, false);
//        }
//
//        return bitmap;
//    }



}
