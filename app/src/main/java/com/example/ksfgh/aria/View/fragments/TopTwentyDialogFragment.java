package com.example.ksfgh.aria.View.fragments;


import android.databinding.DataBindingUtil;
import android.databinding.ObservableField;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ksfgh.aria.R;
import com.example.ksfgh.aria.ViewModel.TopChartsViewModel;
import com.example.ksfgh.aria.databinding.TopTwentyBinding;

import java.io.Serializable;

/**
 * A simple {@link Fragment} subclass.
 */
public class TopTwentyDialogFragment extends BottomSheetDialogFragment {


    public TopTwentyDialogFragment() {
        // Required empty public constructor
    }

    public static TopTwentyDialogFragment newInstance(TopChartsViewModel viewModel) {
        TopTwentyDialogFragment twentyDialogFragment = new TopTwentyDialogFragment();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putSerializable("viewmodel", viewModel);
        twentyDialogFragment.setArguments(args);

        return twentyDialogFragment;
    }

    private TopChartsViewModel viewModel;
    private TopTwentyBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_top_twenty_dialog, container, false);
        viewModel = (TopChartsViewModel) getArguments().getSerializable("viewmodel");
        binding.setViewmodel(viewModel);

        return binding.getRoot();
    }

}
