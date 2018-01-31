package com.example.ksfgh.aria;

import android.app.Activity;

import com.example.ksfgh.aria.Model.CustomSongModelForPlaylist;
import com.example.ksfgh.aria.Model.FacebookUserModel;
import com.example.ksfgh.aria.Model.PlaylistModel;
import com.example.ksfgh.aria.View.fragments.FeedFragment;
import com.example.ksfgh.aria.View.fragments.HomeFragment;

import java.util.ArrayList;

/**
 * Created by ksfgh on 15/11/2017.
 */

public class Singleton {

    public static Singleton INSTANCE = null;

    private Singleton(){};

    public static Singleton getInstance(){
     if(INSTANCE == null){
         INSTANCE = new Singleton();
     }
     return(INSTANCE);
    }

    public  String PREFERENCE_NAME = "arial";
    public  Handlers handlers = new Handlers();

    //constants
    public  final int PICK_PHOTO = 123;
    public  final int PICK_AUDIO = 124;
    public final String BASE = "http://192.168.254.101";

    //static fragments
    public static HomeFragment homeFragment = new HomeFragment();
    public static FeedFragment feedFragment = new FeedFragment();

    //static classes
    public Utils utilities = new Utils();

    //used to identify the user
    public ArrayList<FacebookUserModel> facebookUserModels = new ArrayList<>();

    //used to identify what playlist is clicked
    public PlaylistModel currentPlaylistId;

    //used to identify what song is currently playing
    public CustomSongModelForPlaylist song;
}
