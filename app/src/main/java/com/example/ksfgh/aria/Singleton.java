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

    //constants
    public static final int PICK_PHOTO = 123;
}
