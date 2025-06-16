package com.example.startuptourism.Activities.Both;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.startuptourism.Database.RoomDb.DbHelper;
import com.example.startuptourism.Database.RoomDb.Entity.User;
import com.example.startuptourism.Helper.DialogHelp;
import com.example.startuptourism.Helper.HashHelper;
import com.example.startuptourism.Helper.TextHelp;
import com.example.startuptourism.Helper.Watcher.ConfirmPassHelp;
import com.example.startuptourism.Helper.Watcher.PassHelp;
import com.example.startuptourism.Helper.Watcher.Required;
import com.example.startuptourism.R;
import com.example.startuptourism.databinding.ActivityProfileBinding;
import com.example.startuptourism.databinding.DialogChangePasswordBinding;
import com.squareup.picasso.Picasso;

import java.util.concurrent.Executors;

public class Profile extends AppCompatActivity {
    private ActivityProfileBinding root;
    private DbHelper dbHelper;
    private Handler handler;
    private User user;

    private void setDb() {
        dbHelper = new DbHelper(this);
        handler = new Handler(Looper.getMainLooper());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        root = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(root.getRoot());
        setDb();
        user = (User) getIntent().getSerializableExtra("user");
        retrieveData();
        root.btnLogout.setOnClickListener(view -> logout());
        root.btnBack.setOnClickListener(view -> finish());
        root.btnChangePassword.setOnClickListener(view -> changePasswordDialog());
        root.btnEditDetails.setOnClickListener(view -> {
            startActivity(new Intent(this, EditProfileInfo.class)
                    .putExtra("user", user));
            finish();
        });
    }

    private void retrieveData() {
        dbHelper.getReference().child("users").child(user.getUsername()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                user = task.getResult().getValue(User.class);

            } else {
                //failed to retrieve
            }

            Picasso.get().load(user.getUserImg()).resize(200, 200).placeholder(R.drawable.ic_account).centerCrop().into(root.imgProfile);
            root.txtName.setText(user.getUserFirstName().concat(" ".concat(user.getUserLastName())));
            root.txtContactNum.setText(user.getUserCpNum());
            if (user.getUserEmail() != null)
                root.txtEmailAddress.setText(user.getUserEmail());
            else
                root.txtEmailAddress.setText("[No email]");
            root.txtUsername.setText(user.getUsername());
            root.txtAddress.setText(user.getUserAddress());
        });
    }

    private void changePasswordDialog() {
        DialogChangePasswordBinding binding = DialogChangePasswordBinding.inflate(getLayoutInflater());

        AlertDialog dialog = DialogHelp.createDialog(this, "Change Password", null)
                .setPositiveButton("Save Change", null)
                .setNegativeButton("Cancel", null)
                .setView(binding.getRoot())
                .create();
        dialog.show();

        binding.edtOldPass.addTextChangedListener(new Required(binding.edtOldPass, binding.layoutEdtOldPass));
        binding.edtOldPass.setOnFocusChangeListener(new Required(binding.edtOldPass, binding.layoutEdtOldPass));
        PassHelp passHelp = new PassHelp(binding.edtNewPass, binding.layoutEdtNewPass, binding.edtConfirmPass, binding.layoutEdtConfirmPass, binding.txtSpace, binding.txtCharacters, binding.txtUppercase, binding.txtLowercase, binding.txtSymbol, binding.txtNumber);
        binding.edtNewPass.addTextChangedListener(passHelp);
        binding.edtNewPass.setOnFocusChangeListener(passHelp);
        ConfirmPassHelp confirmPassHelp = new ConfirmPassHelp(binding.edtNewPass, binding.edtConfirmPass, binding.layoutEdtNewPass, binding.layoutEdtConfirmPass);
        binding.edtConfirmPass.addTextChangedListener(confirmPassHelp);
        binding.edtConfirmPass.setOnFocusChangeListener(confirmPassHelp);

        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(view1 -> {
            if (TextHelp.isEmpty(binding.edtOldPass) || TextHelp.isEmpty(binding.edtNewPass) || TextHelp.isEmpty(binding.edtConfirmPass)) {
                Toast.makeText(this, "Please complete all fields", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!HashHelper.hashPasswordCheck(TextHelp.getText(binding.edtOldPass), user.getUserPassword())) {
                Toast.makeText(this, "Invalid old password", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!passHelp.isValidPassword()) {
                Toast.makeText(this, "Invalid new password format", Toast.LENGTH_SHORT).show();
                return;
            }
            if (HashHelper.hashPasswordCheck(TextHelp.getText(binding.edtNewPass), user.getUserPassword())) {
                Toast.makeText(this, "New password must not be the same as old password", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!TextHelp.getText(binding.edtNewPass).equals(TextHelp.getText(binding.edtConfirmPass))) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }


            User user1 = user;
            user1.setUserPassword(HashHelper.hashPassword(TextHelp.getText(binding.edtNewPass)));
            dbHelper.getReference().child("users").child(user1.getUsername()).setValue(user1)
                    .addOnSuccessListener(unused -> {
                        Executors.newSingleThreadExecutor().submit(() -> {
                            try {
                                dbHelper.getUserDao().updateUser(user1);
                                dialog.dismiss();
                                handler.post(() -> Toast.makeText(this, "Password changed successfully", Toast.LENGTH_SHORT).show());
                                user = user1;
                            } catch (Exception ignore) {

                            }
                        });
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Failed to change password. Try again", Toast.LENGTH_SHORT).show();
                    });
        });
    }


    private void logout() {
        DialogHelp.createDialog(this, "Logout Confirmation", "Are you sure you want to log out?")
                .setNegativeButton("CANCEL", null)
                .setPositiveButton("YES", (dialogInterface, i) -> {
                    Executors.newSingleThreadExecutor().submit(() -> {
                        try {
                            dbHelper.getUserDao().deleteUser(user);
                            handler.post(() -> {
                                startActivity(new Intent(this, Login.class));
                                finish();
                            });
                        } catch (Exception e) {
                            handler.post(() -> Toast.makeText(this, "Error occurred: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                        }
                    });
                })
                .create().show();
    }
}