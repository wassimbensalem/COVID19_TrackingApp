package com.example.coronavirus;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.coronavirus.ui.achievements.AddDoctorActivity;
import com.example.coronavirus.ui.background_location.BackgroundLocationService;
import com.example.coronavirus.ui.login.LoginActivity;
import com.example.coronavirus.ui.map.ChooseActivity;
import com.example.coronavirus.ui.map.MapsFragment;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;

import androidx.core.app.ActivityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    private FusedLocationProviderClient fusedLocationClient;


    private String username;
    private String password;
    private String token;
    private LatLng myPosition;
    public Intent locationService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String name = bundle.getString("name");
            ///
        }

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_info, R.id.navigation_dashboard, R.id.navigation_doctors, R.id.navigation_achievements,
                R.id.navigation_map, R.id.navigation_admin
        )
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
        boolean isAdmin= getIntent().getBooleanExtra("isAdmin", false);
        Toast.makeText(this, isAdmin+"", Toast.LENGTH_LONG);
        if(!isAdmin) {
            BottomNavigationView bottomNavigationView = findViewById(R.id.nav_view);
            bottomNavigationView.getMenu().removeItem(R.id.navigation_admin);
        }
        Intent intent = getIntent();
        token = intent.getStringExtra("token");
        password = intent.getStringExtra(LoginActivity.PASSWORD);
        getLocation();


    }

    public void openOfferEssentials(View view) {
        Intent intent = new Intent(this, ChooseActivity.class);
        String token = getIntent().getStringExtra("token");
        intent.putExtra("username", getIntent().getStringExtra(LoginActivity.USERNAME));
        intent.putExtra("token", token);
        if (myPosition != null) {
            intent.putExtra("longitude", Double.toString(myPosition.longitude));
            intent.putExtra("latitude", Double.toString(myPosition.latitude));
        }
        startActivity(intent);
    }

    public  void addDoctor(View view){
        Intent intent = new Intent(this, AddDoctorActivity.class);
        startActivity(intent);
        // String username = intent.getStringExtra(RegisterActivity.REGISTER_USERNAME);
        // EditText usernameElement = (EditText) findViewById(R.id.username);
        // usernameElement.setText(username);
        finish();
    }

    public void  setLocation(LatLng myPosition){
        this.myPosition = myPosition;
        //Toast.makeText(this, Double.toString(myPosition.latitude), Toast.LENGTH_SHORT).show();

    }



    //@RequiresApi(api = Build.VERSION_CODES.O)
    private void getLocation() {

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{"android.permission.ACCESS_FINE_LOCATION","android.permission.ACCESS_COARSE_LOCATION","android.permission.FOREGROUND_SERVICE","android.permission.CAMERA"},9);

        }
       else {
            locationService = new Intent(this, BackgroundLocationService.class);
            //getActivity().startService(locationService);
            locationService.putExtra("token", token);
            locationService.putExtra("username", getIntent().getStringExtra(LoginActivity.USERNAME));
            this.startService((locationService));

        }
    }


    public void onRequestPermissionsResults(int requestCode, String[] permissions,
                                            int[] grantResults) {
        switch (requestCode) {
            case 9:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission is granted. Continue the action or workflow
                    // in your app.
                    getLocation();

                }  else {
                    // Explain to the user that the feature is unavailable because
                    // the features requires a permission that the user has denied.
                    // At the same time, respect the user's decision. Don't link to
                    // system settings in an effort to convince the user to change
                    // their decision.
                    getLocation();
                }
                return;
        }
        // Other 'case' lines to check for other
        // permissions this app might request.
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
    }

    public String getUsername() {
        return username;
    }

}
