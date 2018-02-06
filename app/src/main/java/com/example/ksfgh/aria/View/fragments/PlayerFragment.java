package com.example.ksfgh.aria.View.fragments;


import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ksfgh.aria.Model.CustomSongModelForPlaylist;
import com.example.ksfgh.aria.R;
import com.example.ksfgh.aria.View.activities.PlayerActivity;
import com.example.ksfgh.aria.ViewModel.PlayerActivityViewModel;
import com.example.ksfgh.aria.databinding.PlayerFragmentBinding;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

/**
 * A simple {@link Fragment} subclass.
 */
public class PlayerFragment extends Fragment {


    public PlayerFragment() {
        // Required empty public constructor
    }


    private PlayerFragmentBinding fragmentBinding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_player, container, false);
        fragmentBinding.setViewmodel(((PlayerActivity)this.getActivity()).getPlayerViewModel());
        return fragmentBinding.getRoot();
    }

}
