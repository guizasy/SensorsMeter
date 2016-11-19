package com.taberu.sensorsmeter;


import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.support.v7.app.ActionBar;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.text.TextUtils;
import android.view.MenuItem;
import android.support.v4.app.NavUtils;
import android.widget.Button;

import java.util.List;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends AppCompatPreferenceActivity {
    public static final String KEY_PREF_ENABLE_COLLECT = "enable_collect";

    SharedPreferences.OnSharedPreferenceChangeListener listener =
            new SharedPreferences.OnSharedPreferenceChangeListener() {
                public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
                    if (key.equals(KEY_PREF_ENABLE_COLLECT)) {
                        Preference connectionPref = findPreference(key);
                        Button sendButton = (Button) findViewById(R.id.BtnSend);

                        if (connectionPref.isEnabled()) {
                            sendButton.setEnabled(true);
                        } else {
                            sendButton.setEnabled(false);
                        }
                    }
                }
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new GeneralPreferenceFragment())
                .commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(listener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(listener);
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            if (!super.onMenuItemSelected(featureId, item)) {
                NavUtils.navigateUpFromSameTask(this);
            }
            return true;
        }
        return super.onMenuItemSelected(featureId, item);
    }

    /**
     * This method stops fragment injection in malicious applications.
     * Make sure to deny any unknown fragments here.
     */
    protected boolean isValidFragment(String fragmentName) {
        return GeneralPreferenceFragment.class.getName().equals(fragmentName);
    }

//    @Override
//    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
//        if (key.equals(KEY_PREF_SYNC_CONN)) {
//            Preference connectionPref = findPreference(key);
//            // Set summary to be the user-description for the selected value
//            connectionPref.setSummary(sharedPreferences.getString(key, ""));
//        }
//    }

//    private static SharedPreferences.OnSharedPreferenceChangeListener sBindPreferenceSummaryToValueListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
//        @Override
//        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
//            EditTextPreference pref = (EditTextPreference)findPreference(getString(R.string.pref1));
//            pref1.setOnPreferenceChangeListener(changeListener);
//
//            EditTextPreference pref2 = (EditTextPreference)findPreference(getString(R.string.pref2));
//            pref2.setOnPreferenceChangeListener(changeListener);
//        }

//        @Override
//        public boolean onSharedPreferenceChanged(Preference preference, Object value) {
//            String stringValue = value.toString();
//
//            if (preference instanceof ListPreference) {
//                // For list preferences, look up the correct display value in
//                // the preference's 'entries' list.
//                ListPreference listPreference = (ListPreference) preference;
//                int index = listPreference.findIndexOfValue(stringValue);
//
//                // Set the summary to reflect the new value.
//                preference.setSummary(
//                        index >= 0
//                                ? listPreference.getEntries()[index]
//                                : null);
//
////            } else if (preference instanceof RingtonePreference) {
////                // For ringtone preferences, look up the correct display value
////                // using RingtoneManager.
////                if (TextUtils.isEmpty(stringValue)) {
////                    // Empty values correspond to 'silent' (no ringtone).
////                    preference.setSummary(R.string.pref_ringtone_silent);
////
////                } else {
////                    Ringtone ringtone = RingtoneManager.getRingtone(
////                            preference.getContext(), Uri.parse(stringValue));
////
////                    if (ringtone == null) {
////                        // Clear the summary if there was a lookup error.
////                        preference.setSummary(null);
////                    } else {
////                        // Set the summary to reflect the new ringtone display
////                        // name.
////                        String name = ringtone.getTitle(preference.getContext());
////                        preference.setSummary(name);
////                    }
////                }
//            } else {
//                // For all other preferences, set the summary to the value's
//                // simple string representation.
//                preference.setSummary(stringValue);
//            }
//            return true;
//        }
//    };

//    private static void bindPreferenceSummaryToValue(Preference preference) {
//        // Set the listener to watch for value changes.
//        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);
//
//        // Trigger the listener immediately with the preference's
//        // current value.
//        sBindPreferenceSummaryToValueListener.onSharedPreferenceChanged(preference,
//                PreferenceManager
//                        .getDefaultSharedPreferences(preference.getContext())
//                        .getString(preference.getKey(), ""));
//    }

//    @Override
//    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
////        Preferences.sync(getPreferenceManager(), key);
//
//        if (key.equals(KEY_PREF_ENABLE_COLLECT)) {
//            Preference connectionPref = findPreference(key);
//            Button sendButton = (Button) findViewById(R.id.BtnSend);
//
//            if (connectionPref.isEnabled()) {
//                sendButton.setEnabled(true);
//            } else {
//                sendButton.setEnabled(false);
//            }
//            // Set summary to be the user-description for the selected value
//            connectionPref.setSummary(sharedPreferences.getString(key, ""));
//        }
//    }

    /**
     * This fragment shows general preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class GeneralPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
        }
    }
}
