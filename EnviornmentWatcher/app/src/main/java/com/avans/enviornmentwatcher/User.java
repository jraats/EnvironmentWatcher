package com.avans.enviornmentwatcher;

/**
 * Model class of a user
 */
public class User {
    private String username, apiKey;
    int productID;
    int lightPreference;
    double temperaturePreference;


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

    public double getTemperaturePreference() {
        return temperaturePreference;
    }

    public void setTemperaturePreference(double temperaturePreference) {
        this.temperaturePreference = temperaturePreference;
    }
}
