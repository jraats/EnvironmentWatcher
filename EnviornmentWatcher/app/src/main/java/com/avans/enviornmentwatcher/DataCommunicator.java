package com.avans.enviornmentwatcher;

import android.provider.ContactsContract;

/**
 * Created by Raoul-Laptop on 11-12-2015.
 */
public class DataCommunicator {
    private static DataCommunicator dataCommunicator;
    private User user;

    private DataCommunicator() {

    }

    // returns the class and makes it if it isn't created yet
    public static DataCommunicator getInstance() {
        if(null == dataCommunicator) {
            dataCommunicator = new DataCommunicator();
        }
        return dataCommunicator;
    }

    public void createUser(String username, String apiKey)    {
        user = new User(username, apiKey);
    }


    public User getUser(){
        return user;
    }


}
