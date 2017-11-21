package com.example.ksfgh.aria.Rest;

import com.example.ksfgh.aria.Model.BandModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by ksfgh on 16/11/2017.
 */

public interface AriaClient {

    @GET("bands")
    Call<BandModel[]> getAllBands();
}
