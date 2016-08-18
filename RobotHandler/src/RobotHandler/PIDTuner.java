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
public class PIDTuner implements Runnable{
    static TCPServer controller;
    static final int port = 6790;
    static Thread t;
    static float kPD, kDD, kPSD, kISD, angleOffset;
    static boolean newPID = false;
    static boolean isConnected = false;
    
    public static void connect() throws IOException{
        controller = new TCPServer(port);
        isConnected = true;
    }
    
    @Override
    public void run() {
        while(true){
            try {
                if(isConnected){
                    String input = controller.readFromClient();

                    String[] split = input.split(",");

                    if(split.length == 5){
                        kPD = Float.parseFloat(split[0]);
                        kDD = Float.parseFloat(split[1]);
                        kPSD = Float.parseFloat(split[2]);
                        kISD = Float.parseFloat(split[3]);
                        angleOffset = Float.parseFloat(split[4]);
                    }

                    newPID = true;
                }
                
                Thread.sleep(1);
            }
            catch (IOException ex) {}
            catch (InterruptedException ex) {}
        }
    }
    
    public void start(){
        if(t == null){
            t = new Thread(this, "PID");
            t.start();
        }
    }
    
    public static boolean checkPID(){
        if(newPID){
            newPID = false;
            
            return true;
        }
        else{
            return false;
        }
    }
    
    public static float returnKPD(){
        return kPD;
    }
    
    public static float returnKDD(){
        return kDD;
    }
    
    public static float returnKPSD(){
        return kPSD;
    }
    
    public static float returnKISD(){
        return kISD;
    }
    
    public static float returnAngleOffset(){
        return angleOffset;
    }
}
