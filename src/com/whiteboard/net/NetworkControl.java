
package com.whiteboard.net;

import java.io.IOException;

import com.whiteboard.Control;
import com.whiteboard.graphics.Figure;
import com.whiteboard.graphics.ExceptionNotifier;

/**
 * Network control is a control class that manages connection and data between
 * the local application and the remote.
 *
 * @since 1.0
 * @version 1.0
 * @author lsdpirate
 */
public class NetworkControl {

    private static NetworkControl thisInstance;
    private Thread endpointThread;
    private NetEndpoint endpoint;


    /**
     * This getter returns the current istance for this class. This is very
     * useful for server/client instances that need to comunicate with the
     * network control.
     *
     * @return The current and only instance of this class.
     */
    public static NetworkControl getInstance() {
        return thisInstance;
    }

    public NetworkControl() {

        if (thisInstance == null) {

            thisInstance = this;
        }

    }

    /**
     * This method starts a server service listening on the specified port.
     *
     * @param port The port the server will listen to.
     * @throws IOException If the specified port is busy.
     * @throws InterruptedException If the endpoint thread doesn't return.
     */
    public void startServerService(int port) throws IOException, InterruptedException {

        stopRunningService();
        this.endpoint = new Server(port);
        endpointThread = new Thread(endpoint);
        endpointThread.start();
        this.pollEndpointThreads();

    }

    /**
     * Call this method to establish a connection to a server.
     *
     * @param ip The ip to the server
     * @param port The port for the connection
     * @throws IOException If something goes wrong with the connection attempt.
     * @throws InterruptedException If the endpoint thread doesn't return.
     */
    public void startClientService(String ip, int port) throws IOException, InterruptedException {
        stopRunningService();
        this.endpoint = new Client(ip, port);
        endpointThread = new Thread(endpoint);
        endpointThread.start();
        this.pollEndpointThreads();

    }

    /**
     * Stops the endpoints services and all monitors attached to it.
     * @throws IOException -
     * @throws InterruptedException - 
     */
    public void stopRunningService() throws IOException, InterruptedException {
        if (this.endpoint != null) {
            this.endpoint.requestStop();

            //We want to wait long enought for the endpoint monitor to stop
            //without giving pointer errors. In the future we will use a more
            //elegant solution.
            Thread.sleep(100);

            this.endpoint.closeServices();
            this.endpoint = null;

        }
    }

    /**
     * This method will get a Figure object, parse it into Ints and send them
     * over the connection.
     *
     * @param fig The figure to send.
     * @throws IOException If something goes wrong while sending.
     * @throws IOException If there are no connections.
     */
    public void sendFigureData(Figure fig) throws IOException {

        if (!this.isConnectionEstablished()) {
            throw new IOException("No remote session is running");
        }
        int size = fig.getxPoints().size();
        int toSendData[][] = new int[size][2];

        for (int i = 0; i < size; i++) {

            toSendData[i][0] = fig.getxPoints().get(i);
            toSendData[i][1] = fig.getyPoints().get(i);
        }

        this.endpoint.sendPoints(toSendData);
    }

    /**
     * Notification method used from the server class to tell the network
     * controller that data arrived and needs to be parsed. It will then send
     * the parsed figure to the Control class that will instert it into the
     * board.
     *
     * @param data The received data to parse.
     */
    public void gotData(String data) {

        if (data.subSequence(0, 2).equals(NetConstants.FIGURE_DATA_FLAG)) {
            this.parseFigureData(data.substring(2));

        } else if (data.equals(NetConstants.KEEP_ALIVE_FLAG)) {
            System.out.println("Keep alive received");
            this.endpoint.stillAlive();

        }
    }

