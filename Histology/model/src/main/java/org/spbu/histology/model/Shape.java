package org.spbu.histology.model;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;

public class Shape {
    
    private Long id;
    
    public Long getId() {
        return this.id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    private StringProperty name = new SimpleStringProperty(this, "name");
    public final StringProperty nameProperty() {
        return this.name;
    }
    
    public String getName() {
        return this.nameProperty().get();
    }
    
    public void setName(String name) {
        this.nameProperty().set(name);
    }
    
    private DoubleProperty xRotate = new SimpleDoubleProperty(0);
    public final DoubleProperty xRotateProperty() {
        return this.xRotate;
    }
    
    public double getXRotate() {
        return this.xRotateProperty().get();
    }
    
    public void setXRotate(double xRotate) {
        this.xRotateProperty().set(xRotate);
    }
    
    private DoubleProperty yRotate = new SimpleDoubleProperty(0);
    public final DoubleProperty yRotateProperty() {
        return this.yRotate;
    }
    
    public double getYRotate() {
        return this.yRotateProperty().get();
    }
    
    public void setYRotate(double yRotate) {
        this.yRotateProperty().set(yRotate);
    }
    
    private DoubleProperty xCoordinate = new SimpleDoubleProperty(0);
    public final DoubleProperty xCoordinateProperty() {
        return this.xCoordinate;
    }
    
    public double getXCoordinate() {
        return this.xCoordinateProperty().get();
    }
    
    public void setXCoordinate(double xCoordinate) {
        this.xCoordinateProperty().set(xCoordinate);
    }
    
    private DoubleProperty yCoordinate = new SimpleDoubleProperty(0);
    public final DoubleProperty yCoordinateProperty() {
        return this.yCoordinate;
    }
    
    public double getYCoordinate() {
        return this.yCoordinateProperty().get();
    }
    
    public void setYCoordinate(double yCoordinate) {
        this.yCoordinateProperty().set(yCoordinate);
    }
    
    private DoubleProperty zCoordinate = new SimpleDoubleProperty(0);
    public final DoubleProperty zCoordinateProperty() {
        return this.zCoordinate;
    }
    
    public double getZCoordinate() {
        return this.zCoordinateProperty().get();
    }
    
    public void setZCoordinate(double zCoordinate) {
        this.zCoordinateProperty().set(zCoordinate);
    }
    
    private int facetNumber;
    
    public int getFacetNumber() {
        return facetNumber;
    }
    
    public void setFacetNumber(int facetNumber) {
        this.facetNumber = facetNumber;
    }
    
    private int maxNumberOfPoints;
    
    public int getMaxNumberOfPoints() {
        return maxNumberOfPoints;
    }
    
    public void setMaxNumberOfPoints(int maxNumberOfPoints) {
        this.maxNumberOfPoints = maxNumberOfPoints;
    }

    private ObservableList<TetgenPoint> pointData = FXCollections.observableArrayList();
    private ObservableList<TetgenPoint> holeData = FXCollections.observableArrayList();
    private ObservableList<TetgenFacetPolygon> polygonsInFacetData = FXCollections.observableArrayList();
    private ObservableList<TetgenFacetHole> holesInFacetData = FXCollections.observableArrayList();
    
    public ObservableList<TetgenPoint> getPointData() {
        return pointData;
    }
    
     public ObservableList<TetgenPoint> getHoleData() {
        return holeData;
    }
     
      public ObservableList<TetgenFacetPolygon> getPolygonsInFacetData() {
        return polygonsInFacetData;
    }
      
       public ObservableList<TetgenFacetHole> getHolesInFacetData() {
        return holesInFacetData;
    }
    
    private ObjectProperty<Color> diffuseColor = new SimpleObjectProperty(this, "diffuseColor");
    public final ObjectProperty<Color> diffuseColorProperty() {
        return this.diffuseColor;
    }
    
    public Color getDiffuseColor() {
        return this.diffuseColorProperty().get();
    }
    
    public void setDiffuseColor(Color diffuseColor) {
        this.diffuseColorProperty().set(diffuseColor);
    }
    
    private ObjectProperty<Color> specularColor = new SimpleObjectProperty(this, "specularColor");
    public final ObjectProperty<Color> specularColorProperty() {
        return this.specularColor;
    }
    
    public Color getSpecularColor() {
        return this.specularColorProperty().get();
    }
    
    public void setSpecularColor(Color specularColor) {
        this.specularColorProperty().set(specularColor);
    }
    
    private Long copiedId;
    
    public Long getCopiedId() {
        return this.copiedId;
    }
    
    public void setCopiedId(Long copiedId) {
        this.copiedId = copiedId;
    }
    
    private Long histionId;
    
    public Long getHistionId() {
        return this.histionId;
    }
    
    public void setHistionId(Long histionId) {
        this.histionId = histionId;
    }
    
    private Long cellId;
    
    public Long getCellId() {
        return this.cellId;
    }
    
    public void setCellId(Long cellId) {
        this.cellId = cellId;
    }
    
    private Node nodeAvg;
    
    public Node getNodeAvg() {
        return nodeAvg;
    }
    
    public void setNodeAvg(Node nodeAvg) {
        this.nodeAvg = new Node(nodeAvg.x, nodeAvg.y, nodeAvg.z);
    }
    
    /*private double xAvg;
    private double yAvg;
    private double zAvg;
    
    public double getXAvg() {
        return xAvg;
    }
    
    public void setXAvg(double xAvg) {
        this.xAvg = xAvg;
    }
    
    public double getYAvg() {
        return yAvg;
    }
    
    public void setYAvg(double yAvg) {
        this.yAvg = yAvg;
    }
    
    public double getZAvg() {
        return zAvg;
    }
    
    public void setZAvg(double zAvg) {
        this.zAvg = zAvg;
    }*/
    
    private static long count = 0;
    
    /*private LongProperty cell = new SimpleStringProperty(this, "cell");
    public final StringProperty cellProperty() {
        return this.cell;
    }
    
    public String getCell() {
        return this.cellProperty().get();
    }
    
    public void setCell(String cell) {
        this.cellProperty().set(cell);
    }
    
    private StringProperty histion = new SimpleStringProperty(this, "histion");
    public final StringProperty histionProperty() {
        return this.histion;
    }
    
    public String getHistion() {
        return this.histionProperty().get();
    }
    
    public void setHistion(String histion) {
        this.cellProperty().set(histion);
    }*/
    
    /*private StringProperty tissue = new SimpleStringProperty(this, "tissue");
    public final StringProperty tissueProperty() {
        return this.tissue;
    }
    
    public String getTissue() {
        return this.cellProperty().get();
    }
    
    public void setTissue(String tissue) {
        this.cellProperty().set(tissue);
    }*/
    
    public Shape(long histionId, long cellId) {  
        //this.id = count++;
        this.id = (long)(-1);
        this.name.set("");
        this.xRotate.set(0);
        this.yRotate.set(0);
        this.xCoordinate.set(0);
        this.yCoordinate.set(0);
        this.zCoordinate.set(0);
        /*for (int i = 0; i < pointData.size(); i++)
            this.pointData.add(new TetgenPoint(pointData.get(i)));
        for (int i = 0; i < holeData.size(); i++)
            this.holeData.add(new TetgenPoint(holeData.get(i)));
        for (int i = 0; i < polygonsInFacetData.size(); i++)
            this.polygonsInFacetData.add(new TetgenFacetPolygon(polygonsInFacetData.get(i)));
        for (int i = 0; i < holesInFacetData.size(); i++)
            this.holesInFacetData.add(new TetgenFacetHole(holesInFacetData.get(i)));*/
        this.facetNumber = 4;
        this.maxNumberOfPoints = 3;
        this.diffuseColor.set(Color.RED);
        this.specularColor.set(Color.RED);
        this.copiedId = (long)-1;
        this.histionId = histionId;
        this.cellId = cellId;
    }
    
    public Shape(String name, double xRot, double yRot, double x, double y, double z,
            ObservableList<TetgenPoint> pointData, ObservableList<TetgenPoint> holeData,
            ObservableList<TetgenFacetPolygon> polygonsInFacetData, ObservableList<TetgenFacetHole> holesInFacetData,
            int facetNumber, int maxNumberOfPoints,  Color diffCol, Color specCol, Node nodeAvg, long copiedId, long histionId, long cellId) {  
        this.id = count++;
        this.name.set(name);
        this.xRotate.set(xRot);
        this.yRotate.set(yRot);
        this.xCoordinate.set(x);
        this.yCoordinate.set(y);
        this.zCoordinate.set(z);
        for (int i = 0; i < pointData.size(); i++)
            this.pointData.add(new TetgenPoint(pointData.get(i)));
        for (int i = 0; i < holeData.size(); i++)
            this.holeData.add(new TetgenPoint(holeData.get(i)));
        for (int i = 0; i < polygonsInFacetData.size(); i++)
            this.polygonsInFacetData.add(new TetgenFacetPolygon(polygonsInFacetData.get(i)));
        for (int i = 0; i < holesInFacetData.size(); i++)
            this.holesInFacetData.add(new TetgenFacetHole(holesInFacetData.get(i)));
        this.facetNumber = facetNumber;
        this.maxNumberOfPoints = maxNumberOfPoints;
        this.diffuseColor.set(diffCol);
        this.specularColor.set(specCol);
        /*this.xAvg = xAvg;
        this.yAvg = yAvg;
        this.zAvg = zAvg;*/
        this.nodeAvg = new Node(nodeAvg.x, nodeAvg.y, nodeAvg.z);
        this.copiedId = copiedId;
        this.histionId = histionId;
        this.cellId = cellId;
    }
    
    public Shape(Long id, String name, double xRot, double yRot, double x, double y, double z,
            ObservableList<TetgenPoint> pointData, ObservableList<TetgenPoint> holeData,
            ObservableList<TetgenFacetPolygon> polygonsInFacetData, ObservableList<TetgenFacetHole> holesInFacetData,
            int facetNumber, int maxNumberOfPoints,  Color diffCol, Color specCol, Node nodeAvg, long copiedId, long histionId, long cellId) {  
        this.id = id;
        this.name.set(name);
        this.xRotate.set(xRot);
        this.yRotate.set(yRot);
        this.xCoordinate.set(x);
        this.yCoordinate.set(y);
        this.zCoordinate.set(z);
        for (int i = 0; i < pointData.size(); i++)
            this.pointData.add(new TetgenPoint(pointData.get(i)));
        for (int i = 0; i < holeData.size(); i++)
            this.holeData.add(new TetgenPoint(holeData.get(i)));
        for (int i = 0; i < polygonsInFacetData.size(); i++)
            this.polygonsInFacetData.add(new TetgenFacetPolygon(polygonsInFacetData.get(i)));
        for (int i = 0; i < holesInFacetData.size(); i++)
            this.holesInFacetData.add(new TetgenFacetHole(holesInFacetData.get(i)));
        this.facetNumber = facetNumber;
        this.maxNumberOfPoints = maxNumberOfPoints;
        this.diffuseColor.set(diffCol);
        this.specularColor.set(specCol);
        /*this.xAvg = xAvg;
        this.yAvg = yAvg;
        this.zAvg = zAvg;*/
        this.nodeAvg = new Node(nodeAvg.x, nodeAvg.y, nodeAvg.z);
        this.copiedId = copiedId;
        this.histionId = histionId;
        this.cellId = cellId;
    }
    
    public Shape(Long id, Shape shape) { 
        this.id = id;
        this.name.set(shape.getName());
        this.xRotate.set(shape.getXRotate());
        this.yRotate.set(shape.getYRotate());
        this.xCoordinate.set(shape.getXCoordinate());
        this.yCoordinate.set(shape.getYCoordinate());
        this.zCoordinate.set(shape.getZCoordinate());
        for (int i = 0; i < shape.getPointData().size(); i++)
            this.pointData.add(new TetgenPoint(shape.getPointData().get(i)));
        for (int i = 0; i < shape.getHoleData().size(); i++)
            this.holeData.add(new TetgenPoint(shape.getHoleData().get(i)));
        for (int i = 0; i < shape.getPolygonsInFacetData().size(); i++)
            this.polygonsInFacetData.add(new TetgenFacetPolygon(shape.getPolygonsInFacetData().get(i)));
        for (int i = 0; i < shape.getHolesInFacetData().size(); i++)
            this.holesInFacetData.add(new TetgenFacetHole(shape.getHolesInFacetData().get(i)));
        this.facetNumber = shape.getFacetNumber();
        this.maxNumberOfPoints = shape.getMaxNumberOfPoints();
        this.diffuseColor.set(shape.getDiffuseColor());
        this.specularColor.set(shape.getSpecularColor());
        /*this.xAvg = shape.getXAvg();
        this.yAvg = shape.getYAvg();
        this.zAvg = shape.getZAvg();*/
        this.nodeAvg = new Node(shape.getNodeAvg().x, shape.getNodeAvg().y, shape.getNodeAvg().z);
        this.copiedId = shape.getCopiedId();
        this.histionId = shape.getHistionId();
        this.cellId = shape.getCellId();
    }
    
    public Shape(Shape shape, long histionId, long cellId) { 
        this.id = count++;
        this.name.set(shape.getName());
        this.xRotate.set(shape.getXRotate());
        this.yRotate.set(shape.getYRotate());
        this.xCoordinate.set(shape.getXCoordinate());
        this.yCoordinate.set(shape.getYCoordinate());
        this.zCoordinate.set(shape.getZCoordinate());
        for (int i = 0; i < shape.getPointData().size(); i++)
            this.pointData.add(new TetgenPoint(shape.getPointData().get(i)));
        for (int i = 0; i < shape.getHoleData().size(); i++)
            this.holeData.add(new TetgenPoint(shape.getHoleData().get(i)));
        for (int i = 0; i < shape.getPolygonsInFacetData().size(); i++)
            this.polygonsInFacetData.add(new TetgenFacetPolygon(shape.getPolygonsInFacetData().get(i)));
        for (int i = 0; i < shape.getHolesInFacetData().size(); i++)
            this.holesInFacetData.add(new TetgenFacetHole(shape.getHolesInFacetData().get(i)));
        this.facetNumber = shape.getFacetNumber();
        this.maxNumberOfPoints = shape.getMaxNumberOfPoints();
        this.diffuseColor.set(shape.getDiffuseColor());
        this.specularColor.set(shape.getSpecularColor());
        this.nodeAvg = new Node(shape.getNodeAvg().x, shape.getNodeAvg().y, shape.getNodeAvg().z);
        /*this.xAvg = shape.getXAvg();
        this.yAvg = shape.getYAvg();
        this.zAvg = shape.getZAvg();*/
        this.copiedId = shape.getCopiedId();
        this.histionId = histionId;
        this.cellId = cellId;
    }
    
}
