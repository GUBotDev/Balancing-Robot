/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package RobotHandler;

import java.io.IOException;


public class RobotHandler {
    private static final boolean tribot = false;
    private static final boolean balancer = true;
    
    /**
     * @param args
     * @throws InterruptedException
     * @throws IOException 
     */
    public static void main(String[] args) throws InterruptedException, IOException {
        h.ps("Start");
        
        if(tribot){
            BotController botControl = new BotController(false);
            
            h.ps("Pick up");

            Thread.sleep(2000);
            botControl.start();
            Thread.sleep(250);
            botControl.tribot.rotationControl.shutdown();
            h.ps("done");
        }
        else if (balancer){
            Mpu6050Controller.initialize();
            BotController botControl = new BotController(true);

            h.ps("Pick up");

            Thread.sleep(2000);
            botControl.start();
            Thread.sleep(250);
            botControl.balancer.rotationControl.shutdown();
            h.ps("done");
        }
    }
}

