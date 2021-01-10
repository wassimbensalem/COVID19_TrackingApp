package com.example.coronavirus.ui.map;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.coronavirus.Config;
import com.example.coronavirus.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class StoreActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);
    }

    public void submit(View view) {
        EditText store_name = (EditText) findViewById(R.id.store_name);
        String name = store_name.getText().toString();

        String lat = getIntent().getStringExtra("latitude");
        String lon = getIntent().getStringExtra("longitude");
        String[] location = {lat,lon};

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Config.APIUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        StoreActivityPlaceholder storeActivityPlaceholder;
        storeActivityPlaceholder = retrofit.create(StoreActivityPlaceholder.class);
        Call<StoreResponseDTO> call = storeActivityPlaceholder.postStore(name, location);
        Log.i("location:" , location[0] + ", " + location[1]);

        call.enqueue(new Callback<StoreResponseDTO>() {
            @Override
            public void onResponse(Call<StoreResponseDTO> call, Response<StoreResponseDTO> response) {
                if(!response.isSuccessful()) {

                    Log.i("POST failed", "POST failed");
                    return;
                }
                finish();
            }

            @Override
            public void onFailure(Call<StoreResponseDTO> call, Throwable t) {
                Log.i("postStore failed","postStore failed");
            }
        });

    }
}
