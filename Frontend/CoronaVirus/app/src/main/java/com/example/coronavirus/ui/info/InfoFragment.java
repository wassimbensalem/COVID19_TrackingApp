package com.example.coronavirus.ui.info;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.coronavirus.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class InfoFragment extends  Fragment{

    private InfoViewModel infoViewModel;
    private CoronaSummaryDTO coronaSummaryDTO;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        infoViewModel =
                ViewModelProviders.of(this).get(InfoViewModel.class);
        View root = inflater.inflate(R.layout.fragment_info, container, false);
        final TextView total_affected = root.findViewById(R.id.textView12);
        total_affected.setText("9999");
        final TextView total_death = root.findViewById(R.id.textView14);
        total_death.setText("5555");
        final TextView total_recovered = root.findViewById(R.id.textView18);
        total_recovered.setText("1111");

        getCoronaSummary();


        return root;
    }


    private void getCoronaSummary(){
        Call<CoronaSummaryDTO> call;
        CoronaStatisticsPlaceholder coronaStatisticsPlaceholder;

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.covid19api.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        coronaStatisticsPlaceholder = retrofit.create(CoronaStatisticsPlaceholder.class);

        call = coronaStatisticsPlaceholder.getCoronaSummary();

        call.enqueue(new Callback<CoronaSummaryDTO>() {
            @Override
            public void onResponse(Call<CoronaSummaryDTO> call, Response<CoronaSummaryDTO> response) {
                if(!response.isSuccessful()){
                    return;
                }
                coronaSummaryDTO = response.body();
                Log.i("resp",response.body()+"d");
                Log.i("OK",Integer.toString(coronaSummaryDTO.getGlobal().getTotalConfirmed()));
                TextView total_recovered = getActivity().findViewById(R.id.textView12);
                total_recovered.setText(Integer.toString(coronaSummaryDTO.getGlobal().getTotalConfirmed()));
                total_recovered = getActivity().findViewById(R.id.textView14);
                total_recovered.setText(Integer.toString(coronaSummaryDTO.getGlobal().getTotalDeaths()));
                total_recovered = getActivity().findViewById(R.id.textView18);
                total_recovered.setText(Integer.toString(coronaSummaryDTO.getGlobal().getTotalRecovered()));
                total_recovered = getActivity().findViewById(R.id.textView16);
                total_recovered.setText(Integer.toString(coronaSummaryDTO.getGlobal().getNewConfirmed()));
                total_recovered = getActivity().findViewById(R.id.textView20);
                total_recovered.setText(Integer.toString(coronaSummaryDTO.getGlobal().getNewDeaths()));
            }

            @Override
            public void onFailure(Call<CoronaSummaryDTO> call, Throwable t) {

            }
        });
    }

}
