package org.spbu.histology.model;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.geometry.Point3D;
import javafx.scene.paint.Color;

public class Cell extends HistologyObject<Part> {
    
    private final DoubleProperty xRotate = new SimpleDoubleProperty(0);
    public final DoubleProperty xRotateProperty() {
        return this.xRotate;
    }
    
    public double getXRotate() {
        return this.xRotateProperty().get();
    }
    
    public void setXRotate(double xRotate) {
        this.xRotateProperty().set(xRotate);
    }
    
    private final DoubleProperty yRotate = new SimpleDoubleProperty(0);
    public final DoubleProperty yRotateProperty() {
        return this.yRotate;
    }
    
    public double getYRotate() {
        return this.yRotateProperty().get();
    }
    
    public void setYRotate(double yRotate) {
        this.yRotateProperty().set(yRotate);
    }
    
    private final DoubleProperty xCoordinate = new SimpleDoubleProperty(0);
    public final DoubleProperty xCoordinateProperty() {
        return this.xCoordinate;
    }
    
    public double getXCoordinate() {
        return this.xCoordinateProperty().get();
    }
    
    public void setXCoordinate(double xCoordinate) {
        this.xCoordinateProperty().set(xCoordinate);
    }
    
    private final DoubleProperty yCoordinate = new SimpleDoubleProperty(0);
    public final DoubleProperty yCoordinateProperty() {
        return this.yCoordinate;
    }
    
    public double getYCoordinate() {
        return this.yCoordinateProperty().get();
    }
    
    public void setYCoordinate(double yCoordinate) {
        this.yCoordinateProperty().set(yCoordinate);
    }
    
    private final DoubleProperty zCoordinate = new SimpleDoubleProperty(0);
    public final DoubleProperty zCoordinateProperty() {
        return this.zCoordinate;
    }
    
    public double getZCoordinate() {
        return this.zCoordinateProperty().get();
    }
    
    public void setZCoordinate(double zCoordinate) {
        this.zCoordinateProperty().set(zCoordinate);
    }
    
    private int maxNumberOfPoints;
    
    public int getMaxNumberOfPoints() {
        return maxNumberOfPoints;
    }
    
    public void setMaxNumberOfPoints(int maxNumberOfPoints) {
        this.maxNumberOfPoints = maxNumberOfPoints;
    }

    //private final ObservableList<TetgenPoint> pointData = FXCollections.observableArrayList();
    private final ObservableList<TetgenFacet> facetData = FXCollections.observableArrayList();
    
    /*public ObservableList<TetgenPoint> getPointData() {
        ObservableList<TetgenPoint> pointData = FXCollections.observableArrayList();
        getItems().forEach(p -> {
            for (TetgenPoint point : p.getPointData()) {
                pointData.add(new TetgenPoint(point));
            }
        });
        return pointData;
    }*/
    
    /*public ObservableList<TetgenPoint> getPointData() {
        return pointData;
    }
    
    public void addPointData(ObservableList<TetgenPoint> data) {
        for (TetgenPoint p : data)
            pointData.add(new TetgenPoint(p));
    }*/
     
      public ObservableList<TetgenFacet> getFacetData() {
        return facetData;
    }
    
    private final ObjectProperty<Color> diffuseColor = new SimpleObjectProperty(this, "diffuseColor");
    public final ObjectProperty<Color> diffuseColorProperty() {
        return this.diffuseColor;
    }
    
    public Color getDiffuseColor() {
        return this.diffuseColorProperty().get();
    }
    
    public void setDiffuseColor(Color diffuseColor) {
        this.diffuseColorProperty().set(diffuseColor);
    }
    
    private final ObjectProperty<Color> specularColor = new SimpleObjectProperty(this, "specularColor");
    public final ObjectProperty<Color> specularColorProperty() {
        return this.specularColor;
    }
    
    public Color getSpecularColor() {
        return this.specularColorProperty().get();
    }
    
    public void setSpecularColor(Color specularColor) {
        this.specularColorProperty().set(specularColor);
    }
    
    /*private Integer copiedId;
    
    public Integer getCopiedId() {
        return this.copiedId;
    }
    
    public void setCopiedId(Integer copiedId) {
        this.copiedId = copiedId;
    }*/
    
    private Integer histionId;
    
    public Integer getHistionId() {
        return this.histionId;
    }
    
    public void setHistionId(Integer histionId) {
        this.histionId = histionId;
    }
    
    private boolean show;
    
    public boolean getShow() {
        return this.show;
    }
    
