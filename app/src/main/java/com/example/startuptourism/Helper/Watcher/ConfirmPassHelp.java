package com.example.startuptourism.Helper.Watcher;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import com.example.startuptourism.Helper.TextHelp;
import com.google.android.material.textfield.TextInputLayout;

public class ConfirmPassHelp implements TextWatcher, View.OnFocusChangeListener {
    private final EditText edtPassword;
    private final EditText edtConfirmPassword;
    private final TextInputLayout layoutEdtPassword;
    private final TextInputLayout layoutEdtConfirmPassword;
    public boolean isValid;

    public ConfirmPassHelp(EditText edtPassword, EditText edtConfirmPassword, TextInputLayout layoutEdtPassword, TextInputLayout layoutEdtConfirmPassword) {
        this.edtPassword = edtPassword;
        this.edtConfirmPassword = edtConfirmPassword;
        this.layoutEdtPassword = layoutEdtPassword;
        this.layoutEdtConfirmPassword = layoutEdtConfirmPassword;
    }


    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        if (TextHelp.isEmpty(edtConfirmPassword)) {
            TextHelp.setError(layoutEdtPassword, "This field is required");
        } else {
            TextHelp.clearError(layoutEdtConfirmPassword);
        }
    }
    public boolean isValidConfirmPass(){
        if (TextHelp.isEmpty(edtConfirmPassword)) {
            TextHelp.setError(layoutEdtConfirmPassword, "This field is required.");
            isValid = false;
            return false;
        } else {
            if (TextHelp.getText(edtConfirmPassword).equals(TextHelp.getText(edtPassword))) {
                TextHelp.clearError(layoutEdtConfirmPassword);
                isValid = true;
                return true;
            } else {
                TextHelp.setError(layoutEdtConfirmPassword, "Passwords do not match.");
                isValid = false;
                return false;
            }
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {
    }

    @Override
    public void onFocusChange(View view, boolean b) {
        if (!b)
            isValidConfirmPass();
    }
}
