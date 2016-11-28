package com.taberu.sensorsmeter;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

/**
 * Created by taberu on 27/11/2016.
 */

public class SensorLocationListener implements LocationListener {
    private double longitude = 0.0;
    private double latitude = 0.0;


    @Override
    public void onLocationChanged(Location location) {
        longitude = location.getLongitude();
        latitude = location.getLatitude();
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }
}
