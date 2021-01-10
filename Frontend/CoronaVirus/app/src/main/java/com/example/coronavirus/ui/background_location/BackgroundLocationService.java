package com.example.coronavirus.ui.background_location;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.coronavirus.Config;
import com.example.coronavirus.MainActivity;
import com.example.coronavirus.R;
import com.example.coronavirus.ui.dashboard.RiskFactorDTO;
import com.example.coronavirus.ui.dashboard.RiskFactorPlaceholder;
import com.example.coronavirus.ui.login.LoginActivity;
import com.example.coronavirus.ui.login.LoginActivityPlaceholder;
import com.example.coronavirus.ui.login.LoginResponseDTO;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class BackgroundLocationService extends Service {
    private ArrayList<Location> locationArray = new ArrayList<Location>();
    private Intent intent;
    String riskFactor = null;
    String CHANNEL_ID = "124";

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCreate() {


        NotificationChannel chan = new NotificationChannel("5", "gunter", NotificationManager.IMPORTANCE_HIGH);
        chan.setDescription("<Add channel Description>");
        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        nm.createNotificationChannel(chan);

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent =
                PendingIntent.getActivity(this, 0, notificationIntent, 0);


        @SuppressLint("WrongConstant") Notification notification =
                new Notification.Builder(this, "5")
                        .setOngoing(true)
                        .setPriority(NotificationManager.IMPORTANCE_HIGH)
                        .setCategory(Notification.CATEGORY_SERVICE)
                        .setContentTitle("hi3")
                        .setContentText("hi2")
                        .setTicker("hi")
                        .build();

        startForeground(2, notification);

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();
        createNotificationChannel();

        this.intent = intent;

        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(10000);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return START_STICKY;
        }
        LocationCallback mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                getRiskFactor();
                List<Location> locationList = locationResult.getLocations();
                if (locationList.size() > 0) {
                    //The last location in the list is the newest
                    Location location = locationList.get(locationList.size() - 1);
                    Log.i("Service", "Location: " + location.getLatitude() + " " + location.getLongitude());
                    locationList.forEach(location1 -> locationArray.add(location1));
                    setDataInDatabase();
                }
            }
        };

        fusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());

        // If we get killed, after returning from here, restart
        return START_STICKY;
    }


    private void setDataInDatabase() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Config.APIUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        BackgroundLocationServicePlaceholder backgroundLocationServicePlaceholder;
        backgroundLocationServicePlaceholder = retrofit.create(BackgroundLocationServicePlaceholder.class);

        Call<String> call;

        String token = intent.getStringExtra("token");
        ArrayList<Double> latitude = new ArrayList<>();
        ArrayList<Double> longitude = new ArrayList<>();
        ArrayList<Long> date = new ArrayList<>();
        for(int i=0;i<locationArray.size();i++){
            Location y =locationArray.get(i);
            latitude.add(y.getLatitude());
            longitude.add(y.getLongitude());
            date.add(y.getTime());
        }
        call = backgroundLocationServicePlaceholder.postLoginCredentials(token, latitude, longitude, date);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(!response.isSuccessful()){
                    Log.i("error","error");
                    return;
                }
                if (response.body().equals("OK")) {
                    locationArray.clear();
                    Log.i("OK", response.body());
                }
               else  Log.i("hi", response.body());

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.i("falsch","error");
                Log.i(t.getMessage(),t.getMessage());
            }
        });
    }

    @Override
    public IBinder onBind(Intent intent) {
        // We don't provide binding, so return null
        return null;
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show();
    }

    public void getRiskFactor(){
        Retrofit retrofit1 = new Retrofit.Builder()
                .baseUrl(Config.APIUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RiskFactorPlaceholder riskFactorPlaceholder;
        riskFactorPlaceholder = retrofit1.create(RiskFactorPlaceholder.class);

        String username = intent.getStringExtra("username");
        Log.i("factor:", username);
        Call<RiskFactorDTO> callRiskFactor = riskFactorPlaceholder.getRiskFactor(username);
        callRiskFactor.enqueue(new Callback<RiskFactorDTO>() {
            @Override
            public void onResponse(Call<RiskFactorDTO> call, Response<RiskFactorDTO> response) {
                if(!response.isSuccessful()){
                    Log.i("GET", "failed");
                    return;
                }

                String newRiskFactor = response.body().getRiskFactor();

                Log.i("factor2:", response.body().getRiskFactor());
                if(riskFactor == null){
                    riskFactor = newRiskFactor;
                    return;
                }
                if(!riskFactor.equals( newRiskFactor)){
                    riskFactor = newRiskFactor;
                    Log.i("factor:", riskFactor);
                    createNotification(newRiskFactor);

                }

            }

            @Override
            public void onFailure(Call<RiskFactorDTO> call, Throwable t) {
                Log.i("schitt","schitt");
            }
        });
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        Log.i("create:", "called");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String cName = "notification";
            CharSequence name = cName;
            String description = "Notification channel";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void createNotification(String newRiskFactor){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.india)
                .setContentTitle("Alert!")
                .setContentText("Risk factor: changed to " + newRiskFactor)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(124, builder.build());
    }

}
