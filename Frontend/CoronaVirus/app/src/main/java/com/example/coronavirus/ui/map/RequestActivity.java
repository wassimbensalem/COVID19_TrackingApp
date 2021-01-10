package com.example.coronavirus.ui.map;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.coronavirus.Config;
import com.example.coronavirus.MainActivity;
import com.example.coronavirus.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RequestActivity extends AppCompatActivity {
    public static final String REQUEST = "com.example.coronavirus.REQUEST";
    private RequestActivityPlaceholder requestActivityPlaceholder;
    private RequestResponseDTO requestResponseDTO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request);
    }

    public void submit(View view){


        EditText item_name = (EditText) findViewById(R.id.item_name2);
        String name = item_name.getText().toString();
        EditText item_quantity = (EditText) findViewById(R.id.item_quantity_request);
        String quantity = item_quantity.getText().toString();
        EditText item_category = (EditText) findViewById(R.id.category3);
        String category = item_category.getText().toString();
        EditText item_price = (EditText) findViewById(R.id.price3);
        String price = item_price.getText().toString();
        EditText item_email = (EditText) findViewById(R.id.telephone_number2);
        String email = item_email.getText().toString();
        String username = getIntent().getStringExtra("username");
        Log.i("TAG", "submit: "+ username);
        String lat = getIntent().getStringExtra("latitude");
        String lon = getIntent().getStringExtra("longitude");
        String[] location = {lat,lon};
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Config.APIUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RequestActivityPlaceholder requestActivityPlaceholder;
        requestActivityPlaceholder = retrofit.create(RequestActivityPlaceholder.class);

        String tmp = category+ quantity+ price+ username+ location;

        String token = getIntent().getStringExtra("token");
        Call<RequestResponseDTO> call= requestActivityPlaceholder.postRequest(name,category, quantity, price, username, location, email, token);
        call.enqueue(new Callback<RequestResponseDTO>() {
            @Override
            public void onResponse(Call<RequestResponseDTO> call, Response<RequestResponseDTO> response) {
                if(!response.isSuccessful()) {

                    requestResponseDTO = response.body();
                    Log.i("schitt", "POST failed");
                    return;
                }
                juhu();
                Log.i("msg", response.body().getMessage());
                finish();
            }

            @Override
            public void onFailure(Call<RequestResponseDTO> call, Throwable t) {
                Log.i("schitt","schitt");
            }
        });

    }

    private void juhu() {
        Toast.makeText(this, "yeah", Toast.LENGTH_SHORT);
    }
}
