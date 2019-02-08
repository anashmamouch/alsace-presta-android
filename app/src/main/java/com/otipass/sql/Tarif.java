/**
================================================================================

    TARIF
    Pass Museum Application.

    SQLite package

    @copyright Copyright (c) Otipass 2011. All rights reserved.
    @author ED ($Author: ede $)

    @version $Rev: 2320 $
    $Id: Tarif.java 2320 2013-07-12 12:20:28Z ede $

================================================================================
*/
package com.otipass.sql;

public class Tarif {
	private long id;
	private int service;
	private double price;
	private String name_fr;
	private String name_de;
	
	public Tarif() {
		
	}

	public Tarif(int service, double price,  String name_fr, String name_de) {
		this.service = service;
		this.price = price;
		this.name_fr = name_fr;
		this.name_de = name_de;
	}
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getService() {
		return service;
	}

	public void setService(int service) {
		this.service = service;
	}


	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}


	public String getNameFr() {
		return name_fr;
	}

	public void setNameFr(String name_fr) {
		this.name_fr = name_fr;
	}

	public String getNameDe() {
		return name_de;
	}

	public void setNameDe(String name_de) {
		this.name_de = name_de;
	}
}
