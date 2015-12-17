package com.avans.enviornmentwatcher;

import java.util.HashMap;

/**
 * Created by Raoul-Laptop on 11-12-2015.
 */
public class Product {
    private int id;
    private String location, room;
    private HashMap<String, String> temperature;
    private HashMap<String, String> light;

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

    public HashMap<String, String> getTemperature() {
        return temperature;
    }

    public void setTemperature(HashMap<String, String> temperature) {
        this.temperature = temperature;
    }

    public HashMap<String, String> getLight() {
        return light;
    }

    public void setLight(HashMap<String, String> light) {
        this.light = light;
    }
}
