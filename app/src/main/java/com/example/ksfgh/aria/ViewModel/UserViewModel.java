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

import com.example.ksfgh.aria.Adapters.UserPlaylistsAdapter;
import com.example.ksfgh.aria.Model.FacebookUserModel;
import com.example.ksfgh.aria.Model.PlaylistModel;
import com.example.ksfgh.aria.R;
import com.example.ksfgh.aria.Rest.RetrofitClient;
import com.example.ksfgh.aria.Singleton;
import com.example.ksfgh.aria.View.activities.PlaylistActivity;
import com.example.ksfgh.aria.View.fragments.UserFragment;
import com.example.ksfgh.aria.databinding.CreatePlaylistBinding;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import java.io.File;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * Created by ksfgh on 25/02/2018.
 */

public class UserViewModel {

    public FacebookUserModel user;
    public ObservableArrayList<PlaylistModel> userPlaylists;
    private UserFragment fragment;
    public Uri selectedImage = null;
    public ObservableField<String> bandImage;

    public UserViewModel(UserFragment fragment) {
        EventBus.getDefault().register(this);
        user = Singleton.homeScreen.user;
        userPlaylists = new ObservableArrayList<>();
        this.fragment = fragment;
        bandImage = new ObservableField<>();
        bandImage.set(Singleton.getInstance().utilities.getURLForResource(R.drawable.click_for_image));
        getUserPlaylists();
    }

    @BindingAdapter("bind:userPlaylist")
    public static void userPlaylistAdapter(RecyclerView view, UserViewModel viewModel){
        view.setLayoutManager(new LinearLayoutManager(view.getContext(), LinearLayoutManager.HORIZONTAL, false));
        view.setAdapter(new UserPlaylistsAdapter(viewModel));
    }

    private void getUserPlaylists() {
        Disposable disposable = RetrofitClient.getClient().getPlaylists()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<PlaylistModel[]>() {
                    @Override
                    public void onNext(PlaylistModel[] playlistModels) {
                        for(PlaylistModel model:playlistModels){
                            if(model.getPlCreator().equals(user.getId())){
                                userPlaylists.add(model);
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

                        RequestBody userId = RequestBody.create(MultipartBody.FORM, user.user_id);
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
