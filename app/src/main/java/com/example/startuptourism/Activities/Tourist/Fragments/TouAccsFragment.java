package com.example.startuptourism.Activities.Tourist.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.startuptourism.Activities.Both.Profile;
import com.example.startuptourism.Activities.Tourist.AccsFullMap;
import com.example.startuptourism.Activities.Tourist.TouAccommodationDetails;
import com.example.startuptourism.Activities.Tourist.TouristHome;
import com.example.startuptourism.Adapter.TouAccAdapter;
import com.example.startuptourism.Database.RoomDb.Entity.Accommodation;
import com.example.startuptourism.Helper.TextHelp;
import com.example.startuptourism.databinding.FragmentTouAccsBinding;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TouAccsFragment extends Fragment implements TouAccAdapter.AccomClick {
    private FragmentTouAccsBinding root;
    private TouristHome a;
    private TouAccAdapter touAccAdapter;
    private List<Accommodation> accommodationList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = FragmentTouAccsBinding.inflate(inflater, container, false);
        a = (TouristHome) getActivity();
        root.btnProfile.setOnClickListener(view -> startActivity(new Intent(getContext(), Profile.class)
                .putExtra("user", a.user)));
        root.rvAccommodationList.setAdapter(touAccAdapter = new TouAccAdapter(a, new ArrayList<>(), this));
        root.btnViewAccommodations.setOnClickListener(view -> startActivity(new Intent(a, AccsFullMap.class)
                .putExtra("user", a.user)));
        root.edtSearchQuery.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                filterList();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        retrieveAcc();
        return root.getRoot();
    }

    private void filterList() {
        String filterText = TextHelp.getText(root.edtSearchQuery).toLowerCase();
        if (filterText.equals("")) {
            touAccAdapter.refreshList(accommodationList);
            return;
        }
        List<Accommodation> filteredList = new ArrayList<>();
        if (accommodationList != null) {
            for (Accommodation accommodation : accommodationList) {
                if (checkContains(accommodation.getAccomName(), filterText) ||
                        checkContains(accommodation.getAccomAddress(), filterText) ||
                        checkContains(accommodation.getAccomType(), filterText)
                ) {
                    filteredList.add(accommodation);
                }
            }
            touAccAdapter.refreshList(filteredList);
        }
    }

    @Override
    public void onResume() {
        retrieveAcc();
        super.onResume();
    }

    private boolean checkContains(String text, String filteredText) {
        if (text == null)
            return false;
        else {
            return text.toLowerCase().contains(filteredText);
        }
    }

    private void retrieveAcc() {
        a.dbHelper.getReference().child("accommodation")
                .get().addOnSuccessListener(dataSnapshot -> {
                    accommodationList = new ArrayList<>();
                    for (DataSnapshot userAccom : dataSnapshot.getChildren()) {
                        for (DataSnapshot accom : userAccom.getChildren()) {
                            Accommodation accommodation = accom.getValue(Accommodation.class);
                            accommodation.setAccomId(accom.getKey());
                            accommodationList.add(accommodation);
                        }
                    }
                    Collections.shuffle(accommodationList);
                    touAccAdapter.refreshList(accommodationList);
                });
    }

    @Override
    public void onClick(Accommodation accommodation) {
        startActivity(new Intent(a, TouAccommodationDetails.class)
                .putExtra("accom", accommodation)
                .putExtra("user", a.user));
    }
}