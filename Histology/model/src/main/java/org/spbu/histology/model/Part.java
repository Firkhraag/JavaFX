package org.spbu.histology.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

public class Part extends HistologyObject<HistologyObject<?>> {

    private static Integer count = 0;
    private final ObservableList<TetgenPoint> pointData = FXCollections.observableArrayList();

    public Part(String name, ObservableList<TetgenPoint> pointData) {
        super(count++, name, FXCollections.emptyObservableMap());
        for (int i = 0; i < pointData.size(); i++)
            this.pointData.add(new TetgenPoint(pointData.get(i)));
    }
    
    public Part(Integer id, String name, ObservableList<TetgenPoint> pointData) {
        super(id, name, FXCollections.emptyObservableMap());
        for (int i = 0; i < pointData.size(); i++)
            this.pointData.add(new TetgenPoint(pointData.get(i)));
    }
    
    public Part(Part p) {
        super(count++, p.getName(), FXCollections.emptyObservableMap());
        for (int i = 0; i < p.getPointData().size(); i++)
            this.pointData.add(new TetgenPoint(p.getPointData().get(i)));
    }
    
    public Part(Integer id, Part p) {
        super(id, p.getName(), FXCollections.emptyObservableMap());
        for (int i = 0; i < p.getPointData().size(); i++)
            this.pointData.add(new TetgenPoint(p.getPointData().get(i)));
    }
    
    public ObservableList<TetgenPoint> getPointData() {
        return pointData;
    }
    
    public void setPointData(ObservableList<TetgenPoint> pointData) {
        for (int i = 0; i < pointData.size(); i++)
            this.pointData.add(new TetgenPoint(pointData.get(i)));
    }
    
    @Override
    public ObservableMap<Integer, HistologyObject<?>> getItemMap() {
        return FXCollections.emptyObservableMap();
    }
    
    @Override
    public ObservableList<HistologyObject<?>> getItems() {
        return FXCollections.emptyObservableList();
    }
    
    @Override
    public void addChild(HistologyObject<?> obj) {
        throw new IllegalStateException("Part has no child items");
    }
    
    @Override
    public void deleteChild(Integer id) {
        throw new IllegalStateException("Part has no child items");
    }
}