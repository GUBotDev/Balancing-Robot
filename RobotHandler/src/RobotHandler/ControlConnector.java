/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package RobotHandler;

import java.io.IOException;

/**
 *
 * @author Rollie
 */
public class ControlConnector {
    final TCPServer controller;
    final int port = 6789;
    
    ControlConnector() throws IOException{
        controller = new TCPServer(port);
    }
    
    public boolean[] readControls() throws IOException{
        boolean up = false, down = false, left = false, right = false;
        //requestControls();
        
        String readLine = controller.readFromClient();
        
        String[] split = readLine.split(",");
        
        if(split.length == 4){
            up = split[0].equals("1");
            down = split[1].equals("1");
            left = split[2].equals("1");
            right = split[3].equals("1");
        }
        
        return new boolean[] {up, down, left, right};
    }
    
    private void requestControls() throws IOException{
        controller.writeToClient("Write Controls\n");
    }
}
