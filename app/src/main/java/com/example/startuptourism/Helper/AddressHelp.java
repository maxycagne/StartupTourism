package com.example.startuptourism.Helper;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;

public class AddressHelp {
    public static String getAddress(Address address) {
        String disp = "";
        if (address.getLocality() != null)
            disp += (", " + address.getLocality());
        if (address.getSubAdminArea() != null)
            disp += (", " + address.getSubAdminArea());
        if (address.getAdminArea() != null)
            disp += (", " + address.getAdminArea());
        if (address.getCountryName() != null)
            disp += (", " + address.getCountryName());
        return disp.substring(2);
    }

    public static Address getAddress(Context context, LatLng latLng) {
        Geocoder geocoder = new Geocoder(context);
        try {
            Address address = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1).get(0);
            if (address == null) {
                Toast.makeText(context, "No address retrieved", Toast.LENGTH_SHORT).show();
                return null;
            }
            return address;
        } catch (IOException e) {
            Toast.makeText(context, "Error occurred: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            return null;
        }
    }
}
