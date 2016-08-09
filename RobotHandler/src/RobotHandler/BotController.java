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
public class BotController {
    private final int runTime = 25;//seconds
    //private final String ip = "";
    private boolean bot;
    Balancer balancer;
    TriBot tribot;
    static ControlConnector connection;
    static ReadMPUThread mpuT = new ReadMPUThread();
    
    BotController(boolean bot) throws IOException{
        if(bot){
            balancer = new Balancer();
        }
        else {
            tribot = new TriBot();
            connection = new ControlConnector();
        }
        
        this.bot = bot;
    }
    
    public void start() throws IOException, InterruptedException{
        mpuT.start();
        long startTime = System.currentTimeMillis(); //fetch starting time
        boolean warning = false;
        long endTime = 0;
        
        h.ps("Waiting for Connection");
        
        while((System.currentTimeMillis() - startTime) < 1000 * runTime)
        {
            
            if(bot){
                balancer.turnOnMotors();
                balancer.balance();
                //balancer.rotationControl.steppers.setDrivingMode(1);
                //balancer.rotationControl.stepForward(10, 250000);
            }
            else{
                boolean[] vals = connection.readControls();
                
                tribot.setUp(vals[0]);
                tribot.setDown(vals[1]);
                tribot.setLeft(vals[3]);
                tribot.setRight(vals[2]);
                
                tribot.checkControl();
            }
            
            if (!warning && (System.currentTimeMillis() - startTime) > (1000 * runTime) - 5000){
                h.ps("STOPPING IN 5 SECONDS.");
                
                warning = true;
            }
        }
        
        endTime = System.currentTimeMillis();
        
        long time = endTime - startTime;
        
        h.ps("Max steps per ms: " + time);
    }
}

/*
try {
    KeyboardHandler.setTerminalToCBreak();

    if (System.in.available() != 0) {
        //System.in.skip(System.in.available());

        //clears extra keys
        int temp = 0;
        while (System.in.available() != 0) {
            temp = System.in.read();
        }
        if (temp == 119) {
            tribot.setUp(true);
        }
        if (temp == 115) {
            tribot.setDown(true);
        }
        if (temp == 97) {
            tribot.setLeft(true);
        }
        if (temp == 100) {
            tribot.setRight(true);
        }
    }
    else{
        tribot.setUp(false);
        tribot.setDown(false);
        tribot.setLeft(false);
        tribot.setRight(false);
    }
}
catch (Exception e) {
    System.err.println("IOException: " + e.getMessage());
}
finally {
    try {
        KeyboardHandler.stty( KeyboardHandler.returnTTY().trim() );
    }
    catch (Exception e) {
        System.err.println("Exception restoring tty config");
    }
}
*/