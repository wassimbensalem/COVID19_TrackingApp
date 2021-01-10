package com.example.coronavirus.ui.map.onStoreClick.addEssentialToStore;

import androidx.appcompat.app.AppCompatActivity;

import com.example.coronavirus.Config;
import com.example.coronavirus.R;
import com.example.coronavirus.ui.achievements.AddDoctorResponseDTO;
import com.example.coronavirus.ui.map.OfferActivityPlaceholder;
import com.example.coronavirus.ui.map.OfferResponseDTO;
import com.example.coronavirus.ui.map.StoreActivityPlaceholder;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AddEssentialToStore extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_essential_to_store);
    }

   public void submit(View view){

       Retrofit retrofit = new Retrofit.Builder()
               .baseUrl(Config.APIUrl)
               .addConverterFactory(GsonConverterFactory.create())
               .build();
       StoreActivityPlaceholder storeActivityPlaceholder;
       storeActivityPlaceholder = retrofit.create(StoreActivityPlaceholder.class);

       String storeName = getIntent().getStringExtra("store_name");
       EditText item_name = (EditText) findViewById(R.id.item_name_store);
       String essentialName = item_name.getText().toString();
       EditText item_quantity = (EditText) findViewById(R.id.item_quantity_store);
       String essentialQuantity = item_quantity.getText().toString();
       EditText item_price = (EditText) findViewById(R.id.price_store_essential);
       String essentialPrice = item_price.getText().toString();
       Call<AddDoctorResponseDTO> call= storeActivityPlaceholder.addEssential(storeName, essentialName, essentialQuantity, essentialPrice);
       call.enqueue(new Callback<AddDoctorResponseDTO>() {
           @Override
           public void onResponse(Call<AddDoctorResponseDTO> call, Response<AddDoctorResponseDTO> response) {
               if(!response.isSuccessful()) {
                   if(response.body().getError().size()> 0) {
                       String error = response.body().getError().get(0);
                       TextView a = findViewById(R.id.submit_error_store);
                       a.setText(error);
                   }
                   Log.i("schitt", "POST failed");
                   return;
               }
               finish();
           }

           @Override
           public void onFailure(Call<AddDoctorResponseDTO> call, Throwable t) {
               Log.i("schitt","schitt");
           }
       });

    }
}