/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package RobotHandler;

import java.util.concurrent.TimeUnit;

/**
 *
 * @author Rollie
 */
public final class Steppers {
    Pins pins = new Pins(true);
    
    public static final int FORWARD = 0;
    public static final int BACKWARD = 1;
    public static final int FULL_STEP = 0;
    public static final int HALF_STEP = 1;
    public static final int ONE_FOURTH_STEP = 2;
    public static final int ONE_EIGHTH_STEP = 3;
    public static final int ONE_SIXTEENTH_STEP = 4;
    
    public int mDrivingMode;
    
    final int mDirPin1;
    final int mStepPin1;
    final int mSleepPin1;
    final int mResetPin1;
    final int mMs3Pin1;
    final int mMs2Pin1;
    final int mMs1Pin1;
    final int mEnablePin1;
   
    final int mDirPin2;
    final int mStepPin2;
    final int mSleepPin2;
    final int mResetPin2;
    final int mMs3Pin2;
    final int mMs2Pin2;
    final int mMs1Pin2;
    final int mEnablePin2;
    
    public Steppers(int drivingMode,
            int dirPin1, int stepPin1, int sleepPin1, int resetPin1,
            int ms3Pin1, int ms2Pin1, int ms1Pin1, int enablePin1,
            int dirPin2, int stepPin2, int sleepPin2, int resetPin2,
            int ms3Pin2, int ms2Pin2, int ms1Pin2, int enablePin2) {
        
        mDrivingMode = drivingMode;
        
        this.mDirPin1 = dirPin1;
        this.mStepPin1 = stepPin1;
        this.mSleepPin1 = sleepPin1;
        this.mResetPin1 = resetPin1;
        this.mMs3Pin1 = ms3Pin1;
        this.mMs2Pin1 = ms2Pin1;
        this.mMs1Pin1 = ms1Pin1;
        this.mEnablePin1 = enablePin1;

        this.mDirPin2 = dirPin2;
        this.mStepPin2 = stepPin2;
        this.mSleepPin2 = sleepPin2;
        this.mResetPin2 = resetPin2;
        this.mMs3Pin2 = ms3Pin2;
        this.mMs2Pin2 = ms2Pin2;
        this.mMs1Pin2 = ms1Pin2;
        this.mEnablePin2 = enablePin2;
        
        /*
        for(int i = 0; i < 1000; i++){
            pins.pins[mDirPin1].setHigh();
            pins.pins[mStepPin1].setHigh();
            pins.pins[mSleepPin1].setHigh();
            pins.pins[mResetPin1].setHigh();
            pins.pins[mMs3Pin1].setHigh();
            pins.pins[mMs2Pin1].setHigh();
            pins.pins[mMs1Pin1].setHigh();
            pins.pins[mEnablePin1].setHigh();

            pins.pins[mDirPin2].setHigh();
            pins.pins[mStepPin2].setHigh();
            pins.pins[mSleepPin2].setHigh();
            pins.pins[mResetPin2].setHigh();
            pins.pins[mMs3Pin2].setHigh();
            pins.pins[mMs2Pin2].setHigh();
            pins.pins[mMs1Pin2].setHigh();
            pins.pins[mEnablePin2].setHigh();
            
            try{
                Thread.sleep(100);
            }
            catch(Exception ex){}
            
            pins.pins[mDirPin1].setLow();
            pins.pins[mStepPin1].setLow();
            pins.pins[mSleepPin1].setLow();
            pins.pins[mResetPin1].setLow();
            pins.pins[mMs3Pin1].setLow();
            pins.pins[mMs2Pin1].setLow();
            pins.pins[mMs1Pin1].setLow();
            pins.pins[mEnablePin1].setLow();

            pins.pins[mDirPin2].setLow();
            pins.pins[mStepPin2].setLow();
            pins.pins[mSleepPin2].setLow();
            pins.pins[mResetPin2].setLow();
            pins.pins[mMs3Pin2].setLow();
            pins.pins[mMs2Pin2].setLow();
            pins.pins[mMs1Pin2].setLow();
            pins.pins[mEnablePin2].setLow();
            
            try{
                Thread.sleep(100);
            }
            catch(Exception ex){}
        }
        */
        
        /*
        for(int i = 0; i < 1000; i++){
            for(int j = 0; j < 26; j++){
                if(j == 12 || j == 13){
                    
                }
                else{
                    pins.pins[j].setHigh();
                }
            }
            
            try{
                Thread.sleep(100);
            }
            catch(Exception ex){}
            
            for(int j = 0; j <26; j++){
                if(j == 12 || j == 13){
                    
                }
                else{
                    pins.pins[j].setLow();
                }
            }
            
            try{
                Thread.sleep(100);
            }
            catch(Exception ex){}
        }
        */
        
        pins.pins[mDirPin1].setLow();
        pins.pins[mStepPin1].setLow();
        pins.pins[mSleepPin1].setHigh();
        pins.pins[mResetPin1].setHigh();
        pins.pins[mMs3Pin1].setHigh();
        pins.pins[mMs2Pin1].setHigh();
        pins.pins[mMs1Pin1].setHigh();
        pins.pins[mEnablePin1].setHigh();
        
        pins.pins[mDirPin2].setLow();
        pins.pins[mStepPin2].setLow();
        pins.pins[mSleepPin2].setHigh();
        pins.pins[mResetPin2].setHigh();
        pins.pins[mMs3Pin2].setHigh();
        pins.pins[mMs2Pin2].setHigh();
        pins.pins[mMs1Pin2].setHigh();
        pins.pins[mEnablePin2].setHigh();
        
        setDrivingModeRight(drivingMode);
        setDrivingModeLeft(drivingMode);
    }
    
