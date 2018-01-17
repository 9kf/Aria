package com.example.ksfgh.aria.View.activities;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.example.ksfgh.aria.Model.FacebookUserModel;
import com.example.ksfgh.aria.R;
import com.example.ksfgh.aria.Singleton;
import com.example.ksfgh.aria.View.fragments.HomeFragment;
import com.example.ksfgh.aria.ViewModel.HomeScreenViewModel;
import com.example.ksfgh.aria.databinding.ActivityHomeScreenBinding;
import com.google.gson.Gson;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;
import org.simple.eventbus.ThreadMode;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import nl.psdcompany.duonavigationdrawer.views.DuoDrawerLayout;
import nl.psdcompany.duonavigationdrawer.widgets.DuoDrawerToggle;

public class HomeScreen extends AppCompatActivity {

    private ActivityHomeScreenBinding activityHomeScreenBinding;
    private CompositeDisposable compositeDisposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //set content view and data binding
        activityHomeScreenBinding = DataBindingUtil.setContentView(this, R.layout.activity_home_screen);

        //initialize disposable
        compositeDisposable = new CompositeDisposable();

        //set the toolbar
        Toolbar toolbar = activityHomeScreenBinding.toolbar;
        setSupportActionBar(toolbar);

        //register to event bus
        EventBus.getDefault().register(this);

        //initialize the drawer and drawertoggle
        DuoDrawerLayout duoDrawerLayout = activityHomeScreenBinding.duoDrawerLayout;
        DuoDrawerToggle duoDrawerToggle = new DuoDrawerToggle(this, duoDrawerLayout, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        duoDrawerLayout.setDrawerListener(duoDrawerToggle);
        duoDrawerToggle.syncState();

        //set the viewmodel
        FacebookUserModel user = new Gson().fromJson(getSharedPreferences(Singleton.getInstance().PREFERENCE_NAME, MODE_PRIVATE).getString("user", null),
                FacebookUserModel.class);
        HomeScreenViewModel viewModel = new HomeScreenViewModel(user, this, duoDrawerLayout);
        activityHomeScreenBinding.setViewModel(viewModel);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.searchbar_menu, menu);

        MenuItem item = menu.findItem(R.id.action_search);
        activityHomeScreenBinding.svSearchBar.setMenuItem(item);

        return true;
    }

    @Subscriber(tag = "switchFragment")
    private void switchFragment(Fragment fragment){
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.layoutContainer, fragment)
                .commit();
    }

    @Subscriber(tag = "addHomeDisposables", mode = ThreadMode.ASYNC)
    private void disposeObservable(Disposable disposable){
        compositeDisposable.dispose();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.dispose();
        EventBus.getDefault().unregister(this);
    }
}
