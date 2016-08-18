/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package RobotHandler;

/**
 *
 * @author Rollie
 */
public class StepperThread implements Runnable{
    private static Steppers steppers;
    private final boolean printStepInfo = false;
    private final boolean simpleStepping = true;
    private final int interval = 10000000;//1000000.0f;//Pause time in nanoSeconds between steps, for halfStepping and full step
    private final float stepMultiplier = 1.0f;//
    private final float stepShift = 1.0f;//stepper accuracy, increase val for more microstepping
    private final int minimumMicrostepping = 1;//1 2 4 8 16
    private final int maximumMicrostepping = 16;//1 2 4 8 16
    private static Thread t;
    private float leftRatio = 1.0f;
    private float rightRatio = 1.0f;
    private float left = 0.0f;
    private float right = 0.0f;
    private int steps;
    private long start;
    private long end;
    
    @Override
    public void run() {
        while(true){
            try {
                readLR();
                
                if (right - left > 0){
                    turnRight(right - left);
                }
                else if (right - left < 0){
                    turnLeft(Math.abs(right - left));
                }
                else if (right - left == 0){
                    normalize();
                }
                
                if(simpleStepping){
                    setSpeedTwo(RobotHandler.botControl.robot.returnSpeed());
                }
                else{
                    setSpeed(RobotHandler.botControl.robot.returnSpeed());
                }
            } catch (Exception ex) {
                
            }
        }
    }
    
    public void start(){
        if(t == null){
            steppers = new Steppers(1,
                0,1,2,3,
                4,5,6,7,
                18,19,20,21,
                22,23,24,25);
            
            h.ps("Enabling");
            steppers.enable();
            h.ps("Enabled");
    
            t = new Thread(this, "Steppers");
            t.start();
            
            if(simpleStepping){
                steppers.setDrivingModeRight(4);
                steppers.setDrivingModeLeft(4);
            }
        }
    }
    
