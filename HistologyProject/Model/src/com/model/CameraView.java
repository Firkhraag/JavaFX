package com.model;

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
    
    //public CameraView(String xRotate, String yRotate, String zRotate,
    //        String xCoordinate, String yCoordinate, String zCoordinate,
    //        String FOV, String nearClip, String farClip) {
        /*switch(toolNum) {
            case 1:
                this.xRotate.set(par1);
                this.yRotate.set(par2);
                this.zRotate.set(par3); 
                break;
            case 2:
                this.xCoordinate.set(par1);
                this.yCoordinate.set(par2);
                this.zCoordinate.set(par3); 
                break;
            case 3:
                this.FOV.set(par1);
                this.nearClip.set(par2);
                this.farClip.set(par3); 
                break;
        }*/
    
    
        /*this.xRotate.set(xRotate);
        this.yRotate.set(yRotate);
        this.zRotate.set(zRotate); 
        this.xCoordinate.set(xCoordinate);
        this.yCoordinate.set(yCoordinate);
        this.zCoordinate.set(zCoordinate);
        this.FOV.set(FOV);
        this.nearClip.set(nearClip);
        this.farClip.set(farClip);
    }*/
    
    /*public Shape(String name) {       
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
    }*/
    
}
