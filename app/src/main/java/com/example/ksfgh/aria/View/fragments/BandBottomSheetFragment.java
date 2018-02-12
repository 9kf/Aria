package com.example.ksfgh.aria.View.fragments;


import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ksfgh.aria.Model.CustomModelForBandPage;
import com.example.ksfgh.aria.R;
import com.example.ksfgh.aria.ViewModel.MyBandsViewModel;
import com.example.ksfgh.aria.databinding.BandBottomBinding;

import org.simple.eventbus.EventBus;

/**
 * A simple {@link Fragment} subclass.
 */
public class BandBottomSheetFragment extends BottomSheetDialogFragment {


    public BandBottomSheetFragment() {
        // Required empty public constructor
    }

    private BandBottomBinding binding;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_band_bottom_sheet, container, false);
        EventBus.getDefault().register(this);
        EventBus.getDefault().post(this, "bottomSetVariables");
        return binding.getRoot();
    }

    public void setVariables(MyBandsViewModel viewModel, CustomModelForBandPage model){
        Log.d("mybands", model.band.getBandName());
        binding.setModel(model);
        binding.setViewmodel(viewModel);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }
}
