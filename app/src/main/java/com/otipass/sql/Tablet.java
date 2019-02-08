/**
================================================================================

    TABLET
    Pass Museum Application.

    SQLite package

    @copyright Copyright (c) Otipass 2011. All rights reserved.
    @author ED ($Author: ede $)

    @version $Rev: 2320 $
    $Id: Tablet.java 2320 2013-07-12 12:20:28Z ede $

================================================================================
*/
package com.otipass.sql;

public class Tablet {
	private long id;
	private int numSequence;
	private String downloadTime;
	private String uploadTime;
	private int dbModel;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getNumSequence() {
		return numSequence;
	}

	public void setNumSequence(int numSequence) {
		this.numSequence = numSequence;
	}

	public String getDownloadTime() {
		return downloadTime;
	}

	public void setDownloadTime(String time) {
		this.downloadTime = time;
	}

	public String getUploadTime() {
		return uploadTime;
	}

	public void setUploadTime(String time) {
		this.uploadTime = time;
	}

	public int getDbModel() {
		return dbModel;
	}

	public void setDbModel(int model) {
		this.dbModel = model;
	}
}
