/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package balancingrobot;

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
public class TCPServer extends Server{
    private final BufferedReader reader;
    private final DataOutputStream writer;
    private final ServerSocket serverSocket;
    private final Socket socket;
    
    public TCPServer(int port) throws IOException {
        super(port);
        
        serverSocket = new ServerSocket(port);
        socket = serverSocket.accept();

        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        writer = new DataOutputStream(socket.getOutputStream());
    }

    @Override
    public String readLine() {
        try {
            return reader.readLine();
        } catch (IOException ex) {
        }
        
        return "";
    }

    @Override
    public void writeLine(String line) {
        try {
            writer.writeBytes(line);
        } catch (IOException ex) {
        }
    }
}
