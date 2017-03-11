/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package balancingrobot;

import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;
import java.io.IOException;

/**
 *
 * @author Rollie
 */
public class MPU6050 {
    private final byte MPU6050_ADDRESS = 0x68;
    private final Vector rotationVector;
    private final Vector accelerationVector;
    private final Vector gravityVector;
    private I2CDevice mpu6050;
    private I2CBus bus;
    
    
    MPU6050(){
        rotationVector = new Vector();
        accelerationVector = new Vector();
        gravityVector = new Vector();
    }
    
    public Vector getRotationVector(){
        return rotationVector;
    }
    
    public Vector getAccelerationVector(){
        return accelerationVector;
    }
    
    public Vector getGravityVector(){
        return gravityVector;
    }
    
    public void calculateGravityVector(){
        calculateXGravityVector();
        calculateYGravityVector();
        calculateZGravityVector();
    }
    
    public void calculateXGravityVector(){
        double degrees = Math.atan2(accelerationVector.getY(), pythagoreanTheorem(accelerationVector.getX(), accelerationVector.getZ())) * 57.2958;
        
        gravityVector.setX(degrees);
    }
    
    public void calculateYGravityVector(){
        throw new java.lang.UnsupportedOperationException("Not supported yet.");
    }
    
    public void calculateZGravityVector(){
        throw new java.lang.UnsupportedOperationException("Not supported yet.");
    }
    
    public void readGyroscope(){
        readXGyroscope();
        readYGyroscope();
        readZGyroscope();
    }
    
    public void readAccelerometer(){
        readXAccelerometer();
        readYAccelerometer();
        readZAccelerometer();
    }
    
    public void readXGyroscope(){
        double value = 0;
        
        try {
            value = readWord2C(MPU6050Registers.MPU6050_RA_GYRO_XOUT_H) / 131;//250 degrees per second with 131 sensitivity
        } catch (IOException ex) {}
        
        rotationVector.setX(value);
    }
    
    public void readYGyroscope(){
        double value = 0;
        
        try {
            value = readWord2C(MPU6050Registers.MPU6050_RA_GYRO_YOUT_H) / 131;
        } catch (IOException ex) {}
        
        rotationVector.setY(value);
    }
    
    public void readZGyroscope(){
        double value = 0;
        
        try {
            value = readWord2C(MPU6050Registers.MPU6050_RA_GYRO_ZOUT_H) / 131;
        } catch (IOException ex) {}
        
        rotationVector.setZ(value);
    }
    
    public void readXAccelerometer(){
        try {
            accelerationVector.setX(readWord2C(MPU6050Registers.MPU6050_RA_ACCEL_XOUT_H) / 16384);//2g acceleration with 16384 sensitivity
        } catch (IOException ex) {}
    }
    
    public void readYAccelerometer(){
        try {
            accelerationVector.setY((readWord2C(MPU6050Registers.MPU6050_RA_ACCEL_YOUT_H) / 16384));
        } catch (IOException ex) {}
    }
    
    public void readZAccelerometer(){
        try {
            accelerationVector.setZ((readWord2C(MPU6050Registers.MPU6050_RA_ACCEL_ZOUT_H) / 16384));
        } catch (IOException ex) {}
    }
    
    public void initialize() throws IOException, InterruptedException {
        initializeI2C();
        configureMpu6050();
    }

    private void initializeI2C() throws IOException {
        System.out.println("Creating I2C bus");
        bus = I2CFactory.getInstance(I2CBus.BUS_1);
        System.out.println("Creating I2C device");
        mpu6050 = bus.getDevice(MPU6050_ADDRESS);
    }
    
