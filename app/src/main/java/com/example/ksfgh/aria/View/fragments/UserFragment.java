package com.example.ksfgh.aria.View.fragments;


import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ksfgh.aria.R;
import com.example.ksfgh.aria.ViewModel.UserViewModel;
import com.example.ksfgh.aria.databinding.FragmentUserBinding;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserFragment extends Fragment {


    public UserFragment() {
        // Required empty public constructor
    }

    FragmentUserBinding binding;
    UserViewModel viewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_user, container, false);
        viewModel = new UserViewModel(this);
        binding.setViewmodel(viewModel);
        return binding.getRoot();
    }

}
