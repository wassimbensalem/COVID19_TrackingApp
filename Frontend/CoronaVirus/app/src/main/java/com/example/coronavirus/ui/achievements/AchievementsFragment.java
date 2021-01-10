package com.example.coronavirus.ui.achievements;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.coronavirus.Config;
import com.example.coronavirus.R;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AchievementsFragment extends Fragment {

    private AchievementsViewModel notificationsViewModel;
    private List<Doctors> doctorsDTO = new ArrayList<Doctors>();

    // -----

    String[] docNames = new String[doctorsDTO.size()] ;
    String[] docAdress = new String[doctorsDTO.size()] ;;



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        notificationsViewModel =
            ViewModelProviders.of(this).get(AchievementsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_achievements, container, false);

        //createListDoctor(root);
        getDoctorsSummary(root);

        final Button register = root.findViewById(R.id.addDocBtn);
        register.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                addDoc(v);
            }
        });

        return root;
    }

    public  void addDoc(View view){
        Intent intent = new Intent(getActivity(), AddDoctorActivity.class);
        startActivity(intent);

    }

    private void getDoctorsSummary(View root){
        Call<List<Doctors>> call;
        AchievementsFragmentPlaceholder achievementsFragmentPlaceholder;

        Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(Config.APIUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build();

        achievementsFragmentPlaceholder = retrofit.create(AchievementsFragmentPlaceholder.class);
        call = achievementsFragmentPlaceholder.getDoctors();

        call.enqueue(new Callback<List<Doctors>>() {
            @Override
            public void onResponse(Call<List<Doctors>> call, Response<List<Doctors>> response) {
                if(!response.isSuccessful()){
                    Log.i("FAILURE","FAILED " + response);
                    return;
                }
                Log.i("WORKS","2");
                doctorsDTO = response.body();







                ListView listView=root.findViewById(R.id.listview);
                docNames = new String[doctorsDTO.size()] ;
                docAdress = new String[doctorsDTO.size()] ;;

                for (int i=0;i<doctorsDTO.size();i++) {
                    docNames[i] = doctorsDTO.get(i).getProperties().getName();
                    docAdress[i] =  doctorsDTO.get(i).getAdress() ;
                    Log.i("doc i ",doctorsDTO.get(i) + "KLAPPT " + i);
                    Log.i("doc i ",docAdress[i]  + "KLAPPT " + i);

                }




                final ArrayList<String> arrayList=new ArrayList<>();


                CustomerAdapter customerAdapter = new CustomerAdapter();
                listView.setAdapter(customerAdapter);



            }

            @Override
            public void onFailure(Call<List<Doctors>> call, Throwable t) {
                Log.i("FAILURE","FAILEDDD 3 ");
            }
        });
    }

    private class CustomerAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return doctorsDTO.size();
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

            View view1 = getLayoutInflater().inflate(R.layout.doctor_list_item,null);
            TextView docName = view1.findViewById(R.id.docNameView);
            TextView docAdresses = view1.findViewById(R.id.docAdressView);
            docName.setText(docNames[position]);
            docAdresses.setText(docAdress[position]);

            return view1;
        }
    }
}
