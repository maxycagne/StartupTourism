package com.example.startuptourism.Helper;

import android.content.res.ColorStateList;
import android.widget.EditText;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;
import androidx.core.widget.TextViewCompat;

import com.example.startuptourism.R;
import com.google.android.material.textfield.TextInputLayout;

public class TextHelp {
    public static boolean isEmpty(EditText editText) {
        String txt = editText.getText().toString();
        return txt.isEmpty() || txt.replaceAll(" ", "").isEmpty();
    }

    public static String getText(EditText editText) {
        return editText.getText().toString();
    }

    public static String getText(TextView editText) {
        return editText.getText().toString();
    }


    public static void setError(TextInputLayout layout, String error) {
        layout.setErrorEnabled(true);
        layout.setError(error);
    }

    public static void setPasswordError(TextView txt) {
        txt.setTextColor(ResourcesCompat.getColor(txt.getContext().getResources(), R.color.red, txt.getContext().getTheme()));
        txt.setCompoundDrawablesWithIntrinsicBounds(ResourcesCompat.getDrawable(txt.getContext().getResources(), R.drawable.ic_x, txt.getContext().getTheme()), null, null, null);
        TextViewCompat.setCompoundDrawableTintList(txt, ColorStateList.valueOf(ResourcesCompat.getColor(txt.getContext().getResources(), R.color.red, txt.getContext().getTheme())));
    }

    public static void clearError(TextInputLayout layout) {
        layout.setErrorEnabled(false);
        layout.setError("");
    }

    public static void clearPasswordError(TextView txt) {
        txt.setTextColor(ResourcesCompat.getColor(txt.getContext().getResources(), R.color.green, txt.getContext().getTheme()));
        txt.setCompoundDrawablesWithIntrinsicBounds(ResourcesCompat.getDrawable(txt.getContext().getResources(), R.drawable.ic_check, txt.getContext().getTheme()), null, null, null);
        TextViewCompat.setCompoundDrawableTintList(txt, ColorStateList.valueOf(ResourcesCompat.getColor(txt.getContext().getResources(), R.color.green, txt.getContext().getTheme())));
    }
}
