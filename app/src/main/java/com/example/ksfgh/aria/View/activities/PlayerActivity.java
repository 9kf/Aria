package com.example.ksfgh.aria.View.activities;

import android.databinding.DataBindingUtil;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.ksfgh.aria.R;
import com.example.ksfgh.aria.View.fragments.PlayerFragment;
import com.example.ksfgh.aria.View.fragments.QueueFragment;
import com.example.ksfgh.aria.ViewModel.PlayerActivityViewModel;
import com.example.ksfgh.aria.databinding.PlayerBinding;

import org.simple.eventbus.EventBus;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class PlayerActivity extends AppCompatActivity {

    private PlayerBinding playerBinding;
    private PlayerActivityViewModel playerActivityViewModel;
    private static PlayerFragment playerFragment;
    private static QueueFragment queueFragment;

    private CompositeDisposable compositeDisposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        playerBinding = DataBindingUtil.setContentView(this, R.layout.activity_player);
        playerActivityViewModel = new PlayerActivityViewModel(this);
        playerBinding.setViewmodel(playerActivityViewModel);

        if(savedInstanceState == null){
            playerFragment = new PlayerFragment();
            queueFragment = new QueueFragment();
        }

        switchFragment(playerFragment);
    }

    private void switchFragment(Fragment fragment){
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.flContainer, fragment)
                .commit();
    }

    public PlayerActivityViewModel getPlayerViewModel(){
        return playerActivityViewModel;
    }

    public void addDisposables(Disposable disposable){
        if(compositeDisposable == null)
            compositeDisposable = new CompositeDisposable();

        compositeDisposable.add(disposable);
    }

    public void clearDisposable(){
        compositeDisposable.clear();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(playerActivityViewModel);
        if(compositeDisposable != null)
            compositeDisposable.dispose();
    }
}
