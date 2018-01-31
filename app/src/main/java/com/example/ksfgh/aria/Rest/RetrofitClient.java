package com.example.ksfgh.aria.Rest;

import com.example.ksfgh.aria.Model.BandModel;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import io.reactivex.Observable;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by ksfgh on 15/11/2017.
 */

public class RetrofitClient {
    private static Retrofit retrofit = null;
    private static AriaClient client = null;

    public static AriaClient getClient(){
        if(retrofit == null){
            retrofit = new Retrofit.Builder()
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl("http://192.168.254.101/Aria/public/api/")
                    .build();
        }

        if(client == null){
            client = retrofit.create(AriaClient.class);
        }

        return client;
    }

}
