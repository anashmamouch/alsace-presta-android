/**
================================================================================

    OTIPASS
    Pass Museum Application.

    SQLite package

    @copyright Copyright (c) Otipass 2011. All rights reserved.
    @author ED ($Author: ede $)

    @version $Rev: 4379 $
    $Id: Otipass.java 4379 2014-11-27 15:32:45Z ede $

================================================================================
*/
package com.otipass.sql;

import models.Constants;

public class Otipass {
	private long numOtipass;
	private String serial;
	private short status;
	private String expiry;
	private short type;
	private int pid;
	private String service;
	private String use_day;
	private int option_id;
	private int fare_id;

	public Otipass() {
		
	}
	
	public Otipass(long numOtipass, String serial, short status, String expiry, short type, int pid, String service) {
		this.numOtipass = numOtipass;
		this.serial = serial;
		this.status = status;
		this.expiry = expiry;
		this.type = type;
		this.pid = pid;
		this.service = service;
		this.fare_id = -1;
		this.option_id = -1;
	}
	public Otipass(long numOtipass, String serial, short status, String expiry, short type, int pid, String service, int fare_id, int option_id) {
		this.numOtipass = numOtipass;
		this.serial = serial;
		this.status = status;
		this.expiry = expiry;
		this.type = type;
		this.pid = pid;
		this.service = service;
		this.fare_id = fare_id;
		this.option_id = option_id;
	}
	public Otipass(long numOtipass, String serial, short status, String expiry, short type, int pid, String service, String use_day, int fare_id, int option_id) {
		this.numOtipass = numOtipass;
		this.serial = serial;
		this.status = status;
		this.expiry = expiry;
		this.type = type;
		this.pid = pid;
		this.service = service;
		this.use_day = use_day;
		this.fare_id = fare_id;
		this.option_id = option_id;
	}
	public Otipass(long numOtipass) {
		this.numOtipass = numOtipass;
		this.status = Constants.PASS_CREATED;
		this.type = Constants.PASS_INITIAL;
		this.fare_id = -1;
		this.option_id = -1;
	}
	
	public long getNumOtipass() {
		return this.numOtipass;
	}

	public void setNumOtipass(long numOtipass) {
		this.numOtipass = numOtipass;
	}

	public String getSerial() {
		return this.serial;
	}

	public void setSerial(String serial) {
		this.serial = serial;
	}

	public short getStatus() {
		return this.status;
	}

	public void setStatus(short status) {
		this.status = status;
	}

	public String getExpiry() {
		return this.expiry;
	}

	public void setExpiry(String expiry) {
		this.expiry = expiry;
	}

	public short getType() {
		return this.type;
	}

	public void setType(short type) {
		this.type = type;
	}
	
	public void setPid(int pid){
		this.pid = pid;
	}
	
	public int getPid(){
		return pid;
	}
	
	public void setService(String service){
		this.service = service;
	}
	
	public String getService(){
		return service;
	}

	public void setUseDay(String use_day){
		this.use_day = use_day;
	}
	
	public String getUseDay(){
		return use_day;
	}

	public int getOptionId(){
		return option_id;
	}
	public void setOptionId(int id){
		this.option_id = id;
	}
	public int getFareId(){
		return fare_id;
	}
	public void setFareId(int id){
		this.fare_id = id;
	}

}
