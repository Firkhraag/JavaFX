package org.spbu.histology.model;

import javafx.beans.property.SimpleIntegerProperty;

public class TetgenPointNumber {

    private final SimpleIntegerProperty id;
    private final SimpleIntegerProperty pointNumber;

    public TetgenPointNumber(int i, int pointNumber) {
        this.id = new SimpleIntegerProperty(i);
        this.pointNumber = new SimpleIntegerProperty(pointNumber);
    }

    public int getId() {
        return id.get();
    }

    public void setId(int v) {
        id.set(v);
    }

    public int getPointNumber() {
        return pointNumber.get();
    }

    public void setPointNumber(int v) {
        pointNumber.set(v);
    }
}
