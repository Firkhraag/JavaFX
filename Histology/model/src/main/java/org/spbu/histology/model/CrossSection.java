package org.spbu.histology.model;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.scene.shape.Polygon;

public class CrossSection {
    
    private static StringProperty xRotate = new SimpleStringProperty("0.0");
    public static final StringProperty xRotateProperty() {
        return xRotate;
    }
    
    public static String getXRotate() {
        return xRotateProperty().get();
    }
    
    public static void setXRotate(String xRotate) {
        xRotateProperty().set(xRotate);
    }
    
    private static StringProperty yRotate = new SimpleStringProperty("0.0");
    public static final StringProperty yRotateProperty() {
        return yRotate;
    }
    
    public static String getYRotate() {
        return yRotateProperty().get();
    }
    
    public static void setYRotate(String yRotate) {
        yRotateProperty().set(yRotate);
    }
    
    private static StringProperty xCoordinate = new SimpleStringProperty("0.0");
    public static final StringProperty xCoordinateProperty() {
        return xCoordinate;
    }
    
    public static String getXCoordinate() {
        return xCoordinateProperty().get();
    }
    
    public static void setXCoordinate(String xCoordinate) {
        xCoordinateProperty().set(xCoordinate);
    }
    
    private static StringProperty yCoordinate = new SimpleStringProperty("0.0");
    public static final StringProperty yCoordinateProperty() {
        return yCoordinate;
    }
    
    public static String getYCoordinate() {
        return yCoordinateProperty().get();
    }
    
    public static void setYCoordinate(String yCoordinate) {
        yCoordinateProperty().set(yCoordinate);
    }
    
    private static StringProperty zCoordinate = new SimpleStringProperty("0.0");
    public static final StringProperty zCoordinateProperty() {
        return zCoordinate;
    }
    
    public static String getZCoordinate() {
        return zCoordinateProperty().get();
    }
    
    public static void setZCoordinate(String zCoordinate) {
        zCoordinateProperty().set(zCoordinate);
    }
    
    private static StringProperty opaqueness = new SimpleStringProperty("0");
    public static final StringProperty opaquenessProperty() {
        return opaqueness;
    }
    
    public static String getOpaqueness() {
        return opaquenessProperty().get();
    }
    
    public static void setOpaqueness(String opaqueness) {
        opaquenessProperty().set(opaqueness);
    }
    
    public static void setCrossSection(String xRotate, String yRotate, 
            String xCoordinate, String yCoordinate, String zCoordinate) {
        xRotateProperty().set(xRotate);
        yRotateProperty().set(yRotate);
        xCoordinateProperty().set(xCoordinate);
        yCoordinateProperty().set(yCoordinate);
        zCoordinateProperty().set(zCoordinate);
    }
    
    private static DoubleProperty A = new SimpleDoubleProperty(0.0);
    public static final DoubleProperty AProperty() {
        return A;
    }
    
    public static Double getA() {
        return AProperty().get();
    }
    
    public static void setA(Double A) {
        AProperty().set(A);
    }
    
    private static DoubleProperty B = new SimpleDoubleProperty(-1);
    public static final DoubleProperty BProperty() {
        return B;
    }
    
    public static Double getB() {
        return BProperty().get();
    }
    
    public static void setB(Double B) {
        BProperty().set(B);
    }
    
    private static DoubleProperty C = new SimpleDoubleProperty(0.0);
    public static final DoubleProperty CProperty() {
        return C;
    }
    
    public static Double getC() {
        return CProperty().get();
    }
    
    public static void setC(Double C) {
        CProperty().set(C);
    }
    
    private static DoubleProperty D = new SimpleDoubleProperty(0.0);
    public static final DoubleProperty DProperty() {
        return D;
    }
    
    public static Double getD() {
        return DProperty().get();
    }
    
    public static void setD(Double D) {
        DProperty().set(D);
    }
    
    private static BooleanProperty changed = new SimpleBooleanProperty(false);
    public static final BooleanProperty changedProperty() {
        return changed;
    }
    
    public static Boolean getChanged() {
        return changedProperty().get();
    }
    
    public static void setChanged(Boolean changed) {
        changedProperty().set(changed);
    }
    
    private static final ObservableMap<Integer, ArrayList<Polygon>> polygonMap = 
            FXCollections.observableMap(new ConcurrentHashMap());
    private static final ObservableMap<Integer, ArrayList<javafx.scene.shape.Line>> lineMap = 
            FXCollections.observableMap(new ConcurrentHashMap());
    
    public static ObservableMap<Integer, ArrayList<Polygon>> getPolygonMap() {
        return polygonMap;
    }
    
    public static void addPolygon(Integer id, ArrayList<Polygon> p) {
        polygonMap.put(id, p);
    }
    
    public static void removePolygon(Integer id) {
        polygonMap.remove(id);
    }
    
    public static ObservableMap<Integer, ArrayList<javafx.scene.shape.Line>> getLineMap() {
        return lineMap;
    }
    
    public static void addLine(Integer id, ArrayList<javafx.scene.shape.Line> l) {
        lineMap.put(id, l);
    }
    
    public static void removeLine(Integer id) {
        lineMap.remove(id);
    }
    
    public static BooleanProperty initialized = new SimpleBooleanProperty(false);

}
