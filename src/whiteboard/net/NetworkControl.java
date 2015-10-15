/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package whiteboard.net;

import java.io.IOException;

import whiteboard.Control;
import whiteboard.graphics.Figure;

/**
 * Network control is a controll class that manages connection and data between
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
     * This constructor is not used.
     *
     * @deprecated
     * @param serverIp
     * @param port
     * @throws IOException
     */
    public NetworkControl(String serverIp, int port) throws IOException {
        this();
        
    }

    /**
     * This method starts a server service listening on the specified port.
     *
     * @param port The port the server will listen to.
     * @throws IOException If the specified port is busy.
     * @throws InterruptedException If the endpoint thread doesn't return.
     */
    public void startServerService(int port) throws IOException, InterruptedException {

        if(this.endpoint != null){
            this.endpoint.closeServices();
            this.endpoint.requestStop();
            this.endpointThread.join();
        }
        this.endpoint = new Server(port);
        this.startRunningService();
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

        if(this.endpoint != null){
            this.endpoint.closeServices();
            this.endpoint.requestStop();
            this.endpointThread.join();
        }
        this.endpoint = new Client(ip, port);
        this.startRunningService();
    }

    /**
     * Private method that will run the asynchronous service on the endpoint.
     */
    private void startRunningService() {
        endpointThread = new Thread(this.endpoint);
        endpointThread.start();
        
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
        
        if(!this.isConnectionEstablished()){
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

        if (data.subSequence(0, 2).equals(NetConstants.FIGURE_DATA)) {
            this.parseFigureData(data.substring(2));
        }
    }

    /**
     * Helper method used from gotData() to parse the data.
     *
     * @param figureData A string of data.
     */
    private void parseFigureData(String figureData) {

        System.out.println(figureData);
        String[] pointsCouples = figureData.split(";");

        int[][] result = new int[pointsCouples.length][2];

        int i = 0;
        for (String s : pointsCouples) {
            String[] ps = s.split(",");
            result[i][0] = Integer.parseInt(ps[0]);
            result[i][1] = Integer.parseInt(ps[1]);
            ++i;
        }

        Control.getInstance().insertFigureData(result);
    }

    public void ExceptionOccurred(Exception e) {

    }

    public boolean isConnectionEstablished() {
        return this.endpoint!=null;

    }
}
