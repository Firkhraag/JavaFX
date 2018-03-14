package org.spbu.histology.model;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class TetgenPoint {
    
    private final SimpleIntegerProperty id;
    private final SimpleDoubleProperty x;
    private final SimpleDoubleProperty y;
    private final SimpleDoubleProperty z;
    
    public TetgenPoint(int i, double x, double y, double z) {
        this.id = new SimpleIntegerProperty(i);
        this.x = new SimpleDoubleProperty(x);
        this.y = new SimpleDoubleProperty(y);
        this.z = new SimpleDoubleProperty(z);
    }
    
    public int getId() {
        return id.get();
    }
 
    public void setId(int v) {
        id.set(v);
    }
         
    public double getX() {
        return x.get();
    }
 
    public void setX(double v) {
        x.set(v);
    }
         
    public double getY() {
        return y.get();
    }
 
    public void setY(double v) {
        y.set(v);
    }
         
    public double getZ() {
        return z.get();
    }
 
    public void setZ(double v) {
        z.set(v);
    }
}
