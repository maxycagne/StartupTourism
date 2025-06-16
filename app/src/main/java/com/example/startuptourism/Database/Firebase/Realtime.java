package com.example.startuptourism.Database.Firebase;

import com.google.firebase.database.FirebaseDatabase;

public class Realtime {
    private static FirebaseDatabase database;

    public static FirebaseDatabase getInstance() {
        if (database == null) {
            database = FirebaseDatabase.getInstance("https://practicefirebase-cb734-default-rtdb.firebaseio.com/");
        }
        return database;
    }
}
