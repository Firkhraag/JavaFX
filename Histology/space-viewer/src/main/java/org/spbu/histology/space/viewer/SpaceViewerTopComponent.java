package org.spbu.histology.space.viewer;

import org.spbu.histology.tetgen.TetgenResult;
import org.spbu.histology.tetgen.Tetgen;
import org.spbu.histology.model.CameraView;
import java.awt.BorderLayout;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.DrawMode;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;
import javafx.scene.transform.Rotate;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.util.Lookup;
import org.openide.windows.TopComponent;
import org.openide.util.NbBundle.Messages;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.geometry.Point3D;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import org.openide.LifecycleManager;
import org.spbu.histology.fxyz.Line3D;
import org.spbu.histology.model.Cell;
import org.spbu.histology.model.CrossSection;
import org.spbu.histology.model.Histion;
import org.spbu.histology.model.HistionManager;
import org.spbu.histology.model.TetgenPoint;
import org.spbu.histology.fxyz.Text3DMesh;
import org.spbu.histology.model.AlertBox;
import org.spbu.histology.model.GroupPosition;
import org.spbu.histology.model.HideCells;
import org.spbu.histology.model.Node;
import org.spbu.histology.model.TwoIntegers;
import org.spbu.histology.model.TwoPoints;

/**
 * Top component which displays something.
 */
@ConvertAsProperties(
        dtd = "-//org.spbu.histology.space.viewer//SpaceViewer//EN",
        autostore = false
)
@TopComponent.Description(
        preferredID = "SpaceViewerTopComponent",
        //iconBase="SET/PATH/TO/ICON/HERE", 
        persistenceType = TopComponent.PERSISTENCE_ALWAYS
)
@TopComponent.Registration(mode = "editor", openAtStartup = true)
@ActionID(category = "Window", id = "org.spbu.histology.space.viewer.SpaceViewerTopComponent")
@ActionReference(path = "Menu/Window" /*, position = 333 */)
@TopComponent.OpenActionRegistration(
        displayName = "#CTL_SpaceAction",
        preferredID = "SpaceViewerTopComponent"
)
@Messages({
    "CTL_SpaceAction=SpaceViewer",
    "CTL_SpaceTopComponent=SpaceViewer Window",
    "HINT_SpaceTopComponent=This is a SpaceViewer window"
})

public final class SpaceViewerTopComponent extends TopComponent {
    
    private HistionManager hm = null;
    
    private final Group axisGroup = new Group();
    private final Group shapeGroupAxisGroup = new Group();
    
    private Box crossSectionPlane;
    
    private double mousePosX;
    private double mousePosY;
    private double mouseOldX;
    private double mouseOldY;
    
    private Rotate rotateXCam = new Rotate(0, Rotate.X_AXIS);
    private Rotate rotateYCam = new Rotate(0, Rotate.Y_AXIS);
    
    private Rotate rotateXShapeGroup = new Rotate(0, Rotate.X_AXIS);
    private Rotate rotateYShapeGroup = new Rotate(0, Rotate.Y_AXIS);
    
    private Rotate rotateXCrossSection = new Rotate(0, Rotate.X_AXIS);
    private Rotate rotateYCrossSection = new Rotate(0, Rotate.Y_AXIS);
    
    private PerspectiveCamera camera;
    private Group root = new Group();
    private Group shapeGroup = new Group();
    private final PhongMaterial transparentMaterial = new PhongMaterial();
    private final PhongMaterial transparentXAxisMaterial = new PhongMaterial();
    private final PhongMaterial transparentYAxisMaterial = new PhongMaterial();
    
    private final PhongMaterial shapeGroupMaterial = new PhongMaterial();
    
    private Box crossSectionXAxis;
    private Box crossSectionYAxis;
    
    private final ObservableMap<Integer, TriangleMesh> meshMap = 
            FXCollections.observableMap(new ConcurrentHashMap());
    
    private final ObservableMap<Integer, ArrayList<Line3D>> lineMap = 
            FXCollections.observableMap(new ConcurrentHashMap());
    
    private final ObservableMap<Integer, Color> colorsList = 
            FXCollections.observableMap(new ConcurrentHashMap());
    private final ObservableMap<Integer, double[]> nodesList = 
            FXCollections.observableMap(new ConcurrentHashMap());
    
    private final ObservableMap<Integer, double[]> nodesListTemp = 
            FXCollections.observableMap(new ConcurrentHashMap());
    
    private final ObservableMap<Integer, int[]> tetrahedronsList = 
            FXCollections.observableMap(new ConcurrentHashMap());
    
    double A = 0;
    double B = -1;
    double C = 0;
    double D = 0;
    double crossRotX = 0;
    double crossRotY = 0;
    double crossPosX = 0;
    double crossPosY = 0;
    double crossPosZ = 0;
    
    private final ObservableMap<Integer, List<TwoPoints>> nodesMap = 
            FXCollections.observableMap(new ConcurrentHashMap());
    
    private final MapChangeListener<Integer, Cell> cellListener =
            (change) -> {
                if (change.wasRemoved() && change.wasAdded()) {
                    Cell c = (Cell)change.getValueAdded();
                    Cell removedShape = (Cell)change.getValueRemoved();
                    if (c.getShow()) {
                        if (hm.getShapeMap().get(removedShape.getId()) != null) {
                            CrossSection.removePolygon(c.getId());
                            CrossSection.removeLine(c.getId());
                            shapeGroup.getChildren().remove(hm.getShapeMap().get(removedShape.getId()));
                            for (Line3D l : lineMap.get(removedShape.getId())) {
                                shapeGroup.getChildren().remove(l.getMeshView());
                            }
                        }
                        addCell(c);
                    } else if (!c.getShow() && removedShape.getShow()) {
                        Integer removedCellId = removedShape.getId();
                        shapeGroup.getChildren().remove(hm.getShapeMap().get(removedCellId));
                        for (Line3D l : lineMap.get(removedCellId)) {
                            shapeGroup.getChildren().remove(l.getMeshView());
                        }
                        lineMap.remove(removedCellId);
                        hm.getShapeMap().remove(removedCellId);
                        nodesList.remove(removedCellId);
                        nodesListTemp.remove(removedCellId);
                        tetrahedronsList.remove(removedCellId);
                        colorsList.remove(removedCellId);
                        CrossSection.removePolygon(removedCellId);
                        CrossSection.removeLine(removedCellId);
                        nodesMap.remove(removedCellId);
                    }
                }
                else if (change.wasRemoved()) {  
                    Cell removedCell = (Cell)change.getValueRemoved();
                    if (removedCell.getShow()) {
                        Integer removedCellId = removedCell.getId();
                        shapeGroup.getChildren().remove(hm.getShapeMap().get(removedCellId));
                        for (Line3D l : lineMap.get(removedCellId)) {
                            shapeGroup.getChildren().remove(l.getMeshView());
                        }
                        lineMap.remove(removedCellId);
                        hm.getShapeMap().remove(removedCellId);
                        nodesList.remove(removedCellId);
                        nodesListTemp.remove(removedCellId);
                        tetrahedronsList.remove(removedCellId);
                        colorsList.remove(removedCellId);
                        CrossSection.removePolygon(removedCellId);
                        CrossSection.removeLine(removedCellId);
                        nodesMap.remove(removedCellId);
                    }
                }
                else if (change.wasAdded()) {
                    Cell addedCell = (Cell)change.getValueAdded();
                    if (addedCell.getShow()) {
                        addCell(addedCell);
                    }
                }
            };
    
    private final MapChangeListener<Integer, Histion> histionListener = (change) -> {
                if (change.wasRemoved() && change.wasAdded()) {
                }
                else if (change.wasRemoved()) {  
                }
                else if (change.wasAdded()) {
                    Histion addedHistion = (Histion)change.getValueAdded();
                    for (Cell c : addedHistion.getItems()) {
                        if (c.getShow()) {
                            addCell(c);
                        }
                    }
                    addedHistion.getItemMap().addListener(cellListener);
                }
            };
    
    public SpaceViewerTopComponent() {
        initComponents();
        setName(Bundle.CTL_SpaceTopComponent());
        setToolTipText(Bundle.HINT_SpaceTopComponent());
        
        setLayout(new BorderLayout());
        init();
    }
    
     private void init() {
        final CountDownLatch latch = new CountDownLatch(1);
        JFXPanel fxPanel = new JFXPanel();
        add(fxPanel, BorderLayout.CENTER);
        Platform.setImplicitExit(false);
        
        Platform.runLater(() -> {
            try {
                createScene(fxPanel);
            } finally {
                latch.countDown();
            }
        });
        
        try {
            latch.await();
            CameraView.setCamera("0", "0", "0", "0", "-1000", String.valueOf(35.0));
        } catch (InterruptedException ex) {
            LifecycleManager.getDefault().exit();
        }
    }
    
