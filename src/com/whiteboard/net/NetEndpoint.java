package com.whiteboard.net;

import java.io.IOException;
/**
 * Interface used to implement a method that has to send point tho whatever is
 * connected to the endpoint.
 *
 * @since 1.0
 * @version 1.0
 * @author lsdpirate
 */
public abstract class NetEndpoint implements Runnable {

    private boolean stopRequested;
    
    //By default we want to believe the connection is stopped for normal reasons
    private ServiceStopCause stopCause = ServiceStopCause.END_OF_CONNECTION; 
   
    /**
     * Return the cause for which the service stopped running.
     * @return The reason the service stopped.
     */
    public ServiceStopCause getCause(){return stopCause;}
    
    /**
     * Set the cause of the service termination.
     * @param ssc The cause.
     */
    protected void setCause(ServiceStopCause ssc){stopCause = ssc;}
    
    /**
     * Sends the points to the connected endpoint.
     * @param points The points to send.
     * @throws IOException Occurs if sending the data was impossible.
     */
    public abstract void sendPoints(int[][] points) throws IOException;

    /**
     * Sends raw data to the connected endpoint.
     * @param data Raw data in form of a string.
     * @throws IOException Occurs if sending the data was impossible
     */
    public abstract void sendRawData(String data) throws IOException;

    /**
     * Closes the running services for the current endpoint.
     * @throws IOException -
     */
    public abstract void closeServices() throws IOException;

    protected long sinceLastKeepAliveResponse = Long.MAX_VALUE;
    protected long nextKeepAlive = System.currentTimeMillis() / 1000;

    /**
     * Tells the connected endpoint that the connection is still alive
     * by sending a keep alive.
     * @throws IOException Occurs if the data couldn't be sent.
     */
    protected void checkIfAlive() throws IOException {

        sendRawData(NetConstants.KEEP_ALIVE_FLAG);
        this.nextKeepAlive = System.currentTimeMillis() / 1000 + 3;
        System.out.println("Keep alive sent:" + this.nextKeepAlive);

    }

    /**
     * Used to set the last keep alive packet income.
     */
    protected void stillAlive() {
        sinceLastKeepAliveResponse = System.currentTimeMillis() / 1000;
    }
    /**
     * Executes the keep alive routine by sending a keep alive and parsing any
     * that may come from the connected endpoint.
     * If the time since the last received exceeds the limit, the method
     * will return a negative value.
     * @return n < 0 if the keep alive expired. 0 if the connection still
     * exists.
     */
    protected int doKeepAliveRoutine(){
                int result = 0;
         
                if (nextKeepAlive < System.currentTimeMillis() / 1000) {

                    try {
                        this.checkIfAlive();
                    } catch (IOException ex) {
                        setCause(ServiceStopCause.CONNECTION_TIMED_OUT);
                       result = -1;
                        
                        
                    }
                }
                
                if(sinceLastKeepAliveResponse + 10 < (System.currentTimeMillis() / 1000)){
                    System.out.println("Has connection timed out? " + sinceLastKeepAliveResponse
                    + " " + System.currentTimeMillis() / 1000 + 10);
                    setCause(ServiceStopCause.CONNECTION_TIMED_OUT);
                   result = -1;
                }
                return result;
    }

    /**
     * Sets the stop flag to true.
     */
    public void requestStop() {
        stopRequested = true;
    }

    /**
     * Returns the stop flag status.
     * @return Stop flag status.
     */
    public boolean stopRequested() {
        return this.stopRequested;
    }
}