    /**
     * Helper method used from gotData() to parse the data.
     *
     * @param figureData A string of data.
     */
    private void parseFigureData(String figureData) {

        //Get the points couples from the string (x,y)
        String[] pointsCouples = figureData.split(";");

        //Make a matrix to store the parsing results. The matrix will be
        //n*2 where n is the number of couples.
        int[][] result = new int[pointsCouples.length][2];

        //For each couple, store it into the matrix
        int i = 0;
        for (String s : pointsCouples) {
            String[] ps = s.split(",");
            result[i][0] = Integer.parseInt(ps[0]);
            result[i][1] = Integer.parseInt(ps[1]);
            ++i;
        }

        Control.getInstance().insertFigureData(result);
    }

    /**
     * NOT QUITE CORRECT
     * @return 
     */
    public boolean isConnectionEstablished() {
        return this.endpoint != null;

    }

    /**
     * Method called from the endpoint when the connection is lost.
     */
    void connectionLost() {
        try {
            stopRunningService();
            ExceptionNotifier.notifyException("Connection was lost.");
        } catch (IOException ex) {

        } catch (InterruptedException ex) {

        }

    }

    /**
     * Starts a thread that monitors the endpoint status and informs the user
     * whenever something occurs.
     */
    private void pollEndpointThreads() {

        abstract class simpleRunnable implements Runnable {

        }
        Thread r = new Thread(new simpleRunnable() {
            @Override
            public void run() {

                while (true) {

                    if (endpoint.stopRequested()) {
                        break;
                    }
                    if (!endpointThread.isAlive()) {
                        if (endpoint.getCause() == ServiceStopCause.END_OF_CONNECTION) {

                            Control.getInstance().connectionClosed("Connection was closed");
                            break;

                        } else if (endpoint.getCause() == ServiceStopCause.CONNECTION_TIMED_OUT) {

                            Control.getInstance().connectionClosed("Connection timed out");
                            break;
                        }
                    }

                }
            }
        });
        r.start();

    }

//    void connectionLost() {
//
//        try {
//            this.stopServices();
//            Control.getInstance().connectionClosed();
//            System.out.println("Connection was lost");
//        } catch (IOException ex) {
//
//        } catch (InterruptedException ex) {
//
//        }
//
//    }
    /*
     private class EndpointsTracker implements Runnable {

     private ArrayList<NetEndpoint> endpoints;
     private ArrayList<Thread> endpointsThreads;
     private NetEndpoint server;
     private Thread serverThread;

     public EndpointsTracker() {
     endpoints = new ArrayList<>();
     endpointsThreads = new ArrayList<>();
     }

     public void addEndpoint(NetEndpoint e) {

     Thread t = new Thread(e);
     if (e instanceof Server) {
     server = e;
     serverThread = t;

     } else {
     endpoints.add(e);
     endpointsThreads.add(t);

     }
     t.start();
     }

     @Override
     public void run() {
     while (true) {

     try {
     for (int i = 0; i < endpointsThreads.size() || !endpointsThreads.isEmpty(); ++i) {
     Thread t = endpointsThreads.get(i);

     try {
     t.join(1000);
     if (!t.isAlive()) {
     NetEndpoint e = endpoints.get(i);
     if (e.getCause() == ServiceStopCause.END_OF_CONNECTION) {
     Control.getInstance().connectionClosed("The connection was closed");

     } else if (e.getCause() == ServiceStopCause.CONNECTION_TIMED_OUT) {
     Control.getInstance().connectionClosed("The connection timed out");

     }
     e.closeServices();
     endpoints.remove(e);
     endpointsThreads.remove(t);
     e = null;
     t = null;

     }
     } catch (InterruptedException ex) {

     } catch (IOException ex) {

     }
     }
     if (server != null) {
     serverThread.join(1000);
     if (!serverThread.isAlive()) {
     switch (server.getCause()) {

     case END_OF_CONNECTION:
     Control.getInstance().connectionClosed("The connection was closed");
     case CONNECTION_TIMED_OUT:
     Control.getInstance().connectionClosed("The connection timed out");
     default:

     }
     server.closeServices();
     serverThread = null;
     server = null;
     }
     }
     } catch (InterruptedException ex) {

     } catch (IOException ex) {
                   
     }
     }
     }
    
     }
     */
}
