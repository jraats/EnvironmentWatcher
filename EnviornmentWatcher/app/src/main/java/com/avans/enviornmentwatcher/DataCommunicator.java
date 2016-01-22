package com.avans.enviornmentwatcher;

/**
 * Singleton Class that stores the user.
 */
public class DataCommunicator {
    private static DataCommunicator dataCommunicator;   /**< Singleton */
    private User user;                                  /**< The user's name and API key will be saved here */

    private DataCommunicator() {

    }
    /*! \singleton creator
     *
     *  returns the class and makes it if it isn't created yet
     */
    public static DataCommunicator getInstance() {
        if(null == dataCommunicator) {
            dataCommunicator = new DataCommunicator();
        }
        return dataCommunicator;
    }

    /*! \Creates user
     *  @param username the name of the user
     *  @param apiKey the key needed for communication
     *  Creates a user with APIKEY for communication
     */
    public void createUser(String username, String apiKey)    {
        user = new User(username, apiKey);
    }

    /*! \Returns user
     *
     *  Returns the saved user
     */
    public User getUser(){
        return user;
    }


}
