/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package balancingrobot;

/**
 *
 * @author Rollie
 */
public class PID {
    private final double maxOutput;
    private final double p;
    private final double i;
    private final double d;
    private double offset = 0;
    private double maxIterationError;
    private double maxTotalError;
    private double offsetOld;
    private double offsetNew;
    private double errorSum;
    private double time;
    private double output;
    private double previousTarget;
    
    PID(double p, double i, double d, double maxOutput){
        this.p = p;
        this.i = i;
        this.d = d;
        this.maxOutput = maxOutput;
        
        time = ((double)System.nanoTime()) / 1000000000;
    }
    
    public void addIteration(double target, double actual){
        double currentTime = ((double)System.nanoTime()) / 1000000000;
        offset = target - actual;
        
        errorSum += constrain(offset, -maxIterationError, maxIterationError);
        errorSum = constrain(errorSum, -maxTotalError, maxTotalError);
        
        output = p * offset;
        output += i * errorSum * (currentTime - time);
        output -= (d * (target - previousTarget) - d * (actual - offsetOld)) / ((currentTime - time) * 1000);
        
        offsetOld = offsetNew;
        offsetNew = offset;
        
        previousTarget = target;
        
        time = currentTime;
        previousTarget = target;
    }
    
    public double returnCorrectedValue(){
        return constrain(output, -maxOutput, maxOutput);
    }
    
    private double constrain(double value, double minimum, double maximum){
        if(value > maximum){
            value = maximum;
        }
        else if (value < minimum){
            value = minimum;
        }
        
        return value;
    }
}
