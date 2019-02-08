package com.otipass.sql;

public class ProviderService {

	int package_id;
	String service; 

	public ProviderService(){

	}

	public ProviderService(int package_id, String service){
		this.package_id = package_id;
		this.service = service;
	}

	public void setPackageId(int package_id){
		this.package_id = package_id;
	}

	public int getPackageId(){
		return package_id;
	}

	public void setService(String service){
		this.service = service;
	}

	public String getService(){
		return service;
	}

}
