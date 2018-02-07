package org.spbu.histology.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class CameraView {
    
    private static StringProperty xRotate = new SimpleStringProperty("xRotate");
    public static final StringProperty xRotateProperty() {
        return xRotate;
    }
    
    public static String getXRotate() {
        return xRotateProperty().get();
    }
    
    public static void setXRotate(String xRotate) {
        xRotateProperty().set(xRotate);
    }
    
    private static StringProperty yRotate = new SimpleStringProperty("yRotate");
    public static final StringProperty yRotateProperty() {
        return yRotate;
    }
    
    public static String getYRotate() {
        return yRotateProperty().get();
    }
    
    public static void setYRotate(String yRotate) {
        yRotateProperty().set(yRotate);
    }
    
    private static StringProperty zRotate = new SimpleStringProperty("zRotate");
    public static final StringProperty zRotateProperty() {
        return zRotate;
    }
    
    public static String getZRotate() {
        return zRotateProperty().get();
    }
    
    public static void setZRotate(String zRotate) {
        zRotateProperty().set(zRotate);
    }
    
    private static StringProperty xCoordinate = new SimpleStringProperty("xCoordinate");
    public static final StringProperty xCoordinateProperty() {
        return xCoordinate;
    }
    
    public static String getXCoordinate() {
        return xCoordinateProperty().get();
    }
    
    public static void setXCoordinate(String xCoordinate) {
        xCoordinateProperty().set(xCoordinate);
    }
    
    private static StringProperty yCoordinate = new SimpleStringProperty("yCoordinate");
    public static final StringProperty yCoordinateProperty() {
        return yCoordinate;
    }
    
    public static String getYCoordinate() {
        return yCoordinateProperty().get();
    }
    
    public static void setYCoordinate(String yCoordinate) {
        yCoordinateProperty().set(yCoordinate);
    }
    
    private static StringProperty zCoordinate = new SimpleStringProperty("zCoordinate");
    public static final StringProperty zCoordinateProperty() {
        return zCoordinate;
    }
    
    public static String getZCoordinate() {
        return zCoordinateProperty().get();
    }
    
    public static void setZCoordinate(String zCoordinate) {
        zCoordinateProperty().set(zCoordinate);
    }
    
    private static StringProperty FOV = new SimpleStringProperty("FOV");
    public static final StringProperty FOVProperty() {
        return FOV;
    }
    
    public static String getFOV() {
        return FOVProperty().get();
    }
    
    public static void setFOV(String FOV) {
        FOVProperty().set(FOV);
    }
    
    private static StringProperty nearClip = new SimpleStringProperty("nearClip");
    public static final StringProperty nearClipProperty() {
        return nearClip;
    }
    
    public static String getNearClip() {
        return nearClipProperty().get();
    }
    
    public static void setNearClip(String nearClip) {
        nearClipProperty().set(nearClip);
    }
    
    private static StringProperty farClip = new SimpleStringProperty("farClip");
    public static final StringProperty farClipProperty() {
        return farClip;
    }
    
    public static String getFarClip() {
        return farClipProperty().get();
    }
    
    public static void setFarClip(String farClip) {
        farClipProperty().set(farClip);
    }
    
    public static void setCamera(String xRotate, String yRotate, String zRotate,
            String xCoordinate, String yCoordinate, String zCoordinate,
            String FOV, String nearClip, String farClip) {
        xRotateProperty().set(xRotate);
        yRotateProperty().set(yRotate);
        zRotateProperty().set(zRotate); 
        xCoordinateProperty().set(xCoordinate);
        yCoordinateProperty().set(yCoordinate);
        zCoordinateProperty().set(zCoordinate);
        FOVProperty().set(FOV);
        nearClipProperty().set(nearClip);
        farClipProperty().set(farClip);
    }
    
}
