package com.example.palapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;


public class Maps_Activity extends FragmentActivity implements OnMapReadyCallback {

    String sendMessage = "http://palaver.se.paluno.uni-due.de/api/message/send";
    Location currentLocation;
    FusedLocationProviderClient fusedLocationProviderClient;
    Button button;
    private  ArrayList<Transporter> transporters ;
    private static final int REQUEST_CODE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_maps_);
        button = findViewById(R.id.share_Location);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        transporters = new ArrayList<>();
        fetchLastLocation();
    }

    private void fetchLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    currentLocation = location;
                    Transporter transporter = new Transporter(String.valueOf(currentLocation.getLatitude()),String.valueOf(currentLocation.getLongitude()));
                    transporters.add(transporter);

                    Toast.makeText(getApplicationContext(), currentLocation.getLatitude() + "" + currentLocation.getLongitude(), Toast.LENGTH_SHORT).show();

                    SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.Maps_Activity2);
                    supportMapFragment.getMapAsync(Maps_Activity.this);
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case REQUEST_CODE:
                if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    fetchLastLocation();
                }
                break ;
        }

    }

    @Override
    public  void onMapReady(GoogleMap googleMap) {
        Log.d("this is onMapReady", "onSuccess: ");

        LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());

        MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("Here I am ");
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));

        googleMap.addMarker(markerOptions);
    }
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void sendLocationClicked(View view ){
        String sender = getIntent().getStringExtra("Sender");
        String PasswordSender = getIntent().getStringExtra("password");
        String recipient = getIntent().getStringExtra("Recipient");
        String mime = "text/plain";
        String toSendMessage = buildSendLocationMessage() ;

        HashMap<String,String> paramsMessage = new HashMap<>();
        paramsMessage.put("Username" , sender);
        paramsMessage.put("Password" , PasswordSender);
        paramsMessage.put("Recipient" , recipient);
        paramsMessage.put("Mimetype" , mime);
        paramsMessage.put("Data", toSendMessage);
        sendLocationRequest(paramsMessage);
    }

    private String buildSendLocationMessage() {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("Click Here for Location : ");

        stringBuilder.append(transporters.get(0).getLatitude());
        stringBuilder.append(" ");
        stringBuilder.append(transporters.get(0).getlongitude());
        stringBuilder.append(" ");
        stringBuilder.append("1");
        return stringBuilder.toString();
    }
    public void sendLocationRequest(HashMap<String, String> params){
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest postRequest = new JsonObjectRequest(sendMessage,
                new JSONObject(params),
                new Response.Listener<JSONObject>() {



                    @Override
                    public void onResponse(JSONObject response) {
                        Toast toast = Toast.makeText(getApplicationContext(), response.toString(), Toast.LENGTH_SHORT);
                        toast.show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast toast = Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT);
                        toast.show();
                    }
                });
        queue.add(postRequest);
    }
}
