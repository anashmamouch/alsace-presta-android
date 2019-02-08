/**
================================================================================

    PARTIAL
    Pass Museum Application.

    SQLite package

    @copyright Copyright (c) Otipass 2011. All rights reserved.
    @author ED ($Author: ede $)

    @version $Rev: 2320 $
    $Id: Partial.java 2320 2013-07-12 12:20:28Z ede $

================================================================================
*/
package com.otipass.sql;


public class Partial {
	private long id;
	private int numotipass;
	private short status;
	private String expiry;
	private int pid;
	private int fare_id;
	private int option_id;

	public Partial() {
		
	}
	
	public Partial(int numotipass, short status, String expiry, int pid) {
		this.numotipass = numotipass;
		this.status = status;
		this.expiry = expiry;
		this.pid    = pid;
		this.fare_id = -1;
		this.option_id = -1;
	}

	public Partial(int numotipass, short status, String expiry, int pid, int fare_id, int option_id) {
		this.numotipass = numotipass;
		this.status = status;
		this.expiry = expiry;
		this.pid    = pid;
		this.fare_id = fare_id;
		this.option_id = option_id;
	}

	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}

	public int getOptionId() {
		return option_id;
	}
	public void setOptionId(int id) {
		this.option_id = id;
	}

	public int getFareId() {
		return fare_id;
	}
	public void setFareId(int id) {
		this.fare_id = id;
	}

	public int getNumotipass() {
		return numotipass;
	}

	public void setNumotipass(int numotipass) {
		this.numotipass = numotipass;
	}

	public short getStatus() {
		return status;
	}

	public void setStatus(short status) {
		this.status = status;
	}
	
	public String getExpiry() {
		return expiry;
	}

	public void setExpiry(String expiry) {
		this.expiry = expiry;
	}
	
	public void setPid(int pid){
		this.pid = pid;
	}
	
	public int getPid(){
		return pid;
	}
}
