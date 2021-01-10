package com.example.coronavirus.ui.dashboard.qrCodeScanner;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.coronavirus.Config;
import com.example.coronavirus.R;
import com.example.coronavirus.ui.admin.AdminFragmentPlaceholder;
import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class QrCodeScanner extends AppCompatActivity implements ZXingScannerView.ResultHandler{
    private ZXingScannerView mScannerView;
    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        // Programmatically initialize the scanner view
        mScannerView = new ZXingScannerView(this);
        // Set the scanner view as the content view
        setContentView(mScannerView);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Register ourselves as a handler for scan results.
        mScannerView.setResultHandler(this);
        // Start camera on resume
        mScannerView.startCamera();
    }

    @Override
    public void onPause() {
        super.onPause();
        // Stop camera on pause
        mScannerView.stopCamera();
    }

    @Override
    public void handleResult(Result rawResult) {
        // Do something with the result here
        // Prints scan results
        // Toast.makeText(QrCodeScanner.this, rawResult.getText(), Toast.LENGTH_SHORT).show();
        post(rawResult.getText());

       // finish();
    }

    void post(String code){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Config.APIUrl)
                //.baseUrl("http://localhost:8080/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        QrCodeScannerPlaceholder qrCodeScannerPlaceholder;
        qrCodeScannerPlaceholder = retrofit.create(QrCodeScannerPlaceholder.class);
        Call<String> call = null;
        String token = getIntent().getStringExtra("token");

        call = qrCodeScannerPlaceholder.readCode(token, code);


        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(!response.isSuccessful()){
                    Log.i("unsuccessfull","error");
                    return;
                }
                String get = response.body();
                Toast.makeText(QrCodeScanner.this, get, Toast.LENGTH_SHORT).show();
               if (!(get.equals("code not found")||get.equals("no user not found")))
                   finish();

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.i("onFailure","otto");
                Log.i(t.getMessage(),t.getMessage());
            }
        });
    }
}