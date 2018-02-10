package com.example.ksfgh.aria.Model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ksfgh on 10/02/2018.
 */

public class MemberModel {

    @SerializedName("user_id")
    public String userId;
    @SerializedName("band_id")
    public int bandId;
    @SerializedName("bandrole")
    public String bandrole;
    @SerializedName("created_at")
    public String createdAt;
    @SerializedName("updated_at")
    public String updatedAt;

    public MemberModel(String userId, int bandId, String bandrole, String createdAt, String updatedAt) {
        this.userId = userId;
        this.bandId = bandId;
        this.bandrole = bandrole;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
