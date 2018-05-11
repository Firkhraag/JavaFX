package org.spbu.histology.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class GroupTransforms {
    private static StringProperty xRotate = new SimpleStringProperty("0");
    public static final StringProperty xRotateProperty() {
        return xRotate;
    }
    
    public static String getXRotate() {
        return xRotateProperty().get();
    }
    
    public static void setXRotate(String xRotate) {
        xRotateProperty().set(xRotate);
    }
    
    private static StringProperty yRotate = new SimpleStringProperty("0");
    public static final StringProperty yRotateProperty() {
        return yRotate;
    }
    
    public static String getYRotate() {
        return yRotateProperty().get();
    }
    
    public static void setYRotate(String yRotate) {
        yRotateProperty().set(yRotate);
    }
    
    /*private static StringProperty zRotate = new SimpleStringProperty("0");
    public static final StringProperty zRotateProperty() {
        return zRotate;
    }
    
    public static String getZRotate() {
        return zRotateProperty().get();
    }
    
    public static void setZRotate(String zRotate) {
        zRotateProperty().set(zRotate);
    }*/
    
    private static StringProperty xCoordinate = new SimpleStringProperty("0");
    public static final StringProperty xCoordinateProperty() {
        return xCoordinate;
    }
    
    public static String getXCoordinate() {
        return xCoordinateProperty().get();
    }
    
    public static void setXCoordinate(String xCoordinate) {
        xCoordinateProperty().set(xCoordinate);
    }
    
    private static StringProperty yCoordinate = new SimpleStringProperty("0");
    public static final StringProperty yCoordinateProperty() {
        return yCoordinate;
    }
    
    public static String getYCoordinate() {
        return yCoordinateProperty().get();
    }
    
    public static void setYCoordinate(String yCoordinate) {
        yCoordinateProperty().set(yCoordinate);
    }
    
    private static StringProperty zCoordinate = new SimpleStringProperty("0");
    public static final StringProperty zCoordinateProperty() {
        return zCoordinate;
    }
    
    public static String getZCoordinate() {
        return zCoordinateProperty().get();
    }
    
    public static void setZCoordinate(String zCoordinate) {
        zCoordinateProperty().set(zCoordinate);
    }
}
