package com.example.ksfgh.aria.View.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;

import com.example.ksfgh.aria.Model.BandCreationModel;
import com.example.ksfgh.aria.Model.BandMemberModel;
import com.example.ksfgh.aria.Model.BandModel;
import com.example.ksfgh.aria.Model.FacebookUserModel;
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
import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.btnFbLogin)
    LoginButton btnFbLogin;
    @BindView(R.id.btnGetBands)
    Button btnGetBands;
    @BindView(R.id.btnCreateUser)
    Button btnCreateUser;
    @BindView(R.id.btnPickPhoto)
    Button btnPickPhoto;
    @BindView(R.id.btnCreateBand)
    Button btnCreateBand;

    private CallbackManager callbackManager;
    AccessToken accessToken;
    FacebookCallback facebookCallback;
    private AccessTokenTracker accessTokenTracker;
    private ProfileTracker profileTracker;
    private boolean writeAccepted = false;

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
            public void onSuccess(final LoginResult loginResult) {
                accessToken = loginResult.getAccessToken();
                final Profile profile = Profile.getCurrentProfile();

                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                try {

                                    Log.d("fb",response.toString());
                                    Log.d("fb",object.toString());
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

                                    SharedPreferences.Editor editor = getSharedPreferences(Singleton.PREFERENCE_NAME, MODE_PRIVATE).edit();
                                    editor.putString("user", new Gson().toJson(user));
                                    editor.apply();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Log.d("fb error", e.getMessage());
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Singleton.PICK_PHOTO && resultCode == Activity.RESULT_OK && data != null) {
            try {
                Uri selectedImage = data.getData();
                String[] filePathColumn = { MediaStore.Images.Media.DATA };
                Cursor cursor = getContentResolver().query(selectedImage,filePathColumn, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String picturePath = cursor.getString(columnIndex);
                cursor.close();

                uploadPhoto(selectedImage, picturePath);

            } catch (Exception e) {
                e.printStackTrace();
                Log.d("pick photo error", e.getMessage());
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){
            case 200:
                boolean writeAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                Intent intent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                startActivityForResult(intent, Singleton.PICK_PHOTO);
                break;
        }
    }

    private void uploadPhoto(Uri selectedImage, String imagePath) {

        RequestBody description = RequestBody.create(MultipartBody.FORM, "6");
        File originalFile = new File(imagePath);
        RequestBody filePart = RequestBody.create(MediaType.parse(getContentResolver().getType(selectedImage)), originalFile);
        MultipartBody.Part file = MultipartBody.Part.createFormData("bandPic", originalFile.getName(), filePart);

        Call<ResponseBody> call = RetrofitClient.getClient().editBandPic(description, file);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                Log.d("photo", response.message());
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });

    }


    @OnClick(R.id.btnGetBands)
    public void getBands() {

        Call<BandModel[]> call = RetrofitClient.getClient().getAllBands();
        call.enqueue(new Callback<BandModel[]>() {
            @Override
            public void onResponse(Call<BandModel[]> call, Response<BandModel[]> response) {

                try {
                    for (BandModel band : response.body()) {
                        Log.d("bands", "band is: " + band.getBandName());
                    }
                } catch (Exception e) {
                    Log.d("band error", e.getMessage());
                }


            }

            @Override
            public void onFailure(Call<BandModel[]> call, Throwable t) {
                Log.d("band error", t.getMessage());
            }
        });

    }

    @OnClick(R.id.btnCreateUser)
    public void createUser() {

        FacebookUserModel model = new Gson().fromJson(getSharedPreferences(Singleton.PREFERENCE_NAME, MODE_PRIVATE).getString("user", null),
                FacebookUserModel.class);

        Call<FacebookUserModel> call = RetrofitClient.getClient().createAccount(model);

        call.enqueue(new Callback<FacebookUserModel>() {
            @Override
            public void onResponse(Call<FacebookUserModel> call, Response<FacebookUserModel> response) {
                try {
                    Log.d("user", response.message());
                } catch (Exception e) {
                    Log.d("user error", e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<FacebookUserModel> call, Throwable t) {
                Log.d("user error", t.getMessage());
            }
        });
    }


    @OnClick(R.id.btnPickPhoto)
    public void pickPhoto() {

        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1){
            String[] perms = {"android.permission.WRITE_EXTERNAL_STORAGE"};
            int permsRequestCode = 200;
            requestPermissions(perms, permsRequestCode);
        }
        else {
            Intent intent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/*");
            startActivityForResult(intent, Singleton.PICK_PHOTO);
        }
    }

    @OnClick(R.id.btnCreateBand)
    public void createBand() {

        FacebookUserModel model = new Gson().fromJson(getSharedPreferences(Singleton.PREFERENCE_NAME, MODE_PRIVATE).getString("user", null),
                FacebookUserModel.class);

        BandCreationModel newBand = new BandCreationModel(model.user_id.toString(), "Drummer", "generation g", 1, 3, "this is a band");

        Call<BandMemberModel> call = RetrofitClient.getClient().createBand(newBand);

        call.enqueue(new Callback<BandMemberModel>() {
            @Override
            public void onResponse(Call<BandMemberModel> call, Response<BandMemberModel> response) {
                try {
                    Log.d("create band", response.body().band.bandName);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d("create band error", e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<BandMemberModel> call, Throwable t) {
                Log.d("create band error", t.getMessage());
            }
        });
    }

}
