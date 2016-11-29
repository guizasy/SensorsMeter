package com.taberu.sensorsmeter;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
    Settings:
        - Botao de enable/disable da coleta
            https://developer.android.com/guide/topics/ui/settings.html?hl=pt-br
            https://developer.android.com/reference/android/preference/Preference.html?hl=pt-br
            https://developer.android.com/training/basics/data-storage/shared-preferences.html?hl=pt-br
            https://developer.android.com/reference/android/content/SharedPreferences.html

    Ciclo de vida:
        - Estado do botao
            https://developer.android.com/training/basics/activity-lifecycle/recreating.html
            http://stackoverflow.com/questions/15313598/once-for-all-how-to-correctly-save-instance-state-of-fragments-in-back-stack
            https://inthecheesefactory.com/blog/fragment-state-saving-best-practices/en
            http://stackoverflow.com/questions/7383752/how-to-save-the-state-of-the-tooglebutton-on-off-selection
            http://stackoverflow.com/questions/28584177/how-to-save-switch-button-state-in-android

    Share file:
        - Enviar arquivo de coleta por email
            http://stackoverflow.com/questions/5843834/attaching-a-file-from-secure-storage-in-gmail-from-my-app
            https://developer.android.com/reference/android/support/v4/content/FileProvider.html
            https://developer.android.com/training/secure-file-sharing/setup-sharing.html
            https://developer.android.com/training/secure-file-sharing/share-file.html
            http://stackoverflow.com/questions/16581132/how-to-send-text-file-by-using-intent-in-android

 */

public class MainActivity extends AppCompatActivity {
    public static final String KEY_PREF_ENABLE_COLLECT = "enable_collect";
    SharedPreferences sharedPref;
    Button sendButton;
    Switch collectSwitch;

    final private int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 27;
    private View mLayout;

    private boolean isServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if ("com.taberu.sensorsmeter.SensorsService".equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public void startSensorsService() {
        Intent intent = new Intent(this, SensorsService.class);
        startService(intent);
    }

    public void stopSensorsService() {
        Intent intent = new Intent(this, SensorsService.class);
        stopService(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sendButton = (Button) findViewById(R.id.BtnSend);
        collectSwitch = (Switch) findViewById(R.id.SwitchColeta);
        mLayout = findViewById(R.id.content_main);

        requestAppPermission();

        collectSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences mSharedPref = getApplicationContext().getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = mSharedPref.edit();

                //commit prefs on change
                editor.putBoolean(KEY_PREF_ENABLE_COLLECT, isChecked);
                editor.apply();

                sendButton.setEnabled(collectSwitch.isChecked());
                if (collectSwitch.isChecked()) {
                    startSensorsService();
                } else {
                    stopSensorsService();
                }
            }

        });
    }

    private void requestAppPermission() {
        final List<String> permissionsList = new ArrayList<String>();

        addPermission(permissionsList, Manifest.permission.ACCESS_FINE_LOCATION);
        addPermission(permissionsList, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permissionsList.size() > 0) {
            ActivityCompat.requestPermissions(this, permissionsList.toArray(new String[permissionsList.size()]),
                    REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
        }
    }

    private boolean addPermission(List<String> permissionsList, String permission) {

        if (ActivityCompat.checkSelfPermission(getApplicationContext(), permission) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(permission);
        }

        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS:
            {
                Map<String, Integer> perms = new HashMap<String, Integer>();

                // Initial
                perms.put(Manifest.permission.ACCESS_FINE_LOCATION, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);

                // Fill with results
                for (int i = 0; i < permissions.length; i++)
                    perms.put(permissions[i], grantResults[i]);

                // Check for ACCESS_FINE_LOCATION
                if (perms.get(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        || perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    // Permission Denied
                    Toast.makeText(MainActivity.this, "Some Permission is Denied", Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            }
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Restore preferences
        sharedPref = getApplicationContext().getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE);
        collectSwitch.setChecked(sharedPref.getBoolean(KEY_PREF_ENABLE_COLLECT, false));

        if (isServiceRunning()) {
            Toast.makeText(getApplicationContext(),
                    "Service is running",
                    Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(),
                    "Service is not running",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle save) {
        super.onSaveInstanceState(save);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    public void shareFileOnClick(View view) {
        Context context = view.getContext();

        FileServices fs = new FileServices();
        fs.shareFiles(context);
    }

    public void deleteFileOnClick(View view) {
        Context context = view.getContext();

        FileServices fs = new FileServices();
        fs.clearFiles();

        Toast.makeText(context,
                "Deleting CSV Files",
                Toast.LENGTH_SHORT).show();
    }
}
