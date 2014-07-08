package dev.si.timeismoney.database;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class PreferencesManager {
	
	SharedPreferences pref;
	private final String keyMonth = "month";
	private final String keyDay = "day";
	private final String keyWeek = "week";
	private final String keyHour = "hour";
	
	
	public PreferencesManager(Context context) {
		pref = context.getSharedPreferences("time_is_money_pref", Context.MODE_PRIVATE);
	}
	
	public void setLastUsedMonth(int month) {
		Editor editor = pref.edit();
		editor.putInt(keyMonth, month);
		editor.commit();
	}
	
	public int getLastUsedMonth() {
		return pref.getInt(keyMonth, -1);
	}
	
	public void setLastUsedDay(int day) {
		Editor editor = pref.edit();
		editor.putInt(keyDay, day);
		editor.commit();
	}
	
	public int getLastUsedDay() {
		return pref.getInt(keyDay, -1);
	}
	
	public void setLastUsedWeek(int week) {
		Editor editor = pref.edit();
		editor.putInt(keyWeek, week);
		editor.commit();
	}
	
	public int getLastUsedWeek() {
		return pref.getInt(keyWeek, -1);
	}
	
	public void setLastUsedHour(int hour) {
		Editor editor = pref.edit();
		editor.putInt(keyHour, hour);
		editor.commit();
	}
	
	public int getLastUsedHour() {
		return pref.getInt(keyHour, -1);
	}
	
}
