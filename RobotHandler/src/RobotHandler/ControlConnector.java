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
public class ControlConnector implements Runnable{
    TCPServer controller;
    final int port = 6789;
    boolean up = false, down = false, left = false, right = false;
    static Thread t;
    String readLine = "";
    boolean tcpStart = false;
    boolean wait = true;
    
    ControlConnector(boolean wait) throws IOException{
        if(wait){
            controller = new TCPServer(port);
            
            //tcpStart = true;
        }
    }
    
    @Override
    public void run() {
        while(true){
            try {
                if(!tcpStart){
                    if(controller == null){
                        controller = new TCPServer(port);
                    }
                    
                    tcpStart = true;
                }
                
                readLine = controller.readFromClient();
            }
            catch(Exception ex){}
        }
    }
    
    public void start(){
        if(t == null){
            t = new Thread(this, "Connector");
            t.start();
        }
    }
    
    public boolean[] readControls() throws IOException{
        //requestControls();
        
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
