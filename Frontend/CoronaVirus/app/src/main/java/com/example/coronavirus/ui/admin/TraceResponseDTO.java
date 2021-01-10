package com.example.coronavirus.ui.admin;

import java.io.Serializable;
import java.util.ArrayList;


public class TraceResponseDTO implements Serializable {
    public String user;
    public String name;
    public String date;
    public String isodate;
    public Double risk;
    public ArrayList<Double> location;
    public ArrayList<TraceResponseDTO> contacts;

    @Override
    public String toString() {
        return "TraceResponseDTO{" +
                "user='" + user + '\'' +
                ", name='" + name + '\'' +
                ", date='" + date + '\'' +
                ", risk=" + risk +
                ", location=" + location +
                ", contacts=" + contacts +
                '}';
    }
}
