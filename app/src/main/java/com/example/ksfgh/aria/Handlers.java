package com.example.ksfgh.aria;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.ksfgh.aria.Model.FacebookUserModel;
import com.example.ksfgh.aria.View.activities.HomeScreen;
import com.example.ksfgh.aria.View.activities.StartScreen;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.Arrays;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by ksfgh on 04/01/2018.
 */

public class Handlers {

    AccessToken accessToken;
    AccessTokenTracker accessTokenTracker;
    ProfileTracker profileTracker;

    public void facebookLogin(final StartScreen activity, CallbackManager callbackManager){

        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {

            }
        };

        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {

            }
        };

        LoginManager.getInstance().logInWithReadPermissions(activity,
                Arrays.asList("email", "public_profile"));

        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(final LoginResult loginResult) {
                accessToken = loginResult.getAccessToken();
                final Profile profile = Profile.getCurrentProfile();

                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                try {

                                    FacebookUserModel user = new FacebookUserModel(
                                            profile.getId(),
                                            profile.getFirstName(),
                                            profile.getLastName(),
                                            object.getString("email"),
                                            object.getString("gender"),
                                            "",
                                            "",
                                            "",
                                            "",
                                            profile.getProfilePictureUri(500, 500).toString()
                                    );

                                    SharedPreferences.Editor editor = activity.getSharedPreferences(Singleton.PREFERENCE_NAME, MODE_PRIVATE).edit();
                                    editor.putString("user", new Gson().toJson(user));
                                    editor.apply();

                                    Intent intent = new Intent(activity, HomeScreen.class);
                                    activity.startActivity(intent);
                                    activity.finish();

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });

                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,link,email,gender,installed,friends{id,name,picture}");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });

    }

}
