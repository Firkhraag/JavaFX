package org.spbu.histology.shape.manager.impl;

import org.spbu.histology.model.Shape;
import org.spbu.histology.model.ShapeManager;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import javafx.beans.InvalidationListener;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;
import org.openide.util.lookup.ServiceProvider;

@ServiceProvider(service = ShapeManager.class)
public class ShapeManagerImpl implements ShapeManager {

    private final ObservableMap<Long, Shape> observableMap = 
            FXCollections.observableMap(new ConcurrentHashMap<Long, Shape>());
    
    @Override
    public void addListener(MapChangeListener<? super Long, ? super Shape> m1) {
        observableMap.addListener(m1);
    }

    @Override
    public void removeListener(MapChangeListener<? super Long, ? super Shape> m1) {
        observableMap.removeListener(m1);
    }

    @Override
    public void addListener(InvalidationListener i1) {
        observableMap.addListener(i1);
    }

    @Override
    public void removeListener(InvalidationListener i1) {
        observableMap.removeListener(i1);
    }

    @Override
    public void addShape(Shape s) {
        observableMap.put(s.getId(), new Shape(s.getId(), s));
    }

    @Override
    public void updateShape(Shape s, Long shapeId) {
        observableMap.put(shapeId, new Shape(shapeId, s));
    }

    @Override
    public void deleteShape(Shape s) {
        observableMap.remove(s.getId());
    }

    @Override
    public List<Shape> getAllShapes() {
        List<Shape> copyList = new ArrayList<>();
        observableMap.values().stream().forEach(s ->
                copyList.add(s));
        return copyList;
    }
    
}
