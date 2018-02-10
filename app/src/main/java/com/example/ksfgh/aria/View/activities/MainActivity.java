package com.example.ksfgh.aria.View.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
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
import com.example.ksfgh.aria.Model.SongModel;
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
import org.reactivestreams.Subscription;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
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
    @BindView(R.id.btnAddSong)
    Button btnAddSong;

    private CallbackManager callbackManager;
    AccessToken accessToken;
    FacebookCallback facebookCallback;
    private AccessTokenTracker accessTokenTracker;
    private ProfileTracker profileTracker;
    private boolean writeAccepted = false;
    private CompositeDisposable compositeDisposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        InitComponents();
    }

    private void InitComponents() {

        compositeDisposable = new CompositeDisposable();

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.dispose();
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

                                    Log.d("fb", response.toString());
                                    Log.d("fb", object.toString());
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

                                    SharedPreferences.Editor editor = getSharedPreferences(Singleton.getInstance().PREFERENCE_NAME, MODE_PRIVATE).edit();
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

        if (requestCode == Singleton.getInstance().PICK_PHOTO && resultCode == Activity.RESULT_OK && data != null) {
            try {
                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String picturePath = cursor.getString(columnIndex);
                cursor.close();

                uploadPhoto(selectedImage, picturePath);
                //addAlbum(selectedImage, picturePath);

            } catch (Exception e) {
                e.printStackTrace();
                Log.d("pick photo error", e.getMessage());
            }
        } else if (requestCode == Singleton.getInstance().PICK_AUDIO && resultCode == Activity.RESULT_OK && data != null) {
            Uri audioFileUrl = data.getData();
            String[] audioPathColumn = {MediaStore.Audio.Media.DATA};
            Cursor audioCursor = getContentResolver().query(audioFileUrl, audioPathColumn, null, null, null);
            audioCursor.moveToFirst();
            int audioColumnIndex = audioCursor.getColumnIndex(audioPathColumn[0]);
            String audioPath = audioCursor.getString(audioColumnIndex);
            Log.d("pick audio", audioPath);

            uploadSong(audioFileUrl, audioPath);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case 200:
                boolean writeAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                startActivityForResult(intent, Singleton.getInstance().PICK_PHOTO);
                break;
        }
    }

    @OnClick(R.id.btnGetBands)
    public void getBands() {


        Disposable disposable = RetrofitClient.getClient().getbands()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<BandModel[]>() {
                    @Override
                    public void onNext(BandModel[] bandModels) {
                        for (BandModel band: bandModels){
                            Log.d("bands", band.getBandName());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        Log.d("bands", "Yay Completed!");
                    }
                });

        compositeDisposable.add(disposable);
    }

    @OnClick(R.id.btnCreateUser)
    public void createUser() {

        FacebookUserModel model = new Gson().fromJson(getSharedPreferences(Singleton.getInstance().PREFERENCE_NAME, MODE_PRIVATE).getString("user", null),
                FacebookUserModel.class);

//        Call<FacebookUserModel> call = RetrofitClient.getClient().createAccount(model);
//
//        call.enqueue(new Callback<FacebookUserModel>() {
//            @Override
//            public void onResponse(Call<FacebookUserModel> call, Response<FacebookUserModel> response) {
//                try {
//                    Log.d("user", response.message());
//                } catch (Exception e) {
//                    Log.d("user error", e.getMessage());
//                }
//            }
//
//            @Override
//            public void onFailure(Call<FacebookUserModel> call, Throwable t) {
//                Log.d("user error", t.getMessage());
//            }
//        });
    }


    @OnClick(R.id.btnPickPhoto)
    public void pickPhoto() {

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            String[] perms = {"android.permission.WRITE_EXTERNAL_STORAGE"};
            int permsRequestCode = 200;
            requestPermissions(perms, permsRequestCode);
        } else {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/*");
            startActivityForResult(intent, Singleton.getInstance().PICK_PHOTO);
        }
    }

//    @OnClick(R.id.btnCreateBand)
//    public void createBand() {
//
//        FacebookUserModel model = new Gson().fromJson(getSharedPreferences(Singleton.getInstance().PREFERENCE_NAME, MODE_PRIVATE).getString("user", null),
//                FacebookUserModel.class);
//
//        BandCreationModel newBand = new BandCreationModel(model.user_id.toString(), "Drummer", "generation f", 1, 3, "this is a band");
//
//        Call<BandMemberModel> call = RetrofitClient.getClient().createBand(newBand);
//
//        call.enqueue(new Callback<BandMemberModel>() {
//            @Override
//            public void onResponse(Call<BandMemberModel> call, Response<BandMemberModel> response) {
//                try {
//                    Log.d("create band", response.body().band.bandName);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    Log.d("create band error", e.getMessage());
//                }
//            }
//
//            @Override
//            public void onFailure(Call<BandMemberModel> call, Throwable t) {
//                Log.d("create band error", t.getMessage());
//            }
//        });
//    }

    @OnClick(R.id.btnAddSong)
    public void addSong() {

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("audio/*");
        startActivityForResult(intent, Singleton.getInstance().PICK_AUDIO);
    }

    private void uploadSong(Uri song, String songPath){

        RequestBody albumId = RequestBody.create(MultipartBody.FORM, "1");
        RequestBody songTitle = RequestBody.create(MultipartBody.FORM, "first song");
        RequestBody songDesc = RequestBody.create(MultipartBody.FORM, "this is a song for the lit shit");
        RequestBody genreId = RequestBody.create(MultipartBody.FORM, "1");
        RequestBody bandId = RequestBody.create(MultipartBody.FORM, "1");

        File originalFile = new File(songPath);
        RequestBody filePart = RequestBody.create(MediaType.parse(getContentResolver().getType(song)), originalFile);
        MultipartBody.Part file = MultipartBody.Part.createFormData("song_audio", originalFile.getName(), filePart);

        Disposable disposable = RetrofitClient.getClient().addSong(albumId, songTitle, songDesc, genreId, bandId, file)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<SongModel>() {
                    @Override
                    public void onNext(SongModel songModel) {
                        Log.d("song", songModel.songAudio);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("song error", e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });

        compositeDisposable.add(disposable);
//        Call<SongModel> call = RetrofitClient.getClient().addSong(albumId, songTitle, songDesc, genreId, bandId, file);
//        call.enqueue(new Callback<SongModel>() {
//            @Override
//            public void onResponse(Call<SongModel> call, Response<SongModel> response) {
//                Log.d("song", response.body().songAudio);
//            }
//
//            @Override
//            public void onFailure(Call<SongModel> call, Throwable t) {
//                Log.d("song error", t.getMessage());
//            }
//        });


    }

    public void addAlbum(Uri selectedImage, String imagePath){

        RequestBody bandId = RequestBody.create(MultipartBody.FORM, "1");
        RequestBody albumName = RequestBody.create(MultipartBody.FORM, "first album");
        RequestBody albumDesc = RequestBody.create(MultipartBody.FORM, "first album release");

        File originalFile = new File(imagePath);
        RequestBody filePart = RequestBody.create(MediaType.parse(getContentResolver().getType(selectedImage)), originalFile);
        MultipartBody.Part file = MultipartBody.Part.createFormData("album_pic", originalFile.getName(), filePart);

        Disposable disposable = RetrofitClient.getClient().addAlbum(bandId, albumName, albumDesc, file)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<ResponseBody>() {
                    @Override
                    public void onNext(ResponseBody responseBody) {
                        try {
                            Log.d("album", responseBody.string());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("album error", e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });

        compositeDisposable.add(disposable);

//        Call<ResponseBody> call = RetrofitClient.getClient().addAlbum(bandId, albumName, albumDesc, file);
//        call.enqueue(new Callback<ResponseBody>() {
//            @Override
//            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                Log.d("album", response.message());
//            }
//
//            @Override
//            public void onFailure(Call<ResponseBody> call, Throwable t) {
//                Log.d("album error", t.getMessage());
//            }
//        });
    }

    private void uploadPhoto(Uri selectedImage, String imagePath) {

        RequestBody description = RequestBody.create(MultipartBody.FORM, "1");
        File originalFile = new File(imagePath);
        RequestBody filePart = RequestBody.create(MediaType.parse(getContentResolver().getType(selectedImage)), originalFile);
        MultipartBody.Part file = MultipartBody.Part.createFormData("bandPic", originalFile.getName(), filePart);

        Disposable disposable = RetrofitClient.getClient().editBandPic(description, file)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<ResponseBody>() {
                    @Override
                    public void onNext(ResponseBody responseBody) {
                        try {
                            Log.d("photo", responseBody.string());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("photo", e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });

        compositeDisposable.add(disposable);
    }
}
