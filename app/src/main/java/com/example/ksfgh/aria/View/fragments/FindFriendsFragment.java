package com.example.ksfgh.aria.View.fragments;


import android.databinding.DataBindingUtil;
import android.databinding.ObservableArrayList;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ksfgh.aria.Adapters.UserFriendsAdapter;
import com.example.ksfgh.aria.Model.UserFacebookFriends;
import com.example.ksfgh.aria.Model.UserModel;
import com.example.ksfgh.aria.R;
import com.example.ksfgh.aria.Rest.RetrofitClient;
import com.example.ksfgh.aria.Singleton;
import com.example.ksfgh.aria.databinding.FindFriendsBinding;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.simple.eventbus.EventBus;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * A simple {@link Fragment} subclass.
 */
public class FindFriendsFragment extends Fragment {


    public FindFriendsFragment() {
        // Required empty public constructor
    }


    private FindFriendsBinding binding;
    public ObservableArrayList<UserFacebookFriends> userFacebookFriends;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_find_friends, container, false);
        userFacebookFriends = new ObservableArrayList<>();
        binding.rvFriends.setLayoutManager(new LinearLayoutManager(binding.rvFriends.getContext(), LinearLayoutManager.VERTICAL, false));
        binding.rvFriends.setAdapter(new UserFriendsAdapter(this));
        getFriends();
        return binding.getRoot();
    }

    private void getFriends() {

        GraphRequest graphRequest = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {

                try {
                    for(int i = 0; i < object.getJSONObject("friends").getJSONArray("data").length(); i++){
                        userFacebookFriends.add(new UserFacebookFriends(
                                object.getJSONObject("friends").getJSONArray("data").getJSONObject(i).getString("id"),
                                object.getJSONObject("friends").getJSONArray("data").getJSONObject(i).getString("name"),
                                object.getJSONObject("friends").getJSONArray("data").getJSONObject(i).getJSONObject("picture").getJSONObject("data").getString("url")
                        ));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "friends{id,name,picture,email}");
        graphRequest.setParameters(parameters);
        graphRequest.executeAsync();

    }

    public void userClick(String name){
        Disposable disposable = RetrofitClient.getClient().getUsers2()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<UserModel[]>() {
                    @Override
                    public void onNext(UserModel[] userModels) {
                        for(UserModel user: userModels){
                            if(user.fullname.equals(name)){
                                Singleton.getInstance().currentUser.set(user);
                                EventBus.getDefault().post("", "changeUser");
                                EventBus.getDefault().post(Singleton.getInstance().userFragment, "switchFragment");
                                Singleton.homeScreen.viewModel.onDrawerItemClick(null);
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
    }

}
