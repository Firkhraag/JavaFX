package org.spbu.histology.shape.manager.impl;

import org.spbu.histology.model.ShapeManager;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import javafx.beans.InvalidationListener;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;
import org.openide.util.lookup.ServiceProvider;
import org.spbu.histology.model.Cell;

@ServiceProvider(service = ShapeManager.class)
public class ShapeManagerImpl implements ShapeManager {

    private final ObservableMap<Long, Cell> observableMap = 
            FXCollections.observableMap(new ConcurrentHashMap<>());
    
    @Override
    public void addListener(MapChangeListener<? super Long, ? super Cell> m1) {
        observableMap.addListener(m1);
    }

    @Override
    public void removeListener(MapChangeListener<? super Long, ? super Cell> m1) {
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
    public void addShape(Cell s) {
        observableMap.put(s.getId(), new Cell(s.getId(), s));
    }

    @Override
    public void updateShape(Cell s, Long shapeId) {
        observableMap.put(shapeId, new Cell(shapeId, s));
    }
    
    @Override
    public void deleteShape(long id) {
        observableMap.remove(id);
    }

    @Override
    public List<Cell> getAllShapes() {
        List<Cell> copyList = new ArrayList<>();
        observableMap.values().stream().forEach(s ->
            copyList.add(s));
        return copyList;
    }
    
    @Override
    public ObservableMap<Long, Cell> getShapeMap() {
        return observableMap;
    }
    
}
