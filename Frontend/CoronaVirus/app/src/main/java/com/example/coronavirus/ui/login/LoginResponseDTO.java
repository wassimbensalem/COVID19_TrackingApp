package com.example.coronavirus.ui.login;

import java.util.ArrayList;




public class LoginResponseDTO {

    private String token;

    private ArrayList<String> error;

    private ArrayList<String> message;

    private User loggedUser;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public ArrayList<String> getError() {
        return error;
    }

    public ArrayList<String> getMessage() {
        return message;
    }

    public User getLoggedUser() {
        return loggedUser;
    }

    public class User {

        String name;

        String password;

        Object date;

        String token;

        Object latitude;

        Object longitude;

        Object dates;

        Object points;

        boolean infected;

        Object infectedSince;

        Double risk;

        boolean isAdmin;
    }
}
