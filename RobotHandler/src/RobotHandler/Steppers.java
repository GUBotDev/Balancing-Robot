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
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinState;
import java.util.concurrent.TimeUnit;
//import com.pi4j.io.gpio.RaspiPin;

public class Steppers {
    public static final int FORWARD = 0;
    public static final int BACKWARD = 1;
    public static final int FULL_STEP = 0;
    public static final int HALF_STEP = 1;
    public static final int ONE_FOURTH_STEP = 2;
    public static final int ONE_EIGHTH_STEP = 3;
    public static final int ONE_SIXTEENTH_STEP = 4;
    
    public int mDrivingMode;
    
    final GpioController gpio;
    
    final GpioPinDigitalOutput mStepPin1;
    final GpioPinDigitalOutput mDirPin1;
    final GpioPinDigitalOutput mSleepPin1;
    final GpioPinDigitalOutput mEnablePin1;
    final GpioPinDigitalOutput mMs1Pin1;
    final GpioPinDigitalOutput mMs2Pin1;
    final GpioPinDigitalOutput mMs3Pin1;
    final GpioPinDigitalOutput mResetPin1;
   
    final GpioPinDigitalOutput mStepPin2;
    final GpioPinDigitalOutput mDirPin2;
    final GpioPinDigitalOutput mSleepPin2;
    final GpioPinDigitalOutput mEnablePin2;
    final GpioPinDigitalOutput mMs1Pin2;
    final GpioPinDigitalOutput mMs2Pin2;
    final GpioPinDigitalOutput mMs3Pin2;
    final GpioPinDigitalOutput mResetPin2;
    
    public Steppers(int drivingMode, 
            Pin dirPin1, Pin stepPin1, Pin sleepPin1, Pin resetPin1,
            Pin ms3Pin1, Pin ms2Pin1, Pin ms1Pin1, Pin enablePin1,
            Pin dirPin2, Pin stepPin2, Pin sleepPin2, Pin resetPin2,
            Pin ms3Pin2, Pin ms2Pin2, Pin ms1Pin2, Pin enablePin2) {
        
        mDrivingMode = drivingMode;
        
        gpio = GpioFactory.getInstance();
        
        mStepPin1 = gpio.provisionDigitalOutputPin(stepPin1, "Step Pin1", PinState.LOW);
        mDirPin1 = gpio.provisionDigitalOutputPin(dirPin1, "Direction Pin1", PinState.LOW);
        mSleepPin1 = gpio.provisionDigitalOutputPin(sleepPin1, "Sleep Pin1", PinState.HIGH);
        mEnablePin1 = gpio.provisionDigitalOutputPin(enablePin1, "Enable Pin1", PinState.LOW);
        mMs1Pin1 = gpio.provisionDigitalOutputPin(ms1Pin1, "MS1 Pin1", PinState.HIGH);
        mMs2Pin1 = gpio.provisionDigitalOutputPin(ms2Pin1, "MS2 Pin1", PinState.HIGH);
        mMs3Pin1 = gpio.provisionDigitalOutputPin(ms3Pin1, "MS3 Pin1", PinState.HIGH);
        mResetPin1 = gpio.provisionDigitalOutputPin(resetPin1, "Reset Pin1", PinState.HIGH);
        
        mStepPin2 = gpio.provisionDigitalOutputPin(stepPin2, "Step Pin2", PinState.LOW);
        mDirPin2 = gpio.provisionDigitalOutputPin(dirPin2, "Direction Pin2", PinState.LOW);
        mSleepPin2 = gpio.provisionDigitalOutputPin(sleepPin2, "Sleep Pin2", PinState.HIGH);
        mEnablePin2 = gpio.provisionDigitalOutputPin(enablePin2, "Enable Pin2", PinState.LOW);
        mMs1Pin2 = gpio.provisionDigitalOutputPin(ms1Pin2, "MS1 Pin2", PinState.HIGH);
        mMs2Pin2 = gpio.provisionDigitalOutputPin(ms2Pin2, "MS2 Pin2", PinState.HIGH);
        mMs3Pin2 = gpio.provisionDigitalOutputPin(ms3Pin2, "MS3 Pin2", PinState.HIGH);
        mResetPin2 = gpio.provisionDigitalOutputPin(resetPin2, "Reset Pin2", PinState.HIGH);

        setDrivingModeRight(drivingMode);
        setDrivingModeLeft(drivingMode);
        
        mStepPin1.setShutdownOptions(true, PinState.LOW);
        mDirPin1.setShutdownOptions(true, PinState.LOW);
        mSleepPin1.setShutdownOptions(true, PinState.HIGH);
        mEnablePin1.setShutdownOptions(true, PinState.LOW);
        mMs1Pin1.setShutdownOptions(true, PinState.HIGH);
        mMs2Pin1.setShutdownOptions(true, PinState.HIGH);
        mMs3Pin1.setShutdownOptions(true, PinState.HIGH);
        mResetPin1.setShutdownOptions(true, PinState.HIGH);

        mStepPin2.setShutdownOptions(true, PinState.LOW);
        mDirPin2.setShutdownOptions(true, PinState.LOW);
        mSleepPin2.setShutdownOptions(true, PinState.HIGH);
        mEnablePin2.setShutdownOptions(true, PinState.LOW);
        mMs1Pin2.setShutdownOptions(true, PinState.HIGH);
        mMs2Pin2.setShutdownOptions(true, PinState.HIGH);
        mMs3Pin2.setShutdownOptions(true, PinState.HIGH);
        mResetPin2.setShutdownOptions(true, PinState.HIGH);
    }
    
