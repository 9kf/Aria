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
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.ksfgh.aria.Adapters.MyBandsAdapter;
import com.example.ksfgh.aria.Model.AlbumModel;
import com.example.ksfgh.aria.Model.BandCreationModel;
import com.example.ksfgh.aria.Model.BandMemberModel;
import com.example.ksfgh.aria.Model.BandModel;
import com.example.ksfgh.aria.Model.CustomModelForBandPage;
import com.example.ksfgh.aria.Model.EventModel;
import com.example.ksfgh.aria.Model.MemberModel;
import com.example.ksfgh.aria.Model.VideoModel;
import com.example.ksfgh.aria.R;
import com.example.ksfgh.aria.Rest.RetrofitClient;
import com.example.ksfgh.aria.Singleton;
import com.example.ksfgh.aria.View.activities.BandActivity;
import com.example.ksfgh.aria.View.activities.HomeScreen;
import com.example.ksfgh.aria.databinding.CreateAlbumBinding;
import com.example.ksfgh.aria.databinding.CreateBandBinding;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function4;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

/**
 * Created by ksfgh on 10/02/2018.
 */

public class MyBandsViewModel {

    public ObservableArrayList<CustomModelForBandPage> bandModels;
    public ArrayList<String> bandRoles;
    public ArrayList<String> genres;
    public ObservableField<String> bandImage;
    private ArrayList<BandMemberModel> memberModel;
    public Uri selectedImage = null;
    public Uri selectedVideo = null;
    private HomeScreen activity;

    public MyBandsViewModel(HomeScreen activity) {
        this.activity = activity;
        EventBus.getDefault().register(this);
        bandModels = new ObservableArrayList<>();
        bandRoles = new ArrayList<>();
        genres = new ArrayList<>();
        bandImage = new ObservableField<>();
        bandImage.set(Singleton.getInstance().utilities.getURLForResource(R.drawable.click_for_image));
        memberModel = new ArrayList<>();
        Collections.addAll(genres, activity.getResources().getStringArray(R.array.genres));
        Collections.addAll(bandRoles, activity.getResources().getStringArray(R.array.roles));
        getBands();
    }

    private void getBands() {

        Observable<BandModel[]> observable1 = RetrofitClient.getClient().getbands();
        Observable<AlbumModel[]> observable2 = RetrofitClient.getClient().getAllAlbums();
        Observable<MemberModel[]> observable3 = RetrofitClient.getClient().getBandMembers();
        Observable<EventModel[]> observable4 = RetrofitClient.getClient().getEvents();
        Observable<ArrayList<CustomModelForBandPage>> observable = Observable.zip(observable1, observable2, observable3, observable4,
                new Function4<BandModel[], AlbumModel[], MemberModel[], EventModel[], ArrayList<CustomModelForBandPage>>() {
                    @Override
                    public ArrayList<CustomModelForBandPage> apply(BandModel[] bandModels, AlbumModel[] albumModels, MemberModel[] memberModels, EventModel[] eventModels) throws Exception {
                        ArrayList<CustomModelForBandPage> bands = new ArrayList<>();

                        for(MemberModel members: memberModels){
                            //finding out what band the user belongs to
                            if(members.userId.equals(activity.user.getId())){
                                for(BandModel band: bandModels){
                                    if(members.bandId == band.bandId){
                                        //if the condition is true, the loop has already found what band the user belongs to
                                        //and now the loop will extract the band details like band members, band videos, band albums and band events

                                        ArrayList<MemberModel> bandMembers = new ArrayList<>();
                                        ArrayList<AlbumModel> bandAlbums = new ArrayList<>();
                                        ArrayList<EventModel> bandEvents = new ArrayList<>();

                                        //this loop will determine the members of the band
                                        for(MemberModel members2: memberModels){
                                            if(band.getBandId() == members2.bandId){
                                                bandMembers.add(members2);
                                            }
                                        }

                                        //this loop below will determine the albums of the band
                                        for(AlbumModel albums: albumModels){
                                            if(band.getBandId() == albums.getBandId()){
                                                bandAlbums.add(albums);
                                            }
                                        }

                                        //this loop below will determine the events of the band
                                        for(EventModel events: eventModels){
                                            if(band.getBandId() == events.bandId){
                                                bandEvents.add(events);
                                            }
                                        }

                                        bands.add(new CustomModelForBandPage(band, bandMembers, bandAlbums, bandEvents));

                                    }
                                }
                            }
                        }

                        return bands;
                    }
                });


        Disposable disposable = observable
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<ArrayList<CustomModelForBandPage>>() {
            @Override
            public void onNext(ArrayList<CustomModelForBandPage> customModelForBandPages) {
                bandModels.addAll(customModelForBandPages);
            }

            @Override
            public void onError(Throwable e) {
                Log.d("my bands", e.getMessage() + "getbands");
            }

            @Override
            public void onComplete() {
                for(CustomModelForBandPage band: bandModels){
                    Disposable disposable1 = RetrofitClient.getClient().getBandVideos(String.valueOf(band.band.bandId))
                            .subscribeOn(Schedulers.newThread())
                            .doOnNext(new Consumer<VideoModel[]>() {
                                @Override
                                public void accept(VideoModel[] videoModels) throws Exception {
                                    ArrayList<VideoModel> videoList = new ArrayList<>();
                                    for(VideoModel videos: videoModels){
                                        videoList.add(videos);
                                    }
                                    band.setVideos(videoList);
                                }
                            })
                            .doOnError(new Consumer<Throwable>() {
                                @Override
                                public void accept(Throwable throwable) throws Exception {
                                    Log.d("mybands", throwable.getMessage());
                                }
                            })
                            .doOnComplete(new Action() {
                                @Override
                                public void run() throws Exception {

                                }
                            })
                            .subscribe();

                    EventBus.getDefault().post(disposable1, "myBandDisposables");
                }
            }
        });

