package com.example.ksfgh.aria;

/**
 * Created by ksfgh on 15/11/2017.
 */

public class Singleton {

    private static Singleton INSTANCE = null;

    private Singleton(){};

    public static Singleton getInstance(){
     if(INSTANCE == null){
         INSTANCE = new Singleton();
     }
     return(INSTANCE);
    }


    public static String PREFERENCE_NAME = "arial";
    public static Handlers handlers = new Handlers();
    //public static String VIDEO_URL = "192.168.254.109/Aria/public/assets/videos/Linkin%20Park-Talking%20To%20Myself.mp4";

    //constants
    public static final int PICK_PHOTO = 123;
    public static final int PICK_AUDIO = 124;
}
