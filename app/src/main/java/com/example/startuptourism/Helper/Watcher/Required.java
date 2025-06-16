package com.example.startuptourism.Helper.Watcher;

import android.location.Address;
import android.location.Geocoder;
import android.media.tv.AdRequest;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;


import com.example.startuptourism.Helper.TextHelp;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;

public class Required implements TextWatcher, View.OnFocusChangeListener {
    private final EditText edt;
    private final TextInputLayout layout;

    public Required(EditText edt, TextInputLayout layout) {
        this.edt = edt;
        this.layout = layout;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        isValidField();
    }

    @Override
    public void afterTextChanged(Editable editable) {}

    @Override
    public void onFocusChange(View view, boolean b) {
        if (!b)
            isValidField();

    }
    public boolean isValidField(){
        if (TextHelp.isEmpty(edt)) {
            TextHelp.setError(layout, "This field is required");
            return false;
        }
        else {
            TextHelp.clearError(layout);
            return true;
        }
    }
}

































