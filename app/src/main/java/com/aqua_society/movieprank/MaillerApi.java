package com.aqua_society.movieprank;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * Created by MrCharif on 24/12/2016.
 */

public interface MaillerApi {

    @FormUrlEncoded
    @POST("mailing")
    Call<ResponseBody> sendPine(@Field("email") String email,
                                @Field("name") String name,
                                @Field("tel") String tel,
                                @Field("code") String code );

    @GET("8b06714b7a81f9f71f0bc19bf19770a82e561918/dataadmob_v_app_serie1.json")
    Call<AdsObject> getAdsConfig();

}


