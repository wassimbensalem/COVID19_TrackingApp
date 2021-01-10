package com.example.coronavirus.ui.doctors;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.coronavirus.R;

public class DoctorsFragment extends  Fragment {

    private DoctorsViewModel doctorsViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        doctorsViewModel =
                ViewModelProviders.of(this).get(DoctorsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_doctors, container, false);
        final TextView textView = root.findViewById(R.id.text_doctor);
        doctorsViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }
}
