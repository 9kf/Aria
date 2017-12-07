package com.example.ksfgh.aria.Model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ksfgh on 16/11/2017.
 */

public class BandModel {

    @SerializedName("band_id")
    public int bandId;
    @SerializedName("band_name")
    public String bandName;
    @SerializedName("band_desc")
    public String bandDesc;
    @SerializedName("num_followers")
    public int numFollowers;
    @SerializedName("visit_counts")
    public int visitCounts;
    @SerializedName("band_pic")
    public String bandPic;
    @SerializedName("created_at")
    public String createdAt;
    @SerializedName("updated_at")
    public String updatedAt;
    @SerializedName("weekly_score")
    public double weeklyScore;
    @SerializedName("band_score")
    public String bandScore;
    @SerializedName("scored_updated_date")
    public String scoredUpdatedDate;

    public BandModel(int bandId, String bandName, String bandDesc, int numFollowers, int visitCounts, String bandPic, String createdAt, String updatedAt, double weeklyScore, String bandScore, String scoredUpdatedDate) {
        this.bandId = bandId;
        this.bandName = bandName;
        this.bandDesc = bandDesc;
        this.numFollowers = numFollowers;
        this.visitCounts = visitCounts;
        this.bandPic = bandPic;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.weeklyScore = weeklyScore;
        this.bandScore = bandScore;
        this.scoredUpdatedDate = scoredUpdatedDate;
    }

    public int getBandId() {
        return bandId;
    }

    public void setBandId(int bandId) {
        this.bandId = bandId;
    }

    public String getBandName() {
        return bandName;
    }

    public void setBandName(String bandName) {
        this.bandName = bandName;
    }

    public String getBandDesc() {
        return bandDesc;
    }

    public void setBandDesc(String bandDesc) {
        this.bandDesc = bandDesc;
    }

    public int getNumFollowers() {
        return numFollowers;
    }

    public void setNumFollowers(int numFollowers) {
        this.numFollowers = numFollowers;
    }

    public int getVisitCounts() {
        return visitCounts;
    }

    public void setVisitCounts(int visitCounts) {
        this.visitCounts = visitCounts;
    }

    public String getBandPic() {
        return bandPic;
    }

    public void setBandPic(String bandPic) {
        this.bandPic = bandPic;
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

    public double getWeeklyScore() {
        return weeklyScore;
    }

    public void setWeeklyScore(double weeklyScore) {
        this.weeklyScore = weeklyScore;
    }

    public String getBandScore() {
        return bandScore;
    }

    public void setBandScore(String bandScore) {
        this.bandScore = bandScore;
    }

    public String getScoredUpdatedDate() {
        return scoredUpdatedDate;
    }

    public void setScoredUpdatedDate(String scoredUpdatedDate) {
        this.scoredUpdatedDate = scoredUpdatedDate;
    }
}