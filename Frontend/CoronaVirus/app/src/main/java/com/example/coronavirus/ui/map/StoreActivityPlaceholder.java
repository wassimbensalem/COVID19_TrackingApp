package com.example.coronavirus.ui.map;

import com.example.coronavirus.ui.achievements.AddDoctorResponseDTO;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface StoreActivityPlaceholder {

    @POST("users/addStore")
    Call<StoreResponseDTO> postStore(@Query("storeName") String storeName,
                                     @Query("storeLocation") String[] ownerLocation);
    @GET("users/getStores")
    Call<List<StoresDTO>> getStores();

    @POST("users/addStoreEssentials")
    Call<AddDoctorResponseDTO> addEssential(@Query("storeName") String storeName,
                                            @Query("essentialName") String essentialName,
                                            @Query("essentialQuantity") String essentialQuantity,
                                            @Query("essentialPrice") String essentialPrice);
}
