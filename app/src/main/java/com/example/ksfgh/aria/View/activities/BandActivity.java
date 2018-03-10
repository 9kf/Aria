package com.example.ksfgh.aria.View.activities;

import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.ksfgh.aria.R;
import com.example.ksfgh.aria.Singleton;
import com.example.ksfgh.aria.ViewModel.BandActivityViewModel;
import com.example.ksfgh.aria.databinding.ActivityBandBinding;
import com.google.android.exoplayer2.SimpleExoPlayer;

import org.simple.eventbus.EventBus;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class BandActivity extends AppCompatActivity {

    private ActivityBandBinding binding;
    private BandActivityViewModel viewModel;
    private CompositeDisposable compositeDisposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_band);
        binding.setModel(Singleton.getInstance().currentBand);
        viewModel = new BandActivityViewModel(this);
        binding.setViewmodel(viewModel);
    }

    public void addDisposables(Disposable disposable){
        if (compositeDisposable == null)
            compositeDisposable = new CompositeDisposable();

        compositeDisposable.add(disposable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(compositeDisposable != null)
            compositeDisposable.dispose();

        for (SimpleExoPlayer exo:viewModel.exoPlayers){
            exo.release();
        }

        EventBus.getDefault().unregister(this);
        EventBus.getDefault().unregister(viewModel);
        Singleton.getInstance().videoPlayed = false;

    }
}
