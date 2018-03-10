package com.example.ksfgh.aria.Model;

import android.databinding.ObservableArrayList;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ksfgh on 09/03/2018.
 */

public class CustomSearchModel {

    @SerializedName("band")
    public List<BandModel> band;
    @SerializedName("user")
    public List<UserModel> user;
    @SerializedName("playlist")
    public List<PlaylistModel> playlist;
    @SerializedName("song")
    public List<SongModel> song;
    @SerializedName("album")
    public List<AlbumModel> album;
    @SerializedName("video")
    public List<VideoModel> video;
}
