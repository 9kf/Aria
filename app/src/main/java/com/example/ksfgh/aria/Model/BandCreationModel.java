package com.example.ksfgh.aria.Model;

/**
 * Created by ksfgh on 01/12/2017.
 */

public class BandCreationModel {

    private String user_id;
    private String band_role_create;
    private String band_name;
    private int genre_select_1;
    private int genre_select_2;
    private String bandDescr;

    public BandCreationModel(String user_id, String band_role_create, String band_name, int genre_select_1, int genre_select_2, String bandDescr) {
        this.user_id = user_id;
        this.band_role_create = band_role_create;
        this.band_name = band_name;
        this.genre_select_1 = genre_select_1;
        this.genre_select_2 = genre_select_2;
        this.bandDescr = bandDescr;
    }
}
