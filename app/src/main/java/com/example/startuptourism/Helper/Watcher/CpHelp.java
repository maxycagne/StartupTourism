package com.example.startuptourism.Helper.Watcher;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;

import com.example.startuptourism.Helper.TextHelp;
import com.google.android.material.textfield.TextInputLayout;

public class CpHelp implements TextWatcher, View.OnFocusChangeListener {
    private final EditText edt;
    private final TextInputLayout layout;
    private final boolean isRequired;
    public boolean isValid;

    public CpHelp(EditText edt, TextInputLayout layout, boolean isRequired) {
        this.edt = edt;
        this.layout = layout;
        this.isRequired = isRequired;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    }

    public boolean isValidPhone() {
        if (isRequired) {
            if (TextHelp.isEmpty(edt)) {
                TextHelp.setError(layout, "This field is required");
                isValid = false;
                return false;
            }
        }

        if (Patterns.PHONE.matcher("+639" + TextHelp.getText(edt)).matches() && TextHelp.getText(edt).length() == 9) {
            TextHelp.clearError(layout);
            isValid = true;
            return true;
        } else {
            TextHelp.setError(layout, "Invalid phone format");
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
            isValidPhone();
        }
    }
}

