    private void createScene(JFXPanel fxPanel) {
        root.getChildren().add(shapeGroup);
        Scene scene = new Scene(root, 1000, 1000, true, SceneAntialiasing.BALANCED);
        
        buildAxes();
        buildCamera(scene);
        handleKeyboard(scene);
        handleMouseEvents(scene);       
        
        buildCrossSectionPlane();
        fxPanel.setScene(scene);  
        
        addRotateTransformsToGroup();
    }
    
    private void addPolygon(ArrayList<Line> lineList, ArrayList<Node> pl, ArrayList<Polygon> polList, Integer id) {
        Double[] polPoints = new Double[pl.size() * 2];
        int k = 0;
        for (int i = 0; i < pl.size(); i++) {
            polPoints[k] = pl.get(i).x;
            polPoints[k + 1] = pl.get(i).z;
            k += 2;
            if (i != pl.size() - 1) {
                Line line = new Line();
                
                line.setStartX(pl.get(i).x);
                line.setStartY(pl.get(i).z);
                line.setEndX(pl.get(i + 1).x);
                line.setEndY(pl.get(i + 1).z);
                lineList.add(line);
            } else {
                Line line = new Line();
                
                line.setStartX(pl.get(i).x);
                line.setStartY(pl.get(i).z);
                line.setEndX(pl.get(0).x);
                line.setEndY(pl.get(0).z);
                lineList.add(line);
            }
        }
        
        Polygon polygon = new Polygon();
        polygon.getPoints().addAll(polPoints);
        polygon.setFill(colorsList.get(id));
        polList.add(polygon);
    }
    
    private void findPolygons(List<TwoPoints> lineList, Integer id) {
        ArrayList<Polygon> polList = new ArrayList<>();
        ArrayList<Line> lList = new ArrayList<>();
        Node p;
        ArrayList<Node> pl = new ArrayList<>();
        pl.add(lineList.get(0).getPoint1());
        p = lineList.get(0).getPoint1();
        lineList.remove(0);
        boolean stop = false;
        while (!lineList.isEmpty()) {
            stop = true;
            for (int i = 0; i < lineList.size(); i++) {
                if (p.equals(lineList.get(i).getPoint1())) {
                    pl.add(lineList.get(i).getPoint2());
                    p = lineList.get(i).getPoint2();
                    lineList.remove(i);
                    stop = false;
                    break;
                }
                else if (p.equals(lineList.get(i).getPoint2())) {
                    pl.add(lineList.get(i).getPoint1());
                    p = lineList.get(i).getPoint1();
                    lineList.remove(i);
                    stop = false;
                    break;
                }
            }
            if (stop && (!lineList.isEmpty())) {
                addPolygon(lList, pl, polList, id);
                pl.clear();
                pl.add(lineList.get(0).getPoint1());
                p = lineList.get(0).getPoint1();
                lineList.remove(0);
                //break;
            }
        }
        
        addPolygon(lList, pl, polList, id);
        
        CrossSection.addPolygon(id, polList);
        CrossSection.addLine(id, lList);
    }
    
    private void intersectionsWithEdges(Integer id) {
        List<List<Node>> points = new ArrayList<>();
        List<TwoPoints> nodes = new ArrayList<>();
        
        double[] nl = nodesList.get(id);
        int[] tl = tetrahedronsList.get(id);
        for (int i = 0; i < tl.length; i += 4) {
            ArrayList<Node> intersectionNodes = new ArrayList<>();
            
            findIntersection(new Node(nl[(tl[i] - 1) * 3], nl[(tl[i] - 1) * 3 + 1], nl[(tl[i] - 1) * 3 + 2]),
                    new Node(nl[(tl[i + 1] - 1) * 3], nl[(tl[i + 1] - 1) * 3 + 1], nl[(tl[i + 1] - 1) * 3 + 2]),
                    new Node(nl[(tl[i + 2] - 1) * 3], nl[(tl[i + 2] - 1) * 3 + 1], nl[(tl[i + 2] - 1) * 3 + 2]),
                    new Node(nl[(tl[i + 3] - 1) * 3], nl[(tl[i + 3] - 1) * 3 + 1], nl[(tl[i + 3] - 1) * 3 + 2]),
                    intersectionNodes);
            
            if (intersectionNodes.size() > 2) {
                rotateTillHorizontalPanel(intersectionNodes);
                if (intersectionNodes.size() == 4) {
                    double avgX = (intersectionNodes.get(0).x + intersectionNodes.get(1).x +
                        intersectionNodes.get(2).x + intersectionNodes.get(3).x) / 4;
            
                    double avgZ = (intersectionNodes.get(0).z + intersectionNodes.get(1).z +
                        intersectionNodes.get(2).z + intersectionNodes.get(3).z) / 4;
                    Collections.sort(intersectionNodes, (Node o1, Node o2) -> {
                        double temp1 = Math.atan2(o1.z - avgZ, o1.x - avgX);
                        double temp2 = Math.atan2(o2.z - avgZ, o2.x - avgX);
                        if(temp1 == temp2)
                            return 0;
                        return temp1 < temp2 ? -1 : 1;
                    });
                    
                    boolean already = false;
                    List<Node> temp = intersectionNodes.subList(0, 3);
                    for (List<Node> pl : points) {
                        if (pl.containsAll(temp) && temp.containsAll(pl)) {
                            already = true;
                            break;
                        }
                    }
                    if (!already)
                        points.add(temp);
                    List<Node> temp2 = new ArrayList<>();
                    temp2.add(intersectionNodes.get(0));
                    temp2.add(intersectionNodes.get(3));
                    temp2.add(intersectionNodes.get(2));
                    for (List<Node> pl : points) {
                        if (pl.containsAll(temp2) && temp2.containsAll(pl)) {
                            already = true;
                            break;
                        }
                    }
                    if (!already)
                        points.add(temp2);
                } else {
                    boolean already = false;
                    for (List<Node> pl : points) {
                        if (pl.containsAll(intersectionNodes) && intersectionNodes.containsAll(pl)) {
                            already = true;
                            break;
                        }
                    }
                    if (!already)
                        points.add(intersectionNodes);
                }
            }
        }
        
        for (int i = 0; i < points.size(); i++) {
            boolean cont1 = true;
            boolean cont2 = true;
            boolean cont3 = true;
            TwoPoints tp1 = new TwoPoints(points.get(i).get(0), points.get(i).get(1));
            TwoPoints tp2 = new TwoPoints(points.get(i).get(0), points.get(i).get(2));
            TwoPoints tp3 = new TwoPoints(points.get(i).get(1), points.get(i).get(2));
            for (int j = 0; j < points.size(); j++) {
                if (i == j)
                    continue;
                if ((points.get(j).contains(points.get(i).get(0))) && (points.get(j).contains(points.get(i).get(1)))) {
                    cont1 = false;
                }
                if ((points.get(j).contains(points.get(i).get(0))) && (points.get(j).contains(points.get(i).get(2)))) {
                    cont2 = false;
                }
                if ((points.get(j).contains(points.get(i).get(1))) && (points.get(j).contains(points.get(i).get(2)))) {
                    cont3 = false;
                }
            }
            if (cont1)
                nodes.add(tp1);
            if (cont2)
                nodes.add(tp2);
            if (cont3)
                nodes.add(tp3);
        }
        
        nodesMap.put(id, nodes);
    }
    
