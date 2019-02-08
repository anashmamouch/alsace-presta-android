package com.otipass.sql;

/**
 * Created by emman on 19/10/2017.
 */

public class Fare {
    private int id;
    private String name;
    private String reference;

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getReference() {
        return reference;
    }
    public void setReference(String reference) {
        this.reference = reference;
    }

    public Fare() {};

    public Fare(int id, String name, String reference) {
        this.id = id;
        this.name = name;
        this.reference = reference;
    }

}
