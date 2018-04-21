package org.spbu.histology.model;

import java.util.ArrayList;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;

public class Cell extends HistologyObject<Part> {
    
    private final DoubleProperty xRotate = new SimpleDoubleProperty(0);
    public final DoubleProperty xRotateProperty() {
        return this.xRotate;
    }
    
    public double getXRotate() {
        return this.xRotateProperty().get();
    }
    
    public void setXRotate(double xRotate) {
        this.xRotateProperty().set(xRotate);
    }
    
    private final DoubleProperty yRotate = new SimpleDoubleProperty(0);
    public final DoubleProperty yRotateProperty() {
        return this.yRotate;
    }
    
    public double getYRotate() {
        return this.yRotateProperty().get();
    }
    
    public void setYRotate(double yRotate) {
        this.yRotateProperty().set(yRotate);
    }
    
    private final DoubleProperty xCoordinate = new SimpleDoubleProperty(0);
    public final DoubleProperty xCoordinateProperty() {
        return this.xCoordinate;
    }
    
    public double getXCoordinate() {
        return this.xCoordinateProperty().get();
    }
    
    public void setXCoordinate(double xCoordinate) {
        this.xCoordinateProperty().set(xCoordinate);
    }
    
    private final DoubleProperty yCoordinate = new SimpleDoubleProperty(0);
    public final DoubleProperty yCoordinateProperty() {
        return this.yCoordinate;
    }
    
    public double getYCoordinate() {
        return this.yCoordinateProperty().get();
    }
    
    public void setYCoordinate(double yCoordinate) {
        this.yCoordinateProperty().set(yCoordinate);
    }
    
    private final DoubleProperty zCoordinate = new SimpleDoubleProperty(0);
    public final DoubleProperty zCoordinateProperty() {
        return this.zCoordinate;
    }
    
    public double getZCoordinate() {
        return this.zCoordinateProperty().get();
    }
    
    public void setZCoordinate(double zCoordinate) {
        this.zCoordinateProperty().set(zCoordinate);
    }
    
    private ObservableList<ArrayList<Integer>> facetData = FXCollections.observableArrayList();
     
    public ObservableList<ArrayList<Integer>> getFacetData() {
        return facetData;
    }
    
    public void setFacetData(ObservableList<ArrayList<Integer>> facetData) {
        this.facetData = FXCollections.observableArrayList(facetData);
    }
    
    private final ObjectProperty<Color> diffuseColor = new SimpleObjectProperty(this, "diffuseColor");
    public final ObjectProperty<Color> diffuseColorProperty() {
        return this.diffuseColor;
    }
    
    public Color getDiffuseColor() {
        return this.diffuseColorProperty().get();
    }
    
    public void setDiffuseColor(Color diffuseColor) {
        this.diffuseColorProperty().set(diffuseColor);
    }
    
    private final ObjectProperty<Color> specularColor = new SimpleObjectProperty(this, "specularColor");
    public final ObjectProperty<Color> specularColorProperty() {
        return this.specularColor;
    }
    
    public Color getSpecularColor() {
        return this.specularColorProperty().get();
    }
    
    public void setSpecularColor(Color specularColor) {
        this.specularColorProperty().set(specularColor);
    }
    
    private Integer histionId;
    
    public Integer getHistionId() {
        return this.histionId;
    }
    
    public void setHistionId(Integer histionId) {
        this.histionId = histionId;
    }
    
    private boolean show;
    
    public boolean getShow() {
        return this.show;
    }
    
    public void setShow(boolean show) {
        this.show = show;
    }
        
    private ObservableList<TetgenPoint> transformedPointData = FXCollections.observableArrayList();
    
    public ObservableList<TetgenPoint> getTransformedPointData() {
        return transformedPointData;
    }
    
    public void setTransformedPointData(ObservableList<TetgenPoint> transformedPointData) {
        this.transformedPointData = FXCollections.observableArrayList(transformedPointData);
    }
    
    private static Integer count = 0;
    
    public Cell(String name, double xRot, double yRot, double x, double y, double z,
            ObservableList<ArrayList<Integer>> facetData, //int maxNumberOfPoints, 
            Color diffCol, Color specCol, Integer histionId, boolean show) {  
        super(count++, name);
        this.xRotate.set(xRot);
        this.yRotate.set(yRot);
        this.xCoordinate.set(x);
        this.yCoordinate.set(y);
        this.zCoordinate.set(z);
        this.facetData = FXCollections.observableArrayList(facetData);
        this.diffuseColor.set(diffCol);
        this.specularColor.set(specCol);
        this.histionId = histionId;
        this.show = show;
    }
    
    public Cell(Integer id, String name, double xRot, double yRot, double x, double y, double z,
            ObservableList<ArrayList<Integer>> facetData, Color diffCol,
            Color specCol, Integer histionId, boolean show) {  
        super(id, name);
        this.xRotate.set(xRot);
        this.yRotate.set(yRot);
        this.xCoordinate.set(x);
        this.yCoordinate.set(y);
        this.zCoordinate.set(z);
        this.facetData = FXCollections.observableArrayList(facetData);
        this.diffuseColor.set(diffCol);
        this.specularColor.set(specCol);
        this.histionId = histionId;
        this.show = show;
    }
    
    public Cell(Integer id, Cell c) { 
        super(id, c.getName());
        this.xRotate.set(c.getXRotate());
        this.yRotate.set(c.getYRotate());
        this.xCoordinate.set(c.getXCoordinate());
        this.yCoordinate.set(c.getYCoordinate());
        this.zCoordinate.set(c.getZCoordinate());
        this.facetData = FXCollections.observableArrayList(c.getFacetData());
        this.diffuseColor.set(c.getDiffuseColor());
        this.specularColor.set(c.getSpecularColor());
        this.histionId = c.getHistionId();
        this.show = c.getShow();
    }
    
    public Cell(Cell c, Integer histionId) { 
        super(count++, c.getName());
        this.xRotate.set(c.getXRotate());
        this.yRotate.set(c.getYRotate());
        this.xCoordinate.set(c.getXCoordinate());
        this.yCoordinate.set(c.getYCoordinate());
        this.zCoordinate.set(c.getZCoordinate());
        this.facetData = FXCollections.observableArrayList(c.getFacetData());
        this.diffuseColor.set(c.getDiffuseColor());
        this.specularColor.set(c.getSpecularColor());
        this.histionId = histionId;
        this.show = c.getShow();
        this.transformedPointData = FXCollections.observableArrayList(c.getTransformedPointData());
    }
    
    @Override
    public void addChild(Part p) {
        getItemMap().put(p.getId(), p);
    }
    
    @Override
    public void deleteChild(Integer id) {
        getItemMap().remove(id);
    }
}
