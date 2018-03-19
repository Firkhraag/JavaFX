package org.spbu.histology.model;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class TetgenFacetHole {
    
    private final SimpleIntegerProperty id;
    private final SimpleDoubleProperty x;
    private final SimpleDoubleProperty y;
    private final SimpleDoubleProperty z;
    private final SimpleIntegerProperty facetNumber;
    
    public TetgenFacetHole(TetgenFacetHole p) {
        this.id = new SimpleIntegerProperty(p.getId());
        this.x = new SimpleDoubleProperty(p.getX());
        this.y = new SimpleDoubleProperty(p.getY());
        this.z = new SimpleDoubleProperty(p.getZ());
        this.facetNumber = new SimpleIntegerProperty(p.getFacetNumber());
    }
    
    public TetgenFacetHole(int i, int facetNumber, double x, double y, double z) {
        this.id = new SimpleIntegerProperty(i);
        this.facetNumber = new SimpleIntegerProperty(facetNumber);
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
    
    public int getFacetNumber() {
        return facetNumber.get();
    }
 
    public void setFacetNumber(int v) {
        facetNumber.set(v);
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
