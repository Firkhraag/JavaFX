/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.paint.Color;

/**
 *
 * @author Sigl
 */
public class SphereShape extends Shape {
    
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
    
    public SphereShape(String name, String type, String xRot, String yRot,
            String zRot, String x, String y, String z, Color diffCol,
            Color specCol, String radius) {
        super(name, type, xRot, yRot, zRot, x, y, z, diffCol, specCol);
        this.radius.set(radius);
    }
    
    public SphereShape(Long id, String name, String type, String xRot, String yRot,
            String zRot, String x, String y, String z, Color diffCol,
            Color specCol, String radius) {
        super(id, name, type, xRot, yRot, zRot, x, y, z, diffCol, specCol);
        this.radius.set(radius);
    }
    
}
