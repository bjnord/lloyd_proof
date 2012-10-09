// Copyright © 2012 by Brent J. Nordquist. Some Rights Reserved.
// This work is licensed under http://creativecommons.org/licenses/by-sa/3.0/

package net.nordist.lloydproof;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
        store = new CorrectionStorage(this);
        initializePackageInfo();
        setContentView(R.layout.main);
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
        builder.setView(createDialogView());
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
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
        int id = store.save(currentText);
        if (id > 0) {
            editText.setText("", TextView.BufferType.EDITABLE);
            showStatus(getString(R.string.correction_saved));
        } else {
            showStatus(getString(R.string.correction_save_error));
        }
        updateUploadStatus();
    }

    public void uploadCorrections(View view) {
        if (store.count() > 0) {
            uploader = new CorrectionUploader(this, this);
            uploader.execute();
        } else {
            Log.d(TAG, "no corrections to upload");
        }
    }

    private void showStatus(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void updateUploadStatus() {
        TextView uploadStatus = (TextView)findViewById(R.id.upload_status);
        int count = store.count();
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
        findViewById(R.id.upload_button).setEnabled(true);
    }

    public void about(MenuItem item) {
        showDialog(DIALOG_ABOUT);
    }
}
