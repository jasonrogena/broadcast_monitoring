package com.broadcastmonitoring.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class Time 
{
	public static String getTime(String timezone)
	{
		Date date=new Date();
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		sdf.setTimeZone(TimeZone.getTimeZone(timezone));
		return sdf.format(date)+" ("+timezone+")";
	}

}
