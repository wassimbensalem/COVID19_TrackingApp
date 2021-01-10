package com.example.coronavirus.ui.map;

import retrofit2.Call;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface RequestActivityPlaceholder {

    @POST("users/essentials")
    Call<RequestResponseDTO> postRequest(@Query("objectName") String username,
                                     @Query("category") String category ,
                                     @Query("quantity") String quantity,
                                     @Query("price") String price,
                                     @Query("ownerName") String ownerName,
                                     @Query("ownerLocation") String[] ownerLocation,
                                         @Query("email") String email,
                                         @Header("Authorization") String authHeader
    )

            ;
}
