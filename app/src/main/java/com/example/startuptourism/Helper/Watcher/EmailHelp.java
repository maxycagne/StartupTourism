package com.example.startuptourism.Helper.Watcher;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;

import com.example.startuptourism.Helper.TextHelp;
import com.google.android.material.textfield.TextInputLayout;

public class EmailHelp implements TextWatcher, View.OnFocusChangeListener {
    private final EditText edt;
    private final TextInputLayout layout;
    private final boolean isRequired;
    public boolean isValid;

    public EmailHelp(EditText edt, TextInputLayout layout, boolean isRequired) {
        this.edt = edt;
        this.layout = layout;
        this.isRequired = isRequired;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    }

    public boolean isValidEmail() {
        if (isRequired) {
            if (TextHelp.isEmpty(edt)) {
                TextHelp.setError(layout, "This field is required");
                isValid = false;
                return false;
            }
        }


        if (Patterns.EMAIL_ADDRESS.matcher(TextHelp.getText(edt)).matches()) {
            TextHelp.clearError(layout);
            isValid = true;
            return true;
        } else {
            TextHelp.setError(layout, "Invalid email format");
            isValid = false;
            return false;
        }
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        if (isRequired) {
            if (TextHelp.isEmpty(edt)) {
                TextHelp.setError(layout, "This field is required");
                return;
            }
        }
        TextHelp.clearError(layout);
    }

    @Override
    public void afterTextChanged(Editable editable) {
    }

    @Override
    public void onFocusChange(View view, boolean b) {
        if (!b) {
            isValidEmail();
        }
    }
}

































