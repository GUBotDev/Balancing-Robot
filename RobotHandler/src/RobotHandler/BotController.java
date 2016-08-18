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
    private final int runTime = 120;//secondsS
    private final boolean BALANCER;
    private final boolean customControl = false;
    private static ControlConnector connection;
    private static ReadMPUThread mpuT;
    public static PIDTuner pidT;
    public static StepperThread steppersT;
    boolean warning;
    boolean waitForConnection = true;
    long startTime;
    long time;
    Robot robot;
    
    BotController(boolean balancer) throws IOException{
        robot = new Robot();
        mpuT = new ReadMPUThread();
        steppersT = new StepperThread();
        pidT = new PIDTuner();
        
        h.ps("Waiting for connection...");
        
        connection = new ControlConnector(waitForConnection);
        
        this.BALANCER = balancer;
    }
    
    public void start() throws IOException, InterruptedException{
        startTime = System.currentTimeMillis(); //fetch starting time
        warning = false;
        mpuT.start();
        steppersT.start();
        pidT.start();
        connection.start();
        
        robot.turnOnMotors();
        robot.setBalancer(BALANCER);
        
        while((System.currentTimeMillis() - startTime) < 1000 * runTime)
        {
            if(customControl){
                robot.control();
            }
            else{
                try{
                    boolean[] vals = connection.readControls();

                    robot.setUp(vals[0]);
                    robot.setDown(vals[1]);
                    robot.setLeft(vals[3]);
                    robot.setRight(vals[2]);

                    robot.checkControl();
                }
                catch(Exception ex){}
                
                if(BALANCER){
                    robot.balance();
                }
            }
            
            if (!warning && (System.currentTimeMillis() - startTime) > (1000 * runTime) - 5000){
                h.ps("STOPPING IN 5 SECONDS.");
                
                warning = true;
            }
        }
    }
}