package com.example.coronavirus.ui.dashboard;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface PrevRiskFactorPlaceholder {

    @GET("users/getPreviousRiskFactor")
    Call<PrevRiskFactorDTO> getPrevRiskFactor(@Query("username") String username);

    @POST("setPreviousRiskFactor")
    Call<PrevRiskFactorDTO> setPrevRiskFactor(@Query("username") String username,
                                              @Query("riskprevFactor") float riskprevFactor);
}
