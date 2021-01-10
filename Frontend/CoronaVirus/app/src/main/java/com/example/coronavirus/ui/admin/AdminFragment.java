package com.example.coronavirus.ui.admin;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.coronavirus.Config;
import com.example.coronavirus.R;
import com.example.coronavirus.ui.login.LoginActivity;
import com.example.coronavirus.ui.map.GeoJSONPoint;
import com.example.coronavirus.ui.map.MapFragmentPlaceholder;
import com.example.coronavirus.ui.map.OfferActivity;
import com.google.android.gms.common.SignInButton;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class AdminFragment extends Fragment {

String code;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_admin, container, false);

        final Button deleteButton= root.findViewById(R.id.admin_delete);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                post("delete");
            }
        });
        final Button infectedButton= root.findViewById(R.id.admin_makeInfected);
        infectedButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                post("infected");
            }
        });
        final Button healthyButton= root.findViewById(R.id.admin_markHealthy);
        healthyButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                post("healthy");
            }
        });
        final Button traceButton= root.findViewById(R.id.admin_trace);
        traceButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                trace();
            }
        });
        final Button qrCodeButton = root.findViewById(R.id.admin_addCode);
        qrCodeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                EditText usernameElement = (EditText) root.findViewById(R.id.admin_username);
                code = usernameElement.getText().toString();
                QRCodeWriter writer = new QRCodeWriter();
                try {
                    BitMatrix bitMatrix = writer.encode(code, BarcodeFormat.QR_CODE, 512, 512);
                    int width = bitMatrix.getWidth();
                    int height = bitMatrix.getHeight();
                    Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
                    for (int x = 0; x < width; x++) {
                        for (int y = 0; y < height; y++) {
                            bmp.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                        }
                    }
                    ((ImageView) root.findViewById(R.id.admin_qrCode)).setImageBitmap(bmp);

                } catch (WriterException e) {
                    e.printStackTrace();
                }
                post("qrCode");
            }
        });
        final Button mkAdmin= root.findViewById(R.id.admin_mkadmin);
        mkAdmin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(Config.APIUrl)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                AdminFragmentPlaceholder adminFragmentPlaceholder;
                adminFragmentPlaceholder = retrofit.create(AdminFragmentPlaceholder.class);
                String token = getActivity().getIntent().getStringExtra("token");
                String myName = getActivity().getIntent().getStringExtra(LoginActivity.USERNAME);
                EditText usernameElement = (EditText) getActivity().findViewById(R.id.admin_username);
                String username = usernameElement.getText().toString();
                Call<MkAdminDTO> call = adminFragmentPlaceholder.mkAdmin(token, myName, username, true);

                call.enqueue(new Callback<MkAdminDTO>() {
                    @Override
                    public void onResponse(Call<MkAdminDTO> call, Response<MkAdminDTO> response) {
                        if (!response.isSuccessful()) {
                            Toast.makeText(getActivity(),Integer.toString(response.code()),Toast.LENGTH_SHORT).show();
                            return; }
                        MkAdminDTO mkAdminDTO = response.body();
                        if (mkAdminDTO==null) return;
                        if (mkAdminDTO.error.size()==0)
                            Toast.makeText(getActivity(), mkAdminDTO.getMessage(),Toast.LENGTH_SHORT).show();
                        else Toast.makeText(getActivity(), mkAdminDTO.getError().get(0),Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(Call<MkAdminDTO> call, Throwable t) {
                        Log.i("onFailure",t.getMessage());
                    }
                });
            }
        });
        return root;
    }

    void trace() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Config.APIUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        AdminFragmentPlaceholder adminFragmentPlaceholder;
        adminFragmentPlaceholder = retrofit.create(AdminFragmentPlaceholder.class);
        String token = getActivity().getIntent().getStringExtra("token");
        EditText usernameElement = (EditText) getActivity().findViewById(R.id.admin_username);
        String username = usernameElement.getText().toString();
        Call<TraceResponseDTO> call = adminFragmentPlaceholder.getContactTrace(token, username);

        call.enqueue(new Callback<TraceResponseDTO>() {
            @Override
            public void onResponse(Call<TraceResponseDTO> call, Response<TraceResponseDTO> response) {
                if(!response.isSuccessful()){
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Log.i("unsuccessfull",jObjError.getJSONObject("error").getString("message"));
                    } catch (Exception e) {
                        Log.i("unsuccessfull",e.getMessage());
                    }
                    return;
                }

                Log.i("onResponse",response.body().toString());
                Intent intent = new Intent(getActivity().getApplicationContext(), TraceActivity.class);
                intent.putExtra("data", response.body());
                startActivity(intent);
            }

            @Override
            public void onFailure(Call<TraceResponseDTO> call, Throwable t) {
                Log.i("onFailure",t.getMessage());
            }
        });
    }

   void post(String option){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Config.APIUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        AdminFragmentPlaceholder adminFragmentPlaceholder;
       adminFragmentPlaceholder = retrofit.create(AdminFragmentPlaceholder.class);
        Call<String> call = null;
        String token = getActivity().getIntent().getStringExtra("token");
        EditText usernameElement = (EditText) getActivity().findViewById(R.id.admin_username);
        String username = usernameElement.getText().toString();

        if (option.equals("delete"))
            call = adminFragmentPlaceholder.markDelete(token, username);
       if (option.equals("infected"))
           call = adminFragmentPlaceholder.markInfected(token, username);
       if (option.equals("healthy"))
           call = adminFragmentPlaceholder.markHealthy(token, username);
       if (option.equals("qrCode"))
           call = adminFragmentPlaceholder.addCode(code);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(!response.isSuccessful()){
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Log.i("unsuccessfull",jObjError.getJSONObject("error").getString("message"));
                    } catch (Exception e) {
                        Log.i("unsuccessfull",e.getMessage());
                    }
                    return;
                }
                String get = response.body();

                TextView errorText = (TextView)getActivity().findViewById(R.id.admin_responsText);
                errorText.setText(get);
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.i("onFailure",t.getMessage());
            }
        });
    }
}