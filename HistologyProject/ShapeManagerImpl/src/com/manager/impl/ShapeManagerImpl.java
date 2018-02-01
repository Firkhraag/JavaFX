package com.manager.impl;

import com.model.Shape;
import com.model.ShapeManager;
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
    
    /*private boolean addPredefinedShapes = true;
    
    @Override
    public void setAddPredefinedShapes(boolean a) {
        this.addPredefinedShapes = a;
    }
    
    @Override
    public boolean getAddPredefinedShapes() {
        return this.addPredefinedShapes;
    }*/

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
        //Shape shape = new Shape(s);
        //observableMap.put(shape.getId(), shape);
        observableMap.put(s.getId(), s);
    }

    @Override
    public void updateShape(Shape s, Long shapeId) {
        //Shape shape = new Shape(s);
        //observableMap.put(shape.getId(), shape);
        observableMap.put(shapeId, s);
    }

    @Override
    public void deleteShape(Shape s) {
        observableMap.remove(s.getId());
        //observableMap.remove(s.getId());
    }

    @Override
    public List<Shape> getAllShapes() {
        /*List<Shape> copyList = new ArrayList<>();
        observableMap.values().stream().forEach(s ->
                copyList.add(new Shape(s)));
        return copyList;*/
        List<Shape> copyList = new ArrayList<>();
        observableMap.values().stream().forEach(s ->
                copyList.add(s));
        /*if (copyList.get(0) instanceof BoxShape) {
            System.out.println("Buuuux");
        }*/
        return copyList;
    }
    
}
