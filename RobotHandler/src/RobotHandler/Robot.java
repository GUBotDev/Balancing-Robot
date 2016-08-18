/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package RobotHandler;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

/**
 *
 * @author Rollie
 */
public class Robot {
    //Balancer Control
    private final boolean invertDirection = false;//switch if your robot tries to lay down
    private final boolean kalmanFilterOutput = false;
    private final boolean printBalanceInfo = false;
    private final boolean limitMaxOutput = true;
    private final boolean findMaxOutput = false;
    private final boolean usePIDTuner = false;
    private float angleOffset = -1.0f;//corrects bias in overall angle
    private final float angleToleranceMin = 0.0f;//threshold for no movement -> +/-
    private final float angleToleranceMax = 12.0f;//
    private final float kalmanGainOutput = 0.375f;//
    private final float angleMomRatio = 0.0f;//increases acceleration if angle isn't correcting itself
    private final float distanceCorr = 0.625f;//reduces overall distance between oscillations
    private final float acceleration = 1.0f;//increase velocity with the magnitude of the angle
    private final float centerOfMassHeight = 0.0375f;//meters
    private final float frictionOffset = 0.0f;//
    private final float constA = (float) (100 * Math.sqrt(2 * 9.81f / centerOfMassHeight) / Math.PI);//
    private final float conv = (float) (Math.PI / 180);//
    private final float angleMomentumMin = 0.583f;//52.5/90 - 0.583
    private final float maxOutput = 375.0f;//
    private final int angleMemLength = 4;//decreasing will increase response time
    private final float maxTargetAngle = 12.0f;
    
    //Balancer Variables
    private Iterator iterator;
    private boolean motors = false;
    private float angle = 0.0f;//current angle
    private float targetAngle = 0.0f;//use to move forward/backward
    private float modTargetAngle = 0.0f;
    private float output = 0.0f;//PID output
    private float previousAngle = 0.0f;//stores previous angle
    private float previousOutput = 0.0f;
    private float previousTarget = 0.0f;
    private float angleMomentumCorr = 0.0f;
    private float tempAngleMomentumCorr = 0.0f;
    Queue<Float> angleMem = new LinkedList<>();
    
    //Keyboard ControlVariables
    private boolean upPressed = false;
    private boolean downPressed = false;
    private boolean leftPressed = false;
    private boolean rightPressed = false;
    private float up = 0;
    private float down = 0;
    private volatile float left = 0;
    private volatile float right = 0;
    private float upMax = 500.0f;//degrees
    private float downMax = 500.0f;//degrees
    private int leftMax = 100;//percent
    private int rightMax = 100;//percent
    private float smoothTransitionAccuracyUD = 20.0f;//shifts by x degrees
    private float smoothTransitionAccuracyLR = 5.0f;//shifts by x percent
    private boolean smoothTransitionStart = true;//gradually shifts between states when key is pressed
    private boolean smoothTransitionStop = false;//gradually shifts between states when key is released
    
    private volatile float speed = 0.0f;
    
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
    private float dT = 0.0f;
    private float kI = 0.0f;//not for brobot
    
    private float kP = 0.0f;
    private float kD = 0.0f;
    private float kPS = 0.0f;
    private float kIS = 0.0f;
    
    private float kPD = 0.5f;//0.19 -> 0.35
    private float kDD = 28.0f;//28.0
    private float kPSD = 0.1f;//0.07
    private float kISD = 0.04f;//0.04
    
    private float kPR = 0.16f;
    private float kDR = 36.0f;
    private float kPSR = 0.0f;
    private float kISR = 0.0f;
    
    private float pidErrorNew = 0.0f;
    private float pidErrorOld = 0.0f;
    private float pidErrorSum = 0.0f;
    private int iMaxError = 25;
    private int iMax = 8000;
    private final float bias = 0.0f;//25f;
    private float oldSpeed = 0.0f;
    private float approachAngleOld = 0.0f;
    private float angularVelocity = 0.0f;
    private float estimatedVelocity = 0.0f;
    private float estimatedVelocityFiltered = 0.0f;
    private final float maxControlOutput = 500.0f;
    private float controlOutput = 0.0f;
    float previousError = 0.0f;
    float time;
    float lastTime;
    boolean connect = false;
    private boolean balancer = false;
    
