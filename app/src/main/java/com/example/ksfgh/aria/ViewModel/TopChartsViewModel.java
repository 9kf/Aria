package com.example.ksfgh.aria.ViewModel;

import android.app.AlertDialog;
import android.content.Intent;
import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableList;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.ksfgh.aria.Adapters.TopBandsByGenreAdapter;
import com.example.ksfgh.aria.Adapters.TopTwentyAdapter;
import com.example.ksfgh.aria.Adapters.TopTwentyThisWeekAdapter;
import com.example.ksfgh.aria.Model.AlbumModel;
import com.example.ksfgh.aria.Model.BandGenreModel;
import com.example.ksfgh.aria.Model.BandModel;
import com.example.ksfgh.aria.Model.CustomModelForBandPage;
import com.example.ksfgh.aria.Model.EventModel;
import com.example.ksfgh.aria.Model.MemberModel;
import com.example.ksfgh.aria.Model.VideoModel;
import com.example.ksfgh.aria.R;
import com.example.ksfgh.aria.Rest.RetrofitClient;
import com.example.ksfgh.aria.Singleton;
import com.example.ksfgh.aria.View.activities.BandActivity;
import com.example.ksfgh.aria.View.fragments.Top20ThisWeekDialogFragment;
import com.example.ksfgh.aria.View.fragments.TopBandByGenreDialogFragment;
import com.example.ksfgh.aria.View.fragments.TopTwentyDialogFragment;
import com.example.ksfgh.aria.databinding.TopTenByGenreBinding;

import org.simple.eventbus.EventBus;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function3;
import io.reactivex.functions.Function4;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by ksfgh on 07/03/2018.
 */

public class TopChartsViewModel implements Serializable {

    public ObservableArrayList<CustomModelForBandPage> topChartBands;
    public ObservableArrayList<CustomModelForBandPage> topBandsByGenre;
    public ObservableField<CustomModelForBandPage> topBand;
    public ObservableBoolean isLoading;
    public ObservableField<String> category;
    public ArrayList<String> genres;
    public int identifier;
    TopTwentyDialogFragment top20;
    Top20ThisWeekDialogFragment top20ThisWeek;
    TopBandByGenreDialogFragment topBandByGenreDialogFragment;

    public TopChartsViewModel() {
        topChartBands = new ObservableArrayList<>();
        topBandsByGenre = new ObservableArrayList<>();
        topBand = new ObservableField<>();
        category = new ObservableField<>();
        isLoading = new ObservableBoolean();
        genres = new ObservableArrayList<>();
        Collections.addAll(genres, Singleton.homeScreen.getResources().getStringArray(R.array.genres));
        getTopChartBands();
    }

    @BindingAdapter("bind:setTop20Adapter")
    public static void setTop20Adapter(RecyclerView view, TopChartsViewModel viewModel){
        view.setLayoutManager(new LinearLayoutManager(view.getContext(), LinearLayoutManager.VERTICAL, false));
        if(viewModel.identifier == 0){
            view.setAdapter(new TopTwentyAdapter(viewModel));
        }
        else if(viewModel.identifier == 1){
            view.setAdapter(new TopTwentyThisWeekAdapter(viewModel));
        }
        else if(viewModel.identifier == 2){
            view.setAdapter(new TopBandsByGenreAdapter(viewModel));
        }

    }

