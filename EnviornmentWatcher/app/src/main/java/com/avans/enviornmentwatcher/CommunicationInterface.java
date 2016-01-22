package com.avans.enviornmentwatcher;

/*! Interface class responsible for waiting on response */
public interface CommunicationInterface<T>
{
    /*! \Response Listener
     *  @param object whatever you want it to return
     *  After sending a request it will wait for a response
     */
    public void getResponse(T object);
}
