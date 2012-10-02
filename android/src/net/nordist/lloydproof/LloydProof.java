package net.nordist.lloydproof;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LloydProof extends Activity
{
    private final String TAG = this.getClass().getSimpleName();

    private CorrectionStorage store;
    private CorrectionUploader uploader;
    private Context context;
    private Resources resources;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getApplicationContext();
        resources = context.getResources();
        store = new CorrectionStorage(this);
        uploader = null;
        setContentView(R.layout.main);
        this.updateSyncStatus();
    }

    public void saveCorrection(View view) {
        EditText edit_text = (EditText)findViewById(R.id.current_text);
        String current_text = edit_text.getText().toString();
        if (current_text.isEmpty()) {
            Log.d(TAG, "not saving empty correction");
            return;
        }
        int id = store.save(current_text);
        if (id > 0) {
            Log.d(TAG, "saved correction as id=" + id);
            edit_text.setText("", TextView.BufferType.EDITABLE);
            this.showStatus(resources.getString(R.string.correction_saved));
        } else {
            Log.e(TAG, "error saving correction");
            this.showStatus(resources.getString(R.string.correction_save_error));
        }
        this.updateSyncStatus();
    }

    public void uploadCorrections(View view) {
        uploader = new CorrectionUploader(this);
        uploader.execute();
    }

    private void showStatus(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    private void updateSyncStatus() {
        TextView sync_status = (TextView)findViewById(R.id.sync_status);
        int count = store.count();
        if (count > 0) {
            String plural_count = resources.getQuantityString(R.plurals.corrections, count, count);
            sync_status.setText(plural_count + " " + resources.getString(R.string.to_upload));
        } else {
            sync_status.setText(resources.getString(R.string.all_uploaded));
        }
    }
}