    public void setBalancer(boolean balancer){
        this.balancer = balancer;
    }
    
    private void checkPID(){
        if(PIDTuner.checkPID()){
            //angularVelocity = 0.0f;
            //estimatedVelocity = 0.0f;
            //estimatedVelocityFiltered = 0.0f;
            //modTargetAngle = 0.0f;
            //controlOutput = 0.0f;
            pidErrorSum = 0.0f;
            //error = 0.0f;
            //previousTarget = 0.0f;
            //pidErrorNew = 0.0f;
            //pidErrorOld = 0.0f;
            //targetAngle = 0.0f;
            //dT = 0.0f;
            
            kPD = PIDTuner.returnKPD();
            kDD = PIDTuner.returnKDD();
            kPSD = PIDTuner.returnKPSD();
            kISD = PIDTuner.returnKISD();
            angleOffset = PIDTuner.returnAngleOffset();
            
            p = 0.0f;
            i = 0.0f;
            d = 0.0f;
            
            h.ps("Reset PID");
        }
    }
    
    /**
     * Custom controls for robot, utilize u, d, l, and r variables for movement.
     * Increase/Decrease the values to change the velocity/turning speed.
     * 
     * The setMovement function finalizes the changes and will affect the movement of the stepper motors.
     */
    public void control(){
        float u = 0.0f, d = 0.0f, l = 0.0f, r = 0.0f;
        
        ////////////////////////////////
        //CHANGE/////////////
        ////////////////////////////////
        
        int seconds = 20;
        long time = System.currentTimeMillis();
        
        while((System.currentTimeMillis() - time) < 1000 * seconds){
            u = upMax;
            l = leftMax;
            
            setMovement(u, d, l, r);
        }
        
        u = 0;
        
        setMovement(u, d, l, r);
        
        ////////////////////////////////
        //CHANGE////////////
        ////////////////////////////////
        
    }
    
    /**
     * Constrains movements.
     * @param u Up
     * @param d Down
     * @param l Left
     * @param r Right
     */
    public void setMovement(float u, float d, float l, float r){
        if(u > upMax){
            u = upMax;
        }
        else if(-u > upMax){
            u = -upMax;
        }
        
        if(d > downMax){
            d = downMax;
        }
        else if(-d > downMax){
            d = -downMax;
        }
        
        if(l > leftMax){
            l = leftMax;
        }
        else if(-l > leftMax){
            l = -leftMax;
        }
        
        if(r > rightMax){
            r = rightMax;
        }
        else if(-r > rightMax){
            r = -rightMax;
        }
        
        up = u;
        down = d;
        left = l;
        right = r;
    }

    public void turnOnMotors(){
        motors = true;
    }
    
    public float returnSpeed(){
        return speed;
    }
    
    public float returnLeft(){
        return left;
    }
    
    public float returnRight(){
        return right;
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
        time = (float)System.nanoTime() / 1000000000.0f;
        
        if(lastTime == 0){
            dT = time - time - 2;
        }
        else{
            dT = time - lastTime;
        }
        
        angle = ReadMPUThread.returnAngle();
        
        if(usePIDTuner){
            if(!connect){
                PIDTuner.connect();
                connect = true;
            }
            
            checkPID();
        }
        
        if(findMaxOutput){
            angle = angleToleranceMax;
        }
        
        //normalPID();
        //inversePendulum();
        brobotPIPD();
        
        
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
            
            speed = output;
        }
        
        //Set Global variables
        previousAngle = angle;
        previousOutput = output;
        lastTime = time;
        
        //Print Information
        if(printBalanceInfo){
            h.ps("Angle:" + angle + " Output:" + output + " MomentumCorrection:" + angleMomentumCorr);
        }
        
