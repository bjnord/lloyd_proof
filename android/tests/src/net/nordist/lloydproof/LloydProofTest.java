package net.nordist.lloydproof;

import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.content.Context;
import android.test.ActivityInstrumentationTestCase2;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.EditText;

public class LloydProofTest extends ActivityInstrumentationTestCase2<LloydProof>
{
    private LloydProof activity;
    private KeyguardLock keyguardLock;

    public LloydProofTest() {
        super("net.nordist.lloydproof", LloydProof.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        activity = getActivity();
        KeyguardManager keyGuardManager = (KeyguardManager)activity.getApplicationContext().getSystemService(Context.KEYGUARD_SERVICE);
        keyguardLock = keyGuardManager.newKeyguardLock("LloydProof");
        keyguardLock.disableKeyguard();
    }

    public void testKeyingAndSavingCorrection() {
        final EditText editText = (EditText)activity.findViewById(R.id.current_text);
        activity.runOnUiThread(
            new Runnable() {
                public void run() {
                  editText.requestFocus();
                }
            }
        );
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
        assertEquals("", editText.getText().toString());
    }
}
