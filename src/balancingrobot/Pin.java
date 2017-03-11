/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package balancingrobot;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 *
 * @author Rollie
 */
public class Pin {
    static public final String OUT = "out";
    static public final String IN = "in";
    private FileWriter pinWriter;
    private FileWriter direction;
    private FileReader pinReader;
    private String name;
    
    Pin(String name, String dir){
        this.name = name;
        
        try{
            direction = new FileWriter("/sys/class/gpio/gpio" + name + "/direction");
            direction.write(dir);
            direction.flush();
            pinWriter = new FileWriter("/sys/class/gpio/gpio" + name + "/value");
        }
        catch(IOException ioEx){}
    }
    
    public String getName(){
        return name;
    }
    
    public Boolean getPinValue(){
        try{
            return pinReader.read() == 1;
        }
        catch(IOException ioEx){}
        
        return false;
    }
    
    public void setPinValue(Boolean value){
        try{
            if(value){
                pinWriter.write("1");
                pinWriter.flush();
            }
            else{
                pinWriter.write("0");
                pinWriter.flush();
            }
        }
        catch(IOException ioEx){}
    }
}
