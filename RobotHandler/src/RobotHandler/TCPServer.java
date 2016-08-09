/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package RobotHandler;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author Rollie
 */
public class TCPServer {
    private final BufferedReader reader;
    private final DataOutputStream writer;
    private final ServerSocket serverSocket;
    private final Socket socket;
    
    TCPServer(int port) throws IOException{
        serverSocket = new ServerSocket(port);
        socket = serverSocket.accept();
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        writer = new DataOutputStream(socket.getOutputStream());
    }
    
    public void writeToClient(String output) throws IOException{
        writer.writeBytes(output);
    }
    
    public String readFromClient() throws IOException{
        return reader.readLine();
    }
}
