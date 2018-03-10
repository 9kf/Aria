package com.example.ksfgh.aria.View.fragments;

import android.databinding.ObservableArrayList;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ksfgh.aria.Adapters.SearchAdapter;
import com.example.ksfgh.aria.Model.AlbumModel;
import com.example.ksfgh.aria.Model.BandModel;
import com.example.ksfgh.aria.Model.CustomSearchModel;
import com.example.ksfgh.aria.Model.PlaylistModel;
import com.example.ksfgh.aria.Model.SongModel;
import com.example.ksfgh.aria.Model.UserModel;
import com.example.ksfgh.aria.Model.VideoModel;
import com.example.ksfgh.aria.R;
import com.example.ksfgh.aria.Rest.RetrofitClient;
import com.example.ksfgh.aria.databinding.DialogSearchBinding;

import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function6;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by ksfgh on 09/03/2018.
 */

public class SearchDialogFragment extends DialogFragment {

    public SearchDialogFragment(){}

    public static SearchDialogFragment newInstance(){
        SearchDialogFragment dialogFragment = new SearchDialogFragment();
        dialogFragment.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle);

        Bundle args = new Bundle();
        dialogFragment.setArguments(args);

        return dialogFragment;
    }

    private DialogSearchBinding binding;
    public ObservableBoolean isTextEmpty;
    public ObservableBoolean hasData;
    public ObservableBoolean isLoading;
    private CompositeDisposable compositeDisposable;
    public ObservableArrayList<Object> generalList;
    public ObservableField<CustomSearchModel> results;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_search, container, false);
        binding.setFrag(this);
        initComponents();


        binding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                hasData.set(false);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                checkText(editable.toString());
            }
        });

        binding.ivCloseSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().dismiss();
            }
        });

        binding.ivClearSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.etSearch.setText("");
                isTextEmpty.set(true);
            }
        });

        binding.tlFilter.addTab(binding.tlFilter.newTab().setText("Band"));
        binding.tlFilter.addTab(binding.tlFilter.newTab().setText("People"));
        binding.tlFilter.addTab(binding.tlFilter.newTab().setText("Playlist"));
        binding.tlFilter.addTab(binding.tlFilter.newTab().setText("Song"));
        binding.tlFilter.addTab(binding.tlFilter.newTab().setText("Album"));
        binding.tlFilter.addTab(binding.tlFilter.newTab().setText("Video"));

        return binding.getRoot();
    }

    private void checkText(String text) {

        if(text.equals("")){
            isTextEmpty.set(true);
            hasData.set(false);
        }
        else {
            isTextEmpty.set(false);
            Disposable disposable = RetrofitClient.getClient().searchResults(text)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableObserver<CustomSearchModel>() {
                        @Override
                        public void onNext(CustomSearchModel customSearchModel2) {
                            results.set(customSearchModel2);
                            generalList.clear();
                            switch (binding.tlFilter.getSelectedTabPosition()){
                                case 0:
                                    if(!customSearchModel2.band.isEmpty()){
                                        generalList.addAll(customSearchModel2.band);
                                        hasData.set(true);
                                    }
                                    break;

                                case 1:
                                    if(!customSearchModel2.user.isEmpty()){
                                        generalList.addAll(customSearchModel2.user);
                                        hasData.set(true);
                                    }
                                    break;

                                case 2:
                                    if(!customSearchModel2.playlist.isEmpty()){
                                        generalList.addAll(customSearchModel2.playlist);
                                        hasData.set(true);
                                    }
                                    break;

                                case 3:
                                    if(!customSearchModel2.song.isEmpty()){
                                        generalList.addAll(customSearchModel2.song);
                                        hasData.set(true);
                                    }
                                    break;

                                case 4:
                                    if(!customSearchModel2.album.isEmpty()){
                                        generalList.addAll(customSearchModel2.album);
                                        hasData.set(true);
                                    }
                                    break;

                                case 5:
                                    if(!customSearchModel2.video.isEmpty()){
                                        generalList.addAll(customSearchModel2.video);
                                        hasData.set(true);
                                    }
                                    break;
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.d("search", e.getMessage() + " error");
                        }

                        @Override
                        public void onComplete() {


                        }
                    });

            compositeDisposable.add(disposable);
        }

    }

    private void initComponents() {
        isTextEmpty = new ObservableBoolean();
        hasData = new ObservableBoolean();
        hasData.set(false);
        isTextEmpty.set(true);
        isLoading = new ObservableBoolean();
        isLoading.set(false);
        compositeDisposable = new CompositeDisposable();
        generalList = new ObservableArrayList<>();
        results = new ObservableField<>();

        binding.rvSearchResults.setLayoutManager(new LinearLayoutManager(binding.rvSearchResults.getContext(), LinearLayoutManager.VERTICAL, false));
        binding.rvSearchResults.setAdapter(new SearchAdapter(generalList, this));

        binding.tlFilter.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Log.d("search", "cleared");
                results.set(results.get());
                generalList.clear();
                switch (tab.getPosition()){
                    case 0:
                        if(results.get() != null)
                            if(results.get().band != null)
                                generalList.addAll(results.get().band);
                        break;

                    case 1:
                        Log.d("search", "users");
                        if(results.get() != null)
                            if(results.get().user != null)
                                generalList.addAll(results.get().user);
                        break;

                    case 2:
                        Log.d("search", "playlists");
                        if(results.get() != null)
                            if(results.get().playlist != null)
                                generalList.addAll(results.get().playlist);
                        break;

                    case 3:
                        if(results.get() != null)
                            if(results.get().song != null)
                                generalList.addAll(results.get().song);
                        break;

                    case 4:
                        if(results.get() != null)
                            if(results.get().album != null)
                                generalList.addAll(results.get().album);
                        break;

                    case 5:
                        if(results.get() != null)
                            if(results.get().video != null)
                                generalList.addAll(results.get().video);
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                return;
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        compositeDisposable.dispose();
    }
}
