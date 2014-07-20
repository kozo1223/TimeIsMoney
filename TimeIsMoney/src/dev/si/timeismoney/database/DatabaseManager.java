package dev.si.timeismoney.database;

import java.util.ArrayList;
import java.util.List;

import org.afree.data.time.Week;

import dev.si.timeismoney.utils.MyUtils;
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
        this.dbHelper = new MyDatabaseHelper(context);
        this.db = dbHelper.getWritableDatabase();
    }

    /**
     * データベースに登録されているアプリの一覧を取得
     * @return
     */
    public String[] appNames() {
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

    /**
     * データベースから値を得る
     * @param appName
     * @param column 取得したい項目
     * @return
     */
    public int select(String appName, String column) {
    	db = dbHelper.getReadableDatabase();
        String[] cols = {column};
        String whereClause = "appName = ?";
        String whereArgs[] = { appName };
        int result;

        try {
            Cursor cursor = db.query(TABLE_NAME, cols, whereClause, whereArgs, null, null, null, null);
        	// Cursor cursor = db.query(TABLE_NAME, cols, null, null, null, null, null, null);
            if (cursor.moveToFirst()) {
                result = cursor.getInt(0);
            } else {
                Log.i("SELECT ::", "SELECT Failed");
                result = -1;
            }
        } finally {
        	
        }
        db = dbHelper.getWritableDatabase();
        return result;
    }
    /**
     * データベースに挿入するメソッド
     * @param appName アプリ名
     * @param onceLimit　一回の上限
     * @param dayLimit　一日の上限
     */
    public void insert(String appName, int onceLimit, int dayLimit) {
        ContentValues values = new ContentValues();
        values.put("appName", appName);
        values.put("onceLimit", onceLimit);
        values.put("dayLimit", dayLimit);
        if (select(appName, "onceLimit") == -1) {
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
        } else {
        	Log.i("INSERT ERROR", "Same name application exists");
        	
        }
    }

    /**
     * データベース更新メソッド
     * @param appName
     * @param column
     * @param value
     */
    public void update(String appName, String column, int value) {
        ContentValues values = new ContentValues();
        values.put(column, value);
        String whereClause = "appName = ?";
        String whereArgs[] = new String[1];
        whereArgs[0] = appName;
        Log.i("Database appName::", appName);
        int result;
        try {
            result = db.update(TABLE_NAME, values, whereClause, whereArgs);
        	// result = db.update(TABLE_NAME, values, null, null);
        } finally {
        
        }
        Log.i("DATABASE UPDATE:", String.valueOf(result));

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
            Log.i("Database Message" , "Delete Failed :"+appName);
        } else {
            Log.i("Database Message" , "Delete Success :"+appName);
        }

    }
    
    public int getResultTimeOfWeek(String appName, MyUtils utils) {
    	int result = 0;
    	int dayLimit;
    	dayLimit = select(appName, "dayLimit");
    	for	(int week = 1; week <= 7; week++) {
    		result += dayLimit - select(appName, utils.week2Col(week));
    	}
    	return result;
    }
}
