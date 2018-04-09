package org.spbu.histology.histion.manager.impl;

import org.spbu.histology.model.Histion;
import org.spbu.histology.model.HistionManager;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import javafx.beans.InvalidationListener;
//import javafx.beans.property.BooleanProperty;
//import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;
import org.openide.util.lookup.ServiceProvider;

@ServiceProvider(service = HistionManager.class)
public class HistionManagerImpl implements HistionManager {

    private final ObservableMap<Integer, Histion> observableMap = 
            FXCollections.observableMap(new ConcurrentHashMap<>());
    /*private final BooleanProperty childListChanged = 
            new SimpleBooleanProperty(false);*/
    
    @Override
    public void addListener(MapChangeListener<? super Integer, ? super Histion> m1) {
        observableMap.addListener(m1);
    }

    @Override
    public void removeListener(MapChangeListener<? super Integer, ? super Histion> m1) {
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
    public void addHistion(Histion h) {
        observableMap.put(h.getId(), new Histion(h.getId(), h));
    }

    @Override
    public void updateHistion(Histion s, Integer histionId) {
        observableMap.put(histionId, new Histion(histionId, s));
    }
    
    @Override
    public void deleteHistion(Integer id) {
        //observableMap.get(id).getItemMap().clear();
        observableMap.remove(id);
        
    }

    @Override
    public List<Histion> getAllHistions() {
        List<Histion> copyList = new ArrayList<>();
        observableMap.values().stream().forEach(s ->
            copyList.add(s));
        return copyList;
    }
    
    @Override
    public ObservableMap<Integer, Histion> getHistionMap() {
        return observableMap;
    }
    
}
