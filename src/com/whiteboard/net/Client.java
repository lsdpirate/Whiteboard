
package com.whiteboard.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.InetSocketAddress;

/**
 * The client class is used by the application whenever the user wants to
 * connect to a server. It's usage is simple: instantiate the class with infos
 * about the server to connect to and the client will run asynchronously. A
 * method to send points to the server is also present.
 *
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
     *
     * @param serverIp The server ip.
     * @param port The port to connect to.
     * @throws IOException If something unexpected happens during the
     * connection.
     */
    public Client(String serverIp, int port) throws IOException {

        this.hostInfos = new InetSocketAddress(serverIp, port);
        this.connect();
    }

    /**
     * Private void used to connect to the server specified to the constructor.
     *
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
     *
     * @param points The points to send
     * @throws IOException if an I/O error occurs when creating the output
     * stream or if the socket is not connected.
     */
    @Override
    public void sendPoints(int[][] points) throws IOException {

        
        String toSendData = NetConstants.FIGURE_DATA_FLAG;

        for (int[] i : points) {

            int x, y;
            x = i[0];
            y = i[1];
            toSendData += x + "," + y + ";";
        }

        PrintWriter out = new PrintWriter(this.socket.getOutputStream(), true);
        out.println(toSendData);
        

    }
     /**
     * Sends raw data to the connected server.
     * @param data Raw data in form of a string.
     * @throws IOException Occurs if sending the data was impossible
     */
    @Override
    public void sendRawData(String data) throws IOException {
        PrintWriter out = new PrintWriter(this.socket.getOutputStream(), true);
        out.println(data);
    }

      
    @Override
    public void run() {
        sinceLastKeepAliveResponse = System.currentTimeMillis() / 1000 + 15;
        while (!this.socket.isClosed() || !this.stopRequested()) {

           if(doKeepAliveRoutine() < 0){
              break;
              
           }

            try {
                while (this.input.ready()) {
                    String data = input.readLine();
                    NetworkControl.getInstance().gotData(data);

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
