package com.example.ksfgh.aria.ViewModel;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.BindingAdapter;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.ksfgh.aria.Model.CustomSongModelForPlaylist;
import com.example.ksfgh.aria.Model.FacebookUserModel;
import com.example.ksfgh.aria.R;
import com.example.ksfgh.aria.Rest.RetrofitClient;
import com.example.ksfgh.aria.Singleton;
import com.example.ksfgh.aria.View.activities.HomeScreen;
import com.example.ksfgh.aria.View.activities.StartScreen;
import com.example.ksfgh.aria.View.fragments.FeedFragment;
import com.example.ksfgh.aria.View.fragments.HomeFragment;
import com.facebook.login.LoginManager;

import org.simple.eventbus.EventBus;

import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import jp.wasabeef.glide.transformations.BlurTransformation;
import nl.psdcompany.duonavigationdrawer.views.DuoDrawerLayout;

/**
 * Created by ksfgh on 08/01/2018.
 */

public class HomeScreenViewModel {

    public FacebookUserModel userModel;
    private HomeScreen homeScreen;
    public ObservableField<String> userName = new ObservableField<>();
    public ObservableField<String> url = new ObservableField<>();
    public ObservableField<String> toolbarTitle = new ObservableField<>();
    public ObservableField<CustomSongModelForPlaylist> persistentBarSong = new ObservableField<>();
    public ObservableBoolean isPlayerPlaying = new ObservableBoolean();
    public ObservableBoolean isBottomsheetUp = new ObservableBoolean();

    private View currentView;
    private DuoDrawerLayout duoDrawerLayout;

    public HomeScreenViewModel(FacebookUserModel userModel, HomeScreen homeScreen, DuoDrawerLayout duoDrawerLayout){
        this.userModel = userModel;
        this.homeScreen = homeScreen;
        this.duoDrawerLayout = duoDrawerLayout;
        userName.set(userModel.fname + " " + userModel.lname);
        url.set(userModel.pic);
        toolbarTitle.set("Home");
        isPlayerPlaying.set(homeScreen.isPlaying);
        isBottomsheetUp.set(false);
        onDrawerItemClick(homeScreen.findViewById(R.id.llHome));
        getUsers();
    }

    private void getUsers() {
        if(Singleton.getInstance().facebookUserModels.size() == 0){
            Disposable disposable = RetrofitClient.getClient().getUsers()
                    .subscribeOn(Schedulers.newThread())
                    .subscribeWith(new DisposableObserver<FacebookUserModel[]>() {
                        @Override
                        public void onNext(FacebookUserModel[] facebookUserModels) {
                            for (FacebookUserModel model:facebookUserModels){
                                Singleton.getInstance().facebookUserModels.add(model);
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                        }

                        @Override
                        public void onComplete() {
                        }
                    });

            EventBus.getDefault().post(disposable,"homeDisposables");
        }
    }

    @BindingAdapter({"bind:url"})
    public static void setUserPic(ImageView view, String url){
        Glide.with(view.getContext()).load(url).into(view);
    }

    @BindingAdapter("bind:persistentBarBlur")
    public static void persistentBarBlur(ImageView view, String url){
        Glide.with(view.getContext()).load(url)
                .apply(RequestOptions.bitmapTransform(new BlurTransformation(50)))
                .apply(RequestOptions.overrideOf(400,70))
                .apply(RequestOptions.centerInsideTransform())
                .into(view);
    }

    public void onDrawerItemClick(View view){

        switch (view.getId()){
            case R.id.llHome:
                toolbarTitle.set("Home");
                EventBus.getDefault().post(Singleton.getInstance().homeFragment, "switchFragment");
                break;

            case R.id.llFeed:
                toolbarTitle.set("Feed");
                EventBus.getDefault().post(Singleton.getInstance().feedFragment, "switchFragment");
                break;

            case R.id.llTopCharts:
                toolbarTitle.set("Top Charts");
                break;

            case R.id.llSearch:
                toolbarTitle.set("Search");
                break;

            case R.id.llNotifications:
                toolbarTitle.set("Notifications");
                break;

            case R.id.llMyBands:
                toolbarTitle.set("My Bands");
                EventBus.getDefault().post(Singleton.getInstance().myBandsFragment, "switchFragment");
                break;

            case R.id.llFindFriends:
                toolbarTitle.set("Find Friends");
                break;

            case R.id.llSettings:
                toolbarTitle.set("Settings");
                break;

            case R.id.llLogout:

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(homeScreen);
                alertDialogBuilder
                        .setMessage("Are you sure you want to log out?")
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                SharedPreferences.Editor editor = homeScreen.getSharedPreferences(Singleton.getInstance().PREFERENCE_NAME, Context.MODE_PRIVATE).edit();
                                editor.remove("user");
                                editor.commit();

                                LoginManager.getInstance().logOut();

                                Intent intent = new Intent(homeScreen, StartScreen.class);
                                homeScreen.startActivity(intent);
                                homeScreen.finish();
                            }
                        })
                        .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Log.d("hello", "no");
                            }
                        })
                        .show();

                break;
        }

        if(view.getId() != R.id.llLogout){

            if(currentView == null){
                currentView = view;
                view.setBackgroundColor(Color.parseColor("#E57C1F"));
            }
            else {
                currentView.setBackgroundColor(Color.parseColor("#232323"));
                currentView = view;
                currentView.setBackgroundColor(Color.parseColor("#E57C1F"));
            }

            duoDrawerLayout.closeDrawer();
        }

    }

    public void playOrPause(){
        if(homeScreen.isPlaying){
            EventBus.getDefault().post(false, "playOrPause");
            isPlayerPlaying.set(false);
        }
        else {
            EventBus.getDefault().post(true, "playOrPause");
            isPlayerPlaying.set(true);
        }
    }

    public void openPlayer(){
        EventBus.getDefault().post(homeScreen, "openPlayer");
    }
}
