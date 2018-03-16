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
    private double previousOffset;
    private double maxIterationError;
    private double maxTotalError;
    private double offsetOld;
    private double offsetNew;
    private double time;
    private double output;
    private double integral;
    
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
        
        integral += offset * ((currentTime - time) * 1000);
        
        output = p * offset;
        output += i * integral;
        output -= (offset - previousOffset) / ((currentTime - time) * 1000);
        
        previousOffset = offset;
        
        time = currentTime;
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
