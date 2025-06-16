package com.example.startuptourism.Database.RoomDb.Dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Update;

import com.example.startuptourism.Database.RoomDb.Entity.Accommodation;

@Dao
public interface AccomDao {
    @Insert
    void addAccom(Accommodation accommodation);

    @Update
    void updateAccom(Accommodation accommodation);

    @Delete
    void deleteAccom(Accommodation accommodation);
}
