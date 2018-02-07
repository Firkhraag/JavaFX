package org.spbu.histology.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.paint.Color;

public class BoxShape extends Shape {
    
    private StringProperty length = new SimpleStringProperty(this, "length");
    public final StringProperty lengthProperty() {
        return this.length;
    }
    
    public String getLength() {
        return this.lengthProperty().get();
    }
    
    public void setLength(String length) {
        this.lengthProperty().set(length);
    }
    
    private StringProperty width = new SimpleStringProperty(this, "width");
    public final StringProperty widthProperty() {
        return this.width;
    }
    
    public String getWidth() {
        return this.widthProperty().get();
    }
    
    public void setWidth(String width) {
        this.widthProperty().set(width);
    }
    
    private StringProperty height = new SimpleStringProperty(this, "height");
    public final StringProperty heightProperty() {
        return this.height;
    }
    
    public String getHeight() {
        return this.heightProperty().get();
    }
    
    public void setHeight(String height) {
        this.heightProperty().set(height);
    }
    
    public BoxShape(String name, String type, String xRot, String yRot,
            String zRot, String x, String y, String z, Color diffCol,
            Color specCol, String length, String width, String height) {
        super(name, type, xRot, yRot, zRot, x, y, z, diffCol, specCol);
        this.length.set(length);
        this.width.set(width);
        this.height.set(height);
    }
    
    public BoxShape(Long id, String name, String type, String xRot, String yRot,
            String zRot, String x, String y, String z, Color diffCol,
            Color specCol, String length, String width, String height) {
        super(id, name, type, xRot, yRot, zRot, x, y, z, diffCol, specCol);
        this.length.set(length);
        this.width.set(width);
        this.height.set(height);
    }
    
}
