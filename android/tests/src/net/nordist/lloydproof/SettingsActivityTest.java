// Copyright © 2012 by Brent J. Nordquist. Some Rights Reserved.
// This work is licensed under http://creativecommons.org/licenses/by-sa/3.0/

package net.nordist.lloydproof;

import android.app.AlertDialog;
import android.app.KeyguardManager.KeyguardLock;
import android.app.KeyguardManager;
import android.content.Context;
import android.preference.EditTextPreference;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.EditText;

public class SettingsActivityTest extends ActivityInstrumentationTestCase2<SettingsActivity>
{
    private SettingsActivity activity;
    private Settings settings;
    private KeyguardLock keyguardLock;

    public SettingsActivityTest() {
        super("net.nordist.lloydproof", SettingsActivity.class);
    }

    @Override
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")  // it's super's fault
    public void setUp() throws Exception {
        super.setUp();
        activity = getActivity();
        settings = new Settings(activity);
        KeyguardManager keyGuardManager = (KeyguardManager)activity.getApplicationContext().getSystemService(Context.KEYGUARD_SERVICE);
        keyguardLock = keyGuardManager.newKeyguardLock("SettingsActivity");
        keyguardLock.disableKeyguard();
    }

    /*
     * The emulator seems to need a little time to settle at various points,
     * otherwise tests fail.
     */
    private void settleWait() {
        getInstrumentation().waitForIdleSync();
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            // nothing meaningful we can do
        }
    }

    public void testChangingServerURL() {
        // FIXME use class constant for "pref_server_url"
        final EditTextPreference serverURLPreference = (EditTextPreference)activity.getSettingsFragment().findPreference("pref_server_url");
        assertNotNull(serverURLPreference);
        activity.runOnUiThread(
            new Runnable() {
                public void run() {
                    // click "Server URL" to bring up the dialog
                    // FIXME magic constant 0 won't work when we have more than 1
                    final int SERVER_URL_POSITION = 0;
                    activity.getSettingsFragment().getPreferenceScreen().onItemClick(null, null, SERVER_URL_POSITION, 0);
                    // focus EditText box
                    final EditText serverURL = serverURLPreference.getEditText();
                    assertNotNull(serverURL);
                    serverURL.requestFocus();
                }
            }
        );
        settleWait();
        // FIXME should be "http:" but I can't get the shift key to work
        sendKeys("H T T P PERIOD SLASH SLASH 1 2 7 PERIOD 0 PERIOD 0 PERIOD 1 SLASH");
        activity.runOnUiThread(
            new Runnable() {
                public void run() {
                    // click "OK"
                    AlertDialog dialog = (AlertDialog)serverURLPreference.getDialog();
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).performClick();
                }
            }
        );
        settleWait();
        assertEquals("http.//127.0.0.1/", settings.getString(Settings.SERVER_URL));
    }
}
