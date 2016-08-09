/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package RobotHandler;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Iterator;

/**
 *
 * @author Rollie
 */
public class Balancer {
    //Balancer Control
    private final boolean invertDirection = true;//switch if your robot tries to lay down
    private final boolean complementaryFilter = false;
    private final boolean kalmanFilterAngle = true;
    private final boolean kalmanFilterAccel = false;
    private final boolean kalmanFilterOutput = false;
    private final boolean printBalanceInfo = true;
    private final boolean printStepInfo = false;
    private final boolean printAngleInfo = false;
    private final boolean limitMaxOutput = true;
    private final boolean findMaxOutput = false;
    private final float interval = 125000.0f;//Pause time in nanoSeconds between steps, for halfStepping and full step.
    private final float angleOffset = -4.625f;//corrects bias in overall angle
    private final float angleToleranceMin = 1.25f;//threshold for no movement -> +/-
    private final float angleToleranceMax = 12.0f;//
    private final float stepMultiplier = 2.0f;//
    private final float complementaryRatio = 0.5f;//0.25 -> Gyro*0.25, Accel*0.75
    private final float kalmanGainAccel = 0.5f;//
    private final float kalmanGainAngle = 0.5f;//0.15f
    private final float kalmanGainOutput = 0.75f;//
    private final float angleMomRatio = 0.5f;//increases acceleration if angle isn't correcting itself
    private final float distanceCorr = 0.0875f;//reduces overall distance between oscillations
    private final float acceleration = 1.0f;//increase velocity with the magnitude of the angle
    private final float centerOfMassHeight = 0.08f;//meters
    private final float frictionOffset = 0.0f;//
    private final float constA = (float) (100 * Math.sqrt(2 * 9.81f / centerOfMassHeight) / Math.PI);//
    private final float conv = (float) (Math.PI / 180);//
    private final float angleMomentumMin = 0.583f;//52.5/90 - 0.583
    private final float stepShift = 4.0f;//stepper accuracy, increase val for more microstepping
    private final float maxOutput = 4.0f;//
    private final int angleMemLength = 4;//decreasing will increase response time
    private final int minimumMicrostepping = 1;//1 2 4 8 16
    private final int maximumMicrostepping = 16;//1 2 4 8 16
    
    //Balancer Variables
    private Iterator iterator;
    private boolean motors = false;
    private float angle = 0.0f;//current angle
    private float targetAngle = 0.0f;//use to move forward/backward
    private float output = 0.0f;//PID output
    private float previousAngle = 0.0f;//stores previous angle
    private float previousOutput = 0.0f;
    private float previousAccelX = 0.0f;
    private float angleMomentumCorr = 0.0f;
    private float gyroX = 0.0f;
    private float accelX = 0.0f;
    private float tempAngleMomentumCorr = 0.0f;
    private int steps = 0;
    Queue<Float> angleMem = new LinkedList<>();
    RotationControl rotationControl;
    
    //Keyboard ControlVariables
    private boolean upPressed = false;
    private boolean downPressed = false;
    private boolean leftPressed = false;
    private boolean rightPressed = false;
    private float up = 0;
    private float down = 0;
    private float left = 0;
    private float right = 0;
    private final float upMax = 10.0f;//degrees
    private final float downMax = 10.0f;//degrees
    private final int leftMax = 100;//percent
    private final int rightMax = 100;//percent
    private final float smoothTransitionAccuracyUD = 0.25f;//shifts by x degrees
    private final int smoothTransitionAccuracyLR = 2;//shifts by x percent
    private final boolean smoothTransitionStart = true;//gradually shifts between states when key is pressed
    private final boolean smoothTransitionStop = false;//gradually shifts between states when key is released
    
    private float error = 0.0f;
    private final float maxStepsPerSecond = 26.65f;
    private final float stepAngle = 1.8f;
    private final long startTime = System.currentTimeMillis(); //fetch starting time
    float filterVel = 0.0f;
    float approachAngle = 0.0f;
    float diff = 0.0f;
    float vel = 0.0f;
    float approxVel = 0.0f;
    float throttle = 0.0f;
    private float p = 0.0f;
    private float i = 0.0f;
    private float d = 0.0f;
    private final float kP = 0.03f;//1.2f;//0.07f;
    private final float kI = 0.0f;//0.000000001f;//0.0000000000009925f;//0.04f; 0.000000000005f
    private final float kD = 0.0f;
    private final float bias = 0.0f;//25f;
    private float oldSpeed = 0.0f;
    private float approachAngleOld = 0.0f;
    float previousError = 0.0f;
    long time;
    long lastTime;

