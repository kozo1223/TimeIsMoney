package dev.si.timeismoney.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MyDatabaseHelper extends SQLiteOpenHelper {

	static final String DB_NAME = "time_is_money.db";
    static final int DB_VERSION = 1;

    // FIX ME
    private final String CREATE_TABLE_SQL =
            "CREATE TABLE AppLog (" + "appID INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "appName TEXT NOT NULL, " + "onceLimit INTEGER DEFAULT 600, "
                    + "dayLimit INTEGER DEFAULT 3600, " + "logMon INTEGER DEFAULT 0, "
                    + "logTue INTEGER DEFAULT 0, " + "logWed INTEGER DEFAULT 0, "
                    + "logThu INTEGER DEFAULT 0, " + "logFri INTEGER DEFAULT 0, "
                    + "logSat INTEGER DEFAULT 0, " + "logSun INTEGER DEFAULT 0 " + ");";

    public MyDatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        Log.i("MyDatabaseHelper:", "Constructor is called");
    }

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		Log.i("MyDatabaseHelper.java", "onCreate called");
	    db.execSQL(CREATE_TABLE_SQL);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		db.execSQL("DROP TABLE IF EXISTS AppLog");
        onCreate(db);
	}

}
