package com.example.startuptourism.Activities.Admin;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.startuptourism.databinding.ActivityAdmAccommodationDetailsBinding;

public class AdmAccommodationDetails extends AppCompatActivity {

    private ActivityAdmAccommodationDetailsBinding root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        root = ActivityAdmAccommodationDetailsBinding.inflate(getLayoutInflater());
        setContentView(root.getRoot());


        root.btnBack.setOnClickListener(view -> finish());
        root.btnReturn.setOnClickListener(view -> finish());


    }

    private void back() {
        finish();
    }
}