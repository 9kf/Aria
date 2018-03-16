package com.example.ksfgh.aria.View.fragments;


import android.annotation.SuppressLint;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableBoolean;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ksfgh.aria.Adapters.FeedAdapter;
import com.example.ksfgh.aria.Model.AlbumModel;
import com.example.ksfgh.aria.Model.BandModel;
import com.example.ksfgh.aria.Model.CustomModelForBandPage;
import com.example.ksfgh.aria.Model.EventModel;
import com.example.ksfgh.aria.Model.MemberModel;
import com.example.ksfgh.aria.Model.PreferenceModel;
import com.example.ksfgh.aria.Model.UserModel;
import com.example.ksfgh.aria.Model.VideoModel;
import com.example.ksfgh.aria.R;
import com.example.ksfgh.aria.Rest.RetrofitClient;
import com.example.ksfgh.aria.Singleton;
import com.example.ksfgh.aria.databinding.FragmentFeedBinding;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Function3;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * A simple {@link Fragment} subclass.
 */
public class FeedFragment extends Fragment {


    public FeedFragment() {
        // Required empty public constructor
    }

    private FragmentFeedBinding fragmentFeedBinding;
    public ArrayList<CustomModelForBandPage> userFollowedBands;
    public ObservableArrayList<EventModel> feedList;
    private Date currentDate;
    private Date leastDate;

    @SuppressLint("ResourceAsColor")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentFeedBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_feed, container, false);
        userFollowedBands = new ArrayList<>();
        feedList = new ObservableArrayList<>();
        currentDate = Calendar.getInstance().getTime();

        fragmentFeedBinding.refreshFeed.setProgressBackgroundColorSchemeColor(Color.parseColor("#000000"));
        fragmentFeedBinding.refreshFeed.setColorSchemeColors(Color.parseColor("#E57C1F"));
        fragmentFeedBinding.refreshFeed.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fragmentFeedBinding.refreshFeed.setRefreshing(true);
                userFollowedBands.clear();
                getUserFollowedBands();
            }
        });
        fragmentFeedBinding.rvFeed.setLayoutManager(new LinearLayoutManager(fragmentFeedBinding.rvFeed.getContext(), LinearLayoutManager.VERTICAL, false));
        fragmentFeedBinding.rvFeed.setAdapter(new FeedAdapter(this));

        getUserFollowedBands();
