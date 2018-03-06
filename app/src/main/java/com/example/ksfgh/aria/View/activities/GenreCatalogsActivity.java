package com.example.ksfgh.aria.View.activities;

import android.content.Intent;
import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableArrayList;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.example.ksfgh.aria.Adapters.GenreCatalogBandAdapter;
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
import com.example.ksfgh.aria.databinding.GenreCatalogActivityBinding;
import com.example.ksfgh.aria.databinding.GenreCatalogBinding;

import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function3;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class GenreCatalogsActivity extends AppCompatActivity {

    private GenreCatalogActivityBinding binding;
    public String genre;
    public ObservableArrayList<BandModel> genreBands;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_genre_catalogs);
        binding.setView(this);
        genre = Singleton.getInstance().currentGenre;
        genreBands = new ObservableArrayList<>();

        getGenreBands();
    }

    @BindingAdapter("bind:catalogAdapter")
    public static void catalogAdapter(RecyclerView view, GenreCatalogsActivity activity){
        view.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));
        view.setAdapter(new GenreCatalogBandAdapter(activity));
    }

    private void getGenreBands() {

        Observable<BandGenreModel[]> observable1 = RetrofitClient.getClient().getBandGenre();
        Observable<BandModel[]> observable2 = RetrofitClient.getClient().getbands();
        Observable<ArrayList<BandModel>> observable = Observable.zip(observable1, observable2, new BiFunction<BandGenreModel[], BandModel[], ArrayList<BandModel>>() {
            @Override
            public ArrayList<BandModel> apply(BandGenreModel[] bandGenreModels, BandModel[] bandModels) throws Exception {

                ArrayList<BandModel> bandsTemp = new ArrayList<>();

                for(BandGenreModel bandGenres: bandGenreModels){
                        if(bandGenres.genreId == Singleton.getInstance().genreNumber){
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

        Disposable disposable = observable
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<ArrayList<BandModel>>() {
                    @Override
                    public void onNext(ArrayList<BandModel> aBoolean) {
                        genreBands.addAll(aBoolean);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("mybands", e.getMessage() + " ");
                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }

    public void bandClicked(BandModel band){

        Observable<AlbumModel[]> observable2 = RetrofitClient.getClient().getAllAlbums();
        Observable<MemberModel[]> observable3 = RetrofitClient.getClient().getBandMembers();
        Observable<EventModel[]> observable4 = RetrofitClient.getClient().getEvents();
        Observable<CustomModelForBandPage> observable = Observable.zip(observable2, observable3, observable4, new Function3<AlbumModel[], MemberModel[], EventModel[], CustomModelForBandPage>() {
            @Override
            public CustomModelForBandPage apply(AlbumModel[] albumModels, MemberModel[] memberModels, EventModel[] eventModels) throws Exception {

                ArrayList<MemberModel> bandMembers = new ArrayList<>();
                ArrayList<AlbumModel> bandAlbums = new ArrayList<>();
                ArrayList<EventModel> bandEvents = new ArrayList<>();

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

                //this loop below will determine the events of the band
                for(EventModel events: eventModels){
                    if(band.getBandId() == events.bandId){
                        bandEvents.add(events);
                    }
                }

                return new CustomModelForBandPage(band, bandMembers, bandAlbums, bandEvents);
            }
        });


        Disposable disposable = observable
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<CustomModelForBandPage>() {
                    @Override
                    public void onNext(CustomModelForBandPage customModelForBandPage) {
                        Singleton.getInstance().currentBand = customModelForBandPage;
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("mybands", e.getMessage() + " ");
                    }

                    @Override
                    public void onComplete() {
                        Disposable disposable1 = RetrofitClient.getClient().getBandVideos(String.valueOf(Singleton.getInstance().currentBand.band.bandId))
                                .subscribeOn(Schedulers.newThread())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribeWith(new DisposableObserver<VideoModel[]>() {
                                    @Override
                                    public void onNext(VideoModel[] videoModels) {
                                        ArrayList<VideoModel> videoList = new ArrayList<>();
                                        for(VideoModel videos: videoModels){
                                            videoList.add(videos);
                                        }
                                        Singleton.getInstance().currentBand.setVideos(videoList);
                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        Log.d("mybands", e.getMessage() + " ");
                                    }

                                    @Override
                                    public void onComplete() {
                                        Intent intent = new Intent(GenreCatalogsActivity.this, BandActivity.class);
                                        startActivity(intent);
                                    }
                                });
                    }
                });

    }

    public void destroyActivity(){
        this.finish();
    }

}
