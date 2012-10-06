package net.nordist.lloydproof;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
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
    private static final int DIALOG_ABOUT = 0;

    private final String TAG = getClass().getSimpleName();

    private CorrectionStorage store;
    private CorrectionUploader uploader;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        store = new CorrectionStorage(this);
        setContentView(R.layout.main);
        updateUploadStatus();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public Dialog onCreateDialog(int id) {
        if (id != DIALOG_ABOUT) {
            return null;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.about_app_name);
        // FIXME set app version with %s; ditch app_name_and_version string
        builder.setView(getLayoutInflater().inflate(R.layout.about, null));
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        return builder.create();
    }

    public void saveCorrection(View view) {
        EditText editText = (EditText)findViewById(R.id.current_text);
        String currentText = editText.getText().toString();
        if (currentText.isEmpty()) {
            Log.d(TAG, "not saving empty correction");
            return;
        }
        int id = store.save(currentText);
        if (id > 0) {
            Log.d(TAG, "saved correction as id=" + id);
            editText.setText("", TextView.BufferType.EDITABLE);
            showStatus(getString(R.string.correction_saved));
        } else {
            Log.e(TAG, "error saving correction");
            showStatus(getString(R.string.correction_save_error));
        }
        updateUploadStatus();
    }

    public void uploadCorrections(View view) {
        uploader = new CorrectionUploader(this, this);
        uploader.execute();
    }

    private void showStatus(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void updateUploadStatus() {
        TextView uploadStatus = (TextView)findViewById(R.id.upload_status);
        int count = store.count();
        if (count > 0) {
            uploadStatus.setText(pluralCorrectionCount(count) + " " + getString(R.string.to_upload));
        } else {
            uploadStatus.setText(getString(R.string.all_uploaded));
        }
    }

    private String pluralCorrectionCount(int count) {
        return getResources().getQuantityString(R.plurals.corrections, count, count);
    }

    public void uploadStart() {
        findViewById(R.id.upload_button).setEnabled(false);
    }

    public void uploadSuccess(int count) {
        Log.d(TAG, "uploadSuccess(" + count + ")");
        showStatus(getString(R.string.uploaded_n_corrections, pluralCorrectionCount(count)));
        updateUploadStatus();
    }

    public void uploadFailure(String message) {
        Log.d(TAG, "uploadFailure(" + message + ")");
        showStatus(message);
        updateUploadStatus();
    }

    public void uploadStop() {
        findViewById(R.id.upload_button).setEnabled(true);
    }

    public void about(MenuItem item) {
        showDialog(DIALOG_ABOUT);
    }
}
