package com.example.ksfgh.aria.Rest;

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
                    .baseUrl("http://192.168.254.108/Aria/public/api/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        if(client == null){
            client = retrofit.create(AriaClient.class);
        }

        return client;
    }
}