    private void configureMpu6050() throws IOException, InterruptedException {

        //1 Waking the device up
        writeConfigRegisterAndValidate(
                "Waking up device",
                "Wake-up config succcessfully written: ",
                MPU6050Registers.MPU6050_RA_PWR_MGMT_1,
                MPU6050RegisterValues.MPU6050_RA_PWR_MGMT_1);

        //2 Configure sample rate
        writeConfigRegisterAndValidate(
                "Configuring sample rate",
                "Sample rate succcessfully written: ",
                MPU6050Registers.MPU6050_RA_SMPLRT_DIV,
                MPU6050RegisterValues.MPU6050_RA_SMPLRT_DIV);

        //3 Setting global config
        writeConfigRegisterAndValidate(
                "Setting global config (digital low pass filter)",
                "Global config succcessfully written: ",
                MPU6050Registers.MPU6050_RA_CONFIG,
                MPU6050RegisterValues.MPU6050_RA_CONFIG);

        //4 Configure Gyroscope
        writeConfigRegisterAndValidate(
                "Configuring gyroscope",
                "Gyroscope config successfully written: ",
                MPU6050Registers.MPU6050_RA_GYRO_CONFIG,
                MPU6050RegisterValues.MPU6050_RA_GYRO_CONFIG);

        //5 Configure Accelerometer
        writeConfigRegisterAndValidate(
                "Configuring accelerometer",
                "Accelerometer config successfully written: ",
                MPU6050Registers.MPU6050_RA_ACCEL_CONFIG,
                MPU6050RegisterValues.MPU6050_RA_ACCEL_CONFIG);

        //6 Configure interrupts
        writeConfigRegisterAndValidate(
                "Configuring interrupts",
                "Interrupt config successfully written: ",
                MPU6050Registers.MPU6050_RA_INT_ENABLE,
                MPU6050RegisterValues.MPU6050_RA_INT_ENABLE);

        //7 Configure low power operations
        writeConfigRegisterAndValidate(
                "Configuring low power operations",
                "Low power operation config successfully written: ",
                MPU6050Registers.MPU6050_RA_PWR_MGMT_2,
                MPU6050RegisterValues.MPU6050_RA_PWR_MGMT_2);
        
    }
      
    private void writeRegister(byte register, byte data) throws IOException {
        mpu6050.write(register, data);
    }

    public byte readRegister(byte register) throws IOException {
        int data;
        
        while(true){
            data = mpu6050.read(register);
            break;
        }
        
        return (byte) data;
    }

    public byte readRegister() throws IOException {
        int data = mpu6050.read();
        return (byte) data;
    }
    
    public void writeConfigRegisterAndValidate(String initialText, String successText, byte register, byte registerData) throws IOException {
        System.out.println(initialText);
        writeRegister(register, registerData);
        byte returnedRegisterData = readRegister(register);
        if (returnedRegisterData == registerData) {
            System.out.println(successText + formatBinary(returnedRegisterData));
        } else {
            throw new RuntimeException("Tried to write " + formatBinary(registerData) + " to "
                    + register + ", but validiating value returned " + formatBinary(returnedRegisterData));
        }
    }
    
    public String formatBinary(byte b) {
        String binaryString = Integer.toBinaryString(b);
        if (binaryString.length() > 8) {
            binaryString = binaryString.substring(binaryString.length() - 8);
        }
        if (binaryString.length() < 8) {
            byte fillingZeros = (byte) (8 - binaryString.length());
            for (int j = 1; j <= fillingZeros; j++) {
                binaryString = "0" + binaryString;
            }
        }
        return binaryString;
    }
    
    public double pythagoreanTheorem(double a, double b){
        return (double) Math.sqrt((a * a) + (b * b));
    }
    
    public double readWord(byte addr) throws IOException{
        int high = readRegister(addr);
        int low = readRegister((byte) (addr + 1));
        double value = (high << 8) + low;
        
        return value;
    }
    
    public double readWord2C(byte addr) throws IOException{
        double value = readWord(addr);
        
        if (value >= 0x8000){
            value = (double) -((65535 - value) + 1);
        }
        else{
            value = (double) value;
        }
        
        return value;
    }
}
