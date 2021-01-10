package com.example.coronavirus.ui.achievements;

import com.example.coronavirus.ui.map.GeoJSONPoint;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface AchievementsFragmentPlaceholder {


    @GET("/users/getDoctors")
    Call<List<Doctors>> getDoctors();
}
