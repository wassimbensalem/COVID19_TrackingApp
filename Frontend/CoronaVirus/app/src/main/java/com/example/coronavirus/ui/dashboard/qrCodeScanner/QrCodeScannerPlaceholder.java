package com.example.coronavirus.ui.dashboard.qrCodeScanner;

import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface QrCodeScannerPlaceholder {
    @POST("users/setInfected")
    Call<String> readCode(@Query("token")String token,@Query("code") String code);
}
