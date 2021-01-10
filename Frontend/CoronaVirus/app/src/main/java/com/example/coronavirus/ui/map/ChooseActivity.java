package com.example.coronavirus.ui.map;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.coronavirus.R;
import com.example.coronavirus.ui.login.LoginActivity;

public class ChooseActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose);


    }

    public void openRequest(View view) {
        Intent intent = new Intent(this, RequestActivity.class);
        String username=getIntent().getStringExtra("username");
        String longitude=getIntent().getStringExtra("longitude");
        String latitude=getIntent().getStringExtra("latitude");
        String token = getIntent().getStringExtra("token");
        intent.putExtra("token", token);
        intent.putExtra("username", username);
        intent.putExtra("longitude", longitude);
        intent.putExtra("latitude", latitude);
        startActivity(intent);
        finish();
    }

    public void openOffer(View view) {
        Intent intent = new Intent(this, OfferActivity.class);
        String username=getIntent().getStringExtra("username");
        String longitude=getIntent().getStringExtra("longitude");
        String latitude=getIntent().getStringExtra("latitude");
        String token = getIntent().getStringExtra("token");
        intent.putExtra("token", token);
        intent.putExtra("username", username);
        intent.putExtra("longitude", longitude);
        intent.putExtra("latitude", latitude);
        startActivity(intent);
        finish();
    }

    public void openStore(View view) {
        Intent intent = new Intent(this, StoreActivity.class);
        String longitude=getIntent().getStringExtra("longitude");
        String latitude=getIntent().getStringExtra("latitude");

        intent.putExtra("longitude", longitude);
        intent.putExtra("latitude", latitude);
        startActivity(intent);
        finish();
    }

}
