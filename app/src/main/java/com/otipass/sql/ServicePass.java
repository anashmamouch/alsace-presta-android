package com.otipass.sql;

public class ServicePass {

	private int id;
	private int type;
	private String name;
	
	public ServicePass(int id, int type, String name){
		
		this.id     = id;
		this.type   = type;
		this.name   = name;
		
	}
	
	public ServicePass(){
		
	}
	
	public void setId(int id){
		this.id = id;
	}
	
	public int getId(){
		return id;
	}
	
	public void setType(int type){
		this.type = type;
	}
	
	public int getType(){
		return type;
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public String getName(){
		return name;
	}
}
