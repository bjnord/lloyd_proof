// Copyright © 2012 by Brent J. Nordquist. Some Rights Reserved.
// This work is licensed under http://creativecommons.org/licenses/by-sa/3.0/

package net.nordist.lloydproof;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.preference.PreferenceFragment;
import android.util.Log;

public class SettingsFragment extends PreferenceFragment
{
    private static final String TAG = "SettingsFragment";

    private SharedPreferences sharedPreferences;

    public SettingsFragment(Context context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }

    // NOTE this only supports String/EditText preferences (all we have, currently)
    public void updateSummary(SharedPreferences sharedPreferences, String key) {
        String value = sharedPreferences.getString(key, "");
        Log.d(TAG, "new " + key + " = " + value);
        findPreference(key).setSummary(value);
    }
}
