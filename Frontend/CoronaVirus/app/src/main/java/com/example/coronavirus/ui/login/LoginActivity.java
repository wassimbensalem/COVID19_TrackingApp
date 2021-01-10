package com.example.coronavirus.ui.login;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;

import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.coronavirus.Config;
import com.example.coronavirus.MainActivity;
import com.example.coronavirus.R;


import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;


public class LoginActivity extends AppCompatActivity {

    public static final String USERNAME = "com.example.coronavirus.USERNAME";
    public static final String PASSWORD = "com.example.coronavirus.PASSWORD";
    LoginResponseDTO loginResponseDTO = new LoginResponseDTO();
    Call<LoginResponseDTO> call;


    private LoginActivityPlaceholder loginActivityPlaceholder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        requestPemissions();

        Intent intent = getIntent();
        if (intent==null)
            Log.i("hi",intent.getAction());
        String register_username = intent.getStringExtra(RegisterActivity.REGISTER_USERNAME);


        if (register_username!=null){
            EditText x = findViewById(R.id.username);
            x.setText(register_username);
        }

        final Button button = findViewById(R.id.login_button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                validateCredentials();
               // post();
            }
        });

        final Button register = findViewById(R.id.login_registerbutton);
        register.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                register();
            }
        });

        final Button cheat = findViewById(R.id.login_cheat);
        cheat.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                cheat();
            }
        });

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Config.APIUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        loginActivityPlaceholder = retrofit.create(LoginActivityPlaceholder.class);
    }

    private void requestPemissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(LoginActivity.this,new String[]{"android.permission.ACCESS_FINE_LOCATION","android.permission.ACCESS_COARSE_LOCATION","android.permission.FOREGROUND_SERVICE","android.permission.CAMERA","android.permission.CALL_PHONE"},9);

        }
    }
    public void onRequestPermissionsResults(int requestCode, String[] permissions,
                                            int[] grantResults) {
        switch (requestCode) {
            case 9:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission is granted. Continue the action or workflow
                    // in your app.


                } else {
                    // Explain to the user that the feature is unavailable because
                    // the features requires a permission that the user has denied.
                    // At the same time, respect the user's decision. Don't link to
                    // system settings in an effort to convince the user to change
                    // their decision.
                    requestPemissions();
                }
                return;
        }
    }

    private void cheat() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void register() {

        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);

        finish();
    }


    void validateCredentials()  {
        EditText usernameElement = (EditText) findViewById(R.id.username);
        String username = usernameElement.getText().toString();
        EditText passwordElement = (EditText) findViewById(R.id.password);
        String password = passwordElement.getText().toString();


        sendPost(username, password);




    }


    private void sendPost(final String username, String password) {

        Intent intent = new Intent(this, MainActivity.class);
        LoginCredentialsDTO loginCredentialsDTO = new LoginCredentialsDTO(username, password);


        call = loginActivityPlaceholder.postLoginCredentials(username, password);

        call.enqueue(new Callback<LoginResponseDTO>() {
            @Override
            public void onResponse(Call<LoginResponseDTO> call, Response<LoginResponseDTO> response) {
                if(!response.isSuccessful()){
                    loginResponseDTO = new LoginResponseDTO();
                    loginResponseDTO.setToken("Code: "+ response.code());
                    Log.i("error",loginResponseDTO.getToken());
                    return;
                }

                if(response != null && response.isSuccessful() && response.body() != null) {
                    loginResponseDTO = response.body();
                    if(loginResponseDTO.getMessage().size()==0) {

                        intent.putExtra(USERNAME, username);
                        intent.putExtra("token", loginResponseDTO.getToken());
                        intent.putExtra("isAdmin", loginResponseDTO.getLoggedUser().isAdmin);
                        intent.putExtra("riskFactor", loginResponseDTO.getLoggedUser().risk.toString());
                        intent.putExtra(PASSWORD, password);
                        startActivity(intent);
                        finish();
                    }
                    else{
                        TextView errorText = (TextView)findViewById(R.id.login_errortext);
                        errorText.setText(loginResponseDTO.getMessage().get(0));
                    }

                }
            }

            @Override
            public void onFailure(Call<LoginResponseDTO> call, Throwable t) {
               loginResponseDTO = new LoginResponseDTO();
               loginResponseDTO.setToken(t.getMessage());
                Log.i("error",loginResponseDTO.getToken());
            }
        });


    }


}
