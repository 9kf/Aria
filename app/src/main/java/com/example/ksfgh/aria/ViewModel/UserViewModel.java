package com.example.ksfgh.aria.ViewModel;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableField;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.example.ksfgh.aria.Adapters.UserFollowedBandsAdapter;
import com.example.ksfgh.aria.Adapters.UserPlaylistsAdapter;
import com.example.ksfgh.aria.Model.AlbumModel;
import com.example.ksfgh.aria.Model.BandModel;
import com.example.ksfgh.aria.Model.CustomModelForBandPage;
import com.example.ksfgh.aria.Model.EventModel;
import com.example.ksfgh.aria.Model.FacebookUserModel;
import com.example.ksfgh.aria.Model.MemberModel;
import com.example.ksfgh.aria.Model.PlaylistModel;
import com.example.ksfgh.aria.Model.PreferenceModel;
import com.example.ksfgh.aria.Model.UserModel;
import com.example.ksfgh.aria.Model.VideoModel;
import com.example.ksfgh.aria.R;
import com.example.ksfgh.aria.Rest.RetrofitClient;
import com.example.ksfgh.aria.Singleton;
import com.example.ksfgh.aria.View.activities.BandActivity;
import com.example.ksfgh.aria.View.activities.PlaylistActivity;
import com.example.ksfgh.aria.View.fragments.UserFragment;
import com.example.ksfgh.aria.databinding.CreatePlaylistBinding;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import java.io.File;
import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Function3;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * Created by ksfgh on 25/02/2018.
 */

public class UserViewModel {

    public ObservableField<UserModel> user;
    public ObservableArrayList<PlaylistModel> userPlaylists;
    public ObservableArrayList<CustomModelForBandPage> userFollowedBands;
    public Uri selectedImage = null;
    public ObservableField<String> bandImage;

    public UserViewModel(UserFragment fragment) {
        EventBus.getDefault().register(this);
        user = new ObservableField<>();
        user.set(Singleton.getInstance().currentUser.get());
        userPlaylists = new ObservableArrayList<>();
        userFollowedBands = new ObservableArrayList<>();
        bandImage = new ObservableField<>();
        bandImage.set(Singleton.getInstance().utilities.getURLForResource(R.drawable.click_for_image));
        getUserPlaylists();
        getUserFollowedBands();
    }

    @Subscriber(tag = "changeUser")
    public void changeUser(String empty){
        user.set(Singleton.getInstance().currentUser.get());
        userFollowedBands.clear();
        userPlaylists.clear();
        getUserFollowedBands();
        getUserPlaylists();
    }

    @BindingAdapter("bind:userPlaylist")
    public static void userPlaylistAdapter(RecyclerView view, UserViewModel viewModel){
        view.setLayoutManager(new LinearLayoutManager(view.getContext(), LinearLayoutManager.HORIZONTAL, false));
        view.setAdapter(new UserPlaylistsAdapter(viewModel));
    }

    @BindingAdapter("bind:userFollowedBands")
    public static void userFollowedBandsAdapter(RecyclerView view, UserViewModel viewModel){
        view.setLayoutManager(new LinearLayoutManager(view.getContext(), LinearLayoutManager.HORIZONTAL, false));
        view.setAdapter(new UserFollowedBandsAdapter(viewModel));
    }

