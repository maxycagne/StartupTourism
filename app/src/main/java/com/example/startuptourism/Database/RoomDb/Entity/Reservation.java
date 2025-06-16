package com.example.startuptourism.Database.RoomDb.Entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity
public class Reservation implements Serializable {
    @PrimaryKey
    @NonNull
    private String reserveId;
    private String reserveAccomId, reserveAccomPlace, reserveReason;
    private String reserveUserTou, reserveUserPow, reserveStatus, reserveClientName;
    private long checkInDate, checkOutDate, reservationDate;
    private double reservationAmt;
    private int reserveNumPerson;

    @Ignore
    public Reservation() {
    }

    public Reservation(@NonNull String reserveId, String reserveAccomId, String reserveAccomPlace, String reserveReason, String reserveUserTou, String reserveUserPow, String reserveStatus, String reserveClientName, long checkInDate, long checkOutDate, long reservationDate, double reservationAmt, int reserveNumPerson) {
        this.reserveId = reserveId;
        this.reserveAccomId = reserveAccomId;
        this.reserveAccomPlace = reserveAccomPlace;
        this.reserveReason = reserveReason;
        this.reserveUserTou = reserveUserTou;
        this.reserveUserPow = reserveUserPow;
        this.reserveStatus = reserveStatus;
        this.reserveClientName = reserveClientName;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.reservationDate = reservationDate;
        this.reservationAmt = reservationAmt;
        this.reserveNumPerson = reserveNumPerson;
    }

    @NonNull
    public String getReserveId() {
        return reserveId;
    }

    public void setReserveId(@NonNull String reserveId) {
        this.reserveId = reserveId;
    }

    public String getReserveReason() {
        return reserveReason;
    }

    public void setReserveReason(String reserveReason) {
        this.reserveReason = reserveReason;
    }

    public String getReserveAccomId() {
        return reserveAccomId;
    }

    public void setReserveAccomId(String reserveAccomId) {
        this.reserveAccomId = reserveAccomId;
    }

    public String getReserveUserTou() {
        return reserveUserTou;
    }

    public void setReserveUserTou(String reserveUserTou) {
        this.reserveUserTou = reserveUserTou;
    }

    public String getReserveUserPow() {
        return reserveUserPow;
    }

    public void setReserveUserPow(String reserveUserPow) {
        this.reserveUserPow = reserveUserPow;
    }

    public String getReserveStatus() {
        return reserveStatus;
    }

    public void setReserveStatus(String reserveStatus) {
        this.reserveStatus = reserveStatus;
    }

    public long getCheckInDate() {
        return checkInDate;
    }

    public void setCheckInDate(long checkInDate) {
        this.checkInDate = checkInDate;
    }

    public long getCheckOutDate() {
        return checkOutDate;
    }

    public void setCheckOutDate(long checkOutDate) {
        this.checkOutDate = checkOutDate;
    }

    public long getReservationDate() {
        return reservationDate;
    }

    public void setReservationDate(long reservationDate) {
        this.reservationDate = reservationDate;
    }

    public int getReserveNumPerson() {
        return reserveNumPerson;
    }

    public void setReserveNumPerson(int reserveNumPerson) {
        this.reserveNumPerson = reserveNumPerson;
    }

    public String getReserveAccomPlace() {
        return reserveAccomPlace;
    }

    public void setReserveAccomPlace(String reserveAccomPlace) {
        this.reserveAccomPlace = reserveAccomPlace;
    }

    public String getReserveClientName() {
        return reserveClientName;
    }

    public void setReserveClientName(String reserveClientName) {
        this.reserveClientName = reserveClientName;
    }

    public double getReservationAmt() {
        return reservationAmt;
    }

    public void setReservationAmt(double reservationAmt) {
        this.reservationAmt = reservationAmt;
    }
}
