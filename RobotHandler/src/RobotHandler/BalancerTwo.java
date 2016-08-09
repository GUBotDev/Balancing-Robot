package RobotHandler;


import java.io.IOException;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Rollie
 */
public class BalancerTwo {
    public void readAngle() throws IOException{
        /*
        int high = Mpu6050Controller.readRegister(Mpu6050Registers.MPU6050_RA_GYRO_XOUT_H);
        int low = Mpu6050Controller.readRegister(Mpu6050Registers.MPU6050_RA_GYRO_XOUT_L);
        float value = (high << 8) + low;
        */
        //MPU6050_RA_DMP_INT_STATUS
        
        h.ps(Mpu6050Controller.readRegister(Mpu6050Registers.MPU6050_RA_DMP_INT_STATUS));
        h.ps(Mpu6050Controller.readRegister(Mpu6050Registers.MPU6050_RA_DMP_CFG_1));
        h.ps(Mpu6050Controller.readRegister(Mpu6050Registers.MPU6050_RA_DMP_CFG_2));
        
        //return high;
    }
}
