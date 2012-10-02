package net.nordist.lloydproof;

import android.content.Context;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;

public class CorrectionUploader extends AsyncTask<Void, Void, Void>
{
    private final String TAG = this.getClass().getSimpleName();

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

    @Override
    protected Void doInBackground(Void... params) {
        try {
            JSONArray correction_json = store.getAllAsJsonArray();
            Log.d(TAG, "upload JSON: " + correction_json.toString());
            JSONArray status_json = this.uploadJson(correction_json);
            Log.d(TAG, "status JSON: " + status_json.toString());
            // FIXME delete from local DB based on status
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

    protected JSONArray uploadJson(JSONArray corrections) {
        return new JSONArray();
    }
}
