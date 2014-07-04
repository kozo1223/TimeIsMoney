package dev.si.timeismoney.database;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class PreferencesManager {
	
	SharedPreferences pref;
	private final String keyDay = "day";
	private final String keyHour = "hour";
	
	
	public PreferencesManager(Context context) {
		pref = context.getSharedPreferences("time_is_money_pref", Context.MODE_PRIVATE);
	}
	
	public void setLastUsedDay(int day) {
		Editor editor = pref.edit();
		editor.putInt(keyDay, day);
		editor.commit();
	}
	
	public int getLastUsedDay() {
		return pref.getInt(keyDay, 0);
	}
	
	public void setLastUsedHour(int hour) {
		Editor editor = pref.edit();
		editor.putInt(keyHour, hour);
		editor.commit();
	}
	
	public int getLastUsedHour() {
		return pref.getInt(keyHour, 1);
	}
	
}
