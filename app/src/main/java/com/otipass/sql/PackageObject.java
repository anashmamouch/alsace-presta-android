package com.otipass.sql;

public class PackageObject {

	int id;
	String name;
	String ref;
	int nbChild;
	int duration;
	int period;
	double price;
	
	public PackageObject(){
		
	}
	
	public PackageObject(int id, String name, int nbChild, int duration, int period, double price, String ref){
		this.id = id;
		this.name = name;
		this.nbChild = nbChild;
		this.duration = duration;
		this.period = period;
		this.price = price;
		this.ref = ref;
	}

	public PackageObject(int id, String name, int nbChild, int duration, int period, String ref){
		this.id = id;
		this.name = name;
		this.nbChild = nbChild;
		this.duration = duration;
		this.period = period;
		this.ref = ref;
	}

	public void setId(int id){
		this.id = id;
	}
	
	public int getId(){
		return id;
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public String getName(){
		return name;
	}
	
	public void setNbChild(int nbChild){
		this.nbChild = nbChild;
	}
	
	public int getNbChild(){
		return nbChild;
	}
	
	public void setDuration(int duration){
		this.duration = duration;
	}
	
	public int getDuration(){
		return duration;
	}
	
	public void setPeriod(int period){
		this.period = period;
	}
	
	public int getPeriod(){
		return period;
	}
	
	public void setPrice(double price){
		this.price = price;
	}
	
	public double getPrice(){
		return price;
	}
	
	public void setRef(String ref){
		this.ref = ref;
	}
	
	public String getRef(){
		return ref;
	}
	
}
