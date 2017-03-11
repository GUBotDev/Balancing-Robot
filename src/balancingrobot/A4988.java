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
public class A4988 extends StepperDriver{
    private final Pin enable;
    private final Pin ms1;
    private final Pin ms2;
    private final Pin ms3;
    private final Pin reset;
    private final Pin sleep;
    private final Pin step;
    private final Pin direction;
    
    A4988(Pin enable, Pin ms1, Pin ms2, Pin ms3, Pin reset, Pin sleep, Pin step, Pin direction){
        this.enable = enable;
        this.ms1 = ms1;
        this.ms2 = ms2;
        this.ms3 = ms3;
        this.reset = reset;
        this.sleep = sleep;
        this.step = step;
        this.direction = direction;
        
        this.enable.setPinValue(false);
        this.ms1.setPinValue(true);
        this.ms2.setPinValue(true);
        this.ms3.setPinValue(true);
        this.reset.setPinValue(true);
        this.sleep.setPinValue(true);
        this.step.setPinValue(false);
        this.direction.setPinValue(false);
    }
    
    @Override
    public void step(int delay) {
        step.setPinValue(true);
        
        delay(delay);
        
        step.setPinValue(false);
        
        delay(delay);
    }

    @Override
    public void enable() {
        enable.setPinValue(false);
    }

    @Override
    public void disable() {
        enable.setPinValue(true);
    }

    @Override
    public void setDirection(String dir) {
        switch(dir){
            case "Forward":
                direction.setPinValue(true);
                break;
            case "Backward":
                direction.setPinValue(false);
                break;
            default:
                break;
        }
    }
    
    private void delay(int nanoSeconds){
        long startTime = System.nanoTime();
        
        while((System.nanoTime() - startTime) < nanoSeconds){}
    }
}