    private void findIntersection(final Node p1, final Node p2, final Node p3, final Node p4, ArrayList<Node> intersectionNodes) {
        
        double firstEquation = A * p1.x + B * p1.y + C * p1.z + D;
        double secondEquation = A * p2.x + B * p2.y + C * p2.z + D;
        double thirdEquation = A * p3.x + B * p3.y + C * p3.z + D;
        double fourthEquation = A * p4.x + B * p4.y + C * p4.z + D;
        
        boolean first = false;
        boolean second = false;
        boolean third = false;
        boolean fourth = false;
        if (Math.abs(firstEquation) < 0.000001) {
            intersectionNodes.add(p1);
            first = true;
        }
        if (Math.abs(secondEquation) < 0.000001) {
            intersectionNodes.add(p2);
            second = true;
        }
        if (Math.abs(thirdEquation) < 0.000001) {
            intersectionNodes.add(p3);
            third = true;
        }
        if (Math.abs(fourthEquation) < 0.000001) {
            intersectionNodes.add(p4);
            fourth = true;
        }
        if (intersectionNodes.size() != 3) {
            if ((!first) && (!second)) {
                if (firstEquation * secondEquation < 0) {
                    double a1 = p2.x - p1.x;
                    double a2 = p2.y - p1.y;
                    double a3 = p2.z - p1.z;
                    double t = 1 / (secondEquation - firstEquation) * (-1) * firstEquation;
                    intersectionNodes.add(new Node(a1 * t + p1.x, a2 * t + p1.y, a3 * t + p1.z));
                }
            }
            if ((!first) && (!third)) {
                if (firstEquation * thirdEquation < 0) {
                    double a1 = p3.x - p1.x;
                    double a2 = p3.y - p1.y;
                    double a3 = p3.z - p1.z;
                    double t = 1 / (thirdEquation - firstEquation) * (-1) * firstEquation;
                    intersectionNodes.add(new Node(a1 * t + p1.x, a2 * t + p1.y, a3 * t + p1.z));
                }
            }
            if ((!first) && (!fourth)) {
                if (firstEquation * fourthEquation < 0) {
                    double a1 = p4.x - p1.x;
                    double a2 = p4.y - p1.y;
                    double a3 = p4.z - p1.z;
                    double t = 1 / (fourthEquation - firstEquation) * (-1) * firstEquation;
                    intersectionNodes.add(new Node(a1 * t + p1.x, a2 * t + p1.y, a3 * t + p1.z));
                }
            }
            if ((!third) && (!second)) {
                if (thirdEquation * secondEquation < 0) {
                    double a1 = p2.x - p3.x;
                    double a2 = p2.y - p3.y;
                    double a3 = p2.z - p3.z;
                    double t = 1 / (secondEquation - thirdEquation) * (-1) * thirdEquation;
                    intersectionNodes.add(new Node(a1 * t + p3.x, a2 * t + p3.y, a3 * t + p3.z));
                }
            }
            if ((!fourth) && (!second)) {
                if (fourthEquation * secondEquation < 0) {
                    double a1 = p2.x - p4.x;
                    double a2 = p2.y - p4.y;
                    double a3 = p2.z - p4.z;
                    double t = 1 / (secondEquation - fourthEquation) * (-1) * fourthEquation;
                    intersectionNodes.add(new Node(a1 * t + p4.x, a2 * t + p4.y, a3 * t + p4.z));
                }
            }
            if ((!third) && (!fourth)) {
                if (thirdEquation * fourthEquation < 0) {
                    double a1 = p4.x - p3.x;
                    double a2 = p4.y - p3.y;
                    double a3 = p4.z - p3.z;
                    double t = 1 / (fourthEquation - thirdEquation) * (-1) * thirdEquation;
                    intersectionNodes.add(new Node(a1 * t + p3.x, a2 * t + p3.y, a3 * t + p3.z));
                }
            }
        }
    }
    
    private void rotateTillHorizontalPanel(ArrayList<Node> intersectionNodes) {
        try {
            double angX = -Math.toRadians(crossRotX);
            double angY = -Math.toRadians(crossRotY);
            double cosAngX = Math.cos(angX);
            double cosAngY = Math.cos(angY);
            double sinAngX = Math.sin(angX);
            double sinAngY = Math.sin(angY);
            double xCoord = crossPosX;
            double yCoord = crossPosY;
            double zCoord = crossPosZ;

            for (int i = 0; i < intersectionNodes.size(); i++) {
                double x = intersectionNodes.get(i).x - xCoord;
                double y = intersectionNodes.get(i).y - yCoord;
                double z = intersectionNodes.get(i).z - zCoord;
                
                double temp = x;
                x = x * cosAngY + z * sinAngY;
                z = -temp * sinAngY + z * cosAngY;
                
                z = y * sinAngX + z * cosAngX;
                
                temp = x;
                x = x * cosAngY + z * (-sinAngY);
                z = -temp * (-sinAngY) + z * cosAngY;
                
                intersectionNodes.set(i, new Node(x + xCoord, 0, z + zCoord));
            }
        } catch (Exception ex) {
            
        }
    }
    
    private void applyTransformations(double xRot, double yRot, 
            double xTran, double yTran, double zTran, Point3D nodeAvg,
            ObservableList<TetgenPoint> pointData) {
        double ang, tempVal;
        for (int i = 0; i < pointData.size(); i++) {
            TetgenPoint pd = new TetgenPoint(pointData.get(i));
            
            pd.setX(pd.getX() - nodeAvg.getX());
            pd.setY(pd.getY() - nodeAvg.getY());
            pd.setZ(pd.getZ() - nodeAvg.getZ());
            
            ang = Math.toRadians(xRot);
            tempVal = pd.getY();
            pd.setY(pd.getY() * Math.cos(ang) - pd.getZ() * Math.sin(ang));
            pd.setZ(tempVal * Math.sin(ang) + pd.getZ() * Math.cos(ang));
            
            ang = Math.toRadians(yRot);
            tempVal = pd.getX();
            pd.setX(pd.getX() * Math.cos(ang) + pd.getZ() * Math.sin(ang));
            pd.setZ(-tempVal * Math.sin(ang) + pd.getZ() * Math.cos(ang));
            
            pd.setX(pd.getX() + xTran + nodeAvg.getX());
            pd.setY(pd.getY() + yTran + nodeAvg.getY());
            pd.setZ(pd.getZ() + zTran + nodeAvg.getZ());
            
            pointData.set(i, pd);
        }
    }
    
    int dataSize;
    Point3D nodeAvg;
    
