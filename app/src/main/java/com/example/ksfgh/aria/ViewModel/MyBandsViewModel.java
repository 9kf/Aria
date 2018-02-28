package com.example.ksfgh.aria.ViewModel;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
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
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TimePicker;
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
import com.example.ksfgh.aria.Model.SongModel;
import com.example.ksfgh.aria.Model.VideoModel;
import com.example.ksfgh.aria.R;
import com.example.ksfgh.aria.Rest.RetrofitClient;
import com.example.ksfgh.aria.Singleton;
import com.example.ksfgh.aria.View.activities.BandActivity;
import com.example.ksfgh.aria.View.activities.HomeScreen;
import com.example.ksfgh.aria.databinding.AddEventBinding;
import com.example.ksfgh.aria.databinding.AddSongToAlbumBinding;
import com.example.ksfgh.aria.databinding.AddVideoBinding;
import com.example.ksfgh.aria.databinding.CreateAlbumBinding;
import com.example.ksfgh.aria.databinding.CreateBandBinding;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
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
    public Uri selectedAudio = null;
    public ObservableField<String> videoPath;
    public ObservableField<String> audioPath;
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
        videoPath = new ObservableField<>();
        videoPath.set("");
        audioPath = new ObservableField<>();
        audioPath.set("");
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
                .apply(RequestOptions.centerInsideTransform())
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

    @Subscriber(tag = "setVideoPath")
    public void setVideoPath(String path){

        videoPath.set(path);
    }

    @Subscriber(tag = "setSelectedAudio")
    public void setSelectedAudio(Uri audio){
        selectedAudio = audio;
    }

    @Subscriber(tag = "setAudioPath")
    public void setAudioPath(String path){
        audioPath.set(path);
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

    public void pickAudio(){
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("audio/*");
        activity.startActivityForResult(intent, Singleton.getInstance().PICK_AUDIO);
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

                            Toast.makeText(activity, "Creating band...", Toast.LENGTH_SHORT).show();

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
                                            Toast.makeText(activity, "Band created succesfully", Toast.LENGTH_SHORT).show();
                                            Toast.makeText(activity, "Uploading the image...", Toast.LENGTH_SHORT).show();
                                            if(selectedImage != null){
                                                RequestBody bandId = RequestBody.create(MultipartBody.FORM, String.valueOf(memberModel.get(0).band.id));
                                                File originalFile = new File(bandImage.get().toString());
                                                RequestBody filePart = RequestBody.create(MediaType.parse(activity.getContentResolver().getType(selectedImage)), originalFile);
                                                MultipartBody.Part file = MultipartBody.Part.createFormData("bandPic", originalFile.getName(), filePart);

                                                Disposable disposable1 = RetrofitClient.getClient().editBandPic(bandId, file)
                                                        .subscribeOn(Schedulers.newThread())
                                                        .observeOn(AndroidSchedulers.mainThread())
                                                        .subscribeWith(new DisposableObserver<ResponseBody>() {
                                                            @Override
                                                            public void onNext(ResponseBody responseBody) {

                                                            }

                                                            @Override
                                                            public void onError(Throwable e) {
                                                                Toast.makeText(activity, "There was a problem uploading the image of the band", Toast.LENGTH_SHORT).show();
                                                                Log.d("mybands",e.getMessage() + " edit pic");
                                                            }

                                                            @Override
                                                            public void onComplete() {
                                                                bandModels.clear();
                                                                getBands();
                                                                bandImage.set(Singleton.getInstance().utilities.getURLForResource(R.drawable.click_for_image));
                                                                dialog.dismiss();
                                                            }
                                                        });

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

        EventBus.getDefault().post("","closeBottomSheet");
        Toast.makeText(activity, "Changing the cover photo of the band ...", Toast.LENGTH_SHORT).show();

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
                        try {
                            Log.d("mybands", responseBody.string());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(activity, "There was an error changing the cover photo of your band", Toast.LENGTH_SHORT).show();
                        Log.d("mybands", e.getMessage() + " ");
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

        binding.btnAddReleaseDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                new DatePickerDialog(activity, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        if((i1+1) < 10){
                            if(i2 < 10){
                                binding.tvAlbumReleaseDate.setText("0"+(i1+1)+"/0"+i2+"/"+i);
                            }
                            else {
                                binding.tvAlbumReleaseDate.setText("0"+(i1+1)+"/"+i2+"/"+i);
                            }
                        }
                        else {
                            if(i2 < 10){
                                binding.tvAlbumReleaseDate.setText((i1+1)+"/0"+i2+"/"+i);
                            }
                            else {
                                binding.tvAlbumReleaseDate.setText((i1+1)+"/"+i2+"/"+i);
                            }
                        }

                    }
                }, year, month, day).show();
            }
        });

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity, R.style.BlackAlertDialog);
        alertDialogBuilder
                .setView(binding.getRoot())
                .setCancelable(false)
                .setPositiveButton("Create", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(activity, "Adding album ...", Toast.LENGTH_SHORT).show();
                EventBus.getDefault().post("","closeBottomSheet");

                RequestBody bandId = RequestBody.create(MultipartBody.FORM, String.valueOf(model.band.bandId));
                RequestBody albumName = RequestBody.create(MultipartBody.FORM, binding.etAlbumName.getText().toString());
                RequestBody albumDesc = RequestBody.create(MultipartBody.FORM, binding.etAlbumDesc.getText().toString());
                RequestBody releaseDate = RequestBody.create(MultipartBody.FORM, binding.tvAlbumReleaseDate.getText().toString());
                File originalFile = new File(bandImage.get().toString());
                RequestBody filePart = RequestBody.create(MediaType.parse(activity.getContentResolver().getType(selectedImage)), originalFile);
                MultipartBody.Part file = MultipartBody.Part.createFormData("album_pic", originalFile.getName(), filePart);

                Disposable disposable = RetrofitClient.getClient().addAlbum(bandId,albumName,albumDesc, releaseDate,file)
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableObserver<AlbumModel>() {
                            @Override
                            public void onNext(AlbumModel albumModel) {
                                Singleton.getInstance().currentBand.albums.add(albumModel);
                                Toast.makeText(activity, "Successfully added the album", Toast.LENGTH_SHORT).show();
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

    public void addVideo(){

        LayoutInflater inflater = activity.getLayoutInflater();
        AddVideoBinding binding = DataBindingUtil.inflate(inflater, R.layout.dialog_add_video, null, false);
        binding.setViewmodel(this);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity, R.style.BlackAlertDialog);
        alertDialogBuilder
                .setView(binding.getRoot())
                .setCancelable(false)
                .setPositiveButton("Add video", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {

                    EventBus.getDefault().post("","closeBottomSheet");
                    Toast.makeText(activity, "Adding the video ...", Toast.LENGTH_SHORT).show();

                    RequestBody bandId = RequestBody.create(MultipartBody.FORM, String.valueOf(Singleton.getInstance().currentBand.band.bandId));
                    RequestBody videoDesc = RequestBody.create(MultipartBody.FORM, "hello");
                    RequestBody videoTitle = RequestBody.create(MultipartBody.FORM, binding.etVideoTitle.getText().toString());
                    File originalFile = new File(videoPath.get().toString());
                    RequestBody filePart = RequestBody.create(MediaType.parse(activity.getContentResolver().getType(selectedVideo)), originalFile);
                    MultipartBody.Part file = MultipartBody.Part.createFormData("video_content", originalFile.getName(), filePart);

                    Disposable disposable = RetrofitClient.getClient().addVideo(bandId,videoTitle,videoDesc, file)
                            .subscribeOn(Schedulers.newThread())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeWith(new DisposableObserver<VideoModel>() {
                                @Override
                                public void onNext(VideoModel videoModel) {
                                    Singleton.getInstance().currentBand.videos.add(videoModel);
                                    Toast.makeText(activity, "Successfully added the video", Toast.LENGTH_SHORT).show();

                                }

                                @Override
                                public void onError(Throwable e) {
                                    Toast.makeText(activity, "There was an error adding the video", Toast.LENGTH_SHORT).show();
                                    Log.d("mybands", e.getMessage());
                                }

                                @Override
                                public void onComplete() {
                                    videoPath.set("");
                                }
                            });

                    EventBus.getDefault().post(disposable, "myBandDisposables");

                }
            }).setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    videoPath.set("");
                }
            });

        AlertDialog dialog = alertDialogBuilder.create();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        dialog.show();
    }


    String time;
    public void addEvent(CustomModelForBandPage model){

        AddEventBinding binding = DataBindingUtil.inflate(activity.getLayoutInflater(), R.layout.dialog_add_event, null, false);
        binding.setViewmodel(this);

        binding.btnAddTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar currentTime = Calendar.getInstance();
                int hour = currentTime.get(Calendar.HOUR_OF_DAY);
                int minute = currentTime.get(Calendar.MINUTE);
                new TimePickerDialog(activity, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int i, int i1) {
                        if(i1 > 10){
                            if(i == 12){
                                time = "00:" + i1 + ":00";
                                binding.tvEventTime.setText("00:" + i1 + " PM");
                            }
                            else if( i == 24){
                                time = "00:" + i1 + ":00";
                                binding.tvEventTime.setText("00:" + i1 + " AM");
                            }
                            else if( i < 10){
                                time = "0"+i + ":" + i1 + ":00";
                                binding.tvEventTime.setText("0"+i + ":" + i1 + " AM");
                            }
                            else if(i > 9 && i <= 11){
                                time = i + ":" + i1 + ":00";
                                binding.tvEventTime.setText(i + ":" + i1 + " AM");
                            }
                            else if( i > 11){
                                time = i + ":" + i1 + ":00";
                                binding.tvEventTime.setText((i-12) + ":" + i1 + " PM");
                            }
                        }
                        else {
                            if(i == 12){
                                time = "00:0" + i1 + ":00";
                                binding.tvEventTime.setText("00:0" + i1 + " PM");
                            }
                            else if( i == 24){
                                time = "00:0" + i1 + ":00";
                                binding.tvEventTime.setText("00:0" + i1 + " AM");
                            }
                            else if( i < 10){
                                time = "0"+i + ":0" + i1 + ":00";
                                binding.tvEventTime.setText("0"+i + ":0" + i1 + " AM");
                            }
                            else if(i > 9 && i <= 11){
                                time = i + ":0" + i1 + ":00";
                                binding.tvEventTime.setText(i + ":0" + i1 + " AM");
                            }
                            else if( i > 11){
                                time = i + ":0" + i1 + ":00";
                                binding.tvEventTime.setText((i-12) + ":0" + i1 + " PM");
                            }
                        }

                    }
                }, hour, minute, true).show();
            }
        });

        binding.btnAddDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                new DatePickerDialog(activity, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        if((i1+1) < 10){
                            if(i2 < 10){
                                binding.tvEventDate.setText("0"+(i1+1)+"/0"+i2+"/"+i);
                            }
                            else {
                                binding.tvEventDate.setText("0"+(i1+1)+"/"+i2+"/"+i);
                            }
                        }
                        else {
                            if(i2 < 10){
                                binding.tvEventDate.setText((i1+1)+"/0"+i2+"/"+i);
                            }
                            else {
                                binding.tvEventDate.setText((i1+1)+"/"+i2+"/"+i);
                            }
                        }

                    }
                }, year, month, day).show();
            }
        });

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity, R.style.BlackAlertDialog);
        alertDialogBuilder
                .setView(binding.getRoot())
                .setCancelable(false)
                .setPositiveButton("Add event", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {

                    Toast.makeText(activity, "Adding the event...", Toast.LENGTH_SHORT).show();
                    EventBus.getDefault().post("","closeBottomSheet");

                    Disposable disposable = RetrofitClient.getClient()
                            .addEvent(String.valueOf(Singleton.getInstance().currentBand.band.bandId), binding.etEventName.getText().toString(), binding.tvEventDate.getText().toString(),
                                    time, binding.etEventVenue.getText().toString(), binding.etEventLocation.getText().toString())
                            .subscribeOn(Schedulers.newThread())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeWith(new DisposableObserver<EventModel>() {
                                @Override
                                public void onNext(EventModel eventModel) {
                                    model.events.add(eventModel);
                                    Toast.makeText(activity, "Successfully added the event", Toast.LENGTH_SHORT).show();

                                }

                                @Override
                                public void onError(Throwable e) {
                                    Toast.makeText(activity, "There was an error adding the event", Toast.LENGTH_SHORT).show();
                                    Log.d("mybands", e.getMessage() + " ");
                                }

                                @Override
                                public void onComplete() {

                                }
                            });
                    EventBus.getDefault().post(disposable, "myBandDisposables");
                }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        AlertDialog dialog = alertDialogBuilder.create();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        dialog.show();

    }

    public void addSongToAlbum(CustomModelForBandPage model){
        AddSongToAlbumBinding binding = DataBindingUtil.inflate(activity.getLayoutInflater(), R.layout.dialog_add_song_to_album, null, false);
        binding.setViewmodel(this);

        ArrayList<String> albums = new ArrayList<>();
        for(AlbumModel bandAlbums: model.albums){
            albums.add(bandAlbums.getAlbumName());
        }

        ArrayAdapter<String> albumAdapter = new ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, albums);
        binding.spnrAlbum.setAdapter(albumAdapter);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity, R.style.BlackAlertDialog);
        alertDialogBuilder
                .setView(binding.getRoot())
                .setCancelable(false)
                .setPositiveButton("Create", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        EventBus.getDefault().post("","closeBottomSheet");
                        Toast.makeText(activity, "Adding the song...", Toast.LENGTH_SHORT).show();
                        int selectedAlbumId = 0;
                        for (AlbumModel albumModel: model.albums){
                            if(albumModel.getAlbumName().equals(binding.spnrAlbum.getSelectedItem().toString())){
                                selectedAlbumId = albumModel.getAlbumId();
                                break;
                            }
                        }

                        RequestBody albumId = RequestBody.create(MultipartBody.FORM, String.valueOf(selectedAlbumId));
                        RequestBody songTitle = RequestBody.create(MultipartBody.FORM, binding.etSongTitle.getText().toString());
                        RequestBody songDesc = RequestBody.create(MultipartBody.FORM, binding.etSongDesc.getText().toString());
                        RequestBody genreId = RequestBody.create(MultipartBody.FORM, String.valueOf(binding.spnrSongGenre.getSelectedItemPosition()+1));
                        RequestBody bandId = RequestBody.create(MultipartBody.FORM, String.valueOf(model.band.bandId));

                        File originalFile = new File(audioPath.get());
                        RequestBody filePart = RequestBody.create(MediaType.parse(activity.getContentResolver().getType(selectedAudio)), originalFile);
                        MultipartBody.Part file = MultipartBody.Part.createFormData("song_audio", originalFile.getName(), filePart);

                        Disposable disposable = RetrofitClient.getClient().addSong(albumId, songTitle, songDesc, genreId, bandId, file)
                                .subscribeOn(Schedulers.newThread())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribeWith(new DisposableObserver<SongModel>() {
                                    @Override
                                    public void onNext(SongModel songModel) {
                                        Log.d("song", songModel.songAudio + " ");
                                        Toast.makeText(activity, "Successfully added", Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        Log.d("song error", e.getMessage());
                                        Toast.makeText(activity, "There was an error adding the song", Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onComplete() {

                                    }
                                });

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        audioPath.set("");
                    }
                });

        AlertDialog dialog = alertDialogBuilder.create();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        dialog.show();
    }


}
