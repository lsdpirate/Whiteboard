/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package whiteboard;

import java.io.IOException;
import whiteboard.graphics.Board;
import whiteboard.graphics.Figure;
import whiteboard.graphics.MainView;
import whiteboard.net.NetworkControl;
import whiteboard.net.NetConstants;

/**
 *
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
            System.out.println("ex");
            System.out.println(ex.getMessage());
        } catch (InterruptedException ex) {
            //Send error to gui
        }
    }

    /**
     * Makes the network controller connect to a remote server.
     * @param sessionIp The ip to connect to.
     */
    public void connectToSession(String sessionIp) {
        this.initializeNetwork();
        
        try {
            
            this.networkControl.startClientService(sessionIp, NetConstants.DEF_PORT);
            this.connected = this.networkControl.isConnectionEstablished();
            
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
            
        } catch (InterruptedException ex) {
            //Send error to gui
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
            System.out.println(ex.getMessage());
        } catch (InterruptedException ex) {
            //Send error to gui.
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
}
