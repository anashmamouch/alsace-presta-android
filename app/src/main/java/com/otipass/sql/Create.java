
package com.otipass.sql;


public class Create {
	private long id;
	private int numotipass;
	private String serial;
	private short type;
	private short status;
	private int pid;
	private String srv;
	private String expiry;
	private int fare_id;
	private int option_id;

	public Create() {

	}

	public Create(int numotipass, short type, String serial, short status, int pid, String srv, String expiry) {
		this.numotipass = numotipass;
		this.type       = type;
		this.serial     = serial;
		this.status     = status;
		this.pid        = pid;
		this.srv        = srv;
		this.expiry     = expiry;
		this.fare_id = -1;
		this.option_id = -1;
	}

	public Create(int numotipass, short type, String serial, short status, int pid, String srv, String expiry, int fare_id, int option_id) {
		this.numotipass = numotipass;
		this.type       = type;
		this.serial     = serial;
		this.status     = status;
		this.pid        = pid;
		this.srv        = srv;
		this.expiry     = expiry;
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

	public short getType() {
		return type;
	}

	public void setType(short type) {
		this.type = type;
	}

	public short getStatus() {
		return status;
	}

	public void setStatus(short status) {
		this.status = status;
	}

	public String getSerial() {
		return serial;
	}

	public void setSerial(String serial) {
		this.serial = serial;
	}
	
	public void setPid(int pid){
		this.pid = pid;
	}
	
	public int getPid(){
		return pid;
	}
	
	public void setService(String srv){
		this.srv = srv;
	}
	
	public String getService(){
		return srv;
	}
	
	public void setExpiry(String expiry){
		this.expiry = expiry;
	}
	
	public String getExpiry(){
		return expiry;
	}
}
