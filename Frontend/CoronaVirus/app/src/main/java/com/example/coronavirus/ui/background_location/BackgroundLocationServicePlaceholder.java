package com.example.coronavirus.ui.background_location;

import android.location.Location;

import com.example.coronavirus.ui.login.LoginResponseDTO;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface BackgroundLocationServicePlaceholder {

    @POST("gps/setLocation")
    @FormUrlEncoded
    Call<String> postLoginCredentials(
           @Query("token") String token,
           @Field("latitude") ArrayList<Double> latitude,
           @Field("longitude") ArrayList<Double> longitude,
           @Field("date") ArrayList<Long> date
    );
}
