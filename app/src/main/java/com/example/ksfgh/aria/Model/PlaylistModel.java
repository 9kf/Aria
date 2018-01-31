package com.example.ksfgh.aria.Model;


import com.google.gson.annotations.SerializedName;

/**
 * Created by ksfgh on 28/01/2018.
 */

public class PlaylistModel{


    @SerializedName("pl_id")
    private int plId;
    @SerializedName("pl_title")
    private String plTitle;
    @SerializedName("pl_creator")
    private String plCreator;
    @SerializedName("image")
    private String image;
    @SerializedName("followers")
    private int followers;
    @SerializedName("created_at")
    private String createdAt;
    @SerializedName("updated_at")
    private String updatedAt;

    public int getPlId() {
        return plId;
    }

    public void setPlId(int plId) {
        this.plId = plId;
    }

    public String getPlTitle() {
        return plTitle;
    }

    public void setPlTitle(String plTitle) {
        this.plTitle = plTitle;
    }

    public String getPlCreator() {
        return plCreator;
    }

    public void setPlCreator(String plCreator) {
        this.plCreator = plCreator;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getFollowers() {
        return followers;
    }

    public void setFollowers(int followers) {
        this.followers = followers;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}
