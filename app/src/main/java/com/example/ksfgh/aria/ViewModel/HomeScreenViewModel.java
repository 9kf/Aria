package com.example.ksfgh.aria.ViewModel;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.BindingAdapter;
import android.databinding.ObservableField;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.ksfgh.aria.Model.FacebookUserModel;
import com.example.ksfgh.aria.R;
import com.example.ksfgh.aria.Singleton;
import com.example.ksfgh.aria.View.activities.HomeScreen;
import com.example.ksfgh.aria.View.activities.StartScreen;
import com.example.ksfgh.aria.View.fragments.FeedFragment;
import com.example.ksfgh.aria.View.fragments.HomeFragment;
import com.facebook.login.LoginManager;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.simple.eventbus.EventBus;

import nl.psdcompany.duonavigationdrawer.views.DuoDrawerLayout;

/**
 * Created by ksfgh on 08/01/2018.
 */

public class HomeScreenViewModel {

    public FacebookUserModel userModel;
    private HomeScreen homeScreen;
    public ObservableField<String> userName = new ObservableField<>();
    public ObservableField<String> url = new ObservableField<>();

    private View currentView;
    private DuoDrawerLayout duoDrawerLayout;

    public HomeScreenViewModel(FacebookUserModel userModel, HomeScreen homeScreen, DuoDrawerLayout duoDrawerLayout){
        this.userModel = userModel;
        this.homeScreen = homeScreen;
        this.duoDrawerLayout = duoDrawerLayout;
        userName.set(userModel.fname + " " + userModel.lname);
        url.set(userModel.pic);
        onDrawerItemClick(homeScreen.findViewById(R.id.llHome));
    }

    @BindingAdapter({"bind:url"})
    public static void setUserPic(ImageView view, String url){
        ImageLoader.getInstance().displayImage(url, view);
    }

    public void onDrawerItemClick(View view){

        switch (view.getId()){
            case R.id.llHome:
                homeScreen.getSupportActionBar().setTitle("Home");
                EventBus.getDefault().post(new HomeFragment(), "drawer");
                break;

            case R.id.llFeed:
                homeScreen.getSupportActionBar().setTitle("Feed");
                EventBus.getDefault().post(new FeedFragment(), "drawer");
                break;

            case R.id.llTopCharts:
                break;

            case R.id.llNotifications:
                break;

            case R.id.llMyBands:
                break;

            case R.id.llFindFriends:
                break;

            case R.id.llSettings:
                break;

            case R.id.llLogout:

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(homeScreen);
                alertDialogBuilder
                        .setMessage("Are you sure you want to log out?")
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                SharedPreferences.Editor editor = homeScreen.getSharedPreferences(Singleton.getInstance().PREFERENCE_NAME, Context.MODE_PRIVATE).edit();
                                editor.remove("user");
                                editor.commit();

                                LoginManager.getInstance().logOut();

                                Intent intent = new Intent(homeScreen, StartScreen.class);
                                homeScreen.startActivity(intent);
                                homeScreen.finish();
                            }
                        })
                        .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Log.d("hello", "no");
                            }
                        })
                        .show();

                break;
        }

        if(view.getId() != R.id.llLogout){

            if(currentView == null){
                currentView = view;
                view.setBackgroundColor(Color.parseColor("#E57C1F"));
            }
            else {
                currentView.setBackgroundColor(Color.parseColor("#232323"));
                currentView = view;
                currentView.setBackgroundColor(Color.parseColor("#E57C1F"));
            }

            duoDrawerLayout.closeDrawer();
        }

    }
}
