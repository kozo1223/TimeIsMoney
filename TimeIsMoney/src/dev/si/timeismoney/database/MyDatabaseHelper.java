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
                    + "logSat INTEGER DEFAULT 0, " + "logSun INTEGER DEFAULT 0, "
                    + "logDay_0 INTEGER DEFAULT 0, "
                    + "logDay_1 INTEGER DEFAULT 0, " + "logDay_2 INTEGER DEFAULT 0, "
                    + "logDay_3 INTEGER DEFAULT 0, " + "logDay_4 INTEGER DEFAULT 0, "
                    + "logDay_5 INTEGER DEFAULT 0, " + "logDay_6 INTEGER DEFAULT 0, "
                    + "logDay_7 INTEGER DEFAULT 0, " + "logDay_8 INTEGER DEFAULT 0, "
                    + "logDay_9 INTEGER DEFAULT 0, " + "logDay_10 INTEGER DEFAULT 0, "
                    + "logDay_11 INTEGER DEFAULT 0, " + "logDay_12 INTEGER DEFAULT 0, "
                    + "logDay_13 INTEGER DEFAULT 0, " + "logDay_14 INTEGER DEFAULT 0, "
                    + "logDay_15 INTEGER DEFAULT 0, " + "logDay_16 INTEGER DEFAULT 0, "
                    + "logDay_17 INTEGER DEFAULT 0, " + "logDay_18 INTEGER DEFAULT 0, "
                    + "logDay_19 INTEGER DEFAULT 0, " + "logDay_20 INTEGER DEFAULT 0, "
                    + "logDay_21 INTEGER DEFAULT 0, " + "logDay_22 INTEGER DEFAULT 0, "
                    + "logDay_23 INTEGER DEFAULT 0 "
                    + ");";

    public MyDatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
	    db.execSQL(CREATE_TABLE_SQL);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		db.execSQL("DROP TABLE IF EXISTS AppLog");
        onCreate(db);
	}

}
