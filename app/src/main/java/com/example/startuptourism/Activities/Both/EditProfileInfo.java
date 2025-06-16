package com.example.startuptourism.Activities.Both;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.startuptourism.Database.RoomDb.DbHelper;
import com.example.startuptourism.Database.RoomDb.Entity.User;
import com.example.startuptourism.Helper.DialogHelp;
import com.example.startuptourism.Helper.ImgHelp;
import com.example.startuptourism.Helper.TextHelp;
import com.example.startuptourism.Helper.Watcher.CpHelp;
import com.example.startuptourism.Helper.Watcher.EmailHelp;
import com.example.startuptourism.Helper.Watcher.Required;
import com.example.startuptourism.R;
import com.example.startuptourism.databinding.ActivityEditProfileInfoBinding;
import com.example.startuptourism.databinding.DialogLoadingBinding;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.concurrent.Executors;

public class EditProfileInfo extends AppCompatActivity {

    private ActivityEditProfileInfoBinding root;
    private ActivityResultLauncher<PickVisualMediaRequest> pickMedia;
    private ActivityResultLauncher<Uri> cameraLauncher;
    private Uri selectedImg, tempUri;
    private User prevUser, currentUser;
    private DbHelper dbHelper;
    private Handler handler;
    private EmailHelp emailHelp;
    private CpHelp cpHelp;

    private void setDb() {
        dbHelper = new DbHelper(this);
        handler = new Handler(Looper.getMainLooper());
    }

    private void setBackPressed() {
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                back();
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        root = ActivityEditProfileInfoBinding.inflate(getLayoutInflater());
        setContentView(root.getRoot());
        setDb();
        setBackPressed();
        setImageLauncher();
        prevUser = (User) getIntent().getSerializableExtra("user");
        setDetails();
        root.imgProfile.setOnClickListener(view -> {
            DialogHelp.createDialog(this, "Select image from...", null)
                    .setItems(getResources().getStringArray(R.array.profile_sources), ((dialogInterface, i) -> {
                        switch (i) {
                            case 0:
                                pickMedia.launch(new PickVisualMediaRequest.Builder()
                                        .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                                        .build());
                                break;
                            case 1:
                                tempUri = ImgHelp.getFileUri(this, ImgHelp.createCameraFile(this));
                                cameraLauncher.launch(tempUri);
                                break;
                        }
                    })).create().show();
        });
        root.btnBack.setOnClickListener(view -> back());
        root.btnCancel.setOnClickListener(view -> back());
        root.btnUpdate.setOnClickListener(view -> updateInfo());


        root.edtFirstname.addTextChangedListener(new Required(root.edtFirstname, root.layoutEdFirstname));
        root.edtLastname.addTextChangedListener(new Required(root.edtLastname, root.layoutEdtLastname));
        root.edtAddress.addTextChangedListener(new Required(root.edtAddress, root.layoutEdtAddress));

        root.edtFirstname.setOnFocusChangeListener(new Required(root.edtFirstname, root.layoutEdFirstname));
        root.edtLastname.setOnFocusChangeListener(new Required(root.edtLastname, root.layoutEdtLastname));
        root.edtAddress.setOnFocusChangeListener(new Required(root.edtAddress, root.layoutEdtAddress));

        emailHelp = new EmailHelp(root.edtEmail, root.layoutEdtEmail, false);
        root.edtEmail.setOnFocusChangeListener(emailHelp);
        root.edtEmail.addTextChangedListener(emailHelp);

        cpHelp = new CpHelp(root.edtContactNum, root.layoutEdtContactNum, true);
        root.edtContactNum.addTextChangedListener(cpHelp);
        root.edtContactNum.setOnFocusChangeListener(cpHelp);
    }

    private void setDetails() {
        Picasso.get().load(prevUser.getUserImg()).resize(200, 200).placeholder(R.drawable.ic_account).centerCrop().into(root.imgProfile);
        root.edtLastname.setText(prevUser.getUserLastName());
        root.edtFirstname.setText(prevUser.getUserFirstName());
        root.edtAddress.setText(prevUser.getUserAddress());
        root.edtContactNum.setText(prevUser.getUserCpNum().substring(4));
        root.edtEmail.setText(prevUser.getUserEmail());
    }

