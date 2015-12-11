package com.avans.enviornmentwatcher;

/**
 * Created by Raoul-Laptop on 11-12-2015.
 */
public class User {
    private String username, apiKey;
    int productID;
    int lightPreference,temperaturePreference;


    public User(String username, String apiKey){
        this.username = username;
        this.apiKey = apiKey;
    }

    public String getUsername() {
        return username;
    }


    public String getApiKey() {
        return apiKey;
    }

    public int getProductID() {
        return productID;
    }

    public void setProductID(int productID) {
        this.productID = productID;
    }

    public int getLightPreference() {
        return lightPreference;
    }

    public void setLightPreference(int lightPreference) {
        this.lightPreference = lightPreference;
    }

    public int getTemperaturePreference() {
        return temperaturePreference;
    }

    public void setTemperaturePreference(int temperaturePreference) {
        this.temperaturePreference = temperaturePreference;
    }
}
