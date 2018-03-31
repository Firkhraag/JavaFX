package org.spbu.histology.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

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
    
    public static void setCamera(String xRotate, String yRotate, String xCoordinate, 
            String yCoordinate, String zCoordinate, String FOV) {
        xRotateProperty().set(xRotate);
        yRotateProperty().set(yRotate);
        xCoordinateProperty().set(xCoordinate);
        yCoordinateProperty().set(yCoordinate);
        zCoordinateProperty().set(zCoordinate);
        FOVProperty().set(FOV);
    }
    
    private static ObservableList<Long> shapeIdToHideList = FXCollections.observableArrayList();
    
    public static ObservableList<Long> getShapeIdToHideList() {
        return shapeIdToHideList;
    }
    
    public static void addShapeIdToHide(Long id) {
        shapeIdToHideList.add(id);
    }
    
    public static void removeShapeIdToHide(Long id) {
        shapeIdToHideList.remove(id);
    }
    
}