    private void getUserFollowedBands() {
        Observable<PreferenceModel[]> prefs = RetrofitClient.getClient().getUserPreferences(Singleton.homeScreen.user.user_id);
        Observable<BandModel[]> bands = RetrofitClient.getClient().getbands();
        Observable<ArrayList<BandModel>> observable = Observable.zip(prefs, bands, new BiFunction<PreferenceModel[], BandModel[], ArrayList<BandModel>>() {
            @Override
            public ArrayList<BandModel> apply(PreferenceModel[] preferenceModels, BandModel[] bandModels) throws Exception {
                ArrayList<BandModel> list = new ArrayList<>();
                for(PreferenceModel preferenceModel: preferenceModels){
                    for(BandModel bandModel: bandModels){
                        if(preferenceModel.bandId != null){
                            if(preferenceModel.bandId.equals(String.valueOf(bandModel.bandId))&& preferenceModel.userId.equals(user.get().userId)){
                                list.add(bandModel);
                            }
                        }
                    }
                }
                return list;
            }
        });


        Disposable disposable = observable
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<ArrayList<BandModel>>() {
                    @Override
                    public void onNext(ArrayList<BandModel> bandModels) {
                        getBandDetails(bandModels);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void getBandDetails(ArrayList<BandModel> bandModels) {
        Observable<AlbumModel[]> observable2 = RetrofitClient.getClient().getAllAlbums();
        Observable<MemberModel[]> observable3 = RetrofitClient.getClient().getBandMembers();
        Observable<EventModel[]> observable4 = RetrofitClient.getClient().getEvents();
        Observable<ArrayList<CustomModelForBandPage>> observable = Observable.zip(observable2, observable3, observable4, new Function3<AlbumModel[], MemberModel[], EventModel[], ArrayList<CustomModelForBandPage>>() {
            @Override
            public ArrayList<CustomModelForBandPage> apply(AlbumModel[] albumModels, MemberModel[] memberModels, EventModel[] eventModels) throws Exception {
                ArrayList<CustomModelForBandPage> customModelForBandPages = new ArrayList<>();

                for(BandModel bandModel: bandModels){

                    ArrayList<MemberModel> bandMembers = new ArrayList<>();
                    ArrayList<AlbumModel> bandAlbums = new ArrayList<>();
                    ArrayList<EventModel> bandEvents = new ArrayList<>();

                    //this loop will determine the members of the band
                    for(MemberModel members2: memberModels){
                        if(bandModel.getBandId() == members2.bandId){
                            bandMembers.add(members2);
                        }
                    }

                    //this loop below will determine the albums of the band
                    for(AlbumModel albums: albumModels){
                        if(bandModel.getBandId() == albums.getBandId()){
                            bandAlbums.add(albums);
                        }
                    }

                    //this loop below will determine the events of the band
                    for(EventModel events: eventModels){
                        if(bandModel.getBandId() == events.bandId){
                            bandEvents.add(events);
                        }
                    }

                    customModelForBandPages.add(new CustomModelForBandPage(bandModel, bandMembers, bandAlbums, bandEvents));

                }

                return customModelForBandPages;
            }
        });


        Disposable disposable = observable
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<ArrayList<CustomModelForBandPage>>() {
                    @Override
                    public void onNext(ArrayList<CustomModelForBandPage> customModelForBandPages) {
                        userFollowedBands.addAll(customModelForBandPages);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        for(CustomModelForBandPage model:userFollowedBands){
                            Disposable disposable1 = RetrofitClient.getClient().getBandVideos(String.valueOf(model.band.bandId))
                                    .subscribeOn(Schedulers.newThread())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribeWith(new DisposableObserver<VideoModel[]>() {
                                        @Override
                                        public void onNext(VideoModel[] videoModels) {
                                            ArrayList<VideoModel> videoList = new ArrayList<>();
                                            for(VideoModel videos: videoModels){
                                                videoList.add(videos);
                                            }
                                            model.setVideos(videoList);
                                        }

                                        @Override
                                        public void onError(Throwable e) {

                                        }

                                        @Override
                                        public void onComplete() {

                                        }
                                    });
                        }
                    }
                });
    }

    private void getUserPlaylists() {
        Disposable disposable = RetrofitClient.getClient().getPlaylists()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<PlaylistModel[]>() {
                    @Override
                    public void onNext(PlaylistModel[] playlistModels) {
                        for(PlaylistModel model:playlistModels){
                            if(model.getPlCreator().equals(user.get().userId)){
                                userPlaylists.add(model);
                                if(Singleton.getInstance().userPlaylists.size() == 0)
                                    Singleton.getInstance().userPlaylists.add(model);
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("userSection", e.getMessage() + " ");
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void playlistClicked(PlaylistModel model){
        EventBus.getDefault().post(model, "setPlist");
        Singleton.getInstance().currentPlaylistId = model;
        Intent intent = new Intent(Singleton.homeScreen, PlaylistActivity.class);
        Singleton.homeScreen.startActivity(intent);
    }

    public void bandsClicked(CustomModelForBandPage band){
        Singleton.getInstance().currentBand = band;
        Intent intent = new Intent(Singleton.homeScreen, BandActivity.class);
        Singleton.homeScreen.startActivity(intent);
    }

    public void optionsClicked(View view){
        PopupMenu popupMenu = new PopupMenu(Singleton.homeScreen, view);
        popupMenu.getMenuInflater().inflate(R.menu.user_popup_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.itmEditProfile:
                        Log.d("popup", "edit shit");
                        break;
                    case R.id.itmAddUserPlaylist:
                        addUserPlaylist();
                        break;
                }

                return true;
            }
        });
        popupMenu.show();
    }

    public void pickPhoto(int identifier){

        Singleton.getInstance().CHANGE_OR_ADD = identifier;

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            String[] perms = {"android.permission.WRITE_EXTERNAL_STORAGE"};
            int permsRequestCode = 200;
            Singleton.homeScreen.requestPermissions(perms, permsRequestCode);
        } else {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/*");
            Singleton.homeScreen.startActivityForResult(intent, Singleton.getInstance().PICK_PHOTO);
        }
    }

    @Subscriber(tag = "setSelectedImageUser")
    public void setSelectedImage(Uri image){
        selectedImage = image;
    }

    @Subscriber(tag="changeBandPicUser")
    public void setBandPic(String image){
        bandImage.set(image);
    }

    private void addUserPlaylist() {

        CreatePlaylistBinding binding = DataBindingUtil.inflate(Singleton.homeScreen.getLayoutInflater(), R.layout.dialog_create_playlist, null, false);
        binding.setViewmodel(this);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Singleton.homeScreen, R.style.BlackAlertDialog);
        alertDialogBuilder
                .setView(binding.getRoot())
                .setCancelable(false)
                .setPositiveButton("Create", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        Toast.makeText(Singleton.homeScreen, "Adding playlist ...", Toast.LENGTH_SHORT).show();

                        RequestBody userId = RequestBody.create(MultipartBody.FORM, user.get().userId);
                        RequestBody playlistName = RequestBody.create(MultipartBody.FORM, binding.etPlaylistName.getText().toString());
                        File originalFile = new File(bandImage.get().toString());
                        RequestBody filePart = RequestBody.create(MediaType.parse(Singleton.homeScreen.getContentResolver().getType(selectedImage)), originalFile);
                        MultipartBody.Part file = MultipartBody.Part.createFormData("pl_image", originalFile.getName(), filePart);

                        Disposable disposable = RetrofitClient.getClient().addPlaylist(userId, playlistName, file)
                                .subscribeOn(Schedulers.newThread())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribeWith(new DisposableObserver<PlaylistModel>() {
                                    @Override
                                    public void onNext(PlaylistModel model) {
                                        userPlaylists.add(model);
                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        Log.d("usersection", e.getMessage() + " ");
                                        Toast.makeText(Singleton.homeScreen, "There was an error adding the playlist", Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onComplete() {
                                        Log.d("usersection","complete");
                                        Toast.makeText(Singleton.homeScreen, "Adding playlist complete", Toast.LENGTH_SHORT).show();
                                        bandImage.set(Singleton.getInstance().utilities.getURLForResource(R.drawable.click_for_image));
                                    }
                                });

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        bandImage.set(Singleton.getInstance().utilities.getURLForResource(R.drawable.click_for_image));
                    }
                });

        AlertDialog dialog = alertDialogBuilder.create();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        dialog.show();
    }

}
