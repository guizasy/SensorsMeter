package com.taberu.sensorsmeter;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

import static android.support.v4.content.FileProvider.getUriForFile;

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


public class MainActivity extends AppCompatActivity implements SensorEventListener {
//    public static final String KEY_PREF_ENABLE_COLLECT = "enable_collect";
//    public static final String PREFS_NAME = "com.taberu.sensorsmeter.SettingsActivity";
//    SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
    Button sendButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sendButton = (Button) findViewById(R.id.BtnSend);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        sendButton.setEnabled(settings.getBoolean(KEY_PREF_ENABLE_COLLECT, false));
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

        //noinspection SimplifiableIfStatement

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

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    public void shareFileOnClick(View view) {
        Context context = view.getContext();
        Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND_MULTIPLE);
        ArrayList<Uri> uris = new ArrayList<>();
        File csvPath = new File(context.getFilesDir(), "csv");
        File shareFile = new File(csvPath, "driver_data.csv");
        Uri contentUri = getUriForFile(context, "com.taberu.sensorsmeter.fileprovider", shareFile);

        uris.add(contentUri);

        shareIntent.setType("text/plain");
        shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
        shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "CSV driver data");
        shareIntent.putExtra(android.content.Intent.EXTRA_EMAIL,
                new String[]{"email-address you want to send the file to"});

        try {
            context.startActivity(Intent.createChooser(shareIntent, "Email:")
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context,
                    "Sorry no email Application was found",
                    Toast.LENGTH_SHORT).show();
        }
    }

//    public void collectOnClick(View view) {
//        Context context = view.getContext();
//
//        sendButton.setEnabled(collectSwitch.isChecked());
//        if (collectSwitch.isChecked()) {
//            Toast.makeText(context,
//                    "Send is enabled",
//                    Toast.LENGTH_SHORT).show();
//        } else {
//            Toast.makeText(context,
//                    "Send is disabled",
//                    Toast.LENGTH_SHORT).show();
//        }
//    }

    public void deleteFileOnClick(View view) {

    }
}
