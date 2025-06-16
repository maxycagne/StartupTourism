package com.example.startuptourism.Activities.Both;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import com.example.startuptourism.Activities.Admin.AdminHome;
import com.example.startuptourism.Activities.Owner.PropertyOwnerHome;
import com.example.startuptourism.Activities.Tourist.TouristHome;
import com.example.startuptourism.Database.RoomDb.DbHelper;
import com.example.startuptourism.Database.RoomDb.Entity.User;
import com.example.startuptourism.databinding.ActivityMainBinding;

import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding root;
    private DbHelper dbHelper;
    private Handler handler;

    private void setDb() {
        dbHelper = new DbHelper(this);
        handler = new Handler(Looper.getMainLooper());
    }

    private void setBackPressed() {
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                finish();
                finishAffinity();
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        root = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(root.getRoot());
        setDb();
        setBackPressed();

        Executors.newSingleThreadExecutor().submit(() -> {
            try {
                User user = dbHelper.getUserDao().getCurrentUser();
                if (user != null) {
                    handler.postDelayed(() -> {
                        if (user.getUserType().equalsIgnoreCase("owner"))
                            startActivity(new Intent(this, PropertyOwnerHome.class)
                                    .putExtra("user", user));
                        else if (user.getUserType().equalsIgnoreCase("tourist"))
                            startActivity(new Intent(this, TouristHome.class)
                                    .putExtra("user", user));
                        else
                            startActivity(new Intent(this, AdminHome.class)
                                    .putExtra("user", user));
                        finish();
                    }, 3000);
                } else
                    handler.postDelayed(() -> {
                        startActivity(new Intent(this, Login.class));
                        finish();
                    }, 3250);
            } catch (Exception e) {
                handler.post(() -> Toast.makeText(this, "Error occurred: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        });


        // animation starts here

        root.txt1.setTranslationX(1000);
        root.txt2.setTranslationX(1000);
        root.txt3.setTranslationX(1000);
        root.icon1.setTranslationX(1000);
        root.icon2.setTranslationX(1000);


        long animationDuration = 200L;

        root.txt1.animate()
                .translationXBy(-1000)
                .setDuration(animationDuration)
                .setStartDelay(250)
                .withEndAction(() -> root.txt2.animate()
                        .translationXBy(-1000)
                        .setDuration(animationDuration)
                        .withEndAction(() -> root.icon1.animate()
                                .translationXBy(-1000)
                                .setDuration(animationDuration)
                                .setStartDelay(250)
                                .withEndAction(() -> root.icon2.animate()
                                        .translationXBy(-1000)
                                        .setDuration(animationDuration)
                                        .withEndAction(() -> root.txt3.animate()
                                                .translationXBy(-1000)
                                                .setDuration(animationDuration)
                                                .setStartDelay(250)))))
        ;


    }
}















