package com.example.ksfgh.aria.Model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ksfgh on 04/03/2018.
 */

public class PreferenceModel {


    @SerializedName("user_id")
    public String userId;
    @SerializedName("band_id")
    public String bandId;
    @SerializedName("album_id")
    public String albumId;
    @SerializedName("pl_id")
    public int plId;
    @SerializedName("created_at")
    public String createdAt;
    @SerializedName("updated_at")
    public String updatedAt;

    public PreferenceModel(String userId, String bandId, String albumId, int plId) {
        this.userId = userId;
        this.bandId = bandId;
        this.albumId = albumId;
        this.plId = plId;
    }
}
