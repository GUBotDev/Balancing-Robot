/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package balancingrobot;

import java.io.IOException;

/**
 *
 * @author Rollie
 */
public class BalancingRobot { 
    private static MPU6050 mpu;
    private static StepperDriver stepperLeft;
    private static StepperDriver stepperRight;
    private static KalmanFilter velocityKF;
    private static KalmanFilter gyroscopeKF;
    private static KalmanFilter accelerometerKF;
    private static ComplementaryFilter mpuFusionCF;
    private static Controller controller;
    private static PID anglePID;
    private static PID speedPID;
    private static Pins pins;
    
    private static double stepperLeftSpeedRatio;
    private static double stepperRightSpeedRatio;
    private static double previousAngle;
    private static double targetAngle;
    private static double speed;
    private static int stepperIntervalDelay;
    
    private static final int MAXSTEPINTERVAL = 10000000;//10,000,000 nanoseconds
    
    private static Thread mpuThread;
    private static Thread stepperLeftThread;
    private static Thread stepperRightThread;
    private static Thread controllerThread;

    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException {
        initialize();
        
        runMPU();
        runSteppers();
        
        while(true){
            balance();
        }
    }
    
    public static void initialize() throws IOException{
        mpu = new MPU6050();
        stepperLeft = new A4988(pins.getPin(0),pins.getPin(1),pins.getPin(2),
                                pins.getPin(3),pins.getPin(4),pins.getPin(5),
                                pins.getPin(6),pins.getPin(7));
        stepperRight = new A4988(pins.getPin(18),pins.getPin(19),pins.getPin(20),
                                pins.getPin(21),pins.getPin(22),pins.getPin(23),
                                pins.getPin(24),pins.getPin(25));
        velocityKF = new KalmanFilter(0.125);
        gyroscopeKF = new KalmanFilter(0.25);
        accelerometerKF = new KalmanFilter(0.25);
        mpuFusionCF = new ComplementaryFilter(0.4, 0.6);
        controller = new Controller(8001);
        anglePID = new PID(0.5, 0, 28, 15);
        speedPID = new PID(0.1, 0.04, 0, 500);
    }
    
    public static void balance(){
        double angle = mpuFusionCF.getFilteredValue();
        double angularVelocity = angle - previousAngle;
        double estimatedVelocity = -speed - angularVelocity;
        
        velocityKF.filter(estimatedVelocity);
        
        anglePID.addIteration(speed, velocityKF.getFilteredValue());
        speedPID.addIteration(anglePID.returnCorrectedValue(), angle + targetAngle);
        
        speed = speedPID.returnCorrectedValue();
        previousAngle = angle;
        
        speedToDelay();
    }
    
    public static void readMPU(){
        mpu.readAccelerometer();
        mpu.readXGyroscope();
        mpu.calculateGravityVector();
    }
    
    public static void filterMPU(){
        gyroscopeKF.filter(mpu.getRotationVector().getX());
        accelerometerKF.filter(mpu.getGravityVector().getX());
        
        mpuFusionCF.filter(gyroscopeKF.getFilteredValue(), accelerometerKF.getFilteredValue());
    }
    
    public static void runMPU(){
        mpuThread = new Thread() {
            @Override
            public void run() {
                readMPU();
                filterMPU();
            }
        };
        mpuThread.start();
    }
    
    public static void runSteppers(){
        stepperLeftThread = new Thread() {
            @Override
            public void run() {
                stepperLeft.step((int)(stepperIntervalDelay / stepperLeftSpeedRatio));
            }
        };
        stepperRightThread = new Thread() {
            @Override
            public void run() {
                stepperRight.step((int)(stepperIntervalDelay / stepperRightSpeedRatio));
            }
        };
        
        stepperLeftThread.start();
        stepperRightThread.start();
    }
    
    public static void runController(){
        controllerThread = new Thread() {
            @Override
            public void run() {
                controller.readControls();
                
                Vector motion = controller.getMotion();
                
                motion.getX();
                motion.getY();
                
                //don't allow stepper ratios to reach 0
                
                
                //convert vectors to target angle and left/right ratios
            }
        };
        controllerThread.start();
    }
    
    public static void speedToDelay(){
        stepperIntervalDelay = (int)(MAXSTEPINTERVAL / Math.abs(speed));
    }
}
