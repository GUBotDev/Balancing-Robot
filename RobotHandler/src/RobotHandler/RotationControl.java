/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package RobotHandler;

import com.pi4j.io.gpio.RaspiPin;

/**
 *
 * @author Rollie
 */
public class RotationControl {
    Steppers steppers;
    
    float leftRatio = 1.0f;
    float rightRatio = 1.0f;
    
    RotationControl() {
        steppers = new Steppers(1,
            RaspiPin.GPIO_15,RaspiPin.GPIO_16,RaspiPin.GPIO_01,RaspiPin.GPIO_04, 
            RaspiPin.GPIO_05,RaspiPin.GPIO_06,RaspiPin.GPIO_10,RaspiPin.GPIO_11,
            RaspiPin.GPIO_12,RaspiPin.GPIO_13,RaspiPin.GPIO_14,RaspiPin.GPIO_21, 
            RaspiPin.GPIO_22,RaspiPin.GPIO_23,RaspiPin.GPIO_24,RaspiPin.GPIO_25);
        
        /*
        STEPPER PINOUT:
        
        L
        Direction 15
        Step 16
        Sleep 1
        Reset 4
        MS3 N/A
        MS2 5
        MS1 6
        ENA 10

        R
        Direction 13
        Step 14
        Sleep 21
        Reset 22
        MS3 N/A
        MS2 23
        MS1 24
        ENA 25
        */
        
        h.ps("Enabling");
        steppers.enable();
        h.ps("Enabled");
    }
    
    public void shutdown(){
        h.ps("Disabling");
        steppers.disable();
        h.ps("Shutting down");
        steppers.shutdown();
        
        System.exit(0);
    }
    
    public void normalize(){
        rightRatio = 1.0f;
        leftRatio = 1.0f;
    }
    
    public void turnLeft(float amount){
        amount = amount / 100;
        
        rightRatio = 1.0f;
        leftRatio = 1.0f - amount;
        
    }
    
    public void turnRight(float amount){
        amount = amount / 100;
        
        rightRatio = 1.0f - amount;
        leftRatio = 1.0f;
    }
    
    public void stepForward(int steps, float interval) throws InterruptedException{
        //h.ps("F");
        steppers.move(steps, interval, leftRatio, rightRatio);
    }
    
    public void stepBackward(int steps, float interval) throws InterruptedException{
        //h.ps("B");
        steppers.move(-steps, interval, leftRatio, rightRatio);
    }
}
