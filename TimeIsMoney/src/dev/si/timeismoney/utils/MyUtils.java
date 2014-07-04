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
    
}
