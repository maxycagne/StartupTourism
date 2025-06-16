package com.example.startuptourism.Activities.Owner;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.startuptourism.Activities.Both.ReservationDetails;
import com.example.startuptourism.Adapter.AccImgAdapter;
import com.example.startuptourism.Adapter.OwnerResAccAdapter;
import com.example.startuptourism.Database.RoomDb.DbHelper;
import com.example.startuptourism.Database.RoomDb.Entity.Accommodation;
import com.example.startuptourism.Database.RoomDb.Entity.Reservation;
import com.example.startuptourism.Database.RoomDb.Entity.User;
import com.example.startuptourism.Helper.DialogHelp;
import com.example.startuptourism.R;
import com.example.startuptourism.databinding.ActivityPowAccommodationDetailsBinding;
import com.example.startuptourism.databinding.DialogImageBinding;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class PowAccommodationDetails extends AppCompatActivity implements OnMapReadyCallback, AccImgAdapter.ImageClick, OwnerResAccAdapter.ReservationClick {

    private ActivityPowAccommodationDetailsBinding root;
    private Accommodation accommodation;
    private GoogleMap mMap;
    private DbHelper dbHelper;
    private Handler handler;
    private User user;
    private AccImgAdapter accImgAdapter;
    private OwnerResAccAdapter ownerResAccAdapter;

    private void setDb() {
        dbHelper = new DbHelper(this);
        handler = new Handler(Looper.getMainLooper());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        root = ActivityPowAccommodationDetailsBinding.inflate(getLayoutInflater());
        setContentView(root.getRoot());
        setDb();
        user = (User) getIntent().getSerializableExtra("user");
        accommodation = (Accommodation) getIntent().getSerializableExtra("accom");
        root.btnBack.setOnClickListener(view -> finish());
        root.rvImages.setAdapter(accImgAdapter = new AccImgAdapter(this, new ArrayList<>(), this));
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null)
            mapFragment.getMapAsync(this);
        root.btnEditDetails.setOnClickListener(view ->
                startActivity(new Intent(this, AccommodationForm.class)
                        .putExtra("user", user)
                        .putExtra("mode", "edit")
                        .putExtra("accom", accommodation)));
        root.rvReservations.setAdapter(ownerResAccAdapter = new OwnerResAccAdapter(this, new ArrayList<>(), this, 0));
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        retrieveData();
    }

    @Override
    protected void onResume() {
        retrieveData();
        super.onResume();
    }

    private void retrieveData() {
        dbHelper.getReference().child("accommodation").child(accommodation.getAccomUsername()).child(accommodation.getAccomId())
                .get().addOnSuccessListener(dataSnapshot -> {
                    if (dataSnapshot != null) {
                        accommodation = dataSnapshot.getValue(Accommodation.class);
                        if (accommodation != null) {
                            accommodation.setAccomId(Objects.requireNonNull(dataSnapshot.getKey()));
                            dbHelper.getReference().child("users").child(accommodation.getAccomUsername())
                                    .get().addOnSuccessListener(dataSnapshot1 -> {
                                        if (dataSnapshot1 != null) {
                                            displayDetails();
                                        }
                                    });
                        }
                    }
                });
        dbHelper.getReference().child("reservations").get().addOnSuccessListener(dataSnapshot -> {
            List<Reservation> reservationList = new ArrayList<>();
            if (dataSnapshot != null) {
                for (DataSnapshot res : dataSnapshot.getChildren()) {
                    Reservation reservation = res.getValue(Reservation.class);
                    if (reservation != null) {
                        reservation.setReserveId(Objects.requireNonNull(res.getKey()));
                        if (reservation.getReserveAccomId().equals(accommodation.getAccomId()))
                            reservationList.add(reservation);
                    }
                }
            }
            ownerResAccAdapter.refreshList(reservationList);
            if (reservationList.size() == 0) {
                root.txtNoReservations.setVisibility(View.VISIBLE);
            } else
                root.txtNoReservations.setVisibility(View.GONE);
        });
    }

    private void displayDetails() {
        root.txtName.setText(accommodation.getAccomName());
        root.txtAddress.setText(accommodation.getAccomAddress());
        root.txtDesc.setText(accommodation.getAccomDesc());
        root.txtType.setText(accommodation.getAccomType());
        root.txtMaximumCapacity.setText(String.format(Locale.getDefault(), "%s", accommodation.getAccomPax()).concat(" person(s)"));
        root.txtHourlyRate.setText("₱".concat(String.format(Locale.getDefault(), "%.2f", accommodation.getAccomHourRate())));


        mMap.addMarker(new MarkerOptions().position(new LatLng(accommodation.getAccomLat(), accommodation.getAccomLng())));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(18));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(accommodation.getAccomLat(), accommodation.getAccomLng())));

        List<Uri> imagesList = new ArrayList<>();
        if (accommodation.getImgPath1() != null) {
            imagesList.add(Uri.parse(accommodation.getImgPath1()));
            if (accommodation.getImgPath2() != null) {
                imagesList.add(Uri.parse(accommodation.getImgPath2()));
                if (accommodation.getImgPath3() != null) {
                    imagesList.add(Uri.parse(accommodation.getImgPath3()));
                    if (accommodation.getImgPath4() != null) {
                        imagesList.add(Uri.parse(accommodation.getImgPath4()));
                        if (accommodation.getImgPath5() != null) {
                            imagesList.add(Uri.parse(accommodation.getImgPath5()));
                        }
                    }
                }
            }
        }
        accImgAdapter.refreshList(imagesList);
    }

    @Override
    public void onClick(Uri uri) {
        DialogImageBinding root = DialogImageBinding.inflate(getLayoutInflater());
        Picasso.get().load(uri).resize(500, 300).centerCrop().into(root.img);
        DialogHelp.createDialog(this, "", null)
                .setCustomTitle(null)
                .setView(root.getRoot())
                .create().show();
    }

    @Override
    public void onClick(Reservation reservation) {
        startActivity(new Intent(this, ReservationDetails.class)
                .putExtra("reservation", reservation)
                .putExtra("user", user));
    }
}