package com.example.startuptourism.Database.RoomDb.Relation;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.example.startuptourism.Database.RoomDb.Entity.Accommodation;
import com.example.startuptourism.Database.RoomDb.Entity.Reservation;
import com.example.startuptourism.Database.RoomDb.Entity.User;

import java.io.Serializable;

public class ReserveUserAccom implements Serializable {
    @Embedded
    public Reservation reservation;
    @Relation(
            parentColumn = "reserveAccomId",
            entityColumn = "accomId"
    )
    public Accommodation accommodation;

    @Relation(
            parentColumn = "reserveUsername",
            entityColumn = "username"
    )
    public User tourist;

    public ReserveUserAccom(Reservation reservation, Accommodation accommodation, User tourist) {
        this.reservation = reservation;
        this.accommodation = accommodation;
        this.tourist = tourist;
    }
}