    public void setDrivingModeLeft(int drivingMode) {
        switch (drivingMode) {
            case FULL_STEP: {
                pins.pins[mMs1Pin1].setLow();
                pins.pins[mMs2Pin1].setLow();
                pins.pins[mMs3Pin1].setLow();
                break;
            }
            case HALF_STEP: {
                pins.pins[mMs1Pin1].setHigh();
                pins.pins[mMs2Pin1].setLow();
                pins.pins[mMs3Pin1].setLow();
                break;
            }
            case ONE_FOURTH_STEP: {
                pins.pins[mMs1Pin1].setLow();
                pins.pins[mMs2Pin1].setHigh();
                pins.pins[mMs3Pin1].setLow();
                break;
            }
            case ONE_EIGHTH_STEP: {
                pins.pins[mMs1Pin1].setHigh();
                pins.pins[mMs2Pin1].setHigh();
                pins.pins[mMs3Pin1].setLow();
                break;
            }
            case ONE_SIXTEENTH_STEP: {
                pins.pins[mMs1Pin1].setHigh();
                pins.pins[mMs2Pin1].setHigh();
                pins.pins[mMs3Pin1].setHigh();
                break;
            }
        }
    }
    
    public void setDrivingModeRight(int drivingMode) {
        switch (drivingMode) {
            case FULL_STEP: {
                pins.pins[mMs1Pin2].setLow();
                pins.pins[mMs2Pin2].setLow();
                pins.pins[mMs3Pin2].setLow();
                break;
            }
            case HALF_STEP: {
                pins.pins[mMs1Pin2].setHigh();
                pins.pins[mMs2Pin2].setLow();
                pins.pins[mMs3Pin2].setLow();
                break;
            }
            case ONE_FOURTH_STEP: {
                pins.pins[mMs1Pin2].setLow();
                pins.pins[mMs2Pin2].setHigh();
                pins.pins[mMs3Pin2].setLow();
                break;
            }
            case ONE_EIGHTH_STEP: {
                pins.pins[mMs1Pin2].setHigh();
                pins.pins[mMs2Pin2].setHigh();
                pins.pins[mMs3Pin2].setLow();
                break;
            }
            case ONE_SIXTEENTH_STEP: {
                pins.pins[mMs1Pin2].setHigh();
                pins.pins[mMs2Pin2].setHigh();
                pins.pins[mMs3Pin2].setHigh();
                break;
            }
        }
    }

    public void rotate(double degrees, int interval, int drivingMode) throws InterruptedException {
        move(getStepsFromDegrees(degrees, drivingMode), interval, drivingMode, 1, 1);
    }

    public void rotate(double degrees, int interval) throws InterruptedException {
        rotate(degrees, interval, mDrivingMode);
    }

