package org.spbu.histology.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

public class Part extends HistologyObject<HistologyObject<?>> {

    private static Integer count = 0;
    private ObservableList<TetgenPoint> pointData = FXCollections.observableArrayList();

    public Part(String name, ObservableList<TetgenPoint> pointData, Integer histionId, Integer cellId) {
        super(count++, name);
        this.pointData = FXCollections.observableArrayList(pointData);
        this.histionId = histionId;
        this.cellId = cellId;
    }
    
    public Part(Integer id, String name, ObservableList<TetgenPoint> pointData, Integer histionId, Integer cellId) {
        super(id, name);
        this.pointData = FXCollections.observableArrayList(pointData);
        this.histionId = histionId;
        this.cellId = cellId;
    }
    
    public Part(Part p, Integer histionId, Integer cellId) {
        super(count++, p.getName());
        this.pointData = FXCollections.observableArrayList(p.getPointData());
        this.histionId = histionId;
        this.cellId = cellId;
    }
    
    public Part(Integer id, Part p) {
        super(id, p.getName());
        this.pointData = FXCollections.observableArrayList(p.getPointData());
        this.histionId = p.getHistionId();
        this.cellId = p.getCellId();
    }
    
    public ObservableList<TetgenPoint> getPointData() {
        return pointData;
    }
    
    public void setPointData(ObservableList<TetgenPoint> pointData) {
        this.pointData = FXCollections.observableArrayList(pointData);
    }
    
    private Integer histionId;
    
    public Integer getHistionId() {
        return this.histionId;
    }
    
    public void setHistionId(Integer histionId) {
        this.histionId = histionId;
    }
    
    private Integer cellId;
    
    public Integer getCellId() {
        return this.cellId;
    }
    
    public void setCellId(Integer cellId) {
        this.cellId = cellId;
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