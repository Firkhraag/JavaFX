package org.spbu.histology.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.paint.Color;

public class CylinderShape extends Shape {
    
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
    
    private StringProperty radius = new SimpleStringProperty(this, "radius");
    public final StringProperty radiusProperty() {
        return this.radius;
    }
    
    public String getRadius() {
        return this.radiusProperty().get();
    }
    
    public void setRadius(String radius) {
        this.radiusProperty().set(radius);
    }
    
    public CylinderShape(String name, String type, String xRot, String yRot,
            String zRot, String x, String y, String z, Color diffCol,
            Color specCol, String height, String radius) {
        super(name, type, xRot, yRot, zRot, x, y, z, diffCol, specCol);
        this.height.set(height);
        this.radius.set(radius);
    }
    
    public CylinderShape(Long id, String name, String type, String xRot, String yRot,
            String zRot, String x, String y, String z, Color diffCol,
            Color specCol, String height, String radius) {
        super(id, name, type, xRot, yRot, zRot, x, y, z, diffCol, specCol);
        this.height.set(height);
        this.radius.set(radius);
    }
    
}
