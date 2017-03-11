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
public abstract class StepperDriver{
    public final String FORWARD = "FORWARD";
    public final String BACKWARD = "BACKWARD";
    
    public abstract void step(int delay);//delay in nanoseconds
    public abstract void enable();
    public abstract void disable();
    public abstract void setDirection(String direction);
}