    private void setImageLauncher() {
        pickMedia = registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
            if (uri != null) {
                selectedImg = uri;
                Picasso.get().load(selectedImg).resize(200, 200).placeholder(R.drawable.ic_account).centerCrop().into(root.imgProfile);

            } else {

            }
        });
        cameraLauncher = registerForActivityResult(new ActivityResultContracts.TakePicture(), o -> {
            if (o) {
                selectedImg = tempUri;
                Picasso.get().load(selectedImg).resize(200, 200).placeholder(R.drawable.ic_account).centerCrop().into(root.imgProfile);
            }
        });
    }

    private void updateInfo() {
        validate();
        if (TextHelp.isEmpty(root.edtFirstname) || TextHelp.isEmpty(root.edtLastname) || TextHelp.isEmpty(root.edtAddress)
                || TextHelp.isEmpty(root.edtContactNum)) {
            Toast.makeText(this, "Complete all details", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!cpHelp.isValidPhone()) {
            Toast.makeText(this, "Contact number invalid", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!emailHelp.isValidEmail()) {
            Toast.makeText(this, "Email address invalid", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!hasChanges()) {
            Toast.makeText(this, "No changes has been made to save", Toast.LENGTH_SHORT).show();
            return;
        }

        DialogLoadingBinding binding = DialogLoadingBinding.inflate(getLayoutInflater());
        AlertDialog dialog = DialogHelp.createDialog(this, "Saving Profile Changes", null)
                .setCancelable(false)
                .setView(binding.getRoot())
                .create();
        dialog.show();
        // TODO: 10/05/2024 change waiting text
        binding.txtMessage.setText("Please wait...");

        if (selectedImg != null) {
            StorageReference userFolder = dbHelper.getStorage().getReference().child("userImages");
            StorageReference imgRef = userFolder.child(selectedImg.getLastPathSegment());
            UploadTask uploadTask = imgRef.putFile(selectedImg);
            uploadTask.continueWithTask(task -> {
                if (!task.isSuccessful()) {
                    Toast.makeText(this, "Error occurred", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
                return imgRef.getDownloadUrl();
            }).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    addUser(task.getResult().toString(), dialog);
                } else {
                    dialog.dismiss();
                }
            });
        } else {
            addUser(null, dialog);
        }
    }

    private void getCurrentUser() {
        Gson gson = new Gson();
        currentUser = gson.fromJson(gson.toJson(prevUser), User.class);

        if (TextHelp.isEmpty(root.edtFirstname))
            currentUser.setUserFirstName(null);
        else
            currentUser.setUserFirstName(TextHelp.getText(root.edtFirstname));

        if (TextHelp.isEmpty(root.edtLastname))
            currentUser.setUserLastName(null);
        else
            currentUser.setUserLastName(TextHelp.getText(root.edtLastname));

        if (TextHelp.isEmpty(root.edtAddress))
            currentUser.setUserAddress(null);
        else
            currentUser.setUserAddress(TextHelp.getText(root.edtAddress));

        if (TextHelp.isEmpty(root.edtContactNum))
            currentUser.setUserCpNum(null);
        else
            currentUser.setUserCpNum("+639" + TextHelp.getText(root.edtContactNum));

        if (TextHelp.isEmpty(root.edtEmail))
            currentUser.setUserEmail(null);
        else
            currentUser.setUserEmail(TextHelp.getText(root.edtEmail));

    }

    private void addUser(String string, AlertDialog dialog) {
        getCurrentUser();
        User user1 = currentUser;
        if (string != null) {
            user1.setUserImg(string);
        }
        dbHelper.getReference().child("users").child(user1.getUsername()).setValue(user1)
                .addOnSuccessListener(unused -> {
                    dialog.dismiss();
                    Executors.newSingleThreadExecutor().submit(() -> {
                        try {
                            dbHelper.getUserDao().updateUser(user1);
                            handler.post(() -> {
                                Toast.makeText(this, "Changes made are saved successfully", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(this, Profile.class)
                                        .putExtra("user", user1));
                                finish();
                            });
                        } catch (Exception ignore) {

                        }
                    });
                })
                .addOnFailureListener(e -> {
                    dialog.dismiss();
                    Toast.makeText(this, "Failed to change password. Try again", Toast.LENGTH_SHORT).show();
                });

    }

    private void validate() {
        if (TextHelp.isEmpty(root.edtFirstname))
            TextHelp.setError(root.layoutEdFirstname, "This field is required");
        else
            TextHelp.clearError(root.layoutEdFirstname);

        if (TextHelp.isEmpty(root.edtLastname))
            TextHelp.setError(root.layoutEdtLastname, "This field is required");
        else
            TextHelp.clearError(root.layoutEdtLastname);

        if (TextHelp.isEmpty(root.edtAddress))
            TextHelp.setError(root.layoutEdtAddress, "This field is required");
        else
            TextHelp.clearError(root.layoutEdtAddress);
    }

    private boolean hasChanges() {
        Gson gson = new Gson();
        getCurrentUser();
        if (gson.toJson(prevUser).equals(gson.toJson(currentUser))) {
            return selectedImg != null;
        }
        return true;
    }

    private void back() {
        boolean change = hasChanges();
        if (!change) {
            backToProfile(prevUser);
            return;
        }

        DialogHelp.createDialog(this, "Discard Changes", "Going back will discard your profile changes.")
                .setNegativeButton("CANCEL", null)
                .setPositiveButton("DISCARD DRAFT", ((dialogInterface, i) -> {
                    backToProfile(prevUser);
                }))
                .create().show();
    }

    private void backToProfile(User user) {
        startActivity(new Intent(this, Profile.class)
                .putExtra("user", user));
        finish();
    }
}