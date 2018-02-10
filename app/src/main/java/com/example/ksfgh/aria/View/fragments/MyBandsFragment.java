package com.example.ksfgh.aria.View.fragments;


import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ksfgh.aria.R;
import com.example.ksfgh.aria.View.activities.HomeScreen;
import com.example.ksfgh.aria.ViewModel.MyBandsViewModel;
import com.example.ksfgh.aria.databinding.MyBandsBinding;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyBandsFragment extends Fragment {


    public MyBandsFragment() {
        // Required empty public constructor
    }


    private MyBandsBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_my_bands, container, false);
        binding.setViewmodel(new MyBandsViewModel((HomeScreen) getActivity()));
        return binding.getRoot();
    }

}
