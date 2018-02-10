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

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyBandsFragment extends Fragment {


    public MyBandsFragment() {
        // Required empty public constructor
    }


    private MyBandsBinding binding;
    private CompositeDisposable compositeDisposable;
    private MyBandsViewModel viewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_my_bands, container, false);
        viewModel = new MyBandsViewModel((HomeScreen) getActivity());
        binding.setViewmodel(viewModel);
        compositeDisposable = new CompositeDisposable();
        EventBus.getDefault().register(this);
        return binding.getRoot();
    }

    @Subscriber(tag = "myBandDisposables")
    private void addDisposable(Disposable disposable){
        compositeDisposable.add(disposable);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        compositeDisposable.dispose();
        EventBus.getDefault().unregister(this);
        EventBus.getDefault().unregister(viewModel);
    }
}
