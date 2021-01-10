package com.example.coronavirus.ui.map.onStoreClick;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.coronavirus.Config;
import com.example.coronavirus.R;
import com.example.coronavirus.ui.achievements.AchievementsFragment;
import com.example.coronavirus.ui.achievements.AchievementsFragmentPlaceholder;
import com.example.coronavirus.ui.achievements.Doctors;
import com.example.coronavirus.ui.map.StoreActivityPlaceholder;
import com.example.coronavirus.ui.map.StoresDTO;
import com.example.coronavirus.ui.map.onStoreClick.addEssentialToStore.AddEssentialToStore;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class OnStoreClick extends AppCompatActivity {

    StoresDTO store = null;
    String[] essentials;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_store_click);
        Button addEssential = findViewById(R.id.addEssentialToStore);
        addEssential.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addEssential();
            }
        });
        getEssentialsSummary();
    }

    private void addEssential() {
        Intent intent = new Intent(this, AddEssentialToStore.class);
        intent.putExtra("store_name", getIntent().getStringExtra("store_name"));
        startActivity(intent);
    }

    private void getEssentialsSummary(){
        Call<List<StoresDTO>> call;
        StoreActivityPlaceholder storeActivityPlaceholder;

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Config.APIUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        storeActivityPlaceholder = retrofit.create(StoreActivityPlaceholder.class);
        call = storeActivityPlaceholder.getStores();


        call.enqueue(new Callback<List<StoresDTO>>() {
            @Override
            public void onResponse(Call<List<StoresDTO>> call, Response<List<StoresDTO>> response) {
                if(!response.isSuccessful()){
                    Log.i("GET Stores","FAILEDDD 2 : " + response);
                    return;
                }
                Log.i("KLAPPT","succe 2 ");
                List<StoresDTO> list = response.body();
                String store_name = getIntent().getStringExtra("store_name");

                for(StoresDTO s : list) {
                    if (s.name.equals(store_name)) {
                        store = s;
                    }
                }
            createListView(store);
            }

            @Override
            public void onFailure(Call<List<StoresDTO>> call, Throwable t) {
                Log.i("GET Stores","FAILEDDD 3 ");
            }
        });
    }

    public  void createListView(StoresDTO store) {
        ListView listView = findViewById(R.id.listview_store);
        String text = "Available: ";
        essentials = new String[store.essentials.size()];

        for (int i = 0; i< store.essentials.size(); i++) {
            String e_description = "(" + store.essentials.get(i).name + "," +
                    store.essentials.get(i).price + "," +
                    store.essentials.get(i).quantity + ")";
            essentials[i] = e_description;
        }
            String name = store.name;
            String description = "Available essentials in " + name;
            TextView store_name = findViewById(R.id.store_description);
            store_name.setText(description);
            CustomerAdapter customerAdapter = new CustomerAdapter();
            listView.setAdapter(customerAdapter);
        }

    private class CustomerAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return store.essentials.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View view1 = getLayoutInflater().inflate(R.layout.store_description_list_item,null);
            TextView description = view1.findViewById(R.id.description);
            description.setText(essentials[position]);

            return view1;
        }
    }
    }


