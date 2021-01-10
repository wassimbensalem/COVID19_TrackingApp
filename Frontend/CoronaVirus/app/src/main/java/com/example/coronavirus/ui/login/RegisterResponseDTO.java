package com.example.coronavirus.ui.login;

import java.util.ArrayList;


public class RegisterResponseDTO {
    ArrayList<String> error;
    String name;
    String password;
    String password2;

    public ArrayList<String> getError() {
        return error;
    }
}
