/**
================================================================================

    OTIPASS
    Pass Museum Application.

    package com.otipass.tools

    @copyright Copyright (c) Otipass 2011. All rights reserved.
    @author ED ($Author: ede $)

    @version $Rev: 4152 $
    $Id: tools.java 4152 2014-09-11 12:21:57Z ede $

================================================================================
 */
package com.otipass.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import models.Constants;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import com.otipass.adt67.R;
import com.otipass.sql.MuseumDbAdapter;
import com.otipass.sql.Param;
import com.otipass.sql.Warning;
import com.otipass.sql.Wl;

public class tools {
	public static final String NEXUS_7 = "Nexus 7";

	public static final int cOK = 1;
	public static final int cWarning = 2;
	public static final int cError = 3;
	
	public static final int cIdle = 0;
	public static final int cSingleCall = 1;
	public static final int cPeriodicCall = 2;
	public static final int cNightCall = 3;
	public static final int cCommmunicationPending = 4;
	static int width, height;


	public static boolean checkMD5(String clearPwd, String salt, String cipheredPwd) {
		String pwd;
		boolean checked = false;
		try {
			pwd = clearPwd + salt;
			MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
			digest.update(pwd.getBytes());
			final byte[] resultByte = digest.digest();
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < resultByte.length; ++i) {
				sb.append(Integer.toHexString((resultByte[i] & 0xFF) | 0x100).substring(1,3));
			}
			final String result = sb.toString();
			if (result.equals(cipheredPwd)) {
				checked = true;
			}
		} catch (Exception e) {

		}
		return checked;
	}


	/**
	 * Transform a Uk Date in Fr Format
	 * @param String givenDate
	 * @return String
	 */
	public static String transformDate(String givenDate) {
		String Str_result = givenDate;
		try {
			Date result = new SimpleDateFormat("yyyy-MM-dd").parse(givenDate);
			Str_result = new SimpleDateFormat("dd/MM/yyyy").format(result);
		}catch (java.text.ParseException e) {

		}
		return Str_result;
	}



	public static void ExportLogcat(File file) throws InterruptedException, IOException {		
		Process process = null;
		try {
			ProcessBuilder pb = new ProcessBuilder("logcat", "-d", "-v", "time");
			pb.redirectErrorStream(true);
			process = pb.start();
			BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader(process.getInputStream()), 1024
					);

			StringBuilder log = new StringBuilder();
			String line = "";
			while ((line = bufferedReader.readLine()) != null) {
				log.append(line+"\n");
			}
			String data = log.toString();
			FileOutputStream fOut = null;
			OutputStreamWriter osw = null;
			try{
				fOut = new FileOutputStream(file);
				osw = new OutputStreamWriter(fOut);

				osw.write(data);
				osw.flush();
			}
			catch (Exception e) {      
				e.printStackTrace();
			}
			finally {
				fOut.close();
				osw.close();
			}
			// this erases the logcat
			process = Runtime.getRuntime().exec("logcat -c");
		} catch(IOException e){
			e.printStackTrace();
		}
	}

	public static boolean isTablet() {
		String model = android.os.Build.MODEL;
		if (model.equals(NEXUS_7)) {
			return true;
		}
		return false;
	}

	public static void showAlert(Context context, String message, int type) {
		Drawable icon;
		String title;
		switch (type) {
		case cWarning:
			icon = context.getResources().getDrawable(R.drawable.ic_attention);
			title = context.getString(R.string.warning_message_title);
			break;
		case cError:
			icon = context.getResources().getDrawable(R.drawable.ic_ko);
			title = context.getString(R.string.error_message_title);
			break;
		default:
			icon = context.getResources().getDrawable(R.drawable.logoadt);
			title = context.getString(R.string.Global_information);
			break;
		}
		AlertDialog alertDialog = new AlertDialog.Builder(context).create();
		alertDialog.setTitle(title);
		alertDialog.setIcon(icon);
		alertDialog.setMessage(message);
		alertDialog.setButton(context.getString(R.string.Global_ok), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
			} }); 
		alertDialog.show();
	}

	public static AlertDialog showWait(Context context) {
		Drawable icon;
		String title;
		icon = context.getResources().getDrawable(R.drawable.ic_attention);
		title = context.getString(R.string.Global_information);
		AlertDialog alertDialog = new AlertDialog.Builder(context).create();
		alertDialog.setTitle(title);
		alertDialog.setIcon(icon);
		alertDialog.setMessage(context.getString(R.string.Global_veuillez_patienter));
		alertDialog.show();
		return alertDialog;
	}

	public static String formatNow(String pattern) {
		String now = "";
		Calendar calendar = Calendar.getInstance(); 
		try {
			SimpleDateFormat format = new SimpleDateFormat(pattern);
			now = format.format(calendar.getTime());
		} catch (Exception e) {
			Log.e(Constants.TAG, tools.class.getName() + " - formatNow - " + e.getMessage());
		}
		return now;
	}

	public static String addDayToDate(String pattern, int daysToAdd) {
		String date = "";
		Calendar calendar = Calendar.getInstance(); 
		try {
			SimpleDateFormat format = new SimpleDateFormat(pattern);
			calendar.add(Calendar.DATE, daysToAdd); 
			date = format.format(calendar.getTime());
		} catch (Exception e) {
			Log.e(Constants.TAG, tools.class.getName() + " - addDayToDate - " + e.getMessage());
		}
		return date;
	}

	public static String endOfDay(String pattern) {
		String date = "";
		Calendar calendar = Calendar.getInstance(); 
		try {
			SimpleDateFormat format = new SimpleDateFormat(pattern);
			calendar.set(Calendar.HOUR_OF_DAY, 23); 
			calendar.set(Calendar.MINUTE, 59); 
			calendar.set(Calendar.SECOND, 59); 
			date = format.format(calendar.getTime());
		} catch (Exception e) {
			Log.e(Constants.TAG, tools.class.getName() + " - endOfDay - " + e.getMessage());
		}
		return date;
	}

	public static String formatSQLDate(Calendar calendar) {
		String date = "";
		try {
			date = new SimpleDateFormat(Constants.SQL_FULL_DATE_FORMAT).format(calendar.getTime());	
		} catch (Exception e) {
			Log.e(Constants.TAG, tools.class.getName() + " - formatSQLDate - " + e.getMessage());
		}
		return date;
	}

	public static String formatTextDate(String date) {
		String newDate = "";
		try {
			Calendar cal = setCalendar(date);
			newDate = new SimpleDateFormat(Constants.DATE_FORMAT_FR_NOSEC).format(cal.getTime()) + " h";	
		} catch (Exception e) {
			Log.e(Constants.TAG, tools.class.getName() + " - formatTextDate - " + e.getMessage());
		}
		return newDate;
	}

	public static Calendar setCalendar(String date) {
		Calendar calendar = null;
		try {
			calendar = Calendar.getInstance();
			SimpleDateFormat format = new SimpleDateFormat(Constants.SQL_FULL_DATE_FORMAT); 
			calendar.setTime(format.parse(date));
		} catch (Exception e) {
			Log.e(Constants.TAG, tools.class.getName() + " - setCalendar - " + e.getMessage());
		}
		return calendar;
	}

	public static Calendar setCalendarDate(String date) {
		Calendar calendar = null;
		try {
			calendar = Calendar.getInstance();
			SimpleDateFormat format = new SimpleDateFormat(Constants.FULL_DATE_FORMAT_FR); 
			calendar.setTime(format.parse(date));
		} catch (Exception e) {
			Log.e(Constants.TAG, tools.class.getName() + " - setCalendarDate - " + e.getMessage());
		}
		return calendar;
	}

	// this gives a reference when defining if a card is expired
	// it returns the current date with time set to 00:00:00
	public static Calendar getNow00() {
		Calendar calendar = null;
		try {
			calendar = Calendar.getInstance();
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);

		} catch (Exception e) {
			Log.e(Constants.TAG, tools.class.getName() + " - setCalendar - " + e.getMessage());
		}
		return calendar;
	}

	// returns the number of steps (x 100 cards) stored in the DB
	// the unused warning table is used to store WL variables
	public static int getNbSteps(MuseumDbAdapter dbAdapter) {
		int nbSteps = 0;
		Wl wlState = dbAdapter.getWl(1L);
		if (wlState != null) {
			nbSteps = wlState.getStatus();
		}
		return nbSteps;
	}

	
	public static void broadcastFileContent(String dir,  Activity activity, boolean mustDelete) { 
		File existantfiles = new File(dir);
		for (File f : existantfiles.listFiles()) {
			if (f.isFile())
			{
				if (mustDelete) {
					f.delete();
				}
				Uri uri = Uri.fromFile(f);
				Intent scanFileIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri);
				activity.sendBroadcast(scanFileIntent);
			}
		}
	}

	public static boolean detectNewSoftwareVersion(Context context, MuseumDbAdapter dbAdapter) {
		boolean newVersion = false;
		try {
			String currentVersionName = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
			Param param = dbAdapter.getParam(1L);
			String newVersionName = param.getSoftwareVersion();
			float f1 = Float.valueOf(currentVersionName);
			float f2 = Float.valueOf(newVersionName);
			newVersion = f2 > f1;
		} catch (Exception e) {
			Log.e(Constants.TAG, "tools.detectSoftwareDownload() -" + e.getMessage());
		}
		return newVersion;
	}

	public static boolean isNetworkAvailable(Context context) {
		ConnectivityManager connectivityManager 
		= (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}
	
	@SuppressLint("NewApi")
	public static void PackageShapeColor(Context context){

		Resources res = context.getResources();
		GradientDrawable shape = (GradientDrawable)res.getDrawable(R.drawable.package_shape);
		int shape_color = Color.parseColor("#6666CC");
		int gradient_color = Color.parseColor("#FFFFFF");
		int [] colors = {gradient_color, shape_color};
		shape.setColors(colors);

	}

	public static void setServiceState(Context context, int state) {
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor edt = pref.edit();
		edt.putInt(Constants.SERVICE_KEY, state);
		edt.commit();
	}

	public static int getServiceState(Context context) {
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
		return pref.getInt(Constants.SERVICE_KEY, cIdle); 
	}
	public static void setPeriodicCall(Context context, boolean on) {
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor edt = pref.edit();
		edt.putBoolean(Constants.PERIODIC_CALL, on);
		edt.commit();
	}
	public static boolean getPeriodicCall(Context context) {
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
		return pref.getBoolean(Constants.PERIODIC_CALL, false); 
	}
	public static void setDomain(Context context, String domain) {
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor edt = pref.edit();
		edt.putString(Constants.DOMAIN_KEY, domain);
		edt.commit();
	}
	public static String getDomain(Context context) {
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
		return pref.getString(Constants.DOMAIN_KEY, Constants.OTIPASS_DOMAIN);
	}

	public static void setDbHasChangedModel(Context context, boolean changed) {
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor edt = pref.edit();
		edt.putBoolean(Constants.DB_CHANGED__KEY, changed);
		edt.commit();
	}
	public static boolean getDbHasChangedModel(Context context) {
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
		return pref.getBoolean(Constants.DB_CHANGED__KEY, false);
	}

	public static void setIsNewTariffOn(Context context, boolean on) {
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor edt = pref.edit();
		edt.putBoolean(Constants.NEW_TARIFICATION__KEY, on);
		edt.commit();
	}
	public static boolean getIsNewTariffOn(Context context) {
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
		return true;
	}

	public static Point getScreenSize(Context context) {
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		return size;
	}
	/*
     * Tablet UID detection
     * Famoco and Nexus do have internal NFC
     * Nvidia SHield has no internal NFC
     */
	public static String getDeviceUID(Context context) {
		String UID = "unknown";
		Point p;
		try {
			width = getScreenSize(context).x;
			height = getScreenSize(context).y;
			if (Build.MODEL.equals(Constants.FAMOCO) || Build.MODEL.equals(Constants.Blackview)) {
				TelephonyManager m_telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
				UID = m_telephonyManager.getDeviceId();
			} else {
				UID = android.os.Build.SERIAL;
			}
		} catch (Exception ex) {
			Log.e(Constants.TAG, tools.class.getName() + " - getDeviceUID - " + ex.getMessage());
		}
		return UID;
	}

}
