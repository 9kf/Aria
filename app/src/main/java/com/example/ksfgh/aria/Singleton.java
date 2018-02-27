package com.example.ksfgh.aria;

import android.app.Activity;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableBoolean;

import com.example.ksfgh.aria.Model.CustomModelForBandPage;
import com.example.ksfgh.aria.Model.CustomSongModelForPlaylist;
import com.example.ksfgh.aria.Model.FacebookUserModel;
import com.example.ksfgh.aria.Model.PlaylistModel;
import com.example.ksfgh.aria.View.activities.HomeScreen;
import com.example.ksfgh.aria.View.fragments.FeedFragment;
import com.example.ksfgh.aria.View.fragments.HomeFragment;
import com.example.ksfgh.aria.View.fragments.MyBandsFragment;
import com.example.ksfgh.aria.View.fragments.UserFragment;

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
    public final int PICK_VIDEO = 125;
    public final String BASE = "http://192.168.254.109";

    //home screen
    public static HomeScreen homeScreen;

    //static fragments
    public static HomeFragment homeFragment = new HomeFragment();
    public static FeedFragment feedFragment = new FeedFragment();
    public static MyBandsFragment myBandsFragment = new MyBandsFragment();
    public static UserFragment userFragment = new UserFragment();

    //static classes
    public Utils utilities = new Utils();

    //used to identify the user
    public ArrayList<FacebookUserModel> facebookUserModels = new ArrayList<>();

    //used to identify what playlist is clicked and if the user clicked the same playlist
    public PlaylistModel currentPlaylistId;
    public PlaylistModel playedPlist;

    //used to identify what song is currently playing
    public CustomSongModelForPlaylist song;

    //used to identify if the player is playing a song
    public boolean isPlayerPlaying;

    //used to identify if there is already a song prepared for the player
    public boolean isPlayerPrepared;

    //used to identify what band the user is currently in
    public CustomModelForBandPage currentBand;

    //used to indicate if the user is changing the image details of the band or just adding a band
    public int CHANGE_OR_ADD = 0;

    //used to identify what playlist the user is having
    public ArrayList<PlaylistModel> userPlaylists = new ObservableArrayList<>();

}
