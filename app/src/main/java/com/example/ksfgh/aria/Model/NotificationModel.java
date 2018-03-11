package com.example.ksfgh.aria.Model;

import android.databinding.ObservableField;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ksfgh on 11/03/2018.
 */

public class NotificationModel {


    @SerializedName("band_id")
    public int band_id;
    @SerializedName("user_id")
    public String user_id;
    @SerializedName("bandrole")
    public String bandrole;
    @SerializedName("invitor")
    public String invitor;
    @SerializedName("created_at")
    public String created_at;
    @SerializedName("updated_at")
    public String updated_at;

    public ObservableField<String> bandName = new ObservableField<>();
    public ObservableField<String> bandImage = new ObservableField<>();

    public void setBandName(String bandName) {
        this.bandName.set(bandName);
    }

    public void setBandImage(String bandImage) {
        this.bandImage.set(bandImage);
    }
}
