package org.spbu.histology.model;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

public class Histion extends HistologyObject<Cell> {

    private static int count = 0;

    public Histion(String name, double xPos, double yPos, double zPos) {
        super(count++, name);
        this.xCoordinate.set(xPos);
        this.yCoordinate.set(yPos);
        this.zCoordinate.set(zPos);
    }

    public Histion(Integer id, String name, double xPos, double yPos, double zPos) {
        super(id, name);
        this.xCoordinate.set(xPos);
        this.yCoordinate.set(yPos);
        this.zCoordinate.set(zPos);
    }

    public Histion(Histion h) {
        super(count++, h.getName());
        this.xCoordinate.set(h.getXCoordinate());
        this.yCoordinate.set(h.getYCoordinate());
        this.zCoordinate.set(h.getZCoordinate());
    }

    public Histion(Integer id, Histion h) {
        super(id, h.getName());
        this.xCoordinate.set(h.getXCoordinate());
        this.yCoordinate.set(h.getYCoordinate());
        this.zCoordinate.set(h.getZCoordinate());
    }

    private final DoubleProperty xCoordinate = new SimpleDoubleProperty(0);

    public final DoubleProperty xCoordinateProperty() {
        return this.xCoordinate;
    }

    public final double getXCoordinate() {
        return this.xCoordinateProperty().get();
    }

    public final void setXCoordinate(double xCoordinate) {
        this.xCoordinateProperty().set(xCoordinate);
    }

    private final DoubleProperty yCoordinate = new SimpleDoubleProperty(0);

    public final DoubleProperty yCoordinateProperty() {
        return this.yCoordinate;
    }

    public final double getYCoordinate() {
        return this.yCoordinateProperty().get();
    }

    public final void setYCoordinate(double yCoordinate) {
        this.yCoordinateProperty().set(yCoordinate);
    }

    private final DoubleProperty zCoordinate = new SimpleDoubleProperty(0);

    public final DoubleProperty zCoordinateProperty() {
        return this.zCoordinate;
    }

    public final double getZCoordinate() {
        return this.zCoordinateProperty().get();
    }

    public final void setZCoordinate(double zCoordinate) {
        this.zCoordinateProperty().set(zCoordinate);
    }

    @Override
    public void addChild(Cell c) {
        getItemMap().put(c.getId(), c);
    }

    @Override
    public void deleteChild(Integer id) {
        getItemMap().remove(id);
    }
}
