package com.example.ksfgh.aria.View.activities;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.example.ksfgh.aria.Model.FacebookUserModel;
import com.example.ksfgh.aria.R;
import com.example.ksfgh.aria.Singleton;
import com.example.ksfgh.aria.View.fragments.HomeFragment;
import com.example.ksfgh.aria.ViewModel.HomeScreenViewModel;
import com.example.ksfgh.aria.databinding.ActivityHomeScreenBinding;
import com.google.gson.Gson;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import nl.psdcompany.duonavigationdrawer.views.DuoDrawerLayout;
import nl.psdcompany.duonavigationdrawer.widgets.DuoDrawerToggle;

public class HomeScreen extends AppCompatActivity {

    private ActivityHomeScreenBinding activityHomeScreenBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityHomeScreenBinding = DataBindingUtil.setContentView(this, R.layout.activity_home_screen);

        Toolbar toolbar = activityHomeScreenBinding.toolbar;
        setSupportActionBar(toolbar);
        EventBus.getDefault().register(this);

        DuoDrawerLayout duoDrawerLayout = activityHomeScreenBinding.duoDrawerLayout;
        DuoDrawerToggle duoDrawerToggle = new DuoDrawerToggle(this, duoDrawerLayout, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        duoDrawerLayout.setDrawerListener(duoDrawerToggle);
        duoDrawerToggle.syncState();

        FacebookUserModel user = new Gson().fromJson(getSharedPreferences(Singleton.getInstance().PREFERENCE_NAME, MODE_PRIVATE).getString("user", null),
                FacebookUserModel.class);
        HomeScreenViewModel viewModel = new HomeScreenViewModel(user, this, duoDrawerLayout);
        activityHomeScreenBinding.setViewModel(viewModel);
    }

    @Subscriber(tag = "drawer")
    private void switchFragment(Fragment fragment){
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.layoutContainer, fragment)
                .commit();
    }
}
