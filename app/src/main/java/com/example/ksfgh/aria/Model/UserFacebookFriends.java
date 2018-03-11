package com.example.ksfgh.aria.Model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ksfgh on 11/03/2018.
 */

public class UserFacebookFriends {


    public String id;
    public String name;
    public String picture;

    public UserFacebookFriends(String id, String name, String picture) {
        this.id = id;
        this.name = name;
        this.picture = picture;
    }


}
