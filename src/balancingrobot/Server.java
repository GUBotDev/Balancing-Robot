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
public abstract class Server {
    private final int PORT;
    
    Server(int port){
        this.PORT = port;
    }
    
    public abstract String readLine();
    public abstract void writeLine(String line);
}
