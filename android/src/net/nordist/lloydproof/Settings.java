// Copyright © 2012 by Brent J. Nordquist. Some Rights Reserved.
// This work is licensed under http://creativecommons.org/licenses/by-sa/3.0/

package net.nordist.lloydproof;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Settings
{
    public static final String SERVER_URL = "pref_server_url";

    private static Settings instance;
    private SharedPreferences sharedPreferences;

    public Settings(Context applicationContext) {
        if (instance == null) {
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext);
            instance = this;
        }
    }

    public static String getString(String key) {
        return instance.sharedPreferences.getString(key, "");
    }
}
