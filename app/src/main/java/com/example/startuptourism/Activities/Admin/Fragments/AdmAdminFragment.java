package com.example.startuptourism.Activities.Admin.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.startuptourism.Activities.Admin.AdminHome;
import com.example.startuptourism.Activities.Both.Profile;
import com.example.startuptourism.databinding.FragmentAdmAdminBinding;

public class AdmAdminFragment extends Fragment {


    private FragmentAdmAdminBinding root;
    private AdminHome a;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = FragmentAdmAdminBinding.inflate(inflater, container, false);
        a = (AdminHome) getActivity();
        root.btnProfile.setOnClickListener(view -> {
            startActivity(new Intent(getContext(), Profile.class)
                    .putExtra("user", a.user));
        });


        return root.getRoot();
    }
}