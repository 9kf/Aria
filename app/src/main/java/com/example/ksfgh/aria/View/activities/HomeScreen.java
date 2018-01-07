package com.example.ksfgh.aria.View.activities;

import android.annotation.SuppressLint;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.example.ksfgh.aria.R;
import com.example.ksfgh.aria.View.fragments.Dummyragment;
import com.example.ksfgh.aria.databinding.ActivityHomeScreenBinding;
import com.example.ksfgh.aria.databinding.DrawerMenuBinding;
import com.yarolegovich.slidingrootnav.SlidingRootNav;
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder;

import butterknife.BindView;

public class HomeScreen extends AppCompatActivity {

    private ActivityHomeScreenBinding activityHomeScreenBinding;
    private DrawerMenuBinding drawerMenuBinding;
    private SlidingRootNav slidingRootNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        drawerMenuBinding = DataBindingUtil.setContentView(this, R.layout.drawer_menu);
        activityHomeScreenBinding = DataBindingUtil.setContentView(this, R.layout.activity_home_screen);

        Toolbar toolbar = activityHomeScreenBinding.toolbar;
        setSupportActionBar(toolbar);

        slidingRootNav = new SlidingRootNavBuilder(this)
                .withToolbarMenuToggle(toolbar)
                .withMenuLayout(R.layout.drawer_menu)
                .withSavedState(savedInstanceState)
                .inject();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.layoutContainer, new Dummyragment())
                .commit();

    }
}
