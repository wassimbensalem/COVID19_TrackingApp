package com.example.coronavirus.ui.map.onEssentialClick;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.example.coronavirus.R;


public class OnEssentialClick extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_essential_click);

        String name = getIntent().getStringExtra("name");
        String offReq = getIntent().getStringExtra("offReq");
        String price = getIntent().getStringExtra("price");
        String quantity = getIntent().getStringExtra("quantity");
        String email = getIntent().getStringExtra("email");

        ( (TextView) findViewById(R.id.offer_request)).setText(offReq);
        ( (TextView) findViewById(R.id.essential_name)).setText("Name: "+name);
        ( (TextView) findViewById(R.id.essential_price)).setText("Price: "+price);
        ( (TextView) findViewById(R.id.essential_quantity)).setText("Quantity: "+quantity);
        ( (TextView) findViewById(R.id.essential_email)).setText("Email: "+email);
    }
}