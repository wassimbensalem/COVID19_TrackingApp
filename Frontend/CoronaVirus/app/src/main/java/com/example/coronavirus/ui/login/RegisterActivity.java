package com.example.coronavirus.ui.login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.coronavirus.Config;
import com.example.coronavirus.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RegisterActivity extends AppCompatActivity {
    public static final String REGISTER_USERNAME = "com.example.coronavirus.REGISTER_USERNAME";

    private RegisterActivityPlaceholder registerActivityPlaceholder;

    private RegisterResponseDTO registerResponseDTO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        final Button register = findViewById(R.id.register_registerbutton);
        register.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                register();
            }
        });

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Config.APIUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        registerActivityPlaceholder = retrofit.create(RegisterActivityPlaceholder.class);
    }

    private void register() {
        EditText usernameElement = (EditText) findViewById(R.id.register_username);
        String username = usernameElement.getText().toString();
        EditText password1Element = (EditText) findViewById(R.id.register_password1);
        String password1 = password1Element.getText().toString();
        EditText password2Element = (EditText) findViewById(R.id.register_password2);
        String password2 = password2Element.getText().toString();
        Call<RegisterResponseDTO> call= registerActivityPlaceholder.postLoginCredentials(username,password1,password2);
        Intent intent = new Intent(this, LoginActivity.class);
        call.enqueue(new Callback<RegisterResponseDTO>() {
            @Override
            public void onResponse(Call<RegisterResponseDTO> call, Response<RegisterResponseDTO> response) {
                if(!response.isSuccessful()){

                    registerResponseDTO = response.body();

                    TextView textView = (TextView) findViewById(R.id.register_error);
                    textView.setText(registerResponseDTO.error.get(0));
                    Log.i("schitt",Integer.toString(response.code()));
                    return;
                }
                else {
                    registerResponseDTO = response.body();
                     if (registerResponseDTO.error.size()==0){
                       Log.i("eerror",Boolean.toString(registerResponseDTO.error.size()==0));
                       intent.putExtra(REGISTER_USERNAME, username);
                       startActivity(intent);
                       finish();
                    }
                    else {
                        TextView textView = (TextView) findViewById(R.id.register_error);
                        textView.setText(registerResponseDTO.error.get(0));
                    }
                }
            }

            @Override
            public void onFailure(Call<RegisterResponseDTO> call, Throwable t) {
                Log.i("schitt","schitt");
            }
        });

    }

    private boolean checkPassword() {
        EditText password1Element = (EditText) findViewById(R.id.register_password1);
        String password1 = password1Element.getText().toString();
        EditText password2Element = (EditText) findViewById(R.id.register_password1);
        String password2 = password2Element.getText().toString();
        return password1.equals(password2);
    }
}
