package net.nordist.lloydproof;

import android.content.Context;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.util.Log;

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
    }

    @Override
    protected Void doInBackground(Void... params) {
        SystemClock.sleep(5000);  // FIXME also remove import
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        Log.d(TAG, "onPostExecute() fired");
    }

    @Override
    protected void onCancelled(Void result) {
        Log.d(TAG, "onCancelled() fired");
    }
}
