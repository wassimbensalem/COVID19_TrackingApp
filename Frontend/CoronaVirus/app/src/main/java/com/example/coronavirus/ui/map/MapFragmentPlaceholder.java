package com.example.coronavirus.ui.map;

import com.example.coronavirus.ui.login.LoginResponseDTO;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface MapFragmentPlaceholder {

    @POST("gps/getPoints")
    Call<GeoJSONPoint> getPoints(@Query("token") String token,@Query("hi") String hi);
}