    Balancer() {
        rotationControl = new RotationControl();
    }
    
    public void turnOnMotors(){
        motors = true;
    }
    
    /**
     * 
     * @throws IOException
     * @throws InterruptedException 
     * 
     * Contains the balancing algorithm, before using this the Proportional Integral 
     * Derivative control needs calibrated. This can be done via the following steps:
     * 1. Set kI and kD to zero
     * 2. Set kP high enough for the wheels to be driven under the robot in the direction
     *    it is falling. This will most likely cause the robot to oscillate. 
     * 3. Reduce the kP so the robot oscillates less rapidly - about 5% -> 15% .
     * 4. Increase the kI value. This will accelerate to the surface normal faster, 
     *    and will also cause oscillation.
     * 5. Increase the kD to dampen the oscillation so the robot balances.
     */
    public void balance() throws IOException, InterruptedException{
        //checkControl();
        
        //Read the MPU6050
        //gyroX = readXGyroscope();
        
        angle = ReadMPUThread.returnAngle();
        
        //Accelerometer Kalman Filter
        if(kalmanFilterAccel){
            accelX = kalmanGainAccel * accelX + (1 - kalmanGainAccel) * previousAccelX;//kalman filter for accel
        }
        
        
        /*
        //Gyroscope Kalman Filter
        if(kalmanFilterGyro){
            gyroX = kalmanGainGyro * gyroX + (1 - kalmanGainAccel) * previousGyroX;//kalman filter for accel
        }
        */
        /*
        //ComplementaryFilter
        if(complementaryFilter){
            angle = ((complementaryRatio * (angle + gyroX) + (1.0f - complementaryRatio) * accelX));//complementary filter
        }
        else{
            angle = accelX;
        }
        */
        //Angle Kalman Filter
        if(kalmanFilterAngle){
            angle = kalmanGainAngle * angle + (1 - kalmanGainAngle) * previousAngle;//kalman filter
        }
        
        if(findMaxOutput){
            angle = angleToleranceMax;
        }
        
        
        //Store variables in angle memory
        if(angleMem.size() < angleMemLength){
            angleMem.add(angle - previousAngle);
        }
        else{
            angleMem.remove();
            angleMem.add(angle - previousAngle);
        }
        
        //Find sum of all angles in memory
        iterator = angleMem.iterator();
        tempAngleMomentumCorr = 0.0f;
        
        while(iterator.hasNext()){
            tempAngleMomentumCorr += (float)iterator.next();
        }
        
        //Average all sum of angles in memory
        tempAngleMomentumCorr /= angleMem.size();
        angleMomentumCorr += tempAngleMomentumCorr * 0.65 + angleMomentumCorr * 0.35;
        
        //Reduce angle momentum correction
        angleMomentumCorr = (float)(angleMomentumCorr * angleMomRatio) * -1;//35.0f
        
        //Remove momentum correction below certain angle
        if(Math.abs(angleMomentumCorr) < angleMomentumMin){
            angleMomentumCorr = 0.0f;
        }
        
        //Compute inverted pendulum, and adjust with momentum correction
        output = (float) ((Math.signum(angle - targetAngle + angleOffset) * constA * Math.sqrt(Math.abs(Math.cos(conv * targetAngle) - Math.cos(conv * (angle + angleOffset))))) * distanceCorr);//0.225f
        output = (float) (Math.pow(Math.abs(output) + frictionOffset, (acceleration + angleMomentumCorr)) * Math.signum(angle + angleOffset));//0.02125 - 3.0f//3.75f
        
        //Prevent minor and major movements
        if(limitMaxOutput && Math.abs(output) > maxOutput){
            output = maxOutput * Math.signum(output);
        }
        else if (Math.abs(angle) < angleToleranceMin){
            output = 0.0f;
        }
        
        
        /*
        diff = (angle - angleOffset) - previousAngle;
        vel = 90f * diff;
        approxVel = - oldSpeed - vel;
        throttle = 290;
        filterVel = filterVel * 0.95f + approxVel * 0.05f;

        error = throttle - filterVel;
        p = kP * error;
        i += kI * error;
        d = 1 / 22;
        approachAngle = p + i + d;

        error = approachAngle - (angle - angleOffset);
        p = kP * error;
        d = kD * (error - approachAngleOld + previousAngle) / 33;
        approachAngleOld = approachAngle;
        output += p + d + bias;////////////////////////////////////////////

        output = output * Math.signum(angle - angleOffset);
        */
        
        /*
        time = System.currentTimeMillis();
        long timeChange = time - lastTime;

        error = targetAngle - angle;
        p = kP * error;
        i += error * ((float)(timeChange) / 1000.0f);
        d = kD * ((error - previousError) / ((float)(timeChange) / 1000.0f));

        output = p + (kI * i) + d;
        lastTime = time;
        */
        
         //Prevent minor and major movements
        if(limitMaxOutput && Math.abs(output) > maxOutput){
            output = maxOutput * Math.signum(output);
        }
        else if (Math.abs(angle) < angleToleranceMin){
            output = 0.0f;
        }
        
        //Inverts the direction of the output
        if (invertDirection){
            output *= -1;
        }
        
        //Sends the output to the motors, if they have been activated.
        if(motors){
            //Output Kalman Filter
            if(kalmanFilterOutput){
                output = kalmanGainOutput * output + (1 - kalmanGainOutput) * previousOutput;//kalman filter
            }
            
            setSpeed(output);
        }
        
        //Set Global variables
        previousAngle = angle;
        previousOutput = output;
        previousAccelX = accelX;
        
        //Print Information
        if(printBalanceInfo){
            h.ps("Angle:" + angle + " Accelerometer:" + accelX + " Gyroscope:" + gyroX + " Output:" + output + " MomentumCorrection:" + angleMomentumCorr);
        }
        
        if(printAngleInfo){
             h.ps("Angle:" + angle + " AccelX:" + accelX + " GyroX:" + gyroX);
        }
    }
    
