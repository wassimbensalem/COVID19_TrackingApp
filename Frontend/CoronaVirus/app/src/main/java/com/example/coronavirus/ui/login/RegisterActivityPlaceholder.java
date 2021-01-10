package com.example.coronavirus.ui.login;

import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface RegisterActivityPlaceholder {


    @POST("users/register")
    Call<RegisterResponseDTO> postLoginCredentials(@Query("name") String username, @Query("password") String password ,@Query("password2") String password2);
}
