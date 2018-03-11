package com.example.ksfgh.aria.View.fragments;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableArrayList;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.ksfgh.aria.Adapters.NotificationAdapter;
import com.example.ksfgh.aria.Model.BandModel;
import com.example.ksfgh.aria.Model.NotificationModel;
import com.example.ksfgh.aria.R;
import com.example.ksfgh.aria.Rest.RetrofitClient;
import com.example.ksfgh.aria.Singleton;
import com.example.ksfgh.aria.databinding.NotificationBinding;

import java.io.IOException;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

/**
 * A simple {@link Fragment} subclass.
 */
public class NotificationFragment extends Fragment {


    public NotificationFragment() {
        // Required empty public constructor
    }

    private NotificationBinding binding;
    public ObservableArrayList<NotificationModel> notifs;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_notification, container, false);
        notifs = new ObservableArrayList<>();

        binding.rvUserNotifs.setLayoutManager(new LinearLayoutManager(binding.rvUserNotifs.getContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.rvUserNotifs.setAdapter(new NotificationAdapter(this));

        getUserNotifs();
        return binding.getRoot();
    }

    private void getUserNotifs() {
        Disposable disposable = RetrofitClient.getClient().getUserNotifications(Singleton.homeScreen.user.user_id)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<NotificationModel[]>() {
                    @Override
                    public void onNext(NotificationModel[] notificationModels) {
                        for(NotificationModel notificationModel:notificationModels){

                            Disposable disposable = RetrofitClient.getClient().getbands()
                                    .subscribeOn(Schedulers.newThread())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribeWith(new DisposableObserver<BandModel[]>() {
                                        @Override
                                        public void onNext(BandModel[] bandModels) {
                                            for(BandModel band:bandModels){
                                                if(band.bandId == notificationModel.band_id){
                                                    notificationModel.setBandName(band.bandName);
                                                    notificationModel.setBandImage(band.bandPic);
                                                    break;
                                                }
                                            }
                                        }

                                        @Override
                                        public void onError(Throwable e) {

                                        }

                                        @Override
                                        public void onComplete() {

                                        }
                                    });


                            notifs.add(notificationModel);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void acceptInvitation(NotificationModel model){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Singleton.homeScreen, R.style.BlackAlertDialog);
        alertDialogBuilder.setMessage("Are you sure you want to join the band?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Disposable disposable = RetrofitClient.getClient().addBandMember(model.user_id, model.band_id, model.bandrole)
                                .subscribeOn(Schedulers.newThread())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribeWith(new DisposableObserver<ResponseBody>() {
                                    @Override
                                    public void onNext(ResponseBody responseBody) {
                                        Toast.makeText(Singleton.homeScreen, "You joined " + model.bandName.get(), Toast.LENGTH_SHORT).show();
                                        Disposable disposable1 = RetrofitClient.getClient().declineInvitation(model)
                                                .subscribeOn(Schedulers.newThread())
                                                .observeOn(AndroidSchedulers.mainThread())
                                                .subscribeWith(new DisposableObserver<String>() {
                                                    @Override
                                                    public void onNext(String s) {
                                                        if(s.equals("true"))
                                                            notifs.remove(model);
                                                    }

                                                    @Override
                                                    public void onError(Throwable e) {
                                                        Log.d("invitation", e.getMessage()+ " invitation");
                                                    }

                                                    @Override
                                                    public void onComplete() {

                                                    }
                                                });
                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        Log.d("invitation", "There was a problem joining the band");
                                    }

                                    @Override
                                    public void onComplete() {
                                    }
                                });

                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    public void declineInvitation(NotificationModel model){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Singleton.homeScreen, R.style.BlackAlertDialog);
        alertDialogBuilder.setMessage("Are you sure you want to decline the invitation?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Disposable disposable = RetrofitClient.getClient().declineInvitation(model)
                                .subscribeOn(Schedulers.newThread())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribeWith(new DisposableObserver<String>() {
                                    @Override
                                    public void onNext(String s) {
                                        if(s.equals("true"))
                                            notifs.remove(model);
                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        Log.d("invitation", e.getMessage()+ " invitation");
                                    }

                                    @Override
                                    public void onComplete() {

                                    }
                                });
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

}
