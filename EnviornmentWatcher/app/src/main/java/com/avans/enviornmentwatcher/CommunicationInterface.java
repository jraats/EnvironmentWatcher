package com.avans.enviornmentwatcher;

/**
 * Created by Raoul-Laptop on 11-12-2015.
 */
public interface CommunicationInterface<T>
{
    //Response Listener
    public void getResponse(T object);
}
