package com.example.startuptourism.Activities.Both;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import com.example.startuptourism.Database.RoomDb.DbHelper;
import com.example.startuptourism.Database.RoomDb.Entity.User;
import com.example.startuptourism.Helper.DialogHelp;
import com.example.startuptourism.Helper.HashHelper;
import com.example.startuptourism.Helper.TextHelp;
import com.example.startuptourism.Helper.Watcher.ConfirmPassHelp;
import com.example.startuptourism.Helper.Watcher.CpHelp;
import com.example.startuptourism.Helper.Watcher.EmailHelp;
import com.example.startuptourism.Helper.Watcher.PassHelp;
import com.example.startuptourism.Helper.Watcher.Required;
import com.example.startuptourism.databinding.ActivitySignupBinding;
import com.google.firebase.database.DatabaseReference;
import com.google.gson.Gson;

public class Signup extends AppCompatActivity {

    private ActivitySignupBinding root;
    private DbHelper dbHelper;
    private Handler handler;
    private EmailHelp emailHelp;
    private CpHelp cpHelp;
    private PassHelp passHelp;
    private ConfirmPassHelp confirmPassHelp;
    private Required requiredFirstName, requiredLastName, requiredAddress, requiredUsername;
    private User prevUser, currentUser;

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
        root = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(root.getRoot());
        setDb();
        setBackPressed();
        setValidators();
        root.btnCancel.setOnClickListener(view -> back());
        root.btnSignup.setOnClickListener(view -> validate());
        root.btnBack.setOnClickListener(view -> back());
    }

    private void setValidators() {
        root.edtFirstname.addTextChangedListener(requiredFirstName = new Required(root.edtFirstname, root.layoutEdFirstname));
        root.edtLastname.addTextChangedListener(requiredLastName = new Required(root.edtLastname, root.layoutEdtLastname));
        root.edtAddress.addTextChangedListener(requiredAddress = new Required(root.edtAddress, root.layoutEdtAddress));
        root.edtEmail.addTextChangedListener(emailHelp = new EmailHelp(root.edtEmail, root.layoutEdtEmail, false));
        root.edtContactNum.addTextChangedListener(cpHelp = new CpHelp(root.edtContactNum, root.layoutEdtContactNum, true));
        root.edtPassword.addTextChangedListener(passHelp = new PassHelp(root.edtPassword, root.layoutEdtPassword, root.edtConfirmPass, root.layoutEdtConfirmPass, root.txtSpace, root.txtCharacters, root.txtUppercase, root.txtLowercase, root.txtSymbol, root.txtNumber));
        root.edtUsername.addTextChangedListener(requiredUsername = new Required(root.edtUsername, root.layoutEdtUsername));
        root.edtConfirmPass.addTextChangedListener(confirmPassHelp = new ConfirmPassHelp(root.edtPassword, root.edtConfirmPass, root.layoutEdtPassword, root.layoutEdtConfirmPass));

        root.edtFirstname.setOnFocusChangeListener(requiredFirstName);
        root.edtLastname.setOnFocusChangeListener(requiredLastName);
        root.edtAddress.setOnFocusChangeListener(requiredAddress);
        root.edtEmail.setOnFocusChangeListener(emailHelp);
        root.edtContactNum.setOnFocusChangeListener(cpHelp);
        root.edtPassword.setOnFocusChangeListener(passHelp);
        root.edtUsername.setOnFocusChangeListener(requiredUsername);
        root.edtConfirmPass.setOnFocusChangeListener(confirmPassHelp);
    }

    private void validate() {
        requiredFirstName.isValidField();
        requiredLastName.isValidField();
        requiredAddress.isValidField();
        emailHelp.isValidEmail();
        cpHelp.isValidPhone();
        requiredUsername.isValidField();
        passHelp.isValidPassword();
        confirmPassHelp.isValidConfirmPass();

        if (TextHelp.isEmpty(root.edtFirstname) || TextHelp.isEmpty(root.edtLastname) || TextHelp.isEmpty(root.edtAddress) || TextHelp.isEmpty(root.edtContactNum)
                || TextHelp.isEmpty(root.edtUsername) || TextHelp.isEmpty(root.edtPassword) || TextHelp.isEmpty(root.edtConfirmPass)){
            Toast.makeText(this, "Please complete all fields", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!cpHelp.isValid){
            Toast.makeText(this, "Invalid contact number format", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!emailHelp.isValid){
            Toast.makeText(this, "Invalid email address format", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!passHelp.isValid){
            Toast.makeText(this, "Invalid password format", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!confirmPassHelp.isValid){
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        addUser();
    }

    private void getCurrentInputs() {
        currentUser = new User();
        prevUser = new User();
        if (!TextHelp.isEmpty(root.edtFirstname))
            currentUser.setUserFirstName(TextHelp.getText(root.edtFirstname));

        if (!TextHelp.isEmpty(root.edtLastname))
            currentUser.setUserLastName(TextHelp.getText(root.edtLastname));
        if (!TextHelp.isEmpty(root.edtAddress))
            currentUser.setUserAddress(TextHelp.getText(root.edtAddress));
        if (!TextHelp.isEmpty(root.edtEmail))
            currentUser.setUserEmail(TextHelp.getText(root.edtEmail));
        if (!TextHelp.isEmpty(root.edtContactNum))
            currentUser.setUserCpNum("+639" + TextHelp.getText(root.edtContactNum));
        if (!TextHelp.isEmpty(root.edtUsername))
            currentUser.setUsername(TextHelp.getText(root.edtUsername));
        if (!TextHelp.isEmpty(root.edtPassword))
            currentUser.setUserPassword(HashHelper.hashPassword(TextHelp.getText(root.edtPassword)));
        currentUser.setUserStatus(0);
        prevUser.setUserStatus(0);
        prevUser.setUserType("Tourist");
        if (root.rboTourist.isChecked())
            currentUser.setUserType("Tourist");
        else
            currentUser.setUserType("Owner");
    }

    private void addUser() {
        getCurrentInputs();
        DatabaseReference ref = dbHelper.getReference().child("users");
        ref.get().addOnSuccessListener(dataSnapshot -> {
            if (dataSnapshot != null) {
                if (dataSnapshot.hasChild(currentUser.getUsername())) {
                    TextHelp.setError(root.layoutEdtUsername, "Username already taken");
                    Toast.makeText(this, "Username already taken", Toast.LENGTH_SHORT).show();
                } else {
                    ref.child(currentUser.getUsername()).setValue(currentUser)
                            .addOnSuccessListener(unused -> {
                                if (currentUser.getUserType().equalsIgnoreCase("Owner")) {
                                    DialogHelp.createDialog(this, "Account created successfully", "Log in with your username and password to start posting accommodations.")
                                            .setPositiveButton("OKAY", null)
                                            .setOnDismissListener(dialogInterface -> backToLogin())
                                            .create().show();
                                } else {
                                    DialogHelp.createDialog(this, "Account created successfully", "Log in with your username and password to start looking for accommodations.")
                                            .setPositiveButton("OKAY", null)
                                            .setOnDismissListener(dialogInterface -> backToLogin())
                                            .create().show();
                                }
                            })
                            .addOnFailureListener(e -> Toast.makeText(Signup.this, "Error occurred: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                }
            } else {
                Toast.makeText(this, "Failed to create account. Try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void back() {
        Gson gson = new Gson();
        getCurrentInputs();
        if (gson.toJson(prevUser).equals(gson.toJson(currentUser))){
            backToLogin();
            return;
        }
        DialogHelp.createDialog(this, "Account created successfully", "Log in with your username and password to start posting accommodations.")
                .setPositiveButton("OKAY", null)
                .setOnDismissListener(dialogInterface -> backToLogin())
                .create().show();
    }

    private void backToLogin() {
        startActivity(new Intent(this, Login.class));
        finish();
    }

}