package com.model;

import java.util.List;
import javafx.beans.InvalidationListener;
import javafx.collections.MapChangeListener;

public interface ShapeManager {
    
    /*public boolean getAddPredefinedShapes();
    
    public void setAddPredefinedShapes(boolean a);*/
    
    public void addListener(MapChangeListener<? super Long, ? super Shape> m1);
    
    public void removeListener(MapChangeListener<? super Long, ? super Shape> m1);
    
    public void addListener(InvalidationListener i1);
    
    public void removeListener(InvalidationListener i1);
    
    public void addShape(Shape p);
    
    public void updateShape(Shape p, Long shapeId);
    
    public void deleteShape(Shape p);
    
    public List<Shape> getAllShapes();
    
}
