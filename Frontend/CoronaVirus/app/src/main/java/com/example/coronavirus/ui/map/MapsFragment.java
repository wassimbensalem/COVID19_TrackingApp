package com.example.coronavirus.ui.map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.coronavirus.Config;
import com.example.coronavirus.MainActivity;
import com.example.coronavirus.ui.background_location.BackgroundLocationServicePlaceholder;
import com.example.coronavirus.ui.login.LoginActivity;
import com.example.coronavirus.ui.map.onEssentialClick.OnEssentialClick;
import com.example.coronavirus.ui.map.onStoreClick.OnStoreClick;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;

import com.example.coronavirus.R;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import io.ticofab.androidgpxparser.parser.GPXParser;
import io.ticofab.androidgpxparser.parser.domain.Gpx;
import io.ticofab.androidgpxparser.parser.domain.Track;
import io.ticofab.androidgpxparser.parser.domain.TrackPoint;
import io.ticofab.androidgpxparser.parser.domain.TrackSegment;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MapsFragment extends Fragment {

    private static final String TAG = "MapsFragment";
    GPXParser mParser = new GPXParser(); // consider injection


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
            ((MainActivity) getActivity()).setLocation(darmstadt);

            showEssentials(googleMap);
            showOfferEssentials(googleMap);
            showStores(googleMap);


            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(darmstadt, 12.0f));

            getTrack(googleMap);

            googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    if (marker.isInfoWindowShown())
                    {

                        Intent a = new Intent(getActivity(), ChooseActivity.class);
                        startActivity(a);
                    }
                    return false;
                }
            });
            googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {
                    if (marker.getTitle().contains("Request")||marker.getTitle().contains("Offer")) {
                        Intent a = new Intent(getActivity(), OnEssentialClick.class);
                        Essential essential = (Essential) marker.getTag();

                        if (marker.getTitle().contains("Offer"))  a.putExtra("offReq", "Offer");
                        if (marker.getTitle().contains("Request"))  a.putExtra("offReq", "Request");
                        a.putExtra("name", essential.name);
                        a.putExtra("price", essential.price);
                        a.putExtra("quantity", essential.quantity);
                        a.putExtra("email", essential.email);

                        startActivity(a);
                    }
                    if (marker.getTitle().contains("Store")){
                        Intent a = new Intent(getActivity(), OnStoreClick.class);

                        a.putExtra("store_name", (String) marker.getTag());
                        startActivity(a);
                    }
                }
            });






        }


    };

    private void showStores(GoogleMap googleMap){

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Config.APIUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        StoreActivityPlaceholder storeActivityPlaceholder;
        storeActivityPlaceholder = retrofit.create(StoreActivityPlaceholder.class);

        Log.i("GET STORES FIRST " , "davor");
        Call<List<StoresDTO>> call = storeActivityPlaceholder.getStores();
        Log.i("GET STORES FIRST " , "call.toString()");
        call.enqueue(new Callback<List<StoresDTO>>() {
            @Override
            public void onResponse(Call<List<StoresDTO>> call, Response<List<StoresDTO>> response) {
                if(!response.isSuccessful()) {

                    Log.i("GET Stores","unsuccessful");
                    return;
                }
                Log.i("fetched data:", "successful");
                List<StoresDTO> list = response.body();

                for(StoresDTO s : list){
                    double lat = Double.parseDouble(s.location.get(0));
                    double lon = Double.parseDouble(s.location.get(1));
                    String title = s.name;

                    setMarkerStore(lat, lon, title, googleMap);
                }
            }

            @Override
            public void onFailure(Call<List<StoresDTO>> call, Throwable t) {

                Log.i("canceled:", Boolean.toString(call.isCanceled()));
                Log.i("exec:", Boolean.toString(call.isExecuted()));
                Log.i("GET Stores","failed");
                Log.i("GET Stores ----- > ",t.getMessage());
            }
        });
    }

    private void showOfferEssentials(GoogleMap googleMap) {
        String myName = getActivity().getIntent().getStringExtra(LoginActivity.USERNAME);
        String token = getActivity().getIntent().getStringExtra("token");

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Config.APIUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        OfferActivityPlaceholder offerActivityPlaceholder;
        offerActivityPlaceholder = retrofit.create(OfferActivityPlaceholder.class);

        Call<GetEssentialsDTO> call = offerActivityPlaceholder.getOffers("", token);
        call.enqueue(new Callback<GetEssentialsDTO>() {
            @Override
            public void onResponse(Call<GetEssentialsDTO> call, Response<GetEssentialsDTO> response) {
                if(!response.isSuccessful()) {

                    Log.i("schitt", "GET failed");
                    return;
                }

                Log.i("offer", "ok");
                List<EssentialsDTO> list = response.body().object;
                Log.i("offer", ""+ list.size());
                for(EssentialsDTO l : list){
                    for(EssentialsDTO.PropertiesScheme scheme : l.essentialProperties){
                        double lat = Double.parseDouble(scheme.ownerLocation.get(0));
                        double lon = Double.parseDouble(scheme.ownerLocation.get(1));
                        String snippet ="Email: "+scheme.email;
                        String text = "Offer: "+l.name;
                        Essential essential = new Essential(l.name, scheme.price, scheme.quantity, scheme.email);
                        setMarkerBlue(lat, lon, text, googleMap, snippet, essential);
                    }
                }
            }

            @Override
            public void onFailure(Call<GetEssentialsDTO> call, Throwable t) {
                Log.i("get_offers","failed");
            }
        });
    }


    public class Essential{
        String name;

        String price;

        String quantity;

        String email;

        public Essential(String name, String price, String quantity, String email) {
            this.name = name;
            this.price = price;
            this.quantity = quantity;
            this.email = email;
        }
    }


    private void setMarkerStore(double lat, double lon, String text, GoogleMap googleMap) {
        LatLng location = new LatLng(lat , lon);
        googleMap.addMarker(new MarkerOptions()
                .position(location).title("Store: "+text)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)))
        .setTag(text);
    }

    private void setMarkerBlue(double lat, double lon, String text, GoogleMap googleMap, String snippet, Essential essential) {
        LatLng TutorialsPoint = new LatLng(lat , lon);

        Marker m = googleMap.addMarker(new MarkerOptions()
                .position(TutorialsPoint).title(text).snippet(snippet)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
        m.setTag(essential);
    }

    public void showEssentials(GoogleMap googleMap) {

        String myName = getActivity().getIntent().getStringExtra(LoginActivity.USERNAME);
        String token = getActivity().getIntent().getStringExtra("token");

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Config.APIUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        OfferActivityPlaceholder offerActivityPlaceholder;
        offerActivityPlaceholder = retrofit.create(OfferActivityPlaceholder.class);

        Call<List<EssentialsDTO>> call = offerActivityPlaceholder.getOffer(myName, token);
        call.enqueue(new Callback<List<EssentialsDTO>>() {
            @Override
            public void onResponse(Call<List<EssentialsDTO>> call, Response<List<EssentialsDTO>> response) {
                if(!response.isSuccessful()) {

                    Log.i("schitt", "GET failed");
                    return;
                }

                Log.i("msg", "ok");
                List<EssentialsDTO> list = response.body();
                Log.i("len", Integer.toString(list.size()));
                for(EssentialsDTO l : list){
                    //Log.i("e_name", l.name);
                    for(EssentialsDTO.PropertiesScheme scheme : l.essentialProperties){
                        double lat = Double.parseDouble(scheme.ownerLocation.get(0));
                        double lon = Double.parseDouble(scheme.ownerLocation.get(1));
                        String snippet ="Email: "+scheme.email;
                        String text = "Request: "+l.name;
                        Essential essential = new Essential(l.name, scheme.price, scheme.quantity, scheme.email);
                        setMarker(lat, lon, text, googleMap, snippet, essential);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<EssentialsDTO>> call, Throwable t) {
                Log.i("get_requests","failed");
            }
        });
    }

    public void setMarker(double v1, double v2, String text, GoogleMap googleMap, String snippet, Essential essential){
        LatLng TutorialsPoint = new LatLng(v1 , v2);
        Marker m = googleMap.addMarker(new MarkerOptions()
                .position(TutorialsPoint).title(text).snippet(snippet)
        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
        m.setTag(essential);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_maps, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }


    }




    private void getTrack(GoogleMap googleMap){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Config.APIUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        MapFragmentPlaceholder mapFragmentPlaceholder;
        mapFragmentPlaceholder = retrofit.create(MapFragmentPlaceholder.class);
        Call<GeoJSONPoint> call;
        String token = getActivity().getIntent().getStringExtra("token");
        call = mapFragmentPlaceholder.getPoints(token, "hi");

        call.enqueue(new Callback<GeoJSONPoint>() {
            @Override
            public void onResponse(Call<GeoJSONPoint> call, Response<GeoJSONPoint> response) {
                if(!response.isSuccessful()){
                    Log.i("hilfe","error");
                    return;
                }
               GeoJSONPoint track = response.body();
              //  Log.i("hiu",track.getLatitude().get(0)+"");
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

       for (int i=0;i<body.latitude.size();i++){
            LatLng position = new LatLng(body.latitude.get(i), body.longitude.get(i));
            points.add(position);
        }
        polyLineOptions.addAll(points);
        polyLineOptions.width(5);
        polyLineOptions.color(Color.BLUE);

        googleMap.addPolyline(polyLineOptions);

        if (points.size()>0) {
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(points.get(points.size()-1), 12.0f));

            googleMap.addMarker(new MarkerOptions().position(points.get(points.size()-1)).title("Location"));

            ((MainActivity) getActivity()).setLocation((points.get(points.size()-1)));
        }

    }

}
