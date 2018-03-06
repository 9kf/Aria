package com.example.ksfgh.aria.Model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ksfgh on 05/03/2018.
 */

public class BandGenreModel {


    @SerializedName("band_id")
    public int bandId;
    @SerializedName("genre_id")
    public int genreId;
    @SerializedName("created_at")
    public String createdAt;
    @SerializedName("updated_at")
    public String updatedAt;

    public BandGenreModel(int bandId, int genreId) {
        this.bandId = bandId;
        this.genreId = genreId;
    }
}