    private void addCell(Cell c) {
        if (c.getHistionId() != 0) {
            hm.getHistionMap().get(0).getItems().forEach(cell -> {
                if (cell.getName().equals(c.getName())) {
                    nodesList.put(c.getId(), new double[nodesListTemp.get(cell.getId()).length]);
                    
                    nodesListTemp.put(c.getId(), new double[nodesListTemp.get(cell.getId()).length]);
                    
                    tetrahedronsList.put(c.getId(), new int[tetrahedronsList.get(cell.getId()).length]);
                    for (int i = 0; i < nodesList.get(cell.getId()).length; i += 3) {
                        nodesListTemp.get(c.getId())[i] = nodesListTemp.get(cell.getId())[i] +
                                hm.getHistionMap().get(c.getHistionId()).getXCoordinate();
                        nodesListTemp.get(c.getId())[i + 1] = nodesListTemp.get(cell.getId())[i + 1] +
                                hm.getHistionMap().get(c.getHistionId()).getYCoordinate();
                        nodesListTemp.get(c.getId())[i + 2] = nodesListTemp.get(cell.getId())[i + 2] +
                                hm.getHistionMap().get(c.getHistionId()).getZCoordinate();
                    }
                    double ang = rotateXShapeGroup.getAngle() + mouseDeltaY * 0.1;
                    double angXCos = Math.cos(Math.toRadians(ang));
                    double angXSin = Math.sin(Math.toRadians(ang));

                    ang = rotateYShapeGroup.getAngle() - mouseDeltaX * 0.1;
                    double angYSin = Math.sin(Math.toRadians(ang));
                    double angYCos = Math.cos(Math.toRadians(ang));
                    if (c.getShow()) {
                        for (int i = 0; i < nodesList.get(cell.getId()).length; i += 3) {
                            double x = nodesListTemp.get(c.getId())[i];
                            double y = nodesListTemp.get(c.getId())[i + 1];
                            double z = nodesListTemp.get(c.getId())[i + 2];
                            double tempVal = y;
                            y = y * angXCos - z * angXSin;
                            z = tempVal * angXSin + z * angXCos;

                            tempVal = x;
                            x = x * angYCos + z * angYSin;
                            z = -tempVal * angYSin + z * angYCos;

                            nodesList.get(c.getId())[i] = x;
                            nodesList.get(c.getId())[i + 1] = y;
                            nodesList.get(c.getId())[i + 2] = z;
                        }
                    }
                    for (int i = 0; i < tetrahedronsList.get(cell.getId()).length; i++) {
                        tetrahedronsList.get(c.getId())[i] = tetrahedronsList.get(cell.getId())[i];
                    }
                    
                    colorsList.put(c.getId(), c.getDiffuseColor());
                    MeshView newMeshView = new MeshView(hm.getShapeMap().get(cell.getId()).getMesh());
                    final PhongMaterial phongMaterial = new PhongMaterial();
                    phongMaterial.setDiffuseColor(c.getDiffuseColor());
                    phongMaterial.setSpecularColor(c.getSpecularColor());
                    newMeshView.setMaterial(phongMaterial);
                    newMeshView.setTranslateX(hm.getHistionMap().get(c.getHistionId()).getXCoordinate());
                    newMeshView.setTranslateY(hm.getHistionMap().get(c.getHistionId()).getYCoordinate());
                    newMeshView.setTranslateZ(hm.getHistionMap().get(c.getHistionId()).getZCoordinate());
                    shapeGroup.getChildren().add(newMeshView);
                    hm.getShapeMap().put(c.getId(), newMeshView);
                    
                    ArrayList<Line3D> lineArr = new ArrayList<>();
                    for (Line3D l :lineMap.get(cell.getId())) {
                        Line3D line = new Line3D(l);
                        line.getMeshView().setTranslateX(hm.getHistionMap().get(c.getHistionId()).getXCoordinate());
                        line.getMeshView().setTranslateY(hm.getHistionMap().get(c.getHistionId()).getYCoordinate());
                        line.getMeshView().setTranslateZ(hm.getHistionMap().get(c.getHistionId()).getZCoordinate());
                        shapeGroup.getChildren().add(line.getMeshView());
                        lineArr.add(line);
                    }
                    lineMap.put(c.getId(), lineArr);
                    intersectionsWithEdges(c.getId());
                    if (nodesMap.get(c.getId()).size() > 0) {
                        findPolygons(nodesMap.get(c.getId()), c.getId());
                    } else {
                        CrossSection.addPolygon(c.getId(), new ArrayList<>());
                        CrossSection.addLine(c.getId(), new ArrayList<>());
                    }
                }
            });
            return;
        }
        ObservableList<TetgenPoint> pointData = FXCollections.observableArrayList();
        
        double xRot = c.getXRotate();
        double yRot = c.getYRotate();
        double xTran = c.getXCoordinate();
        double yTran = c.getYCoordinate();
        double zTran = c.getZCoordinate();
        
        dataSize = 0;
        nodeAvg = new Point3D(0, 0, 0);
        hm.getHistionMap().get(c.getHistionId()).getItemMap().get(c.getId()).getItems().forEach(p -> {
            for (TetgenPoint point : p.getPointData()) {
                pointData.add(new TetgenPoint(point));
                nodeAvg = new Point3D(nodeAvg.getX() + point.getX(), nodeAvg.getY() + point.getY(), nodeAvg.getZ() + point.getZ());
            }
            dataSize += p.getPointData().size();
        });
        nodeAvg = new Point3D(nodeAvg.getX() / dataSize, nodeAvg.getY() / dataSize, nodeAvg.getZ() / dataSize);
        
        applyTransformations(xRot, yRot, xTran, yTran, zTran, nodeAvg, pointData);
        
        c.setTransformedPointData(pointData);
   
        int numberOfNodes = pointData.size();
        double[] nodeList = new double[numberOfNodes * 3];
        int count = 0;
        for (int i = 0; i < numberOfNodes; i++) {
            nodeList[count] = pointData.get(i).getX();
            nodeList[count + 1] = pointData.get(i).getY();
            nodeList[count + 2] = pointData.get(i).getZ();
            count += 3;
        }
        
        ObservableList<ArrayList<Integer>> facetData = c.getFacetData();
        int numberOfFacets = facetData.size();
        
        int[] numberOfPolygonsInFacet = new int[numberOfFacets];
        for (int i = 0; i < numberOfFacets; i++)
            numberOfPolygonsInFacet[i] = 1;
        
        int[] numberOfVerticesInPolygon = new int[numberOfFacets];
        for (int i = 0; i < numberOfFacets; i++) {
            numberOfVerticesInPolygon[i] = facetData.get(i).size();
        }
        
        count = 0;
        for (int i = 0; i < numberOfFacets; i++) {
            count += numberOfVerticesInPolygon[i];
        }
        
        int[] vertexList = new int[count];
        count = 0;
        for (int i = 0; i < numberOfFacets; i++) {
            for (int j = 0; j < numberOfVerticesInPolygon[i]; j++) {
                vertexList[count] = facetData.get(i).get(j);
                count++;
            }
        }
        
        int[] numberOfHolesInFacet = new int[numberOfFacets];
        for (int i = 0; i < numberOfFacets; i++)
            numberOfHolesInFacet[i] = 0;
        double[] holeListInFacet = new double[0];
        
        int numberOfHoles = 0;
        double[] holeList = new double[0];
        
        int numberOfRegions = 0;
        double[] regionList = new double[0]; 
        
        TetgenResult tr = Tetgen.tetrahedralization(numberOfNodes, nodeList, 
                numberOfFacets, numberOfPolygonsInFacet, numberOfHolesInFacet, 
                holeListInFacet, numberOfVerticesInPolygon, vertexList, 
                numberOfHoles, holeList, numberOfRegions, regionList, "pq10000a1000000.0");
        
        if (tr.getNodeList().length == 0) {
            c.setShow(false);
            AlertBox.display("Warning", "Cell can not be be created");
            return;
        }
        
        nodesList.put(c.getId(), new double[tr.getNodeList().length]);
        
        nodesListTemp.put(c.getId(), new double[tr.getNodeList().length]);
        
        tetrahedronsList.put(c.getId(), new int[tr.getTetrahedronList().length]);
        
        System.arraycopy(tr.getNodeList(), 0, nodesList.get(c.getId()), 0, tr.getNodeList().length);
        
        System.arraycopy(tr.getNodeList(), 0, nodesListTemp.get(c.getId()), 0, tr.getNodeList().length);
        
        System.arraycopy(tr.getTetrahedronList(), 0, tetrahedronsList.get(c.getId()), 0, tr.getTetrahedronList().length);
        
        final PhongMaterial phongMaterial = new PhongMaterial();
        phongMaterial.setDiffuseColor(c.getDiffuseColor());
        colorsList.put(c.getId(), c.getDiffuseColor());
        phongMaterial.setSpecularColor(c.getSpecularColor());
        TriangleMesh shapeMesh = new TriangleMesh();
        shapeMesh.getTexCoords().addAll(0,0);
        for (int i = 0; i < tr.getNodeList().length; i++)
            shapeMesh.getPoints().addAll((float)tr.getNodeList()[i]);
        for (int i = 0; i < tr.getFaceList().length; i++)
            shapeMesh.getFaces().addAll(tr.getFaceList()[i] - 1,0);
        for (int i = tr.getFaceList().length - 1; i >= 0; i--)
            shapeMesh.getFaces().addAll(tr.getFaceList()[i] - 1,0);
        meshMap.put(c.getId(), shapeMesh);
        MeshView shape= new MeshView(shapeMesh);
        shape.setDrawMode(DrawMode.FILL);
        shape.setMaterial(phongMaterial);

        String n = c.getName();
        n = n.substring(n.indexOf("<") + 1, n.lastIndexOf(">")); 
        if (!HideCells.getCellNameToHideList().contains(n))
            shapeGroup.getChildren().add(shape);
        hm.getShapeMap().put(c.getId(), shape);
        
        ArrayList<Point3D> linePointsList = new ArrayList<>();
        ArrayList<TwoIntegers> lineData = new ArrayList<>();
        ArrayList<Line3D> lineList = new ArrayList<>();
        
        for (ArrayList<Integer> f : facetData) {
            for (int i = 1; i < f.size(); i++) {
                TwoIntegers ti = new TwoIntegers(0, f.get(i - 1), f.get(i));
                if (!lineData.contains(ti)) {
                    linePointsList.add(new Point3D(pointData.get(ti.getPoint1() - 1).getX(),
                            pointData.get(ti.getPoint1() - 1).getY(),
                            pointData.get(ti.getPoint1() - 1).getZ()));
                    linePointsList.add(new Point3D(pointData.get(ti.getPoint2() - 1).getX(),
                            pointData.get(ti.getPoint2() - 1).getY(),
                            pointData.get(ti.getPoint2() - 1).getZ()));
                    
                    lineData.add(ti);
                } else {
                    if (linePointsList.size() > 0) {
                        Line3D line = new Line3D(linePointsList, 2f, Color.BLACK);
                        lineList.add(line);
                        if (!HideCells.getCellNameToHideList().contains(n)) {
                            shapeGroup.getChildren().add(line.getMeshView());
                        }
                        linePointsList.clear();
                    }
                }
            }
            
            TwoIntegers ti = new TwoIntegers(0, f.get(f.size() - 1), f.get(0));
            if (!lineData.contains(ti)) {
                linePointsList.add(new Point3D(pointData.get(ti.getPoint1() - 1).getX(),
                        pointData.get(ti.getPoint1() - 1).getY(),
                        pointData.get(ti.getPoint1() - 1).getZ()));
                linePointsList.add(new Point3D(pointData.get(ti.getPoint2() - 1).getX(),
                        pointData.get(ti.getPoint2() - 1).getY(),
                        pointData.get(ti.getPoint2() - 1).getZ()));
                Line3D line = new Line3D(linePointsList, 2f, Color.BLACK);
                lineList.add(line);
                if (!HideCells.getCellNameToHideList().contains(n))
                    shapeGroup.getChildren().add(line.getMeshView());
            } else {
                if (linePointsList.size() > 0) {
                    Line3D line = new Line3D(linePointsList, 2f, Color.BLACK);
                    lineList.add(line);
                    if (!HideCells.getCellNameToHideList().contains(n)) {
                        shapeGroup.getChildren().add(line.getMeshView());
                    }
                }
            }
            linePointsList.clear();
        }
        
        lineMap.put(c.getId(), lineList);
        intersectionsWithEdges(c.getId());  
        if (nodesMap.get(c.getId()).size() > 0) {
            findPolygons(nodesMap.get(c.getId()), c.getId());
        } else {
            CrossSection.addPolygon(c.getId(), new ArrayList<>());
            CrossSection.addLine(c.getId(), new ArrayList<>());
        }
    }
    
