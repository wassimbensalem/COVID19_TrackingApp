package com.example.coronavirus.ui.login;

public class LoginCredentialsDTO {

    private String name;

    private String password;


    public LoginCredentialsDTO(String name, String password) {
        this.name = name;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }




}