    public void move(int distance, int interval, int drivingMode, float leftRatio, float rightRatio) throws InterruptedException {
        if(distance < 0){
            setDirection(FORWARD);
        }
        else{
            setDirection(BACKWARD);
        }
        
        distance *= 2;
        
        int leftSteps = (int) ((float)distance * leftRatio);
        int rightSteps = (int) ((float)distance * rightRatio);
        float leftIncrementVal = 0;
        float rightIncrementVal = 0;
        
        leftIncrementVal = (float)Math.abs((float)distance / (float)leftSteps);
        rightIncrementVal = (float)Math.abs((float)distance / (float)rightSteps);
        
        /*
        if (leftSteps == 0){
            leftIncrementVal = 8;
        }
        else if(leftSteps > 0){
        }
        if (rightSteps == 0){
            rightIncrementVal = 8;
        }
        else if (rightSteps > 0){
        }
        */

        float leftIncrementOn = 0.0f;
        float rightIncrementOn = 0.0f;
        float leftIncrementOff = 0.0f;
        float rightIncrementOff = 0.0f;
        
        //h.ps(leftIncrementVal + " " + rightIncrementVal);
        //h.ps(leftSteps + " " + rightSteps);
        
        for (int i = 0; i < Math.abs(distance); i++) {
            if(leftIncrementOn <= i){
                stepOnL();
                leftIncrementOn += leftIncrementVal;
                //h.ps("L");
            }
            if(rightIncrementOn <= i){
                stepOnR();
                rightIncrementOn += rightIncrementVal;
                //h.ps("R");
            }
            
            delay(interval);
            
            if(leftIncrementOff <= i){
                stepOffL();
                leftIncrementOff += leftIncrementVal;
            }
            if(rightIncrementOff <= i){
                stepOffR();
                rightIncrementOff += rightIncrementVal;
            }
            
            delay(interval);
        }
    }
    
    public void delay(int interval){
        long startTime = System.nanoTime();
        
        while((System.nanoTime() - startTime) < interval){}
    }
    
    private void stepOnL(){
        pins.pins[mStepPin1].setHigh();
    }
    
    private void stepOnR(){
        pins.pins[mStepPin2].setHigh();
    }
    
    private void stepOffL(){
        pins.pins[mStepPin1].setLow();
    }
    
    private void stepOffR(){
        pins.pins[mStepPin2].setLow();
    }

    public void move(int steps, int interval, float leftRatio, float rightRatio) throws InterruptedException {
        move(steps, interval, mDrivingMode, leftRatio, rightRatio);
    }

    public void sleep() {
        pins.pins[mSleepPin1].setLow();
        pins.pins[mSleepPin2].setLow();
    }

    public void wake() {
        pins.pins[mSleepPin1].setHigh();
        pins.pins[mSleepPin2].setHigh();
    }

    public void reset() {
        pins.pins[mResetPin1].setLow();
        pins.pins[mResetPin2].setLow();
    }

    public void enable() {
        pins.pins[mEnablePin1].setLow();
        pins.pins[mEnablePin2].setLow();
    }

    public void disable() {
        pins.pins[mEnablePin1].setHigh();
        pins.pins[mEnablePin2].setHigh();
    }

    public void shutdown() {
        pins.pins[mDirPin1].setLow();
        pins.pins[mStepPin1].setLow();
        pins.pins[mSleepPin1].setHigh();
        pins.pins[mResetPin1].setLow();
        pins.pins[mMs3Pin1].setHigh();
        pins.pins[mMs2Pin1].setHigh();
        pins.pins[mMs1Pin1].setHigh();
        pins.pins[mEnablePin1].setHigh();
        
        pins.pins[mDirPin2].setLow();
        pins.pins[mStepPin2].setLow();
        pins.pins[mSleepPin2].setHigh();
        pins.pins[mResetPin2].setLow();
        pins.pins[mMs3Pin2].setHigh();
        pins.pins[mMs2Pin2].setHigh();
        pins.pins[mMs1Pin2].setHigh();
        pins.pins[mEnablePin2].setHigh();
    }

    public void setDirection(int direction) {
        if (direction == FORWARD) {
            pins.pins[mDirPin1].setHigh();
            pins.pins[mDirPin2].setHigh();
        }
        else if (direction == BACKWARD) {
            pins.pins[mDirPin1].setLow();
            pins.pins[mDirPin2].setLow();
        }
    }

    public static double getDegreesFromStep(int steps, int drivingMode) {
        switch (drivingMode) {
            case FULL_STEP:
                return steps * 1.8;
            case HALF_STEP:
                return steps * 0.9;
            case ONE_FOURTH_STEP:
                return steps * 0.45;
            case ONE_EIGHTH_STEP:
                return steps * 0.225;
            default:
                return 0.0;
        }
    }

    public static int getStepsFromDegrees(double degrees, int drivingMode) {
        switch (drivingMode) {
            case FULL_STEP:
                return (int) (degrees / 1.8);
            case HALF_STEP:
                return (int) (degrees / 0.9);
            case ONE_FOURTH_STEP:
                return (int) (degrees / 0.45);
            case ONE_EIGHTH_STEP:
                return (int) (degrees / 0.225);
            default:
                return 0;
        }
    }
}