    private void getTopChartBands() {
        isLoading.set(true);
        ArrayList<BandModel> bands = new ArrayList<>();

        Disposable disposable = RetrofitClient.getClient().getbands()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<BandModel[]>() {
                    @Override
                    public void onNext(BandModel[] bandModels) {

                        for(BandModel bandModel: bandModels){
                            bands.add(bandModel);
                        }


                        Collections.sort(bands, new Comparator<BandModel>() {
                            @Override
                            public int compare(BandModel bandModel, BandModel t1) {
                                return Double.compare(t1.bandScore, bandModel.bandScore);
                            }
                        });


                        getBandDetails(bands);

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("topCharts",e.getMessage() + " :getTopChartBands");
                    }

                    @Override
                    public void onComplete() {

                    }
                });


    }

    private void getBandDetails(ArrayList<BandModel> bands) {
        Observable<AlbumModel[]> observable2 = RetrofitClient.getClient().getAllAlbums();
        Observable<MemberModel[]> observable3 = RetrofitClient.getClient().getBandMembers();
        Observable<EventModel[]> observable4 = RetrofitClient.getClient().getEvents();
        Observable<ArrayList<CustomModelForBandPage>> observable = Observable.zip(observable2, observable3, observable4, new Function3<AlbumModel[], MemberModel[], EventModel[], ArrayList<CustomModelForBandPage>>() {
            @Override
            public ArrayList<CustomModelForBandPage> apply(AlbumModel[] albumModels, MemberModel[] memberModels, EventModel[] eventModels) throws Exception {
                ArrayList<CustomModelForBandPage> customModelForBandPages = new ArrayList<>();

                for(BandModel bandModel: bands){

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
                        topChartBands.addAll(customModelForBandPages);
                        topBand.set(topChartBands.get(0));

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("topCharts",e.getMessage() + " :getBandDetails");
                    }

                    @Override
                    public void onComplete() {
                        isLoading.set(false);
                        for(CustomModelForBandPage bandPage: topChartBands){
                            Disposable disposable1 = RetrofitClient.getClient().getBandVideos(String.valueOf(bandPage.band.bandId))
                                    .subscribeOn(Schedulers.newThread())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribeWith(new DisposableObserver<VideoModel[]>() {
                                        @Override
                                        public void onNext(VideoModel[] videoModels) {
                                            ArrayList<VideoModel> videoList = new ArrayList<>();
                                            for(VideoModel videos: videoModels){
                                                videoList.add(videos);
                                            }
                                            bandPage.setVideos(videoList);
                                            if(bandPage.band.bandId == topBand.get().band.bandId)
                                                topBand.get().setVideos(videoList);
                                        }

                                        @Override
                                        public void onError(Throwable e) {
                                            Log.d("topCharts",e.getMessage() + " :getBandVideos");
                                        }

                                        @Override
                                        public void onComplete() {

                                        }
                                    });
                        }
                    }
                });

    }

    public void visitFeaturedPage(){
        Singleton.getInstance().currentBand = topBand.get();
        Intent intent = new Intent(Singleton.homeScreen, BandActivity.class);
        Singleton.homeScreen.startActivity(intent);
    }

    public void visitPage(CustomModelForBandPage band){
        dismissDialogFragment();
        Singleton.getInstance().currentBand = band;
        Intent intent = new Intent(Singleton.homeScreen, BandActivity.class);
        Singleton.homeScreen.startActivity(intent);
    }

    public void OverallTwenty(){
        identifier = 0;
        category.set("The Top 20");
        top20 = TopTwentyDialogFragment.newInstance(this);
        top20.show(Singleton.homeScreen.getSupportFragmentManager(), top20.getTag());
    }

    public void TopTwentyThisWeek(){
        identifier = 1;
        category.set("This week's top 20");
        top20ThisWeek = Top20ThisWeekDialogFragment.newInstance(this);
        top20ThisWeek.show(Singleton.homeScreen.getSupportFragmentManager(), top20ThisWeek.getTag());
    }

    public void Top10ByGenre(TopChartsViewModel viewModel){
        identifier = 2;
        if(!topBandsByGenre.isEmpty())
            topBandsByGenre.clear();
        TopTenByGenreBinding binding = DataBindingUtil.inflate(Singleton.homeScreen.getLayoutInflater(), R.layout.dialog_top_10_by_genre, null, false);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Singleton.homeScreen, R.style.BlackAlertDialog);
        alertDialogBuilder.setView(binding.getRoot());

        AlertDialog dialog = alertDialogBuilder.create();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        binding.lvGenres.setAdapter(new ArrayAdapter<String>(Singleton.homeScreen, android.R.layout.simple_list_item_1, android.R.id.text1, genres){
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view1 =  super.getView(position, convertView, parent);
                TextView text = (TextView) view1.findViewById(android.R.id.text1);
                text.setTextColor(Color.WHITE);
                return view1;
            }
        });

        binding.lvGenres.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                category.set("Top 10 for " + genres.get(i));
                Observable<BandGenreModel[]> observable1 = RetrofitClient.getClient().getBandGenre();
                Observable<BandModel[]> observable2 = RetrofitClient.getClient().getbands();
                Observable<ArrayList<BandModel>> observable = Observable.zip(observable1, observable2, new BiFunction<BandGenreModel[], BandModel[], ArrayList<BandModel>>() {
                    @Override
                    public ArrayList<BandModel> apply(BandGenreModel[] bandGenreModels, BandModel[] bandModels) throws Exception {

                        ArrayList<BandModel> bandsTemp = new ArrayList<>();

                        for(BandGenreModel bandGenres: bandGenreModels){
                            if(bandGenres.genreId == i+1){
                                for(BandModel bands: bandModels){
                                    if(bands.bandId == bandGenres.bandId){
                                        bandsTemp.add(bands);
                                        break;
                                    }
                                }
                            }
                        }
                        return bandsTemp;
                    }
                });

                dialog.dismiss();
                Disposable disposable = observable
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableObserver<ArrayList<BandModel>>() {
                            @Override
                            public void onNext(ArrayList<BandModel> bandModels) {
                                for(CustomModelForBandPage model:topChartBands){
                                    for(BandModel band:bandModels){
                                        if(model.band.bandId == band.bandId){
                                            topBandsByGenre.add(model);
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onError(Throwable e) {

                            }

                            @Override
                            public void onComplete() {
                                topBandByGenreDialogFragment = TopBandByGenreDialogFragment.newInstance(viewModel);
                                topBandByGenreDialogFragment.show(Singleton.homeScreen.getSupportFragmentManager(), topBandByGenreDialogFragment.getTag());
                            }
                        });
            }
        });


        dialog.show();
    }

    public void dismissDialogFragment(){
        if(top20 != null)
            top20.dismiss();

        if(top20ThisWeek != null)
            top20ThisWeek.dismiss();

        if(topBandByGenreDialogFragment != null)
            topBandByGenreDialogFragment.dismiss();
    }

}
