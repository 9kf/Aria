package com.example.ksfgh.aria.View.activities;

import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.ksfgh.aria.R;
import com.example.ksfgh.aria.Singleton;
import com.example.ksfgh.aria.databinding.ActivityBandBinding;

public class BandActivity extends AppCompatActivity {

    private ActivityBandBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_band);
        binding.setModel(Singleton.getInstance().currentBand);
    }
}
