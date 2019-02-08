/**
================================================================================

    PASS MUSEUM project

    Package com.otipass.swdownload

    @copyright Otipass 2013. All rights reserved.
    @author ED ($Author: ede $)

    @version $Rev: 2448 $
    $Id: SwDownload.java 2448 2013-08-29 16:18:14Z ede $

================================================================================
 */package com.otipass.swdownload;

 import java.io.BufferedInputStream;
 import java.io.File;
 import java.io.FileOutputStream;
 import java.io.InputStream;
 import java.io.OutputStream;
 import java.net.URL;
 import java.net.URLConnection;

 import models.Constants;

 import android.content.Context;
 import android.os.Environment;
 import android.util.Log;
 import com.otipass.sql.MuseumDbAdapter;
 import com.otipass.sql.Param;
 import com.otipass.tools.tools;

public class SwDownload {
	 public static final int cPending = 1;
	 public static final int cOK = 2;
	 public static final int cError = 3;
	 private int statusDwnld;
	 private String apkPath;
	 private Context context;
	 long fileSize, lngReceived;
	 int progress;
	private String content;

	 public SwDownload(Context context) {
		 this.context = context;
	 }

	 public String getApkPath() {
		 return apkPath;
	 }

	 public int getProgress() {
		 return progress;
	 }

	private void download(final String urlToDownload) {
		statusDwnld = cPending;
		new Thread() {
			@Override
			public void run() {
				try {
					progress = 0;
					lngReceived = 0;
					URL url = new URL(urlToDownload);
					URLConnection connection = url.openConnection();
					connection.setRequestProperty("Accept-Encoding", "identity");
					final int fileSize = connection.getContentLength();
					connection.connect();
					// download the file
					if (fileSize > 0) {
						InputStream input = new BufferedInputStream(url.openStream());
						OutputStream output = new FileOutputStream(apkPath);

						byte data[] = new byte[4096];
						int count;
						while ((count = input.read(data)) != -1) {
							output.write(data, 0, count);
							lngReceived += count;
							progress = (int)(lngReceived * 100 / fileSize) ;
						}
						output.flush();
						output.close();
						input.close();
						File file = new File(apkPath);
						if (file.exists()) {
							statusDwnld = cOK;
						} else {
							statusDwnld = cError;
						}
					} else {
						statusDwnld = cError;
						Log.e(Constants.TAG, "Filesize:"+fileSize);
					}
				} catch (Exception e) {
					statusDwnld = cError;
					Log.e(Constants.TAG, e.getMessage());
				}
			}
		}.start();

	}

	 public int getDownloadStatus() {
		 return statusDwnld;
	 }

	 public void downloadSW(String newVersionName) {
		 String v = newVersionName.replace(".", "");
		 String file = "adt_v"+ v + ".apk";
		 apkPath = Environment.getExternalStorageDirectory().toString() + "/download/" + file;
		 String domain = tools.getDomain(context);
		 String url = Constants.ADTPlateform + "." + domain + "/mobile/alsace/apk/" + file;
		 download(url);
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
			 Log.e(Constants.TAG, "SwDownload.detectSoftwareDownload() -" + e.getMessage());
		 }
		 return newVersion;
	 }
 }