    public void setShow(boolean show) {
        this.show = show;
    }
    
    /*private Point3D nodeAvg;
    
    public Point3D getNodeAvg() {
        return nodeAvg;
    }
    
    public void setNodeAvg(Point3D nodeAvg) {
        this.nodeAvg = new Point3D(nodeAvg.getX(), nodeAvg.getY(), nodeAvg.getZ());
    }*/
    
    private static Integer count = 0;
    
    /*public Cell(Integer histionId, ObservableMap<Integer, Part> itemMap) {  
        super(-1, "", itemMap);
        this.xRotate.set(0);
        this.yRotate.set(0);
        this.xCoordinate.set(0);
        this.yCoordinate.set(0);
        this.zCoordinate.set(0);
        this.maxNumberOfPoints = 3;
        this.diffuseColor.set(Color.RED);
        this.specularColor.set(Color.RED);
        this.copiedId = -1;
        this.histionId = histionId;
    }*/
    
    public Cell(String name, double xRot, double yRot, double x, double y, double z,
            ObservableList<TetgenFacet> facetData, int maxNumberOfPoints, 
            Color diffCol, Color specCol, Integer histionId, boolean show) {  
        super(count++, name);
        this.xRotate.set(xRot);
        this.yRotate.set(yRot);
        this.xCoordinate.set(x);
        this.yCoordinate.set(y);
        this.zCoordinate.set(z);
        /*for (int i = 0; i < pointData.size(); i++)
            this.pointData.add(new TetgenPoint(pointData.get(i)));*/
        for (int i = 0; i < facetData.size(); i++)
            this.facetData.add(new TetgenFacet(facetData.get(i)));
        this.maxNumberOfPoints = maxNumberOfPoints;
        this.diffuseColor.set(diffCol);
        this.specularColor.set(specCol);
        //this.nodeAvg = new Point3D(nodeAvg.getX(), nodeAvg.getY(), nodeAvg.getZ());
        this.histionId = histionId;
        this.show = show;
    }
    
    public Cell(Integer id, String name, double xRot, double yRot, double x, double y, double z,
            ObservableList<TetgenFacet> facetData, int maxNumberOfPoints, 
            Color diffCol, Color specCol, Integer histionId, boolean show) {  
        super(id, name);
        this.xRotate.set(xRot);
        this.yRotate.set(yRot);
        this.xCoordinate.set(x);
        this.yCoordinate.set(y);
        this.zCoordinate.set(z);
        /*for (int i = 0; i < pointData.size(); i++)
            this.pointData.add(new TetgenPoint(pointData.get(i)));*/
        for (int i = 0; i < facetData.size(); i++)
            this.facetData.add(new TetgenFacet(facetData.get(i)));
        this.maxNumberOfPoints = maxNumberOfPoints;
        this.diffuseColor.set(diffCol);
        this.specularColor.set(specCol);
        //this.nodeAvg = new Point3D(nodeAvg.getX(), nodeAvg.getY(), nodeAvg.getZ());
        this.histionId = histionId;
        this.show = show;
    }
    
    public Cell(Integer id, Cell с) { 
        super(id, с.getName());
        this.xRotate.set(с.getXRotate());
        this.yRotate.set(с.getYRotate());
        this.xCoordinate.set(с.getXCoordinate());
        this.yCoordinate.set(с.getYCoordinate());
        this.zCoordinate.set(с.getZCoordinate());
        /*for (int i = 0; i < с.getPointData().size(); i++)
            this.pointData.add(new TetgenPoint(с.getPointData().get(i)));*/
        for (int i = 0; i < с.getFacetData().size(); i++)
            this.facetData.add(new TetgenFacet(с.getFacetData().get(i)));
        this.maxNumberOfPoints = с.getMaxNumberOfPoints();
        this.diffuseColor.set(с.getDiffuseColor());
        this.specularColor.set(с.getSpecularColor());
        //this.nodeAvg = new Point3D(с.getNodeAvg().getX(), с.getNodeAvg().getY(), с.getNodeAvg().getZ());
        this.histionId = с.getHistionId();
        this.show = с.getShow();
        /*с.getItems().forEach(p -> {
            addChild(p);
        });*/
    }
    