    private void buildCrossSectionPlane() {
        crossSectionPlane = new Box(3000, 1, 3000);
        transparentMaterial.setDiffuseColor(Color.rgb(0, 0, 0, 0.0));
        transparentMaterial.setSpecularColor(Color.rgb(0, 0, 0, 0.0));
        crossSectionPlane.setMaterial(transparentMaterial);
        crossSectionPlane.setTranslateX(0.0);
        crossSectionPlane.setTranslateY(0.0);
        crossSectionPlane.setTranslateZ(0.0);
        crossSectionPlane.getTransforms().addAll(rotateYCrossSection, rotateXCrossSection);
        
        transparentXAxisMaterial.setDiffuseColor(Color.rgb(0, 0, 0, 0.0));
        transparentXAxisMaterial.setSpecularColor(Color.rgb(0, 0, 0, 0.0));

        transparentYAxisMaterial.setDiffuseColor(Color.rgb(0, 0, 0, 0.0));
        transparentYAxisMaterial.setSpecularColor(Color.rgb(0, 0, 0, 0.0));

        crossSectionXAxis = new Box(3000, 1, 1);
        crossSectionYAxis = new Box(1, 3000, 1);
        
        crossSectionXAxis.setTranslateX(0);
        crossSectionXAxis.setTranslateY(0);
        crossSectionXAxis.setTranslateZ(0);
        crossSectionYAxis.setTranslateX(0);
        crossSectionYAxis.setTranslateY(0);
        crossSectionYAxis.setTranslateZ(0);

        crossSectionXAxis.setMaterial(transparentXAxisMaterial);
        crossSectionYAxis.setMaterial(transparentYAxisMaterial);
        
        crossSectionXAxis.getTransforms().add(rotateYCrossSection);
        
        root.getChildren().addAll(crossSectionXAxis, crossSectionYAxis);
        
        
        root.getChildren().addAll(crossSectionPlane);
        CrossSection.setCrossSection(String.valueOf(rotateXCrossSection.getAngle()), 
                String.valueOf(rotateYCrossSection.getAngle()),  
                String.valueOf(crossSectionPlane.getTranslateX()),
                String.valueOf(crossSectionPlane.getTranslateY()),
                String.valueOf(crossSectionPlane.getTranslateZ()));
    }
    
    private void buildAxes() {
        final PhongMaterial redMaterial = new PhongMaterial();
        redMaterial.setDiffuseColor(Color.DARKRED);
        redMaterial.setSpecularColor(Color.RED);

        final PhongMaterial greenMaterial = new PhongMaterial();
        greenMaterial.setDiffuseColor(Color.DARKGREEN);
        greenMaterial.setSpecularColor(Color.GREEN);

        final PhongMaterial blueMaterial = new PhongMaterial();
        blueMaterial.setDiffuseColor(Color.DARKBLUE);
        blueMaterial.setSpecularColor(Color.BLUE);

        double axisLen = 1900;
        
        final Box xAxis = new Box(axisLen, 2, 2);
        final Box yAxis = new Box(2, axisLen, 2);
        final Box zAxis = new Box(2, 2, axisLen);
        
        xAxis.setTranslateX(0);
        xAxis.setTranslateY(0);
        xAxis.setTranslateZ(0);

        xAxis.setMaterial(redMaterial);
        yAxis.setMaterial(greenMaterial);
        zAxis.setMaterial(blueMaterial);
        
        
        float h = 10; 
        float s = 10;
        TriangleMesh pyramidMesh = new TriangleMesh();
        pyramidMesh.getTexCoords().addAll(0,0);
        pyramidMesh.getPoints().addAll(
            0, 0, 0, // Node 0 - Top
            0, h, -s/2, // Node 1 - Front
            -s/2, h, 0, // Node 2 - Left
            s/2, h, 0, // Node 3 - Back
            0, h, s/2 // Node 4 - Right
        );
        pyramidMesh.getFaces().addAll(
            0,0, 2,0, 1,0, // Front left face
            0,0, 1,0, 3,0, // Front right face
            0,0, 3,0, 4,0, // Back right face
            0,0, 4,0, 2,0, // Back left face
            4,0, 1,0, 2,0, // Bottom rear face
            4,0, 3,0, 1,0 // Bottom front face
        );
        
        MeshView pyramidX= new MeshView(pyramidMesh);
        pyramidX.setDrawMode(DrawMode.FILL);
        pyramidX.setMaterial(redMaterial);
        pyramidX.setTranslateX(axisLen / 2 + 10);
        pyramidX.setTranslateY(0);
        pyramidX.setTranslateZ(0);
        Rotate pyrXRot = new Rotate(90, Rotate.Z_AXIS);
        pyramidX.getTransforms().add(pyrXRot);
        
        MeshView pyramidY= new MeshView(pyramidMesh);
        pyramidY.setDrawMode(DrawMode.FILL);
        pyramidY.setMaterial(greenMaterial);
        pyramidY.setTranslateX(0);
        pyramidY.setTranslateY(axisLen / 2 + 10);
        pyramidY.setTranslateZ(0);
        Rotate pyrYRot = new Rotate(180, Rotate.X_AXIS);
        pyramidY.getTransforms().add(pyrYRot);
        
        MeshView pyramidZ= new MeshView(pyramidMesh);
        pyramidZ.setDrawMode(DrawMode.FILL);
        pyramidZ.setMaterial(blueMaterial);
        pyramidZ.setTranslateX(0);
        pyramidZ.setTranslateY(0);
        pyramidZ.setTranslateZ(axisLen / 2 + 10);
        Rotate pyrZRot = new Rotate(-90, Rotate.X_AXIS);
        pyramidZ.getTransforms().add(pyrZRot);

        axisGroup.getChildren().addAll(pyramidX, pyramidY, pyramidZ);
        
        final ArrayList<MeshView> coordMeshes = new ArrayList<>();
        Text3DMesh text = new Text3DMesh("X", "Arial", 40, true, 1, 0d, 1);
        for (MeshView m : text.getMeshes()) {
            coordMeshes.add(new MeshView(m.getMesh()));
        }
        for (MeshView m : coordMeshes) {
            m.setMaterial(redMaterial);
            m.setTranslateX(axisLen / 2 + 10);
            m.setTranslateY(10);
            m.setTranslateZ(0);
            axisGroup.getChildren().add(m);
        }
        coordMeshes.clear();
        
        text = new Text3DMesh("Y", "Arial", 40, true, 1, 0d, 1);
        for (MeshView m : text.getMeshes()) {
            coordMeshes.add(new MeshView(m.getMesh()));
        }
        for (MeshView m : coordMeshes) {
            m.setMaterial(greenMaterial);
            m.setTranslateX(10);
            m.setTranslateY(axisLen / 2);
            m.setTranslateZ(0);
            axisGroup.getChildren().add(m);
        }
        coordMeshes.clear();
        
        text = new Text3DMesh("Z", "Arial", 40, true, 1, 0d, 1);
        for (MeshView m : text.getMeshes()) {
            coordMeshes.add(new MeshView(m.getMesh()));
        }
        for (MeshView m : coordMeshes) {
            m.setMaterial(blueMaterial);
            m.setTranslateX(10);
            m.setTranslateY(0);
            m.setTranslateZ(axisLen / 2);
            axisGroup.getChildren().add(m);
        }
        coordMeshes.clear();

        axisGroup.getChildren().addAll(xAxis, yAxis, zAxis);
        
        root.getChildren().addAll(axisGroup);
        root.getChildren().addAll(shapeGroupAxisGroup);
    }
    
