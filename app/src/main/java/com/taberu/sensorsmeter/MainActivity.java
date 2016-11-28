package com.taberu.sensorsmeter;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

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

    private static final int REQUEST_ACCESS_FINE_LOCATION = 0;
    private static final int REQUEST_WRITE_EXTERNAL_STORAGE = 1;
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

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissionGPS();
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissionExternalStorage();
        }

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
//                    Toast.makeText(getApplicationContext(),
//                            "Send is enabled",
//                            Toast.LENGTH_SHORT).show();
                } else {
                    stopSensorsService();
//                    Toast.makeText(getApplicationContext(),
//                            "Send is disabled",
//                            Toast.LENGTH_SHORT).show();
                }
            }

        });
    }

    private void requestPermissionExternalStorage() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_EXTERNAL_STORAGE);
    }

    private void requestPermissionGPS() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // For example if the user has previously denied the permission.
            Log.i("<<GUILHERME>>", "Displaying GPS permission rationale to provide additional context.");
            Snackbar.make(mLayout, R.string.permission_fine_rationale,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction("Ok", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    REQUEST_ACCESS_FINE_LOCATION);
                        }
                    })
                    .show();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_ACCESS_FINE_LOCATION);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.menu_settings) {
            Intent myIntent = new Intent(MainActivity.this, SettingsActivity.class);
            MainActivity.this.startActivity(myIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
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
        fs.clearFiles(context);

        Toast.makeText(context,
                "Deleting CSV Files",
                Toast.LENGTH_SHORT).show();
    }
}
