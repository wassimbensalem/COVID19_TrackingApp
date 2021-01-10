package com.example.coronavirus.ui.admin;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.coronavirus.R;
import com.example.coronavirus.ui.map.OfferActivity;
import com.example.coronavirus.ui.map.RequestActivity;

public class TraceActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trace);

        Intent intent = getIntent();
        TraceResponseDTO data = (TraceResponseDTO) intent.getSerializableExtra("data");

        final Button deleteButton = findViewById(R.id.infectionMap);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                Intent intent = new Intent(getApplicationContext(), InfectionMapActivity.class);
                intent.putExtra("data", data);
                startActivity(intent);
            }
        });

        TableLayout ll = (TableLayout) findViewById(R.id.trace_table);

        TableRow row = new TableRow(this);
        TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
        row.setLayoutParams(lp);
        TextView name = new TextView(this);
        TextView date = new TextView(this);
        TextView risk = new TextView(this);
        name.setPadding(0, 0, 20, 0);
        name.setTextColor(Color.parseColor("#FFFFFF"));
        name.setTypeface(null, Typeface.BOLD);
        name.setText("Username");
        date.setPadding(20, 0, 20, 0);
        date.setTextColor(Color.parseColor("#FFFFFF"));
        date.setTypeface(null, Typeface.BOLD);
        date.setText("Date");
        risk.setPadding(20, 0, 20, 0);
        risk.setTextColor(Color.parseColor("#FFFFFF"));
        risk.setTypeface(null, Typeface.BOLD);
        risk.setText("Risk");
        //row.addView(intendtext);
        row.addView(name);
        row.addView(date);
        row.addView(risk);
        ll.addView(row);

        addRow(ll, data, 0);
    }

    private void addRow(TableLayout table, TraceResponseDTO data, Integer intend) {
        TableRow row = new TableRow(this);
        TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
        row.setLayoutParams(lp);
        TextView name = new TextView(this);
        TextView date = new TextView(this);
        TextView risk = new TextView(this);
        name.setPadding(intend * 40, 0, 20, 0);
        name.setTextColor(Color.parseColor("#FFFFFF"));
        name.setText(data.name);
        date.setPadding(20, 0, 20, 0);
        date.setTextColor(Color.parseColor("#FFFFFF"));
        date.setText(data.date);
        risk.setPadding(20, 0, 20, 0);
        risk.setTextColor(Color.parseColor("#FFFFFF"));
        risk.setText(String.format("%.4f", data.risk));
        //row.addView(intendtext);
        row.addView(name);
        row.addView(date);
        row.addView(risk);
        table.addView(row);

        Integer newin = intend + 1;
        for (TraceResponseDTO contact :
                data.contacts) {
            addRow(table, contact, newin);
        }
    }
}