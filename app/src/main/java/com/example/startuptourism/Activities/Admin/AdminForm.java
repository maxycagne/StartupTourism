package com.example.startuptourism.Activities.Admin;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.startuptourism.databinding.ActivityAdminFormBinding;

public class AdminForm extends AppCompatActivity {

    private ActivityAdminFormBinding root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        root = ActivityAdminFormBinding.inflate(getLayoutInflater());
        setContentView(root.getRoot());


        root.btnBack.setOnClickListener(view -> finish());
        root.btnCancel.setOnClickListener(view -> finish());


    }

    private void back() {
        finish();
    }
}