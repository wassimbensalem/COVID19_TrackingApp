package com.example.coronavirus.ui.map;

import com.example.coronavirus.ui.map.OfferResponseDTO;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface OfferActivityPlaceholder {

    @POST("users/essentials/offer")
    Call<OfferResponseDTO> postOffer(@Query("objectName") String username,
                                     @Query("category") String category ,
                                     @Query("quantity") String quantity,
                                     @Query("price") String price,
                                     @Query("ownerName") String ownerName,
                                     @Query("ownerLocation") String[] ownerLocation,
                                     @Query("email") String email,
                                     @Header("Authorization") String authHeader);
    @GET("users/admin/getAllEssentials")
    Call<List<EssentialsDTO>> getOffer(
            @Query("requestorName") String requestorName, @Header("Authorization") String authHeader
    );

    @GET("users/essentials/offer")
    Call<GetEssentialsDTO> getOffers(
            @Query("objectName") String objectName, @Header("Authorization") String authHeader
    );

}
