package com.example.ksfgh.aria.Model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ksfgh on 10/02/2018.
 */

public class VideoModel {


    @SerializedName("video_id")
    public int videoId;
    @SerializedName("video_title")
    public String videoTitle;
    @SerializedName("video_desc")
    public String videoDesc;
    @SerializedName("video_content")
    public String videoContent;
    @SerializedName("created_at")
    public String createdAt;
    @SerializedName("updated_at")
    public String updatedAt;

    public VideoModel(int videoId, String videoTitle, String videoDesc, String videoContent, String createdAt, String updatedAt) {
        this.videoId = videoId;
        this.videoTitle = videoTitle;
        this.videoDesc = videoDesc;
        this.videoContent = videoContent;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