    public void setDrivingModeLeft(int drivingMode) {
        switch (drivingMode) {
            case FULL_STEP: {
                mMs1Pin1.low();
                mMs2Pin1.low();
                mMs3Pin1.low();
                break;
            }
            case HALF_STEP: {
                mMs1Pin1.high();
                mMs2Pin1.low();
                mMs3Pin1.low();
                break;
            }
            case ONE_FOURTH_STEP: {
                mMs1Pin1.low();
                mMs2Pin1.high();
                mMs3Pin1.low();
                break;
            }
            case ONE_EIGHTH_STEP: {
                mMs1Pin1.high();
                mMs2Pin1.high();
                mMs3Pin1.low();
                break;
            }
            case ONE_SIXTEENTH_STEP: {
                mMs1Pin1.high();
                mMs2Pin1.high();
                mMs3Pin1.high();
                break;
            }
        }
    }
    
    public void setDrivingModeRight(int drivingMode) {
        switch (drivingMode) {
            case FULL_STEP: {
                mMs1Pin2.low();
                mMs2Pin2.low();
                mMs3Pin2.low();
                break;
            }
            case HALF_STEP: {
                mMs1Pin2.high();
                mMs2Pin2.low();
                mMs3Pin2.low();
                break;
            }
            case ONE_FOURTH_STEP: {
                mMs1Pin2.low();
                mMs2Pin2.high();
                mMs3Pin2.low();
                break;
            }
            case ONE_EIGHTH_STEP: {
                mMs1Pin2.high();
                mMs2Pin2.high();
                mMs3Pin2.low();
                break;
            }
            case ONE_SIXTEENTH_STEP: {
                mMs1Pin2.high();
                mMs2Pin2.high();
                mMs3Pin2.high();
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

    public void move(int distance, float interval, int drivingMode, float leftRatio, float rightRatio) throws InterruptedException {
        int leftSteps = (int) (distance * leftRatio);
        int rightSteps = (int) (distance * rightRatio);
        float leftIncrementVal = 0;
        float rightIncrementVal = 0;
        
        if (leftSteps == 0){
            leftIncrementVal = 0;
        }
        else if(leftSteps > 0){
            leftIncrementVal = distance / leftSteps;
        }
        if (rightSteps == 0){
            rightIncrementVal = 0;
        }
        else if (rightSteps > 0){
            rightIncrementVal = distance / rightSteps;
        }
        
        float leftIncrementOn = 0;
        float rightIncrementOn = 0;
        float leftIncrementOff = 0;
        float rightIncrementOff = 0;
        
        if (drivingMode != mDrivingMode) {
            setDrivingModeRight(drivingMode);
            setDrivingModeLeft(drivingMode);
        }

        if (distance < 0) {
            setDirection(BACKWARD);
        }
        else {
            setDirection(FORWARD);
        }
        
        //h.ps(distance);
        //TimeUnit.MICROSECONDS.sleep((long) (1 / (Math.abs(currentAngle)) * 50000));

        //h.ps("h:" + leftIncrementVal + " " + rightIncrementVal);
        
        if(rightRatio < 1.0){
            setDrivingModeRight(1);
        }
        
        if(leftRatio < 1.0){
            setDrivingModeLeft(1);
        }
        
        for (int i = 0; i < Math.abs(distance); i++) {
            /*
            if(leftIncrementOn <= i){
                mStepPin1.high();
                leftIncrementOn += leftIncrementVal;
            }
            if(rightIncrementOn <= i){
                mStepPin2.high();
                rightIncrementOn += rightIncrementVal;
            }
            
            //Thread.sleep(interval);
            TimeUnit.NANOSECONDS.sleep((long)(interval));
            
            if(leftIncrementOff <= i){
                mStepPin1.low();
                leftIncrementOff += leftIncrementVal;
            }
            if(rightIncrementOff <= i){
                mStepPin2.low();
                rightIncrementOff += rightIncrementVal;
            }
`           */
            
            mStepPin1.high();
            mStepPin2.high();
            
            TimeUnit.NANOSECONDS.sleep((long)(interval));
            
            mStepPin1.low();
            mStepPin2.low();
            
            TimeUnit.NANOSECONDS.sleep((long)(interval));
            
            //h.ps(leftIncrementOn + " " + rightIncrementOn + " " + i);
            //h.ps("LI:" + leftRatio + " RI:" + rightRatio + " S:" + distance + " L:" + leftIncrementOn + " R:"  + rightIncrementOn);
            
        }
        
        setDrivingModeRight(0);
        setDrivingModeLeft(0);
    }

    public void move(int steps, float interval, float leftRatio, float rightRatio) throws InterruptedException {
        move(steps, interval, mDrivingMode, leftRatio, rightRatio);
    }

    public void sleep() {
        mSleepPin1.low();
        mSleepPin2.low();
    }

    public void wake() {
        mSleepPin1.high();
        mSleepPin2.high();
    }

    public void reset() {
        mResetPin1.low();
        mResetPin2.low();
    }

    public void enable() {
        mEnablePin1.low();
        mEnablePin2.low();
    }

    public void disable() {
        mEnablePin1.high();
        mEnablePin2.high();
    }

    public void shutdown() {
        gpio.unprovisionPin(mStepPin1);
        gpio.unprovisionPin(mDirPin1);
        gpio.unprovisionPin(mSleepPin1);
        gpio.unprovisionPin(mEnablePin1);
        gpio.unprovisionPin(mMs1Pin1);
        gpio.unprovisionPin(mMs2Pin1);
        gpio.unprovisionPin(mResetPin1);
        
        gpio.unprovisionPin(mStepPin2);
        gpio.unprovisionPin(mDirPin2);
        gpio.unprovisionPin(mSleepPin2);
        gpio.unprovisionPin(mEnablePin2);
        gpio.unprovisionPin(mMs1Pin2);
        gpio.unprovisionPin(mMs2Pin2);
        gpio.unprovisionPin(mResetPin2);
        
        gpio.shutdown();
    }

    public void setDirection(int direction) {
        if (direction == FORWARD) {
            mDirPin1.high();
            mDirPin2.high();
        } else if (direction == BACKWARD) {
            mDirPin1.low();
            mDirPin2.low();
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