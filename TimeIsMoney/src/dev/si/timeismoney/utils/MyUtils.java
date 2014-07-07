package dev.si.timeismoney.utils;

import java.util.Calendar;

public class MyUtils {
	
    private Calendar cal;

    public MyUtils() {
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
}
