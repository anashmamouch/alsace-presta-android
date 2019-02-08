/**
================================================================================

    DISCOUNT
    Pass Museum Application.

    SQLite package

    @copyright Copyright (c) Otipass 2011. All rights reserved.
    @author ED ($Author: ede $)

    @version $Rev: 4152 $
    $Id: Discount.java 4152 2014-09-11 12:21:57Z ede $

================================================================================
*/
package com.otipass.sql;

public class Discount {
	private double amountEUR;
	private double amountFCH;
	private String startDate;
	private String endDate;
	
	public Discount() {
		
	}

	public Discount(double amountEUR, double amountFCH, String startDate, String endDate) {
		this.amountEUR = amountEUR;
		this.amountFCH = amountFCH;
		this.startDate = startDate;
		this.endDate = endDate;
	}
	

	public double getAmountEUR() {
		return amountEUR;
	}

	public void setAmountEUR(double amountEUR) {
		this.amountEUR = amountEUR;
	}


	public double getAmountFCH() {
		return amountFCH;
	}

	public void setAmountFCH(double amountFCH) {
		this.amountFCH = amountFCH;
	}


	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
}