    public Cell(Cell с, Integer histionId) { 
        super(count++, с.getName());
        this.xRotate.set(с.getXRotate());
        this.yRotate.set(с.getYRotate());
        this.xCoordinate.set(с.getXCoordinate());
        this.yCoordinate.set(с.getYCoordinate());
        this.zCoordinate.set(с.getZCoordinate());
        /*for (int i = 0; i < с.getPointData().size(); i++)
            this.pointData.add(new TetgenPoint(с.getPointData().get(i)));*/
        for (int i = 0; i < с.getFacetData().size(); i++)
            this.facetData.add(new TetgenFacet(с.getFacetData().get(i)));
        this.maxNumberOfPoints = с.getMaxNumberOfPoints();
        this.diffuseColor.set(с.getDiffuseColor());
        this.specularColor.set(с.getSpecularColor());
        //this.nodeAvg = new Point3D(с.getNodeAvg().getX(), с.getNodeAvg().getY(), с.getNodeAvg().getZ());
        this.histionId = histionId;
        this.show = с.getShow();
        /*с.getItems().forEach(p -> {
            addChild(p);
        });*/
    }
    
    /*private static long count = 0;

    public Cell(String name, double xRot, double yRot, double xPos, double yPos,
            double zPos) {
        super(count++, name);
        this.xRotate.set(xRot);
        this.yRotate.set(yRot);
        this.xCoordinate.set(xPos);
        this.yCoordinate.set(yPos);
        this.zCoordinate.set(zPos);
    }
    
    public Cell(Long id, String name, double xRot, double yRot, double xPos,
            double yPos, double zPos) {
        super(id, name);
        this.xRotate.set(xRot);
        this.yRotate.set(yRot);
        this.xCoordinate.set(xPos);
        this.yCoordinate.set(yPos);
        this.zCoordinate.set(zPos);
    }
    
    public Cell(Cell c) {
        super(count++, c.getName());
        this.xRotate.set(c.getXRotate());
        this.yRotate.set(c.getYRotate());
        this.xCoordinate.set(c.getXCoordinate());
        this.yCoordinate.set(c.getYCoordinate());
        this.zCoordinate.set(c.getZCoordinate());
    }
    
    public Cell(Long id, Cell c) {
        super(id, c.getName());
        this.xRotate.set(c.getXRotate());
        this.yRotate.set(c.getYRotate());
        this.xCoordinate.set(c.getXCoordinate());
        this.yCoordinate.set(c.getYCoordinate());
        this.zCoordinate.set(c.getZCoordinate());
    }
    
    private final DoubleProperty xRotate = new SimpleDoubleProperty(0);
    public final DoubleProperty xRotateProperty() {
        return this.xRotate;
    }
    
    public final double getXRotate() {
        return this.xRotateProperty().get();
    }
    
    public final void setXRotate(double xRotate) {
        this.xRotateProperty().set(xRotate);
    }
    
    private final DoubleProperty yRotate = new SimpleDoubleProperty(0);
    public final DoubleProperty yRotateProperty() {
        return this.yRotate;
    }
    
    public final double getYRotate() {
        return this.yRotateProperty().get();
    }
    
    public final void setYRotate(double yRotate) {
        this.yRotateProperty().set(yRotate);
    }
    
    private final DoubleProperty xCoordinate = new SimpleDoubleProperty(0);
    public final DoubleProperty xCoordinateProperty() {
        return this.xCoordinate;
    }
    
    public final double getXCoordinate() {
        return this.xCoordinateProperty().get();
    }
    
    public final void setXCoordinate(double xCoordinate) {
        this.xCoordinateProperty().set(xCoordinate);
    }
    
    private final DoubleProperty yCoordinate = new SimpleDoubleProperty(0);
    public final DoubleProperty yCoordinateProperty() {
        return this.yCoordinate;
    }
    
    public final double getYCoordinate() {
        return this.yCoordinateProperty().get();
    }
    
    public final void setYCoordinate(double yCoordinate) {
        this.yCoordinateProperty().set(yCoordinate);
    }
    
    private final DoubleProperty zCoordinate = new SimpleDoubleProperty(0);
    public final DoubleProperty zCoordinateProperty() {
        return this.zCoordinate;
    }
    
    public final double getZCoordinate() {
        return this.zCoordinateProperty().get();
    }
    
    public final void setZCoordinate(double zCoordinate) {
        this.zCoordinateProperty().set(zCoordinate);
    }*/
    
    @Override
    public void addChild(Part p) {
        //getItemMap().put(p.getId(), new Part(p.getId(), p));
        getItemMap().put(p.getId(), p);
    }
    
    @Override
    public void deleteChild(Integer id) {
        getItemMap().remove(id);
    }
}
