package com.example.ksfgh.aria.Model;

import android.databinding.BaseObservable;

/**
 * Created by ksfgh on 27/11/2017.
 */

public class FacebookUserModel extends BaseObservable{


    public String user_id;
    public String fname;
    public String lname;
    public String email;
    public String age;
    public String gender;
    public String address;
    public String contact;
    public String bio;
    public String pic;

    public FacebookUserModel(String user_id, String fname, String lname, String email, String gender, String age, String address, String contact, String bio, String pic) {
        this.user_id = user_id;
        this.fname = fname;
        this.lname = lname;
        this.email = email;
        this.age = age;
        this.gender = gender;
        this.address = address;
        this.contact = contact;
        this.bio = bio;
        this.pic = pic;
    }

    public String getId() {
        return user_id;
    }

    public String getFname() {
        return fname;
    }

    public String getLname() {
        return lname;
    }

    public String getEmail() {
        return email;
    }

    public String getAge() {
        return age;
    }

    public String getGender() {
        return gender;
    }

    public String getAddress() {
        return address;
    }

    public String getContact() {
        return contact;
    }

    public String getBio() {
        return bio;
    }

    public String getPicture() {
        return pic;
    }
}

