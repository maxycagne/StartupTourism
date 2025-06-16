package com.example.startuptourism.Database.RoomDb.Dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Update;

import com.example.startuptourism.Database.RoomDb.Entity.Reservation;

@Dao
public interface ReserveDao {
    @Insert
    void addReservation(Reservation reservation);

    @Update
    void updateReservation(Reservation reservation);

    @Delete
    void deleteReservation(Reservation reservation);

}
