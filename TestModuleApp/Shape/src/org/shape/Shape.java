package org.shape;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Shape {
    
    private final String name; 
    
    public String getName() {
        return this.name;
    }
    
    private StringProperty xCoordinate = new SimpleStringProperty(this, "x");
    public final StringProperty xCoordinateProperty() {
        return this.xCoordinate;
    }
    
    public String getXCoordinate() {
        return this.xCoordinateProperty().get();
    }
    
    public void setXCoordinate(String xCoordinate) {
        this.xCoordinateProperty().set(xCoordinate);
    }
    
    private StringProperty yCoordinate = new SimpleStringProperty(this, "y");
    public final StringProperty yCoordinateProperty() {
        return this.yCoordinate;
    }
    
    public String getYCoordinate() {
        return this.yCoordinateProperty().get();
    }
    
    public void setYCoordinate(String yCoordinate) {
        this.yCoordinateProperty().set(yCoordinate);
    }
    
    private StringProperty zCoordinate = new SimpleStringProperty(this, "z");
    public final StringProperty zCoordinateProperty() {
        return this.zCoordinate;
    }
    
    public String getZCoordinate() {
        return this.zCoordinateProperty().get();
    }
    
    public void setZCoordinate(String zCoordinate) {
        this.zCoordinateProperty().set(zCoordinate);
    }
    
    public Shape(String name) {       
        this.name = name;
        this.xCoordinate.set("0");
        this.yCoordinate.set("0");
        this.zCoordinate.set("0");        
    }
    
    public Shape(String name, String x, String y, String z) {       
        this.name = name;
        this.xCoordinate.set(x);
        this.yCoordinate.set(y);
        this.zCoordinate.set(z);        
    }
    
    public Shape(Shape shape) {       
        this.name = shape.getName();
        this.xCoordinate.set(shape.getXCoordinate());
        this.yCoordinate.set(shape.getYCoordinate());
        this.zCoordinate.set(shape.getZCoordinate());        
    }
    
}
