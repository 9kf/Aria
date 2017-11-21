package com.example.ksfgh.aria.View.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Debug;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ksfgh.aria.Model.BandModel;
import com.example.ksfgh.aria.R;
import com.example.ksfgh.aria.Rest.RetrofitClient;
import com.example.ksfgh.aria.Singleton;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.btnFbLogin)
    LoginButton btnFbLogin;
    @BindView(R.id.btnGetBands)
    Button btnGetBands;

    private CallbackManager callbackManager;
    AccessToken accessToken;
    FacebookCallback facebookCallback;
    private AccessTokenTracker accessTokenTracker;
    private ProfileTracker profileTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        InitComponents();
    }

    private void InitComponents() {
        callbackManager = CallbackManager.Factory.create();

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
    }

    @OnClick(R.id.btnFbLogin)
    public void onViewClicked() {

        LoginManager.getInstance().logInWithReadPermissions(this,
                Arrays.asList("email", "public_profile"));

        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                accessToken = loginResult.getAccessToken();
                Profile profile = Profile.getCurrentProfile();

                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                Log.d("aa", response.toString());
                            }
                        });

                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,link,email,gender,installed,age_range,cover,friends{id,name,picture}");
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @OnClick(R.id.btnGetBands)
    public void getBands() {

        Call<BandModel[]> call = RetrofitClient.getClient().getAllBands();
        call.enqueue(new Callback<BandModel[]>() {
            @Override
            public void onResponse(Call<BandModel[]> call, Response<BandModel[]> response) {

                try{
                    for (BandModel band:response.body()) {
                        Log.d("bands", "band is: " + band.getBandName());
                    }
                }
                catch(Exception e){
                    Log.d("band error", e.getMessage());
                }


            }

            @Override
            public void onFailure(Call<BandModel[]> call, Throwable t) {
                Log.d("band error", t.getMessage());
            }
        });

    }
}
