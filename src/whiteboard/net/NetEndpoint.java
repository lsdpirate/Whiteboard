/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package whiteboard.net;

import java.io.IOException;

/**
 * Interface used to implement a method that has to send point tho whatever
 * is connected to the endpoint.
 * @since 1.0
 * @version 1.0
 * @author lsdpirate
 */
public abstract class NetEndpoint implements Runnable{
        
    private boolean stopRequested;
    public abstract void sendPoints(int [] [] points) throws IOException;
    public abstract void closeServices() throws IOException;
    
    public void requestStop(){
        stopRequested = true;
    }
    public boolean stopRequested(){
        return this.stopRequested;
    }
}
