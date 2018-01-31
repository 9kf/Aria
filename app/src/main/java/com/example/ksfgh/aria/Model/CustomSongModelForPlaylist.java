package com.example.ksfgh.aria.Model;

/**
 * Created by ksfgh on 30/01/2018.
 */

public class CustomSongModelForPlaylist {

    private SongModel song;
    private BandModel band;
    private AlbumModel album;

    public CustomSongModelForPlaylist(SongModel song, BandModel band, AlbumModel album) {
        this.song = song;
        this.band = band;
        this.album = album;
    }

    public SongModel getSong() {
        return song;
    }

    public void setSong(SongModel song) {
        this.song = song;
    }

    public BandModel getBand() {
        return band;
    }

    public void setBand(BandModel band) {
        this.band = band;
    }

    public AlbumModel getAlbum() {
        return album;
    }

    public void setAlbum(AlbumModel album) {
        this.album = album;
    }

}
