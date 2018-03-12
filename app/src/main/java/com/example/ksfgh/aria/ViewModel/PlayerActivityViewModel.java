package com.example.ksfgh.aria.ViewModel;

import android.databinding.BindingAdapter;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.databinding.ObservableLong;
import android.databinding.adapters.SeekBarBindingAdapter;
import android.media.Image;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.example.ksfgh.aria.Model.CustomSongModelForPlaylist;
import com.example.ksfgh.aria.R;
import com.example.ksfgh.aria.Singleton;
import com.example.ksfgh.aria.View.activities.PlayerActivity;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by ksfgh on 05/02/2018.
 */

public class PlayerActivityViewModel {

    public PlayerActivity playerActivity;
    public ObservableField<CustomSongModelForPlaylist> song;
    public ObservableField<String> songDuration = new ObservableField<>();
    public ObservableField<String> currentTime = new ObservableField<>();
    public ObservableInt seekBarMax = new ObservableInt();
    public ObservableInt seekBarProgress = new ObservableInt();
    private Disposable disposable;
    public ObservableBoolean nextVisibility;
    public ObservableBoolean previousVisibility;
    public ObservableBoolean isShuffled;
    public ObservableBoolean isRepeated;

    public PlayerActivityViewModel(PlayerActivity playerActivity) {
        this.playerActivity = playerActivity;
        song = new ObservableField<>();
        nextVisibility = new ObservableBoolean();
        previousVisibility = new ObservableBoolean();
        isShuffled = new ObservableBoolean();
        isRepeated = new ObservableBoolean();
        isThereANext("");
        isThereAPrevious("");
        EventBus.getDefault().register(this);
        onNextSong("");
        isPlayerShuffled();
        isPlayerRepeated();
    }

    @BindingAdapter("bind:imgBlur")
    public static void imgBlur(ImageView view, String image){
        Glide.with(view.getContext()).load(image)
                .apply(RequestOptions.overrideOf(500,500))
                .apply(RequestOptions.bitmapTransform(new BlurTransformation(70)))
                .into(view);
    }

    @BindingAdapter("bind:imgUrl")
    public static void imgUrl(ImageView view, String image){
        Glide.with(view.getContext()).load(image)
                .apply(RequestOptions.overrideOf(250,250))
                .apply(RequestOptions.centerCropTransform())
                .into(view);
    }

    @Subscriber(tag = "songTimer")
    public void songTimer(String empty){

        if(disposable != null)
            if(!disposable.isDisposed()){
                disposable.dispose();
                playerActivity.clearDisposable();
            }

        if(isPlayerPlaying()){

            int timer = (int)(Singleton.homeScreen.exoPlayer.getCurrentPosition()/1000) + 1;
            disposable = Observable.interval(1, TimeUnit.SECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .take((Singleton.homeScreen.exoPlayer.getDuration()/1000) - (Singleton.homeScreen.exoPlayer.getCurrentPosition() / 1000))
                    .map(x -> timer+x)
                    .doOnNext(new Consumer<Long>() {
                        @Override
                        public void accept(Long aLong) throws Exception {
                            seekBarProgress.set(Integer.parseInt(String.valueOf(aLong)));
                            currentTime.set(Singleton.getInstance().utilities.timeToString((int) Singleton.homeScreen.exoPlayer.getCurrentPosition()));
                        }
                    })
                    .doOnError(new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {

                        }
                    })
                    .subscribe();

            playerActivity.addDisposables(disposable);

        }
    }

    public void onProgressChanged(SeekBar view, int progress, boolean fromUser){
        Log.d("sbSeek", String.valueOf(progress));
        if(fromUser){
            EventBus.getDefault().post(progress, "seekSongTo");
            seekBarProgress.set((int) Singleton.homeScreen.exoPlayer.getCurrentPosition()/1000);
            currentTime.set(Singleton.getInstance().utilities.timeToString((int) Singleton.homeScreen.exoPlayer.getCurrentPosition()));
        }
    }

    public Boolean isPlayerPlaying(){
        return Singleton.getInstance().isPlayerPlaying;
    }

    ImageView view;
    public void pauseOrPlay(View view){
        this.view = (ImageView) view;
        if(Singleton.getInstance().isPlayerPlaying){
            EventBus.getDefault().post(false, "playOrPause");
            this.view.setImageResource(R.drawable.exo_controls_play);
            //EventBus.getDefault().post(false, "setFabSrc");
        }
        else {
            EventBus.getDefault().post(true, "playOrPause");
            this.view.setImageResource(R.drawable.exo_controls_pause);
            //EventBus.getDefault().post(true, "setFabSrc");
        }
    }

    @Subscriber(tag="onNextSong")
    public void onNextSong(String empty){

        song.set(Singleton.getInstance().song);
        songDuration.set(Singleton.getInstance().utilities.timeToString((int)Singleton.homeScreen.exoPlayer.getDuration()));
        currentTime.set(Singleton.getInstance().utilities.timeToString((int) Singleton.homeScreen.exoPlayer.getCurrentPosition()));
        seekBarMax.set((int)Singleton.homeScreen.exoPlayer.getDuration()/1000);
        seekBarProgress.set((int) Singleton.homeScreen.exoPlayer.getCurrentPosition()/1000);
        songTimer("");

    }

    public void nextSong(){
        EventBus.getDefault().post("", "nextSong");
    }

    public void previousSong(){
        EventBus.getDefault().post("", "previousSong");
    }

    @Subscriber(tag = "isThereAPrevious")
    public void isThereAPrevious(String empty){
        if(Singleton.homeScreen.windowIndex-1 >= 0)
           previousVisibility.set(true);
        else
            previousVisibility.set(false);
    }

    @Subscriber(tag = "isThereANext")
    public void isThereANext(String empty){
        if(Singleton.homeScreen.windowIndex+1 < Singleton.homeScreen.songList.size())
            nextVisibility.set(true);
        else
            nextVisibility.set(false);
    }

    public void isPlayerShuffled(){
        if(Singleton.homeScreen.exoPlayer.getShuffleModeEnabled())
            isShuffled.set(true);
        else
            isShuffled.set(false);
    }

    public void shufflePlayer(){
        if(Singleton.homeScreen.exoPlayer.getShuffleModeEnabled()){
            //Singleton.homeScreen.exoPlayer.setShuffleModeEnabled(false);
            EventBus.getDefault().post("", "shufflePlaylist");
            isShuffled.set(false);
        }
        else{
            EventBus.getDefault().post("", "shufflePlaylist");
            //Singleton.homeScreen.exoPlayer.setShuffleModeEnabled(true);
            isShuffled.set(true);
        }
    }

    public void isPlayerRepeated(){
        if(Singleton.homeScreen.exoPlayer.getRepeatMode() == 0)
            isRepeated.set(false);
        else
            isRepeated.set(true);
    }

    public void repeatPlayer(){
        if(Singleton.homeScreen.exoPlayer.getRepeatMode() == 0){
            EventBus.getDefault().post(2, "repeatPlaylist");
            isRepeated.set(true);
        }
        else {
            EventBus.getDefault().post(0,"repeatPlaylist");
            isRepeated.set(false);
        }

    }

    @Subscriber(tag = "playerEndOfQueue")
    public void playerEndOfQueue(String empty){
        view.setImageResource(R.drawable.exo_controls_play);
    }

    public void destroyActivity(){
        playerActivity.finish();
    }
}
