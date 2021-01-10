package com.example.coronavirus.ui.map;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.coronavirus.Config;
import com.example.coronavirus.MainActivity;
import com.example.coronavirus.R;
import com.example.coronavirus.ui.dashboard.qrCodeScanner.QrCodeScannerPlaceholder;
import com.example.coronavirus.ui.login.LoginActivity;
import com.example.coronavirus.ui.login.RegisterResponseDTO;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.FileWriter;
import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class OfferActivity extends AppCompatActivity {

    public static final String OFFER = "com.example.coronavirus.OFFER";
    private OfferActivityPlaceholder offerActivityPlaceholder;
    private OfferResponseDTO offerResponseDTO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offer);
    }

    public void submit(View view) {
        EditText item_name = (EditText) findViewById(R.id.item_nameOffer);
        String name = item_name.getText().toString();
        EditText item_quantity = (EditText) findViewById(R.id.item_quantityOffer);
        String quantity = item_quantity.getText().toString();
        EditText item_category = (EditText) findViewById(R.id.categoryOffer);
        String category = item_category.getText().toString();
        EditText item_price = (EditText) findViewById(R.id.priceOffer);
        String price = item_price.getText().toString();
        EditText item_email = (EditText) findViewById(R.id.telephone_numberOffer);
        String email = item_email.getText().toString();
        String username = getIntent().getStringExtra("username");
        String lat = getIntent().getStringExtra("latitude");
        String lon = getIntent().getStringExtra("longitude");
        String[] location = {lat,lon};
        System.out.println("--------------------------");
        Log.i("Error","----------------------------------");
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Config.APIUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        OfferActivityPlaceholder offerActivityPlaceholder;
        offerActivityPlaceholder = retrofit.create(OfferActivityPlaceholder.class);


        String token = getIntent().getStringExtra("token");
        Call<OfferResponseDTO> call= offerActivityPlaceholder.postOffer(name,category, quantity, price, username,location, email, token);
        call.enqueue(new Callback<OfferResponseDTO>() {
            @Override
            public void onResponse(Call<OfferResponseDTO> call, Response<OfferResponseDTO> response) {
                if(!response.isSuccessful()) {

                    offerResponseDTO = response.body();
                    Log.i("schitt", "POST failed");
                    return;
                }
                finish();
                Log.i("msg", response.body().getMessage());
            }

            @Override
            public void onFailure(Call<OfferResponseDTO> call, Throwable t) {
                Log.i("schitt","schitt");
            }
        });



    }
}
