package com.otipass.synchronization;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import com.otipass.adt67.HomeActivity;
import com.otipass.tools.tools;

import models.Constants;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class SynchroAlarm {

	private static String[] splitTime(String time) {
		String sTime[] = null;
		try {
			sTime = time.split(":");
		} catch(Exception e) {}
		return sTime;
	}

	private static int convertTime(String time) {
		int convert = 0;
		try {
			String[] sTime = time.split(":");
			convert = (Integer.valueOf(sTime[0]) * 3600) + (Integer.valueOf(sTime[1]) * 60) + Integer.valueOf(sTime[2]); 
		} catch(Exception e) {

		}
		return convert;
	}

	public static void setAlarm(Context context, String callingTime) {
		Calendar cur_cal = new GregorianCalendar();
		cur_cal.setTimeInMillis(System.currentTimeMillis());//set the current time and date for this calendar
		Calendar firstTime = new GregorianCalendar();
		String sTime[];
		try {
			Date now = new Date();
			String currentTime = new SimpleDateFormat("HH:mm:ss").format(now);
			int t1 = convertTime(currentTime); // the actual time
			int t2 = convertTime(callingTime); // the calling time
			sTime = splitTime(callingTime);

			if (t2 >= t1) {
				// first call today
				firstTime.set(Calendar.DAY_OF_YEAR, cur_cal.get(Calendar.DAY_OF_YEAR));
				firstTime.set(Calendar.YEAR, cur_cal.get(Calendar.YEAR));
			} else {
				// first call tomorrow
				if (cur_cal.get(Calendar.DAY_OF_YEAR) < cur_cal.getActualMaximum(Calendar.DAY_OF_YEAR)) {
					firstTime.set(Calendar.DAY_OF_YEAR, cur_cal.get(Calendar.DAY_OF_YEAR) + 1);	
				} else {
					firstTime.set(Calendar.DAY_OF_YEAR, 1);
					firstTime.set(Calendar.YEAR, cur_cal.get(Calendar.YEAR) + 1);
				}

			}
			firstTime.set(Calendar.HOUR_OF_DAY, Integer.valueOf(sTime[0]));
			firstTime.set(Calendar.MINUTE, Integer.valueOf(sTime[1])); 
			firstTime.set(Calendar.SECOND, Integer.valueOf(sTime[2]));
			firstTime.set(Calendar.MILLISECOND, 0);

			Calendar cal = Calendar.getInstance();        
			cal.add(Calendar.SECOND, 30);         
			//Create a new PendingIntent and add it to the AlarmManager        
			Intent intent1 = new Intent(context, SynchronizationService.class);      
			intent1.putExtra(Constants.WAKE_UP_STR, Constants.ALARM_UPLOAD_STR);
			Intent intent2 = new Intent(context, SynchronizationService.class);      
			intent2.putExtra(Constants.WAKE_UP_STR, Constants.ALARM_DOWNLOAD_STR);
			PendingIntent p1 = PendingIntent.getService(context, 1, intent1, PendingIntent.FLAG_CANCEL_CURRENT);
			PendingIntent p2 = PendingIntent.getService(context, 2, intent2, PendingIntent.FLAG_CANCEL_CURRENT);        
			AlarmManager am = (AlarmManager) context.getSystemService(Activity.ALARM_SERVICE);        
			long period24 = (long)(1000 * 60 * 60 * 24);
			// 1st alarm is to upload entries and updates
			am.setRepeating(AlarmManager.RTC_WAKEUP, firstTime.getTimeInMillis(), period24, p1);
			Log.i(Constants.TAG, "Start 1st alarm: " + tools.formatSQLDate(firstTime));
			// 2nd alarm is to get the WL, user, param
			// it is set 3 h later than the 1st one
			long delay = (long)(3 * 3600 * 1000);
			am.setRepeating(AlarmManager.RTC_WAKEUP, firstTime.getTimeInMillis() + delay, period24, p2); 
			Calendar newCal = Calendar.getInstance();
			newCal.setTimeInMillis(firstTime.getTimeInMillis() + delay);
			Log.i(Constants.TAG, "Start 2nd alarm: " + tools.formatSQLDate(newCal));
		} catch (Exception ex) {
			Log.e(Constants.TAG, "SynchroAlarm.setAlarm() " + ex.getMessage());
		}
	}
	
	public static void setPeriodicAlarm(Context context){
		
		String sTime[];
		Date now = new Date();
		String currentTime = new SimpleDateFormat("HH:mm:ss").format(now);
		sTime = splitTime(currentTime);
		Calendar callingTime = new GregorianCalendar();
		callingTime.set(Calendar.HOUR_OF_DAY, Integer.valueOf(sTime[0]));
		callingTime.set(Calendar.MINUTE, Integer.valueOf(sTime[1])); 
		callingTime.set(Calendar.SECOND, Integer.valueOf(sTime[2]));
		callingTime.set(Calendar.MILLISECOND, 0);
		
		Intent intent = new Intent(context, SynchronizationService.class);      
		intent.putExtra(Constants.ALARM_UP_PERIOD, Constants.ALARM_SYNCHRO_PERIOD);
		PendingIntent p = PendingIntent.getService(context, 1, intent, PendingIntent.FLAG_CANCEL_CURRENT);        
		AlarmManager am = (AlarmManager) context.getSystemService(Activity.ALARM_SERVICE);        
		long period = (long)(1000 * 60 * Constants.SYNCHRO_PERIOD);
		am.setRepeating(AlarmManager.RTC_WAKEUP, callingTime.getTimeInMillis(), period, p);
		
	}
}
