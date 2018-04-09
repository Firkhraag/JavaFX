package org.spbu.histology.model;

import java.util.List;
import javafx.beans.InvalidationListener;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;

public interface ShapeManager {
    
    public void addListener(MapChangeListener<? super Long, ? super Cell> m1);
    
    public void removeListener(MapChangeListener<? super Long, ? super Cell> m1);
    
    public void addListener(InvalidationListener i1);
    
    public void removeListener(InvalidationListener i1);
    
    public void addShape(Cell s);
    
    public void updateShape(Cell s, Long shapeId);
    
    //public void deleteShape(Shape s);
    public void deleteShape(long id);
    
    public List<Cell> getAllShapes();
    
    public ObservableMap<Long, Cell> getShapeMap();
    
}
