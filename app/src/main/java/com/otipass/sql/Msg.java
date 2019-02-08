/**
================================================================================

    PASS MUSEUM project

    Package com.otipass.sql

    @copyright Otipass 2013. All rights reserved.
    @author ED ($Author: ede $)

    @version $Rev: 2426 $
    $Id: Msg.java 2426 2013-08-28 16:34:15Z ede $

================================================================================
*/
package com.otipass.sql;

public class Msg {
	private String msg;
	private int type;
	private int minProfile;
	private int id;
	String startDate;
	String endDate;
	String lang;
	
	public Msg() {
		
	}
	
	public Msg(String msg, int type, int id, int minProfile) {
		this.msg = msg;
		this.type = type;
		this.id = id;
		this.minProfile = minProfile;
	}
	
	public Msg(String msg, String startDate, String endDate, String lang) {
		this.msg = msg;
		this.startDate = startDate;
		this.endDate = endDate;
		this.lang = lang;
	}

	public String getMsg() {
		return msg;
	}
	
	public void setMsg(String msg) {
		this.msg = msg;
	}

	public int getType() {
		return type;
	}
	
	public void setType(int type) {
		this.type = type;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getMinProfile() {
		return minProfile;
	}

	public void setMinProfile(int profile) {
		this.minProfile = minProfile;
	}

	public String getStartDate() {
		return startDate;
	}
	
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}
	
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public String getLang() {
		return lang;
	}
	
	public void setLang(String lang) {
		this.lang = lang;
	}

}