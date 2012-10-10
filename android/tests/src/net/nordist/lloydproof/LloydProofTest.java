// Copyright © 2012 by Brent J. Nordquist. Some Rights Reserved.
// This work is licensed under http://creativecommons.org/licenses/by-sa/3.0/

package net.nordist.lloydproof;

import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.content.Context;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.Button;
import android.widget.EditText;

public class LloydProofTest extends ActivityInstrumentationTestCase2<LloydProof>
{
    private LloydProof activity;
    private CorrectionStorage store;  // FIXME figure out how to mock
    private KeyguardLock keyguardLock;

    public LloydProofTest() {
        super("net.nordist.lloydproof", LloydProof.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        activity = getActivity();
        store = new CorrectionStorage(activity);
        KeyguardManager keyGuardManager = (KeyguardManager)activity.getApplicationContext().getSystemService(Context.KEYGUARD_SERVICE);
        keyguardLock = keyGuardManager.newKeyguardLock("LloydProof");
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
        } catch (Exception e) {
        }
    }

    public void testKeyingAndSavingCorrection() {
        int startCount = store.count();
        final EditText editText = (EditText)activity.findViewById(R.id.current_text);
        activity.runOnUiThread(
            new Runnable() {
                public void run() {
                    editText.requestFocus();
                }
            }
        );
        settleWait();
        sendKeys("D O U B L E COMMA SPACE D O U B L E COMMA SPACE");
        sendKeys("T O I L SPACE A N D SPACE B U B B L E");
        assertEquals("double, double, toil and bubble", editText.getText().toString());
        final Button saveButton = (Button)activity.findViewById(R.id.save_button);
        activity.runOnUiThread(
            new Runnable() {
                public void run() {
                    saveButton.performClick();
                }
            }
        );
        settleWait();
        assertEquals("", editText.getText().toString());
        assertEquals(startCount + 1, store.count());
    }
}
