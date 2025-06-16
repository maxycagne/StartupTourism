package com.example.startuptourism.Helper;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;

import com.example.startuptourism.databinding.DialogTitleBinding;

public class DialogHelp {
    public static AlertDialog.Builder createDialog(Context context, String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        DialogTitleBinding root = DialogTitleBinding.inflate(LayoutInflater.from(context));
        builder.setCustomTitle(root.getRoot());
        root.txtTitle.setText(title);
        if (message != null)
            builder.setMessage(message);
        return builder;
    }
}
