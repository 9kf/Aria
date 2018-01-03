package com.example.ksfgh.aria.View.activities;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class StartScreen extends AppCompatActivity {

    private ActivityStartScreenBinding activityStartScreenBinding;

    public CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //initialize fb sdk to avoid error on the facebook button
        FacebookSdk.sdkInitialize(getApplicationContext());

        //setting content view
        activityStartScreenBinding = DataBindingUtil.setContentView(this, R.layout.activity_start_screen);
        activityStartScreenBinding.setHandlers(Singleton.handlers);
        activityStartScreenBinding.setActivity(this);

        if(getSharedPreferences(Singleton.PREFERENCE_NAME, MODE_PRIVATE).contains("user")){
            Intent intent = new Intent(this, HomeScreen.class);
            startActivity(intent);
            finish();
        }

        //facebook component
        callbackManager = CallbackManager.Factory.create();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