    private void buildCamera(Scene scene) {
        camera = new PerspectiveCamera(true);
        camera.setTranslateZ(-1000);
        camera.setNearClip(0.1);
        camera.setFarClip(4000);
        camera.setFieldOfView(35.0);
        scene.setCamera(camera);     
    }
    
    private void addRotateTransformsToGroup() {
        shapeGroup.getTransforms().clear();
        shapeGroup.getTransforms().addAll(rotateYShapeGroup, rotateXShapeGroup);
    }
    
    Service<Void> process = new Service() {
        @Override
        protected Task<Void> createTask() {
            return new Task() {
                @Override
                protected Void call() throws Exception {
                    hm.getAllHistions().forEach(h -> {
                        h.getItems().forEach(c -> {
                            if (c.getShow()) {
                                for (int i = 0; i < nodesList.get(c.getId()).length; i += 3) {
                                    double x = nodesListTemp.get(c.getId())[i];
                                    double y = nodesListTemp.get(c.getId())[i + 1];
                                    double z = nodesListTemp.get(c.getId())[i + 2];
                                    double tempVal = y;
                                    y = y * angXCos - z * angXSin;
                                    z = tempVal * angXSin + z * angXCos;

                                    tempVal = x;
                                    x = x * angYCos + z * angYSin;
                                    z = -tempVal * angYSin + z * angYCos;

                                    nodesList.get(c.getId())[i] = x + trX;
                                    nodesList.get(c.getId())[i + 1] = y + trY;
                                    nodesList.get(c.getId())[i + 2] = z + trZ;
                                }
                                intersectionsWithEdges(c.getId());
                            }
                        });
                    });

                    Platform.runLater(() -> {
                        nodesMap.forEach((i, n) -> {
                            if (n.size() > 0) {
                                findPolygons(n, i);
                            } else {
                                CrossSection.addPolygon(i, new ArrayList<>());
                                CrossSection.addLine(i, new ArrayList<>());
                            }
                        });
                    });
                    return null;
                }
            };
        }
    };
    
    Service<Void> processCrossSection = new Service() {
        @Override
        protected Task<Void> createTask() {
            return new Task() {
                @Override
                protected Void call() throws Exception {
                    hm.getAllHistions().forEach(h -> {
                        h.getItems().forEach(c -> {
                            if (c.getShow()) {
                                intersectionsWithEdges(c.getId());
                            }
                        });
                    });
                    
                    Platform.runLater(() -> {
                        nodesMap.forEach((i, n) -> {
                            if (n.size() > 0) {
                                findPolygons(n, i);
                            } else {
                                CrossSection.addPolygon(i, new ArrayList<>());
                                CrossSection.addLine(i, new ArrayList<>());
                            }
                        });
                    });
                    return null;
                }
            };
        }
    };
    
    double mouseDeltaX; 
    double mouseDeltaY; 
    
    double oldValXRot = 0;
    double oldValYRot = 0;
    
    double angXCos = 1;
    double angYCos = 1;
    double angXSin = 0;
    double angYSin = 0;
    
    double trX = 0;
    double trY = 0;
    double trZ = 0;
    
    boolean startProcess = true;
    
    private void handleMouseEvents(Scene scene) {
        
        camera.getTransforms().clear();
        camera.getTransforms().addAll(rotateYCam, rotateXCam);
        shapeGroupAxisGroup.getTransforms().addAll(rotateYShapeGroup, rotateXShapeGroup);
        
        scene.setOnMousePressed(me -> {         
            mouseOldX = me.getSceneX();
            mouseOldY = me.getSceneY();
        });
        
        process.setExecutor(Executors.newFixedThreadPool(1, runnable -> {
            Thread t = new Thread(runnable);
            t.setDaemon(true);
            return t ;
        }));
        
        scene.setOnMouseDragged(me -> {
            
            if (me.isSecondaryButtonDown()) {
                
                final Box xAxisShapeGroup = new Box(2000, 2, 2);
                final Box yAxisShapeGroup = new Box(2, 2000, 2);
                
                shapeGroupMaterial.setDiffuseColor(Color.rgb(0, 0, 0));
                shapeGroupMaterial.setSpecularColor(Color.rgb(0, 0, 0));
                xAxisShapeGroup.setMaterial(shapeGroupMaterial);
                yAxisShapeGroup.setMaterial(shapeGroupMaterial);
                shapeGroupAxisGroup.getChildren().addAll(xAxisShapeGroup, yAxisShapeGroup);
                
                mousePosX = me.getSceneX();
                mousePosY = me.getSceneY();
                mouseDeltaX = (mousePosX - mouseOldX); 
                mouseDeltaY = (mousePosY - mouseOldY); 
                
                double ang1 = rotateXShapeGroup.getAngle()+mouseDeltaY*0.1;
                
                if ((ang1 >= 360) || (ang1 <= -360)) {
                    ang1 = ang1 % 360;
                }
                rotateXShapeGroup.setAngle(ang1);

                double ang2 = rotateYShapeGroup.getAngle() - mouseDeltaX * 0.1;
                if ((ang2 >= 360) || (ang2 <= -360)) {
                    ang2 = ang2 % 360;
                }
                rotateYShapeGroup.setAngle(ang2);
                
                startProcess = false;
                GroupPosition.setXRotate(String.valueOf(ang1));
                GroupPosition.setYRotate(String.valueOf(ang2));
                startProcess = true;
                
                if (!process.isRunning()) {
                    angXCos = Math.cos(Math.toRadians(ang1));
                    angXSin = Math.sin(Math.toRadians(ang1));
                    angYSin = Math.sin(Math.toRadians(ang2));
                    angYCos = Math.cos(Math.toRadians(ang2));
                    process.restart();
                }
                
                mouseOldX = mousePosX;
                mouseOldY = mousePosY;
            }
            
            if (me.isPrimaryButtonDown()) {
                mousePosX = me.getSceneX();
                mousePosY = me.getSceneY();
                
                double angX = rotateXCam.getAngle()-(mousePosY - mouseOldY)*0.05;
                double angY = rotateYCam.getAngle()+(mousePosX - mouseOldX)*0.05;
                
                mouseOldX = mousePosX;
                mouseOldY = mousePosY;
                CameraView.setXRotate(String.valueOf(angX));
                CameraView.setYRotate(String.valueOf(angY));
            }
            
            if (me.isMiddleButtonDown()) {
                mousePosX = me.getSceneX();
                mousePosY = me.getSceneY();
                mouseDeltaX = (mousePosX - mouseOldX);
                mouseDeltaY = (mousePosY - mouseOldY);
                
                double pos1 = shapeGroup.getTranslateX() + mouseDeltaX * Math.cos(Math.toRadians(rotateYCam.getAngle()));
                double pos2 = shapeGroup.getTranslateY() + mouseDeltaY;
                double pos3 = shapeGroup.getTranslateZ() - mouseDeltaX * Math.sin(Math.toRadians(rotateYCam.getAngle()));
                
                shapeGroup.setTranslateX(pos1);
                shapeGroup.setTranslateY(pos2);
                shapeGroup.setTranslateZ(pos3);
                
                shapeGroupAxisGroup.setTranslateX(pos1);
                shapeGroupAxisGroup.setTranslateY(pos2);
                shapeGroupAxisGroup.setTranslateZ(pos3);
                
                startProcess = false;
                GroupPosition.setXCoordinate(String.valueOf(pos1));
                GroupPosition.setYCoordinate(String.valueOf(pos2));
                GroupPosition.setZCoordinate(String.valueOf(pos3));
                startProcess = true;
                
                if (!process.isRunning()) {
                    trX = shapeGroup.getTranslateX() + mouseDeltaX * Math.cos(Math.toRadians(rotateYCam.getAngle()));
                    trY = shapeGroup.getTranslateY() + mouseDeltaY;
                    trZ = shapeGroup.getTranslateZ() - mouseDeltaX * Math.sin(Math.toRadians(rotateYCam.getAngle()));
                    process.restart();
                }
                
                mouseOldX = mousePosX;
                mouseOldY = mousePosY;
            }
        });
        
        scene.setOnMouseReleased(me -> {
            shapeGroupAxisGroup.getChildren().clear();
            
            if (!process.isRunning()) {
                double ang1 = rotateXShapeGroup.getAngle();
                double ang2 = rotateYShapeGroup.getAngle();
                angXCos = Math.cos(Math.toRadians(ang1));
                angXSin = Math.sin(Math.toRadians(ang1));
                angYSin = Math.sin(Math.toRadians(ang2));
                angYCos = Math.cos(Math.toRadians(ang2));
                trX = shapeGroup.getTranslateX();
                trY = shapeGroup.getTranslateY();
                trZ = shapeGroup.getTranslateZ();
                process.restart();
            }

        });
        
        scene.setOnScroll((ScrollEvent e) -> {
                double dy = e.getDeltaY();
                if (dy < 0) {
                    if (camera.getFieldOfView() >= 80)
                        return;
                    CameraView.setFOV(String.valueOf(camera.getFieldOfView() + 0.5));
                } else {
                    if (camera.getFieldOfView() <= 1)
                        return;
                    CameraView.setFOV(String.valueOf(camera.getFieldOfView() - 0.5));
                }
            });
    }
    
