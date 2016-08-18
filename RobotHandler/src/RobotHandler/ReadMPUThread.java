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
public class ReadMPUThread implements Runnable{
    private final boolean complementaryFilter = true;
    private final boolean kalmanFilterAngle = true;
    private final boolean kalmanFilterAccel = false;
    private final boolean kalmanFilterGyro = false;
    private final boolean printAngleInfo = false;
    
    private static volatile float angle = 0.0f;
    
    private final float kalmanGainAccel = 0.5f;//
    private final float kalmanGainAngle = 0.05f;//0.15f
    private final float kalmanGainGyro = 0.5f;//
    private final float errorOffset = 2.0f;//1.9875f;//corrects bias in gyro direction - 1.9875f -> specific to sensor
    private final int averageAmount = 2;
    private float gyroX = 0.0f;
    private float accelX = 0.0f;
    private float previousAngle = 0.0f;
    private float previousAccelX = 0.0f;
    private float previousGyroX = 0.0f;
    private final float complementaryRatio = 0.9f;//0.25 -> Gyro*0.25, Accel*0.75
    static Thread t;
    
    @Override
    public void run() {
        while(true){
            try {
                for(int i = 0; i < averageAmount; i++){
                    accelX += readXAccelerometer();
                    gyroX += readXGyroscope();
                }
                
                accelX /= averageAmount;
                gyroX /= averageAmount;
                
                //Accelerometer Kalman Filter
                if(kalmanFilterAccel){
                    accelX = kalmanGainAccel * accelX + (1 - kalmanGainAccel) * previousAccelX;//kalman filter for accel
                }
                
                //Gyroscope Kalman Filter
                if(kalmanFilterGyro){
                    gyroX = kalmanGainGyro * gyroX + (1 - kalmanGainAccel) * previousGyroX;//kalman filter for accel
                }
                
                //ComplementaryFilter
                if(complementaryFilter){
                    angle = ((complementaryRatio * (angle + gyroX) + (1.0f - complementaryRatio) * accelX));//complementary filter
                }
                else{
                    angle = accelX;
                }
                
                //Angle Kalman Filter
                if(kalmanFilterAngle){
                    angle = kalmanGainAngle * angle + (1 - kalmanGainAngle) * previousAngle;//kalman filter
                }
                
                if(printAngleInfo){
                     h.ps("Angle:" + angle + " AccelX:" + accelX + " GyroX:" + gyroX);
                }
                
                //h.ps(angle);
                
                previousAngle = angle;
                previousGyroX = gyroX;
                previousAccelX = accelX;
                accelX = 0;
                gyroX = 0;
            } catch (IOException ex) {
                
            }
        }
    }
    
    public void start(){
        if(t == null){
            t = new Thread(this, "MPU");
            t.start();
        }
    }
    
    public static float returnAngle(){
        return angle;
    }
    
    /***
     * 
     * @return Returns the degrees per second on the x axis - angular velocity
     * @throws IOException 
     * 
     */
    public float readXGyroscope() throws IOException{
        int high = Mpu6050Controller.readRegister(Mpu6050Registers.MPU6050_RA_GYRO_XOUT_H);
        int low = Mpu6050Controller.readRegister(Mpu6050Registers.MPU6050_RA_GYRO_XOUT_L);
        float value = (high << 8) + low;
        
        if (value >= 0x8000){
            value = (short) (-((65535 - value) + 1) / 131);
        }
        else{
            value = (short)(value / 131);
        }
        
        value += errorOffset;
            
        if(Math.abs(value) < 3.0f){
            value = 0;   
        }
        
        return value;
    }
    
    /***
     * 
     * @param a
     * @param b
     * @return 
     * 
     * Pythagorean theorem
     */
    public float dist(float a, float b){
        return (float) Math.sqrt((a * a) + (b * b));
    }
    
    /***
     * 
     * @return
     * @throws IOException 
     * 
     * Reads all accelerometer axes to determine the gravity vector.
     */
    public float readXAccelerometer() throws IOException{
        float accelerationX = (float) (readWord2C(Mpu6050Registers.MPU6050_RA_ACCEL_XOUT_H) / 16384.0);
        float accelerationY = (float) (readWord2C(Mpu6050Registers.MPU6050_RA_ACCEL_YOUT_H) / 16384.0);
        float accelerationZ = (float) (readWord2C(Mpu6050Registers.MPU6050_RA_ACCEL_ZOUT_H) / 16384.0);
        
        float radians = (float) Math.atan2(accelerationY, dist(accelerationX, accelerationZ));
        
        return radians * 57.2958f;
    }
    
    /**
     * 
     * @param addr
     * @return
     * @throws IOException 
     * 
     * Reads from i2c device
     */
    public float readWord(byte addr) throws IOException{
        int high = Mpu6050Controller.readRegister(addr);
        int low = Mpu6050Controller.readRegister((byte) (addr + 1));
        float value = (high << 8) + low;
        
        return value;
    }
    
    /**
     * 
     * @param addr
     * @return
     * @throws IOException 
     * 
     * Reads corrected value from i2c
     */
    public float readWord2C(byte addr) throws IOException{
        float value = readWord(addr);
        
        if (value >= 0x8000){
            value = (short) (-((65535 - value) + 1) / 131);
        }
        else{
            value = (short)(value / 131);
        }
        
        return value;
    }

}
