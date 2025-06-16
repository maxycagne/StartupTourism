package com.example.startuptourism.Activities.Tourist;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Pair;

import com.example.startuptourism.Adapter.AccImgAdapter;
import com.example.startuptourism.Database.RoomDb.DbHelper;
import com.example.startuptourism.Database.RoomDb.Entity.Accommodation;
import com.example.startuptourism.Database.RoomDb.Entity.Reservation;
import com.example.startuptourism.Database.RoomDb.Entity.User;
import com.example.startuptourism.Helper.DateHelp;
import com.example.startuptourism.Helper.DialogHelp;
import com.example.startuptourism.Helper.TextHelp;
import com.example.startuptourism.R;
import com.example.startuptourism.databinding.ActivityTouAccommodationDetailsBinding;
import com.example.startuptourism.databinding.DialogImageBinding;
import com.example.startuptourism.databinding.DialogLoadingBinding;
import com.example.startuptourism.databinding.DialogNumpickerBinding;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.slider.LabelFormatter;
import com.google.firebase.database.DatabaseReference;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TouAccommodationDetails extends AppCompatActivity implements OnMapReadyCallback, AccImgAdapter.ImageClick {

    private ActivityTouAccommodationDetailsBinding root;
    private User user;
    private Accommodation accommodation;
    private User owner;
    private DbHelper dbHelper;
    private Handler handler;
    private GoogleMap mMap;
    private AccImgAdapter accImgAdapter;

    private void setDb() {
        dbHelper = new DbHelper(this);
        handler = new Handler(Looper.getMainLooper());
    }

    private void setBackPressed() {
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                back();
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);
    }

    private void back() {
        // TODO: 10/05/2024 validate back
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        root = ActivityTouAccommodationDetailsBinding.inflate(getLayoutInflater());
        setContentView(root.getRoot());
        setDb();
        setBackPressed();
        user = (User) getIntent().getSerializableExtra("user");
        accommodation = (Accommodation) getIntent().getSerializableExtra("accom");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        root.rvImages.setAdapter(accImgAdapter = new AccImgAdapter(this, new ArrayList<>(), this));
        root.btnBack.setOnClickListener(view -> back());
        root.btnCancel.setOnClickListener(view -> back());
        root.btnReserve.setOnClickListener(view -> validate());
        root.edtCheckInDate.setText(DateHelp.convertToString(DateHelp.getCurrent(), DateHelp.dateFormat));
        root.edtCheckOutDate.setText(DateHelp.convertToString(DateHelp.getTomorrow(), DateHelp.dateFormat));
        root.sliderCheckInTime.setValue(DateHelp.getCurrent(Calendar.HOUR));
        root.sliderCheckOutTime.setValue(DateHelp.getCurrent(Calendar.HOUR));
        root.txtCheckInTime.setText(DateHelp.convertToTime(DateHelp.getCurrent(Calendar.HOUR)));
        root.txtCheckOutTime.setText(DateHelp.convertToTime(DateHelp.getCurrent(Calendar.HOUR)));
        root.sliderCheckOutTime.setLabelBehavior(LabelFormatter.LABEL_GONE);
        root.sliderCheckInTime.setLabelBehavior(LabelFormatter.LABEL_GONE);
        root.sliderCheckInTime.addOnChangeListener((slider, value, fromUser) -> {
            root.txtCheckInTime.setText(DateHelp.convertToTime((int) value));
            computeAmt();
        });
        root.sliderCheckOutTime.addOnChangeListener(((slider, value, fromUser) -> {
            root.txtCheckOutTime.setText(DateHelp.convertToTime((int) value));
            computeAmt();
        }));
        root.edtCheckInDate.setOnFocusChangeListener(((view, b) -> {
            if (b)
                dateRangerDialog();
        }));
        root.edtCheckOutDate.setOnFocusChangeListener(((view, b) -> {
            if (b)
                dateRangerDialog();
        }));
        root.edtNumberOfPerson.setText("1");
        root.edtNumberOfPerson.setOnFocusChangeListener(((view, b) -> {
            if (b) {
                DialogNumpickerBinding binding = DialogNumpickerBinding.inflate(getLayoutInflater());
                AlertDialog dialog = DialogHelp.createDialog(this, "Select number of person", null)
                        .setPositiveButton("SET", ((dialogInterface, i) -> {
                            root.edtNumberOfPerson.setText(String.valueOf(binding.numpicker.getValue()));
                        }))
                        .setView(binding.getRoot())
                        .setOnDismissListener(dialogInterface -> {
                            root.edtNumberOfPerson.clearFocus();
                            root.btnReserve.hasFocus();
                        })
                        .create();
                dialog.show();
                binding.numpicker.setValue(Integer.parseInt(TextHelp.getText(root.edtNumberOfPerson)));
                binding.numpicker.setMinValue(1);
                binding.numpicker.setMaxValue(accommodation.getAccomPax());
            }
        }));

    }

    private void computeAmt() {
        try {
            Date checkIn = DateHelp.convertToDate((TextHelp.getText(root.edtCheckInDate) + " at " + TextHelp.getText(root.txtCheckInTime)), DateHelp.dateTimeFormat);
            Date checkOut = DateHelp.convertToDate((TextHelp.getText(root.edtCheckOutDate) + " at " + TextHelp.getText(root.txtCheckOutTime)), DateHelp.dateTimeFormat);
            if (checkIn.getTime() >= checkOut.getTime()) {
                root.txtAmountDue.setText("₱ 0.00");
                return;
            }
            int hours = 0;
            double amount = 0;
            int days = DateHelp.days(checkIn, checkOut);
            if (days == 0) {
                hours = DateHelp.getValueHour(checkOut) - DateHelp.getValueHour(checkIn);
            } else {
                if (DateHelp.getValueHour(checkOut) == DateHelp.getValueHour(checkIn))
                    hours = ((days * 24));
                else if (DateHelp.getValueHour(checkOut) > DateHelp.getValueHour(checkIn))
                    hours = ((days * 24) + (DateHelp.getValueHour(checkOut) - DateHelp.getValueHour(checkIn)));
                else
                    hours = ((days) * 24) - (DateHelp.getValueHour(checkIn) - DateHelp.getValueHour(checkOut));
            }
            amount = hours * accommodation.getAccomHourRate();
            root.txtAmountDue.setText("₱ ".concat(String.format(Locale.getDefault(), "%.2f", amount)));
        } catch (ParseException e) {

        }
    }

    private void dateRangerDialog() {
        MaterialDatePicker<Pair<Long, Long>> materialDatePicker = null;
        try {
            materialDatePicker = MaterialDatePicker.Builder.dateRangePicker().setSelection(
                            new Pair<>(
                                    DateHelp.convertToDate(TextHelp.getText(root.edtCheckInDate), DateHelp.dateFormat).getTime(),
                                    DateHelp.convertToDate(TextHelp.getText(root.edtCheckOutDate), DateHelp.dateFormat).getTime()))
                    .build();
            materialDatePicker.addOnPositiveButtonClickListener(selection -> {
                if (selection.first < DateHelp.getCurrentDate().getTime()) {
                    Toast.makeText(this, "Invalid date range", Toast.LENGTH_SHORT).show();
                    dateRangerDialog();
                } else {
                    root.edtCheckInDate.setText(DateHelp.convertToString(selection.first, DateHelp.dateFormat));
                    root.edtCheckOutDate.setText(DateHelp.convertToString(selection.second, DateHelp.dateFormat));
                    computeAmt();
                }
            });
            materialDatePicker.addOnDismissListener((dialogInterface -> {
                root.edtCheckInDate.clearFocus();
                root.edtCheckOutDate.clearFocus();
            }));
            materialDatePicker.show(getSupportFragmentManager(), "dialog");
        } catch (ParseException e) {

        }
    }

    private void validate() {
        try {
            Date checkIn = DateHelp.convertToDate((TextHelp.getText(root.edtCheckInDate) + " at " + TextHelp.getText(root.txtCheckInTime)), DateHelp.dateTimeFormat);
            Date checkOut = DateHelp.convertToDate((TextHelp.getText(root.edtCheckOutDate) + " at " + TextHelp.getText(root.txtCheckOutTime)), DateHelp.dateTimeFormat);
            if (checkIn.getTime() <= DateHelp.getCurrent().getTime()) {
                Toast.makeText(this, "Check-in date and time must not be earlier than now", Toast.LENGTH_SHORT).show();
                return;
            }
            if (checkIn.getTime() >= checkOut.getTime()) {
                Toast.makeText(this, "Invalid check-in and check-out date and time", Toast.LENGTH_SHORT).show();
                return;
            }
            reserve();
        } catch (ParseException e) {

        }
    }

    private void reserve() {
        DialogHelp.createDialog(this, "Reservation Confirmation", "Proceeding will submit your reservation to accommodation owner for approval. He/She will be able to view your contact information")
                .setNegativeButton("CANCEL", null)
                .setPositiveButton("OKAY", ((dialogInterface, i) -> {
                    addToDb();
                }))
                .create().show();
    }

    private void addToDb() {
        DialogLoadingBinding binding = DialogLoadingBinding.inflate(getLayoutInflater());
        AlertDialog dialog = DialogHelp.createDialog(this, "Submitting your reservation request", null)
                .setCancelable(false)
                .setView(binding.getRoot())
                .create();
        dialog.show();
        // TODO: 10/05/2024 change waiting text
        binding.txtMessage.setText("Please wait...");
        Reservation reservation = getInputReserve();
        DatabaseReference reference = dbHelper.getReference().child("reservations").push();
        reservation.setReserveId(reference.getKey());
        reference.setValue(reservation).addOnSuccessListener(unused -> {
            dialog.dismiss();
            DialogHelp.createDialog(this, "Reservation request submitted", "Your request was successfully submitted. Please wait for the accommodation's owner's approval.")
                    .setPositiveButton("OKAY", null)
                    .setOnDismissListener((dialogInterface -> startActivity(new Intent(this, TouristHome.class)
                            .putExtra("user", user))))
                    .create().show();
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Error occurred: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private Reservation getInputReserve() {
        Reservation reservation = new Reservation();
        reservation.setReserveAccomId(accommodation.getAccomId());
        reservation.setReserveClientName(user.getUserFirstName() + " " + user.getUserLastName());
        reservation.setReserveAccomPlace(accommodation.getAccomName() + ", " + accommodation.getAccomAddress());
        reservation.setReserveStatus("Pending");
        reservation.setReserveUserTou(user.getUsername());
        reservation.setReserveUserPow(accommodation.getAccomUsername());
        reservation.setReserveNumPerson(Integer.parseInt(TextHelp.getText(root.edtNumberOfPerson)));
        reservation.setReservationAmt(Double.parseDouble(TextHelp.getText(root.txtAmountDue).substring(2)));
        try {
            reservation.setCheckInDate(DateHelp.convertToDate(
                    (TextHelp.getText(root.edtCheckInDate) + " at " + TextHelp.getText(root.txtCheckInTime)),
                    DateHelp.dateTimeFormat
            ).getTime());
            reservation.setCheckOutDate(DateHelp.convertToDate(
                    (TextHelp.getText(root.edtCheckOutDate) + " at " + TextHelp.getText(root.txtCheckOutTime)),
                    DateHelp.dateTimeFormat
            ).getTime());
            reservation.setReservationDate(DateHelp.getCurrent().getTime());
        } catch (ParseException e) {

        }
        return reservation;
    }


    private void retrieveData() {
        dbHelper.getReference().child("accommodation").child(accommodation.getAccomUsername()).child(accommodation.getAccomId())
                .get().addOnSuccessListener(dataSnapshot -> {
                    if (dataSnapshot != null) {
                        accommodation = dataSnapshot.getValue(Accommodation.class);
                        accommodation.setAccomId(dataSnapshot.getKey());
                        dbHelper.getReference().child("users").child(accommodation.getAccomUsername())
                                .get().addOnSuccessListener(dataSnapshot1 -> {
                                    if (dataSnapshot1 != null) {
                                        owner = dataSnapshot1.getValue(User.class);
                                        displayDetails();
                                    }
                                });
                    }
                });
    }

    private void displayDetails() {
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
        root.txtName.setText(accommodation.getAccomName());
        root.txtAddress.setText(accommodation.getAccomAddress());
        root.txtDesc.setText(accommodation.getAccomDesc());
        root.txtType.setText(accommodation.getAccomType());
        root.txtMaximumCapacity.setText(String.valueOf(accommodation.getAccomPax()));
        root.txtHourlyRate.setText("₱" + String.format(Locale.getDefault(), "%.2f", accommodation.getAccomHourRate()));
        root.txtOwnerName.setText(owner.getUserFirstName().concat(" ".concat(owner.getUserLastName())));
        root.txtOwnerContactNum.setText("+" + owner.getUserCpNum());
        root.txtOwnerEmail.setText(owner.getUserEmail());
        root.txtOwnerAddress.setText(owner.getUserAddress());
        computeAmt();
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        retrieveData();
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