    private void handleKeyboard(Scene scene) {   
        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                switch (event.getCode()) {
                    case A:
                        CameraView.setZCoordinate(String.valueOf(
                                camera.getTranslateZ() + Math.sin(Math.toRadians(rotateYCam.getAngle())) * 12));
                        CameraView.setXCoordinate(String.valueOf(
                                camera.getTranslateX() - Math.cos(Math.toRadians(rotateYCam.getAngle())) * 12));
                        break;
                    case D:
                        CameraView.setZCoordinate(String.valueOf(
                                camera.getTranslateZ() - Math.sin(Math.toRadians(rotateYCam.getAngle())) * 12));
                        CameraView.setXCoordinate(String.valueOf(
                                camera.getTranslateX() + Math.cos(Math.toRadians(rotateYCam.getAngle())) * 12));
                        break;
                    case W:
                        CameraView.setZCoordinate(String.valueOf(
                                camera.getTranslateZ() + Math.cos(Math.toRadians(rotateYCam.getAngle())) * 12));
                        CameraView.setXCoordinate(String.valueOf(
                                camera.getTranslateX() + Math.sin(Math.toRadians(rotateYCam.getAngle())) * 12));
                        break;
                    case S:
                        CameraView.setZCoordinate(String.valueOf(
                                camera.getTranslateZ() - Math.cos(Math.toRadians(rotateYCam.getAngle())) * 12));
                        CameraView.setXCoordinate(String.valueOf(
                                camera.getTranslateX() - Math.sin(Math.toRadians(rotateYCam.getAngle())) * 12));
                        break;
                    case Q:
                        CameraView.setYCoordinate(String.valueOf(camera.getTranslateY() + 10));
                        break;
                    case E:
                        CameraView.setYCoordinate(String.valueOf(camera.getTranslateY() - 10));
                        break;
                }
            }
        });
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
    
    
    ChangeListener<String> xRotListener = (v, oldValue, newValue) -> {
        try {
            double ang = Double.parseDouble(newValue);
            if ((ang <= 90) && (ang >= -90)) {
                rotateXCam.setAngle(ang);
            }
        } catch (Exception ex) {
            
        }
    };
    ChangeListener<String> yRotListener = (v, oldValue, newValue) -> {
        try {
            double ang = Double.parseDouble(newValue);
            if ((ang >= 360) || (ang <= -360)) {
                ang = ang % 360;
                CameraView.yRotateProperty().set(String.valueOf(ang));
            }
            rotateYCam.setAngle(ang);
        } catch (Exception ex) {
            
        }
    };
    
    ChangeListener<String> xPosListener = (v, oldValue, newValue) -> {
        try {
            double pos = Double.parseDouble(newValue);
            if ((pos <= 2000) && (pos >= -2000))
                camera.setTranslateX(pos);
        } catch (Exception ex) {
            
        }
    };
    ChangeListener<String> yPosListener = (v, oldValue, newValue) -> {
        try {
            double pos = Double.parseDouble(newValue);
            if ((pos <= 2000) && (pos >= -2000))
                camera.setTranslateY(pos);
        } catch (Exception ex) {
            
        }
    };
    ChangeListener<String> zPosListener = (v, oldValue, newValue) -> {
        try {
            double pos = Double.parseDouble(newValue);
            if ((pos <= 2000) && (pos >= -2000))
                camera.setTranslateZ(pos);
        } catch (Exception ex) {
            
        }
    };
    
    ChangeListener<String> FOVListener = (v, oldValue, newValue) -> {
        try {
            double f = Double.parseDouble(newValue);
            if ((f >= 1) && (f <= 80))
                camera.setFieldOfView(f);
        } catch (Exception ex) {
            
        }
    };
    
    ListChangeListener<String> hideShapeListChangeListener = (change) -> {
        while (change.next()) {
            if (change.wasAdded()) {
                for (String name : change.getAddedSubList()) {
                    hm.getAllHistions().forEach(h -> {
                        h.getItems().forEach(c -> {
                            String n = c.getName();
                            n = n.substring(n.indexOf("<") + 1, n.lastIndexOf(">"));
                            if (n.equals(name)) {
                                shapeGroup.getChildren().remove(hm.getShapeMap().get(c.getId()));
                                for (Line3D l : lineMap.get(c.getId())) {
                                    shapeGroup.getChildren().remove(l.getMeshView());
                                }
                            }
                        });
                    });
                }
            } else {
                for (String name : change.getRemoved()) {
                    hm.getAllHistions().forEach(h -> {
                        h.getItems().forEach(c -> {
                            String n = c.getName();
                            n = n.substring(n.indexOf("<") + 1, n.lastIndexOf(">"));
                            if (n.equals(name)) {
                                shapeGroup.getChildren().add(hm.getShapeMap().get(c.getId()));
                                for (Line3D l : lineMap.get(c.getId())) {
                                    shapeGroup.getChildren().add(l.getMeshView());
                                }
                            }
                        });
                    });
                }
            }
        }
    };
    
    private void addCameraViewListener() {
        CameraView.xRotateProperty().addListener(xRotListener);
        CameraView.yRotateProperty().addListener(yRotListener);
        CameraView.xCoordinateProperty().addListener(xPosListener);
        CameraView.yCoordinateProperty().addListener(yPosListener);
        CameraView.zCoordinateProperty().addListener(zPosListener);
        CameraView.FOVProperty().addListener(FOVListener); 
        HideCells.getCellNameToHideList().addListener(hideShapeListChangeListener);
    }
    
    private void removeCameraViewListener() {
        CameraView.xRotateProperty().removeListener(xRotListener);
        CameraView.yRotateProperty().removeListener(yRotListener);
        CameraView.xCoordinateProperty().removeListener(xPosListener);
        CameraView.yCoordinateProperty().removeListener(yPosListener);
        CameraView.zCoordinateProperty().removeListener(zPosListener);
        CameraView.FOVProperty().removeListener(FOVListener); 
        HideCells.getCellNameToHideList().removeListener(hideShapeListChangeListener);
    }
    
    double oldValXRotCross = 0;
    double oldValYRotCross = 0;
    double oldValXPosCross = 0;
    double oldValYPosCross = 0;
    double oldValZPosCross = 0;
    
    ChangeListener<String> crossSectionXRotListener = (v, oldValue, newValue) -> {
        try {
            double ang = Double.parseDouble(newValue);
            rotateXCrossSection.setAngle(ang);
        } catch (Exception ex) {
            
        }
    };
    ChangeListener<String> crossSectionYRotListener = (v, oldValue, newValue) -> {
        try {
            double ang = Double.parseDouble(newValue);
            if ((ang >= 360) || (ang <= -360)) {
                ang = ang % 360;
                CrossSection.yRotateProperty().set(String.valueOf(ang));
            }
            rotateYCrossSection.setAngle(ang);
        } catch (Exception ex) {
            
        }
    };
    
    ChangeListener<String> crossSectionXPosListener = (v, oldValue, newValue) -> {
        try {
            double pos = Double.parseDouble(newValue);
            if ((pos <= 900) && (pos >= -900)) {
                crossSectionPlane.setTranslateX(pos);
                crossSectionXAxis.setTranslateX(pos);
                crossSectionYAxis.setTranslateX(pos);
            }
        } catch (Exception ex) {
            
        }
    };
    ChangeListener<String> crossSectionYPosListener = (v, oldValue, newValue) -> {
        try {
            double pos = Double.parseDouble(newValue);
            if ((pos <= 900) && (pos >= -900)) {
                crossSectionPlane.setTranslateY(pos);
                crossSectionXAxis.setTranslateY(pos);
                crossSectionYAxis.setTranslateY(pos);
            }
        } catch (Exception ex) {
            
        }
    };
    ChangeListener<String> crossSectionZPosListener = (v, oldValue, newValue) -> {
        try {
            double pos = Double.parseDouble(newValue);
            if ((pos <= 900) && (pos >= -900)) {
                crossSectionPlane.setTranslateZ(pos);
                crossSectionXAxis.setTranslateZ(pos);
                crossSectionYAxis.setTranslateZ(pos);
            }
        } catch (Exception ex) {
            
        }
    };
    
    ChangeListener<String> opaquenessListener = (v, oldValue, newValue) -> {
        try {
            double opq = Double.parseDouble(newValue);
            if ((opq >= 0) && (opq <= 1)) {
                transparentMaterial.setDiffuseColor(Color.rgb(0, 0, 0, opq));
                transparentMaterial.setSpecularColor(Color.rgb(0, 0, 0, opq));
                transparentXAxisMaterial.setDiffuseColor(Color.rgb(0, 0, 0, opq + 0.05));
                transparentXAxisMaterial.setSpecularColor(Color.rgb(0, 0, 0, opq + 0.05));
                transparentYAxisMaterial.setDiffuseColor(Color.rgb(0, 0, 0, opq + 0.05));
                transparentYAxisMaterial.setSpecularColor(Color.rgb(0, 0, 0, opq + 0.05));
                if (opq == 0) {
                    transparentXAxisMaterial.setDiffuseColor(Color.rgb(0, 0, 0, opq));
                    transparentXAxisMaterial.setSpecularColor(Color.rgb(0, 0, 0, opq));
                    transparentYAxisMaterial.setDiffuseColor(Color.rgb(0, 0, 0, opq));
                    transparentYAxisMaterial.setSpecularColor(Color.rgb(0, 0, 0, opq));
                } else if (opq == 1) {
                    transparentXAxisMaterial.setDiffuseColor(Color.rgb(0, 0, 0, opq));
                    transparentXAxisMaterial.setSpecularColor(Color.rgb(0, 0, 0, opq));
                    transparentYAxisMaterial.setDiffuseColor(Color.rgb(0, 0, 0, opq));
                    transparentYAxisMaterial.setSpecularColor(Color.rgb(0, 0, 0, opq));
                }
                    
            }
        } catch (Exception ex) {
            
        }
    };
    
    ChangeListener<Boolean> changeListener = (v, oldValue, newValue) -> {
        if (newValue) {
                if (!processCrossSection.isRunning()) {
                    A = CrossSection.getA();
                    B = CrossSection.getB();
                    C = CrossSection.getC();
                    D = CrossSection.getD();
                    crossRotX = Double.parseDouble(CrossSection.getXRotate());
                    crossRotY = Double.parseDouble(CrossSection.getYRotate());
                    crossPosX = Double.parseDouble(CrossSection.getXCoordinate());
                    crossPosY = Double.parseDouble(CrossSection.getYCoordinate());
                    crossPosZ = Double.parseDouble(CrossSection.getZCoordinate());
                    processCrossSection.restart();
                }
            //
            CrossSection.setChanged(false);
        }
    };
    
    ChangeListener<Boolean> updateListener = (v, oldValue, newValue) -> {
        if (newValue) {
            if (!processCrossSection.isRunning()) {
                processCrossSection.restart();
            }
        }
    };
    
    private void addCrossSectionListener() {
        processCrossSection.setExecutor(Executors.newFixedThreadPool(1, runnable -> {
            Thread t = new Thread(runnable);
            t.setDaemon(true);
            return t ;
        }));
        CrossSection.xRotateProperty().addListener(crossSectionXRotListener);
        CrossSection.yRotateProperty().addListener(crossSectionYRotListener);
        CrossSection.xCoordinateProperty().addListener(crossSectionXPosListener);
        CrossSection.yCoordinateProperty().addListener(crossSectionYPosListener);
        CrossSection.zCoordinateProperty().addListener(crossSectionZPosListener);
        CrossSection.opaquenessProperty().addListener(opaquenessListener);
        CrossSection.changedProperty().addListener(changeListener);
        CrossSection.initialized.addListener(updateListener);
    }
    
    private void removeCrossSectionListener() {
        CrossSection.xRotateProperty().removeListener(crossSectionXRotListener);
        CrossSection.yRotateProperty().removeListener(crossSectionYRotListener);
        CrossSection.xCoordinateProperty().removeListener(crossSectionXPosListener);
        CrossSection.yCoordinateProperty().removeListener(crossSectionYPosListener);
        CrossSection.zCoordinateProperty().removeListener(crossSectionZPosListener);
        CrossSection.opaquenessProperty().removeListener(opaquenessListener);
        CrossSection.changedProperty().removeListener(changeListener);
        CrossSection.initialized.removeListener(updateListener);
    }
    
    ChangeListener<String> groupXRotListener = (v, oldValue, newValue) -> {
        if (startProcess) {
            try {
                double rot = Double.parseDouble(newValue);
                rotateXShapeGroup.setAngle(rot);
                if (!process.isRunning()) {
                    angXSin = Math.sin(Math.toRadians(rot));
                    angXCos = Math.cos(Math.toRadians(rot));
                    process.restart();
                }
            } catch (Exception ex) {

            }
        }
    };
    
    ChangeListener<String> groupYRotListener = (v, oldValue, newValue) -> {
        if (startProcess) {
            try {
                double rot = Double.parseDouble(newValue);
                rotateYShapeGroup.setAngle(rot);
                if (!process.isRunning()) {
                    angYSin = Math.sin(Math.toRadians(rot));
                    angYCos = Math.cos(Math.toRadians(rot));
                    process.restart();
                }
            } catch (Exception ex) {

            }
        }
    };
    
    ChangeListener<String> groupXPosListener = (v, oldValue, newValue) -> {
        if (startProcess) {
            try {
                double pos = Double.parseDouble(newValue);
                shapeGroup.setTranslateX(pos);
                shapeGroupAxisGroup.setTranslateX(pos);
                if (!process.isRunning()) {
                    trX = pos;
                    process.restart();
                }
            } catch (Exception ex) {

            }
        }
    };
    
    ChangeListener<String> groupYPosListener = (v, oldValue, newValue) -> {
        if (startProcess) {
            try {
                double pos = Double.parseDouble(newValue);
                shapeGroup.setTranslateY(pos);
                shapeGroupAxisGroup.setTranslateY(pos);
                if (!process.isRunning()) {
                    trY = pos;
                    process.restart();
                }
            } catch (Exception ex) {

            }
        }
    };
    
    ChangeListener<String> groupZPosListener = (v, oldValue, newValue) -> {
        if (startProcess) {
            try {
                double pos = Double.parseDouble(newValue);
                shapeGroup.setTranslateZ(pos);
                shapeGroupAxisGroup.setTranslateZ(pos);
                if (!process.isRunning()) {
                    trZ = pos;
                    process.restart();
                }
            } catch (Exception ex) {

            }
        }
    };
    
    private void addGroupPositionListener() {
        GroupPosition.xRotateProperty().addListener(groupXRotListener);
        GroupPosition.yRotateProperty().addListener(groupYRotListener);
        GroupPosition.xCoordinateProperty().addListener(groupXPosListener);
        GroupPosition.yCoordinateProperty().addListener(groupYPosListener);
        GroupPosition.zCoordinateProperty().addListener(groupZPosListener);
    }
    
    private void removeGroupPositionListener() {
        GroupPosition.xRotateProperty().removeListener(groupXRotListener);
        GroupPosition.yRotateProperty().removeListener(groupYRotListener);
        GroupPosition.xCoordinateProperty().removeListener(groupXPosListener);
        GroupPosition.yCoordinateProperty().removeListener(groupYPosListener);
        GroupPosition.zCoordinateProperty().removeListener(groupZPosListener);
    }
    
    @Override
    public void componentOpened() {
        addCameraViewListener();
        addCrossSectionListener();
        addGroupPositionListener();
        hm = Lookup.getDefault().lookup(HistionManager.class);
        if (hm == null) {
            LifecycleManager.getDefault().exit();
        }
        hm.getAllHistions().forEach(h -> {
            h.getItemMap().addListener(cellListener);
        });
        hm.addListener(histionListener);
        
        if (hm.getHistionMap().isEmpty())
            hm.addHistion(new Histion("Main histion",0,0,0));
    }

    @Override
    public void componentClosed() {
        removeCameraViewListener();
        removeCrossSectionListener();
        removeGroupPositionListener();
        hm.getAllHistions().forEach(h -> {
            h.getItemMap().removeListener(cellListener);
        });
        hm.removeListener(histionListener);
        // TODO add custom code on component closing
    }

    void writeProperties(java.util.Properties p) {
        // better to version settings since initial version as advocated at
        // http://wiki.apidesign.org/wiki/PropertyFiles
        p.setProperty("version", "1.0");
        // TODO store your settings
    }

    void readProperties(java.util.Properties p) {
        String version = p.getProperty("version");
        // TODO read your settings according to their version
    }
}
