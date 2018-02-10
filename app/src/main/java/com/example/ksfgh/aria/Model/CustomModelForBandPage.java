package com.example.ksfgh.aria.Model;

import java.util.ArrayList;

/**
 * Created by ksfgh on 10/02/2018.
 */

public class CustomModelForBandPage {

    public BandModel band;
    public ArrayList<MemberModel> members;
    public ArrayList<AlbumModel> albums;
    public ArrayList<VideoModel> videos;

    public CustomModelForBandPage(BandModel band, ArrayList<MemberModel> members, ArrayList<AlbumModel> albums) {
        this.band = band;
        this.members = members;
        this.albums = albums;
    }

    public void setVideos(ArrayList<VideoModel> videos) {
        this.videos = videos;
    }
}
