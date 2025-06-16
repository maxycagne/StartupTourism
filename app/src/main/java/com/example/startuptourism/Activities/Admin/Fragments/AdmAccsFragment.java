package com.example.startuptourism.Activities.Admin.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.startuptourism.Activities.Admin.AdminHome;
import com.example.startuptourism.databinding.FragmentAdmAccsBinding;

public class AdmAccsFragment extends Fragment {
    private FragmentAdmAccsBinding root;
    private AdminHome a;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = FragmentAdmAccsBinding.inflate(inflater, container, false);
        a = (AdminHome) getActivity();
        root.btnBack.setOnClickListener(view -> getActivity().finish());
        return root.getRoot();
    }
}