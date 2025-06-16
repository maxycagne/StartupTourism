package com.example.startuptourism.Activities.Owner.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.startuptourism.Activities.Both.Profile;
import com.example.startuptourism.Activities.Owner.AccommodationForm;
import com.example.startuptourism.Activities.Owner.PowAccommodationDetails;
import com.example.startuptourism.Activities.Owner.PropertyOwnerHome;
import com.example.startuptourism.Adapter.OwnerAccAdapter;
import com.example.startuptourism.Database.RoomDb.Entity.Accommodation;
import com.example.startuptourism.databinding.FragmentPowAccsBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class PowAccsFragment extends Fragment implements OwnerAccAdapter.AccomClick {
    private FragmentPowAccsBinding root;
    private PropertyOwnerHome a;
    private OwnerAccAdapter ownerAccAdapter;
    private ValueEventListener accommodationListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = FragmentPowAccsBinding.inflate(inflater, container, false);
        a = (PropertyOwnerHome) getActivity();
        root.rvAccommodationList.setAdapter(ownerAccAdapter = new OwnerAccAdapter(a, new ArrayList<>(), this));
        root.btnAddAcc.setOnClickListener(view -> {
            startActivity(new Intent(a, AccommodationForm.class)
                    .putExtra("mode", "add")
                    .putExtra("user", a.user));
        });
        root.btnProfile.setOnClickListener(view -> {
            startActivity(new Intent(a, Profile.class).putExtra("user", a.user));
        });
        setListener();
        return root.getRoot();
    }

    private void setListener() {
        accommodationListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Accommodation> accommodationList = new ArrayList<>();
                for (DataSnapshot accom : snapshot.getChildren()) {
                    Accommodation accommodation = accom.getValue(Accommodation.class);
                    accommodation.setAccomId(accom.getKey());
                    accommodationList.add(accommodation);
                }
                ownerAccAdapter.refreshList(accommodationList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        a.dbHelper.getRealtime().getReference().child("accommodation").child(a.user.getUsername()).addValueEventListener(accommodationListener);
    }

    @Override
    public void onResume() {
        a.dbHelper.getRealtime().getReference().child("accommodation").child(a.user.getUsername()).addValueEventListener(accommodationListener);
        super.onResume();
    }

    @Override
    public void onClick(Accommodation accomUserImages) {
        startActivity(new Intent(a, PowAccommodationDetails.class)
                .putExtra("accom", accomUserImages)
                .putExtra("user", a.user));
    }

    @Override
    public void onPause() {
        try {
            a.dbHelper.getRealtime().getReference().child("accommodation").child(a.user.getUsername()).removeEventListener(accommodationListener);
        } catch (Exception e) {

        }
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        try {
            a.dbHelper.getRealtime().getReference().child("accommodation").child(a.user.getUsername()).removeEventListener(accommodationListener);
        } catch (Exception e) {

        }
        super.onDestroyView();
    }
}