package com.example.startuptourism.Database.RoomDb;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.startuptourism.Database.RoomDb.Dao.AccomDao;
import com.example.startuptourism.Database.RoomDb.Dao.ReserveDao;
import com.example.startuptourism.Database.RoomDb.Dao.UserDao;
import com.example.startuptourism.Database.RoomDb.Entity.Accommodation;
import com.example.startuptourism.Database.RoomDb.Entity.Reservation;
import com.example.startuptourism.Database.RoomDb.Entity.User;

@Database(entities = {User.class, Accommodation.class, Reservation.class}, version = 1)
@TypeConverters({Converters.class})
public abstract class RoomDb extends RoomDatabase {
    private static RoomDb INSTANCE;

    public static synchronized RoomDb getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context,
                    RoomDb.class, "tourism_db").build();
        }
        return INSTANCE;
    }

    public abstract UserDao userDao();

    public abstract AccomDao accomDao();

    public abstract ReserveDao reserveDao();
}
