package com.example.ksfgh.aria.View.activities;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.MediaController;
import android.widget.VideoView;

import com.example.ksfgh.aria.Handlers;
import com.example.ksfgh.aria.R;
import com.example.ksfgh.aria.Singleton;

import com.example.ksfgh.aria.databinding.ActivityStartScreenBinding;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;
import org.simple.eventbus.ThreadMode;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class StartScreen extends AppCompatActivity {

    private ActivityStartScreenBinding activityStartScreenBinding;
    public CallbackManager callbackManager;
    private CompositeDisposable compositeDisposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //initialize fb sdk to avoid error on the facebook button
        FacebookSdk.sdkInitialize(getApplicationContext());

        //initialize universal image loader
        ImageLoaderConfiguration configuration = new ImageLoaderConfiguration.Builder(this).build();
        ImageLoader.getInstance().init(configuration);

        //initialize variables
        compositeDisposable = new CompositeDisposable();

        //Subscribing to event bus
        EventBus.getDefault().register(this);

        //setting content view
        activityStartScreenBinding = DataBindingUtil.setContentView(this, R.layout.activity_start_screen);
        activityStartScreenBinding.setHandlers(Singleton.getInstance().handlers);
        activityStartScreenBinding.setActivity(this);

        //check if the user has already logged in
        if(getSharedPreferences(Singleton.getInstance().PREFERENCE_NAME, MODE_PRIVATE).contains("user")){
            Intent intent = new Intent(this, HomeScreen.class);
            startActivity(intent);
            finish();
        }

        //facebook component
        callbackManager = CallbackManager.Factory.create();
    }

    //add the http calls so that when the activity is destroyed, http calls wont continue to produce memory leak
    @Subscriber(tag = "addDisposables", mode = ThreadMode.ASYNC)
    private void addDisposables(Disposable disposable){
        compositeDisposable.add(disposable);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //dispose all the http calls to avoid memory leak
        compositeDisposable.dispose();

        //unregister from event bus
        EventBus.getDefault().unregister(this);
    }
}
