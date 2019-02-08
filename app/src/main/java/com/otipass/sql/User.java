/**
================================================================================

    USER
    Pass Museum Application.

    SQLite package

    @copyright Copyright (c) Otipass 2011. All rights reserved.
    @author ED ($Author: ede $)

    @version $Rev: 2320 $
    $Id: User.java 2320 2013-07-12 12:20:28Z ede $

================================================================================
*/
package com.otipass.sql;

public class User {
	private long id;
	private String userid;
	private String password;
	private String salt;
	private short profile;

	public User() {
		
	}
	
	public User(int id, String userid, String password, String salt, short profile) {
		this.id = id;
		this.userid = userid;
		this.password = password;
		this.salt = salt;
		this.profile = profile;
	}
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getSalt() {
		return salt;
	}

	public void setSalt(String salt) {
		this.salt = salt;
	}

	public short getProfile() {
		return profile;
	}

	public void setProfile(short profile) {
		this.profile = profile;
	}
}
