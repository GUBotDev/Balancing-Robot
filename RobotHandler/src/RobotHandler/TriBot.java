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
public class TriBot {
    //Keyboard ControlVariables
    private boolean upPressed = false;
    private boolean downPressed = false;
    private boolean leftPressed = false;
    private boolean rightPressed = false;
    private float up = 0;
    private float down = 0;
    private float left = 0;
    private float right = 0;
    private final float upMax = 7.5f;//degrees
    private final float downMax = 7.5f;//degrees
    private final int leftMax = 100;//percent
    private final int rightMax = 100;//percent
    private final float smoothTransitionAccuracyUD = 0.125f;//shifts by x degrees
    private final int smoothTransitionAccuracyLR = 2;//shifts by x percent
    private final boolean smoothTransitionStart = false;//gradually shifts between states when key is pressed
    private final boolean smoothTransitionStop = false;//gradually shifts between states when key is released
    
    //TriBot Control
    private final boolean printStepInfo = false;
    private final boolean printMoveInfo = false;
    private final float interval = 125000;//1000000.0f;//Pause time in nanoSeconds between steps, for halfStepping and full step
    private final float stepMultiplier = 4.0f;//
    private final float stepShift = 1.0f;//stepper accuracy, increase val for more microstepping
    private final int minimumMicrostepping = 1;//1 2 4 8 16
    private final int maximumMicrostepping = 16;//1 2 4 8 16
    
    private int steps = 0;
    
    RotationControl rotationControl;
    
    TriBot(){
        rotationControl = new RotationControl();
    }
    
    /**
     * Sets the direction and velocity of the robot via keyboard input
     * @throws java.lang.InterruptedException
     */
    public void checkControl() throws InterruptedException{
        if(leftPressed && rightPressed){
            rotationControl.normalize();
        }
        
        if (smoothTransitionStart){
            if(upPressed && up < upMax){
                up += smoothTransitionAccuracyUD;
            }
            if(downPressed && down < downMax){
                down += smoothTransitionAccuracyUD;
            }
            if(leftPressed && left < leftMax){
                left += smoothTransitionAccuracyLR;
            }
            if(rightPressed && right < rightMax){
                right += smoothTransitionAccuracyLR;
            }
        }
        if (smoothTransitionStop){
            if (!upPressed && up > 0){
                up -= smoothTransitionAccuracyUD;
            }
            if (!downPressed && down > 0){
                down -= smoothTransitionAccuracyUD;
            }
            if (!leftPressed && left > 0){
                left -= smoothTransitionAccuracyLR;
            }
            if (!rightPressed && right > 0){
                right -= smoothTransitionAccuracyLR;
            }
        }
        if (!smoothTransitionStart){
            if(upPressed){
                up = upMax;
            }
            if(downPressed){
                down = downMax;
            }
            if(leftPressed){
                left = leftMax;
            }
            if(rightPressed){
                right = rightMax;
            }
        }
        if (!smoothTransitionStop){
            if(!upPressed){
                up = 0;
            }
            if (!downPressed){
                down = 0;
            }
            if (!leftPressed){
                left = 0;
            }
            if (!rightPressed){
                right = 0;
            }
        }
        
        if (up - down > 0){
            setSpeed(up - down);
        }
        else if (up - down < 0){
            setSpeed(up - down);
        }
        
        if (right - left > 0){
            rotationControl.turnRight(right - left);
        }
        else if (left - right > 0){
            rotationControl.turnLeft(left - right);
        }
        else if (right - left == 0){
            rotationControl.normalize();
        }
        
        if(printMoveInfo){
            h.ps("Up:" + up  + " " + upPressed + " Down:" + down + " " + downPressed +  " Left:" + left + " " + leftPressed + " Right:" + right + " " + rightPressed);
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
        
        float intervalDec = interval * Math.abs(input);
        
        if(intervalDec > interval){
            intervalDec = interval;
        }
        else if (intervalDec < 50000){
            intervalDec = 0;
        }
        
        //h.ps(intervalDec);
        
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
            rotationControl.steppers.setDrivingModeRight(0);
            rotationControl.steppers.setDrivingModeLeft(0);
            steps = (int) (stepMultiplier * input);
            
            if(printStepInfo){
                h.ps("MicroStepping:0 Steps:" + steps + " Correction:" + input);
            }
            
            if(input < 0){
                rotationControl.stepForward(steps, intervalDec);
            }
            else if (input > 0){
                rotationControl.stepBackward(-steps, intervalDec);
            }
        }
        else if (tempVal == 1){
            rotationControl.steppers.setDrivingModeRight(1);
            rotationControl.steppers.setDrivingModeLeft(1);
            steps = (int) (Math.pow(stepMultiplier, 2) * input);
            
            if(printStepInfo){
                h.ps("MicroStepping:1/2 Steps:" + steps + " Correction:" + input);
            }
            
            if(input < 0){
                rotationControl.stepForward(steps, intervalDec);
            }
            else if (input > 0){
                rotationControl.stepBackward(-steps, intervalDec);
            }
        }
        else if (tempVal == 2){
            rotationControl.steppers.setDrivingModeRight(2);
            rotationControl.steppers.setDrivingModeLeft(2);
            steps = (int) (Math.pow(stepMultiplier, 3) * input);
            
            if(printStepInfo){
                h.ps("MicroStepping:1/4 Steps:" + steps + " Correction:" + input);
            }
            
            if(input < 0){
                rotationControl.stepForward(steps, intervalDec);
            }
            else if (input > 0){
                rotationControl.stepBackward(-steps, intervalDec);
            }
        }
        else if (tempVal == 3){
            rotationControl.steppers.setDrivingModeRight(3);
            rotationControl.steppers.setDrivingModeLeft(3);
            steps = (int) (Math.pow(stepMultiplier, 4) * input);
            
            if(printStepInfo){
                h.ps("MicroStepping:1/8 Steps:" + steps + " Correction:" + input);
            }
            
            if(input < 0){
                rotationControl.stepForward(steps, intervalDec);
            }
            else if (input > 0){
                rotationControl.stepBackward(-steps, intervalDec);
            }
        }
        else if (tempVal >= 4){
            rotationControl.steppers.setDrivingModeRight(4);
            rotationControl.steppers.setDrivingModeLeft(4);
            steps = (int) (Math.pow(stepMultiplier, 5) * input);
            
            if(printStepInfo){
                h.ps("MicroStepping:1/16 Steps:" + steps + " Correction:" + input);
            }
            
            if(input < 0){
                rotationControl.stepForward(steps, intervalDec);
            }
            else if (input > 0){
                rotationControl.stepBackward(-steps, intervalDec);
            }
        }
    }
    
    public void setUp(boolean in){
        upPressed = in;
    }
    
    public void setDown(boolean in){
        downPressed = in;
    }
    
    public void setLeft(boolean in){
        leftPressed = in;
    }
    
    public void setRight(boolean in){
        rightPressed = in;
    }
}
