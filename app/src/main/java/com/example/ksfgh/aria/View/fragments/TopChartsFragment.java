package com.example.ksfgh.aria.View.fragments;


import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ksfgh.aria.R;
import com.example.ksfgh.aria.ViewModel.TopChartsViewModel;
import com.example.ksfgh.aria.databinding.TopChartsBinding;

/**
 * A simple {@link Fragment} subclass.
 */
public class TopChartsFragment extends Fragment {


    public TopChartsFragment() {
        // Required empty public constructor
    }

    TopChartsBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_top_charts, container,false);
        binding.setViewmodel(new TopChartsViewModel());
        return binding.getRoot();
    }

}
