package com.otipass.tools;

import java.util.Calendar;

public class EntrySale {

	int numotipass;
	Calendar date;
	int type;
	int service_id;

	public EntrySale(int numotipass, Calendar date, int type, int service_id){
		this.numotipass = numotipass;
		this.date 		= date;
		this.type 		= type;
		this.service_id = service_id;
	}

	public EntrySale(){

	}

	public void setNumOtipass(int numotipass){
		this.numotipass = numotipass;
	}

	public int getNumOtipass(){
		return numotipass;
	}

	public void setDate(Calendar date){
		this.date = date;
	}

	public Calendar getDate(){
		return date;
	}

	public void setType(int type){
		this.type = type;
	}

	public int getType(){
		return type;
	}

	public void setServiceId(int service_id){
		this.service_id = service_id;
	}

	public int getServiceId(){
		return service_id;
	}

}
