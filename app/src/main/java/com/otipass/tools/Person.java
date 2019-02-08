package com.otipass.tools;

import java.io.Serializable;

public class Person implements Serializable {
	
	private String civility;
	private String firstName;
	private String name;
	private String email;
	private String title;
	
	public Person(){
		
	}
	
	public Person(String civility, String firstName, String name, String email, String title){
		
		this.civility  = civility;
		this.firstName = firstName;
		this.name      = name;
		this.email     = email;
		this.title     = title;
	}
	
	public void setCivility(String civility){
		this.civility = civility;
	}
	
	public String getCivility(){
		return civility;
	}

	public void setFirstName(String firstName){
		this.firstName = firstName;
	}
	
	public String getFirstName(){
		return firstName;
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public String getName(){
		return name;
	}
	
	public void setEmail(String email){
		this.email = email;
	}
	
	public String getEmail(){
		return email;
	}
	
	public void setTitle(String title){
		this.title = title;
	}
	
	public String getTitle(){
		return title;
	}
}
