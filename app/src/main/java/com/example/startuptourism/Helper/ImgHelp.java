package com.example.startuptourism.Helper;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;

import androidx.core.content.FileProvider;

import java.io.File;

public class ImgHelp {
    public static Uri getFileUri(Context context, File file) {
        Uri photoURI = FileProvider.getUriForFile(context,
                "com.example.startuptourism",
                file);
        return photoURI;
    }

    public static File createCameraFile(Context context) {
        File file = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "camera.jpg");
        return file;
    }
}
