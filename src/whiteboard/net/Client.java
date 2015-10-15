/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package whiteboard.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.InetSocketAddress;


/**
 * The client class is used by the application whenever the user wants to 
 * connect to a server. It's usage is simple: instantiate the class with
 * infos about the server to connect to and the client will run asynchronously.
 * A method to send points to the server is also present.
 * @author lsdpirate
 * @since 1.0
 * @version 1.0
 */
public class Client extends NetEndpoint {

    private Socket socket;
    private InetSocketAddress hostInfos;
    private BufferedReader input;
    

    /**
     * Private default constructor to make the use of this class simple and
     * straightforward (its coding too :) ).
     */
    private Client() {
    }

    /**
     * Creates a client class that will connect to the specified server.
     * @param serverIp The server ip.
     * @param port The port to connect to.
     * @throws IOException If something unexpected happens during
     * the connection.
     */
    public Client(String serverIp, int port) throws IOException {

        this.hostInfos = new InetSocketAddress(serverIp, port);
        this.connect();
    }

    /**
     * Private void used to connect to the server specified to the constructor.
     * @throws IOException If something unexpected happens during the connection
     * or if the inputstream isn't aviabile.
     */
    private void connect() throws IOException {
        this.socket = new Socket();
        this.socket.connect(hostInfos);
        this.input = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
    }
    
    /**
     * Public method that will send points to the connected server.
     * @param points The points to send 
     * @throws IOException if an I/O error occurs when creating the output 
     * stream or if the socket is not connected. 
     */
    @Override
    public void sendPoints(int [] [] points) throws IOException{
        
        System.out.println("Sending effective data...");
        String toSendData = NetConstants.FIGURE_DATA;
        
        for(int [] i:points){
        
            int x, y;
            x = i[0];
            y = i[1];
            toSendData += x + "," + y + ";";
        }
        
        PrintWriter out = new PrintWriter(this.socket.getOutputStream(), true);
        out.println(toSendData);
        System.out.println("Data sent! :" + toSendData);
        
    }

    /**
     * In this overridden method of the class Runnable happens the main loop of
     * the client. Each time the client will check if data has arrived and will 
     * send it to the controller instance that will route it.
     */
    @Override
    public void run() {

        while (!this.socket.isClosed() ||!this.stopRequested()) {
            try {
                while (this.input.ready()) {
                    String data = input.readLine();
                    NetworkControl.getInstance().gotData(data);
                    System.out.println("Received :" + data);
                }
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

    
    @Override
    public void closeServices() throws IOException {
        this.socket.close();
        this.input.close();
        
    }

  

}
