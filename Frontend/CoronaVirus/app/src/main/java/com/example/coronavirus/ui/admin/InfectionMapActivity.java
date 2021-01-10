package com.example.coronavirus.ui.admin;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.coronavirus.Config;
import com.example.coronavirus.MainActivity;
import com.example.coronavirus.R;
import com.example.coronavirus.RandomColors;
import com.example.coronavirus.ui.map.GeoJSONPoint;
import com.example.coronavirus.ui.map.MapFragmentPlaceholder;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

import io.ticofab.androidgpxparser.parser.GPXParser;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class InfectionMapActivity extends AppCompatActivity {

    private static final String TAG = "InfectionMapActivity";
    GPXParser mParser = new GPXParser(); // consider injection
    TraceResponseDTO gdata;
    RandomColors rc;
    LatLngBounds.Builder builder = new LatLngBounds.Builder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_infectionmap);

        rc = new RandomColors();

        Intent intent = getIntent();
        gdata = (TraceResponseDTO) intent.getSerializableExtra("data");

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }


    }


    private final OnMapReadyCallback callback = new OnMapReadyCallback() {

        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        @Override
        public void onMapReady(GoogleMap googleMap) {
            // Zoom to Darmstadt
            LatLng darmstadt = new LatLng(49.8751, 8.6573);
            //setLocation(darmstadt);

            showContact(gdata, googleMap, "");

            LatLngBounds bounds = builder.build();

            int width = getResources().getDisplayMetrics().widthPixels;
            int height = getResources().getDisplayMetrics().heightPixels;
            int padding = (int) (width * 0.10); // offset from edges of the map 10% of screen

            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);

            googleMap.animateCamera(cu);

        }
    };

    private void showContact(TraceResponseDTO data, GoogleMap googleMap, String oldName) {

        if(data.location != null) {
            LatLng ContactPoint = new LatLng(data.location.get(1), data.location.get(0));
            googleMap.addMarker(new MarkerOptions()
                    .position(ContactPoint).title(oldName+" meet "+data.name).snippet(data.date));

            builder.include(ContactPoint);
        }

        getTrack(googleMap, data.user, data.isodate);

        for (TraceResponseDTO contact :
                data.contacts) {
            showContact(contact, googleMap, data.name);
        }
    }

    private void getTrack(GoogleMap googleMap, String userid, String date){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Config.APIUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        AdminFragmentPlaceholder AdminFragmentPlaceholder;
        AdminFragmentPlaceholder = retrofit.create(AdminFragmentPlaceholder.class);
        Call<GeoJSONPoint> call;
        String token = getIntent().getStringExtra("token");
        call = AdminFragmentPlaceholder.getInfectionTrace(token, userid, date);

        call.enqueue(new Callback<GeoJSONPoint>() {
            @Override
            public void onResponse(Call<GeoJSONPoint> call, Response<GeoJSONPoint> response) {
                if(!response.isSuccessful()){
                    Log.i("hilfe","error");
                    return;
                }
               GeoJSONPoint track = response.body();
                drawTrack(track,googleMap);

            }

            @Override
            public void onFailure(Call<GeoJSONPoint> call, Throwable t) {
                Log.i("falsch","otto");
                Log.i(t.getMessage(),t.getMessage());
            }
        });
    }

    private void drawTrack(GeoJSONPoint body, GoogleMap googleMap) {
        ArrayList<LatLng> points;
        PolylineOptions polyLineOptions = null;
        points = new ArrayList<>();
        polyLineOptions = new PolylineOptions();

       for (int i=0;i<body.getLatitude().size();i++){
            LatLng position = new LatLng(body.getLatitude().get(i), body.getLongitude().get(i));
            points.add(position);
        }
        polyLineOptions.addAll(points);
        polyLineOptions.width(7);
        polyLineOptions.color(rc.getColor());

        googleMap.addPolyline(polyLineOptions);

    }

}
