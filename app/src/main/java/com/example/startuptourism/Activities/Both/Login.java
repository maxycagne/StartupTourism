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
import com.example.startuptourism.Helper.HashHelper;
import com.example.startuptourism.Helper.TextHelp;
import com.example.startuptourism.databinding.ActivityLoginBinding;
import com.google.firebase.database.DatabaseReference;

import java.util.concurrent.Executors;

public class Login extends AppCompatActivity {

    private ActivityLoginBinding root;

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
        root = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(root.getRoot());
        setDb();
        setBackPressed();

        root.btnLogin.setOnClickListener(view -> validate());

        root.btnCreateAccount.setOnClickListener(view -> {
            startActivity(new Intent(this, Signup.class));
            finish();
        });

    }

    private void validate() {
        if (TextHelp.isEmpty(root.edtUsername) || TextHelp.isEmpty(root.edtPassword)) {
            Toast.makeText(this, "Please complete all fields", Toast.LENGTH_SHORT).show();
            return;
        }
        DatabaseReference ref = dbHelper.getReference().child("users").child(TextHelp.getText(root.edtUsername));
        ref.get().addOnSuccessListener(dataSnapshot -> {
            if (dataSnapshot != null) {
                if (dataSnapshot.getChildrenCount() > 0) {
                    User user = dataSnapshot.getValue(User.class);
                    if (user != null)
                        if (HashHelper.hashPasswordCheck(TextHelp.getText(root.edtPassword), user.getUserPassword()))
                            addToLocalDB(user);
                        else
                            Toast.makeText(this, "Invalid username or password", Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(this, "Invalid username or password", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addToLocalDB(User user) {
        Executors.newSingleThreadExecutor().submit(() -> {
            try {
                dbHelper.getUserDao().addUser(user);
                handler.post(() -> {
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
                });
            } catch (Exception e) {
                handler.post(() -> Toast.makeText(this, "Error occurred: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        });
    }
}











