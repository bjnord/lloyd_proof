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
    private Context context;
    private Resources resources;

    public CorrectionUploader(Context pContext) {
        super();
        context = pContext;
        resources = context.getResources();
        store = new CorrectionStorage(context);
    }

    @Override
    protected void onPreExecute() {
        Log.d(TAG, "onPreExecute() fired");
        // FIXME gray out Upload button
    }

    // FIXME refactor
    @Override
    protected Void doInBackground(Void... params) {
        try {
            JSONObject upload_json = new JSONObject();
            upload_json.put("corrections", store.getAllAsJsonArray());
            Log.d(TAG, "upload JSON: " + upload_json.toString());
            JSONArray status_json = this.upload(upload_json);
            Log.d(TAG, "status JSON: " + status_json.toString());
            // FIXME get return value (number uploaded)
            store.deleteByJsonArrayStatus(status_json);
        } catch (JSONException jex) {
            Log.e(TAG, "error constructing JSON: " + jex.getMessage());
            this.cancel(true);
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        Log.d(TAG, "onPostExecute() fired");
        // FIXME reenable Upload button
    }

    @Override
    protected void onCancelled(Void result) {
        Log.d(TAG, "onCancelled() fired");
        // FIXME reenable Upload button
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
            if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                byte[] body = EntityUtils.toByteArray(httpResponse.getEntity());
                statusJson = new JSONArray(new String(body, "UTF-8"));
            }
            Log.e(TAG, "HTTP " + statusLine.getStatusCode() + " " +
                statusLine.getReasonPhrase());
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, "UEE " + e.getMessage());
        } catch (IOException e) {
            Log.e(TAG, "IOE " + e.getMessage());
        } catch (JSONException e) {
            Log.e(TAG, "JE " + e.getMessage());
        }
        return statusJson;
    }
}
