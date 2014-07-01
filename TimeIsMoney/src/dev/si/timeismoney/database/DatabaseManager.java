package dev.si.timeismoney.database;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DatabaseManager {

    private MyDatabaseHelper dbHelper;
    private SQLiteDatabase db;
    private final String TABLE_NAME = "AppLog";

    public DatabaseManager(Context context) {
        Log.i("Databasemanager.java", "Databasemanager constructor is called");
        this.dbHelper = new MyDatabaseHelper(context);
        this.db = dbHelper.getWritableDatabase();
        Log.i("Databasemanager.java", "Databasemanager constructor is finished");
    }

    public String[] appNames() {
        Log.i("DEBUG", "appNames() called .");

        List<String> result = new ArrayList<String>();
        try {
            Cursor cursor = db.query(TABLE_NAME, new String[]{"appName"}, null, null, null, null, "appID");
            cursor.moveToFirst();
            for (int i = 1; i <= cursor.getCount(); i++) {
                result.add(cursor.getString(0));
                Log.i("DEBUG", result.get(result.size() - 1));
                cursor.moveToNext();
            }
            Log.i("RESULT ::", String.valueOf(result.size()));
        } finally {

        }
        return (String[])result.toArray(new String[0]);
    }

    public int select(String appName, String column) {
        String[] cols = {column};
        String whereClause = "appName = ?";
        String whereArgs[] = { appName };
        int result;

        try {
            Cursor cursor = db.query(TABLE_NAME, cols, whereClause, whereArgs, null, null, null, null);
            if (cursor.moveToFirst()) {
                result = cursor.getInt(0);
            } else {
                Log.i("SELECT ::", "SELECT Failed");
                result = 0;
            }
        } finally {

        }

        return result;
    }

    public void insert(String appName, int onceLimit, int dayLimit) {
        ContentValues values = new ContentValues();
        values.put("appName", appName);
        values.put("onceLimit", onceLimit);
        values.put("dayLimit", dayLimit);

        long result;
        try {
            result = db.insert(TABLE_NAME, null, values);
        } finally {

        }
        if (result == -1) {
            Log.i("Database Message" , "Insert Failed");
        } else {
            Log.i("Database Message" , "Insert Success");
        }
    }

    public void update(String appName, String column, int value) {
        ContentValues values = new ContentValues();
        values.put(column, value);
        String whereClause = "appName = ?";
        String whereArgs[] = { appName };

        int result;
        try {
            result = db.update(TABLE_NAME, values, whereClause, whereArgs);
        } finally {

        }

        if (result == -1) {
            Log.i("Database Message" , "Update Failed");
        } else {
            Log.i("Database Message" , "Update Success");
        }


    }

    public void delete(String appName) {
        String whereClause = "appName = ?";
        String whereArgs[] = { appName };

        int result;
        try {
            result = db.delete(TABLE_NAME, whereClause, whereArgs);
        } finally {

        }

        if (result == -1) {
            Log.i("Database Message" , "Delete Failed");
        } else {
            Log.i("Database Message" , "Delete Success");
        }

    }
}
