/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.whiteboard;

import java.io.IOException;
import com.whiteboard.graphics.Board;
import com.whiteboard.graphics.ExceptionNotifier;
import com.whiteboard.graphics.Figure;
import com.whiteboard.graphics.MainView;
import com.whiteboard.net.NetworkControl;
import com.whiteboard.net.NetConstants;

/**
 * IMPORTANT: all exception handling is built in a very poor way as for 1.0.
 * Will still keep it this way because i'll figure out a better way in the
 * future (~1.3).
 * The control class synchronizes the view with the model(s).
 * This class is the first thing created at the start of the program and then
 * initializes everything else. This class will be kept as far away as possible
 * from being a GodClass.
 * @since 1.0
 * @version 1.0
 * @author lsdpirate
 */
public class Control {

   
    private final Board thisBoard;
        
    private NetworkControl networkControl;
    private final MainView view;
    private static Control thisInstance;
    private boolean connected = false;

    /**
     * Creates a new controller with a view, a board, a remote board and network
     * controller.
     */
    public Control() {

       
        thisInstance = this;
        this.view = new MainView();
        this.view.setVisible(true);
        this.thisBoard = new Board(this.view.getWidth(), this.view.getHeight());
        this.view.setBoard(thisBoard);
       
        Runtime.getRuntime().addShutdownHook(new Thread(){
        
            @Override
            public void run(){
                
                if(networkControl != null){
                    try {
                        networkControl.stopRunningService();
                    } catch (IOException ex) {
                        
                    } catch (InterruptedException ex) {
                        
                    }
                }
            }
        
        });
        
        
    }

    /**
     * Sends a figure to the connected client if connected.
     * @param fig A figure.
     */
    public void sendFigureToRemote(Figure fig) {
        
        if(!connected){return;}
            
        System.out.println("Sending...");
        try {
            this.networkControl.sendFigureData(fig);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        
    }

    /**
     * Adds a figure to the local board.
     * @param s A set of points making a figure.
     */
    public void insertFigureData(int[][] s) {
        System.out.println("Data received and parsed");
        Figure f = new Figure();

        for (int[] a : s) {

            f.addPoint(a[0], a[1]);
        }
        this.thisBoard.addGuestFigure(f);
       
       // this.remoteBoard.repaint();

    }

    /**
     * Makes the network controller listen for incoming connection.
     */
    public void startListeningSessions() {
        this.initializeNetwork();
        try {
            this.networkControl.startServerService(NetConstants.DEF_PORT);
            this.connected = this.networkControl.isConnectionEstablished();

        } catch (IOException ex) {
            String message = "Something went wrong initializing the server service.\n"
                    + "Try restarting the program. ex:{" + ex.getMessage() + "}";
            ExceptionNotifier.notifyException(message);
            
        } catch (InterruptedException ex) {
            String message = "Something went wrong initializing the server service.\n"
                    + "Try restarting the program. ex:{" + ex.getMessage() + "}";
            ExceptionNotifier.notifyException(message);
        }
    }

    /**
     * Makes the network controller connect to a remote server.
     * @param sessionIp The ip to connect to.
     */
    public void connectToSession(String sessionIp) {
        this.initializeNetwork();
        
        try {
            if(sessionIp == null || sessionIp.equals("")){
                throw new InvalidInputException("The entered ip is not correct");
            }
            this.networkControl.startClientService(sessionIp, NetConstants.DEF_PORT);
            this.connected = this.networkControl.isConnectionEstablished();
            
        } catch (IOException ex) {
            String message = "Something went wrong initializing the client service.\n"
                    + "Try restarting the program. \n ex:{" + ex.getMessage() + "}";
            ExceptionNotifier.notifyException(message);
            
        } catch (InterruptedException ex) {
              String message = "Something went wrong initializing the client service.\n"
                    + "Try restarting the program. \n ex:{" + ex.getMessage() + "}";
            ExceptionNotifier.notifyException(message);
        } catch(InvalidInputException ex){
                ExceptionNotifier.notifyException(ex.getMessage());
        }
    }

    /**
     * Makes the network controller connect to a remote server.
     * @param sessionIp The ip to connect to.
     * @param port The port for the connection.
     */
    public void connectToSession(String sessionIp, int port) {
        this.initializeNetwork();
        try {
            this.networkControl.startClientService(sessionIp, port);
            this.connected = this.networkControl.isConnectionEstablished();
            
        } catch (IOException ex) {
            String message = "Something went wrong initializing the client service.\n"
                    + "Try restarting the program. \n ex:{" + ex.getMessage() + "}";
            ExceptionNotifier.notifyException(message);
            
        } catch (InterruptedException ex) {
              String message = "Something went wrong initializing the client service.\n"
                    + "Try restarting the program. \n ex:{" + ex.getMessage() + "}";
            ExceptionNotifier.notifyException(message);
        }
    }

    private void initializeNetwork(){
               
        if(this.networkControl==null){
            this.networkControl = new NetworkControl();
            
        }
        
    }
 
    /**
     * Returns the current control instance.
     * @return The only instance of this class.
     */
    public static Control getInstance() {
        return thisInstance;
    }
    
    
    public void connectionClosed(){
    
        ExceptionNotifier.notifyException("Connection was closed");
    }
    
    
    public void connectionClosed(String message){
    
        ExceptionNotifier.notifyException(message);
    }
}
