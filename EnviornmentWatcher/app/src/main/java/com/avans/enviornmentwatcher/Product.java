package com.avans.enviornmentwatcher;

import java.util.HashMap;

/**
 * Model class of a product
 */
public class Product {
    private int id;                                     //The ID seperates the product from eachother
    private String location, room;                      //Names of where the sensor is located
    public Product()
    {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

}
