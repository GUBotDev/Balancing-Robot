/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package RobotHandler;

import java.io.IOException;


public class RobotHandler {
    private static final boolean BALANCER = true;
    static BotController botControl;
    
    /**
     * @param args
     * @throws InterruptedException
     * @throws IOException 
     */
    public static void main(String[] args) throws InterruptedException, IOException {
        h.ps("Start");
        
        if(!BALANCER){
            botControl = new BotController(false);
            
            h.ps("Pick up");

            Thread.sleep(2000);
            botControl.start();
            Thread.sleep(250);
            StepperThread.shutdown();
            h.ps("done");
        }
        else {
            Mpu6050Controller.initialize();
            botControl = new BotController(true);

            h.ps("Pick up");

            Thread.sleep(2000);
            botControl.start();
            Thread.sleep(250);
            StepperThread.shutdown();
            h.ps("done");
        }
    }
}

