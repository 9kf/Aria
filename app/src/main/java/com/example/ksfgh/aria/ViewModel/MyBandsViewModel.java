package com.example.ksfgh.aria.ViewModel;

import android.databinding.BindingAdapter;
import android.databinding.ObservableArrayList;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.ksfgh.aria.Adapters.MyBandsAdapter;
import com.example.ksfgh.aria.Model.AlbumModel;
import com.example.ksfgh.aria.Model.BandModel;
import com.example.ksfgh.aria.Model.CustomModelForBandPage;
import com.example.ksfgh.aria.Model.MemberModel;
import com.example.ksfgh.aria.Model.VideoModel;
import com.example.ksfgh.aria.Rest.RetrofitClient;
import com.example.ksfgh.aria.View.activities.HomeScreen;

import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function3;
import io.reactivex.functions.Function4;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;


/**
 * Created by ksfgh on 10/02/2018.
 */

public class MyBandsViewModel {

    public ObservableArrayList<CustomModelForBandPage> bandModels;
    private HomeScreen activity;

    public MyBandsViewModel(HomeScreen activity) {
        this.activity = activity;
        bandModels = new ObservableArrayList<>();
        getBands();
    }

    private void getBands() {

        Observable<BandModel[]> observable1 = RetrofitClient.getClient().getbands();
        Observable<AlbumModel[]> observable2 = RetrofitClient.getClient().getAllAlbums();
        Observable<MemberModel[]> observable3 = RetrofitClient.getClient().getBandMembers();
        Observable<ArrayList<CustomModelForBandPage>> observable = Observable.zip(observable1, observable2, observable3,
                new Function3<BandModel[], AlbumModel[], MemberModel[], ArrayList<CustomModelForBandPage>>() {
                    @Override
                    public ArrayList<CustomModelForBandPage> apply(BandModel[] bandModels, AlbumModel[] albumModels, MemberModel[] memberModels) throws Exception {
                        ArrayList<CustomModelForBandPage> bands = new ArrayList<>();

                        for(MemberModel members: memberModels){
                            //finding out what band the user belongs to
                            if(members.userId.equals(activity.user.getId())){
                                for(BandModel band: bandModels){
                                    if(members.bandId == band.bandId){
                                        //if the condition is true, the loop has already found what band the user belongs to
                                        //and now the loop will extract the band details like band members, band videos, band albums and band events

                                        ArrayList<MemberModel> bandMembers = new ArrayList<>();
                                        ArrayList<AlbumModel> bandAlbums = new ArrayList<>();

                                        //this loop will determine the members of the band
                                        for(MemberModel members2: memberModels){
                                            if(band.getBandId() == members2.bandId){
                                                bandMembers.add(members2);
                                            }
                                        }

                                        //this loop below will determine the albums of the band
                                        for(AlbumModel albums: albumModels){
                                            if(band.getBandId() == albums.getBandId()){
                                                bandAlbums.add(albums);
                                            }
                                        }

                                        bands.add(new CustomModelForBandPage(band, bandMembers, bandAlbums));

                                    }
                                }
                            }
                        }

                        return bands;
                    }
                });


        Disposable disposable = observable
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<ArrayList<CustomModelForBandPage>>() {
            @Override
            public void onNext(ArrayList<CustomModelForBandPage> customModelForBandPages) {
                bandModels.addAll(customModelForBandPages);
            }

            @Override
            public void onError(Throwable e) {
                Log.d("my bands", e.getMessage());
            }

            @Override
            public void onComplete() {
                for(CustomModelForBandPage band: bandModels){
                    Disposable disposable1 = RetrofitClient.getClient().getBandVideos(String.valueOf(band.band.bandId))
                            .subscribeOn(Schedulers.newThread())
                            .doOnNext(new Consumer<VideoModel[]>() {
                                @Override
                                public void accept(VideoModel[] videoModels) throws Exception {
                                    ArrayList<VideoModel> videoList = new ArrayList<>();
                                    for(VideoModel videos: videoModels){
                                        videoList.add(videos);
                                    }
                                    band.setVideos(videoList);
                                }
                            })
                            .doOnComplete(new Action() {
                                @Override
                                public void run() throws Exception {
                                    for (CustomModelForBandPage band:bandModels){
                                        Log.d("my bands", band.band.getBandName() + "\nAlbums: \n" );
                                        for(AlbumModel album: band.albums){
                                            Log.d("my bands", album.getAlbumName());
                                        }
                                        Log.d("my bands","\nMembers: \n" );
                                        for (MemberModel members:band.members){
                                            Log.d("my bands", members.bandrole );
                                        }
                                    }
                                }
                            })
                            .doOnError(new Consumer<Throwable>() {
                                @Override
                                public void accept(Throwable throwable) throws Exception {
                                    Log.d("my bands", throwable.getMessage());
                                }
                            })
                            .subscribe();
                }
            }
        });

    }

    @BindingAdapter("bind:list")
    public static void bindList(RecyclerView view, MyBandsViewModel viewModel){

        view.setLayoutManager(new LinearLayoutManager(view.getContext()));
        view.setAdapter(new MyBandsAdapter(viewModel));
    }

    @BindingAdapter("bind:imgUrl")
    public static void imgUrl(ImageView view, String url){

        Glide.with(view.getContext()).load(url)
                .apply(RequestOptions.overrideOf(70,70))
                .apply(RequestOptions.centerCropTransform())
                .into(view);
    }
}
