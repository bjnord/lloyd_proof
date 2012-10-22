// Copyright © 2012 by Brent J. Nordquist. Some Rights Reserved.
// This work is licensed under http://creativecommons.org/licenses/by-sa/3.0/

package net.nordist.lloydproof;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CorrectionUploader extends AsyncTask<Void, Void, Void>
{
    private static final String TAG = "CorrectionUploader";

    private CorrectionStorage store;
    private CorrectionUploadObserver observer;
    private Settings settings;
    private int uploadedCount;
    private String failureMessage;
    private HttpJSONClient uploadClient;

    public CorrectionUploader(Context context, CorrectionUploadObserver observer) {
        super();
        this.observer = observer;
        store = new CorrectionStorage(context);
        settings = new Settings(context);
    }

    @Override
    protected void onPreExecute() {
        observer.uploadStart();
    }

    @Override
    @SuppressWarnings("PMD.AvoidCatchingGenericException")  // intentionally handling multiple
    protected Void doInBackground(Void... params) {
        try {
            JSONObject correctionsJSON = createCorrectionsJSON();
            JSONArray statusJSON = uploadCorrectionsJSON(correctionsJSON);
            deleteCorrectionsWithSuccessfulStatus(statusJSON);
        } catch (Exception e) {
            failureMessage = e.getClass().getSimpleName() + ": " + e.getMessage();
            Log.e(TAG, failureMessage);
            cancel(true);
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        observer.uploadSuccess(uploadedCount);
        observer.uploadStop();
    }

    @Override
    protected void onCancelled(Void result) {
        observer.uploadFailure(failureMessage);
        observer.uploadStop();
    }

    protected JSONObject createCorrectionsJSON() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("corrections", store.getAllAsJSONArray());
        Log.d(TAG, "corrections JSON: " + json.toString());
        return json;
    }

    protected JSONArray uploadCorrectionsJSON(JSONObject json)
            throws UnsupportedEncodingException, IOException, JSONException {
        sendCorrectionsRequest(json);
        cancelOnCorrectionsResponseFailure();
        if (isCancelled()) {
            return new JSONArray();
        }
        return parseCorrectionsResponse();
    }

    protected void sendCorrectionsRequest(JSONObject json) throws IOException, JSONException {
        String url = settings.getString(Settings.SERVER_URL) + "corrections/upload.json";
        uploadClient = new HttpJSONClient(url);
        uploadClient.sendRequest(json);
    }

    protected void cancelOnCorrectionsResponseFailure() {
        if (uploadClient.requestFailed()) {
            failureMessage = uploadClient.failureMessage();
            Log.e(TAG, failureMessage);
            cancel(true);
        }
    }

    protected JSONArray parseCorrectionsResponse()
            throws UnsupportedEncodingException, IOException, JSONException {
        JSONObject statusJSON = uploadClient.parseResponse();
        Log.d(TAG, "status JSON: " + statusJSON.toString());
        return (JSONArray)statusJSON.get("upload_status");
    }

    protected void deleteCorrectionsWithSuccessfulStatus(JSONArray statusJSON)
            throws JSONException {
        if (isCancelled()) {
            return;
        }
        List<Integer> idsToDelete = new ArrayList<Integer>();
        for (int i = 0; i < statusJSON.length(); i++) {
            JSONObject status = statusJSON.getJSONObject(i);
            if (status.getString("status").equals("ok")) {
                idsToDelete.add(status.getInt("sync_id"));
            }
        }
        uploadedCount = store.deleteByIdList(idsToDelete);
    }
}
