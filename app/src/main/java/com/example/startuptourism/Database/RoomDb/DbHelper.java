package com.example.startuptourism.Database.RoomDb;

import android.content.Context;

import com.example.startuptourism.Database.Firebase.Realtime;
import com.example.startuptourism.Database.Firebase.Storage;
import com.example.startuptourism.Database.RoomDb.Dao.AccomDao;
import com.example.startuptourism.Database.RoomDb.Dao.ReserveDao;
import com.example.startuptourism.Database.RoomDb.Dao.UserDao;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

public class DbHelper {
    private final RoomDb roomDb;
    private final UserDao userDao;
    private final AccomDao accomDao;
    private final ReserveDao reserveDao;
    private final FirebaseStorage storage;
    private final FirebaseDatabase realtime;
    private final DatabaseReference reference;


    public DbHelper(Context context) {
        this.roomDb = RoomDb.getInstance(context);
        this.userDao = roomDb.userDao();
        this.storage = Storage.getInstance();
        this.realtime = Realtime.getInstance();
        this.reference = realtime.getReference();
        this.accomDao = roomDb.accomDao();
        this.reserveDao = roomDb.reserveDao();
    }

    public RoomDb getRoomDb() {
        return roomDb;
    }

    public UserDao getUserDao() {
        return userDao;
    }

    public FirebaseStorage getStorage() {
        return storage;
    }

    public FirebaseDatabase getRealtime() {
        return realtime;
    }

    public AccomDao getAccomDao() {
        return accomDao;
    }

    public ReserveDao getReserveDao() {
        return reserveDao;
    }

    public DatabaseReference getReference() {
        return reference;
    }
}
