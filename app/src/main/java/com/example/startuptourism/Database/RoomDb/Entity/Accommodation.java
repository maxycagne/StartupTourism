package com.example.startuptourism.Database.RoomDb.Entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;


@Entity
public class Accommodation implements Serializable {
    @PrimaryKey
    @NonNull
    private String accomId;
    private String accomUsername, accomName, accomAddress, accomType, accomDesc;
    private int accomPax, accomVerified, accomMinHours;
    private double accomHourRate;
    private double accomLat, accomLng;
    private String imgPath1, imgPath2, imgPath3, imgPath4, imgPath5;

    @Ignore
    public Accommodation() {
    }

    public Accommodation(@NonNull String accomId, String accomUsername, String accomName, String accomAddress, String accomType, String accomDesc, int accomPax, int accomVerified, int accomMinHours, double accomHourRate, double accomLat, double accomLng, String imgPath1, String imgPath2, String imgPath3, String imgPath4, String imgPath5) {
        this.accomId = accomId;
        this.accomUsername = accomUsername;
        this.accomName = accomName;
        this.accomAddress = accomAddress;
        this.accomType = accomType;
        this.accomDesc = accomDesc;
        this.accomPax = accomPax;
        this.accomVerified = accomVerified;
        this.accomMinHours = accomMinHours;
        this.accomHourRate = accomHourRate;
        this.accomLat = accomLat;
        this.accomLng = accomLng;
        this.imgPath1 = imgPath1;
        this.imgPath2 = imgPath2;
        this.imgPath3 = imgPath3;
        this.imgPath4 = imgPath4;
        this.imgPath5 = imgPath5;
    }

    @NonNull
    public String getAccomId() {
        return accomId;
    }

    public void setAccomId(@NonNull String accomId) {
        this.accomId = accomId;
    }

    public String getAccomUsername() {
        return accomUsername;
    }

    public void setAccomUsername(String accomUsername) {
        this.accomUsername = accomUsername;
    }

    public String getAccomName() {
        return accomName;
    }

    public void setAccomName(String accomName) {
        this.accomName = accomName;
    }

    public String getAccomAddress() {
        return accomAddress;
    }

    public void setAccomAddress(String accomAddress) {
        this.accomAddress = accomAddress;
    }

    public String getAccomType() {
        return accomType;
    }

    public void setAccomType(String accomType) {
        this.accomType = accomType;
    }

    public String getAccomDesc() {
        return accomDesc;
    }

    public void setAccomDesc(String accomDesc) {
        this.accomDesc = accomDesc;
    }

    public int getAccomPax() {
        return accomPax;
    }

    public void setAccomPax(int accomPax) {
        this.accomPax = accomPax;
    }

    public int getAccomVerified() {
        return accomVerified;
    }

    public void setAccomVerified(int accomVerified) {
        this.accomVerified = accomVerified;
    }

    public double getAccomLat() {
        return accomLat;
    }

    public void setAccomLat(double accomLat) {
        this.accomLat = accomLat;
    }

    public double getAccomLng() {
        return accomLng;
    }

    public void setAccomLng(double accomLng) {
        this.accomLng = accomLng;
    }

    public String getImgPath1() {
        return imgPath1;
    }

    public void setImgPath1(String imgPath1) {
        this.imgPath1 = imgPath1;
    }

    public String getImgPath2() {
        return imgPath2;
    }

    public void setImgPath2(String imgPath2) {
        this.imgPath2 = imgPath2;
    }

    public String getImgPath3() {
        return imgPath3;
    }

    public void setImgPath3(String imgPath3) {
        this.imgPath3 = imgPath3;
    }

    public String getImgPath4() {
        return imgPath4;
    }

    public void setImgPath4(String imgPath4) {
        this.imgPath4 = imgPath4;
    }

    public String getImgPath5() {
        return imgPath5;
    }

    public void setImgPath5(String imgPath5) {
        this.imgPath5 = imgPath5;
    }

    public int getAccomMinHours() {
        return accomMinHours;
    }

    public void setAccomMinHours(int accomMinHours) {
        this.accomMinHours = accomMinHours;
    }

    public double getAccomHourRate() {
        return accomHourRate;
    }

    public void setAccomHourRate(double accomHourRate) {
        this.accomHourRate = accomHourRate;
    }
}

