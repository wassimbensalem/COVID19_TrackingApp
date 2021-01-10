package com.example.coronavirus.ui.dashboard;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.coronavirus.Config;
import com.example.coronavirus.MainActivity;
import com.example.coronavirus.R;
import com.example.coronavirus.ui.achievements.AchievementsFragment;
import com.example.coronavirus.ui.achievements.AddDoctorActivity;
import com.example.coronavirus.ui.dashboard.qrCodeScanner.QrCodeScanner;
import com.example.coronavirus.ui.login.LoginActivity;
import com.example.coronavirus.ui.map.OfferActivityPlaceholder;

import android.content.Intent;
import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;



public class DashboardFragment extends Fragment {

    private DashboardViewModel dashboardViewModel;
    String riskFactor = "0";



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel =
            ViewModelProviders.of(this).get(DashboardViewModel.class);
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);
        MainActivity myActivity = (MainActivity) getActivity();

        final Button button_call =  root.findViewById(R.id.buttoncall);
        final Button button_sms =  root.findViewById(R.id.button2);
        button_call.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {


                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel: 116117"));


                startActivity(callIntent);
            }
        });
        button_sms.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {

                Intent sendIntent = new Intent(Intent.ACTION_VIEW);
                sendIntent.setData(Uri.parse("sms:"));

                startActivity(sendIntent);
            }
        });

        Button setInfected = root.findViewById(R.id.dashboard_markInfected);
        setInfected.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {

                Intent sendIntent = new Intent(getActivity(),QrCodeScanner.class);
                String token = getActivity().getIntent().getStringExtra("token");
                sendIntent.putExtra("token",token);

                startActivity(sendIntent);
            }
        });

        ImageButton logout = root.findViewById(R.id.button3);
        logout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                Intent intent = new Intent(getActivity(),LoginActivity.class);
                ((MainActivity) getActivity()).stopService( ((MainActivity) getActivity()).locationService);
                startActivity(intent);
                getActivity().finish();
            }
        });



        String username = getActivity().getIntent().getStringExtra(LoginActivity.USERNAME);
        TextView total_recovered = root.findViewById(R.id.dashboard_username);
        total_recovered.setText("Hello, " + username + "!");

        ArrayList<Float> riskFactors = new ArrayList<Float>();

        Retrofit retrofit1 = new Retrofit.Builder()
                .baseUrl(Config.APIUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();


        RiskFactorPlaceholder riskFactorPlaceholder;
        riskFactorPlaceholder = retrofit1.create(RiskFactorPlaceholder.class);

        Call<RiskFactorDTO> callRiskFactor = riskFactorPlaceholder.getRiskFactor(username);
        callRiskFactor.enqueue(new Callback<RiskFactorDTO>() {
            @Override
            public void onResponse(Call<RiskFactorDTO> call, Response<RiskFactorDTO> response) {
                if(!response.isSuccessful()){
                    Log.i("GET", "failed");
                    return;
                }

                riskFactor = response.body().getRiskFactor() + "";


            }

            @Override
            public void onFailure(Call<RiskFactorDTO> call, Throwable t) {
                Log.i("schitt","schitt");
            }
        });


        InfectionPlaceholder infectionPlaceholder;
        infectionPlaceholder = retrofit1.create(InfectionPlaceholder.class);

        Call<InfectionDTO> call= infectionPlaceholder.getCoronaSummary(username);


        call.enqueue(new Callback<InfectionDTO>() {
            @Override
            public void onResponse(Call<InfectionDTO> call, Response<InfectionDTO> response) {
                int color;
                if(!response.isSuccessful()){
                    Log.i("GET", "failed");
                    Log.i("error:", response.errorBody().toString());
                    return;
                }
                Boolean isInfected = response.body().getStatus();
                String isInf = new String();
                if(isInfected) {
                    isInf = "infected \n Riskfactor: " + riskFactor ;
                    color = Color.rgb(193,64,61);
                    openDialog("You may be infected. Please consider going to the doctor. Take care of yourself and those around you.");
                }
                else {
                    isInf = "not infected \n Riskfactor: " + riskFactor;
                    color = Color.rgb(2,148,165);
                }

                TextView infected_textView = root.findViewById(R.id.isInfected);
                infected_textView.setText("You are " + isInf);
                infected_textView.setBackgroundColor(color);


            }

            @Override
            public void onFailure(Call<InfectionDTO> call, Throwable t) {
                Log.i("schitt","schitt");

            }
        });

        return root;
    }


    public  void  openDialog (String msg) {
        AlertDialog alertDialog = new AlertDialog();
        alertDialog.setText(msg);
        FragmentManager fm = DashboardFragment.this.getActivity().getSupportFragmentManager();
        alertDialog.show(fm, "alert dialog");
    }
}
