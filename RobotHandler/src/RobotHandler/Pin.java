/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package RobotHandler;

import java.io.FileWriter;
import java.io.IOException;

/**
 *
 * @author Rollie
 */
public final class Pin {
    private FileWriter control;
    private FileWriter direction;
    static public final String OUT = "out";
    static public final String IN = "in";
    private final String pin;
    
    Pin(String pin, String direction){
        this.pin = pin;
        
        try{
            setDirection(direction);
            control = new FileWriter("/sys/class/gpio/gpio" + pin + "/value");
        }
        catch(IOException ioEx){}
    }
    
    private void setDirection(String dir) throws IOException{
        direction = new FileWriter("/sys/class/gpio/gpio" + pin + "/direction");
        direction.write(dir);
        direction.flush();
    }
    
    public void printPinName(){
        System.out.print("Pin name: " + pin);
    }
    
    public void setHigh(){
        try{
            control.write("1");
            control.flush();
        }
        catch(IOException ioEx){}
    }
    
    public void setLow(){
        try{
            control.write("0");
            control.flush();
        }
        catch(IOException ioEx){}
    }
    
    public String readHigh(){
        return null;
    }
    
    public String readLow(){
        return null;
    }
}
