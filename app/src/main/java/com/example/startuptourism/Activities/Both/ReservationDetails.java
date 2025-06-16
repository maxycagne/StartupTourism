package com.example.startuptourism.Activities.Both;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.startuptourism.Adapter.AccImgAdapter;
import com.example.startuptourism.Database.RoomDb.DbHelper;
import com.example.startuptourism.Database.RoomDb.Entity.Accommodation;
import com.example.startuptourism.Database.RoomDb.Entity.Reservation;
import com.example.startuptourism.Database.RoomDb.Entity.User;
import com.example.startuptourism.Helper.DateHelp;
import com.example.startuptourism.Helper.DialogHelp;
import com.example.startuptourism.Helper.TextHelp;
import com.example.startuptourism.Helper.Watcher.Required;
import com.example.startuptourism.R;
import com.example.startuptourism.databinding.ActivityReservationDetailsBinding;
import com.example.startuptourism.databinding.DialogImageBinding;
import com.example.startuptourism.databinding.DialogReasonBinding;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ReservationDetails extends AppCompatActivity implements OnMapReadyCallback, AccImgAdapter.ImageClick {

    private ActivityReservationDetailsBinding root;
    private Reservation reservation;
    private User user;
    private Accommodation accommodation;
    private User otherUser;
    private DbHelper dbHelper;
    private Handler handler;
    private GoogleMap mMap;
    private ValueEventListener reservationListener, userListener, accommodationListener;
    private AccImgAdapter accImgAdapter;

    private void setDb() {
        dbHelper = new DbHelper(this);
        handler = new Handler(Looper.getMainLooper());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        root = ActivityReservationDetailsBinding.inflate(getLayoutInflater());
        setContentView(root.getRoot());
        setDb();
        reservation = (Reservation) getIntent().getSerializableExtra("reservation");
        user = (User) getIntent().getSerializableExtra("user");
        root.btnBack.setOnClickListener(view -> finish());
        root.rvImages.setAdapter(accImgAdapter = new AccImgAdapter(this, new ArrayList<>(), this));
        root.btnAccept.setOnClickListener(view -> accept());
        root.btnReject.setOnClickListener(view -> reject());
        root.btnCancel.setOnClickListener(view -> cancel());
        if (user.getUserType().equalsIgnoreCase("Owner")) {
            root.layoutOwner.setVisibility(View.GONE);
            root.layoutRequestor.setVisibility(View.VISIBLE);
        } else if (user.getUserType().equalsIgnoreCase("Tourist")) {
            root.layoutOwner.setVisibility(View.VISIBLE);
            root.layoutRequestor.setVisibility(View.GONE);
        }
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    private void cancel() {
        DialogReasonBinding binding = DialogReasonBinding.inflate(getLayoutInflater());
        AlertDialog dialog = DialogHelp.createDialog(this, "Cancellation Confirmation", null)
                .setNegativeButton("GO BACK", null)
                .setPositiveButton("PROCEED", null)
                .setView(binding.getRoot())
                .create();
        dialog.show();
        binding.edtReason.addTextChangedListener(new Required(binding.edtReason, binding.layoutEdtReason));
        binding.edtReason.setOnFocusChangeListener(new Required(binding.edtReason, binding.layoutEdtReason));
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(view -> {
            if (TextHelp.isEmpty(binding.edtReason)) {
                Toast.makeText(this, "Please specify reason for cancellation", Toast.LENGTH_SHORT).show();
                return;
            }
            Reservation reservation1 = reservation;
            reservation1.setReserveStatus("Cancelled");
            reservation1.setReserveReason(TextHelp.getText(binding.edtReason));
            dbHelper.getReference().child("reservations").child(reservation.getReserveId())
                    .setValue(reservation1).addOnSuccessListener(unused -> {
                        dialog.dismiss();
                        Toast.makeText(this, "Reservation Cancelled", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Error occurred: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });
    }

    private void reject() {
        DialogReasonBinding binding = DialogReasonBinding.inflate(getLayoutInflater());
        AlertDialog dialog = DialogHelp.createDialog(this, "Rejection Reservation", null)
                .setNegativeButton("CANCEL", null)
                .setPositiveButton("PROCEED", null)
                .setView(binding.getRoot())
                .create();
        dialog.show();
        binding.edtReason.addTextChangedListener(new Required(binding.edtReason, binding.layoutEdtReason));
        binding.edtReason.setOnFocusChangeListener(new Required(binding.edtReason, binding.layoutEdtReason));
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(view -> {
            if (TextHelp.isEmpty(binding.edtReason)) {
                Toast.makeText(this, "Please specify reason for rejecting reservation", Toast.LENGTH_SHORT).show();
                return;
            }
            Reservation reservation1 = reservation;
            reservation1.setReserveStatus("Rejected");
            reservation1.setReserveReason(TextHelp.getText(binding.edtReason));
            dbHelper.getReference().child("reservations").child(reservation.getReserveId())
                    .setValue(reservation1).addOnSuccessListener(unused -> {
                        Toast.makeText(this, "Reservation Cancelled", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Error occurred: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });
    }

    private void accept() {
        DialogHelp.createDialog(this, "Accepting Reservation", "Proceeding will notify client that his/her booking was accepted")
                .setNegativeButton("CANCEL", null)
                .setPositiveButton("PROCEED", ((dialogInterface, i) -> {
                    Reservation reservation1 = reservation;
                    reservation1.setReserveStatus("Accepted");
                    dbHelper.getReference().child("reservations").child(reservation.getReserveId())
                            .setValue(reservation1).addOnSuccessListener(unused -> {
                                Toast.makeText(this, "Reservation Accepted", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(this, "Error occurred: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                }))
                .create().show();
    }

    private void setListeners() {
        reservationListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                reservation = snapshot.getValue(Reservation.class);
                setReservationDetails();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        dbHelper.getReference().child("reservations").child(reservation.getReserveId()).addValueEventListener(reservationListener);

        if (user.getUserType().equalsIgnoreCase("owner")) {
            userListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    otherUser = snapshot.getValue(User.class);
                    displaUserTou();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            };
            dbHelper.getReference().child("users").child(reservation.getReserveUserTou()).addValueEventListener(userListener);
        } else {
            userListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    otherUser = snapshot.getValue(User.class);
                    displaUserPow();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            };
            dbHelper.getReference().child("users").child(reservation.getReserveUserPow()).addValueEventListener(userListener);
        }
        accommodationListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                accommodation = snapshot.getValue(Accommodation.class);
                displayAccomDetails();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        dbHelper.getReference().child("accommodation").child(reservation.getReserveUserPow()).child(reservation.getReserveAccomId()).addValueEventListener(accommodationListener);
    }

    private void displaUserPow() {
        Picasso.get().load(otherUser.getUserImg()).resize(150, 150).centerCrop().placeholder(R.drawable.ic_account).into(root.imgPostedByProfile);
        root.txtOwnerName.setText(otherUser.getUserFirstName().concat(" ".concat(otherUser.getUserLastName())));
        root.txtOwnerContactNum.setText("+" + otherUser.getUserCpNum());
        root.txtOwnerEmail.setText(otherUser.getUserEmail());
        root.txtOwnerAddress.setText(otherUser.getUserAddress());
    }

    private void displaUserTou() {
        Picasso.get().load(otherUser.getUserImg()).resize(150, 150).centerCrop().placeholder(R.drawable.ic_account).into(root.imgRequestedByProfile);
        root.txtRequestorName.setText(otherUser.getUserFirstName().concat(" ".concat(otherUser.getUserLastName())));
        root.txtRequestorContactNum.setText("+" + otherUser.getUserCpNum());
        root.txtRequestorEmail.setText(otherUser.getUserEmail());
        root.txtRequestorAddress.setText(otherUser.getUserAddress());
    }

    private void displayAccomDetails() {
        mMap.addMarker(new MarkerOptions().position(new LatLng(accommodation.getAccomLat(), accommodation.getAccomLng())));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(18));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(accommodation.getAccomLat(), accommodation.getAccomLng())));
        root.txtName.setText(accommodation.getAccomName());
        root.txtAddress.setText(accommodation.getAccomAddress());
        root.txtType.setText(accommodation.getAccomType());
        root.txtMaximumCapacity.setText(String.valueOf(accommodation.getAccomPax()));
        root.txtHourlyRate.setText("₱" + String.format(Locale.getDefault(), "%.2f", accommodation.getAccomHourRate()));
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

    private void setReservationDetails() {
        root.txtNumberOfPerson.setText(reservation.getReserveNumPerson() + " Person");
        root.txtCheckInDateAndTime.setText(DateHelp.convertToString(reservation.getCheckInDate(), DateHelp.dateTimeFormat));
        root.txtCheckOutDateAndTime.setText(DateHelp.convertToString(reservation.getCheckOutDate(), DateHelp.dateTimeFormat));
        root.txtAmountDue.setText("₱".concat(String.format(Locale.getDefault(), "%.2f", reservation.getReservationAmt())));
        root.txtStatus.setText(reservation.getReserveStatus());
        if (reservation.getReserveStatus().equalsIgnoreCase("Accepted") ||
                reservation.getReserveStatus().equalsIgnoreCase("Cancelled") ||
                reservation.getReserveStatus().equalsIgnoreCase("Rejected")) {
            root.btnAccept.setVisibility(View.GONE);
            root.btnReject.setVisibility(View.GONE);
            root.btnCancel.setVisibility(View.GONE);
            if (reservation.getReserveStatus().equalsIgnoreCase("Cancelled") ||
                    reservation.getReserveStatus().equalsIgnoreCase("Rejected")) {
                String reason = reservation.getReserveStatus().equalsIgnoreCase("Cancelled") ? "Cancellation" : "Rejection";
                root.layoutReason.setVisibility(View.VISIBLE);
                root.txtReasonFor.setText("Reason for " + reason + ":");
                root.txtReason.setText(reservation.getReserveReason());
            }
        } else {
            root.layoutReason.setVisibility(View.GONE);
            if (user.getUserType().equalsIgnoreCase("Owner")) {
                root.btnCancel.setVisibility(View.GONE);
                root.btnAccept.setVisibility(View.VISIBLE);
                root.btnReject.setVisibility(View.VISIBLE);
            } else if (user.getUserType().equalsIgnoreCase("Tourist")) {
                root.btnAccept.setVisibility(View.GONE);
                root.btnReject.setVisibility(View.GONE);
                root.btnCancel.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        setListeners();
    }

    @Override
    protected void onDestroy() {
        try {
            dbHelper.getReference().child("reservations").child(reservation.getReserveId()).removeEventListener(reservationListener);
        } catch (Exception e) {

        }
        try {
            dbHelper.getReference().child("users").child(reservation.getReserveUserTou()).removeEventListener(userListener);
        } catch (Exception e) {

        }
        try {
            dbHelper.getReference().child("users").child(reservation.getReserveUserPow()).removeEventListener(userListener);
        } catch (Exception e) {

        }
        try {
            dbHelper.getReference().child("accommodation").child(reservation.getReserveAccomId()).removeEventListener(accommodationListener);
        } catch (Exception e) {

        }
        super.onDestroy();
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
}