package net.nordist.lloydproof;

import android.content.Context;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.util.Log;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CorrectionUploader extends AsyncTask<Void, Void, Void>
{
    private final String TAG = this.getClass().getSimpleName();
    private final String BASE_URL = "http://10.0.2.2:3000/";

    private CorrectionStorage store;
    private CorrectionUploadObserver observer;
    private int uploadedCount;
    private String failureMessage;
    // FIXME RF context and resources aren't used outside of constructor
    private Context context;
    private Resources resources;

    public CorrectionUploader(Context pContext, CorrectionUploadObserver pObserver) {
        super();
        context = pContext;
        observer = pObserver;
        uploadedCount = 0;
        failureMessage = "";
        resources = context.getResources();
        store = new CorrectionStorage(context);
    }

    @Override
    protected void onPreExecute() {
        Log.d(TAG, "onPreExecute() fired");
        observer.uploadStart();
    }

    // FIXME refactor
    @Override
    protected Void doInBackground(Void... params) {
        try {
            JSONObject correctionsJSON = this.createCorrectionsJSON();
            JSONArray status_json = this.upload(correctionsJSON);
            Log.d(TAG, "status JSON: " + status_json.toString());
            uploadedCount = store.deleteByJsonArrayStatus(status_json);
        } catch (Exception e) {
            failureMessage = e.getClass().getSimpleName() + ": " + e.getMessage();
            Log.e(TAG, failureMessage);
            this.cancel(true);
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        Log.d(TAG, "onPostExecute() fired");
        observer.uploadSuccess(uploadedCount);
        observer.uploadStop();
    }

    @Override
    protected void onCancelled(Void result) {
        Log.d(TAG, "onCancelled() fired");
        observer.uploadFailure(failureMessage);
        observer.uploadStop();
    }

    protected JSONObject createCorrectionsJSON() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("corrections", store.getAllAsJsonArray());
        Log.d(TAG, "corrections JSON: " + json.toString());
        return json;
    }

    // FIXME refactor
    protected JSONArray upload(JSONObject json) {
        String url = BASE_URL + "corrections/sync.json";
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpRequest = new HttpPost(url);
        JSONArray statusJson = new JSONArray();
        try {
            StringEntity entity = new StringEntity(json.toString(), HTTP.UTF_8);
            entity.setContentType("application/json");
            httpRequest.setEntity(entity);
            HttpResponse httpResponse = httpClient.execute(httpRequest);
            StatusLine statusLine = httpResponse.getStatusLine();
            String statusString = "HTTP " + statusLine.getStatusCode() + " " +
                statusLine.getReasonPhrase();
            if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                Log.d(TAG, statusString);
                byte[] body = EntityUtils.toByteArray(httpResponse.getEntity());
                statusJson = new JSONArray(new String(body, "UTF-8"));
            } else {
                failureMessage = statusString;
                Log.e(TAG, failureMessage);
                this.cancel(true);
            }
        } catch (Exception e) {
            failureMessage = e.getClass().getSimpleName() + ": " + e.getMessage();
            Log.e(TAG, failureMessage);
            this.cancel(true);
        }
        return statusJson;
    }
}
