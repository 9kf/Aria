package com.example.ksfgh.aria;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.ksfgh.aria.Model.FacebookUserModel;
import com.example.ksfgh.aria.Rest.RetrofitClient;
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
import org.simple.eventbus.EventBus;

import java.io.IOException;
import java.util.Arrays;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

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
                                Log.d("fbUser", object.toString());
                                try {
                                    final FacebookUserModel user = new FacebookUserModel(
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

                                    //check if the user has already registered in the app
                                    //if the user has already registered, the user's fb credentials will be downloaded from the server
                                    //if the user has not yet registered, then the user's fb credentials will be uploaded in the server
                                    checkIfRegistered(user, activity);

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });

                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,link,email,gender,installed,friends");
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

    private boolean isRegistered = false;
    private void checkIfRegistered(final FacebookUserModel model, final StartScreen activity) {

        Disposable disposable = RetrofitClient.getClient().getUsers()
                .subscribeOn(Schedulers.newThread())
                .subscribeWith(new DisposableObserver<FacebookUserModel[]>() {
                    @Override
                    public void onNext(FacebookUserModel[] facebookUserModels) {
                        for(FacebookUserModel user:facebookUserModels){
                            if(user.getEmail().equals(model.getEmail())){
                                model.setUser_id(user.getId());
                                isRegistered = true;
                                break;
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                        SharedPreferences.Editor editor = activity.getSharedPreferences(Singleton.getInstance().PREFERENCE_NAME, MODE_PRIVATE).edit();
                        editor.putString("user", new Gson().toJson(model));
                        editor.apply();

                        Intent intent = new Intent(activity, HomeScreen.class);
                        activity.startActivity(intent);
                        activity.finish();

                        if(isRegistered){
                            Disposable disposable = RetrofitClient.getClient().createAccount(model)
                                    .subscribeOn(Schedulers.newThread())
                                    .subscribeWith(new DisposableObserver<FacebookUserModel>() {

                                        @Override
                                        public void onNext(FacebookUserModel userModel) {

                                            SharedPreferences.Editor editor = activity.getSharedPreferences(Singleton.getInstance().PREFERENCE_NAME, MODE_PRIVATE).edit();
                                            editor.putString("user", new Gson().toJson(userModel));
                                            editor.apply();

                                            Intent intent = new Intent(activity, HomeScreen.class);
                                            activity.startActivity(intent);
                                            activity.finish();

                                        }

                                        @Override
                                        public void onError(Throwable e) {
                                            Log.d("fbUser", e.getMessage());
                                        }

                                        @Override
                                        public void onComplete() {

                                        }
                                    });

                            EventBus.getDefault().post(disposable, "addDisposables");
                        }
                    }
                });

        EventBus.getDefault().post(disposable, "addDisposables");

    }

}
