package com.example.coronavirus.ui.admin;

import com.example.coronavirus.ui.map.GeoJSONPoint;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface AdminFragmentPlaceholder {

    @POST("users/delete")
    Call<String> markDelete(@Query("token") String token, @Query("username") String username);

    @POST("users/markInfected")
    Call<String> markInfected(@Query("token") String token, @Query("username") String username);

    @POST("users/markHealthy")
    Call<String> markHealthy(@Query("token") String token, @Query("username") String username);

    @POST("users/addSetInfected")
    Call<String> addCode(@Query("code") String code);

    @POST("gps/getUserTrace")
    @FormUrlEncoded
    Call<TraceResponseDTO> getContactTrace(
            @Query("token") String token,
            @Field("username") String username
    );

    @POST("users/admin/markAsAdmin")
    Call<MkAdminDTO> mkAdmin( @Header("Authorization") String token,@Query("requestorName") String requestorName, @Query("name") String name, @Query("isAdmin") boolean isAdmin);

    @POST("gps/getInfectionTrace")
    @FormUrlEncoded
    Call<GeoJSONPoint> getInfectionTrace(
            @Query("token") String token,
            @Field("userid") String userid,
            @Field("date") String date);
}
