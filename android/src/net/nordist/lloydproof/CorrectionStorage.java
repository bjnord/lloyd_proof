package net.nordist.lloydproof;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import java.util.Calendar;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CorrectionStorage extends SQLiteOpenHelper
{
    private final String TAG = getClass().getSimpleName();

    private SQLiteDatabase writeDB;
    private SQLiteDatabase readDB;

    public static final String TABLE_NAME = "corrections";
    private static final String DATABASE_NAME = "corrections.db";
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_CREATE = "CREATE TABLE " + TABLE_NAME
        + " (id integer PRIMARY KEY AUTOINCREMENT,"
        + " current_text text NOT NULL DEFAULT '');";

    public CorrectionStorage(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.w(TAG, "Creating database v" + DATABASE_VERSION);
        db.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(TAG, "Upgrading database from v" + oldVersion + " to v"
            + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME + ";");
        onCreate(db);
    }

    public int save(String currentText) {
        ContentValues values = new ContentValues();
        values.put("current_text", currentText);
        openWriteDB();
        return (int)writeDB.insert(TABLE_NAME, null, values);
    }

    public int count() {
        openReadDB();
        Cursor cursor = readDB.query(TABLE_NAME, new String[] {"COUNT(1)"},
            null, null, null, null, null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            return cursor.getInt(0);
        } else {
            return 0;
        }
    }

    private JSONObject getNextAsJSONObject(Cursor cursor) throws JSONException {
        JSONObject jobject = new JSONObject();
        jobject.put("sync_id", cursor.getInt(0));
        jobject.put("current_text", cursor.getString(1));
        return jobject;
    }

    public JSONArray getAllAsJSONArray() throws JSONException {
        openReadDB();
        Cursor cursor = readDB.query(TABLE_NAME,
            new String[] {"id", "current_text"},
            null, null, null, null, null);
        JSONArray jarray = new JSONArray();
        while (cursor.moveToNext()) {
            jarray.put(getNextAsJSONObject(cursor));
        }
        return jarray;
    }

    // FIXME refactor
    public int deleteByJSONArrayStatus(JSONArray statusArray) throws JSONException {
        openWriteDB();
        int deletedCount = 0;
        for (int i = 0; i < statusArray.length(); i++) {
            JSONObject status = statusArray.getJSONObject(i);
            if (status.getString("status").equals("ok")) {
                int syncId = status.getInt("sync_id");
                String[] wherevals = new String[] {Integer.toString(syncId)};
                deletedCount += writeDB.delete(TABLE_NAME, "id=?", wherevals);
                Log.d(TAG, "deleted id=" + syncId);
            }
        }
        return deletedCount;
    }

    public void close() {
        if (writeDB != null) {
            writeDB.close();
            writeDB = null;
        }
        if (readDB != null) {
            readDB.close();
            readDB = null;
        }
    }

    protected void finalize() throws Throwable {
        try {
            close();
        } finally {
            super.finalize();
        }
    }

    private void openWriteDB() {
        if (writeDB == null) {
            writeDB = getWritableDatabase();
        }
    }

    private void openReadDB() {
        if (readDB == null) {
            readDB = getReadableDatabase();
        }
    }
}
