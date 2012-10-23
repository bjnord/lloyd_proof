// Copyright © 2012 by Brent J. Nordquist. Some Rights Reserved.
// This work is licensed under http://creativecommons.org/licenses/by-sa/3.0/

package net.nordist.lloydproof;

import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.content.Context;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.Button;
import android.widget.EditText;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/*
 * NB: setUp() deletes all stored corrections; each test is expected
 * to clean up after itself and leave the storage empty again when done
 * (to avoid failures from indeterminate test order).
 */
public class LloydProofTest extends ActivityInstrumentationTestCase2<LloydProof>
{
    private LloydProof activity;
    private CorrectionStorage store;  // FIXME figure out how to mock
    private KeyguardLock keyguardLock;

    public LloydProofTest() {
        super("net.nordist.lloydproof", LloydProof.class);
    }

    @Override
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")  // it's super's fault
    public void setUp() throws Exception {
        super.setUp();
        activity = getActivity();
        store = new CorrectionStorage(activity);
        store.deleteAll();
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
        } catch (InterruptedException e) {
            // nothing meaningful we can do
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
        store.deleteAll();
    }

    // FIXME DummyRequester and CorrectionUploader tests should be in separate class

    private class DummyRequester implements JSONRequester {
        private List<Integer> successfulIds = new ArrayList<Integer>();
        private List<Integer> failedIds = new ArrayList<Integer>();
        private boolean failRequest;

        public DummyRequester(boolean failRequest) {
            this.failRequest = failRequest;
        }

        public void sendRequest(JSONObject json) throws IOException, JSONException {
            if (failRequest) {
                return;
            }
            JSONArray correctionJSON = (JSONArray)json.get("corrections");
            for (int i = 0; i < correctionJSON.length(); i++) {
                JSONObject correction = correctionJSON.getJSONObject(i);
                if (correction.getString("current_text").equals("FAIL-ME")) {
                    failedIds.add(correction.getInt("sync_id"));
                } else {
                    successfulIds.add(correction.getInt("sync_id"));
                }
            }
        }

        public boolean requestFailed() {
            return failRequest;
        }

        public String failureMessage() {
            return failRequest ? "simulated failure" : null;
        }

        public JSONObject parseResponse()
                throws UnsupportedEncodingException, JSONException, IOException {

            JSONObject jsonParent = new JSONObject();
            JSONArray jsonArray = new JSONArray();
            Iterator<Integer> i = successfulIds.iterator();
            while (i.hasNext()) {
                JSONObject jsonItem = new JSONObject();
                jsonItem.put("sync_id", i.next().intValue());
                jsonItem.put("status", "ok");
                jsonArray.put(jsonItem);
            }
            i = failedIds.iterator();
            while (i.hasNext()) {
                JSONObject jsonItem = new JSONObject();
                jsonItem.put("sync_id", i.next().intValue());
                jsonItem.put("status", "error");
                // FIXME should add "errors" array also
                jsonArray.put(jsonItem);
            }
            jsonParent.put("upload_status", jsonArray);
            return jsonParent;
        }
    }

    public void testUploadWhenAllCorrectionsSucceed() {
        givenSomeValidStoredCorrections();
        final JSONRequester requester = new DummyRequester(false);
        final CorrectionUploader uploader = new CorrectionUploader(activity, requester);
        activity.runOnUiThread(
            new Runnable() {
                public void run() {
                    uploader.execute();
                }
            }
        );
        settleWait();
        assertEquals(0, store.count());
    }

    public void givenSomeValidStoredCorrections() {
        store.save("strah");
        store.save("woold");
        store.save("brikk");
        assertEquals(3, store.count());
    }
}
