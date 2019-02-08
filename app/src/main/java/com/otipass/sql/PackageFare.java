package com.otipass.sql;

/**
 * Created by emman on 19/10/2017.
 */

public class PackageFare {
    private int package_id;
    private int fare_id;
    private int option_id;
    private double price;

    public void setPrice(double price){
        this.price = price;
    }
    public double getPrice(){
        return price;
    }
    public int getOptionId() {
        return option_id;
    }
    public void setOptionId(int id) {
        this.option_id = id;
    }

    public int getFareId() {
        return fare_id;
    }
    public void setFareId(int id) {
        this.fare_id = id;
    }

    public int getPackageId() {
        return package_id;
    }
    public void setPackageId(int id) {
        this.package_id = id;
    }

    public PackageFare() {};
    public PackageFare(int idpackage, int idFare, int idOption, double price) {
        this.package_id = idpackage;
        this.fare_id = idFare;
        this.option_id = idOption;
        this.price = price;
    }
}
