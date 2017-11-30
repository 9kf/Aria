package com.example.ksfgh.aria.Model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ksfgh on 01/12/2017.
 */

public class BandMemberModel {


    @SerializedName("band")
    public Band band;
    @SerializedName("member")
    public Member member;

    public static class Band {
        @SerializedName("band_name")
        public String bandName;
        @SerializedName("band_desc")
        public String bandDesc;
        @SerializedName("num_followers")
        public int numFollowers;
        @SerializedName("visit_counts")
        public int visitCounts;
        @SerializedName("scored_updated_date")
        public String scoredUpdatedDate;
        @SerializedName("updated_at")
        public String updatedAt;
        @SerializedName("created_at")
        public String createdAt;
        @SerializedName("id")
        public int id;
    }

    public static class Member {
        @SerializedName("band_id")
        public int bandId;
        @SerializedName("user_id")
        public String userId;
        @SerializedName("bandrole")
        public String bandrole;
        @SerializedName("updated_at")
        public String updatedAt;
        @SerializedName("created_at")
        public String createdAt;
        @SerializedName("id")
        public int id;
    }
}
