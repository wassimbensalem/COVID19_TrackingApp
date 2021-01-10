package com.example.coronavirus.ui.map;

import java.util.ArrayList;

public class GeoJSONPoint {
    ArrayList<Double> latitude;
    ArrayList<Double> longitude;

    public ArrayList<Double> getLatitude() {
        return latitude;
    }

    public void setLatitude(ArrayList<Double> latitude) {
        this.latitude = latitude;
    }

    public ArrayList<Double> getLongitude() {
        return longitude;
    }

    public void setLongitude(ArrayList<Double> longitude) {
        this.longitude = longitude;
    }
}
