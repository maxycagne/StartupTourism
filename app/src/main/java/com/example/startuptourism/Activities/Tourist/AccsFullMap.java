package com.example.startuptourism.Activities.Tourist;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.startuptourism.Database.RoomDb.DbHelper;
import com.example.startuptourism.Database.RoomDb.Entity.Accommodation;
import com.example.startuptourism.Database.RoomDb.Entity.User;
import com.example.startuptourism.R;
import com.example.startuptourism.databinding.ActivityAccsFullMapBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.List;

public class AccsFullMap extends AppCompatActivity implements OnMapReadyCallback {

    private ActivityAccsFullMapBinding root;
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private DbHelper dbHelper;
    private Handler handler;
    private ActivityResultLauncher<String[]> locationPermissionRequest;
    private Accommodation accommSelected;
    private List<Accommodation> accommodationList;
    private User user;

    private void setDb() {
        dbHelper = new DbHelper(this);
        handler = new Handler(Looper.getMainLooper());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        root = ActivityAccsFullMapBinding.inflate(getLayoutInflater());
        setContentView(root.getRoot());
        setDb();
        user = (User) getIntent().getSerializableExtra("user");
        root.btnBack.setOnClickListener(view -> finish());
        root.btnViewDetails.setOnClickListener(view ->
                startActivity(new Intent(this, TouAccommodationDetails.class)
                        .putExtra("accom", accommSelected)
                        .putExtra("user", user)));
        setLocationRequest();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        root.cardview.setVisibility(View.GONE);
    }

    private void retrieveData() {
        accommodationList = new ArrayList<>();
        dbHelper.getReference().child("accommodation")
                .get().addOnSuccessListener(dataSnapshot -> {
                    mMap.clear();
                    for (DataSnapshot userAccom : dataSnapshot.getChildren()) {
                        for (DataSnapshot accom : userAccom.getChildren()) {
                            Accommodation accommodation = accom.getValue(Accommodation.class);
                            if (accommodation.getAccomVerified() == -1)
                                continue;
                            accommodation.setAccomId(accom.getKey());
                            accommodationList.add(accommodation);
                            LatLng accomLatLng = new LatLng(accommodation.getAccomLat(), accommodation.getAccomLng());
                            mMap.addMarker(new MarkerOptions().position(accomLatLng).snippet(String.valueOf(accommodationList.size())));
                        }
                    }
                });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        getCurrentLoc();
        retrieveData();
        mMap.setOnMarkerClickListener(marker -> {
            accommSelected = accommodationList.get(Integer.parseInt(marker.getSnippet()) - 1);
            root.cardview.setVisibility(View.VISIBLE);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
            mMap.moveCamera(CameraUpdateFactory.zoomTo(22));
            root.txtName.setText(accommSelected.getAccomName());
            root.txtAddress.setText(accommSelected.getAccomAddress());
            if (accommSelected.getAccomVerified() == 0)
                root.txtVerified.setVisibility(View.GONE);
            else {
                root.txtVerified.setVisibility(View.VISIBLE);
                if (accommSelected.getAccomVerified() == 1)
                    root.txtVerified.setText("Verified");
            }
            return false;
        });
    }

    private void getCurrentLoc() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            locationPermissionRequest.launch(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            });
            return;
        }

        fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
            LatLng currentLoc = new LatLng(15.4861, 120.5862);
            if (location != null) {
                currentLoc = new LatLng(location.getLatitude(), location.getLongitude());
            }
            mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLoc));
            mMap.moveCamera(CameraUpdateFactory.zoomTo(20));
            // TODO: 10/05/2024 fix current location


        });
    }

    private void setLocationRequest() {
        locationPermissionRequest = registerForActivityResult(new ActivityResultContracts
                        .RequestMultiplePermissions(), result -> {
                    Boolean fineLocationGranted = result.getOrDefault(
                            Manifest.permission.ACCESS_FINE_LOCATION, false);
                    Boolean coarseLocationGranted = result.getOrDefault(
                            Manifest.permission.ACCESS_COARSE_LOCATION, false);
                    if (fineLocationGranted != null && fineLocationGranted) {
                        getCurrentLoc();
                    } else if (coarseLocationGranted != null && coarseLocationGranted) {
                        getCurrentLoc();
                    } else {
                        Toast.makeText(this, "Please allow location permission for more accurate reservation", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    @Override
    protected void onResume() {
        retrieveData();
        super.onResume();
    }
}