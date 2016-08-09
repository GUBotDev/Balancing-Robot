/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package RobotHandler;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Rollie
 */
public class ReadMPUThread implements Runnable{
    private final float errorOffset = 2.0f;//1.9875f;//corrects bias in gyro direction - 1.9875f -> specific to sensor
    private float gyroX = 0.0f;
    private float accelX = 0.0f;
    private static volatile float angle = 0.0f;
    private float previousAngle = 0.0f;
    static Thread t;
    private String name;
    
    @Override
    public void run() {
        while(true){
            try {
                accelX = (readXAccelerometer() + readXAccelerometer() + readXAccelerometer() + readXAccelerometer() + readXAccelerometer())/ 5;
                gyroX = (readXGyroscope() + readXGyroscope() + readXGyroscope() + readXGyroscope() + readXGyroscope()) / 5;
                
                angle = ((0.75f * (angle + gyroX) + (0.25f) * accelX));//complementary filter
                
                angle = 0.375f * angle + (1 - 0.375f) * previousAngle;//kalman filter
                previousAngle = angle;
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
