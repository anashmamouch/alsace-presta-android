package com.otipass.sql;

public class PartialServiceCpt {
	
	private int numotipass;
	private String srv;
	
	public PartialServiceCpt(){
		
	}

	public PartialServiceCpt(int numotipass, String srv){
		this.numotipass	= numotipass;
		this.srv        = srv;
	}
	
	public void setNumOtipass(int numotipass){
		this.numotipass = numotipass;
	}
	
	public int getNumOtipass(){
		return numotipass;
	}
	
	public void setSrv(String srv){
		this.srv = srv;
	}
	
	public String getSrv(){
		return srv;
	}
}
