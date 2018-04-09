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
import javafx.scene.input.PickResult;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Text;
import javafx.scene.transform.Translate;
import org.openide.LifecycleManager;
import org.spbu.histology.cross.section.viewer.CrossSectionViewerTopComponent;
import org.spbu.histology.model.Cell;
import org.spbu.histology.model.CrossSection;
import org.spbu.histology.model.Histion;
import org.spbu.histology.model.HistionManager;
import org.spbu.histology.model.Node;
import org.spbu.histology.model.Part;
import org.spbu.histology.model.TetgenFacet;
import org.spbu.histology.model.TetgenPoint;
import org.spbu.histology.fxyz.Text3DMesh;
import org.spbu.histology.model.AlertBox;

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
    
    //private ShapeManager sm = null;
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
    /*private final ObservableMap<Long, Rotate> xRotateList = 
            FXCollections.observableMap(new ConcurrentHashMap());
    private final ObservableMap<Long, Rotate> yRotateList = 
            FXCollections.observableMap(new ConcurrentHashMap());*/

    private final ArrayList<Node> intersectionNodes = new ArrayList();
    
    private final double EPS = 0.0000001;
    
    /*private void returnNodeListToOriginal(Shape s) {
        double angX = -Math.toRadians(s.getXRotate());
        double tempVal;
        double angY = -Math.toRadians(s.getYRotate());
        
        for (int i = 0; i < nodesList.get(s.getId()).length; i+=3) {
            nodesList.get(s.getId())[i] -= s.getXCoordinate();
            nodesList.get(s.getId())[i + 1] -= s.getYCoordinate();
            nodesList.get(s.getId())[i + 2] -= s.getZCoordinate();
            
            tempVal = nodesList.get(s.getId())[i];
            nodesList.get(s.getId())[i] = tempVal * Math.cos(angY) + nodesList.get(s.getId())[i + 2] * Math.sin(angY);
            nodesList.get(s.getId())[i + 2] = -tempVal * Math.sin(angY) + nodesList.get(s.getId())[i + 2] * Math.cos(angY);
            
            tempVal = nodesList.get(s.getId())[i + 1];
            nodesList.get(s.getId())[i + 1] = tempVal * Math.cos(angX) - nodesList.get(s.getId())[i + 2] * Math.sin(angX);
            nodesList.get(s.getId())[i + 2] = tempVal * Math.sin(angX) + nodesList.get(s.getId())[i + 2] * Math.cos(angX);
        }
    }*/
    
    private final MapChangeListener<Integer, Cell> cellListener =
            (change) -> {
                if (change.wasRemoved() && change.wasAdded()) {
                    Cell c = (Cell)change.getValueAdded();
                    //if (c.getFacetData().size() > 0) {
                    if (c.getShow()) {
                        Cell removedShape = (Cell)change.getValueRemoved();
                        if (shapeMap.get(removedShape.getId()) != null) {
                            CrossSectionViewerTopComponent.clearPolygonArray(polygonList.get(c.getId()));
                        //if (s.getCopiedId() == -2) {
                        //    returnNodeListToOriginal(removedShape);
                        //}
                        //else {
                        //    shapeGroup.getChildren().remove(shapeMap.get(removedShape.getId()));
                        //}
                            shapeGroup.getChildren().remove(shapeMap.get(removedShape.getId()));
                        }
                        //System.out.println(c.getItems().size());
                        addCell(c);
                        //intersectionsWithEdges(change.getKey());
                    }
                }
                else if (change.wasRemoved()) {  
                    Cell removedCell = (Cell)change.getValueAdded();
                    //if (removedCell.getFacetData().size() > 0) {
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
                    //if (addedCell.getFacetData().size() > 0) {
                    if (addedCell.getShow()) {
                        //System.out.println(addedCell.getFacetData().size());
                        addCell(addedCell);
                        //intersectionsWithEdges(change.getKey());
                    }
                }
            };
    
    private final MapChangeListener<Integer, Histion> histionListener = (change) -> {
                if (change.wasRemoved() && change.wasAdded()) {
                    //System.out.println("Dead end");
                }
                else if (change.wasRemoved()) {  
                    //System.out.println("Removed");
                    /*Histion removedHistion = (Histion)change.getValueAdded();
                    System.out.println(removedHistion.getName());
                    removedHistion.getItemMap().removeListener(cellListener);*/
                }
                else if (change.wasAdded()) {
                    Histion addedHistion = (Histion)change.getValueAdded();
                    /*for (Cell c : addedHistion.getItems()) {
                        if (c.getShow()) {
                            //System.out.println(addedCell.getFacetData().size());
                            addCell(c);
                            //intersectionsWithEdges(change.getKey());
                        }
                    }*/
                    addedHistion.getItemMap().addListener(cellListener);
                }
            };
    
    /*public static class Grid extends Pane {

        Rectangle wall;

        public Grid(double size) {
            wall = new Rectangle(size, size);
            getChildren().add(wall);

            double zTranslate = 0;
            double lineWidth = 1.0;
            Color gridColor = Color.BLACK;

            for (int y = 0; y <= size; y += size / 10) {
                Line line = new Line(0, 0, size, 0);
                line.setStroke(gridColor);
                line.setFill(gridColor);
                line.setTranslateY(y);
                line.setTranslateZ(zTranslate);
                line.setStrokeWidth(lineWidth);
                getChildren().addAll(line);
            }
            for (int x = 0; x <= size; x += size / 10) {
                Line line = new Line(0, 0, 0, size);
                line.setStroke(gridColor);
                line.setFill(gridColor);
                line.setTranslateX(x);
                line.setTranslateZ(zTranslate);
                line.setStrokeWidth(lineWidth);
                getChildren().addAll(line);
            }
        }
        public void setFill(Paint paint) {
            wall.setFill(paint);
        }
    }*/

    /*public static void changeDrawMode(int id) {
        shapeMap.get(id).setDrawMode(DrawMode.LINE);
    }*/
    
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
        //buildGrid();
        
        /*sm = Lookup.getDefault().lookup(ShapeManager.class);
        if (sm == null) {
            LifecycleManager.getDefault().exit();
        }*/
        
        hm = Lookup.getDefault().lookup(HistionManager.class);
        if (hm == null) {
            LifecycleManager.getDefault().exit();
        }
        hm.getAllHistions().forEach(h -> {
            h.getItemMap().addListener(cellListener);
        });
        hm.addListener(histionListener);
        
        buildData();
        /*sm.addListener(shapeListener);        
        buildData();        
        if (!sm.getAllShapes().isEmpty())
            handleMouseEvents();  */
        
        buildCrossSectionPlane();
        fxPanel.setScene(scene);  
    }

    private Polygon findPolygons(ArrayList<Node> intersectionNodes, Color color) {
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
            
            Polygon polygon = new Polygon();
            polygon.getPoints().addAll(new Double[]{
                intersectionNodes.get(0).x, intersectionNodes.get(0).z,
                intersectionNodes.get(1).x, intersectionNodes.get(1).z,
                intersectionNodes.get(2).x, intersectionNodes.get(2).z,
                intersectionNodes.get(3).x, intersectionNodes.get(3).z
            });
            
            polygon.setFill(color);
            /*polygon.setTranslateX(paneSize / 2);
            polygon.setTranslateY(paneSize / 2);
            root.getChildren().add(polygon);*/
            return polygon;
        }
        Polygon polygon = new Polygon();
        polygon.getPoints().addAll(new Double[]{
            intersectionNodes.get(0).x, intersectionNodes.get(0).z,
            intersectionNodes.get(1).x, intersectionNodes.get(1).z,
            intersectionNodes.get(2).x, intersectionNodes.get(2).z
        });
        polygon.setFill(color);
        /*polygon.setTranslateX(paneSize / 2);
        polygon.setTranslateY(paneSize / 2);
        root.getChildren().add(polygon);*/
        return polygon;
    }
    
    private void intersectionsWithEdges(Integer id) {
        /*if ((tetrahedronsList == null) || (nodesList == null))
            return;*/
        
        //CrossSectionViewerTopComponent.clear();
        /*sm.getAllShapes().forEach(s -> {
            //polygonList.put()
            double[] nl = nodesList.get(s.getId());
            int[] tl = tetrahedronsList.get(s.getId());
            for (int i = 0; i < tl.length; i += 4) {
                intersectionNodes.clear();
                findIntersection(new Node(nl[(tl[i] - 1) * 3], nl[(tl[i] - 1) * 3 + 1], nl[(tl[i] - 1) * 3 + 2]), new Node(nl[(tl[i + 1] - 1) * 3], nl[(tl[i + 1] - 1) * 3 + 1], nl[(tl[i + 1] - 1) * 3 + 2]));
                findIntersection(new Node(nl[(tl[i] - 1) * 3], nl[(tl[i] - 1) * 3 + 1], nl[(tl[i] - 1) * 3 + 2]), new Node(nl[(tl[i + 2] - 1) * 3], nl[(tl[i + 2] - 1) * 3 + 1], nl[(tl[i + 2] - 1) * 3 + 2]));
                findIntersection(new Node(nl[(tl[i] - 1) * 3], nl[(tl[i] - 1) * 3 + 1], nl[(tl[i] - 1) * 3 + 2]), new Node(nl[(tl[i + 3] - 1) * 3], nl[(tl[i + 3] - 1) * 3 + 1], nl[(tl[i + 3] - 1) * 3 + 2]));
                findIntersection(new Node(nl[(tl[i + 2] - 1) * 3], nl[(tl[i + 2] - 1) * 3 + 1], nl[(tl[i + 2] - 1) * 3 + 2]), new Node(nl[(tl[i + 1] - 1) * 3], nl[(tl[i + 1] - 1) * 3 + 1], nl[(tl[i + 1] - 1) * 3 + 2]));
                findIntersection(new Node(nl[(tl[i + 3] - 1) * 3], nl[(tl[i + 3] - 1) * 3 + 1], nl[(tl[i + 3] - 1) * 3 + 2]), new Node(nl[(tl[i + 1] - 1) * 3], nl[(tl[i + 1] - 1) * 3 + 1], nl[(tl[i + 1] - 1) * 3 + 2]));
                findIntersection(new Node(nl[(tl[i + 2] - 1) * 3], nl[(tl[i + 2] - 1) * 3 + 1], nl[(tl[i + 2] - 1) * 3 + 2]), new Node(nl[(tl[i + 3] - 1) * 3], nl[(tl[i + 3] - 1) * 3 + 1], nl[(tl[i + 3] - 1) * 3 + 2]));
                if (intersectionNodes.size() > 2) {
                    rotateTillHorizontalPanel();
                    CrossSectionViewerTopComponent.show(intersectionNodes, colorsList.get(s.getId()));
                }
            }
        });*/
        polygonList.put(id, new ArrayList<>());
        double[] nl = nodesList.get(id);
        int[] tl = tetrahedronsList.get(id);
        for (int i = 0; i < tl.length; i += 4) {
            intersectionNodes.clear();
            findIntersection(new Node(nl[(tl[i] - 1) * 3], nl[(tl[i] - 1) * 3 + 1], nl[(tl[i] - 1) * 3 + 2]), new Node(nl[(tl[i + 1] - 1) * 3], nl[(tl[i + 1] - 1) * 3 + 1], nl[(tl[i + 1] - 1) * 3 + 2]));
            findIntersection(new Node(nl[(tl[i] - 1) * 3], nl[(tl[i] - 1) * 3 + 1], nl[(tl[i] - 1) * 3 + 2]), new Node(nl[(tl[i + 2] - 1) * 3], nl[(tl[i + 2] - 1) * 3 + 1], nl[(tl[i + 2] - 1) * 3 + 2]));
            findIntersection(new Node(nl[(tl[i] - 1) * 3], nl[(tl[i] - 1) * 3 + 1], nl[(tl[i] - 1) * 3 + 2]), new Node(nl[(tl[i + 3] - 1) * 3], nl[(tl[i + 3] - 1) * 3 + 1], nl[(tl[i + 3] - 1) * 3 + 2]));
            findIntersection(new Node(nl[(tl[i + 2] - 1) * 3], nl[(tl[i + 2] - 1) * 3 + 1], nl[(tl[i + 2] - 1) * 3 + 2]), new Node(nl[(tl[i + 1] - 1) * 3], nl[(tl[i + 1] - 1) * 3 + 1], nl[(tl[i + 1] - 1) * 3 + 2]));
            findIntersection(new Node(nl[(tl[i + 3] - 1) * 3], nl[(tl[i + 3] - 1) * 3 + 1], nl[(tl[i + 3] - 1) * 3 + 2]), new Node(nl[(tl[i + 1] - 1) * 3], nl[(tl[i + 1] - 1) * 3 + 1], nl[(tl[i + 1] - 1) * 3 + 2]));
            findIntersection(new Node(nl[(tl[i + 2] - 1) * 3], nl[(tl[i + 2] - 1) * 3 + 1], nl[(tl[i + 2] - 1) * 3 + 2]), new Node(nl[(tl[i + 3] - 1) * 3], nl[(tl[i + 3] - 1) * 3 + 1], nl[(tl[i + 3] - 1) * 3 + 2]));
            if (intersectionNodes.size() > 2) {
                rotateTillHorizontalPanel();
                //polygonList.get(id).add(CrossSectionViewerTopComponent.show(intersectionNodes, colorsList.get(id)));
                polygonList.get(id).add(findPolygons(intersectionNodes, colorsList.get(id)));
            }
        }
        CrossSectionViewerTopComponent.show(polygonList.get(id));
    }
    
    private void findIntersection(Node p1, Node p2) {
        double firstEquation = CrossSection.getA() * p1.x + CrossSection.getB() * p1.y + CrossSection.getC() * p1.z + CrossSection.getD();
        double secondEquation = CrossSection.getA() * p2.x + CrossSection.getB() * p2.y + CrossSection.getC() * p2.z + CrossSection.getD();
        double absValueOfFirstEquation = Math.abs(firstEquation);
        double absValueOfSecondEquation = Math.abs(secondEquation);
        if ((absValueOfFirstEquation < EPS) && (absValueOfSecondEquation < EPS)) {
            if (!intersectionNodes.contains(p1))
                intersectionNodes.add(new Node(p1));
            if (!intersectionNodes.contains(p2))
                intersectionNodes.add(new Node(p2));
        } else if (absValueOfFirstEquation < EPS) {
            if (!intersectionNodes.contains(p1))
                intersectionNodes.add(new Node(p1));
        } else if (absValueOfSecondEquation < EPS) {
            if (!intersectionNodes.contains(p2))
                intersectionNodes.add(new Node(p2));
        } else if (firstEquation * secondEquation < 0) {
            double a1 = p2.x - p1.x;
            double a2 = p2.y - p1.y;
            double a3 = p2.z - p1.z;
            double t = 1 / (CrossSection.getA() * a1 + CrossSection.getB() * a2 +
                    CrossSection.getC() * a3) * (-1) * firstEquation;
            double x = a1 * t + p1.x;
            double y = a2 * t + p1.y;
            double z = a3 * t + p1.z;
            intersectionNodes.add(new Node(x, y, z));
        } 
    }
    
    private void rotateTillHorizontalPanel() {
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
                x = intersectionNodes.get(i).x - xCoord;
                y = intersectionNodes.get(i).y - yCoord;
                z = intersectionNodes.get(i).z - zCoord;
                
                temp = x;
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
    
    private void buildData() {
        ObservableList<TetgenPoint> pointData = FXCollections.observableArrayList();
        pointData.add(new TetgenPoint(1,-100,100,90));
        pointData.add(new TetgenPoint(2,-120,100,120));
        pointData.add(new TetgenPoint(3,-110,100,130));
        pointData.add(new TetgenPoint(4,-90,100,100));
        pointData.add(new TetgenPoint(5,-20,100,100));
        pointData.add(new TetgenPoint(6,0,100,140));
        pointData.add(new TetgenPoint(7,20,100,100));
        pointData.add(new TetgenPoint(8,90,100,100));
        pointData.add(new TetgenPoint(9,110,100,130));
        pointData.add(new TetgenPoint(10,120,100,120));
        pointData.add(new TetgenPoint(11,100,100,90));
        pointData.add(new TetgenPoint(12,100,100,-90));
        pointData.add(new TetgenPoint(13,120,100,-120));
        pointData.add(new TetgenPoint(14,110,100,-130));
        pointData.add(new TetgenPoint(15,90,100,-100));
        pointData.add(new TetgenPoint(16,20,100,-100));
        pointData.add(new TetgenPoint(17,0,100,-140));
        pointData.add(new TetgenPoint(18,-20,100,-100));
        pointData.add(new TetgenPoint(19,-90,100,-100));
        pointData.add(new TetgenPoint(20,-110,100,-130));
        pointData.add(new TetgenPoint(21,-120,100,-120));
        pointData.add(new TetgenPoint(22,-100,100,-90));
        
        pointData.add(new TetgenPoint(23,-100,-100,90));
        pointData.add(new TetgenPoint(24,-120,-100,120));
        pointData.add(new TetgenPoint(25,-110,-100,130));
        pointData.add(new TetgenPoint(26,-90,-100,100));
        pointData.add(new TetgenPoint(27,-20,-100,100));
        pointData.add(new TetgenPoint(28,0,-100,140));
        pointData.add(new TetgenPoint(29,20,-100,100));
        pointData.add(new TetgenPoint(30,90,-100,100));
        pointData.add(new TetgenPoint(31,110,-100,130));
        pointData.add(new TetgenPoint(32,120,-100,120));
        pointData.add(new TetgenPoint(33,100,-100,90));
        pointData.add(new TetgenPoint(34,100,-100,-90));
        pointData.add(new TetgenPoint(35,120,-100,-120));
        pointData.add(new TetgenPoint(36,110,-100,-130));
        pointData.add(new TetgenPoint(37,90,-100,-100));
        pointData.add(new TetgenPoint(38,20,-100,-100));
        pointData.add(new TetgenPoint(39,0,-100,-140));
        pointData.add(new TetgenPoint(40,-20,-100,-100));
        pointData.add(new TetgenPoint(41,-90,-100,-100));
        pointData.add(new TetgenPoint(42,-110,-100,-130));
        pointData.add(new TetgenPoint(43,-120,-100,-120));
        pointData.add(new TetgenPoint(44,-100,-100,-90));
        
        /*int pointSize = pointData.size();
        nodeAvg = new Point3D(0,0,0);
        for (int i = 0; i < pointSize; i++) {
            Point3D n = new Point3D(pointData.get(i).getX(), 
                    pointData.get(i).getY(),
                    pointData.get(i).getZ());
            nodeAvg.add(n.getX(), n.getY(), n.getZ());
        }
        nodeAvg = new Point3D(nodeAvg.getX() / pointSize, nodeAvg.getY() / pointSize, nodeAvg.getZ() / pointSize);*/
        
        ObservableList<TetgenFacet> facetData = FXCollections.observableArrayList();
        facetData.add(new TetgenFacet(1, 2, 1, 23, 24, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
        /*facetData.add(new TetgenFacet(1, 2, 1, 23, 24, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));*/
        facetData.add(new TetgenFacet(2, 3, 2, 24, 25, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
        facetData.add(new TetgenFacet(3, 4, 3, 25, 26, 
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
        facetData.add(new TetgenFacet(4, 5, 4, 26, 27, 
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
        facetData.add(new TetgenFacet(5, 6, 5, 27, 28, 
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
        facetData.add(new TetgenFacet(6, 7, 6, 28, 29, 
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
        facetData.add(new TetgenFacet(7, 8, 7, 29, 30, 
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
        facetData.add(new TetgenFacet(8, 9, 8, 30, 31, 
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
        facetData.add(new TetgenFacet(9, 10, 9, 31, 32, 
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
        facetData.add(new TetgenFacet(10, 11, 10, 32, 33, 
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
        facetData.add(new TetgenFacet(11, 12, 11, 33, 34, 
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
        facetData.add(new TetgenFacet(12, 13, 12, 34, 35, 
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
        facetData.add(new TetgenFacet(13, 14, 13, 35, 36, 
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
        facetData.add(new TetgenFacet(14, 15, 14, 36, 37, 
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
        facetData.add(new TetgenFacet(15, 16, 15, 37, 38, 
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
        facetData.add(new TetgenFacet(16, 17, 16, 38, 39, 
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
        facetData.add(new TetgenFacet(17, 18, 17, 39, 40, 
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
        facetData.add(new TetgenFacet(18, 19, 18, 40, 41, 
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
        facetData.add(new TetgenFacet(19, 20, 19, 41, 42, 
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
        facetData.add(new TetgenFacet(20, 21, 20, 42, 43, 
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
        facetData.add(new TetgenFacet(21, 22, 21, 43, 44, 
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
        facetData.add(new TetgenFacet(22, 1, 22, 44, 23, 
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
        
        facetData.add(new TetgenFacet(23, 1, 2, 3, 4, 
                5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 0, 0, 0, 0, 0, 0, 0, 0));
        facetData.add(new TetgenFacet(24, 23, 24, 25, 26, 
                27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 0, 0, 0, 0, 0, 0, 0, 0));
        
        int maxNumberOfVertices = 22;

        hm.addHistion(new Histion("Histion <1>",0,0,0,0,0));
        //hm.getAllHistions().get(0).addChild(new Cell("Cell <1>"));
        //Cell c = new Cell("Cell <2>", 0, 0, 0, 0, 0, FXCollections.observableArrayList(), 3,
        //                Color.RED, Color.RED, -1, 0, false, FXCollections.emptyObservableMap());
        Cell c = new Cell("Cell <1>", 0, 0, 0, 0, 0, facetData, maxNumberOfVertices, Color.BLUE, Color.LIGHTBLUE, 0, true);
        
        ObservableList<TetgenPoint> tempData1 = FXCollections.observableArrayList();
        ObservableList<TetgenPoint> tempData2 = FXCollections.observableArrayList();
        for (int i = 0; i < 22; i++)
            tempData1.add(new TetgenPoint(i + 1, pointData.get(i)));
        for (int i = 22; i < 44; i++)
            tempData2.add(new TetgenPoint(i + 1 - 22, pointData.get(i)));
        
        c.addChild(new Part("Part <1>",tempData1));
        c.addChild(new Part("Part <2>",tempData2));
        hm.getHistionMap().get(0).addChild(c);
        
        //hm.getHistionMap().get(0).getItemMap().get(0).addChild(new Part("Part <1>",tempData1));
        //hm.getHistionMap().get(0).getItemMap().get(0).addChild(new Part("Part <2>",tempData2));
        //c = new Cell(c.getId(), "Cell <1>", 0, 0, 0, 0, 0, facetData, maxNumberOfVertices, Color.BLUE, Color.LIGHTBLUE, -1, 0, true, hm.getHistionMap().get(0).getItemMap().get(0).getItemMap());
        //m.getHistionMap().get(0).addChild(c);
        //sm.addShape(new Shape("1", 0, 0, 0, 0, 0, pointData, facetData, maxNumberOfVertices, Color.BLUE, Color.LIGHTBLUE, nodeAvg, -1, 0));
        //hm.getAllHistions().get(0).getItems().get(0).addChild(new Part("Part <1>"));
        //sm.addShape(new Shape("Shape3", 0, 0, 0, 0, 0, pointData, holeData, polygonsInFacetData, holesInFacetData, facetNumber, maxNumberOfVertices, Color.BLUE, Color.LIGHTBLUE));
        //System.out.println("----");
        
        //addCell(c);
        //intersectionsWithEdges(c.getId());
        
        pointData = FXCollections.observableArrayList();
        pointData.add(new TetgenPoint(1,100,100,90));
        pointData.add(new TetgenPoint(2,120,100,120));
        pointData.add(new TetgenPoint(3,180,100,120));
        pointData.add(new TetgenPoint(4,200,100,90));
        pointData.add(new TetgenPoint(5,200,100,-90));
        pointData.add(new TetgenPoint(6,180,100,-120));
        pointData.add(new TetgenPoint(7,120,100,-120));
        pointData.add(new TetgenPoint(8,100,100,-90));
        
        pointData.add(new TetgenPoint(9,100,-100,90));
        pointData.add(new TetgenPoint(10,120,-100,120));
        pointData.add(new TetgenPoint(11,180,-100,120));
        pointData.add(new TetgenPoint(12,200,-100,90));
        pointData.add(new TetgenPoint(13,200,-100,-90));
        pointData.add(new TetgenPoint(14,180,-100,-120));
        pointData.add(new TetgenPoint(15,120,-100,-120));
        pointData.add(new TetgenPoint(16,100,-100,-90));
        
        /*pointSize = pointData.size();
        nodeAvg = new Point3D(0,0,0);
        for (int i = 0; i < pointSize; i++) {
            Point3D n = new Point3D(pointData.get(i).getX(), 
                    pointData.get(i).getY(),
                    pointData.get(i).getZ());
            nodeAvg.add(n.getX(), n.getY(), n.getZ());
        }
        nodeAvg = new Point3D(nodeAvg.getX() / pointSize, nodeAvg.getY() / pointSize, nodeAvg.getZ() / pointSize);*/
        
        facetData = FXCollections.observableArrayList();
        facetData.add(new TetgenFacet(1, 2, 1, 9, 10, 
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
        facetData.add(new TetgenFacet(2, 3, 2, 10, 11, 
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
        facetData.add(new TetgenFacet(3, 4, 3, 11, 12, 
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
        facetData.add(new TetgenFacet(4, 5, 4, 12, 13, 
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
        facetData.add(new TetgenFacet(5, 6, 5, 13, 14, 
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
        facetData.add(new TetgenFacet(6, 7, 6, 14, 15, 
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
        facetData.add(new TetgenFacet(7, 8, 7, 15, 16, 
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
        facetData.add(new TetgenFacet(8, 1, 8, 16, 9, 
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
        
        facetData.add(new TetgenFacet(9, 1, 2, 3, 4, 
                5, 6, 7, 8, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
        facetData.add(new TetgenFacet(10, 9, 10, 11, 12, 
                13, 14, 15, 16, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
        
        maxNumberOfVertices = 8;
        
        //c = new Cell("Cell <2>", 0, 0, 0, 0, 0, FXCollections.observableArrayList(), 3,
        //                Color.RED, Color.RED, -1, 0, false, FXCollections.emptyObservableMap());
        
        c = new Cell("Cell <2>", 0, 0, 0, 0, 0, facetData, maxNumberOfVertices, Color.DARKRED, Color.RED, 0, true);
        
        tempData1 = FXCollections.observableArrayList();
        tempData2 = FXCollections.observableArrayList();
        for (int i = 0; i < 8; i++)
            tempData1.add(new TetgenPoint(i + 1, pointData.get(i)));
        for (int i = 8; i < 16; i++)
            tempData2.add(new TetgenPoint(i + 1 - 8, pointData.get(i)));
        
        c.addChild(new Part("Part <1>",tempData1));
        c.addChild(new Part("Part <2>",tempData2));
        hm.getHistionMap().get(0).addChild(c);
        
        //hm.getHistionMap().get(0).getItemMap().get(1).addChild(new Part("Part <1>",tempData1));
        //hm.getHistionMap().get(0).getItemMap().get(1).addChild(new Part("Part <2>",tempData2));
        
        
        //c = new Cell(c.getId(), "Cell <2>", 0, 0, 0, 0, 0, facetData, maxNumberOfVertices, Color.DARKRED, Color.RED, -1, 0, true, hm.getHistionMap().get(0).getItemMap().get(1).getItemMap());
        //hm.getHistionMap().get(0).addChild(c);
        //sm.addShape(new Shape("2", 0, 0, 0, 0, 0, pointData, facetData, maxNumberOfVertices, Color.DARKRED, Color.RED, nodeAvg, -1, 0));
        hm.addHistion(new Histion("Histion <2>",0,0,0,0,0));
        hm.deleteHistion(1);
        //addCell(c);
        //intersectionsWithEdges(c.getId());
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
        
        /*if (c.getCopiedId() > -1) {
            nodesList.put(c.getId(), new double[nodesList.get(c.getCopiedId()).length]);
            tetrahedronsList.put(c.getId(), new int[tetrahedronsList.get(c.getCopiedId()).length]);
            facesList.put(c.getId(), new int[facesList.get(c.getCopiedId()).length]);
            System.arraycopy(nodesList.get(c.getCopiedId()), 0, nodesList.get(c.getId()), 0, nodesList.get(c.getCopiedId()).length);
            System.arraycopy(tetrahedronsList.get(c.getCopiedId()), 0, tetrahedronsList.get(c.getId()), 0, tetrahedronsList.get(c.getCopiedId()).length);
            System.arraycopy(facesList.get(c.getCopiedId()), 0, facesList.get(c.getId()), 0, facesList.get(c.getCopiedId()).length);
            colorsList.put(c.getId(), c.getDiffuseColor());
            MeshView newMeshView = new MeshView(shapeMap.get(c.getCopiedId()).getMesh());
            final PhongMaterial phongMaterial = new PhongMaterial();
            phongMaterial.setDiffuseColor(c.getDiffuseColor());
            phongMaterial.setSpecularColor(c.getSpecularColor());
            newMeshView.setMaterial(phongMaterial);
            shapeGroup.getChildren().add(newMeshView);
            shapeMap.put(c.getId(), newMeshView);
            c.setCopiedId(-1);
            return;
        }*/
        
        /*} else if (s.getCopiedId() == -2) {
            shapeMap.get(s.getId()).setTranslateX(s.getXCoordinate());
            shapeMap.get(s.getId()).setTranslateY(s.getYCoordinate());
            shapeMap.get(s.getId()).setTranslateZ(s.getZCoordinate());
            
            double angX = Math.toRadians(s.getXRotate());
            double tempVal;
            double angY = Math.toRadians(s.getYRotate());
            
            for (int i = 0; i < nodesList.get(s.getId()).length; i+=3) {
                
                tempVal = nodesList.get(s.getId())[i + 1];
                nodesList.get(s.getId())[i + 1] = tempVal * Math.cos(angX) - nodesList.get(s.getId())[i + 2] * Math.sin(angX);
                nodesList.get(s.getId())[i + 2] = tempVal * Math.sin(angX) + nodesList.get(s.getId())[i + 2] * Math.cos(angX);
                
                tempVal = nodesList.get(s.getId())[i];
                nodesList.get(s.getId())[i] = tempVal * Math.cos(angY) + nodesList.get(s.getId())[i + 2] * Math.sin(angY);
                nodesList.get(s.getId())[i + 2] = -tempVal * Math.sin(angY) + nodesList.get(s.getId())[i + 2] * Math.cos(angY);
                
                nodesList.get(s.getId())[i] += s.getXCoordinate();
                nodesList.get(s.getId())[i + 1] += s.getYCoordinate();
                nodesList.get(s.getId())[i + 2] += s.getZCoordinate();
            }
            return;
        }*/
        
        //ObservableList<TetgenPoint> pointData = c.getPointData();
        ObservableList<TetgenPoint> pointData = FXCollections.observableArrayList();
        
        //ObservableList<TetgenPoint> tempPointData = FXCollections.observableArrayList();
        hm.getHistionMap().get(c.getHistionId()).getItemMap().get(c.getId()).getItems().forEach(p -> {
            //System.out.println("Part");
            for (TetgenPoint point : p.getPointData()) {
                //System.out.println("Point");
                pointData.add(new TetgenPoint(point));
            }
        });
        //System.out.println(pointData.size());
        //System.out.println(pointData.size());
        //return pointData;
        
        /*for (int i = 0; i < tempPointData.size(); i++)
            pointData.add(new TetgenPoint(tempPointData.get(i)));*/
        /*for (int i = 0; i < c.getPointData().size(); i++)
            pointData.add(new TetgenPoint(c.getPointData().get(i)));*/
        
        //Cell c = hm.getHistionMap().get(s.getHistionId()).getItemMap().get(s.getCellId());
        double xRot = c.getXRotate();
        double yRot = c.getYRotate();
        double xTran = c.getXCoordinate();
        double yTran = c.getYCoordinate();
        double zTran = c.getZCoordinate();
        
        dataSize = 0;
        nodeAvg = new Point3D(0, 0, 0);
        Cell cellValue = hm.getHistionMap().get(c.getHistionId()).getItemMap().get(c.getId());
        hm.getHistionMap().get(c.getHistionId()).getItemMap().get(c.getId()).getItems().forEach(p -> {
            ObservableList<TetgenPoint> data = FXCollections.observableArrayList();
            for (TetgenPoint point : p.getPointData()) {
                    data.add(new TetgenPoint(point));
                }
            dataSize += data.size();
            for (int i = 0; i < data.size(); i++) {
                nodeAvg.add(data.get(i).getX() + cellValue.getXCoordinate(), data.get(i).getY() + cellValue.getYCoordinate(), data.get(i).getZ() + cellValue.getZCoordinate());
            }
        });
        nodeAvg = new Point3D(nodeAvg.getX() / dataSize, nodeAvg.getY() / dataSize, nodeAvg.getZ() / dataSize);
        
        applyTransformations(xRot, yRot, xTran, yTran, zTran, nodeAvg, pointData);
        
        dataSize = 0;
        nodeAvg = new Point3D(0, 0, 0);
        Histion h = hm.getHistionMap().get(c.getHistionId());
        h.getItems().forEach(cell -> {
            //ObservableList<TetgenPoint> data = sm.getShapeMap().get(cell.getId()).getPointData();
            //ObservableList<TetgenPoint> data = cell.getPointData();
            //ObservableList<TetgenPoint> data = cell.getPointData();
            ObservableList<TetgenPoint> data = FXCollections.observableArrayList();
            hm.getHistionMap().get(cell.getHistionId()).getItemMap().get(cell.getId()).getItems().forEach(p -> {
                for (TetgenPoint point : p.getPointData()) {
                    data.add(new TetgenPoint(point));
                }
            });
            dataSize += data.size();
            for (int i = 0; i < data.size(); i++) {
                nodeAvg.add(data.get(i).getX() + cell.getXCoordinate(), data.get(i).getY() + cell.getYCoordinate(), data.get(i).getZ() + cell.getZCoordinate());
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
        
        ObservableList<TetgenFacet> facetData = c.getFacetData();
        int numberOfFacets = facetData.size();
        
        int[] numberOfPolygonsInFacet = new int[numberOfFacets];
        for (int i = 0; i < numberOfFacets; i++)
            numberOfPolygonsInFacet[i] = 1;
        
        int[] numberOfVerticesInPolygon = new int[numberOfFacets];
        for (int i = 0; i < numberOfFacets; i++) {
            ////////////////////////////////
            for (int j = 0; j < 30; j++) {
                if (facetData.get(i).getVertex(j + 1) == 0)
                    break;
                numberOfVerticesInPolygon[i]++;
            }
        }
        
        count = 0;
        for (int i = 0; i < numberOfFacets; i++) {
            count += numberOfVerticesInPolygon[i];
        }
        
        int[] vertexList = new int[count];
        count = 0;
        for (int i = 0; i < numberOfFacets; i++) {
            ////////////////////////////////
            for (int j = 0; j < numberOfVerticesInPolygon[i]; j++) {
                vertexList[count] = facetData.get(i).getVertex(j + 1);
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
        
        
        /*shape.setOnMouseEntered(e->{
            PickResult pr = e.getPickResult();
            System.out.println(pr.getIntersectedFace());
            //System.out.println(pr.getIntersectedPoint());
        });*/
        
        
        //xRotateList.put(s.getId(), new Rotate(0, Rotate.X_AXIS));
        //yRotateList.put(s.getId(), new Rotate(0, Rotate.Y_AXIS));

        shapeGroup.getChildren().add(shape);
        shapeMap.put(c.getId(), shape);
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
            
            
        
        
        
        /*final PhongMaterial phongMaterial = new PhongMaterial();
        phongMaterial.setDiffuseColor(Color.BLACK);
        phongMaterial.setSpecularColor(Color.BLACK);
        
        ObservableList<TetgenPoint> pointData = FXCollections.observableArrayList();
        pointData.add(new TetgenPoint(1,-100,100,90));
        pointData.add(new TetgenPoint(2,-120,100,120));
        pointData.add(new TetgenPoint(3,-110,100,130));
        pointData.add(new TetgenPoint(4,-90,100,100));
        pointData.add(new TetgenPoint(5,-20,100,100));
        pointData.add(new TetgenPoint(6,0,100,140));
        pointData.add(new TetgenPoint(7,20,100,100));
        pointData.add(new TetgenPoint(8,90,100,100));
        pointData.add(new TetgenPoint(9,110,100,130));
        pointData.add(new TetgenPoint(10,120,100,120));
        pointData.add(new TetgenPoint(11,100,100,90));
        pointData.add(new TetgenPoint(12,100,100,-90));
        pointData.add(new TetgenPoint(13,120,100,-120));
        pointData.add(new TetgenPoint(14,110,100,-130));
        pointData.add(new TetgenPoint(15,90,100,-100));
        pointData.add(new TetgenPoint(16,20,100,-100));
        pointData.add(new TetgenPoint(17,0,100,-140));
        pointData.add(new TetgenPoint(18,-20,100,-100));
        pointData.add(new TetgenPoint(19,-90,100,-100));
        pointData.add(new TetgenPoint(20,-110,100,-130));
        pointData.add(new TetgenPoint(21,-120,100,-120));
        pointData.add(new TetgenPoint(22,-100,100,-90));
        
        pointData.add(new TetgenPoint(23,-100,-100,90));
        pointData.add(new TetgenPoint(24,-120,-100,120));
        pointData.add(new TetgenPoint(25,-110,-100,130));
        pointData.add(new TetgenPoint(26,-90,-100,100));
        pointData.add(new TetgenPoint(27,-20,-100,100));
        pointData.add(new TetgenPoint(28,0,-100,140));
        pointData.add(new TetgenPoint(29,20,-100,100));
        pointData.add(new TetgenPoint(30,90,-100,100));
        pointData.add(new TetgenPoint(31,110,-100,130));
        pointData.add(new TetgenPoint(32,120,-100,120));
        pointData.add(new TetgenPoint(33,100,-100,90));
        pointData.add(new TetgenPoint(34,100,-100,-90));
        pointData.add(new TetgenPoint(35,120,-100,-120));
        pointData.add(new TetgenPoint(36,110,-100,-130));
        pointData.add(new TetgenPoint(37,90,-100,-100));
        pointData.add(new TetgenPoint(38,20,-100,-100));
        pointData.add(new TetgenPoint(39,0,-100,-140));
        pointData.add(new TetgenPoint(40,-20,-100,-100));
        pointData.add(new TetgenPoint(41,-90,-100,-100));
        pointData.add(new TetgenPoint(42,-110,-100,-130));
        pointData.add(new TetgenPoint(43,-120,-100,-120));
        pointData.add(new TetgenPoint(44,-100,-100,-90));
        
        pointData.add(new TetgenPoint(23,-100,-200,90));
        pointData.add(new TetgenPoint(24,-120,-200,120));
        pointData.add(new TetgenPoint(25,-110,-200,130));
        pointData.add(new TetgenPoint(26,-90,-200,100));
        pointData.add(new TetgenPoint(27,-20,-200,100));
        pointData.add(new TetgenPoint(28,0,-200,140));
        pointData.add(new TetgenPoint(29,20,-200,100));
        pointData.add(new TetgenPoint(30,90,-200,100));
        pointData.add(new TetgenPoint(31,110,-200,130));
        pointData.add(new TetgenPoint(32,120,-200,120));
        pointData.add(new TetgenPoint(33,100,-200,90));
        pointData.add(new TetgenPoint(34,100,-200,-90));
        pointData.add(new TetgenPoint(35,120,-200,-120));
        pointData.add(new TetgenPoint(36,110,-200,-130));
        pointData.add(new TetgenPoint(37,90,-200,-100));
        pointData.add(new TetgenPoint(38,20,-200,-100));
        pointData.add(new TetgenPoint(39,0,-200,-140));
        pointData.add(new TetgenPoint(40,-20,-200,-100));
        pointData.add(new TetgenPoint(41,-90,-200,-100));
        pointData.add(new TetgenPoint(42,-110,-200,-130));
        pointData.add(new TetgenPoint(43,-120,-200,-120));
        pointData.add(new TetgenPoint(44,-100,-200,-90));
        
        ArrayList<Point3D> testList = new ArrayList<>();*/
        
        
        /*final ArrayList<MeshView> numMeshes = new ArrayList<>();
        Text3DMesh text = new Text3DMesh("X", "Arial", 40, true, 1, 0d, 1);
        for (MeshView m : text.getMeshes()) {
            numMeshes.add(new MeshView(m.getMesh()));
        }
        for (MeshView m : coordMeshes) {
            m.setMaterial(redMaterial);
            m.setTranslateX(axisLen / 2 + 10);
            m.setTranslateY(10);
            m.setTranslateZ(0);
            axisGroup.getChildren().add(m);
        }
        coordMeshes.clear();*/
        
        
        /*final PhongMaterial blackMaterial = new PhongMaterial();
        blackMaterial.setDiffuseColor(Color.BLACK);
        blackMaterial.setSpecularColor(Color.BLACK);
        final ArrayList<MeshView> numMeshes = new ArrayList<>();
        Text3DMesh t;
        int num = 0;
        for (TetgenPoint p : pointData) {
            //if (num == 30) break;
            System.out.println(num);
            Box c = new Box(2,2,2);
            c.setTranslateX(p.getX());
            c.setTranslateY(p.getY());
            c.setTranslateZ(p.getZ());
            c.setMaterial(phongMaterial);
            //shapeGroup.getChildren().add(c);
            
            String str = String.valueOf(num);
                
                
            //text = new Text3DMesh(String.valueOf(num), "Arial", 11, true, 1, 0d, 1);
            for (int i = 0; i < str.length(); i++) {
            t = new Text3DMesh(String.valueOf(str.charAt(i)), "Arial", 11, true, 1, 0d, 1);
            //text = new Text3DMesh("1", "Arial", 11, true, 1, 0d, 1);
            for (MeshView m : t.getMeshes()) {
                numMeshes.add(new MeshView(m.getMesh()));
            }
            for (MeshView m : numMeshes) {
                m.setMaterial(blackMaterial);
                m.setTranslateX(p.getX() - 5);
                m.setTranslateY(p.getY() - 5);
                m.setTranslateZ(p.getZ());
                axisGroup.getChildren().add(m);
            }
            numMeshes.clear();
            }
            num++;
        }*/
        
        
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
    
    //private volatile boolean isPicking=false;
   // private int picked;
    /*private Point3D vecIni, vecPos;
    private double distance;
    private MeshView mv;
    public Point3D unProjectDirection(double sceneX, double sceneY, double sWidth, double sHeight) {
        double tanHFov = Math.tan(Math.toRadians(camera.getFieldOfView()) * 0.5f);
        Point3D vMouse = new Point3D(tanHFov*(2*sceneX/sWidth-1), tanHFov*(2*sceneY/sWidth-sHeight/sWidth), 1);

        Point3D result = localToSceneDirection(vMouse);
        return result.normalize();
    }
    public Point3D localToScene(Point3D pt) {
        Point3D res = camera.localToParentTransformProperty().get().transform(pt);
        if (camera.getParent() != null) {
            res = camera.getParent().localToSceneTransformProperty().get().transform(res);
        }
        return res;
    }

    public Point3D localToSceneDirection(Point3D dir) {
        Point3D res = localToScene(dir);
        return res.subtract(localToScene(new Point3D(0, 0, 0)));
    }*/
    
    private double distance(Point3D p, Point3D q) {
        return Math.sqrt(Math.pow(q.getX() - p.getX(), 2) + Math.pow(q.getY() - p.getY(), 2) + Math.pow(q.getZ() - p.getZ(), 2));
    }
    
    private void handleMouseEvents() {
        
        /*Rotate rotateX = new Rotate(0, Rotate.X_AXIS);
        Rotate rotateY = new Rotate(0, Rotate.Y_AXIS);
        if (shapeGroup != null)
            shapeGroup.getTransforms().addAll(rotateX, rotateY);
        axisGroup.getTransforms().addAll(rotateX, rotateY);*/
        
        camera.getTransforms().clear();
        camera.getTransforms().addAll(rotateYCam, rotateXCam);
        
        scene.setOnMousePressed(me -> {         
            mouseOldX = me.getSceneX();
            mouseOldY = me.getSceneY();
            
            /*PickResult pr = me.getPickResult();
            if(pr!=null && pr.getIntersectedNode() != null && pr.getIntersectedNode() instanceof MeshView){
                Point3D intPoint = pr.getIntersectedPoint();
                for (int i = 0; i < meshMap.get((long)0).getPoints().size(); i+=3) {
                    if (distance(new Point3D(meshMap.get((long)0).getPoints().get(i), meshMap.get((long)0).getPoints().get(i + 1), meshMap.get((long)0).getPoints().get(i + 2)),
                            intPoint) < 3) {
                        isPicking=true;
                        picked = i;
                    }
                }
                //System.out.println(pr.getIntersectedPoint());
                //distance=pr.getIntersectedDistance();
                //mv = (MeshView) pr.getIntersectedNode();
                //vecIni = unProjectDirection(mousePosX, mousePosY, scene.getWidth(),scene.getHeight());
            }*/
        });
        
        /*scene.setOnMouseReleased(me -> {
            if(isPicking){
                isPicking=false;
            }
        });*/
        
        scene.setOnMouseDragged(me -> {
            
            /*if (me.isSecondaryButtonDown()) {
                mousePosX = me.getSceneX();
                mousePosY = me.getSceneY();
                rotateX.setAngle(rotateX.getAngle()+(mousePosY - mouseOldY)*0.05);
                rotateY.setAngle(rotateY.getAngle()-(mousePosX - mouseOldX)*0.05);
                mouseOldX = mousePosX;
                mouseOldY = mousePosY;
            }*/
            
            if (me.isPrimaryButtonDown()) {
                mousePosX = me.getSceneX();
                mousePosY = me.getSceneY();
                
                
                /*if(isPicking){
                    double dx = (mouseOldX - mousePosX);
                    double dy = (mouseOldY - mousePosY);
                    System.out.println(dx);
                    System.out.println(dy);
                    meshMap.get((long)0).getPoints().set(picked, meshMap.get((long)0).getPoints().get(picked) + (float)dx);
                    meshMap.get((long)0).getPoints().set(picked + 1, meshMap.get((long)0).getPoints().get(picked + 1) + (float)dy);
                    //System.out.println("Dragged");
                    /*vecPos = unProjectDirection(mousePosX, mousePosY, scene.getWidth(),scene.getHeight());
                    Point3D p=vecPos.subtract(vecIni).multiply(distance);
                    mv.getTransforms().add(new Translate(p.getX(),p.getY(),p.getZ()));
                    vecIni=vecPos;
                    PickResult pr = me.getPickResult();
                    if(pr!=null && pr.getIntersectedNode() != null && pr.getIntersectedNode()==mv){
                        distance=pr.getIntersectedDistance();
                    } else {
                        isPicking=false;
                    }*/
                //} else {
                
                double angX = rotateXCam.getAngle()-(mousePosY - mouseOldY)*0.05;
                double angY = rotateYCam.getAngle()+(mousePosX - mouseOldX)*0.05;
                
                mouseOldX = mousePosX;
                mouseOldY = mousePosY;
                CameraView.setXRotate(String.valueOf(angX));
                CameraView.setYRotate(String.valueOf(angY));
                //}
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
    
    ListChangeListener<Integer> hideShapeListChangeListener = (change) -> {
        while (change.next()) {
            if (change.wasAdded()) {
                for(Integer id : change.getAddedSubList()) {
                    shapeGroup.getChildren().remove(shapeMap.get(id));
                }
            } else {
                for(Integer id : change.getRemoved()) {
                    shapeGroup.getChildren().add(shapeMap.get(id));
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
        CameraView.getShapeIdToHideList().addListener(hideShapeListChangeListener);
    }
    
    private void removeCameraViewListener() {
        CameraView.xRotateProperty().removeListener(xRotListener);
        CameraView.yRotateProperty().removeListener(yRotListener);
        CameraView.xCoordinateProperty().removeListener(xPosListener);
        CameraView.yCoordinateProperty().removeListener(yPosListener);
        CameraView.zCoordinateProperty().removeListener(zPosListener);
        CameraView.FOVProperty().removeListener(FOVListener); 
        CameraView.getShapeIdToHideList().removeListener(hideShapeListChangeListener);
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
                    //if (c.getFacetData().size() > 0)
                    if (c.getShow())
                        intersectionsWithEdges(c.getId());
                });
            });
            //sm.getAllShapes().forEach(s -> intersectionsWithEdges(s.getId()));
            CrossSection.setChanged(false);
        }
    };
    
    ChangeListener<Boolean> updateListener = (v, oldValue, newValue) -> {
        if (newValue) {
            CrossSectionViewerTopComponent.clear();
            hm.getAllHistions().forEach(h -> {
                h.getItems().forEach(c -> {
                    //if (c.getFacetData().size() > 0)
                    if (c.getShow())
                        intersectionsWithEdges(c.getId());
                });
            });
            //sm.getAllShapes().forEach(s -> intersectionsWithEdges(s.getId()));
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
