package com.example.coronavirus.ui.achievements;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.coronavirus.Config;
import com.example.coronavirus.R;
import com.example.coronavirus.ui.login.LoginActivity;
import com.example.coronavirus.ui.login.RegisterActivityPlaceholder;
import com.example.coronavirus.ui.login.RegisterResponseDTO;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AddDoctorActivity extends AppCompatActivity {

    public static final String REGISTER_USERNAME = "com.example.coronavirus.REGISTER_USERNAME";

    private AddDocActivityPlaceholder addDoctoActivityPlaceholder;

    private AddDoctorResponseDTO addDoctorResponseDTO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adddoctor);

        final Button register = findViewById(R.id.addDocButton);
        register.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                addDoc();
            }
        });

        Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(Config.APIUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build();
        addDoctoActivityPlaceholder = retrofit.create(AddDocActivityPlaceholder.class);
    }



    private void addDoc() {

        String city1 = "";
        String country1 = "";
        String postCode2 ="";
        String docStreet2 = "";
        String docHouseNr2 = "";

        EditText docName = (EditText) findViewById(R.id.docName);
        String docName2 = docName.getText().toString();

        EditText docFullAdress = (EditText) findViewById(R.id.docAdress);
        String docFullAdress2 = docFullAdress.getText().toString();



        Call<AddDoctorResponseDTO> call= addDoctoActivityPlaceholder.postLoginCredentials(city1,country1,postCode2,docName2,docStreet2,docHouseNr2,docFullAdress2);
        call.enqueue(new Callback<AddDoctorResponseDTO>() {
            @Override
            public void onResponse(Call<AddDoctorResponseDTO> call, Response<AddDoctorResponseDTO> response) {
                if(!response.isSuccessful()){

                    addDoctorResponseDTO = response.body();


                    Log.i("schitt",Integer.toString(response.code()));
                    Log.i("WORKS",addDoctorResponseDTO + "");
                    return;
                }
                else {
                    addDoctorResponseDTO = response.body();
                    if (addDoctorResponseDTO.error.size()==0){
                        Log.i("eerror",Boolean.toString(addDoctorResponseDTO.error.size()==0));

                        finish();
                    }
                    else {
                        //TextView textView = (TextView) findViewById(R.id.register_error);
                        //textView.setText(registerResponseDTO.error.get(0));
                    }
                }
            }

            @Override
            public void onFailure(Call<AddDoctorResponseDTO> call, Throwable t) {
                Log.i("schitt","schitt");
            }
        });

    }
}
