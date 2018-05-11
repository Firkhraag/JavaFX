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
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.concurrent.Task;
import javafx.geometry.Point3D;
import javafx.scene.CacheHint;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import org.openide.LifecycleManager;
//import org.spbu.histology.cross.section.viewer.CrossSectionViewerTopComponent;
import org.spbu.histology.fxyz.Line3D;
import org.spbu.histology.model.Cell;
import org.spbu.histology.model.CrossSection;
import org.spbu.histology.model.Histion;
import org.spbu.histology.model.HistionManager;
import org.spbu.histology.model.TetgenPoint;
import org.spbu.histology.fxyz.Text3DMesh;
import org.spbu.histology.model.AlertBox;
import org.spbu.histology.model.GroupTransforms;
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
    private Rotate rotateZShapeGroup = new Rotate(0, Rotate.Z_AXIS);
    
    private Rotate rotateXCrossSection = new Rotate(0, Rotate.X_AXIS);
    private Rotate rotateYCrossSection = new Rotate(0, Rotate.Y_AXIS);
    
    //private Scene scene;
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
    
    //private final ObservableMap<Integer, MeshView> shapeMap = 
    //        FXCollections.observableMap(new ConcurrentHashMap());
    
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
    
    private final ObservableMap<Integer, ArrayList<TwoPoints>> includedNodesMap =
            FXCollections.observableMap(new ConcurrentHashMap());
    
    /*private final ObservableMap<Integer, ArrayList<Polygon>> polygonList = 
            FXCollections.observableMap(new ConcurrentHashMap());
    private final ObservableMap<Integer, ArrayList<Line>> linesList = 
            FXCollections.observableMap(new ConcurrentHashMap());*/
    
    private final MapChangeListener<Integer, Cell> cellListener =
            (change) -> {
                if (change.wasRemoved() && change.wasAdded()) {
                    Cell c = (Cell)change.getValueAdded();
                    Cell removedShape = (Cell)change.getValueRemoved();
                    if (c.getShow()) {
                        //if (shapeMap.get(removedShape.getId()) != null) {
                        if (hm.getShapeMap().get(removedShape.getId()) != null) {
                            CrossSection.removePolygon(c.getId());
                            CrossSection.removeLine(c.getId());
                            //CrossSectionViewerTopComponent.clearPolygonArray(polygonList.get(c.getId()));
                            //CrossSectionViewerTopComponent.clearLineArray(linesList.get(c.getId()));
                            //shapeGroup.getChildren().remove(shapeMap.get(removedShape.getId()));
                            shapeGroup.getChildren().remove(hm.getShapeMap().get(removedShape.getId()));
                            for (Line3D l : lineMap.get(removedShape.getId())) {
                                shapeGroup.getChildren().remove(l.getMeshView());
                                //shapeGroup.getChildren().remove(l.getMeshView(2));
                                //shapeGroup.getChildren().remove(l.getMeshView(3));
                                //shapeGroup.getChildren().remove(l.getMeshView(4));
                            }
                            /*for (Line3D3 l : line3Map.get(removedShape.getId())) {
                                shapeGroup.getChildren().remove(l);
                            }
                            for (Line3D5 l : line5Map.get(removedShape.getId())) {
                                shapeGroup.getChildren().remove(l);
                            }*/
                        }
                        addCell(c);
                    } else if (!c.getShow() && removedShape.getShow()) {
                        Integer removedCellId = removedShape.getId();
                        //shapeGroup.getChildren().remove(shapeMap.get(removedCellId));
                        shapeGroup.getChildren().remove(hm.getShapeMap().get(removedCellId));
                        for (Line3D l : lineMap.get(removedCellId)) {
                            shapeGroup.getChildren().remove(l.getMeshView());
                            //shapeGroup.getChildren().remove(l.getMeshView(2));
                            //shapeGroup.getChildren().remove(l.getMeshView(3));
                            //shapeGroup.getChildren().remove(l.getMeshView(4));
                        }
                        /*for (Line3D3 l : line3Map.get(removedCellId)) {
                            shapeGroup.getChildren().remove(l);
                        }
                        for (Line3D5 l : line5Map.get(removedCellId)) {
                            shapeGroup.getChildren().remove(l);
                        }*/
                        lineMap.remove(removedCellId);
                        /*line3Map.remove(removedCellId);
                        line5Map.remove(removedCellId);*/
                        //shapeMap.remove(removedCellId);
                        hm.getShapeMap().remove(removedCellId);
                        nodesList.remove(removedCellId);
                        
                        nodesListTemp.remove(removedCellId);
                        
                        tetrahedronsList.remove(removedCellId);
                        //facesList.remove(removedCellId);
                        colorsList.remove(removedCellId);
                        CrossSection.removePolygon(removedCellId);
                        CrossSection.removeLine(removedCellId);
                        //CrossSectionViewerTopComponent.clearPolygonArray(polygonList.get(removedCellId));
                        //CrossSectionViewerTopComponent.clearLineArray(linesList.get(removedCellId));
                        //polygonList.remove(removedCellId);
                        //linesList.remove(removedCellId);
                    }
                }
                else if (change.wasRemoved()) {  
                    Cell removedCell = (Cell)change.getValueRemoved();
                    if (removedCell.getShow()) {
                        Integer removedCellId = removedCell.getId();
                        //shapeGroup.getChildren().remove(shapeMap.get(removedCellId));
                        shapeGroup.getChildren().remove(hm.getShapeMap().get(removedCellId));
                        for (Line3D l : lineMap.get(removedCellId)) {
                            shapeGroup.getChildren().remove(l.getMeshView());
                            //shapeGroup.getChildren().remove(l.getMeshView(2));
                            //shapeGroup.getChildren().remove(l.getMeshView(3));
                            //shapeGroup.getChildren().remove(l.getMeshView(4));
                        }
                        /*for (Line3D3 l : line3Map.get(removedCellId)) {
                            shapeGroup.getChildren().remove(l);
                        }
                        for (Line3D5 l : line5Map.get(removedCellId)) {
                            shapeGroup.getChildren().remove(l);
                        }*/
                        lineMap.remove(removedCellId);
                        /*line3Map.remove(removedCellId);
                        line5Map.remove(removedCellId);*/
                        //shapeMap.remove(removedCellId);
                        hm.getShapeMap().remove(removedCellId);
                        nodesList.remove(removedCellId);
                        
                        nodesListTemp.remove(removedCellId);
                        
                        tetrahedronsList.remove(removedCellId);
                        //facesList.remove(removedCellId);
                        colorsList.remove(removedCellId);
                        CrossSection.removePolygon(removedCellId);
                        CrossSection.removeLine(removedCellId);
                        //CrossSectionViewerTopComponent.clearPolygonArray(polygonList.get(removedCellId));
                        //CrossSectionViewerTopComponent.clearLineArray(linesList.get(removedCellId));
                        //polygonList.remove(removedCellId);
                        //linesList.remove(removedCellId);
                    }
                }
                else if (change.wasAdded()) {
                    Cell addedCell = (Cell)change.getValueAdded();
                    //System.out.println("Cell was added");
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
                    //System.out.println("NOOOOOOO");
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
        
        //System.out.println(Runtime.getRuntime().freeMemory());
        
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
        
        //root.setCache(true);
        //root.setCacheHint(CacheHint.SPEED);
    }
    //Color c = Color.BLACK;
    //Color c2 = Color.YELLOW;
    private void addPolygon(ArrayList<Line> lineList, ArrayList<Node> pl, ArrayList<Polygon> polList, Integer id) {
        Double[] polPoints = new Double[pl.size() * 2];
        int k = 0;
        for (int i = 0; i < pl.size(); i++) {
            polPoints[k] = pl.get(i).x;
            polPoints[k + 1] = pl.get(i).z;
            k += 2;
            if (i != pl.size() - 1) {
                Line line = new Line();
                
                //line.setStroke(c);
                
                line.setStartX(pl.get(i).x);
                line.setStartY(pl.get(i).z);
                line.setEndX(pl.get(i + 1).x);
                line.setEndY(pl.get(i + 1).z);
                lineList.add(line);
            } else {
                Line line = new Line();
                
                //line.setStroke(c2);
                
                line.setStartX(pl.get(i).x);
                line.setStartY(pl.get(i).z);
                line.setEndX(pl.get(0).x);
                line.setEndY(pl.get(0).z);
                lineList.add(line);
            }
        }
        /*if (c == Color.BLACK) {
            c = Color.BLUE;
        } else if (c == Color.BLUE) {
            c = Color.BLACK;
        }*/
        Polygon polygon = new Polygon();
        polygon.getPoints().addAll(polPoints);
        polygon.setFill(colorsList.get(id));
        polList.add(polygon);
    }
    
    /*private void addPolygon(ArrayList<Line> lineList, ArrayList<Point3D> pl, ArrayList<Polygon> polList, Integer id) {
        Double[] polPoints = new Double[pl.size() * 2];
        int k = 0;
        for (int i = 0; i < pl.size(); i++) {
            polPoints[k] = pl.get(i).getX();
            polPoints[k + 1] = pl.get(i).getZ();
            k += 2;
            if (i != pl.size() - 1) {
                Line line = new Line();
                line.setStartX(pl.get(i).getX());
                line.setStartY(pl.get(i).getZ());
                line.setEndX(pl.get(i + 1).getX());
                line.setEndY(pl.get(i + 1).getZ());
                lineList.add(line);
            } else {
                Line line = new Line();
                line.setStartX(pl.get(i).getX());
                line.setStartY(pl.get(i).getZ());
                line.setEndX(pl.get(0).getX());
                line.setEndY(pl.get(0).getZ());
                lineList.add(line);
            }
        }
        Polygon polygon = new Polygon();
        polygon.getPoints().addAll(polPoints);
        polygon.setFill(colorsList.get(id));
        polList.add(polygon);
    }*/
    
    private void findPolygons(List<TwoPoints> lineList, Integer id) {
    //        ArrayList<Polygon> polList, ArrayList<Line> lList) {
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
        
        //polygonList.put(id, polList);
        //linesList.put(id, lList);
    }
    
    private void intersectionsWithEdges(Integer id) {
        List<List<Node>> points = new ArrayList<>();
        //ArrayList<Node> intersectionNodes = new ArrayList<>();
        List<TwoPoints> nodes = new ArrayList<>();
        
        //ArrayList<Polygon> polList = new ArrayList<>();
        //ArrayList<Line> lList = new ArrayList<>();
        
        double[] nl = nodesList.get(id);
        int[] tl = tetrahedronsList.get(id);
        for (int i = 0; i < tl.length; i += 4) {
            ArrayList<Node> intersectionNodes = new ArrayList<>();
            //intersectionNodes.clear();
            
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
                    
                    //ArrayList<Node> temp = new ArrayList<>();
                    boolean already = false;
                    //temp.add(intersectionNodes.get(0));
                    //temp.add(intersectionNodes.get(1));
                    //temp.add(intersectionNodes.get(2));
                    List<Node> temp = intersectionNodes.subList(0, 3);
                    for (List<Node> pl : points) {
                        if (pl.containsAll(temp) && temp.containsAll(pl)) {
                            //System.out.println("Found");
                            already = true;
                            break;
                        }
                    }
                    if (!already)
                        //points.add(new ArrayList<>(temp));
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
                        //points.add(new ArrayList<>(intersectionNodes));
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
        
        if (nodes.size() > 0) {
            findPolygons(nodes, id);
        } else {
            CrossSection.addPolygon(id, new ArrayList<>());
            CrossSection.addLine(id, new ArrayList<>());
        }
    }
    
    private void findIntersection(final Node p1, final Node p2, final Node p3, final Node p4, ArrayList<Node> intersectionNodes) {
        double firstEquation = CrossSection.getA() * p1.x + CrossSection.getB() * p1.y + CrossSection.getC() * p1.z + CrossSection.getD();
        double secondEquation = CrossSection.getA() * p2.x + CrossSection.getB() * p2.y + CrossSection.getC() * p2.z + CrossSection.getD();
        double thirdEquation = CrossSection.getA() * p3.x + CrossSection.getB() * p3.y + CrossSection.getC() * p3.z + CrossSection.getD();
        double fourthEquation = CrossSection.getA() * p4.x + CrossSection.getB() * p4.y + CrossSection.getC() * p4.z + CrossSection.getD();
        
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
        /*if (intersectionNodes.size() == 3) {
            return;
        }
        if (intersectionNodes.isEmpty()) {*/
        if (intersectionNodes.size() != 3) {
            if ((!first) && (!second)) {
                if (firstEquation * secondEquation < 0) {
                    double a1 = p2.x - p1.x;
                    double a2 = p2.y - p1.y;
                    double a3 = p2.z - p1.z;
                    double t = 1 / (secondEquation - firstEquation) * (-1) * firstEquation;
                    /*double x = a1 * t + p1.x;
                    double y = a2 * t + p1.y;
                    double z = a3 * t + p1.z;*/
                    //System.out.println("Inter4");
                    //System.out.println(x + " " + y + " " + z);
                    intersectionNodes.add(new Node(a1 * t + p1.x, a2 * t + p1.y, a3 * t + p1.z));
                }
            }
            if ((!first) && (!third)) {
                if (firstEquation * thirdEquation < 0) {
                    double a1 = p3.x - p1.x;
                    double a2 = p3.y - p1.y;
                    double a3 = p3.z - p1.z;
                    double t = 1 / (thirdEquation - firstEquation) * (-1) * firstEquation;
                    /*double x = a1 * t + p1.x;
                    double y = a2 * t + p1.y;
                    double z = a3 * t + p1.z;*/
                    //System.out.println("Inter4");
                    //System.out.println(x + " " + y + " " + z);
                    intersectionNodes.add(new Node(a1 * t + p1.x, a2 * t + p1.y, a3 * t + p1.z));
                }
            }
            if ((!first) && (!fourth)) {
                if (firstEquation * fourthEquation < 0) {
                    double a1 = p4.x - p1.x;
                    double a2 = p4.y - p1.y;
                    double a3 = p4.z - p1.z;
                    double t = 1 / (fourthEquation - firstEquation) * (-1) * firstEquation;
                    /*double x = a1 * t + p1.x;
                    double y = a2 * t + p1.y;
                    double z = a3 * t + p1.z;*/
                    //System.out.println("Inter4");
                    //System.out.println(x + " " + y + " " + z);
                    intersectionNodes.add(new Node(a1 * t + p1.x, a2 * t + p1.y, a3 * t + p1.z));
                }
            }
            if ((!third) && (!second)) {
                if (thirdEquation * secondEquation < 0) {
                    double a1 = p2.x - p3.x;
                    double a2 = p2.y - p3.y;
                    double a3 = p2.z - p3.z;
                    double t = 1 / (secondEquation - thirdEquation) * (-1) * thirdEquation;
                    /*double x = a1 * t + p3.x;
                    double y = a2 * t + p3.y;
                    double z = a3 * t + p3.z;*/
                    //System.out.println("Inter4");
                    //System.out.println(x + " " + y + " " + z);
                    intersectionNodes.add(new Node(a1 * t + p3.x, a2 * t + p3.y, a3 * t + p3.z));
                }
            }
            if ((!fourth) && (!second)) {
                if (fourthEquation * secondEquation < 0) {
                    double a1 = p2.x - p4.x;
                    double a2 = p2.y - p4.y;
                    double a3 = p2.z - p4.z;
                    double t = 1 / (secondEquation - fourthEquation) * (-1) * fourthEquation;
                    //double x = a1 * t + p4.x;
                    //double y = a2 * t + p4.y;
                    //double z = a3 * t + p4.z;
                    //System.out.println("Inter4");
                    //System.out.println(x + " " + y + " " + z);
                    intersectionNodes.add(new Node(a1 * t + p4.x, a2 * t + p4.y, a3 * t + p4.z));
                }
            }
            if ((!third) && (!fourth)) {
                if (thirdEquation * fourthEquation < 0) {
                    double a1 = p4.x - p3.x;
                    double a2 = p4.y - p3.y;
                    double a3 = p4.z - p3.z;
                    double t = 1 / (fourthEquation - thirdEquation) * (-1) * thirdEquation;
                    /*double x = a1 * t + p3.x;
                    double y = a2 * t + p3.y;
                    double z = a3 * t + p3.z;*/
                    intersectionNodes.add(new Node(a1 * t + p3.x, a2 * t + p3.y, a3 * t + p3.z));
                }
            }
        }
        
        /*if ((absValueOfFirstEquation < 0.000001) && (absValueOfSecondEquation < 0.000001) && (absValueOfThirdEquation < 0.000001)) {
            intersectionNodes.add(p1);
            intersectionNodes.add(p2);
            intersectionNodes.add(p3);
        } else if ((absValueOfFirstEquation < 0.000001) && (absValueOfSecondEquation < 0.000001) && (absValueOfFourthEquation < 0.000001)) {
            intersectionNodes.add(p1);
            intersectionNodes.add(p2);
            intersectionNodes.add(p4);
        } else if ((absValueOfThirdEquation < 0.000001) && (absValueOfSecondEquation < 0.000001) && (absValueOfFourthEquation < 0.000001)) {
            intersectionNodes.add(p3);
            intersectionNodes.add(p2);
            intersectionNodes.add(p4);
        } else if ((absValueOfThirdEquation < 0.000001) && (absValueOfFirstEquation < 0.000001) && (absValueOfFourthEquation < 0.000001)) {
            intersectionNodes.add(p3);
            intersectionNodes.add(p1);
            intersectionNodes.add(p4);
        }
        
        if ((absValueOfFirstEquation < 0.000001) && (absValueOfSecondEquation < 0.000001)) {
            //System.out.println("Inter1");
            //if (!intersectionNodes.contains(p1))
            if (absValueOfThirdEquation < 0.000001) {
                intersectionNodes.add(p1);
                intersectionNodes.add(p2);
                intersectionNodes.add(p3);
            } else if (absValueOfFourthEquation < 0.000001) {
                intersectionNodes.add(p1);
                intersectionNodes.add(p2);
                intersectionNodes.add(p4);
            }
            //intersectionNodes.add(p1);
            //if (!intersectionNodes.contains(p2))
            //intersectionNodes.add(p2);
        } else if (absValueOfFirstEquation < 0.000001) {
            //System.out.println("Inter2");
            //if (!intersectionNodes.contains(p1))
            intersectionNodes.add(p1);
        } else if (absValueOfSecondEquation < 0.000001) {
            //System.out.println("Inter3");
            //if (!intersectionNodes.contains(p2))
            intersectionNodes.add(p2);
        } else if (firstEquation * secondEquation < 0) {
            double a1 = p2.x - p1.x;
            double a2 = p2.y - p1.y;
            double a3 = p2.z - p1.z;
            double t = 1 / (secondEquation - firstEquation) * (-1) * firstEquation;
            double x = a1 * t + p1.x;
            double y = a2 * t + p1.y;
            double z = a3 * t + p1.z;
            //System.out.println("Inter4");
            //System.out.println(x + " " + y + " " + z);
            intersectionNodes.add(new Node(x, y, z));
        }*/
    }
    
    /*private void findIntersection(final Node p1, final Node p2, ArrayList<Node> intersectionNodes) {
        double firstEquation = CrossSection.getA() * p1.x + CrossSection.getB() * p1.y + CrossSection.getC() * p1.z + CrossSection.getD();
        double secondEquation = CrossSection.getA() * p2.x + CrossSection.getB() * p2.y + CrossSection.getC() * p2.z + CrossSection.getD();
        double absValueOfFirstEquation = Math.abs(firstEquation);
        double absValueOfSecondEquation = Math.abs(secondEquation);
        if ((absValueOfFirstEquation < 0.000001) && (absValueOfSecondEquation < 0.000001)) {
            //System.out.println("Inter1");
            if (!intersectionNodes.contains(p1))
                intersectionNodes.add(p1);
            if (!intersectionNodes.contains(p2))
                intersectionNodes.add(p2);
        } else if (absValueOfFirstEquation < 0.000001) {
            //System.out.println("Inter2");
            if (!intersectionNodes.contains(p1))
                intersectionNodes.add(p1);
        } else if (absValueOfSecondEquation < 0.000001) {
            //System.out.println("Inter3");
            if (!intersectionNodes.contains(p2))
                intersectionNodes.add(p2);
        } else if (firstEquation * secondEquation < 0) {
            double a1 = p2.x - p1.x;
            double a2 = p2.y - p1.y;
            double a3 = p2.z - p1.z;
            double t = 1 / (secondEquation - firstEquation) * (-1) * firstEquation;
            double x = a1 * t + p1.x;
            double y = a2 * t + p1.y;
            double z = a3 * t + p1.z;
            //System.out.println("Inter4");
            //System.out.println(x + " " + y + " " + z);
            intersectionNodes.add(new Node(x, y, z));
        }
    }*/
    
    /*private void findIntersection(final Point3D p1, final Point3D p2, ArrayList<Point3D> intersectionNodes) {
        double firstEquation = CrossSection.getA() * p1.getX() + CrossSection.getB() * p1.getY() + CrossSection.getC() * p1.getZ() + CrossSection.getD();
        double secondEquation = CrossSection.getA() * p2.getX() + CrossSection.getB() * p2.getY() + CrossSection.getC() * p2.getZ() + CrossSection.getD();
        double absValueOfFirstEquation = Math.abs(firstEquation);
        double absValueOfSecondEquation = Math.abs(secondEquation);
        if ((absValueOfFirstEquation < 0.0001) && (absValueOfSecondEquation < 0.0001)) {
            //System.out.println("Inter1");
            if (!intersectionNodes.contains(p1))
                intersectionNodes.add(p1);
            if (!intersectionNodes.contains(p2))
                intersectionNodes.add(p2);
        } else if (absValueOfFirstEquation < 0.0001) {
            //System.out.println("Inter2");
            if (!intersectionNodes.contains(p1))
                intersectionNodes.add(p1);
        } else if (absValueOfSecondEquation < 0.0001) {
            //System.out.println("Inter3");
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
            System.out.println("Inter4");
            System.out.println(x + " " + y + " " + z);
            intersectionNodes.add(new Point3D(x, y, z));
        }
    }*/
    
    private void rotateTillHorizontalPanel(ArrayList<Node> intersectionNodes) {
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
            //double x,y,z,temp;

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
    
    /*private void rotateTillHorizontalPanel(ArrayList<Point3D> intersectionNodes) {
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
    }*/
    
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
                    //facesList.put(c.getId(), new int[facesList.get(cell.getId()).length]);
                    //double[] nodesArr = nodesList.get(cell.getId());
                    for (int i = 0; i < nodesList.get(cell.getId()).length; i += 3) {
                        /*double x = nodesList.get(cell.getId())[i];
                        double y = nodesList.get(cell.getId())[i + 1];
                        double z = nodesList.get(cell.getId())[i + 2];
                        System.out.println(x + " " + y + " " + z);
                        x += hm.getHistionMap().get(c.getHistionId()).getXCoordinate();
                        y += hm.getHistionMap().get(c.getHistionId()).getYCoordinate();
                        z += hm.getHistionMap().get(c.getHistionId()).getZCoordinate();*/
                        
                        /*nodesList.get(c.getId())[i] = nodesList.get(cell.getId())[i] +
                                hm.getHistionMap().get(c.getHistionId()).getXCoordinate();
                        nodesList.get(c.getId())[i + 1] = nodesList.get(cell.getId())[i + 1] +
                                hm.getHistionMap().get(c.getHistionId()).getYCoordinate();
                        nodesList.get(c.getId())[i + 2] = nodesList.get(cell.getId())[i + 2] +
                                hm.getHistionMap().get(c.getHistionId()).getZCoordinate();*/
                        
                        nodesListTemp.get(c.getId())[i] = nodesListTemp.get(cell.getId())[i] +
                                hm.getHistionMap().get(c.getHistionId()).getXCoordinate();
                        nodesListTemp.get(c.getId())[i + 1] = nodesListTemp.get(cell.getId())[i + 1] +
                                hm.getHistionMap().get(c.getHistionId()).getYCoordinate();
                        nodesListTemp.get(c.getId())[i + 2] = nodesListTemp.get(cell.getId())[i + 2] +
                                hm.getHistionMap().get(c.getHistionId()).getZCoordinate();
                    }
                    double ang = rotateXShapeGroup.getAngle() + mouseDeltaY * 0.1;
                    //double angRadX = Math.toRadians(ang);
                    double angXCos = Math.cos(Math.toRadians(ang));
                    double angXSin = Math.sin(Math.toRadians(ang));

                    ang = rotateYShapeGroup.getAngle() - mouseDeltaX * 0.1;
                    //double angRadY = Math.toRadians(ang);
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
                    //System.arraycopy(nodesList.get(cell.getId()), 0, nodesList.get(c.getId()), 0, nodesList.get(cell.getId()).length);
                    //System.arraycopy(tetrahedronsList.get(cell.getId()), 0, tetrahedronsList.get(c.getId()), 0, tetrahedronsList.get(cell.getId()).length);
                    //System.arraycopy(facesList.get(cell.getId()), 0, facesList.get(c.getId()), 0, facesList.get(cell.getId()).length);
                    /*for (int i = 0; i < nodesList.size(); i += 3) {
                        System.out.println(i);
                        nodesList.get(c.getId())[i] += hm.getHistionMap().get(c.getHistionId()).getXCoordinate();
                        nodesList.get(c.getId())[i + 1] += hm.getHistionMap().get(c.getHistionId()).getYCoordinate();
                        nodesList.get(c.getId())[i + 2] += hm.getHistionMap().get(c.getHistionId()).getZCoordinate();
                    }*/
                    
                    colorsList.put(c.getId(), c.getDiffuseColor());
                    //MeshView newMeshView = new MeshView(shapeMap.get(cell.getId()).getMesh());
                    MeshView newMeshView = new MeshView(hm.getShapeMap().get(cell.getId()).getMesh());
                    final PhongMaterial phongMaterial = new PhongMaterial();
                    phongMaterial.setDiffuseColor(c.getDiffuseColor());
                    phongMaterial.setSpecularColor(c.getSpecularColor());
                    newMeshView.setMaterial(phongMaterial);
                    newMeshView.setTranslateX(hm.getHistionMap().get(c.getHistionId()).getXCoordinate());
                    newMeshView.setTranslateY(hm.getHistionMap().get(c.getHistionId()).getYCoordinate());
                    newMeshView.setTranslateZ(hm.getHistionMap().get(c.getHistionId()).getZCoordinate());
                    shapeGroup.getChildren().add(newMeshView);
                    //shapeMap.put(c.getId(), newMeshView);
                    hm.getShapeMap().put(c.getId(), newMeshView);
                    
                    ArrayList<Line3D> lineArr = new ArrayList<>();
                    for (Line3D l :lineMap.get(cell.getId())) {
                        Line3D line = new Line3D(l);
                        line.getMeshView().setTranslateX(hm.getHistionMap().get(c.getHistionId()).getXCoordinate());
                        line.getMeshView().setTranslateY(hm.getHistionMap().get(c.getHistionId()).getYCoordinate());
                        line.getMeshView().setTranslateZ(hm.getHistionMap().get(c.getHistionId()).getZCoordinate());
                        shapeGroup.getChildren().add(line.getMeshView());
                        /*line.getMeshView(1).setTranslateX(hm.getHistionMap().get(c.getHistionId()).getXCoordinate());
                        line.getMeshView(2).setTranslateX(hm.getHistionMap().get(c.getHistionId()).getXCoordinate());
                        line.getMeshView(3).setTranslateX(hm.getHistionMap().get(c.getHistionId()).getXCoordinate());
                        line.getMeshView(4).setTranslateX(hm.getHistionMap().get(c.getHistionId()).getXCoordinate());
                        line.getMeshView(1).setTranslateY(hm.getHistionMap().get(c.getHistionId()).getYCoordinate());
                        line.getMeshView(2).setTranslateY(hm.getHistionMap().get(c.getHistionId()).getYCoordinate());
                        line.getMeshView(3).setTranslateY(hm.getHistionMap().get(c.getHistionId()).getYCoordinate());
                        line.getMeshView(4).setTranslateY(hm.getHistionMap().get(c.getHistionId()).getYCoordinate());
                        line.getMeshView(1).setTranslateZ(hm.getHistionMap().get(c.getHistionId()).getZCoordinate());
                        line.getMeshView(2).setTranslateZ(hm.getHistionMap().get(c.getHistionId()).getZCoordinate());
                        line.getMeshView(3).setTranslateZ(hm.getHistionMap().get(c.getHistionId()).getZCoordinate());
                        line.getMeshView(4).setTranslateZ(hm.getHistionMap().get(c.getHistionId()).getZCoordinate());
                        shapeGroup.getChildren().add(line.getMeshView(1));
                        shapeGroup.getChildren().add(line.getMeshView(2));
                        shapeGroup.getChildren().add(line.getMeshView(3));
                        shapeGroup.getChildren().add(line.getMeshView(4));*/
                        //l.getMeshView(1).setTranslateX(-100);
                        lineArr.add(line);
                        //System.out.println("555");
                    }
                    /*for (Line3D l : lineArr) {
                        //System.out.println("7777");
                        l.getMeshView(1).setTranslateX(hm.getHistionMap().get(c.getHistionId()).getXCoordinate());
                        l.getMeshView(2).setTranslateX(hm.getHistionMap().get(c.getHistionId()).getXCoordinate());
                        l.getMeshView(3).setTranslateX(hm.getHistionMap().get(c.getHistionId()).getXCoordinate());
                        l.getMeshView(4).setTranslateX(hm.getHistionMap().get(c.getHistionId()).getXCoordinate());
                        l.getMeshView(1).setTranslateY(hm.getHistionMap().get(c.getHistionId()).getYCoordinate());
                        l.getMeshView(2).setTranslateY(hm.getHistionMap().get(c.getHistionId()).getYCoordinate());
                        l.getMeshView(3).setTranslateY(hm.getHistionMap().get(c.getHistionId()).getYCoordinate());
                        l.getMeshView(4).setTranslateY(hm.getHistionMap().get(c.getHistionId()).getYCoordinate());
                        l.getMeshView(1).setTranslateZ(hm.getHistionMap().get(c.getHistionId()).getZCoordinate());
                        l.getMeshView(2).setTranslateZ(hm.getHistionMap().get(c.getHistionId()).getZCoordinate());
                        l.getMeshView(3).setTranslateZ(hm.getHistionMap().get(c.getHistionId()).getZCoordinate());
                        l.getMeshView(4).setTranslateZ(hm.getHistionMap().get(c.getHistionId()).getZCoordinate());
                    }*/
                    lineMap.put(c.getId(), lineArr);
                    intersectionsWithEdges(c.getId());
                }
            });
            return;
        }
        System.out.println(c.getId());
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
        
        /*dataSize = 0;
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
        applyTransformations(xRot, yRot, xTran, yTran, zTran, nodeAvg, pointData);*/
        
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
        //facesList.put(c.getId(), new int[tr.getFaceList().length]);
        System.arraycopy(tr.getNodeList(), 0, nodesList.get(c.getId()), 0, tr.getNodeList().length);
        
        System.arraycopy(tr.getNodeList(), 0, nodesListTemp.get(c.getId()), 0, tr.getNodeList().length);
        
        System.arraycopy(tr.getTetrahedronList(), 0, tetrahedronsList.get(c.getId()), 0, tr.getTetrahedronList().length);
        //System.arraycopy(tr.getFaceList(), 0, facesList.get(c.getId()), 0, tr.getFaceList().length);
        
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
        
        /*final PhongMaterial blackMaterial = new PhongMaterial();
        blackMaterial.setDiffuseColor(Color.BLACK);
        blackMaterial.setSpecularColor(Color.BLACK);
        MeshView shape2= new MeshView(shapeMesh);
        shape2.setDrawMode(DrawMode.LINE);
        shape2.setMaterial(blackMaterial);
        shape2.setScaleX(1.1);
        shape2.setScaleY(1.1);
        shape2.setScaleZ(1.1);*/

        String n = c.getName();
        n = n.substring(n.indexOf("<") + 1, n.lastIndexOf(">")); 
        if (!HideCells.getCellNameToHideList().contains(n))
            shapeGroup.getChildren().add(shape);
        //shapeGroup.getChildren().add(shape2);
        //shapeMap.put(c.getId(), shape);
        hm.getShapeMap().put(c.getId(), shape);
        
        ArrayList<Point3D> linePointsList = new ArrayList<>();
        ArrayList<TwoIntegers> lineData = new ArrayList<>();
        ArrayList<Line3D> lineList = new ArrayList<>();
        /*ArrayList<Line3D3> line3List = new ArrayList<>();
        ArrayList<Line3D5> line5List = new ArrayList<>();*/
        
        /*for (ArrayList<Integer> f : facetData) {
            for (int i = 1; i < f.size(); i++) {
                
            }
        }*/
        //int cou = 0;
        for (ArrayList<Integer> f : facetData) {
            //cou++;
            //System.out.println(cou);
            //if (cou == 3)
            //    System.out.println("***" + linePointsList.size());
            //if (cou == 3)
            //    break;
            for (int i = 1; i < f.size(); i++) {
                //if (cou == 3)
                //System.out.println("---" + i);
                TwoIntegers ti = new TwoIntegers(0, f.get(i - 1), f.get(i));
                if (!lineData.contains(ti)) {
                    //if (cou == 3)
                    //System.out.println("1");
                    //linePointsList.clear();
                    linePointsList.add(new Point3D(pointData.get(ti.getPoint1() - 1).getX(),
                            pointData.get(ti.getPoint1() - 1).getY(),
                            pointData.get(ti.getPoint1() - 1).getZ()));
                    linePointsList.add(new Point3D(pointData.get(ti.getPoint2() - 1).getX(),
                            pointData.get(ti.getPoint2() - 1).getY(),
                            pointData.get(ti.getPoint2() - 1).getZ()));
                    /*if (i == f.size() - 1) {
                        //if (cou == 3)
                        //    System.out.println("2");
                        Line3D line = new Line3D(linePointsList, 2f, Color.BLACK);
                        lineList.add(line);
                        if (!HideCells.getCellNameToHideList().contains(n)) {
                            //if (cou == 3)
                            shapeGroup.getChildren().add(line.getMeshView());
                        }
                        linePointsList.clear();
                    }*/
                    
                    //Line3D line = new Line3D(linePointsList, 2f, Color.BLACK);
                    //lineList.add(line);
                    //if (!HideCells.getCellNameToHideList().contains(n))
                    //    shapeGroup.getChildren().add(line.getMeshView());
                    lineData.add(ti);
                } else {
                    if (linePointsList.size() > 0) {
                        //if (cou == 3)
                        //    System.out.println("3");
                        Line3D line = new Line3D(linePointsList, 2f, Color.BLACK);
                        lineList.add(line);
                        if (!HideCells.getCellNameToHideList().contains(n)) {
                            //if (cou == 3)
                            shapeGroup.getChildren().add(line.getMeshView());
                        }
                        //lineData.add(ti);
                        linePointsList.clear();
                    }
                }
            }
            
            TwoIntegers ti = new TwoIntegers(0, f.get(f.size() - 1), f.get(0));
            if (!lineData.contains(ti)) {
                //linePointsList.clear();
                linePointsList.add(new Point3D(pointData.get(ti.getPoint1() - 1).getX(),
                        pointData.get(ti.getPoint1() - 1).getY(),
                        pointData.get(ti.getPoint1() - 1).getZ()));
                linePointsList.add(new Point3D(pointData.get(ti.getPoint2() - 1).getX(),
                        pointData.get(ti.getPoint2() - 1).getY(),
                        pointData.get(ti.getPoint2() - 1).getZ()));
                Line3D line = new Line3D(linePointsList, 2f, Color.BLACK);
                lineList.add(line);
                if (!HideCells.getCellNameToHideList().contains(n))
                    //if (cou == 3)
                    shapeGroup.getChildren().add(line.getMeshView());
            } else {
                if (linePointsList.size() > 0) {
                    //if (cou == 3)
                    //    System.out.println("3");
                    Line3D line = new Line3D(linePointsList, 2f, Color.BLACK);
                    lineList.add(line);
                    if (!HideCells.getCellNameToHideList().contains(n)) {
                        //if (cou == 3)
                        shapeGroup.getChildren().add(line.getMeshView());
                    }
                    //lineData.add(ti);
                    //linePointsList.clear();
                }
            }
            linePointsList.clear();
            
            /*TwoIntegers ti = new TwoIntegers(0, f.get(f.size() - 1), f.get(0));
            if (!lineData.contains(ti)) {
                linePointsList.clear();
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
            }*/
        }
        
        //System.out.println(lineList.size());
        /*TwoIntegers ti;
        //System.out.println(facetData.size());
        //int count1 = 0;
        for (ArrayList<Integer> f : facetData) {
            //count1++;
            //System.out.println("---");
            //System.out.println(count1);
            for (int i = 1; i < f.size(); i++) {
                //System.out.println(i);
                ti = new TwoIntegers(i, f.get(i - 1), f.get(i));
                linePointsList.add(new Point3D(pointData.get(ti.getPoint1() - 1).getX(),
                        pointData.get(ti.getPoint1() - 1).getY(),
                        pointData.get(ti.getPoint1() - 1).getZ()));
                linePointsList.add(new Point3D(pointData.get(ti.getPoint2() - 1).getX(),
                        pointData.get(ti.getPoint2() - 1).getY(),
                        pointData.get(ti.getPoint2() - 1).getZ()));
            }
            ti = new TwoIntegers(f.size(), f.get(f.size() - 1), f.get(0));
            linePointsList.add(new Point3D(pointData.get(ti.getPoint1() - 1).getX(),
                    pointData.get(ti.getPoint1() - 1).getY(),
                    pointData.get(ti.getPoint1() - 1).getZ()));
            linePointsList.add(new Point3D(pointData.get(ti.getPoint2() - 1).getX(),
                    pointData.get(ti.getPoint2() - 1).getY(),
                    pointData.get(ti.getPoint2() - 1).getZ()));
            Line3D line = new Line3D(linePointsList, 3f, Color.BLACK);
            lineList.add(line);
            shapeGroup.getChildren().add(line.getMeshView(1));
            shapeGroup.getChildren().add(line.getMeshView(2));
            shapeGroup.getChildren().add(line.getMeshView(3));
            shapeGroup.getChildren().add(line.getMeshView(4));
        }
        lineMap.put(c.getId(), lineList);*/
        lineMap.put(c.getId(), lineList);
        //hm.getHistionMap().get(0).setShapeMap(shapeMap);
        intersectionsWithEdges(c.getId());  
        /*for (int i = 0; i < 100; i++)
        System.out.println("Ended");*/
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
        
        xAxis.setTranslateX(0);
        xAxis.setTranslateY(0);
        xAxis.setTranslateZ(0);

        xAxis.setMaterial(redMaterial);
        
        final Box xAxisShapeGroup = new Box(axisLen, 2, 2);
        final Box yAxisShapeGroup = new Box(2, axisLen, 2);
        
        //final PhongMaterial redMaterialShapeGroup = new PhongMaterial();
        //shapeGroupMaterial.setDiffuseColor(Color.rgb(0, 0, 0, 0.0));
        //shapeGroupMaterial.setSpecularColor(Color.rgb(0, 0, 0, 0.0));
        shapeGroupMaterial.setDiffuseColor(Color.rgb(0, 0, 0));
        shapeGroupMaterial.setSpecularColor(Color.rgb(0, 0, 0));
        xAxisShapeGroup.setMaterial(shapeGroupMaterial);
        yAxisShapeGroup.setMaterial(shapeGroupMaterial);
        shapeGroupAxisGroup.getChildren().addAll(xAxisShapeGroup, yAxisShapeGroup);
        
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
        //root.getChildren().add(cameraXform);
        //cameraXform.getChildren().add(camera);
    }
    
    private void addRotateTransformsToGroup() {
        shapeGroup.getTransforms().clear();
        //shapeGroup.getTransforms().addAll(rotateYShapeGroup, rotateXShapeGroup);
        //axisGroup.getTransforms().addAll(rotateYShapeGroup, rotateXShapeGroup);
        //shapeGroup.getTransforms().addAll(rotateZShapeGroup, rotateYShapeGroup, rotateXShapeGroup);
        //shapeGroup.getTransforms().addAll(rotateZShapeGroup, rotateYShapeGroup, rotateXShapeGroup);
        //shapeGroup.getTransforms().addAll(rotateYShapeGroup, rotateZShapeGroup, rotateXShapeGroup);
        shapeGroup.getTransforms().addAll(rotateYShapeGroup, rotateXShapeGroup);
        //shapeGroup.getTransforms().addAll(rotateXShapeGroup, rotateZShapeGroup, rotateYShapeGroup);
        //shapeGroup.getTransforms().addAll(rotateZShapeGroup, rotateXShapeGroup, rotateYShapeGroup);
    }
    
    //final XForm cameraXform = new XForm();
    //final XForm cameraXform2 = new XForm();
    //final XForm cameraXform3 = new XForm();
    
    boolean isDone = true;
    
    class DoWork extends Task<Boolean> {
        
        @Override
        protected Boolean call() throws Exception {
            double ang = rotateXShapeGroup.getAngle() + mouseDeltaY * 0.1;
            double angRadX = Math.toRadians(ang);
            rotateXShapeGroup.setAngle(ang);

            ang = rotateYShapeGroup.getAngle() - mouseDeltaX * 0.1;
            double angRadY = Math.toRadians(ang);
            rotateYShapeGroup.setAngle(ang);
            hm.getAllHistions().forEach(h -> {
                h.getItems().forEach(c -> {
                    for (int i = 0; i < nodesList.get(c.getId()).length; i += 3) {
                        if (isCancelled()) {
                            break;
                        }
                        double x = nodesListTemp.get(c.getId())[i];
                        double y = nodesListTemp.get(c.getId())[i + 1];
                        double z = nodesListTemp.get(c.getId())[i + 2];
                        double tempVal = y;
                        y = y * Math.cos(angRadX) - z * Math.sin(angRadX);
                        z = tempVal * Math.sin(angRadX) + z * Math.cos(angRadX);

                        tempVal = x;
                        x = x * Math.cos(angRadY) + z * Math.sin(angRadY);
                        z = -tempVal * Math.sin(angRadY) + z * Math.cos(angRadY);

                        nodesList.get(c.getId())[i] = x;
                        nodesList.get(c.getId())[i + 1] = y;
                        nodesList.get(c.getId())[i + 2] = z;

                    }
                });
            });
            hm.getAllHistions().forEach(h -> {
                    h.getItems().forEach(c -> {
                        if (c.getShow())
                            if (!isCancelled())
                                intersectionsWithEdges(c.getId());
                            //System.out.println(c.getId());
                    });
                });
            if (!isCancelled()) {
                isDone = true;
                return true;
            }
            else {
                isDone = false;
                return false;
            }
        }
    
}
    DoWork task = new DoWork();
    double mouseDeltaX; 
    double mouseDeltaY; 
    
    double oldValXRot = 0;
    double oldValYRot = 0;
    /*double oldValXPos = 0;
    double oldValYPos = 0;
    double oldValZPos = 0;*/
    
    /*ChangeListener xRotationChangeListener = (v, oldValue, newValue) -> {
        double newVal = (double)newValue;
        if (change) {
            if (Math.abs(newVal - oldValXRot) > 1) {
                oldValXRot = newVal;
                xRotation.setText(newValue + "");
            }
        }
    };*/
    
    private void handleMouseEvents(Scene scene) {
        
        camera.getTransforms().clear();
        camera.getTransforms().addAll(rotateYCam, rotateXCam);
        shapeGroupAxisGroup.getTransforms().addAll(rotateYShapeGroup, rotateXShapeGroup);
        
        scene.setOnMousePressed(me -> {         
            mouseOldX = me.getSceneX();
            mouseOldY = me.getSceneY();
        });
        
        scene.setOnMouseDragged(me -> {
            
            if (me.isSecondaryButtonDown()) {
                //shapeGroupMaterial.setDiffuseColor(Color.rgb(0, 0, 0, 0.8));
                //shapeGroupMaterial.setSpecularColor(Color.rgb(0, 0, 0, 0.8));
                mousePosX = me.getSceneX();
                mousePosY = me.getSceneY();
                //double mouseDeltaX = (mousePosX - mouseOldX); 
                //double mouseDeltaY = (mousePosY - mouseOldY); 
                mouseDeltaX = (mousePosX - mouseOldX); 
                mouseDeltaY = (mousePosY - mouseOldY); 
                //double modifier = 1.0;
                //double modifierFactor = 0.1;
                
                
                /*GroupTransforms.setXRotate(String.valueOf(Double.parseDouble(
                        GroupTransforms.getXRotate())+mouseDeltaY*0.1));
                GroupTransforms.setYRotate(String.valueOf(Double.parseDouble(GroupTransforms.getYRotate())-mouseDeltaX*0.1));*/
                
                double ang = rotateXShapeGroup.getAngle()+mouseDeltaY*0.1;
                //double angRadX = Math.toRadians(ang);
                double angXCos = Math.cos(Math.toRadians(ang));
                double angXSin = Math.sin(Math.toRadians(ang));
                rotateXShapeGroup.setAngle(ang);

                ang = rotateYShapeGroup.getAngle() - mouseDeltaX * 0.1;
                //double angRadY = Math.toRadians(ang);
                double angYSin = Math.sin(Math.toRadians(ang));
                double angYCos = Math.cos(Math.toRadians(ang));
                rotateYShapeGroup.setAngle(ang);
                
                
                
                if ((Math.abs(rotateXShapeGroup.getAngle()+mouseDeltaY*0.1 - oldValXRot) > 2) ||
                        (Math.abs(ang - oldValYRot) > 2)) {
                    oldValXRot = rotateXShapeGroup.getAngle()+mouseDeltaY*0.1;
                    oldValYRot = ang;
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

                                nodesList.get(c.getId())[i] = x;
                                nodesList.get(c.getId())[i + 1] = y;
                                nodesList.get(c.getId())[i + 2] = z;

                            }
                            intersectionsWithEdges(c.getId());
                        }
                    });
                });
                }
                
                //GroupTransforms.setXRotate(String.valueOf(Double.parseDouble(GroupTransforms.getXRotate())+(mousePosY - mouseOldY)*0.1));
                //GroupTransforms.setYRotate(String.valueOf(Double.parseDouble(GroupTransforms.getYRotate())-(mousePosX - mouseOldX)*0.1));
                
                //GroupTransforms.setXRotate(String.valueOf(Double.parseDouble(GroupTransforms.getXRotate())+(mousePosY - mouseOldY)*0.1*Math.sin(Math.toRadians(rotateYCam.getAngle()))));
                /*System.out.println(mouseDeltaY);
                if (mouseDeltaY < 0) {
                    if (Double.parseDouble(GroupTransforms.getXRotate()) > -90)
                        GroupTransforms.setXRotate(String.valueOf(Double.parseDouble(GroupTransforms.getXRotate())+mouseDeltaY*0.1* Math.cos(Math.toRadians(rotateYCam.getAngle()))));
                    if (rotateZShapeGroup.getAngle() < 90)
                        rotateZShapeGroup.setAngle(rotateZShapeGroup.getAngle()-mouseDeltaY*0.1* Math.sin(Math.toRadians(rotateYCam.getAngle())));
                }
                if (mouseDeltaY > 0) {
                    if (Double.parseDouble(GroupTransforms.getXRotate()) < 90)
                        GroupTransforms.setXRotate(String.valueOf(Double.parseDouble(GroupTransforms.getXRotate())+mouseDeltaY*0.1* Math.cos(Math.toRadians(rotateYCam.getAngle()))));
                    if (rotateZShapeGroup.getAngle() > -90)
                        rotateZShapeGroup.setAngle(rotateZShapeGroup.getAngle()-mouseDeltaY*0.1* Math.sin(Math.toRadians(rotateYCam.getAngle())));
                }*/
                //if (Double.parseDouble(GroupTransforms.getXRotate()) < 90 && Double.parseDouble(GroupTransforms.getXRotate()) > -90)
                //    GroupTransforms.setXRotate(String.valueOf(Double.parseDouble(GroupTransforms.getXRotate())+mouseDeltaY*0.1* Math.cos(Math.toRadians(rotateYCam.getAngle()))));
                //rotateZShapeGroup.setAngle(rotateZShapeGroup.getAngle()-mouseDeltaY*0.1* Math.sin(Math.toRadians(rotateYCam.getAngle())));
                //GroupTransforms.setYRotate(String.valueOf(Double.parseDouble(GroupTransforms.getYRotate())-mouseDeltaX*0.1));
                //rotateZShapeGroup.setAngle(rotateZShapeGroup.getAngle()+(mousePosY - mouseOldY)*0.1*Math.sin(Math.toRadians(rotateYCam.getAngle())));
                //cameraXform.ry.setAngle(cameraXform.ry.getAngle() - mouseDeltaX*modifierFactor*modifier*2.0);  // +
                //cameraXform.rx.setAngle(cameraXform.rx.getAngle() + mouseDeltaY*modifierFactor*modifier*2.0);
                mouseOldX = mousePosX;
                mouseOldY = mousePosY;
                
                /*for (int i = 0; i < test.size(); i++) {

                    shapeGroup.getChildren().remove(test.get(i));
                }
                test.clear();
                hm.getHistionMap().get(0).getItems().forEach(c -> {
                    for (int i = 0; i < nodesList.get(c.getId()).length; i += 3) {
                        Box b = new Box(5, 5, 5);
                        b.setTranslateX(nodesList.get(c.getId())[i]);
                        b.setTranslateY(nodesList.get(c.getId())[i + 1]);
                        b.setTranslateZ(nodesList.get(c.getId())[i + 2]);
                        shapeGroup.getChildren().add(b);
                        test.add(b);
                    }
                });*/
                
                
                //redMaterialShapeGroup.setDiffuseColor(Color.rgb(0, 0, 0, 0));
                //redMaterialShapeGroup.setSpecularColor(Color.rgb(0, 0, 0, 0));
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
                double mouseDeltaX = (mousePosX - mouseOldX); 
                double mouseDeltaY = (mousePosY - mouseOldY);
                shapeGroup.setTranslateX(shapeGroup.getTranslateX() + mouseDeltaX * Math.cos(Math.toRadians(rotateYCam.getAngle())));
                shapeGroup.setTranslateY(shapeGroup.getTranslateY() + mouseDeltaY);
                shapeGroup.setTranslateZ(shapeGroup.getTranslateZ() - mouseDeltaX * Math.sin(Math.toRadians(rotateYCam.getAngle())));
                shapeGroupAxisGroup.setTranslateX(shapeGroup.getTranslateX() + mouseDeltaX * Math.cos(Math.toRadians(rotateYCam.getAngle())));
                shapeGroupAxisGroup.setTranslateY(shapeGroup.getTranslateY() + mouseDeltaY);
                shapeGroupAxisGroup.setTranslateZ(shapeGroup.getTranslateZ() - mouseDeltaX * Math.sin(Math.toRadians(rotateYCam.getAngle())));
                mouseOldX = mousePosX;
                mouseOldY = mousePosY;
            }
        });
        
        scene.setOnMouseReleased(me -> {
            //shapeGroupMaterial.setDiffuseColor(Color.rgb(0, 0, 0, 0));
            //shapeGroupMaterial.setSpecularColor(Color.rgb(0, 0, 0, 0));

            double ang = rotateXShapeGroup.getAngle() + mouseDeltaY * 0.1;
            double angXCos = Math.cos(Math.toRadians(ang));
            double angXSin = Math.sin(Math.toRadians(ang));

            ang = rotateYShapeGroup.getAngle() - mouseDeltaX * 0.1;
            double angYSin = Math.sin(Math.toRadians(ang));
            double angYCos = Math.cos(Math.toRadians(ang));
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

                            nodesList.get(c.getId())[i] = x;
                            nodesList.get(c.getId())[i + 1] = y;
                            nodesList.get(c.getId())[i + 2] = z;

                        }
                        intersectionsWithEdges(c.getId());
                    }
                });
            });
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
                    case R:
                        shapeGroup.setScaleX(shapeGroup.getScaleX() + 0.05);
                        shapeGroup.setScaleY(shapeGroup.getScaleY() + 0.05);
                        shapeGroup.setScaleZ(shapeGroup.getScaleZ() + 0.05);
                        hm.getAllHistions().forEach(h -> {
                            h.getItems().forEach(c -> {
                                for (int i = 0; i < nodesList.get(c.getId()).length; i += 3) {
                                    nodesList.get(c.getId())[i] *= 1.05;
                                    nodesList.get(c.getId())[i + 1] *= 1.05;
                                    nodesList.get(c.getId())[i + 2] *= 1.05;

                                }
                            });
                        });
                        hm.getAllHistions().forEach(h -> {
                            h.getItems().forEach(c -> {
                                if (c.getShow()) {
                                    intersectionsWithEdges(c.getId());
                                }
                                //System.out.println(c.getId());
                            });
                        });
                        break;
                    case T:
                        shapeGroup.setScaleX(shapeGroup.getScaleX() - 0.05);
                        shapeGroup.setScaleY(shapeGroup.getScaleY() - 0.05);
                        shapeGroup.setScaleZ(shapeGroup.getScaleZ() - 0.05);
                        hm.getAllHistions().forEach(h -> {
                            h.getItems().forEach(c -> {
                                for (int i = 0; i < nodesList.get(c.getId()).length; i += 3) {
                                    nodesList.get(c.getId())[i] *= 0.95;
                                    nodesList.get(c.getId())[i + 1] *= 0.95;
                                    nodesList.get(c.getId())[i + 2] *= 0.95;

                                }
                            });
                        });
                        hm.getAllHistions().forEach(h -> {
                            h.getItems().forEach(c -> {
                                if (c.getShow()) {
                                    intersectionsWithEdges(c.getId());
                                }
                                //System.out.println(c.getId());
                            });
                        });
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
                                //shapeGroup.getChildren().remove(shapeMap.get(c.getId()));
                                shapeGroup.getChildren().remove(hm.getShapeMap().get(c.getId()));
                                //shapeGroup.getChildren().remove(lineList.);
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
                                //shapeGroup.getChildren().add(shapeMap.get(c.getId()));
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
    boolean shouldBeChanged = false;
    
    ChangeListener<String> crossSectionXRotListener = (v, oldValue, newValue) -> {
        try {
            double ang = Double.parseDouble(newValue);
            if (Math.abs(ang - oldValXRotCross) > 2) {
                oldValXRotCross = ang;
                shouldBeChanged = true;
            }
            /*if ((ang >= 0) && (ang <= 90)) {
                CrossSection.xRotateProperty().set(String.valueOf(ang));
            }    */   
            rotateXCrossSection.setAngle(ang);
        } catch (Exception ex) {
            
        }
    };
    ChangeListener<String> crossSectionYRotListener = (v, oldValue, newValue) -> {
        try {
            double ang = Double.parseDouble(newValue);
            if (Math.abs(ang - oldValYRotCross) > 2) {
                oldValYRotCross = ang;
                shouldBeChanged = true;
            }
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
                if (Math.abs(pos - oldValXPosCross) > 10) {
                    oldValXPosCross = pos;
                    shouldBeChanged = true;
                }
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
                if (Math.abs(pos - oldValYPosCross) > 10) {
                    oldValYPosCross = pos;
                    shouldBeChanged = true;
                }
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
                if (Math.abs(pos - oldValZPosCross) > 10) {
                    oldValZPosCross = pos;
                    shouldBeChanged = true;
                }
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
            //CrossSectionViewerTopComponent.clear();
            if (shouldBeChanged) {
                shouldBeChanged = false;
            hm.getAllHistions().forEach(h -> {
                h.getItems().forEach(c -> {
                    if (c.getShow())
                        intersectionsWithEdges(c.getId());
                });
            });
            }
            CrossSection.setChanged(false);
        }
    };
    
    ChangeListener<Boolean> updateListener = (v, oldValue, newValue) -> {
        if (newValue) {
            //CrossSectionViewerTopComponent.clear();
            //polygonList.clear();
            //linesList.clear();
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
    
    ArrayList<Box> test = new ArrayList<>();
    
    ChangeListener<String> groupXRotListener = (v, oldValue, newValue) -> {
        try {
            double ang = Double.parseDouble(newValue);
            //if ((ang >= -90) && (ang <= 90)) {
                //GroupTransforms.xRotateProperty().set(String.valueOf(ang));
            //}   
            /*if ((ang >= 360) || (ang <= -360)) {
                ang = ang % 360;
                GroupTransforms.xRotateProperty().set(String.valueOf(ang));
            }*/
            rotateXShapeGroup.setAngle(ang);
            double angRad = Math.toRadians(ang - Double.parseDouble(oldValue));
            hm.getAllHistions().forEach(h -> {
                //System.out.println(h.getName());
                h.getItems().forEach(c -> {
                for (int i = 0; i < nodesList.get(c.getId()).length; i += 3) {
                    //double x = nodesList.get(c.getId())[i];
                    double y = nodesList.get(c.getId())[i + 1];
                    double z = nodesList.get(c.getId())[i + 2];
                    //ang = Math.toRadians(xRot);
                    double tempVal = y;
                    //double angRad = Math.toRadians(ang);
                    y = y * Math.cos(angRad) - z * Math.sin(angRad);
                    z = tempVal * Math.sin(angRad) + z * Math.cos(angRad);
                    //nodesList.get(c.getId())[i] = x;
                    nodesList.get(c.getId())[i + 1] = y;
                    nodesList.get(c.getId())[i + 2] = z;
            
            /*ang = Math.toRadians(yRot);
            tempVal = pd.getX();
            pd.setX(pd.getX() * Math.cos(ang) + pd.getZ() * Math.sin(ang));
            pd.setZ(-tempVal * Math.sin(ang) + pd.getZ() * Math.cos(ang));*/
                    
                }
            });
            });
            
            /*hm.getHistionMap().get(0).getItems().forEach(c -> {
                for (int i = 0; i < nodesList.get(c.getId()).length; i += 3) {
                    //double x = nodesList.get(c.getId())[i];
                    double y = nodesList.get(c.getId())[i + 1];
                    double z = nodesList.get(c.getId())[i + 2];
                    //ang = Math.toRadians(xRot);
                    double tempVal = y;
                    //double angRad = Math.toRadians(ang);
                    y = y * Math.cos(angRad) - z * Math.sin(angRad);
                    z = tempVal * Math.sin(angRad) + z * Math.cos(angRad);
                    //nodesList.get(c.getId())[i] = x;
                    nodesList.get(c.getId())[i + 1] = y;
                    nodesList.get(c.getId())[i + 2] = z;
                    
                }
            });*/
            
            /*for (int i = 0; i < test.size(); i++) {
                
                shapeGroup.getChildren().remove(test.get(i));
            }
            test.clear();
            hm.getHistionMap().get(0).getItems().forEach(c -> {
            for (int i = 0; i < nodesList.get(c.getId()).length; i+=3) {
                Box b = new Box(5,5,5);
                b.setTranslateX(nodesList.get(c.getId())[i]);
                b.setTranslateY(nodesList.get(c.getId())[i + 1]);
                b.setTranslateZ(nodesList.get(c.getId())[i + 2]);
                shapeGroup.getChildren().add(b);
                test.add(b);
            }
            });*/
        } catch (Exception ex) {
            
        }
    };
    
    ChangeListener<String> groupYRotListener = (v, oldValue, newValue) -> {
        try {
            double ang = Double.parseDouble(newValue);
            /*if ((ang >= 360) || (ang <= -360)) {
                ang = ang % 360;
                GroupTransforms.yRotateProperty().set(String.valueOf(ang));
            }*/
            rotateYShapeGroup.setAngle(ang);
            //double[] nl = new double[nodesList.get(c.getId()).length];
            double angRad = Math.toRadians(ang - Double.parseDouble(oldValue));
            //double angRad = Math.toRadians(ang);
            hm.getAllHistions().forEach(h -> {
                h.getItems().forEach(c -> {
                for (int i = 0; i < nodesList.get(c.getId()).length; i += 3) {
                    
                    double x = nodesList.get(c.getId())[i];
                    //double y = nodesList.get(c.getId())[i + 1];
                    double z = nodesList.get(c.getId())[i + 2];
                    //ang = Math.toRadians(xRot);
                    double tempVal = x;
                    //double angRad = Math.toRadians(ang);
                    x = x * Math.cos(angRad) + z * Math.sin(angRad);
                    z = -tempVal * Math.sin(angRad) + z * Math.cos(angRad);
                    nodesList.get(c.getId())[i] = x;
                    //nodesList.get(c.getId())[i + 1] = y;
                    nodesList.get(c.getId())[i + 2] = z;
            
            /*ang = Math.toRadians(yRot);
            tempVal = pd.getX();
            pd.setX(pd.getX() * Math.cos(ang) + pd.getZ() * Math.sin(ang));
            pd.setZ(-tempVal * Math.sin(ang) + pd.getZ() * Math.cos(ang));*/
                    
                }
            });
            });
            /*hm.getHistionMap().get(0).getItems().forEach(c -> {
                for (int i = 0; i < nodesList.get(c.getId()).length; i += 3) {
                    double x = nodesList.get(c.getId())[i];
                    //double y = nodesList.get(c.getId())[i + 1];
                    double z = nodesList.get(c.getId())[i + 2];
                    //ang = Math.toRadians(xRot);
                    double tempVal = x;
                    //double angRad = Math.toRadians(ang);
                    x = x * Math.cos(angRad) + z * Math.sin(angRad);
                    z = -tempVal * Math.sin(angRad) + z * Math.cos(angRad);
                    nodesList.get(c.getId())[i] = x;
                    //nodesList.get(c.getId())[i + 1] = y;
                    nodesList.get(c.getId())[i + 2] = z;
                    
                }
            });*/
            
            /*for (int i = 0; i < test.size(); i++) {
                
                shapeGroup.getChildren().remove(test.get(i));
            }
            test.clear();
            hm.getHistionMap().get(0).getItems().forEach(c -> {
            for (int i = 0; i < nodesList.get(c.getId()).length; i+=3) {
                Box b = new Box(5,5,5);
                b.setTranslateX(nodesList.get(c.getId())[i]);
                b.setTranslateY(nodesList.get(c.getId())[i + 1]);
                b.setTranslateZ(nodesList.get(c.getId())[i + 2]);
                shapeGroup.getChildren().add(b);
                test.add(b);
                }
            });*/
            
        } catch (Exception ex) {
            
        }
    };
    
    /*ChangeListener<String> groupZRotListener = (v, oldValue, newValue) -> {
        try {
            double ang = Double.parseDouble(newValue);
            rotateZShapeGroup.setAngle(ang);
            //System.out.println(ang);
            double angRad = Math.toRadians(ang - Double.parseDouble(oldValue));
            hm.getHistionMap().get(0).getItems().forEach(c -> {
                for (int i = 0; i < nodesList.get(c.getId()).length; i += 3) {
                    double x = nodesList.get(c.getId())[i];
                    double y = nodesList.get(c.getId())[i + 1];
                    //double z = nodesList.get(c.getId())[i + 2];
                    //ang = Math.toRadians(xRot);
                    double tempVal = x;
                    //double angRad = Math.toRadians(ang);
                    x = x * Math.cos(angRad) - y * Math.sin(angRad);
                    y = tempVal * Math.sin(angRad) + y * Math.cos(angRad);
                    nodesList.get(c.getId())[i] = x;
                    nodesList.get(c.getId())[i + 1] = y;
                    //nodesList.get(c.getId())[i + 2] = z;
                    
                }
            });
        } catch (Exception ex) {
            
        }
    };*/
    
    private void addGroupListener() {
        GroupTransforms.xRotateProperty().addListener(groupXRotListener);
        GroupTransforms.yRotateProperty().addListener(groupYRotListener);
        //GroupTransforms.zRotateProperty().addListener(groupZRotListener);
    }
    
    private void removeGroupListener() {
        GroupTransforms.xRotateProperty().removeListener(groupXRotListener);
        GroupTransforms.yRotateProperty().removeListener(groupYRotListener);
        //GroupTransforms.zRotateProperty().removeListener(groupZRotListener);
    }
    
    @Override
    public void componentOpened() {
        addCameraViewListener();
        addCrossSectionListener();
        //addGroupListener();
        hm = Lookup.getDefault().lookup(HistionManager.class);
        if (hm == null) {
            LifecycleManager.getDefault().exit();
        }
        hm.getAllHistions().forEach(h -> {
            h.getItemMap().addListener(cellListener);
        });
        hm.addListener(histionListener);
        
        if (hm.getHistionMap().isEmpty())
            //hm.addHistion(new Histion("Main histion",0,0,0,0,0));
            hm.addHistion(new Histion("Main histion",0,0,0));
    }

    @Override
    public void componentClosed() {
        removeCameraViewListener();
        removeCrossSectionListener();
        //removeGroupListener();
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
