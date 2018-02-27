package com.example.ksfgh.aria.Model;

import android.databinding.ObservableArrayList;
import android.databinding.ObservableField;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ksfgh on 26/02/2018.
 */

public class CustomModelForAlbum {

    @SerializedName("songs")
    public ArrayList<SongModel> songs;
    @SerializedName("album")
    public AlbumModel album;

    public CustomModelForAlbum(ArrayList<SongModel> songs, AlbumModel album) {
        this.songs = songs;
        this.album = album;

    }

}
