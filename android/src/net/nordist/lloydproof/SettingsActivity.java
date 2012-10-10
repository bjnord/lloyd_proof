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
        settingsFragment = new SettingsFragment(this);
        getFragmentManager().beginTransaction()
            .replace(android.R.id.content, settingsFragment).commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        settingsFragment.updateSummaries();
        defaultSharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        defaultSharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        settingsFragment.updateSummary(sharedPreferences, key);
    }

    // TODO find a way to privatize this; only needed by test
    public SettingsFragment getSettingsFragment() {
        return settingsFragment;
    }
}
