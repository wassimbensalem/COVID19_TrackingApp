package com.example.coronavirus.ui.login;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface LoginActivityPlaceholder {

    @POST("users/login")
    Call<LoginResponseDTO> postLoginCredentials(@Query("name") String username, @Query("password") String password);
}