        Thread.sleep(25);//Limit to 40 hertz
    }
    
    private void brobotPIPD(){
        if (angle < 20 && angle > -20)
        {
            kP = kPD;
            kD = kDD;
            kPS = kPSD;
            kIS = kISD;
        }
        else
        {
            kP = kPR;
            kD = kDR;
            kPS = kPSR;
            kIS = kISR;
        }

        angularVelocity = (angle - previousAngle);// * 90.0f;
        estimatedVelocity = -output - angularVelocity;
        estimatedVelocityFiltered = estimatedVelocityFiltered * 0.95f + (float)estimatedVelocity * 0.05f;

        modTargetAngle = speedPI(dT, estimatedVelocityFiltered, throttle - (angleOffset * (500 / 90)));//max output / angle to compensate
        modTargetAngle = constrain(modTargetAngle, -(int)maxTargetAngle, (int)maxTargetAngle);

        controlOutput += stabilityPD(dT, angle, modTargetAngle);
        controlOutput = constrain(controlOutput, -(int)maxControlOutput, (int)maxControlOutput);
        
        if(angle < 30 && angle > -30){
            StepperThread.enable();
            
            output = controlOutput * 8f;//* 46; * 8
        }
        else{
            StepperThread.disable();
            
            output = 0.0f;
        }
        
        //h.ps(output);
        
        previousOutput = output;
    }
    
    //BROBOT
    private float stabilityPD(float dT, float angle, float target){
        float error;
        float output;
        
        error = target - angle;
        
        output = kP * error + (kD * (target - previousTarget) - kD * (angle - pidErrorOld)) / (dT * 1000);
        pidErrorOld = pidErrorNew;
        pidErrorNew = angle;
        previousTarget = target;
        
        return (output);
    }
    
    //BROBOT
    private float speedPI(float dT, float angle, float target){
        float error;
        float output;

        error = target - angle;
        pidErrorSum += constrain(error, -iMaxError, iMaxError);
        pidErrorSum = constrain(pidErrorSum, -iMax, iMax);

        output = kPS * error + kIS * pidErrorSum * dT;// * 0.001; // DT is in miliseconds...
        
        return (output);
    }
    
    private void normalPID(){
        time = (float)System.nanoTime() / 1000000000.0f;
        
        if(lastTime == 0){
            dT = time - time - 2;
        }
        else{
            dT = time - lastTime;
        }

        error = (targetAngle - angleOffset) - angle;
        
        p = error;
        i += error * dT;
        d = (error - previousError) / dT;

        output = (kP * p) + (kI * i) + (kD * d);
        lastTime = time;
        //h.ps("Delay: " + time + " " + (dT * 1000000000));
        
        delay((int)(dT * 1000000000.0f));//back to nanoseconds
        
        //Prevent minor and major movements
        if(limitMaxOutput && Math.abs(output) > maxOutput){
            output = maxOutput * Math.signum(output);
        }
        else if (Math.abs(angle) < angleToleranceMin){
            output = 0.0f;
        }
    }
    
    private void inversePendulum(){
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
    }
    
    /**
     * Sets the direction and velocity of the robot via keyboard input
     */
    public void checkControl(){
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
        
        //targetAngle = up - down;
        
        //h.ps(up + " " + down + " " + left + " " + right);
        
        if(balancer){
            smoothTransitionAccuracyUD = 4.0f;
            smoothTransitionAccuracyLR = 0.0025f;
            upMax = 250.0f;
            downMax = 250.0f;
            smoothTransitionStop = true;
            leftMax = 50;
            rightMax = 50;
            
            throttle = (up - down);
            
            //h.ps(throttle);
        }
        else{
            speed = up - down;
        }
    }
    
    
    public void setUp(boolean in){
        downPressed = in;//invert
    }
    
    public void setDown(boolean in){
        upPressed = in;//invert
    }
    
    public void setLeft(boolean in){
        leftPressed = in;
    }
    
    public void setRight(boolean in){
        rightPressed = in;
    }
    
    public void delay(int interval){
        long startTime = System.nanoTime();
        
        while((System.nanoTime() - startTime) < interval){}
    }
    
    private float constrain(float amount, int min, int max){
        
        if(amount > max){
            amount = max;
        }
        else if(amount < min){
            amount = min;
        }
        
        return amount;
    }
}


/*
diff = (angle - angleOffset) - previousAngle;
vel = 90f * diff;
approxVel = - oldSpeed - vel;
throttle = 0;
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