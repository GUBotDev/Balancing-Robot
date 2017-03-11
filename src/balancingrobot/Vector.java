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
public class Vector {
    private double x;
    private double y;
    private double z;
    
    Vector(){
        this.x = 0;
        this.y = 0;
        this.z = 0;
    }
    
    Vector(double x, double y, double z){
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    public Vector getVector(){
        return this;
    }
    
    public double getX(){
        return x;
    }
    
    public double getY(){
        return y;
    }
    
    public double getZ(){
        return z;
    }
    
    public void setX(double x){
        this.x = x;
    }
    
    public void setY(double y){
        this.y = y;
    }
    
    public void setZ(double z){
        this.z = z;
    }
    
    public void addVector(Vector vector){
        this.x += vector.getX();
        this.y += vector.getY();
        this.z += vector.getZ();
    }
    
    public void subtractVector(Vector vector){
        this.x -= vector.getX();
        this.y -= vector.getY();
        this.z -= vector.getZ();
    }
    
    public void multiplyVector(Vector vector){
        this.x *= vector.getX();
        this.y *= vector.getY();
        this.z *= vector.getZ();
    }
    
    public void divideVector(Vector vector){
        this.x /= vector.getX();
        this.y /= vector.getY();
        this.z /= vector.getZ();
    }
    
    public void scalarProduct(Vector vector){
        throw new java.lang.UnsupportedOperationException("Not supported yet.");
    }
    
    public void crossProduct(Vector vector){
        throw new java.lang.UnsupportedOperationException("Not supported yet.");
    }
}
