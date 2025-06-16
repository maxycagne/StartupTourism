package com.example.startuptourism.Activities.Tourist;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.example.startuptourism.Database.RoomDb.DbHelper;
import com.example.startuptourism.Database.RoomDb.Entity.User;
import com.example.startuptourism.R;
import com.example.startuptourism.databinding.ActivityTouristHomeBinding;

public class TouristHome extends AppCompatActivity {

    public User user;
    public DbHelper dbHelper;
    public Handler handler;
    private ActivityTouristHomeBinding root;
    private NavController navController;

    private void setDb() {
        dbHelper = new DbHelper(this);
        handler = new Handler(Looper.getMainLooper());
    }

    private void setBackPressed() {
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                if (root.botNavBar.getSelectedItemId() == R.id.touAccsFragment) {
                    finish();
                    finishAffinity();
                } else {
                    navController.navigate(R.id.touAccsFragment);
                    root.botNavBar.setSelectedItemId(R.id.touAccsFragment);
                }
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        root = ActivityTouristHomeBinding.inflate(getLayoutInflater());
        setContentView(root.getRoot());
        setDb();
        setBackPressed();
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.fragment);
        navController = navHostFragment.getNavController();
        NavigationUI.setupWithNavController(root.botNavBar, navController);

        root.botNavBar.setOnItemSelectedListener(item -> {
            navController.navigate(item.getItemId());
            return true;
        });
        user = (User) getIntent().getSerializableExtra("user");
    }


}