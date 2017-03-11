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
public class KalmanFilter extends Filter{
    private final double GAIN;
    private double filteredValue;
    
    KalmanFilter(double gain){
        this.GAIN = gain;
    }

    /**
     *
     * @param values
     */
    @Override
    public void filter(double... values) {
        int i = 0;
        double sum = 0;
        double avg;
        double gainInverse = (1 - GAIN);
        
        for(double value : values){
            sum += value;
            i++;
        }
        
        avg = sum / i;
        
        filteredValue = (GAIN * filteredValue) + (gainInverse * avg);
    }
    
    public double getFilteredValue(){
        return filteredValue;
    }
}
