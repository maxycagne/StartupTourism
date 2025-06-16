package com.example.startuptourism.Helper.Watcher;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.startuptourism.Helper.TextHelp;
import com.google.android.material.textfield.TextInputLayout;

import java.util.regex.Pattern;

public class PassHelp implements TextWatcher, View.OnFocusChangeListener {
    private final EditText edtPassword;
    private final EditText edtConfirmPassword;
    private final TextInputLayout layoutEdtPassword;
    private final TextInputLayout layoutEdtConfirmPassword;
    private final TextView txtSpace;
    private final TextView txtEight;
    private final TextView txtUpper;
    private final TextView txtLower;
    private final TextView txtSpecial;
    private final TextView txtNum;
    public boolean isValid;

    public PassHelp(EditText edtPassword, TextInputLayout layoutEdtPassword, EditText edtConfirmPassword, TextInputLayout layoutEdtConfirmPassword, TextView txtSpace, TextView txtEight, TextView txtUpper, TextView txtLower, TextView txtSpecial, TextView txtNum) {
        this.edtPassword = edtPassword;
        this.edtConfirmPassword = edtConfirmPassword;
        this.layoutEdtPassword = layoutEdtPassword;
        this.layoutEdtConfirmPassword = layoutEdtConfirmPassword;
        this.txtSpace = txtSpace;
        this.txtEight = txtEight;
        this.txtUpper = txtUpper;
        this.txtLower = txtLower;
        this.txtSpecial = txtSpecial;
        this.txtNum = txtNum;
    }

    public boolean isValidPassword() {
        String password = TextHelp.getText(edtPassword);
        boolean hasNoSpace = !password.contains(" ");
        boolean hasEight = password.length() >= 8;
        boolean hasUpper = Pattern.matches(".*[A-Z].*", password);
        boolean hasLower = Pattern.matches(".*[a-z].*", password);
        boolean hasNum = Pattern.matches(".*[0-9].*", password);
        boolean hasSpecial = Pattern.matches(".*[\\W].*", password.replaceAll(" ", ""));

        if (!hasNoSpace)
            TextHelp.setPasswordError(txtSpace);
        else
            TextHelp.clearPasswordError(txtSpace);

        if (!hasEight)
            TextHelp.setPasswordError(txtEight);
        else
            TextHelp.clearPasswordError(txtEight);

        if (!hasUpper)
            TextHelp.setPasswordError(txtUpper);
        else
            TextHelp.clearPasswordError(txtUpper);

        if (!hasLower)
            TextHelp.setPasswordError(txtLower);
        else
            TextHelp.clearPasswordError(txtLower);

        if (!hasNum)
            TextHelp.setPasswordError(txtNum);
        else
            TextHelp.clearPasswordError(txtNum);

        if (!hasSpecial)
            TextHelp.setPasswordError(txtSpecial);
        else
            TextHelp.clearPasswordError(txtSpecial);
        isValid = hasNoSpace && hasEight && hasUpper && hasLower && hasNum && hasSpecial;
        return isValid;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        if (TextHelp.isEmpty(edtPassword)) {
            TextHelp.setError(layoutEdtPassword, "This field is required");
            isValidPassword();
            if (!TextHelp.isEmpty(edtConfirmPassword))
                TextHelp.setError(layoutEdtConfirmPassword, "No password to confirm");
            else
                TextHelp.clearError(layoutEdtConfirmPassword);
        } else {
            TextHelp.clearError(layoutEdtPassword);
            if (isValidPassword()) {
                if (!TextHelp.isEmpty(edtConfirmPassword) && !TextHelp.getText(edtConfirmPassword).equals(TextHelp.getText(edtPassword)))
                    TextHelp.setError(layoutEdtConfirmPassword, "Passwords do not match");
                else
                    TextHelp.clearError(layoutEdtConfirmPassword);
            }
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {
    }

    @Override
    public void onFocusChange(View view, boolean b) {
        if (!b)
            if (TextHelp.isEmpty(edtPassword)) {
                TextHelp.setError(layoutEdtPassword, "This field is required");
                isValidPassword();
                if (!TextHelp.isEmpty(edtConfirmPassword))
                    TextHelp.setError(layoutEdtConfirmPassword, "No password to confirm");
                else
                    TextHelp.clearError(layoutEdtConfirmPassword);
            } else {
                if (isValidPassword()) {
                    TextHelp.clearError(layoutEdtPassword);
                    if (!TextHelp.isEmpty(edtConfirmPassword) && !TextHelp.getText(edtConfirmPassword).equals(TextHelp.getText(edtPassword)))
                        TextHelp.setError(layoutEdtConfirmPassword, "Passwords do not match");
                    else
                        TextHelp.clearError(layoutEdtConfirmPassword);
                } else
                    TextHelp.setError(layoutEdtPassword, "Invalid password format");
            }
    }
}
