/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package whiteboard.net;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Arrays;

/**
 * The server class will be instantiated when the user want to host a session
 * of remote drawing. When instantiated it will listen to the local port for
 * connection and data. For version 1.0 this class will only be able to keep
 * track of 1 client only.
 * @author lsdpirate
 */
public class Server extends NetEndpoint{
    
    private ServerSocket socket;
    private Socket client;
    private InetSocketAddress bindingInfos;
    private BufferedReader input;
    
    /**
     * Private constructor to keep the use of this class pretty straightforward
     */
    private Server(){}
    
    /**
     * Creates server and starts listening
     * @param port to listen to.
     * @throws java.io.IOException If the port is already bind.
     */
    public Server(int port) throws IOException{
        
        this.bindingInfos = new InetSocketAddress("127.0.0.1", port);
        this.socket = new ServerSocket(this.bindingInfos.getPort());
        this.startListening();
        
    }
    
    /**
     * This will make the ServerSocket start listening to the specified port.
     * @throws IOException If an I/O error occurs while waiting a connection.
     */
    private void startListening() throws IOException{
        this.client = this.socket.accept();
        input = new BufferedReader(new InputStreamReader(this.client.getInputStream()));
        System.out.println("A client connected");
        
    }

    /**
     * Method used to send points to the connected client.
     * @param points The points to send.
     * @throws IOException If an I/O error occurs while sending the data.
     */
    public void sendPoints(int [] [] points) throws IOException{
        
        System.out.println("Sending data: " + Arrays.toString(points));
        String toSendData = NetConstants.FIGURE_DATA;
        
        for(int i []: points){
            int x, y;
            x = i[0];
            y = i[1];
            toSendData += x + "," + y + ";";
        }
        PrintWriter out = new PrintWriter(this.client.getOutputStream(), true);
        out.println(toSendData);
    }
    
    /**
     * Closes the server.
     * @throws IOException 
     */
    @Override
    public void closeServices() throws IOException{
        this.socket.close();
    }
    @Override
    public void run() {
        
        while(!this.socket.isClosed() || !this.stopRequested()){
           // System.out.println("Polling...");
        try {
            
            
            while(input.ready()){
                String message = input.readLine();
                NetworkControl.getInstance().gotData(message);
                System.out.println("Received: " + message);
            }
        } catch (IOException ex) {
            //Notify network controller somehting occured.
            System.out.println(ex.getMessage());
        }
    }}
    
    
}
