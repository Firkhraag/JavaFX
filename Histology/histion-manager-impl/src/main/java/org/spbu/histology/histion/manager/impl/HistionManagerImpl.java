package org.spbu.histology.histion.manager.impl;

import org.spbu.histology.model.Histion;
import org.spbu.histology.model.HistionManager;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import javafx.beans.InvalidationListener;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;
import javafx.scene.shape.MeshView;
import org.openide.util.lookup.ServiceProvider;

@ServiceProvider(service = HistionManager.class)
public class HistionManagerImpl implements HistionManager {

    private final ObservableMap<Integer, Histion> observableMap
            = FXCollections.observableMap(new ConcurrentHashMap<>());

    private final ObservableMap<Integer, MeshView> shapeMap
            = FXCollections.observableMap(new ConcurrentHashMap());

    public ObservableMap<Integer, MeshView> getShapeMap() {
        return shapeMap;
    }

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
        observableMap.put(h.getId(), h);
    }

    @Override
    public void updateHistion(Histion h, Integer histionId) {
        observableMap.put(histionId, h);
    }

    @Override
    public void deleteHistion(Integer id) {
        observableMap.remove(id);

    }

    @Override
    public List<Histion> getAllHistions() {
        List<Histion> copyList = new ArrayList<>();
        observableMap.values().stream().forEach(s
                -> copyList.add(s));
        return copyList;
    }

    @Override
    public ObservableMap<Integer, Histion> getHistionMap() {
        return observableMap;
    }

}
