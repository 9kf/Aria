package com.example.ksfgh.aria.View.fragments;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ksfgh.aria.R;
import com.example.ksfgh.aria.ViewModel.TopChartsViewModel;
import com.example.ksfgh.aria.databinding.TopTwentyBinding;

/**
 * Created by ksfgh on 07/03/2018.
 */

public class Top20ThisWeekDialogFragment extends BottomSheetDialogFragment {

    public Top20ThisWeekDialogFragment(){

    }

    public static Top20ThisWeekDialogFragment newInstance(TopChartsViewModel viewModel){

        Top20ThisWeekDialogFragment top20ThisWeekDialogFragment = new Top20ThisWeekDialogFragment();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putSerializable("viewmodel", viewModel);
        top20ThisWeekDialogFragment.setArguments(args);

        return  top20ThisWeekDialogFragment;

    }

    private TopChartsViewModel viewModel;
    private TopTwentyBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_top_twenty_dialog, container, false);
        viewModel = (TopChartsViewModel) getArguments().getSerializable("viewmodel");
        binding.setViewmodel(viewModel);
        return binding.getRoot();
    }
}
