package com.example.coronavirus.ui.dashboard;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RiskFactorPlaceholder {

    @GET("users/getUserRiskFactor")
    Call<RiskFactorDTO> getRiskFactor(@Query("username") String username);
}
