package com.example.startuptourism.Activities.Owner.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.fragment.app.Fragment;

import com.example.startuptourism.Activities.Both.Profile;
import com.example.startuptourism.Activities.Both.ReservationDetails;
import com.example.startuptourism.Activities.Owner.PropertyOwnerHome;
import com.example.startuptourism.Adapter.OwnerResAccAdapter;
import com.example.startuptourism.Database.RoomDb.Entity.Reservation;
import com.example.startuptourism.R;
import com.example.startuptourism.databinding.FragmentPowResBinding;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.List;

public class PowResFragment extends Fragment implements OwnerResAccAdapter.ReservationClick {
    private FragmentPowResBinding root;
    private PropertyOwnerHome a;
    private OwnerResAccAdapter ownerResAccAdapter;
    private List<Reservation> reservationList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = FragmentPowResBinding.inflate(inflater, container, false);
        a = (PropertyOwnerHome) getActivity();
        root.rvReservationList.setAdapter(ownerResAccAdapter = new OwnerResAccAdapter(a, new ArrayList<>(), this, 1));
        root.btnProfile.setOnClickListener(view -> startActivity(new Intent(getContext(), Profile.class)
                .putExtra("user", a.user)));
        root.spnSort.setText("Earliest Requested");
        root.spnSort.setAdapter(new ArrayAdapter<String>(a, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.sort_tou)));
        root.spnSort.setOnItemClickListener((adapterView, view, i, l) -> setListener());
        root.cbPending.setOnCheckedChangeListener((compoundButton, b) -> setListener());
        root.cbAccepted.setOnCheckedChangeListener((compoundButton, b) -> setListener());
        root.cbRejected.setOnCheckedChangeListener((compoundButton, b) -> setListener());
        root.cbCancelled.setOnCheckedChangeListener((compoundButton, b) -> setListener());
        setListener();
        return root.getRoot();
    }

    private void setListener() {
        // TODO: 11/05/2024 sort
        a.dbHelper.getReference().child("reservations").get().addOnSuccessListener(dataSnapshot -> {
            reservationList = new ArrayList<>();
            if (dataSnapshot != null) {
                for (DataSnapshot res : dataSnapshot.getChildren()) {
                    Reservation reservation = res.getValue(Reservation.class);
                    if (reservation.getReserveUserPow().equals(a.user.getUsername())) {
                        reservation.setReserveId(res.getKey());
                        if (root.cbPending.isChecked() & reservation.getReserveStatus().equals("Pending"))
                            reservationList.add(reservation);
                        if (root.cbRejected.isChecked() & reservation.getReserveStatus().equals("Rejected"))
                            reservationList.add(reservation);
                        if (root.cbAccepted.isChecked() & reservation.getReserveStatus().equals("Accepted"))
                            reservationList.add(reservation);
                        if (root.cbCancelled.isChecked() & reservation.getReserveStatus().equals("Cancelled"))
                            reservationList.add(reservation);
                    }
                }
            }
            ownerResAccAdapter.refreshList(reservationList);
        });
    }

    @Override
    public void onClick(Reservation reservation) {
        startActivity(new Intent(a, ReservationDetails.class)
                .putExtra("reservation", reservation)
                .putExtra("user", a.user));
    }

    @Override
    public void onResume() {
        root.spnSort.setText("Earliest Requested");
        root.spnSort.setAdapter(new ArrayAdapter<String>(a, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.sort_tou)));
        setListener();
        super.onResume();
    }
}
