/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package balancingrobot;

import java.io.IOException;

/**
 *
 * @author Rollie
 */
public class Controller extends TCPServer{
    private Vector motion;
    
    public Controller(int port) throws IOException {
        super(port);
    }
    
    public void readControls(){
        readLine();
        
        
    }
    
    public Vector getMotion(){
        return motion;
    }
    
}
