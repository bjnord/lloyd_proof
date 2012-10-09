// Copyright © 2012 by Brent J. Nordquist. Some Rights Reserved.
// This work is licensed under http://creativecommons.org/licenses/by-sa/3.0/

package net.nordist.lloydproof;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;

public class SettingsActivity extends PreferenceActivity
    implements OnSharedPreferenceChangeListener
{
    private static final String TAG = "SettingsActivity";
    private static final String KEY_SERVER_URL = "pref_server_url";

    private SettingsFragment settingsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        settingsFragment = new SettingsFragment();
        getFragmentManager().beginTransaction()
            .replace(android.R.id.content, settingsFragment).commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initializeSummaries();
        PreferenceManager.getDefaultSharedPreferences(this)
            .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        PreferenceManager.getDefaultSharedPreferences(this)
            .unregisterOnSharedPreferenceChangeListener(this);
    }

    private void initializeSummaries() {
        SharedPreferences defaultSharedPreferences =
            PreferenceManager.getDefaultSharedPreferences(this);
        String value = defaultSharedPreferences.getString(KEY_SERVER_URL, "");
        settingsFragment.findPreference(KEY_SERVER_URL).setSummary(value);
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(KEY_SERVER_URL)) {
            String newValue = sharedPreferences.getString(key, "");
            Log.d(TAG, "new " + key + " = " + newValue);
            settingsFragment.findPreference(key).setSummary(newValue);
        }
    }
}
