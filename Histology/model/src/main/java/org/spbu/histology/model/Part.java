package org.spbu.histology.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

public class Part extends HistologyObject<HistologyObject<?>> {

    private static long count = 0;

    public Part(String name, double xRot, double yRot, double xPos, double yPos,
            double zPos) {
        super(count++, name, xRot, yRot, xPos, yPos, zPos);
    }
    
    public Part(Long id, String name, double xRot, double yRot, double xPos, 
            double yPos, double zPos) {
        super(id, name, xRot, yRot, xPos, yPos, zPos);
    }
    
    public Part(Part p) {
        super(count++, p.getName(), p.getXRotate(), p.getYRotate(), 
                p.getXCoordinate(), p.getYCoordinate(), p.getZCoordinate());
    }
    
    public Part(Long id, Part p) {
        super(id, p.getName(), p.getXRotate(), p.getYRotate(), 
                p.getXCoordinate(), p.getYCoordinate(), p.getZCoordinate());
    }
    
    @Override
    public ObservableMap<Long, HistologyObject<?>> getItemMap() {
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
    public void deleteChild(long id) {
        throw new IllegalStateException("Part has no child items");
    }
}