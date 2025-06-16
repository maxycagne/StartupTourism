package com.example.startuptourism.Activities.Owner;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.example.startuptourism.Database.RoomDb.DbHelper;
import com.example.startuptourism.Database.RoomDb.Entity.User;
import com.example.startuptourism.R;
import com.example.startuptourism.databinding.ActivityPropertyOwnerHomeBinding;

public class PropertyOwnerHome extends AppCompatActivity {

    public DbHelper dbHelper;
    public Handler handler;
    public User user;
    private ActivityPropertyOwnerHomeBinding root;

    private void setDb() {
        dbHelper = new DbHelper(this);
        handler = new Handler(Looper.getMainLooper());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        root = ActivityPropertyOwnerHomeBinding.inflate(getLayoutInflater());
        setContentView(root.getRoot());
        setDb();
        user = (User) getIntent().getSerializableExtra("user");
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.fragment);
        NavController navController = navHostFragment.getNavController();
        NavigationUI.setupWithNavController(root.botNavBar, navController);


        root.botNavBar.setOnItemSelectedListener(item -> {
            navController.navigate(item.getItemId());
            return true;
        });
    }
}