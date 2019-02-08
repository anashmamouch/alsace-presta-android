package com.otipass.sql;

public class Usage {

	int numotipass;
	String date;

	public Usage(){

	}

	public Usage(int numotipass, String date){
		this.numotipass = numotipass;
		this.date = date;
	}

	public void setNumOtipass(int numotipass){
		this.numotipass = numotipass;
	}

	public int getNumOtipass(){
		return numotipass;
	}

	public void setDate(String date){
		this.date = date;
	}

	public String getDate(){
		return date;
	}

}
