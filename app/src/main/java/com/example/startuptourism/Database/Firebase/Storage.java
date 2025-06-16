package com.example.startuptourism.Database.Firebase;

import com.google.firebase.storage.FirebaseStorage;

public class Storage {
    private static FirebaseStorage storage;

    public static FirebaseStorage getInstance() {
        if (storage == null) {
            storage = FirebaseStorage.getInstance();
        }
        return storage;
    }
}
