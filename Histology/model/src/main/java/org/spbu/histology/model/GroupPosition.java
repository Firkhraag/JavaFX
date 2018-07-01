package org.spbu.histology.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class GroupPosition {
    
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
