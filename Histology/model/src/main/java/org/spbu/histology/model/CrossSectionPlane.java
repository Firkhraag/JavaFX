package org.spbu.histology.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class CrossSectionPlane {

    private static StringProperty xRotate = new SimpleStringProperty("0.0");

    public static final StringProperty xRotateProperty() {
        return xRotate;
    }

    public static String getXRotate() {
        return xRotateProperty().get();
    }

    public static void setXRotate(String xRotate) {
        xRotateProperty().set(xRotate);
        findPlane();
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
        findPlane();
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
        findPlane();
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
        findPlane();
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
        findPlane();
    }

    private static StringProperty opaqueness = new SimpleStringProperty("0.1");

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
        findPlane();
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

    public static BooleanProperty initialized = new SimpleBooleanProperty(false);
    
    /*private void findPlane(Node p1, Node p2, Node p3) {
        try {
            double A = ((p2.y - p1.y) * (p3.z - p1.z) - (p3.y - p1.y) * (p2.z - p1.z));
            double B = -((p2.x - p1.x) * (p3.z - p1.z) - (p3.x - p1.x) * (p2.z - p1.z));
            double C = ((p2.x - p1.x) * (p3.y - p1.y) - (p3.x - p1.x) * (p2.y - p1.y));
            double D = -p1.x * A - p1.y * B - p1.z * C;
            if ((Math.abs(A - CrossSectionPlane.getA()) > EPS) || (Math.abs(B - CrossSectionPlane.getB()) > EPS)
                    || (Math.abs(C - CrossSectionPlane.getC()) > EPS) || (Math.abs(D - CrossSectionPlane.getD()) > EPS)) {
                CrossSectionPlane.setA(A);
                CrossSectionPlane.setB(B);
                CrossSectionPlane.setC(C);
                CrossSectionPlane.setD(D);
                CrossSectionPlane.setChanged(true);
            }
        } catch (Exception ex) {

        }
    }*/

    static private void findPlane() {
        try {
            double temp;
            double ang;
            double xPos = Double.parseDouble(getXCoordinate());
            double yPos = Double.parseDouble(getYCoordinate());
            double zPos = Double.parseDouble(getZCoordinate());

            Node p1 = new Node(0, 0, 0);
            Node p2 = new Node(1, 0, 0);
            Node p3 = new Node(0, 0, 1);

            ang = Math.toRadians(Double.parseDouble(getXRotate()));
            temp = p1.y;
            p1.y = p1.y * Math.cos(ang) - p1.z * Math.sin(ang);
            p1.z = temp * Math.sin(ang) + p1.z * Math.cos(ang);
            temp = p2.y;
            p2.y = p2.y * Math.cos(ang) - p2.z * Math.sin(ang);
            p2.z = temp * Math.sin(ang) + p2.z * Math.cos(ang);
            temp = p3.y;
            p3.y = p3.y * Math.cos(ang) - p3.z * Math.sin(ang);
            p3.z = temp * Math.sin(ang) + p3.z * Math.cos(ang);

            ang = Math.toRadians(Double.parseDouble(getYRotate()));
            temp = p1.x;
            p1.x = p1.x * Math.cos(ang) + p1.z * Math.sin(ang);
            p1.z = -temp * Math.sin(ang) + p1.z * Math.cos(ang);
            temp = p2.x;
            p2.x = p2.x * Math.cos(ang) + p2.z * Math.sin(ang);
            p2.z = -temp * Math.sin(ang) + p2.z * Math.cos(ang);
            temp = p3.x;
            p3.x = p3.x * Math.cos(ang) + p3.z * Math.sin(ang);
            p3.z = -temp * Math.sin(ang) + p3.z * Math.cos(ang);

            p1.x += xPos;
            p2.x += xPos;
            p3.x += xPos;
            p1.y += yPos;
            p2.y += yPos;
            p3.y += yPos;
            p1.z += zPos;
            p2.z += zPos;
            p3.z += zPos;
            
            double A = ((p2.y - p1.y) * (p3.z - p1.z) - (p3.y - p1.y) * (p2.z - p1.z));
            double B = -((p2.x - p1.x) * (p3.z - p1.z) - (p3.x - p1.x) * (p2.z - p1.z));
            double C = ((p2.x - p1.x) * (p3.y - p1.y) - (p3.x - p1.x) * (p2.y - p1.y));
            double D = -p1.x * A - p1.y * B - p1.z * C;
            if ((Math.abs(A - getA()) > 0.00001) || (Math.abs(B - getB()) > 0.00001)
                    || (Math.abs(C - getC()) > 0.00001) || (Math.abs(D - getD()) > 0.00001)) {
                /*CrossSectionPlane.setA(A);
                CrossSectionPlane.setB(B);
                CrossSectionPlane.setC(C);
                CrossSectionPlane.setD(D);
                CrossSectionPlane.setChanged(true);*/
                setA(A);
                setB(B);
                setC(C);
                setD(D);
                setChanged(true);
            }

        } catch (Exception ex) {

        }
    }

}
