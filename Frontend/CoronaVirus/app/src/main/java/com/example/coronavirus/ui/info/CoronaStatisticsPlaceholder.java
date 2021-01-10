package com.example.coronavirus.ui.info;

import retrofit2.Call;
import retrofit2.http.GET;

public interface CoronaStatisticsPlaceholder {
    @GET("summary")
    Call<CoronaSummaryDTO> getCoronaSummary();
}