    /***
     * 
     * @param input Speed from -1 to 1
     * @throws InterruptedException 
     * 
     * This function utilizes microstepping for accuracy and speed, similar to an 
     * automatic shifter in a car.
     */
    public void setSpeed(float input) throws InterruptedException{
        int tempVal = (int) Math.abs(1 / (input / stepShift));
        
        int intervalDec = (int)((float)interval * (float)Math.abs(input));
        
        if(intervalDec > interval){
            intervalDec = interval;
        }
        else if (intervalDec < 50000.0f){
            intervalDec = 0;
        }
        
        if(input > 1){
            input = 1.0f;
        }
        else if (input < -1){
            input = -1.0f;
        }
        
        switch(minimumMicrostepping){
            case 1:
                break;
            case 2:
                if(tempVal < 1){
                    tempVal = 1;
                }
                break;
            case 4:
                if(tempVal < 2){
                    tempVal = 2;
                }
                break;
            case 8:
                if(tempVal < 3){
                    tempVal = 3;
                }
                break;
            case 16:
                if(tempVal < 4){
                    tempVal = 4;
                }
                break;
        }
        
        switch(maximumMicrostepping){
            case 1:
                if(tempVal > 1 && minimumMicrostepping < 2){
                    tempVal = 0;
                }
                break;
            case 2:
                if(tempVal > 1 && minimumMicrostepping < 4){
                    tempVal = 1;
                }
                break;
            case 4:
                if(tempVal > 2 && minimumMicrostepping < 8){
                    tempVal = 2;
                }
                break;
            case 8:
                if(tempVal > 3 && minimumMicrostepping < 16){
                    tempVal = 3;
                }
                break;
            case 16:
                break;
        }
        
        if (tempVal <= 0){
            steppers.setDrivingModeRight(0);
            steppers.setDrivingModeLeft(0);
            steps = (int) (stepMultiplier * input);
            
            if(printStepInfo){
                h.ps("MicroStepping:0 Steps:" + steps + " Correction:" + input);
            }
            
            if(input < 0){
                stepForward(steps, intervalDec);
            }
            else if (input > 0){
                stepBackward(-steps, intervalDec);
            }
        }
        else if (tempVal == 1){
            steppers.setDrivingModeRight(1);
            steppers.setDrivingModeLeft(1);
            steps = (int) (Math.pow(stepMultiplier, 2) * input);
            
            if(printStepInfo){
                h.ps("MicroStepping:1/2 Steps:" + steps + " Correction:" + input);
            }
            
            if(input < 0){
                stepForward(steps, intervalDec);
            }
            else if (input > 0){
                stepBackward(-steps, intervalDec);
            }
        }
        else if (tempVal == 2){
            steppers.setDrivingModeRight(2);
            steppers.setDrivingModeLeft(2);
            steps = (int) (Math.pow(stepMultiplier, 3) * input);
            
            if(printStepInfo){
                h.ps("MicroStepping:1/4 Steps:" + steps + " Correction:" + input);
            }
            
            if(input < 0){
                stepForward(steps, intervalDec);
            }
            else if (input > 0){
                stepBackward(-steps, intervalDec);
            }
        }
        else if (tempVal == 3){
            steppers.setDrivingModeRight(3);
            steppers.setDrivingModeLeft(3);
            steps = (int) (Math.pow(stepMultiplier, 4) * input);
            
            if(printStepInfo){
                h.ps("MicroStepping:1/8 Steps:" + steps + " Correction:" + input);
            }
            
            if(input < 0){
                stepForward(steps, intervalDec);
            }
            else if (input > 0){
                stepBackward(-steps, intervalDec);
            }
        }
        else if (tempVal >= 4){
            steppers.setDrivingModeRight(4);
            steppers.setDrivingModeLeft(4);
            steps = (int) (Math.pow(stepMultiplier, 5) * input);
            
            if(printStepInfo){
                h.ps("MicroStepping:1/16 Steps:" + steps + " Correction:" + input);
            }
            
            if(input < 0){
                stepForward(steps, intervalDec);
            }
            else if (input > 0){
                stepBackward(-steps, intervalDec);
            }
        }
    }
    
    
    /***
     * 
     * @param input Speed from -1 to 1
     * @throws InterruptedException 
     * 
     * This function utilizes microstepping for accuracy and speed, similar to an 
     * automatic shifter in a car.
     */
    public void setSpeedTwo(float input) throws InterruptedException{
        int intervalDec = (int)((float)interval / (float)Math.abs(input));
        
        if(intervalDec > interval){
            intervalDec = (int)interval;
        }
        
        /*
        if(intervalDec < 1000000){
            intervalDec = 1000000;
        }
        */
        
        if(input > 1.0f){
            input = 1.0f;
        }
        else if (input < -1.0f){
            input = -1.0f;
        }
        
        steps = (int) (stepMultiplier * input);

        if(printStepInfo){
            h.ps("MicroStepping:1/16 Steps:" + steps + " Correction:" + input);
        }
        
        //h.ps(intervalDec + " " + interval + " " + input + " " + steps);
        
        
        if(input < 0){
            stepForward(steps, intervalDec);
        }
        else if (input > 0){
            stepBackward(steps, intervalDec);
        }
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
    
    public void stepForward(int steps, int interval) throws InterruptedException{
        steppers.move(Math.abs(steps), interval, leftRatio, rightRatio);
    }
    
    public void stepBackward(int steps, int interval) throws InterruptedException{
        steppers.move(-Math.abs(steps), interval, leftRatio, rightRatio);
    }

    public static void shutdown(){
        h.ps("Disabling");
        steppers.disable();
        h.ps("Shutting down");
        steppers.shutdown();
        
        System.exit(0);
    }
    
    public static void enable(){
        steppers.enable();
    }
    
    public static void disable(){
        steppers.disable();
    }
    
    public void readLR(){
        left = RobotHandler.botControl.robot.returnLeft();
        right = RobotHandler.botControl.robot.returnRight();
    }
}