    /**
     * Sets the direction and velocity of the robot via keyboard input
     */
    private void checkControl(){
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
        
        targetAngle = up - down;
        
        if (right - left > 0){
            rotationControl.turnRight(right - left);
        }
        else if (right - left < 0){
            rotationControl.turnRight(Math.abs(right - left));
        }
        else if (right - left == 0){
            rotationControl.normalize();
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

//private float error = 0.0f;
//private final float maxStepsPerSecond = 26.65f;
//private final float stepAngle = 1.8f;
//private final long startTime = System.currentTimeMillis(); //fetch starting time
//float filterVel = 0.0f;
//float approachAngle = 0.0f;
//float diff = 0.0f;
//float vel = 0.0f;
//float approxVel = 0.0f;
//float throttle = 0.0f;
//private float p = 0.0f;
//private float i = 0.0f;
//private float d = 0.0f;
//private final float kP = 0.75f;//1.2f;//0.07f;
//private final float kI = 0.0f;//0.000000001f;//0.0000000000009925f;//0.04f; 0.000000000005f
//private final float kD = 0.0f;
//private final float bias = 0.0f;//25f;
//private float oldSpeed = 0.0f;
//private float approachAngleOld = 0.0f;
//float previousError = 0.0f;
    

/*
long time = 0;
long lastTime = 0;
    
output = ((angle - previousAngle) * kP) + (angle * kI);
*/
/*
diff = angle - previousAngle;
vel = 90f * diff;
approxVel = - oldSpeed - vel;
throttle = 290;
filterVel = filterVel * 0.95f + approxVel * 0.05f;

error = throttle - filterVel;
p = kP * error;
i += kI * error;
d = 1 / 22;
approachAngle = p + i + d;

error = approachAngle - angle;
p = kP * error;
d = kD * (error - approachAngleOld + previousAngle) / 33;
approachAngleOld = approachAngle;
output += p + d + bias;////////////////////////////////////////////

output = output * Math.signum(angle);
*/
/*
time = System.currentTimeMillis();
long timeChange = time - lastTime;

error = targetAngle - angle;
p = kP * error;
i += error * ((float)(timeChange) / 1000.0f);
d = kD * ((error - previousError) / ((float)(timeChange) / 1000.0f));

output = p + (kI * i) + d;
lastTime = time;
*/

//previousError = error;
//filterVel = 0.0f;
//approachAngle = 0.0f;
//diff = 0.0f;
//vel = 0.0f;
//approxVel = 0.0f;
//throttle = 0.0f;
//error = 0.0f;