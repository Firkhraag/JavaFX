package org.spbu.histology.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

public class Part extends HistologyObject<HistologyObject<?>> {

    private static Integer count = 0;
    private ObservableList<TetgenPoint> pointData = FXCollections.observableArrayList();

    public Part(String name, ObservableList<TetgenPoint> pointData, Integer cellId) {
        super(count++, name);
        this.pointData = FXCollections.observableArrayList(pointData);
        avgNode = new Node(0, 0, 0);
        for (TetgenPoint p : pointData) {
            avgNode.x += p.getX();
            avgNode.z += p.getZ();
        }
        avgNode.x /= pointData.size();
        avgNode.z /= pointData.size();

        this.cellId = cellId;
    }

    public Part(Integer id, String name, ObservableList<TetgenPoint> pointData, Integer cellId) {
        super(id, name);
        this.pointData = FXCollections.observableArrayList(pointData);
        avgNode = new Node(0, 0, 0);
        for (TetgenPoint p : pointData) {
            avgNode.x += p.getX();
            avgNode.z += p.getZ();
        }
        avgNode.x /= pointData.size();
        avgNode.z /= pointData.size();
        this.cellId = cellId;
    }

    public Part(Part p, Integer cellId) {
        super(count++, p.getName());
        this.pointData = FXCollections.observableArrayList(p.getPointData());
        avgNode = new Node(0, 0, 0);
        for (TetgenPoint point : p.getPointData()) {
            avgNode.x += point.getX();
            avgNode.z += point.getZ();
        }
        avgNode.x /= pointData.size();
        avgNode.z /= pointData.size();
        this.cellId = cellId;
    }

    public Part(Integer id, Part p) {
        super(id, p.getName());
        this.pointData = FXCollections.observableArrayList(p.getPointData());
        avgNode = new Node(0, 0, 0);
        for (TetgenPoint point : p.getPointData()) {
            avgNode.x += point.getX();
            avgNode.z += point.getZ();
        }
        avgNode.x /= pointData.size();
        avgNode.z /= pointData.size();
        this.cellId = p.getCellId();
    }

    public ObservableList<TetgenPoint> getPointData() {
        return pointData;
    }

    public void setPointData(ObservableList<TetgenPoint> pointData) {
        this.pointData = FXCollections.observableArrayList(pointData);
    }

    private Integer cellId;

    public Integer getCellId() {
        return this.cellId;
    }

    public void setCellId(Integer cellId) {
        this.cellId = cellId;
    }

    private Node avgNode;

    public Node getAvgNode() {
        return this.avgNode;
    }

    public void setAvgNode() {
        avgNode = new Node(0, 0, 0);
        for (TetgenPoint p : pointData) {
            avgNode.x += p.getX();
            avgNode.z += p.getZ();
        }
        avgNode.x /= pointData.size();
        avgNode.z /= pointData.size();
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
