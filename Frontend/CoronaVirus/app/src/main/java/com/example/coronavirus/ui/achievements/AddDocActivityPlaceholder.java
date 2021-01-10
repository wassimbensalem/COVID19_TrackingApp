package com.example.coronavirus.ui.achievements;

import com.example.coronavirus.ui.login.RegisterResponseDTO;

import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface AddDocActivityPlaceholder {


    @POST("users/admin/addDoctor")
    Call<AddDoctorResponseDTO> postLoginCredentials(@Query("city") String city, @Query("country") String country , @Query("postcode") String postcode,@Query("name") String name , @Query("street") String street, @Query("addrhousenumber") String addrhousenumber,@Query("adress") String adress);
}
