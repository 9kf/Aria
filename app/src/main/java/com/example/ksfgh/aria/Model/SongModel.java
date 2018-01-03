package com.example.ksfgh.aria.Model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ksfgh on 29/12/2017.
 */

public class SongModel {


    @SerializedName("song_title")
    public String songTitle;
    @SerializedName("song_desc")
    public String songDesc;
    @SerializedName("song_audio")
    public String songAudio;
    @SerializedName("genre_id")
    public String genreId;
    @SerializedName("album_id")
    public String albumId;
    @SerializedName("updated_at")
    public String updatedAt;
    @SerializedName("created_at")
    public String createdAt;
    @SerializedName("song_id")
    public int songId;

    public SongModel(String songTitle, String songDesc, String songAudio, String genreId, String albumId, String updatedAt, String createdAt, int songId) {
        this.songTitle = songTitle;
        this.songDesc = songDesc;
        this.songAudio = songAudio;
        this.genreId = genreId;
        this.albumId = albumId;
        this.updatedAt = updatedAt;
        this.createdAt = createdAt;
        this.songId = songId;
    }
}
