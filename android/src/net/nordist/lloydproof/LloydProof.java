package net.nordist.lloydproof;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LloydProof extends Activity implements CorrectionUploadObserver
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
        this.updateUploadStatus();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
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
        this.updateUploadStatus();
    }

    public void uploadCorrections(View view) {
        uploader = new CorrectionUploader(this, this);
        uploader.execute();
    }

    private void showStatus(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    private void updateUploadStatus() {
        TextView upload_status = (TextView)findViewById(R.id.upload_status);
        int count = store.count();
        if (count > 0) {
            upload_status.setText(this.pluralCorrectionCount(count) + " " +
                resources.getString(R.string.to_upload));
        } else {
            upload_status.setText(resources.getString(R.string.all_uploaded));
        }
    }

    private String pluralCorrectionCount(int count) {
        // FIXME do this with %s so l10n can change the word order too
        return resources.getQuantityString(R.plurals.corrections, count, count);
    }

    public void uploadStart() {
        findViewById(R.id.upload_button).setEnabled(false);
    }

    public void uploadSuccess(int count) {
        Log.d(TAG, "uploadSuccess(" + count + ")");
        this.showStatus(resources.getString(R.string.uploaded) + " " +
            this.pluralCorrectionCount(count) + ".");
        this.updateUploadStatus();
    }

    public void uploadFailure(String message) {
        Log.d(TAG, "uploadFailure(" + message + ")");
        this.showStatus(message);
        this.updateUploadStatus();
    }

    public void uploadStop() {
        findViewById(R.id.upload_button).setEnabled(true);
    }

    public void about(MenuItem item) {
        Log.d(TAG, "about() fired");
    }
}
