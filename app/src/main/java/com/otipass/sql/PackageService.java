package com.otipass.sql;

public class PackageService {

	int package_id;
	int service_id;
	int number;

	public PackageService(){

	}

	public PackageService(int package_id, int service_id, int number){
		this.package_id = package_id;
		this.service_id = service_id;
		this.number     = number;
	}

	public void setPackageId(int package_id){
		this.package_id = package_id;
	}

	public int getPackageId(){
		return package_id;
	}

	public void setServiceId(int service_id){
		this.service_id = service_id;
	}

	public int getServiceId(){
		return service_id;
	}

	public void setNumber(int number){
		this.number = number;
	}
	
	public int getNumber(){
		return number;
	}
}
