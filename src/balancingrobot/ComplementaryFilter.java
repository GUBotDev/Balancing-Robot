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
public class ComplementaryFilter extends Filter{
    private final double ratios[];
    private double filteredValue;
    
    ComplementaryFilter(double... ratios){
        this.ratios = new double[ratios.length];
        int i = 0;
        
        for(double ratio : ratios){
            this.ratios[i] = ratio;
            i++;
        }
    }
    
    /**
     *
     * @param values 
     */
    @Override
    public void filter(double... values) {
        int i = 0;
        filteredValue = 0;
        
        for(double value : values){
            filteredValue += value * ratios[i];
            i++;
        }
    }
    
    public double getFilteredValue(){
        return filteredValue;
    }
}
