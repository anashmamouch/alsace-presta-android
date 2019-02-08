/**
================================================================================

    WARNING
    Pass Museum Application.

    SQLite package

    @copyright Copyright (c) Otipass 2011. All rights reserved.
    @author ED ($Author: ede $)

    @version $Rev: 2749 $
    $Id: Warning.java 2749 2013-10-05 04:50:52Z ede $

================================================================================
*/
package com.otipass.sql;

public class Warning {
	public static final short cInvalidStatus = 1;
	
	private long id;
	private String date;
	private String serial;
	private short event;

	public Warning(String date, String serial, short event) {
		this.date = date;
		this.serial = serial;
		this.event = event;
	}
	
	public Warning() {
		
	}
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getSerial() {
		return serial;
	}

	public void setSerial(String serial) {
		this.serial = serial;
	}


	public short getEvent() {
		return event;
	}

	public void setEvent(short event) {
		this.event = event;
	}

}
