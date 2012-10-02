package net.nordist.lloydproof;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class LloydProof extends Activity
{
    private final String TAG = this.getClass().getSimpleName();

    private CorrectionStorage store;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        store = new CorrectionStorage(this);
        setContentView(R.layout.main);
    }

    public void saveCorrection(View view) {
        EditText current_text = (EditText)findViewById(R.id.current_text);
        // FIXME do nothing if EditText box is empty
        int id = store.save(current_text.getText().toString());
        if (id > 0) {
            Log.d(TAG, "saved correction as id=" + id);
            current_text.setText("", TextView.BufferType.EDITABLE);
            // FIXME show success as flyover status
        } else {
            Log.e(TAG, "error saving correction");
            // FIXME show error as flyover status
        }
        this.updateSyncStatus();
    }

    private void updateSyncStatus() {
        TextView sync_status = (TextView)findViewById(R.id.sync_status);
        int count = store.count();
        // FIXME use string resources for i18n
        if (count > 0) {
            sync_status.setText(Integer.toString(count) + " corrections to upload");
        } else {
            sync_status.setText("All corrections uploaded");
        }
    }
}
