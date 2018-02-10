package com.example.ksfgh.aria.Model;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by ksfgh on 10/02/2018.
 */

public class CustomModelForBandPage {

    public BandModel band;
    public ArrayList<MemberModel> members;
    public ArrayList<AlbumModel> albums;
    public ArrayList<VideoModel> videos;
    public ArrayList<EventModel> events;

    public CustomModelForBandPage(BandModel band, ArrayList<MemberModel> members, ArrayList<AlbumModel> albums, ArrayList<EventModel> events) {
        this.band = band;
        this.members = members;
        this.albums = albums;
        this.events = events;
    }

    public void setVideos(ArrayList<VideoModel> videos) {
        this.videos = videos;
    }
}
