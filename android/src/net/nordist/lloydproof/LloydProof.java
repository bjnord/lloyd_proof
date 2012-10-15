// Copyright © 2012 by Brent J. Nordquist. Some Rights Reserved.
// This work is licensed under http://creativecommons.org/licenses/by-sa/3.0/

package net.nordist.lloydproof;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class LloydProof extends Activity implements CorrectionUploadObserver
{
    private static final String TAG = "LloydProof";
    private static final int DIALOG_ABOUT = 0;

    private CorrectionStorage store;
    private CorrectionUploader uploader;
    private String appVersionName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        store = new CorrectionStorage(this);
        initializePackageInfo();
        setContentView(R.layout.main);
        handleReceivingSentText(getIntent());
    }

    @Override
    public void onStart() {
        super.onStart();
        updateUploadStatus();
    }

    private void initializePackageInfo() {
        try {
            PackageInfo packageInfo = this.getPackageManager().getPackageInfo(this.getPackageName(), 0);
            appVersionName = packageInfo.versionName;
        } catch (NameNotFoundException e) {
            appVersionName = "?";
        }
    }

    private void handleReceivingSentText(Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_SEND)
                && intent.getType().equals("text/plain")) {
            String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
            Log.d(TAG, "handleReceivingSentText(" + sharedText + ")");
            EditText editText = (EditText)findViewById(R.id.current_text);
            editText.setText(sharedText);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    // TODO Android doc says onCreateDialog is deprecated; use DialogFragment
    @Override
    public Dialog onCreateDialog(int dialogId) {
        if (dialogId != DIALOG_ABOUT) {
            return null;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.about_app_name);
        builder.setView(createDialogView());
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        return builder.create();
    }

    private LinearLayout createDialogView() {
        LinearLayout dialogView = (LinearLayout)getLayoutInflater().inflate(R.layout.about, null);
        TextView appVersion = (TextView)dialogView.findViewById(R.id.about_app_version);
        appVersion.setText(getString(R.string.app_version, appVersionName));
        return dialogView;
    }

    public void saveCorrection(View view) {
        EditText editText = (EditText)findViewById(R.id.current_text);
        String currentText = editText.getText().toString();
        if (currentText.isEmpty()) {
            Log.d(TAG, "not saving empty correction");
            return;
        }
        int id = store.save(currentText);  // NOPMD - id makes sense
        if (id > 0) {
            editText.setText("", TextView.BufferType.EDITABLE);
            showStatus(getString(R.string.correction_saved));
        } else {
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
        Button uploadButton = (Button)findViewById(R.id.upload_button);
        int count = store.count();
        if (count > 0) {
            String buttonText = String.format("%s (%d)", getString(R.string.upload), count);
            uploadButton.setText(buttonText);
            uploadButton.setEnabled(true);
        } else {
            uploadButton.setText(getString(R.string.upload));
            uploadButton.setEnabled(false);
        }
        updateUploadStatusText(count);
    }

    private void updateUploadStatusText(int count) {
        TextView uploadStatus = (TextView)findViewById(R.id.upload_status);
        if (count > 0) {
            uploadStatus.setText(getString(R.string.n_corrections_to_upload, pluralCorrectionCount(count)));
        } else {
            uploadStatus.setText(getString(R.string.all_corrections_uploaded));
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
        updateUploadStatus();
    }

    public void settings(MenuItem item) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivityForResult(intent, 0);
    }

    public void about(MenuItem item) {
        showDialog(DIALOG_ABOUT);
    }
}