;        return fragmentFeedBinding.getRoot();
    }

    private void getUserFollowedBands() {
        Observable<PreferenceModel[]> prefs = RetrofitClient.getClient().getUserPreferences(Singleton.homeScreen.user.user_id);
        Observable<BandModel[]> bands = RetrofitClient.getClient().getbands();
        Observable<ArrayList<BandModel>> observable = Observable.zip(prefs, bands, new BiFunction<PreferenceModel[], BandModel[], ArrayList<BandModel>>() {
            @Override
            public ArrayList<BandModel> apply(PreferenceModel[] preferenceModels, BandModel[] bandModels) throws Exception {
                ArrayList<BandModel> list = new ArrayList<>();
                for(PreferenceModel preferenceModel: preferenceModels){
                    for(BandModel bandModel: bandModels){
                        if(preferenceModel.bandId != null){
                            if(preferenceModel.bandId.equals(String.valueOf(bandModel.bandId))&& preferenceModel.userId.equals(Singleton.homeScreen.user.user_id)){
                                list.add(bandModel);
                            }
                        }
                    }
                }
                return list;
            }
        });


        Disposable disposable = observable
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<ArrayList<BandModel>>() {
                    @Override
                    public void onNext(ArrayList<BandModel> bandModels) {
                        getBandDetails(bandModels);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void getBandDetails(ArrayList<BandModel> bandModels) {
        Observable<AlbumModel[]> observable2 = RetrofitClient.getClient().getAllAlbums();
        Observable<MemberModel[]> observable3 = RetrofitClient.getClient().getBandMembers();
        Observable<EventModel[]> observable4 = RetrofitClient.getClient().getEvents();
        Observable<ArrayList<CustomModelForBandPage>> observable = Observable.zip(observable2, observable3, observable4, new Function3<AlbumModel[], MemberModel[], EventModel[], ArrayList<CustomModelForBandPage>>() {
            @Override
            public ArrayList<CustomModelForBandPage> apply(AlbumModel[] albumModels, MemberModel[] memberModels, EventModel[] eventModels) throws Exception {
                ArrayList<CustomModelForBandPage> customModelForBandPages = new ArrayList<>();

                for(BandModel bandModel: bandModels){

                    ArrayList<MemberModel> bandMembers = new ArrayList<>();
                    ArrayList<AlbumModel> bandAlbums = new ArrayList<>();
                    ArrayList<EventModel> bandEvents = new ArrayList<>();

                    //this loop will determine the members of the band
                    for(MemberModel members2: memberModels){
                        if(bandModel.getBandId() == members2.bandId){
                            bandMembers.add(members2);
                        }
                    }

                    //this loop below will determine the albums of the band
                    for(AlbumModel albums: albumModels){
                        if(bandModel.getBandId() == albums.getBandId()){
                            bandAlbums.add(albums);
                        }
                    }

                    //this loop below will determine the events of the band
                    for(EventModel events: eventModels){
                        if(bandModel.getBandId() == events.bandId){
                            bandEvents.add(events);
                        }
                    }

                    customModelForBandPages.add(new CustomModelForBandPage(bandModel, bandMembers, bandAlbums, bandEvents));

                }

                return customModelForBandPages;
            }
        });


        Disposable disposable = observable
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<ArrayList<CustomModelForBandPage>>() {
                    @Override
                    public void onNext(ArrayList<CustomModelForBandPage> customModelForBandPages) {
                        userFollowedBands.addAll(customModelForBandPages);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        for(CustomModelForBandPage model:userFollowedBands){
                            Disposable disposable1 = RetrofitClient.getClient().getBandVideos(String.valueOf(model.band.bandId))
                                    .subscribeOn(Schedulers.newThread())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribeWith(new DisposableObserver<VideoModel[]>() {
                                        @Override
                                        public void onNext(VideoModel[] videoModels) {
                                            ArrayList<VideoModel> videoList = new ArrayList<>();
                                            for(VideoModel videos: videoModels){
                                                videoList.add(videos);
                                            }
                                            model.setVideos(videoList);
                                        }

                                        @Override
                                        public void onError(Throwable e) {

                                        }

                                        @Override
                                        public void onComplete() {
                                            fragmentFeedBinding.refreshFeed.setRefreshing(false);
                                            getFeed();
                                        }
                                    });
                        }
                    }
                });
    }

    private void getFeed(){
        feedList.clear();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        for(CustomModelForBandPage band:userFollowedBands){

            Collections.sort(band.events, new Comparator<EventModel>() {
                @Override
                public int compare(EventModel eventModel, EventModel t1) {
                    try {
                        return simpleDateFormat.parse(t1.createdAt).compareTo(simpleDateFormat.parse(eventModel.createdAt));
                    } catch (ParseException e) {
                        e.printStackTrace();
                        return -2;
                    }
                }
            });

            Collections.sort(band.albums, new Comparator<AlbumModel>() {
                @Override
                public int compare(AlbumModel albumModel, AlbumModel t1) {
                    try {
                        return simpleDateFormat.parse(t1.getCreatedAt()).compareTo(simpleDateFormat.parse(albumModel.getCreatedAt()));
                    } catch (ParseException e) {
                        e.printStackTrace();
                        return -2;
                    }
                }
            });

            Collections.sort(band.members, new Comparator<MemberModel>() {
                @Override
                public int compare(MemberModel memberModel, MemberModel t1) {
                    try {
                        return simpleDateFormat.parse(t1.createdAt).compareTo(simpleDateFormat.parse(memberModel.createdAt));
                    } catch (ParseException e) {
                        e.printStackTrace();
                        return -2;
                    }
                }
            });

            feedList.addAll(band.events);
            Log.d("feed", feedList.size() + " size");
//            feedList.addAll(band.albums);
//            feedList.addAll(band.members);

//            for(Object object: feedList){
//                    if(object instanceof EventModel){
//                        Log.d("feed",((EventModel)object).eventName + " event" );
//                    }
//                    else if(object instanceof MemberModel){
//                        Log.d("feed",((MemberModel)object).bandrole + " role" );
//                    }
//                    else if(object instanceof AlbumModel){
//                        Log.d("feed", ((AlbumModel)object).getAlbumName() + " album");
//                    }
//            }


        }
    }

//    private void getFeed(){
//        Log.d("feed", "starting to get feed");
//        feedList.clear();
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
//        Calendar c = Calendar.getInstance();
//        Calendar c1 = Calendar.getInstance();
//        c1.setTime(currentDate);
//
//        for(CustomModelForBandPage band:userFollowedBands){
//            //for getting events
//            for(EventModel event:band.events){
//                try {
//                    Date eventDate = simpleDateFormat.parse(event.createdAt);
//
//                    if(leastDate == null)
//                        leastDate =eventDate;
//                    else if(leastDate.compareTo(eventDate) == 1 || leastDate.compareTo(eventDate) == 0){
//                        leastDate = eventDate;
//                    }
//
//                    c.setTime(eventDate);
//                    if(c.get(Calendar.YEAR) == c1.get(Calendar.YEAR)){
//                        if(c.get(Calendar.MONTH) == c1.get(Calendar.MONTH)){
//                            if((c.get(Calendar.WEEK_OF_MONTH)) == c1.get(Calendar.WEEK_OF_MONTH) || c.get(Calendar.DAY_OF_MONTH) <= c1.get(Calendar.DAY_OF_MONTH)){
//                                feedList.add(event);
//                            }
//                        }
//                    }
//                } catch (ParseException e) {
//                    e.printStackTrace();
//                }
//            }
//
//
//            //for getting albums
//            for(AlbumModel albumModel:band.albums){
//                try {
//                    Date albumDate = simpleDateFormat.parse(albumModel.getCreatedAt());
//
//                    if(leastDate == null)
//                        leastDate =albumDate;
//                    else if(leastDate.compareTo(albumDate) == 1 || leastDate.compareTo(albumDate) == 0){
//                        leastDate = albumDate;
//                    }
//
//                    c.setTime(albumDate);
//                    if(c.get(Calendar.YEAR) == c1.get(Calendar.YEAR)){
//                        if(c.get(Calendar.MONTH) == c1.get(Calendar.MONTH)){
//                            if((c.get(Calendar.WEEK_OF_MONTH)) == c1.get(Calendar.WEEK_OF_MONTH) || c.get(Calendar.DAY_OF_WEEK_IN_MONTH) <= c1.get(Calendar.DAY_OF_WEEK_IN_MONTH)){
//                                feedList.add(albumModel);
//                            }
//                        }
//                    }
//                } catch (ParseException e) {
//                    e.printStackTrace();
//                }
//            }
//
//            //for getting band members
//            for(MemberModel memberModel:band.members){
//                try {
//                    Date memberDate = simpleDateFormat.parse(memberModel.createdAt);
//
//                    if(leastDate == null)
//                        leastDate =memberDate;
//                    else if(leastDate.compareTo(memberDate) == 1 || leastDate.compareTo(memberDate) == 0){
//                        leastDate = memberDate;
//                    }
//
//                    c.setTime(memberDate);
//                    if(c.get(Calendar.YEAR) == c1.get(Calendar.YEAR)){
//                        if(c.get(Calendar.MONTH) == c1.get(Calendar.MONTH)){
//                            if((c.get(Calendar.WEEK_OF_MONTH)) == c1.get(Calendar.WEEK_OF_MONTH) || c.get(Calendar.DAY_OF_WEEK_IN_MONTH) <= c1.get(Calendar.DAY_OF_WEEK_IN_MONTH)){
//                                feedList.add(memberModel);
//                            }
//                        }
//                    }
//                } catch (ParseException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//
//        Log.d("feed", "feed list size is " + feedList.size());
//
//        Date newDate = new Date(currentDate.getTime() - 604800000L);
//        currentDate = newDate;
//
//        Log.d("feed", "new date started");
//
//        Log.d("feed", currentDate.toString());
//        Log.d("feed", leastDate.toString());
//
//        if(feedList.size() < 10){
//            if(currentDate.compareTo(leastDate) == 1){
//                Log.d("feed", "getting another feed");
//                getFeed();
//            }
//            else {
//                Log.d("feed", "not getting");
//                for(Object object: feedList){
//                    if(object instanceof EventModel){
//                        Log.d("feed",((EventModel)object).eventName + " event" );
//                    }
//                    else if(object instanceof MemberModel){
//                        Log.d("feed",((MemberModel)object).bandrole + " role" );
//                    }
//                    else if(object instanceof AlbumModel){
//                        Log.d("feed", ((AlbumModel)object).getAlbumName() + " album");
//                    }
//                }
//            }
//        }
//        else{
//            Log.d("feed", "not getting");
//            for(Object object: feedList){
//                if(object instanceof EventModel){
//                    Log.d("feed",((EventModel)object).eventName + " event" );
//                }
//                else if(object instanceof MemberModel){
//                    Log.d("feed",((MemberModel)object).bandrole + " role" );
//                }
//                else if(object instanceof AlbumModel){
//                    Log.d("feed", ((AlbumModel)object).getAlbumName() + " album");
//                }
//            }
//        }
//
//    }

}
