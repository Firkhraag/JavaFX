package org.spbu.histology.model;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class Histion extends HistologyObject<Cell> {

    private static int count = 0;
    private Node nodeAvg = new Node(0, 0, 0);

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
    
    public Node getPointAvg() {
        return nodeAvg;
    }

    @Override
    public void addChild(Cell c) {
        getItemMap().put(c.getId(), c);
        if (getId() == 0) {
            nodeAvg = new Node(0, 0, 0);
            final IntegerProperty num = new SimpleIntegerProperty(0);
            getItems().forEach(cell -> {
                cell.getTransformedPointData().forEach(p -> {
                    nodeAvg.x += p.getX();
                    nodeAvg.y += p.getY();
                    nodeAvg.z += p.getZ();
                });
                num.set(num.get() + cell.getTransformedPointData().size());
            });
            nodeAvg.x /= num.get();
            nodeAvg.y /= num.get();
            nodeAvg.z /= num.get();
        }
    }

    @Override
    public void deleteChild(Integer id) {
        getItemMap().remove(id);
    }
}