        EventBus.getDefault().post(disposable, "myBandDisposables");

    }

    @BindingAdapter("bind:list")
    public static void bindList(RecyclerView view, MyBandsViewModel viewModel){

        view.setLayoutManager(new LinearLayoutManager(view.getContext()));
        view.setAdapter(new MyBandsAdapter(viewModel));
    }

    @BindingAdapter("bind:imgUrl")
    public static void imgUrl(ImageView view, String url){

        Glide.with(view.getContext()).load(url)
                .apply(RequestOptions.overrideOf(70,70))
                .apply(RequestOptions.centerCropTransform())
                .into(view);
    }

    @BindingAdapter("bind:bandImage")
    public static void myBandImage(ImageView view, String url){

        Glide.with(view.getContext()).load(url)
                .apply(RequestOptions.overrideOf(200,200))
                .apply(RequestOptions.centerCropTransform())
                .into(view);
    }

    @BindingAdapter("bind:acAdapter")
    public static void bindAutoCompleteTextView(AutoCompleteTextView view, ArrayList<String> list){
        view.setAdapter(new ArrayAdapter<String>(view.getContext(), android.R.layout.simple_list_item_1, list));
        view.setThreshold(1);
    }

    @BindingAdapter("bind:spnrAdapter")
    public static void bindSpinner(Spinner view, ArrayList<String> list){
        view.setAdapter(new ArrayAdapter<String>(view.getContext(), R.layout.spinner_item_genre, list));
    }

    public void itemClick(CustomModelForBandPage model){
        Singleton.getInstance().currentBand = model;
        EventBus.getDefault().post(model,"bandClicked");
    }

    @Subscriber(tag = "setSelectedImage")
    public void setSelectedImage(Uri image){
        selectedImage = image;
    }

    @Subscriber(tag = "setSelectedVideo")
    public void setSelectedVideo(Uri video){
        selectedVideo = video;
    }

    public void pickPhoto(int identifier){

        Singleton.getInstance().CHANGE_OR_ADD = identifier;

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            String[] perms = {"android.permission.WRITE_EXTERNAL_STORAGE"};
            int permsRequestCode = 200;
            activity.requestPermissions(perms, permsRequestCode);
        } else {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/*");
            activity.startActivityForResult(intent, Singleton.getInstance().PICK_PHOTO);
        }
    }

    public void pickVideo(){

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            String[] perms = {"android.permission.WRITE_EXTERNAL_STORAGE"};
            int permsRequestCode = 200;
            activity.requestPermissions(perms, permsRequestCode);
        } else {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
            intent.setType("video/*");
            activity.startActivityForResult(intent, Singleton.getInstance().PICK_VIDEO);
        }

    }

    @Subscriber(tag="changeBandPic")
    public void setBandPic(String image){
        bandImage.set(image);
    }


    public void addBand(Boolean canCreate){

        if(canCreate){

            LayoutInflater inflater = activity.getLayoutInflater();
            CreateBandBinding binding = DataBindingUtil.inflate(inflater, R.layout.dialog_create_band, null, false);
            binding.setViewmodel(this);

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity, R.style.BlackAlertDialog);
            alertDialogBuilder
                    .setCancelable(false)
                    .setView(binding.getRoot())
                    .setPositiveButton("Create", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                            BandCreationModel model = new BandCreationModel(activity.user.user_id,
                                    binding.actvBandRole.getText().toString(),
                                    binding.etBandName.getText().toString(),
                                    binding.spnrPrimaryGenre.getSelectedItemPosition()+1,
                                    binding.spnrSecondaryGenre.getSelectedItemPosition() +1,
                                    binding.etBandDesc.getText().toString());

                            Disposable disposable = RetrofitClient.getClient().createBand(model)
                                    .subscribeOn(Schedulers.newThread())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .doOnNext(new Consumer<BandMemberModel>() {
                                        @Override
                                        public void accept(BandMemberModel bandMemberModel) throws Exception {
                                            memberModel.add(bandMemberModel);
                                        }
                                    })
                                    .doOnError(new Consumer<Throwable>() {
                                        @Override
                                        public void accept(Throwable throwable) throws Exception {
                                            Log.d("mybands", throwable.getMessage() + "band creation");
                                        }
                                    })
                                    .doOnComplete(new Action() {
                                        @Override
                                        public void run() throws Exception {

                                            if(selectedImage != null){
                                                RequestBody bandId = RequestBody.create(MultipartBody.FORM, String.valueOf(memberModel.get(0).band.id));
                                                File originalFile = new File(bandImage.get().toString());
                                                RequestBody filePart = RequestBody.create(MediaType.parse(activity.getContentResolver().getType(selectedImage)), originalFile);
                                                MultipartBody.Part file = MultipartBody.Part.createFormData("bandPic", originalFile.getName(), filePart);

                                                Disposable disposable1 = RetrofitClient.getClient().editBandPic(bandId, file)
                                                        .subscribeOn(Schedulers.newThread())
                                                        .observeOn(AndroidSchedulers.mainThread())
                                                        .doOnNext(new Consumer<ResponseBody>() {
                                                            @Override
                                                            public void accept(ResponseBody responseBody) throws Exception {

                                                            }
                                                        })
                                                        .doOnError(new Consumer<Throwable>() {
                                                            @Override
                                                            public void accept(Throwable throwable) throws Exception {
                                                                Log.d("mybands", throwable.getMessage() + "edit pic");
                                                            }
                                                        })
                                                        .doOnComplete(new Action() {
                                                            @Override
                                                            public void run() throws Exception {
                                                                bandModels.clear();
                                                                getBands();
                                                                bandImage.set(Singleton.getInstance().utilities.getURLForResource(R.drawable.click_for_image));
                                                                dialog.dismiss();
                                                            }
                                                        })
                                                        .subscribe();

                                                EventBus.getDefault().post(disposable1, "myBandDisposables");
                                            }
                                        }
                                    })
                                    .subscribe();

                            EventBus.getDefault().post(disposable, "myBandDisposables");

                        }
                    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            bandImage.set(Singleton.getInstance().utilities.getURLForResource(R.drawable.click_for_image));
                            dialog.dismiss();
                     }
            });

            AlertDialog dialog = alertDialogBuilder.create();
            dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
            dialog.show();
        }
        else {

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity, R.style.BlackAlertDialog2);
            alertDialogBuilder.setMessage("You have reached the maximum number of joined bands")
            .setNegativeButton("Close", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).show();
        }

    }

    public void checkNumberOfBands() {

        ArrayList<Boolean> isValidToAdd = new ArrayList<>();

        Observable<MemberModel[]> observable1 = RetrofitClient.getClient().getBandMembers();
        Observable<BandModel[]> observable2 = RetrofitClient.getClient().getbands();
        Observable<Boolean> observable = Observable.zip(observable1, observable2, new BiFunction<MemberModel[], BandModel[], Boolean>() {
            @Override
            public Boolean apply(MemberModel[] memberModels, BandModel[] bandModels) throws Exception {

                int count = 0;

                for(MemberModel members: memberModels){
                    if(members.userId.equals(activity.user.getId())){
                        for(BandModel bands:bandModels){
                            if(bands.bandId == members.bandId){
                                count++;
                                break;
                            }
                        }
                    }
                }

                if (count < 3)
                    return true;
                else
                    return false;
            }
        });

        Disposable disposable = observable.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        isValidToAdd.add(aBoolean);

                    }
                })
                .doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                })
                .doOnComplete(new Action() {
                    @Override
                    public void run() throws Exception {
                        if(isValidToAdd.size() != 0)
                            addBand(isValidToAdd.get(0));
                        else
                            addBand(false);
                    }
                })
                .subscribe();


        EventBus.getDefault().post(disposable, "myBandDisposables");
    }

    public void viewBandPage(){
        Intent intent = new Intent(activity, BandActivity.class);
        activity.startActivity(intent);
        EventBus.getDefault().post("","closeBottomSheet");
    }

    @Subscriber(tag = "addBandCoverPhoto")
    public void addBandCoverPhoto(String empty){

        RequestBody bandId = RequestBody.create(MultipartBody.FORM, String.valueOf(Singleton.getInstance().currentBand.band.bandId));
        File originalFile = new File(bandImage.get().toString());
        RequestBody filePart = RequestBody.create(MediaType.parse(activity.getContentResolver().getType(selectedImage)), originalFile);
        MultipartBody.Part file = MultipartBody.Part.createFormData("bandPic", originalFile.getName(), filePart);


        Disposable disposable = RetrofitClient.getClient().addBandCoverPhoto(bandId, file)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<ResponseBody>() {
                    @Override
                    public void onNext(ResponseBody responseBody) {
                        Toast.makeText(activity, "Successfully changed the cover photo of your band", Toast.LENGTH_SHORT).show();
                        EventBus.getDefault().post("","closeBottomSheet");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(activity, "There was an error changing the cover photo of your band", Toast.LENGTH_SHORT).show();
                        Log.d("mybands", e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        bandImage.set(Singleton.getInstance().utilities.getURLForResource(R.drawable.click_for_image));
                    }
                });

        EventBus.getDefault().post(disposable, "myBandDisposables");
    }

    public void addAlbum(CustomModelForBandPage model){

        LayoutInflater inflater = activity.getLayoutInflater();
        CreateAlbumBinding binding = DataBindingUtil.inflate(inflater, R.layout.dialog_create_album, null, false);
        binding.setViewmodel(this);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity, R.style.BlackAlertDialog);
        alertDialogBuilder
                .setView(binding.getRoot())
                .setCancelable(false)
                .setPositiveButton("Create", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                RequestBody bandId = RequestBody.create(MultipartBody.FORM, String.valueOf(model.band.bandId));
                RequestBody albumName = RequestBody.create(MultipartBody.FORM, binding.etAlbumName.getText().toString());
                RequestBody albumDesc = RequestBody.create(MultipartBody.FORM, binding.etAlbumDesc.getText().toString());
                File originalFile = new File(bandImage.get().toString());
                RequestBody filePart = RequestBody.create(MediaType.parse(activity.getContentResolver().getType(selectedImage)), originalFile);
                MultipartBody.Part file = MultipartBody.Part.createFormData("album_pic", originalFile.getName(), filePart);

                Disposable disposable = RetrofitClient.getClient().addAlbum(bandId,albumName,albumDesc,file)
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableObserver<AlbumModel>() {
                            @Override
                            public void onNext(AlbumModel albumModel) {
                                model.albums.add(albumModel);
                                Toast.makeText(activity, "Successfully added the album", Toast.LENGTH_SHORT).show();
                                EventBus.getDefault().post("","closeBottomSheet");
                            }

                            @Override
                            public void onError(Throwable e) {
                                Toast.makeText(activity, "There was an error adding your new album", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onComplete() {
                                bandImage.set(Singleton.getInstance().utilities.getURLForResource(R.drawable.click_for_image));
                            }
                        });

                EventBus.getDefault().post(disposable, "myBandDisposables");
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                bandImage.set(Singleton.getInstance().utilities.getURLForResource(R.drawable.click_for_image));
            }
        });

        AlertDialog dialog = alertDialogBuilder.create();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        dialog.show();

    }

    @Subscriber(tag = "addVideo")
    public void addVideo(String videoPath){

        RequestBody bandId = RequestBody.create(MultipartBody.FORM, String.valueOf(Singleton.getInstance().currentBand.band.bandId));
        RequestBody videoDesc = RequestBody.create(MultipartBody.FORM, "hello");
        RequestBody videoTitle = RequestBody.create(MultipartBody.FORM, "Video Title Here");
        File originalFile = new File(videoPath);
        RequestBody filePart = RequestBody.create(MediaType.parse(activity.getContentResolver().getType(selectedVideo)), originalFile);
        MultipartBody.Part file = MultipartBody.Part.createFormData("video_content", originalFile.getName(), filePart);

        Log.d("mybands", videoPath);

        Disposable disposable = RetrofitClient.getClient().addVideo(bandId,videoTitle,videoDesc, file)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<VideoModel>() {
                    @Override
                    public void onNext(VideoModel videoModel) {
                        Singleton.getInstance().currentBand.videos.add(videoModel);
                        Toast.makeText(activity, "Successfully added the video", Toast.LENGTH_SHORT).show();
                        EventBus.getDefault().post("","closeBottomSheet");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(activity, "There was an error adding the video", Toast.LENGTH_SHORT).show();
                        Log.d("mybands", e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });

        EventBus.getDefault().post(disposable, "myBandDisposables");
    }

}
