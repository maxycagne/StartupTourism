package com.example.startuptourism.Activities.Admin;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.startuptourism.Database.RoomDb.Entity.User;
import com.example.startuptourism.databinding.ActivityAdminHomeBinding;


public class AdminHome extends AppCompatActivity {

    public User user;
    private ActivityAdminHomeBinding root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        root = ActivityAdminHomeBinding.inflate(getLayoutInflater());
        setContentView(root.getRoot());
        user = (User) getIntent().getSerializableExtra("user");
    }
}