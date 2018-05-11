package org.spbu.histology.model;

import java.util.List;
import javafx.beans.InvalidationListener;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;
import javafx.scene.shape.MeshView;

public interface HistionManager {
    
    public void addListener(MapChangeListener<? super Integer, ? super Histion> m1);
    
    public void removeListener(MapChangeListener<? super Integer, ? super Histion> m1);
    
    public void addListener(InvalidationListener i1);
    
    public void removeListener(InvalidationListener i1);
    
    public void addHistion(Histion h);
    
    public void updateHistion(Histion h, Integer histionId);
    
    public void deleteHistion(Integer id);
    
    public List<Histion> getAllHistions();
    
    public ObservableMap<Integer, Histion> getHistionMap();
    
    public ObservableMap<Integer, MeshView> getShapeMap();
    
}
