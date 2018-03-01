package com.example.ksfgh.aria.Model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ksfgh on 30/01/2018.
 */

public class AlbumModel {
    @SerializedName("album_id")
    private int albumId;
    @SerializedName("album_name")
    private String albumName;
    @SerializedName("album_desc")
    private String albumDesc;
    @SerializedName("album_pic")
    private String albumPic;
    @SerializedName("num_likes")
    private String numLikes;
    @SerializedName("band_id")
    private int bandId;
    @SerializedName("released_date")
    private String releasedDate;
    @SerializedName("created_at")
    private String createdAt;
    @SerializedName("updated_at")
    private String updatedAt;

    public AlbumModel(int albumId, String albumName, String albumDesc, String albumPic, String numLikes, int bandId, String releasedDate, String createdAt, String updatedAt) {
        this.albumId = albumId;
        this.albumName = albumName;
        this.albumDesc = albumDesc;
        this.albumPic = albumPic;
        this.numLikes = numLikes;
        this.bandId = bandId;
        this.releasedDate = releasedDate;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public int getAlbumId() {
        return albumId;
    }

    public void setAlbumId(int albumId) {
        this.albumId = albumId;
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public String getAlbumDesc() {
        return albumDesc;
    }

    public void setAlbumDesc(String albumDesc) {
        this.albumDesc = albumDesc;
    }

    public String getAlbumPic() {
        return albumPic;
    }

    public void setAlbumPic(String albumPic) {
        this.albumPic = albumPic;
    }

    public String getNumLikes() {
        return numLikes;
    }

    public void setNumLikes(String numLikes) {
        this.numLikes = numLikes;
    }

    public int getBandId() {
        return bandId;
    }

    public void setBandId(int bandId) {
        this.bandId = bandId;
    }

    public String getReleasedDate() {
        return releasedDate;
    }

    public void setReleasedDate(String releasedDate) {
        this.releasedDate = releasedDate;
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
