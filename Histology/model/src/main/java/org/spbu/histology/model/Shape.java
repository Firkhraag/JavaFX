package org.spbu.histology.model;

import java.util.Arrays;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;

public class Shape {
    
    private Long id;
    
    public Long getId() {
        return this.id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    private StringProperty name = new SimpleStringProperty(this, "name");
    public final StringProperty nameProperty() {
        return this.name;
    }
    
    public String getName() {
        return this.nameProperty().get();
    }
    
    public void setName(String name) {
        this.nameProperty().set(name);
    }
    
    private int facetNumber;
    
    public int getFacetNumber() {
        return facetNumber;
    }
    
    public void setFacetNumber(int facetNumber) {
        this.facetNumber = facetNumber;
    }
    
    private int maxNumberOfPoints;
    
    public int getMaxNumberOfPoints() {
        return maxNumberOfPoints;
    }
    
    public void setMaxNumberOfPoints(int maxNumberOfPoints) {
        this.maxNumberOfPoints = maxNumberOfPoints;
    }

    private ObservableList<TetgenPoint> pointData = FXCollections.observableArrayList();
    private ObservableList<TetgenPoint> holeData = FXCollections.observableArrayList();
    private ObservableList<TetgenFacetPolygon> polygonsInFacetData = FXCollections.observableArrayList();
    private ObservableList<TetgenFacetHole> holesInFacetData = FXCollections.observableArrayList();
    
    public ObservableList<TetgenPoint> getPointData() {
        return pointData;
    }
    
     public ObservableList<TetgenPoint> getHoleData() {
        return holeData;
    }
     
      public ObservableList<TetgenFacetPolygon> getPolygonsInFacetData() {
        return polygonsInFacetData;
    }
      
       public ObservableList<TetgenFacetHole> getHolesInFacetData() {
        return holesInFacetData;
    }
    
    private ObjectProperty<Color> diffuseColor = new SimpleObjectProperty(this, "diffuseColor");
    public final ObjectProperty<Color> diffuseColorProperty() {
        return this.diffuseColor;
    }
    
    public Color getDiffuseColor() {
        return this.diffuseColorProperty().get();
    }
    
    public void setDiffuseColor(Color diffuseColor) {
        this.diffuseColorProperty().set(diffuseColor);
    }
    
    private ObjectProperty<Color> specularColor = new SimpleObjectProperty(this, "specularColor");
    public final ObjectProperty<Color> specularColorProperty() {
        return this.specularColor;
    }
    
    public Color getSpecularColor() {
        return this.specularColorProperty().get();
    }
    
    public void setSpecularColor(Color specularColor) {
        this.specularColorProperty().set(specularColor);
    }
    
    private static long count = 0;
    
    public Shape(String name, ObservableList<TetgenPoint> pointData, ObservableList<TetgenPoint> holeData,
            ObservableList<TetgenFacetPolygon> polygonsInFacetData, ObservableList<TetgenFacetHole> holesInFacetData,
            int facetNumber, int maxNumberOfPoints,  Color diffCol, Color specCol) {  
        this.id = count++;
        this.name.set(name);
        this.pointData = pointData;
        this.holeData = holeData;
        this.polygonsInFacetData = polygonsInFacetData;
        this.holesInFacetData = holesInFacetData;
        this.facetNumber = facetNumber;
        this.maxNumberOfPoints = maxNumberOfPoints;
        this.diffuseColor.set(diffCol);
        this.specularColor.set(specCol);
    }
    
    public Shape(Long id, String name, ObservableList<TetgenPoint> pointData, ObservableList<TetgenPoint> holeData,
            ObservableList<TetgenFacetPolygon> polygonsInFacetData, ObservableList<TetgenFacetHole> holesInFacetData,
            int facetNumber, int maxNumberOfPoints,  Color diffCol, Color specCol) {  
        this.id = id;
        this.name.set(name);
        this.pointData = pointData;
        this.holeData = holeData;
        this.polygonsInFacetData = polygonsInFacetData;
        this.holesInFacetData = holesInFacetData;
        this.facetNumber = facetNumber;
        this.maxNumberOfPoints = maxNumberOfPoints;
        this.diffuseColor.set(diffCol);
        this.specularColor.set(specCol);
    }
    
    public Shape(Shape shape) { 
        this.id = shape.getId();
        this.name.set(shape.getName()); 
        this.pointData = shape.getPointData();
        this.holeData = shape.getHoleData();
        this.polygonsInFacetData = shape.getPolygonsInFacetData();
        this.holesInFacetData = shape.getHolesInFacetData();
        this.facetNumber = shape.getFacetNumber();
        this.maxNumberOfPoints = shape.getMaxNumberOfPoints();
        this.diffuseColor.set(shape.getDiffuseColor());
        this.specularColor.set(shape.getSpecularColor());
    }
    
}
