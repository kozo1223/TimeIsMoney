package dev.si.timeismoney.utils;

import java.util.Calendar;

public class MyUtils {
	
    private Calendar cal;

    public MyUtils() {
        cal = Calendar.getInstance();
    }

    public int getDayOfWeek() {
        return cal.get(Calendar.DAY_OF_WEEK);
    }
    
    public int getHourOfDay() {
    	return cal.get(Calendar.HOUR_OF_DAY);
    }
    
}
