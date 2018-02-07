package org.spbu.histology.model;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
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
    
    private StringProperty type = new SimpleStringProperty(this, "type");
    public final StringProperty typeProperty() {
        return this.type;
    }
    
    public String getType() {
        return this.typeProperty().get();
    }
    
    public void setType(String type) {
        this.typeProperty().set(type);
    }
    
    private StringProperty xRotate = new SimpleStringProperty(this, "xRotate");
    public final StringProperty xRotateProperty() {
        return this.xRotate;
    }
    
    public String getXRotate() {
        return this.xRotateProperty().get();
    }
    
    public void setXRotate(String xRotate) {
        this.xRotateProperty().set(xRotate);
    }
    
    private StringProperty yRotate = new SimpleStringProperty(this, "yRotate");
    public final StringProperty yRotateProperty() {
        return this.yRotate;
    }
    
    public String getYRotate() {
        return this.yRotateProperty().get();
    }
    
    public void setYRotate(String yRotate) {
        this.yRotateProperty().set(yRotate);
    }
    
    private StringProperty zRotate = new SimpleStringProperty(this, "zRotate");
    public final StringProperty zRotateProperty() {
        return this.zRotate;
    }
    
    public String getZRotate() {
        return this.zRotateProperty().get();
    }
    
    public void setZRotate(String zRotate) {
        this.zRotateProperty().set(zRotate);
    }
    
    private StringProperty xCoordinate = new SimpleStringProperty(this, "x");
    public final StringProperty xCoordinateProperty() {
        return this.xCoordinate;
    }
    
    public String getXCoordinate() {
        return this.xCoordinateProperty().get();
    }
    
    public void setXCoordinate(String xCoordinate) {
        this.xCoordinateProperty().set(xCoordinate);
    }
    
    private StringProperty yCoordinate = new SimpleStringProperty(this, "y");
    public final StringProperty yCoordinateProperty() {
        return this.yCoordinate;
    }
    
    public String getYCoordinate() {
        return this.yCoordinateProperty().get();
    }
    
    public void setYCoordinate(String yCoordinate) {
        this.yCoordinateProperty().set(yCoordinate);
    }
    
    private StringProperty zCoordinate = new SimpleStringProperty(this, "z");
    public final StringProperty zCoordinateProperty() {
        return this.zCoordinate;
    }
    
    public String getZCoordinate() {
        return this.zCoordinateProperty().get();
    }
    
    public void setZCoordinate(String zCoordinate) {
        this.zCoordinateProperty().set(zCoordinate);
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
    
    /*public Shape(String name) { 
        this.id = count++;
        this.name = name;
        this.xCoordinate.set("0");
        this.yCoordinate.set("0");
        this.zCoordinate.set("0");        
    }*/
    
    public Shape(String name, String type, String xRot, String yRot, String zRot, String x, String y, String z, Color diffCol, Color specCol) {  
        this.id = count++;
        this.name.set(name);
        this.type.set(type);
        this.xRotate.set(xRot);
        this.yRotate.set(yRot);
        this.zRotate.set(zRot);
        this.xCoordinate.set(x);
        this.yCoordinate.set(y);
        this.zCoordinate.set(z); 
        this.diffuseColor.set(diffCol);
        this.specularColor.set(specCol);
    }
    
    public Shape(Long id, String name, String type, String xRot, String yRot, String zRot, String x, String y, String z, Color diffCol, Color specCol) {  
        this.id = id;
        this.name.set(name);
        this.type.set(type);
        this.xRotate.set(xRot);
        this.yRotate.set(yRot);
        this.zRotate.set(zRot);
        this.xCoordinate.set(x);
        this.yCoordinate.set(y);
        this.zCoordinate.set(z); 
        this.diffuseColor.set(diffCol);
        this.specularColor.set(specCol);
    }
    
    public Shape(Shape shape) { 
        this.id = shape.getId();
        this.name.set(shape.getName());
        this.type.set(shape.getType());
        this.xRotate.set(shape.getXRotate());
        this.yRotate.set(shape.getYRotate());
        this.zRotate.set(shape.getZRotate());
        this.xCoordinate.set(shape.getXCoordinate());
        this.yCoordinate.set(shape.getYCoordinate());
        this.zCoordinate.set(shape.getZCoordinate());   
        this.diffuseColor.set(shape.getDiffuseColor());
        this.specularColor.set(shape.getSpecularColor());
    }
    
}
