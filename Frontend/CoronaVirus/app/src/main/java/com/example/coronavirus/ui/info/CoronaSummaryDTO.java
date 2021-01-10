package com.example.coronavirus.ui.info;

import java.util.ArrayList;

public class CoronaSummaryDTO{
    private CoronaGlobalSummaryDTO Global;
    private ArrayList<CoronaCountrySummaryDTO> Countries;
    private String Date;

    public CoronaGlobalSummaryDTO getGlobal() {
        return Global;
    }

    public ArrayList<CoronaCountrySummaryDTO> getCountries() {
        return Countries;
    }

    public String getDate() {
        return Date;
    }
}