package dev.si.timeismoney.utils;

import java.util.Calendar;

public class MyUtils {
	
    private Calendar cal;

    public MyUtils() {
    }
    
    public int getMonth() {
    	cal = Calendar.getInstance();
    	return cal.get(Calendar.MONTH);
    }
    
    public int month2MonthLength(int month) {
    	if (month == 2 || month == 4 || month == 6
    			|| month == 9 || month == 11) {
    		return 30;
    	} else {
    		return 31;
    	}
    }
    
    public int getDayOfMonth() {
    	cal = Calendar.getInstance();
    	return cal.get(Calendar.DAY_OF_MONTH);
    }

    public int getDayOfWeek() {
    	cal = Calendar.getInstance();
        return cal.get(Calendar.DAY_OF_WEEK);
    }
    
    public int getHourOfDay() {
    	cal = Calendar.getInstance();
    	return cal.get(Calendar.HOUR_OF_DAY);
    }
    
    public int getMinute() {
    	cal = Calendar.getInstance();
    	return cal.get(Calendar.MINUTE);
    }
    
    public String week2Col(int week) {
        String result = "";
        switch (week) {
            case 1:
                result = "logSun";
                break;
            case 2:
                result = "logMon";
                break;
            case 3:
                result = "logTue";
                break;
            case 4:
                result = "logWed";
                break;
            case 5:
                result = "logThu";
                break;
            case 6:
                result = "logFri";
                break;
            case 7:
                result = "logSat";
                break;
            default:
                break;
        }
        return result;
    }
    
    public String hour2Col(int hour) {
    	return "logDay_"+String.valueOf(hour);
    }
}
