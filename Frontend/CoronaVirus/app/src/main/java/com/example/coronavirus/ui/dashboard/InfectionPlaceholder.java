package com.example.coronavirus.ui.dashboard;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface InfectionPlaceholder {

    @GET("users/getUserStatus")
    Call<InfectionDTO> getCoronaSummary(@Query("username") String username);
}
