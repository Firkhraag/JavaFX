package org.spbu.histology.model;

import java.util.concurrent.ConcurrentHashMap;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

public abstract class HistologyObject<T extends HistologyObject<?>> {

    public HistologyObject(Long id, String name, double xRot, double yRot, 
            double xPos, double yPos, double zPos) {
        setName(name);
        this.id = id;
        setXRotate(xRot);
        setYRotate(yRot);
        setXCoordinate(xPos);
        setYCoordinate(yPos);
        setZCoordinate(zPos);
    }

    private final StringProperty name = new SimpleStringProperty();

    public final StringProperty nameProperty() {
        return this.name;
    }


    public final String getName() {
        return this.nameProperty().get();
    }


    public final void setName(final String name) {
        this.nameProperty().set(name);
    }

    private Long id;
    
    public Long getId() {
        return this.id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    private final DoubleProperty xRotate = new SimpleDoubleProperty(0);
    public final DoubleProperty xRotateProperty() {
        return this.xRotate;
    }
    
    public final double getXRotate() {
        return this.xRotateProperty().get();
    }
    
    public final void setXRotate(double xRotate) {
        this.xRotateProperty().set(xRotate);
    }
    
    private final DoubleProperty yRotate = new SimpleDoubleProperty(0);
    public final DoubleProperty yRotateProperty() {
        return this.yRotate;
    }
    
    public final double getYRotate() {
        return this.yRotateProperty().get();
    }
    
    public final void setYRotate(double yRotate) {
        this.yRotateProperty().set(yRotate);
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
    
    private final ObservableMap<Long, T> itemMap = 
            FXCollections.observableMap(new ConcurrentHashMap());

    public ObservableMap<Long, T> getItemMap() {
        return itemMap;
    }
    
    public ObservableList<T> getItems() {
        ObservableList<T> copyList = FXCollections.observableArrayList();
        itemMap.values().stream().forEach(s ->
            copyList.add(s));
        return copyList;
    }
    
    public abstract void addChild(T obj);
    public abstract void deleteChild(long id);
}
