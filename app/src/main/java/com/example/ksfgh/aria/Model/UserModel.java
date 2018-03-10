package com.example.ksfgh.aria.Model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ksfgh on 15/02/2018.
 */

public class UserModel {

    @SerializedName("user_id")
    public String userId;
    @SerializedName("fname")
    public String fname;
    @SerializedName("lname")
    public String lname;
    @SerializedName("fullname")
    public String fullname;
    @SerializedName("email")
    public String email;
    @SerializedName("age")
    public String age;
    @SerializedName("gender")
    public String gender;
    @SerializedName("address")
    public String address;
    @SerializedName("contact")
    public String contact;
    @SerializedName("bio")
    public String bio;
    @SerializedName("profile_pic")
    public String profilePic;
    @SerializedName("remember_token")
    public String rememberToken;
    @SerializedName("created_at")
    public String createdAt;
    @SerializedName("updated_at")
    public String updatedAt;

    public UserModel() {
    }

    public UserModel(String userId, String fname, String lname, String fullname, String email, String age, String gender, String address, String contact, String bio, String profilePic) {
        this.userId = userId;
        this.fname = fname;
        this.lname = lname;
        this.fullname = fullname;
        this.email = email;
        this.age = age;
        this.gender = gender;
        this.address = address;
        this.contact = contact;
        this.bio = bio;
        this.profilePic = profilePic;
    }
}
