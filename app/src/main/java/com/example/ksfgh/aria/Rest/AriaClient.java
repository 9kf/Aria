package com.example.ksfgh.aria.Rest;

import com.example.ksfgh.aria.Model.BandCreationModel;
import com.example.ksfgh.aria.Model.BandMemberModel;
import com.example.ksfgh.aria.Model.BandModel;
import com.example.ksfgh.aria.Model.FacebookUserModel;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * Created by ksfgh on 16/11/2017.
 */

public interface AriaClient {

    @GET("bands")
    Call<BandModel[]> getAllBands();

    @POST("saveUser")
    Call<FacebookUserModel> createAccount(@Body FacebookUserModel facebookUserModel);

    @POST("createBand")
    Call<BandMemberModel> createBand(@Body BandCreationModel bandCreationModel);

    @Multipart
    @POST("editbandPic")
    Call<ResponseBody> editBandPic(
            @Part("bandId") RequestBody bandId,
            @Part MultipartBody.Part pic
    );

}
