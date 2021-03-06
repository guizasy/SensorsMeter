package com.taberu.sensorsmeter;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

// https://developer.android.com/training/location/retrieve-current.html
// https://github.com/googlesamples/android-RuntimePermissions/blob/master/Application/src/main/java/com/example/android/system/runtimepermissions/MainActivity.java
// https://inthecheesefactory.com/blog/things-you-need-to-know-about-android-m-permission-developer-edition/en

public class SensorsService extends Service implements SensorEventListener {
    private final String TAG = "SensorsService";
    private final static float ALPHA = 0.1f;
    private float[] output = new float[3];
    private SensorManager mSensorManager = null;
    private Sensor mSensorAccelerometer = null;
    LocationManager mLocationManager;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }

    @Override
    public void onCreate() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = new SensorLocationListener();
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10, locationListener);

        // Output value initialization
        output[0] = 0.0f;
        output[1] = 0.0f;
        output[2] = 0.0f;

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensorAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        mSensorManager.registerListener(this, mSensorAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onDestroy() {
        if (mSensorManager != null && mSensorAccelerometer != null)
            mSensorManager.unregisterListener(this);
    }

    private void saveAccelerometerData(String timestamp, double input) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        Location location = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        double longitude = location.getLongitude();
        double latitude = location.getLatitude();

        String strRadio = timestamp + "," + Double.toString(input) + "," + Double.toString(latitude) + "," + Double.toString(longitude) + "\n";

        FileServices fs = new FileServices();
        fs.appendFile(strRadio);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() != Sensor.TYPE_LINEAR_ACCELERATION)
            return;

        Log.i(TAG, String.format("Input[0]: %f Input[1]: %f Input[2]: %f", sensorEvent.values[0], sensorEvent.values[1], sensorEvent.values[2]));

        output[0] = output[0] + ALPHA * (sensorEvent.values[0] - output[0]);
        output[1] = output[1] + ALPHA * (sensorEvent.values[1] - output[1]);
        output[2] = output[2] + ALPHA * (sensorEvent.values[2] - output[2]);

        double gforce = Math.sqrt(output[0] * output[0] + output[1] * output[1] + output[2] * output[2]);
        Log.i(TAG, String.format("Output[0]: %f Output[1]: %f Output[2]: %f\n", output[0], output[1], output[2]));
        Log.i(TAG, String.format("G-Force: %f\n", gforce));

        if (gforce > 1.0f) {
            saveAccelerometerData(Double.toString(sensorEvent.timestamp), gforce);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        // do nothing
    }
}
