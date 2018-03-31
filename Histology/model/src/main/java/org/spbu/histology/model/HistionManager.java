package org.spbu.histology.model;

import java.util.List;
import javafx.beans.InvalidationListener;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;

public interface HistionManager {
    
    public void addListener(MapChangeListener<? super Long, ? super Histion> m1);
    
    public void removeListener(MapChangeListener<? super Long, ? super Histion> m1);
    
    public void addListener(InvalidationListener i1);
    
    public void removeListener(InvalidationListener i1);
    
    public void addHistion(Histion h);
    
    public void updateHistion(Histion h, Long histionId);
    
    public void deleteHistion(long id);
    
    public List<Histion> getAllHistions();
    
    public ObservableMap<Long, Histion> getHistionMap();
    
}
