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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.geometry.Point3D;
import javafx.scene.shape.Polygon;
import org.openide.LifecycleManager;
import org.spbu.histology.cross.section.viewer.CrossSectionViewerTopComponent;
import org.spbu.histology.model.Cell;
import org.spbu.histology.model.CrossSection;
import org.spbu.histology.model.Histion;
import org.spbu.histology.model.HistionManager;
import org.spbu.histology.model.TetgenPoint;
import org.spbu.histology.fxyz.Text3DMesh;
import org.spbu.histology.model.AlertBox;
import org.spbu.histology.model.HideCells;
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
    
    private Box crossSectionPlane;
    
    private double mousePosX;
    private double mousePosY;
    private double mouseOldX;
    private double mouseOldY;
    
    private Rotate rotateXCam = new Rotate(0, Rotate.X_AXIS);
    private Rotate rotateYCam = new Rotate(0, Rotate.Y_AXIS);
    
    private Rotate rotateXCrossSection = new Rotate(0, Rotate.X_AXIS);
    private Rotate rotateYCrossSection = new Rotate(0, Rotate.Y_AXIS);
    
    private JFXPanel fxPanel;
    private Scene scene;
    private PerspectiveCamera camera;
    private Group root = new Group();
    private Group shapeGroup = new Group();
    private final double fov = 35.0;
    private final double nearClip = 0.1;
    private final double farClip = 4000.0;  
    private final double axisLen = 1900.0;
    private final double camPosLim = 2000;
    private final double crossSectSize = 3000;
    private final double crossSectPosLim = 900;
    private final PhongMaterial transparentMaterial = new PhongMaterial();
    private final PhongMaterial transparentXAxisMaterial = new PhongMaterial();
    private final PhongMaterial transparentYAxisMaterial = new PhongMaterial();
    
    private Box crossSectionXAxis;
    private Box crossSectionYAxis;
    
    private final ObservableMap<Integer, TriangleMesh> meshMap = 
            FXCollections.observableMap(new ConcurrentHashMap());
    
    private final ObservableMap<Integer, MeshView> shapeMap = 
            FXCollections.observableMap(new ConcurrentHashMap());
    private final ObservableMap<Integer, Color> colorsList = 
            FXCollections.observableMap(new ConcurrentHashMap());
    private final ObservableMap<Integer, double[]> nodesList = 
            FXCollections.observableMap(new ConcurrentHashMap());
    private final ObservableMap<Integer, int[]> tetrahedronsList = 
            FXCollections.observableMap(new ConcurrentHashMap());
    private final ObservableMap<Integer, int[]> facesList = 
            FXCollections.observableMap(new ConcurrentHashMap());
    private final ObservableMap<Integer, ArrayList<Polygon>> polygonList = 
            FXCollections.observableMap(new ConcurrentHashMap());
    
    private final double EPS = 0.0000001;
    
    private final MapChangeListener<Integer, Cell> cellListener =
            (change) -> {
                if (change.wasRemoved() && change.wasAdded()) {
                    Cell c = (Cell)change.getValueAdded();
                    if (c.getShow()) {
                        Cell removedShape = (Cell)change.getValueRemoved();
                        if (shapeMap.get(removedShape.getId()) != null) {
                            CrossSectionViewerTopComponent.clearPolygonArray(polygonList.get(c.getId()));
                            shapeGroup.getChildren().remove(shapeMap.get(removedShape.getId()));
                        }
                        addCell(c);
                    }
                }
                else if (change.wasRemoved()) {  
                    Cell removedCell = (Cell)change.getValueRemoved();
                    if (removedCell.getShow()) {
                        Integer removedCellId = removedCell.getId();
                        shapeGroup.getChildren().remove(shapeMap.get(removedCellId));
                        shapeMap.remove(removedCellId);
                        nodesList.remove(removedCellId);
                        tetrahedronsList.remove(removedCellId);
                        facesList.remove(removedCellId);
                        colorsList.remove(removedCellId);
                        CrossSectionViewerTopComponent.clearPolygonArray(polygonList.get(removedCellId));
                        polygonList.remove(removedCellId);
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
        fxPanel = new JFXPanel();
        add(fxPanel, BorderLayout.CENTER);
        Platform.setImplicitExit(false);
        
        Platform.runLater(() -> {
            try {
                createScene();
            } finally {
                latch.countDown();
            }
        });
        
        try {
            latch.await();
            CameraView.setCamera("0", "0", "0", "0", "-1000", String.valueOf(fov));
        } catch (InterruptedException ex) {
            LifecycleManager.getDefault().exit();
        }
    }
    
    private void createScene() {
        root.getChildren().add(shapeGroup);
        scene = new Scene(root, 1000, 1000, true, SceneAntialiasing.BALANCED);
        
        buildAxes();
        buildCamera();
        handleKeyboard();
        handleMouseEvents();       
        
        hm = Lookup.getDefault().lookup(HistionManager.class);
        if (hm == null) {
            LifecycleManager.getDefault().exit();
        }
        hm.getAllHistions().forEach(h -> {
            h.getItemMap().addListener(cellListener);
        });
        hm.addListener(histionListener);
        
        if (hm.getHistionMap().isEmpty())
            hm.addHistion(new Histion("Main histion",0,0,0,0,0));
        
        buildCrossSectionPlane();
        fxPanel.setScene(scene);  
    }
    
    private void addPolygon(ArrayList<Point3D> pl, ArrayList<Polygon> polList, Integer id) {
        Double[] polPoints = new Double[pl.size() * 2];
        int k = 0;
        for (int i = 0; i < pl.size(); i++) {
            polPoints[k] = pl.get(i).getX();
            polPoints[k + 1] = pl.get(i).getZ();
            k += 2;
        }
        Polygon polygon = new Polygon();
        polygon.getPoints().addAll(polPoints);
        polygon.setFill(colorsList.get(id));
        polList.add(polygon);
    }
    
    private void findPolygons(ArrayList<TwoPoints> lineList, Integer id) {
        ArrayList<Polygon> polList = new ArrayList<>();
        Point3D p;
        ArrayList<Point3D> pl = new ArrayList<>();
        pl.add(lineList.get(0).getPoint1());
        p = lineList.get(0).getPoint1();
        lineList.remove(0);
        boolean stop = false;
        while (!lineList.isEmpty()) {
            stop = true;
            for (int i = 0; i < lineList.size(); i++) {
                if (p.distance(lineList.get(i).getPoint1()) < 0.0001) {
                    pl.add(lineList.get(i).getPoint2());
                    p = lineList.get(i).getPoint2();
                    lineList.remove(i);
                    stop = false;
                    break;
                }
                if (p.distance(lineList.get(i).getPoint2()) < 0.0001) {
                    pl.add(lineList.get(i).getPoint1());
                    p = lineList.get(i).getPoint1();
                    lineList.remove(i);
                    stop = false;
                    break;
                }
            }
            if (stop && (!lineList.isEmpty())) {
                addPolygon(pl, polList, id);
                pl.clear();
                pl.add(lineList.get(0).getPoint1());
                p = lineList.get(0).getPoint1();
                lineList.remove(0);
            }
        }
        addPolygon(pl, polList, id);
        polygonList.put(id, polList);
    }
    
    private void intersectionsWithEdges(Integer id) {
        ArrayList<ArrayList<Point3D>> points = new ArrayList<>();
        ArrayList<Point3D> intersectionNodes = new ArrayList<>();
        ArrayList<TwoPoints> excludedNodes = new ArrayList<>();
        ArrayList<TwoPoints> includedNodes = new ArrayList<>();
        polygonList.put(id, new ArrayList<>());
        
        double[] nl = nodesList.get(id);
        int[] tl = tetrahedronsList.get(id);
        for (int i = 0; i < tl.length; i += 4) {
            Point3D p1 = new Point3D(nl[(tl[i] - 1) * 3], nl[(tl[i] - 1) * 3 + 1], nl[(tl[i] - 1) * 3 + 2]);
            Point3D p2 = new Point3D(nl[(tl[i + 1] - 1) * 3], nl[(tl[i + 1] - 1) * 3 + 1], nl[(tl[i + 1] - 1) * 3 + 2]);
            Point3D p3 = new Point3D(nl[(tl[i + 2] - 1) * 3], nl[(tl[i + 2] - 1) * 3 + 1], nl[(tl[i + 2] - 1) * 3 + 2]);
            Point3D p4 = new Point3D(nl[(tl[i + 3] - 1) * 3], nl[(tl[i + 3] - 1) * 3 + 1], nl[(tl[i + 3] - 1) * 3 + 2]);
            intersectionNodes.clear();
            findIntersection(p1, p2, intersectionNodes);
            findIntersection(p1, p3, intersectionNodes);
            findIntersection(p1, p4, intersectionNodes);
            findIntersection(p3, p2, intersectionNodes);
            findIntersection(p4, p2, intersectionNodes);
            findIntersection(p3, p4, intersectionNodes);
            
            if (intersectionNodes.size() > 2) {
                rotateTillHorizontalPanel(intersectionNodes);
                if (intersectionNodes.size() == 4) {
                    double avgX = (intersectionNodes.get(0).getX() + intersectionNodes.get(1).getX() +
                        intersectionNodes.get(2).getX() + intersectionNodes.get(3).getX()) / 4;
            
                    double avgZ = (intersectionNodes.get(0).getZ() + intersectionNodes.get(1).getZ() +
                        intersectionNodes.get(2).getZ() + intersectionNodes.get(3).getZ()) / 4;
                    Collections.sort(intersectionNodes, (Point3D o1, Point3D o2) -> {
                        double temp1 = Math.atan2(o1.getZ() - avgZ, o1.getX() - avgX);
                        double temp2 = Math.atan2(o2.getZ() - avgZ, o2.getX() - avgX);
                        if(temp1 == temp2)
                            return 0;
                        return temp1 < temp2 ? -1 : 1;
                    }); 
                    ArrayList<Point3D> temp = new ArrayList<>();
                    temp.add(intersectionNodes.get(0));
                    temp.add(intersectionNodes.get(1));
                    temp.add(intersectionNodes.get(2));
                    points.add(new ArrayList<>(temp));
                    temp = new ArrayList<>();
                    temp.add(intersectionNodes.get(0));
                    temp.add(intersectionNodes.get(3));
                    temp.add(intersectionNodes.get(2));
                    points.add(new ArrayList<>(temp));
                } else {
                    points.add(new ArrayList<>(intersectionNodes));
                }
            }
        }
        for (int i = 0; i < points.size(); i++) {
            for (int j = i + 1; j < points.size(); j++) {
                if (((points.get(i).get(0).distance(points.get(j).get(0)) < 0.0001) &&
                       (points.get(i).get(1).distance(points.get(j).get(1)) < 0.0001) &&
                       (points.get(i).get(2).distance(points.get(j).get(2)) < 0.0001)) ||
                    ((points.get(i).get(0).distance(points.get(j).get(0)) < 0.0001) &&
                       (points.get(i).get(1).distance(points.get(j).get(2)) < 0.0001) &&
                       (points.get(i).get(2).distance(points.get(j).get(1)) < 0.0001)) ||
                   ((points.get(i).get(0).distance(points.get(j).get(2)) < 0.0001) &&
                       (points.get(i).get(1).distance(points.get(j).get(1)) < 0.0001) &&
                       (points.get(i).get(2).distance(points.get(j).get(0)) < 0.0001)) ||
                   ((points.get(i).get(0).distance(points.get(j).get(1)) < 0.0001) &&
                       (points.get(i).get(1).distance(points.get(j).get(2)) < 0.0001) &&
                       (points.get(i).get(2).distance(points.get(j).get(0)) < 0.0001)) ||
                   ((points.get(i).get(0).distance(points.get(j).get(1)) < 0.0001) &&
                       (points.get(i).get(1).distance(points.get(j).get(0)) < 0.0001) &&
                       (points.get(i).get(2).distance(points.get(j).get(2)) < 0.0001)) ||
                   ((points.get(i).get(0).distance(points.get(j).get(2)) < 0.0001) &&
                       (points.get(i).get(1).distance(points.get(j).get(0)) < 0.0001) &&
                       (points.get(i).get(2).distance(points.get(j).get(1)) < 0.0001)))
                    continue;
                    
                if (isOneSide(points.get(i).get(0), points.get(i).get(1),
                        points.get(j).get(0), points.get(j).get(1))) {
                    TwoPoints tp = new TwoPoints(points.get(i).get(0), points.get(i).get(1));
                    if (!excludedNodes.contains(tp))
                        excludedNodes.add(tp);
                }
                if (isOneSide(points.get(i).get(0), points.get(i).get(1),
                        points.get(j).get(0), points.get(j).get(2))) {
                    TwoPoints tp = new TwoPoints(points.get(i).get(0), points.get(i).get(1));
                    if (!excludedNodes.contains(tp))
                        excludedNodes.add(tp);
                }
                if (isOneSide(points.get(i).get(0), points.get(i).get(1),
                        points.get(j).get(2), points.get(j).get(1))) {
                    TwoPoints tp = new TwoPoints(points.get(i).get(0), points.get(i).get(1));
                    if (!excludedNodes.contains(tp))
                        excludedNodes.add(tp);
                }
                    
                if (isOneSide(points.get(i).get(0), points.get(i).get(2),
                        points.get(j).get(0), points.get(j).get(1))) {
                    TwoPoints tp = new TwoPoints(points.get(i).get(0), points.get(i).get(2));
                    if (!excludedNodes.contains(tp))
                        excludedNodes.add(tp);
                }
                if (isOneSide(points.get(i).get(0), points.get(i).get(2),
                            points.get(j).get(0), points.get(j).get(2))) {
                    TwoPoints tp = new TwoPoints(points.get(i).get(0), points.get(i).get(2));
                    if (!excludedNodes.contains(tp))
                        excludedNodes.add(tp);
                }
                if (isOneSide(points.get(i).get(0), points.get(i).get(2),
                        points.get(j).get(2), points.get(j).get(1))) {
                    TwoPoints tp = new TwoPoints(points.get(i).get(0), points.get(i).get(2));
                    if (!excludedNodes.contains(tp))
                        excludedNodes.add(tp);
                }
                    
                if (isOneSide(points.get(i).get(2), points.get(i).get(1),
                        points.get(j).get(0), points.get(j).get(1))) {
                    TwoPoints tp = new TwoPoints(points.get(i).get(1), points.get(i).get(2));
                    if (!excludedNodes.contains(tp))
                        excludedNodes.add(tp);
                }
                if (isOneSide(points.get(i).get(2), points.get(i).get(1),
                        points.get(j).get(0), points.get(j).get(2))) {
                    TwoPoints tp = new TwoPoints(points.get(i).get(1), points.get(i).get(2));
                    if (!excludedNodes.contains(tp))
                        excludedNodes.add(tp);
                }
                if (isOneSide(points.get(i).get(2), points.get(i).get(1),
                        points.get(j).get(2), points.get(j).get(1))) {
                    TwoPoints tp = new TwoPoints(points.get(i).get(1), points.get(i).get(2));
                    if (!excludedNodes.contains(tp))
                        excludedNodes.add(tp);
                }
            }
        }
        for (int i = 0; i < points.size(); i++) {
            TwoPoints tp = new TwoPoints(points.get(i).get(0), points.get(i).get(1));
            if (!excludedNodes.contains(tp))
                if (!includedNodes.contains(tp))
                    includedNodes.add(tp);
            tp = new TwoPoints(points.get(i).get(0), points.get(i).get(2));
            if (!excludedNodes.contains(tp))
                if (!includedNodes.contains(tp))
                    includedNodes.add(tp);
            tp = new TwoPoints(points.get(i).get(2), points.get(i).get(1));
            if (!excludedNodes.contains(tp))
                if (!includedNodes.contains(tp))
                    includedNodes.add(tp);
        }
        
        if (includedNodes.size() > 0)
            findPolygons(includedNodes, id);
        
        CrossSectionViewerTopComponent.show(polygonList.get(id));
    }
    
    private boolean isOneSide(Point3D p1, Point3D p2, Point3D p3, Point3D p4) {
        if ((p1.distance(p3) < 0.0001) && (p2.distance(p4) < 0.0001))
            return true;
        if ((p1.distance(p4) < 0.0001) && (p2.distance(p3) < 0.0001))
            return true;
        return false;
    }
    
    private void findIntersection(final Point3D p1, final Point3D p2, ArrayList<Point3D> intersectionNodes) {
        double firstEquation = CrossSection.getA() * p1.getX() + CrossSection.getB() * p1.getY() + CrossSection.getC() * p1.getZ() + CrossSection.getD();
        double secondEquation = CrossSection.getA() * p2.getX() + CrossSection.getB() * p2.getY() + CrossSection.getC() * p2.getZ() + CrossSection.getD();
        double absValueOfFirstEquation = Math.abs(firstEquation);
        double absValueOfSecondEquation = Math.abs(secondEquation);
        if ((absValueOfFirstEquation < EPS) && (absValueOfSecondEquation < EPS)) {
            if (!intersectionNodes.contains(p1))
                intersectionNodes.add(p1);
            if (!intersectionNodes.contains(p2))
                intersectionNodes.add(p2);
        } else if (absValueOfFirstEquation < EPS) {
            if (!intersectionNodes.contains(p1))
                intersectionNodes.add(p1);
        } else if (absValueOfSecondEquation < EPS) {
            if (!intersectionNodes.contains(p2))
                intersectionNodes.add(p2);
        } else if (firstEquation * secondEquation < 0) {
            double a1 = p2.getX() - p1.getX();
            double a2 = p2.getY() - p1.getY();
            double a3 = p2.getZ() - p1.getZ();
            double t = 1 / (secondEquation - firstEquation) * (-1) * firstEquation;
            double x = a1 * t + p1.getX();
            double y = a2 * t + p1.getY();
            double z = a3 * t + p1.getZ();
            intersectionNodes.add(new Point3D(x, y, z));
        }
    }
    
    private void rotateTillHorizontalPanel(ArrayList<Point3D> intersectionNodes) {
        try {
            double angX = -Math.toRadians(Double.parseDouble(CrossSection.getXRotate()));
            double angY = -Math.toRadians(Double.parseDouble(CrossSection.getYRotate()));
            double cosAngX = Math.cos(angX);
            double cosAngY = Math.cos(angY);
            double sinAngX = Math.sin(angX);
            double sinAngY = Math.sin(angY);
            double xCoord = Double.parseDouble(CrossSection.getXCoordinate());
            double yCoord = Double.parseDouble(CrossSection.getYCoordinate());
            double zCoord = Double.parseDouble(CrossSection.getZCoordinate());
            double x,y,z,temp;

            for (int i = 0; i < intersectionNodes.size(); i++) {
                x = intersectionNodes.get(i).getX() - xCoord;
                y = intersectionNodes.get(i).getY() - yCoord;
                z = intersectionNodes.get(i).getZ() - zCoord;
                
                temp = x;
                x = x * cosAngY + z * sinAngY;
                z = -temp * sinAngY + z * cosAngY;
                
                z = y * sinAngX + z * cosAngX;
                
                temp = x;
                x = x * cosAngY + z * (-sinAngY);
                z = -temp * (-sinAngY) + z * cosAngY;
                
                intersectionNodes.set(i, new Point3D(x + xCoord, 0, z + zCoord));
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
        ObservableList<TetgenPoint> pointData = FXCollections.observableArrayList();
        
        double xRot = c.getXRotate();
        double yRot = c.getYRotate();
        double xTran = c.getXCoordinate();
        double yTran = c.getYCoordinate();
        double zTran = c.getZCoordinate();
        
        dataSize = 0;
        nodeAvg = new Point3D(0, 0, 0);
        hm.getHistionMap().get(c.getHistionId()).getItemMap().get(c.getId()).getItems().forEach(p -> {
            System.out.println(p.getId() + " " + p.getName());
            for (TetgenPoint point : p.getPointData()) {
                pointData.add(new TetgenPoint(point));
                nodeAvg = new Point3D(nodeAvg.getX() + point.getX(), nodeAvg.getY() + point.getY(), nodeAvg.getZ() + point.getZ());
            }
            dataSize += p.getPointData().size();
        });
        nodeAvg = new Point3D(nodeAvg.getX() / dataSize, nodeAvg.getY() / dataSize, nodeAvg.getZ() / dataSize);
        
        applyTransformations(xRot, yRot, xTran, yTran, zTran, nodeAvg, pointData);
        
        c.setTransformedPointData(pointData);
        
        dataSize = 0;
        nodeAvg = new Point3D(0, 0, 0);
        Histion h = hm.getHistionMap().get(c.getHistionId());
        h.getItems().forEach(cell -> {
            if (cell.getShow()) {
                for (TetgenPoint point : cell.getTransformedPointData()) {
                    nodeAvg = new Point3D(nodeAvg.getX() + point.getX(), nodeAvg.getY() + point.getY(), nodeAvg.getZ() + point.getZ());
                }
                dataSize += cell.getTransformedPointData().size();
            }
        });
        nodeAvg = new Point3D(nodeAvg.getX() / dataSize, nodeAvg.getY() / dataSize, nodeAvg.getZ() / dataSize);
        xRot = h.getXRotate();
        yRot = h.getYRotate();
        xTran = h.getXCoordinate();
        yTran = h.getYCoordinate();
        zTran = h.getZCoordinate();
        applyTransformations(xRot, yRot, xTran, yTran, zTran, nodeAvg, pointData);
        
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
                //vertexList[count] = facetData.get(i).getVertex(j + 1);
                vertexList[count] = facetData.get(i).get(j);
                count ++;
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
        tetrahedronsList.put(c.getId(), new int[tr.getTetrahedronList().length]);
        facesList.put(c.getId(), new int[tr.getFaceList().length]);
        System.arraycopy(tr.getNodeList(), 0, nodesList.get(c.getId()), 0, tr.getNodeList().length);
        System.arraycopy(tr.getTetrahedronList(), 0, tetrahedronsList.get(c.getId()), 0, tr.getTetrahedronList().length);
        System.arraycopy(tr.getFaceList(), 0, facesList.get(c.getId()), 0, tr.getFaceList().length);
        
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
        shapeMap.put(c.getId(), shape);
        hm.getHistionMap().get(0).setShapeMap(shapeMap);
        intersectionsWithEdges(c.getId());  
    }
    
    private void buildCrossSectionPlane() {
        crossSectionPlane = new Box(crossSectSize, 1, crossSectSize);
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

        crossSectionXAxis = new Box(crossSectSize, 1, 1);
        crossSectionYAxis = new Box(1, crossSectSize, 1);
        
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
    }
    
    private void buildCamera() {
        camera = new PerspectiveCamera(true);
        camera.setTranslateZ(-1000);
        camera.setNearClip(nearClip);
        camera.setFarClip(farClip);
        camera.setFieldOfView(fov);
        scene.setCamera(camera);          
    }
    
    private void handleMouseEvents() {
        
        camera.getTransforms().clear();
        camera.getTransforms().addAll(rotateYCam, rotateXCam);
        
        scene.setOnMousePressed(me -> {         
            mouseOldX = me.getSceneX();
            mouseOldY = me.getSceneY();
        });
        
        scene.setOnMouseDragged(me -> {
            
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
    
    private void handleKeyboard() {   
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
                    case Y:
                        CrossSectionViewerTopComponent.clearPolygonArray(polygonList.get(0));
                        CrossSectionViewerTopComponent.clearPolygonArray(polygonList.get(1));
                        break;
                    case U:
                        CrossSectionViewerTopComponent.clearPolygonArray(polygonList.get(2));
                        CrossSectionViewerTopComponent.clearPolygonArray(polygonList.get(3));
                        break;
                    case I:
                        CrossSectionViewerTopComponent.clearPolygonArray(polygonList.get(4));
                        CrossSectionViewerTopComponent.clearPolygonArray(polygonList.get(5));
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
            if ((pos <= camPosLim) && (pos >= -camPosLim))
                camera.setTranslateX(pos);
        } catch (Exception ex) {
            
        }
    };
    ChangeListener<String> yPosListener = (v, oldValue, newValue) -> {
        try {
            double pos = Double.parseDouble(newValue);
            if ((pos <= camPosLim) && (pos >= -camPosLim))
                camera.setTranslateY(pos);
        } catch (Exception ex) {
            
        }
    };
    ChangeListener<String> zPosListener = (v, oldValue, newValue) -> {
        try {
            double pos = Double.parseDouble(newValue);
            if ((pos <= camPosLim) && (pos >= -camPosLim))
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
                            if (n.equals(name))
                                shapeGroup.getChildren().remove(shapeMap.get(c.getId()));
                        });
                    });
                }
            } else {
                for (String name : change.getRemoved()) {
                    hm.getAllHistions().forEach(h -> {
                        h.getItems().forEach(c -> {
                            String n = c.getName();
                            n = n.substring(n.indexOf("<") + 1, n.lastIndexOf(">"));
                            if (n.equals(name))
                                shapeGroup.getChildren().add(shapeMap.get(c.getId()));
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
    
    ChangeListener<String> crossSectionXRotListener = (v, oldValue, newValue) -> {
        try {
            double ang = Double.parseDouble(newValue);
            if ((ang >= 0) && (ang <= 90)) {
                CrossSection.xRotateProperty().set(String.valueOf(ang));
            }       
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
            if ((pos <= crossSectPosLim) && (pos >= -crossSectPosLim)) {
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
            if ((pos <= crossSectPosLim) && (pos >= -crossSectPosLim)) {
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
            if ((pos <= crossSectPosLim) && (pos >= -crossSectPosLim)) {
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
            CrossSectionViewerTopComponent.clear();
            hm.getAllHistions().forEach(h -> {
                h.getItems().forEach(c -> {
                    if (c.getShow())
                        intersectionsWithEdges(c.getId());
                });
            });
            CrossSection.setChanged(false);
        }
    };
    
    ChangeListener<Boolean> updateListener = (v, oldValue, newValue) -> {
        if (newValue) {
            CrossSectionViewerTopComponent.clear();
            polygonList.clear();
            hm.getAllHistions().forEach(h -> {
                h.getItems().forEach(c -> {
                    if (c.getShow())
                        intersectionsWithEdges(c.getId());
                });
            });
        }
    };
    
    private void addCrossSectionListener() {
        CrossSection.xRotateProperty().addListener(crossSectionXRotListener);
        CrossSection.yRotateProperty().addListener(crossSectionYRotListener);
        CrossSection.xCoordinateProperty().addListener(crossSectionXPosListener);
        CrossSection.yCoordinateProperty().addListener(crossSectionYPosListener);
        CrossSection.zCoordinateProperty().addListener(crossSectionZPosListener);
        CrossSection.opaquenessProperty().addListener(opaquenessListener);
        CrossSection.changedProperty().addListener(changeListener);
        CrossSectionViewerTopComponent.initialized.addListener(updateListener);
    }
    
    private void removeCrossSectionListener() {
        CrossSection.xRotateProperty().removeListener(crossSectionXRotListener);
        CrossSection.yRotateProperty().removeListener(crossSectionYRotListener);
        CrossSection.xCoordinateProperty().removeListener(crossSectionXPosListener);
        CrossSection.yCoordinateProperty().removeListener(crossSectionYPosListener);
        CrossSection.zCoordinateProperty().removeListener(crossSectionZPosListener);
        CrossSection.opaquenessProperty().removeListener(opaquenessListener);
        CrossSection.changedProperty().removeListener(changeListener);
        CrossSectionViewerTopComponent.initialized.removeListener(updateListener);
    }
    
    @Override
    public void componentOpened() {
        addCameraViewListener();
        addCrossSectionListener();
    }

    @Override
    public void componentClosed() {
        removeCameraViewListener();
        removeCrossSectionListener();
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
