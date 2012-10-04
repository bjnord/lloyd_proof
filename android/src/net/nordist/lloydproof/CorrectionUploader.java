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
            String correctionsJSON = this.createCorrectionsJSON();
            JSONArray statusJSON = this.uploadCorrectionsJSON(correctionsJSON);
            uploadedCount = store.deleteByJsonArrayStatus(statusJSON);
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

    protected String createCorrectionsJSON() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("corrections", store.getAllAsJsonArray());
        String jsonString = json.toString();
        Log.d(TAG, "corrections JSON: " + jsonString);
        return jsonString;
    }

    protected JSONArray uploadCorrectionsJSON(String jsonString)
            throws UnsupportedEncodingException, IOException, JSONException {
        HttpResponse httpResponse = this.sendCorrectionsHttpRequest(jsonString);
        this.cancelOnHttpResponseFailure(httpResponse);
        if (this.isCancelled()) {
            return new JSONArray();
        }
        return this.parseCorrectionsHttpResponse(httpResponse);
    }

    protected HttpResponse sendCorrectionsHttpRequest(String jsonString)
            throws UnsupportedEncodingException, IOException {
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpRequest = new HttpPost(BASE_URL + "corrections/sync.json");
        StringEntity entity = new StringEntity(jsonString, HTTP.UTF_8);
        entity.setContentType("application/json");
        httpRequest.setEntity(entity);
        return httpClient.execute(httpRequest);
    }

    protected void cancelOnHttpResponseFailure(HttpResponse httpResponse) {
        StatusLine statusLine = httpResponse.getStatusLine();
        if (statusLine.getStatusCode() != HttpStatus.SC_OK) {
            failureMessage = "HTTP " + statusLine.getStatusCode() + " " +
                statusLine.getReasonPhrase();
            Log.e(TAG, failureMessage);
            this.cancel(true);
        }
    }

    protected JSONArray parseCorrectionsHttpResponse(HttpResponse httpResponse)
            throws UnsupportedEncodingException, IOException, JSONException {
        byte[] body = EntityUtils.toByteArray(httpResponse.getEntity());
        JSONArray statusJSON = new JSONArray(new String(body, "UTF-8"));
        Log.d(TAG, "status JSON: " + statusJSON.toString());
        return statusJSON;
    }
}
