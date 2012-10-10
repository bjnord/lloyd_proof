// Copyright © 2012 by Brent J. Nordquist. Some Rights Reserved.
// This work is licensed under http://creativecommons.org/licenses/by-sa/3.0/

package net.nordist.lloydproof;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;

public class SettingsActivity extends PreferenceActivity
    implements OnSharedPreferenceChangeListener
{
    private static final String TAG = "SettingsActivity";

    private SharedPreferences defaultSharedPreferences;
    private SettingsFragment settingsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        settingsFragment = new SettingsFragment();
        getFragmentManager().beginTransaction()
            .replace(android.R.id.content, settingsFragment).commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initializeSummaries();
        defaultSharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        defaultSharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
    }

    private void initializeSummaries() {
        String serverURL = defaultSharedPreferences.getString(Settings.SERVER_URL, "");
        settingsFragment.findPreference(Settings.SERVER_URL).setSummary(serverURL);
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(Settings.SERVER_URL)) {
            String serverURL = sharedPreferences.getString(key, "");
            Log.d(TAG, "new " + key + " = " + serverURL);
            settingsFragment.findPreference(key).setSummary(serverURL);
        }
    }

    // TODO find a way to privatize this; only needed by test
    public SettingsFragment getSettingsFragment() {
        return settingsFragment;
    }
}
