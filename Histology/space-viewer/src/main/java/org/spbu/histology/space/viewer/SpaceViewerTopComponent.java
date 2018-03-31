package org.spbu.histology.space.viewer;

import org.spbu.histology.tetgen.TetgenResult;
import org.spbu.histology.tetgen.Tetgen;
import org.spbu.histology.model.CameraView;
import org.spbu.histology.model.Shape;
import org.spbu.histology.model.ShapeManager;
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
import javafx.scene.shape.Polygon;
import javafx.scene.text.Text;
import org.openide.LifecycleManager;
import org.spbu.histology.cross.section.viewer.CrossSectionViewerTopComponent;
import org.spbu.histology.model.Cell;
import org.spbu.histology.model.CrossSection;
import org.spbu.histology.model.Histion;
import org.spbu.histology.model.HistionManager;
import org.spbu.histology.model.Node;
import org.spbu.histology.model.Part;
import org.spbu.histology.model.TetgenFacetHole;
import org.spbu.histology.model.TetgenFacetHoleComparator;
import org.spbu.histology.model.TetgenFacetPolygon;
import org.spbu.histology.model.TetgenFacetPolygonComparator;
import org.spbu.histology.model.TetgenPoint;

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
    
    private ShapeManager sm = null;
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
    
    private final ObservableMap<Long, MeshView> shapeMap = 
            FXCollections.observableMap(new ConcurrentHashMap());
    private final ObservableMap<Long, Color> colorsList = 
            FXCollections.observableMap(new ConcurrentHashMap());
    private final ObservableMap<Long, double[]> nodesList = 
            FXCollections.observableMap(new ConcurrentHashMap());
    private final ObservableMap<Long, int[]> tetrahedronsList = 
            FXCollections.observableMap(new ConcurrentHashMap());
    private final ObservableMap<Long, int[]> facesList = 
            FXCollections.observableMap(new ConcurrentHashMap());
    private final ObservableMap<Long, ArrayList<Polygon>> polygonList = 
            FXCollections.observableMap(new ConcurrentHashMap());
    /*private final ObservableMap<Long, Rotate> xRotateList = 
            FXCollections.observableMap(new ConcurrentHashMap());
    private final ObservableMap<Long, Rotate> yRotateList = 
            FXCollections.observableMap(new ConcurrentHashMap());*/

    private final ArrayList<Node> intersectionNodes = new ArrayList();
    
    private final double EPS = 0.0000001;
    
    private void returnNodeListToOriginal(Shape s) {
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
    }
    
    private final MapChangeListener<Long, Shape> shapeListener =
            (change) -> {
                if (change.wasRemoved() && change.wasAdded()) {
                    Shape s = (Shape)change.getValueAdded();
                    Shape removedShape = (Shape)change.getValueRemoved();
                    CrossSectionViewerTopComponent.clearPolygonArray(polygonList.get(s.getId()));
                    /*if (s.getCopiedId() == -2) {
                        returnNodeListToOriginal(removedShape);
                    }
                    else {
                        shapeGroup.getChildren().remove(shapeMap.get(removedShape.getId()));
                    }*/
                    shapeGroup.getChildren().remove(shapeMap.get(removedShape.getId()));
                    addShape(s);
                    intersectionsWithEdges(change.getKey());
                }
                else if (change.wasRemoved()) {  
                    Long removedShapeId = ((Shape)change.getValueRemoved()).getId();
                    shapeGroup.getChildren().remove(shapeMap.get(removedShapeId));
                    shapeMap.remove(removedShapeId);
                    nodesList.remove(removedShapeId);
                    tetrahedronsList.remove(removedShapeId);
                    facesList.remove(removedShapeId);
                    colorsList.remove(removedShapeId);
                    CrossSectionViewerTopComponent.clearPolygonArray(polygonList.get(removedShapeId));
                    polygonList.remove(removedShapeId);
                }
                else if (change.wasAdded()) {
                    Shape addedShape = (Shape)change.getValueAdded();
                    addShape(addedShape);
                    intersectionsWithEdges(change.getKey());
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
        
        sm = Lookup.getDefault().lookup(ShapeManager.class);
        if (sm == null) {
            LifecycleManager.getDefault().exit();
        }
        
        hm = Lookup.getDefault().lookup(HistionManager.class);
        if (hm == null) {
            LifecycleManager.getDefault().exit();
        }
        
        sm.addListener(shapeListener);        
        buildData();        
        if (!sm.getAllShapes().isEmpty())
            handleMouseEvents();  
        
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
    
    private void intersectionsWithEdges(long id) {
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
        pointData.add(new TetgenPoint(1,-100,90,100));
        pointData.add(new TetgenPoint(2,-120,120,100));
        pointData.add(new TetgenPoint(3,-110,130,100));
        pointData.add(new TetgenPoint(4,-90,100,100));
        pointData.add(new TetgenPoint(5,-20,100,100));
        pointData.add(new TetgenPoint(6,0,140,100));
        pointData.add(new TetgenPoint(7,20,100,100));
        pointData.add(new TetgenPoint(8,90,100,100));
        pointData.add(new TetgenPoint(9,110,130,100));
        pointData.add(new TetgenPoint(10,120,120,100));
        pointData.add(new TetgenPoint(11,100,90,100));
        pointData.add(new TetgenPoint(12,100,-90,100));
        pointData.add(new TetgenPoint(13,120,-120,100));
        pointData.add(new TetgenPoint(14,110,-130,100));
        pointData.add(new TetgenPoint(15,90,-100,100));
        pointData.add(new TetgenPoint(16,20,-100,100));
        pointData.add(new TetgenPoint(17,0,-140,100));
        pointData.add(new TetgenPoint(18,-20,-100,100));
        pointData.add(new TetgenPoint(19,-90,-100,100));
        pointData.add(new TetgenPoint(20,-110,-130,100));
        pointData.add(new TetgenPoint(21,-120,-120,100));
        pointData.add(new TetgenPoint(22,-100,-90,100));
        
        pointData.add(new TetgenPoint(23,-100,90,-100));
        pointData.add(new TetgenPoint(24,-120,120,-100));
        pointData.add(new TetgenPoint(25,-110,130,-100));
        pointData.add(new TetgenPoint(26,-90,100,-100));
        pointData.add(new TetgenPoint(27,-20,100,-100));
        pointData.add(new TetgenPoint(28,0,140,-100));
        pointData.add(new TetgenPoint(29,20,100,-100));
        pointData.add(new TetgenPoint(30,90,100,-100));
        pointData.add(new TetgenPoint(31,110,130,-100));
        pointData.add(new TetgenPoint(32,120,120,-100));
        pointData.add(new TetgenPoint(33,100,90,-100));
        pointData.add(new TetgenPoint(34,100,-90,-100));
        pointData.add(new TetgenPoint(35,120,-120,-100));
        pointData.add(new TetgenPoint(36,110,-130,-100));
        pointData.add(new TetgenPoint(37,90,-100,-100));
        pointData.add(new TetgenPoint(38,20,-100,-100));
        pointData.add(new TetgenPoint(39,0,-140,-100));
        pointData.add(new TetgenPoint(40,-20,-100,-100));
        pointData.add(new TetgenPoint(41,-90,-100,-100));
        pointData.add(new TetgenPoint(42,-110,-130,-100));
        pointData.add(new TetgenPoint(43,-120,-120,-100));
        pointData.add(new TetgenPoint(44,-100,-90,-100));
        
        int pointSize = pointData.size();
        Node nodeAvg = new Node(0,0,0);
        for (int i = 0; i < pointSize; i++) {
            Node n = new Node(pointData.get(i).getX(), 
                    pointData.get(i).getY(),
                    pointData.get(i).getZ());
            nodeAvg.x += n.x;
            nodeAvg.y += n.y;
            nodeAvg.z += n.z;
        }
        nodeAvg.x /= pointSize;
        nodeAvg.y /= pointSize;
        nodeAvg.z /= pointSize;
        
        int facetNumber = 24;
        
        ObservableList<TetgenFacetPolygon> polygonsInFacetData = FXCollections.observableArrayList();
        polygonsInFacetData.add(new TetgenFacetPolygon(1, 1, 1, 2, 1, 23, 24, 
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
        polygonsInFacetData.add(new TetgenFacetPolygon(2, 2, 1, 3, 2, 24, 25, 
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
        polygonsInFacetData.add(new TetgenFacetPolygon(3, 3, 1, 4, 3, 25, 26, 
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
        polygonsInFacetData.add(new TetgenFacetPolygon(4, 4, 1, 5, 4, 26, 27, 
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
        polygonsInFacetData.add(new TetgenFacetPolygon(5, 5, 1, 6, 5, 27, 28, 
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
        polygonsInFacetData.add(new TetgenFacetPolygon(6, 6, 1, 7, 6, 28, 29, 
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
        polygonsInFacetData.add(new TetgenFacetPolygon(7, 7, 1, 8, 7, 29, 30, 
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
        polygonsInFacetData.add(new TetgenFacetPolygon(8, 8, 1, 9, 8, 30, 31, 
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
        polygonsInFacetData.add(new TetgenFacetPolygon(9, 9, 1, 10, 9, 31, 32, 
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
        polygonsInFacetData.add(new TetgenFacetPolygon(10, 10, 1, 11, 10, 32, 33, 
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
        polygonsInFacetData.add(new TetgenFacetPolygon(11, 11, 1, 12, 11, 33, 34, 
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
        polygonsInFacetData.add(new TetgenFacetPolygon(12, 12, 1, 13, 12, 34, 35, 
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
        polygonsInFacetData.add(new TetgenFacetPolygon(13, 13, 1, 14, 13, 35, 36, 
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
        polygonsInFacetData.add(new TetgenFacetPolygon(14, 14, 1, 15, 14, 36, 37, 
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
        polygonsInFacetData.add(new TetgenFacetPolygon(15, 15, 1, 16, 15, 37, 38, 
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
        polygonsInFacetData.add(new TetgenFacetPolygon(16, 16, 1, 17, 16, 38, 39, 
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
        polygonsInFacetData.add(new TetgenFacetPolygon(17, 17, 1, 18, 17, 39, 40, 
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
        polygonsInFacetData.add(new TetgenFacetPolygon(18, 18, 1, 19, 18, 40, 41, 
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
        polygonsInFacetData.add(new TetgenFacetPolygon(19, 19, 1, 20, 19, 41, 42, 
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
        polygonsInFacetData.add(new TetgenFacetPolygon(20, 20, 1, 21, 20, 42, 43, 
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
        polygonsInFacetData.add(new TetgenFacetPolygon(21, 21, 1, 22, 21, 43, 44, 
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
        polygonsInFacetData.add(new TetgenFacetPolygon(22, 22, 1, 1, 22, 44, 23, 
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
        
        polygonsInFacetData.add(new TetgenFacetPolygon(23, 23, 1, 1, 2, 3, 4, 
                5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 0, 0, 0, 0, 0, 0, 0, 0));
        polygonsInFacetData.add(new TetgenFacetPolygon(24, 24, 1, 23, 24, 25, 26, 
                27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 0, 0, 0, 0, 0, 0, 0, 0));
        
        ObservableList<TetgenPoint> holeData = FXCollections.observableArrayList();
        ObservableList<TetgenFacetHole> holesInFacetData = FXCollections.observableArrayList();
        
        int maxNumberOfVertices = 22;

        hm.addHistion(new Histion("Histion <1>",0,0,0,0,0));
        //hm.getAllHistions().get(0).addChild(new Cell("Cell <1>"));
        hm.getHistionMap().get((long)0).addChild(new Cell("Cell <1>",0,0,0,0,0));
        hm.getHistionMap().get((long)0).getItemMap().get((long)0).addChild(new Part("Part <1>",0,0,0,0,0));
        sm.addShape(new Shape("1", 0, 0, 0, 0, 0, pointData, holeData, polygonsInFacetData, holesInFacetData, facetNumber, maxNumberOfVertices, Color.BLUE, Color.LIGHTBLUE, nodeAvg, -1, 0, 0));
        //hm.getAllHistions().get(0).getItems().get(0).addChild(new Part("Part <1>"));
        //sm.addShape(new Shape("Shape3", 0, 0, 0, 0, 0, pointData, holeData, polygonsInFacetData, holesInFacetData, facetNumber, maxNumberOfVertices, Color.BLUE, Color.LIGHTBLUE));
        //System.out.println("----");
        
        pointData = FXCollections.observableArrayList();
        pointData.add(new TetgenPoint(1,100,90,100));
        pointData.add(new TetgenPoint(2,120,120,100));
        pointData.add(new TetgenPoint(3,180,120,100));
        pointData.add(new TetgenPoint(4,200,90,100));
        pointData.add(new TetgenPoint(5,200,-90,100));
        pointData.add(new TetgenPoint(6,180,-120,100));
        pointData.add(new TetgenPoint(7,120,-120,100));
        pointData.add(new TetgenPoint(8,100,-90,100));
        
        pointData.add(new TetgenPoint(9,100,90,-100));
        pointData.add(new TetgenPoint(10,120,120,-100));
        pointData.add(new TetgenPoint(11,180,120,-100));
        pointData.add(new TetgenPoint(12,200,90,-100));
        pointData.add(new TetgenPoint(13,200,-90,-100));
        pointData.add(new TetgenPoint(14,180,-120,-100));
        pointData.add(new TetgenPoint(15,120,-120,-100));
        pointData.add(new TetgenPoint(16,100,-90,-100));
        
        pointSize = pointData.size();
        nodeAvg = new Node(0,0,0);
        for (int i = 0; i < pointSize; i++) {
            Node n = new Node(pointData.get(i).getX(), 
                    pointData.get(i).getY(),
                    pointData.get(i).getZ());
            nodeAvg.x += n.x;
            nodeAvg.y += n.y;
            nodeAvg.z += n.z;
        }
        nodeAvg.x /= pointSize;
        nodeAvg.y /= pointSize;
        nodeAvg.z /= pointSize;
        
        facetNumber = 10;
        
        polygonsInFacetData = FXCollections.observableArrayList();
        polygonsInFacetData.add(new TetgenFacetPolygon(1, 1, 1, 2, 1, 9, 10, 
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
        polygonsInFacetData.add(new TetgenFacetPolygon(2, 2, 1, 3, 2, 10, 11, 
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
        polygonsInFacetData.add(new TetgenFacetPolygon(3, 3, 1, 4, 3, 11, 12, 
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
        polygonsInFacetData.add(new TetgenFacetPolygon(4, 4, 1, 5, 4, 12, 13, 
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
        polygonsInFacetData.add(new TetgenFacetPolygon(5, 5, 1, 6, 5, 13, 14, 
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
        polygonsInFacetData.add(new TetgenFacetPolygon(6, 6, 1, 7, 6, 14, 15, 
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
        polygonsInFacetData.add(new TetgenFacetPolygon(7, 7, 1, 8, 7, 15, 16, 
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
        polygonsInFacetData.add(new TetgenFacetPolygon(8, 8, 1, 1, 8, 16, 9, 
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
        
        polygonsInFacetData.add(new TetgenFacetPolygon(9, 9, 1, 1, 2, 3, 4, 
                5, 6, 7, 8, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
        polygonsInFacetData.add(new TetgenFacetPolygon(10, 10, 1, 9, 10, 11, 12, 
                13, 14, 15, 16, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
        
        holeData = FXCollections.observableArrayList();
        holesInFacetData = FXCollections.observableArrayList();
        
        maxNumberOfVertices = 8;
        
        hm.getHistionMap().get((long)0).addChild(new Cell("Cell <2>",0,0,0,0,0));
        hm.getHistionMap().get((long)0).getItemMap().get((long)1).addChild(new Part("Part <2>",0,0,0,0,0));
        sm.addShape(new Shape("2", 0, 0, 0, 0, 0, pointData, holeData, polygonsInFacetData, holesInFacetData, facetNumber, maxNumberOfVertices, Color.DARKRED, Color.RED, nodeAvg, -1, 0, 1));
        hm.addHistion(new Histion("Histion <2>",0,0,0,0,0));
        hm.deleteHistion(1);
    }
    
    private void applyTransformations(Shape s, double xRot, double yRot, 
            double xTran, double yTran, double zTran, Node nodeAvg,
            ObservableList<TetgenPoint> pointData,
            ObservableList<TetgenPoint> holeData,
            ObservableList<TetgenFacetHole> holesInFacetData) {
        double ang, tempVal;
        for (int i = 0; i < s.getPointData().size(); i++) {
            //TetgenPoint pd = new TetgenPoint(s.getPointData().get(i));
            TetgenPoint pd = new TetgenPoint(pointData.get(i));
            
            pd.setX(pd.getX() - nodeAvg.x);
            pd.setY(pd.getY() - nodeAvg.y);
            pd.setZ(pd.getZ() - nodeAvg.z);
            
            ang = Math.toRadians(xRot);
            tempVal = pd.getY();
            pd.setY(pd.getY() * Math.cos(ang) - pd.getZ() * Math.sin(ang));
            pd.setZ(tempVal * Math.sin(ang) + pd.getZ() * Math.cos(ang));
            
            ang = Math.toRadians(yRot);
            tempVal = pd.getX();
            pd.setX(pd.getX() * Math.cos(ang) + pd.getZ() * Math.sin(ang));
            pd.setZ(-tempVal * Math.sin(ang) + pd.getZ() * Math.cos(ang));
            
            pd.setX(pd.getX() + xTran + nodeAvg.x);
            pd.setY(pd.getY() + yTran + nodeAvg.y);
            pd.setZ(pd.getZ() + zTran + nodeAvg.z);
            
            //pointData.add(pd);
            pointData.set(i, pd);
        }
        for (int i = 0; i < s.getHoleData().size(); i++) {
            //TetgenPoint pd = new TetgenPoint(s.getHoleData().get(i));
            TetgenPoint pd = new TetgenPoint(holeData.get(i));
            
            pd.setX(pd.getX() - nodeAvg.x);
            pd.setY(pd.getY() - nodeAvg.y);
            pd.setZ(pd.getZ() - nodeAvg.z);
            
            ang = Math.toRadians(xRot);
            tempVal = pd.getY();
            pd.setY(pd.getY() * Math.cos(ang) - pd.getZ() * Math.sin(ang));
            pd.setZ(tempVal * Math.sin(ang) + pd.getZ() * Math.cos(ang));
            
            ang = Math.toRadians(yRot);
            tempVal = pd.getX();
            pd.setX(pd.getX() * Math.cos(ang) + pd.getZ() * Math.sin(ang));
            pd.setZ(-tempVal * Math.sin(ang) + pd.getZ() * Math.cos(ang));
            
            pd.setX(pd.getX() + xTran + nodeAvg.x);
            pd.setY(pd.getY() + yTran + nodeAvg.y);
            pd.setZ(pd.getZ() + zTran + nodeAvg.z);
            
            //holeData.add(pd);
            holeData.set(i, pd);
        }
        for (int i = 0; i < s.getHolesInFacetData().size(); i++) {
            //TetgenFacetHole pd = new TetgenFacetHole(s.getHolesInFacetData().get(i));
            TetgenFacetHole pd = new TetgenFacetHole(holesInFacetData.get(i));
            
            pd.setX(pd.getX() - nodeAvg.x);
            pd.setY(pd.getY() - nodeAvg.y);
            pd.setZ(pd.getZ() - nodeAvg.z);
            
            ang = Math.toRadians(xRot);
            tempVal = pd.getY();
            pd.setY(pd.getY() * Math.cos(ang) - pd.getZ() * Math.sin(ang));
            pd.setZ(tempVal * Math.sin(ang) + pd.getZ() * Math.cos(ang));
            
            ang = Math.toRadians(yRot);
            tempVal = pd.getX();
            pd.setX(pd.getX() * Math.cos(ang) + pd.getZ() * Math.sin(ang));
            pd.setZ(-tempVal * Math.sin(ang) + pd.getZ() * Math.cos(ang));
            
            pd.setX(pd.getX() + xTran + nodeAvg.x);
            pd.setY(pd.getY() + yTran + nodeAvg.y);
            pd.setZ(pd.getZ() + zTran + nodeAvg.z);
            
            //holesInFacetData.add(pd);
            holesInFacetData.set(i, pd);
        }
    }
    
    int dataSize;
    Node nodeAvg;
    
    private void addShape(Shape s) {
        
        if (s.getCopiedId() > -1) {
            nodesList.put(s.getId(), new double[nodesList.get(s.getCopiedId()).length]);
            tetrahedronsList.put(s.getId(), new int[tetrahedronsList.get(s.getCopiedId()).length]);
            facesList.put(s.getId(), new int[facesList.get(s.getCopiedId()).length]);
            System.arraycopy(nodesList.get(s.getCopiedId()), 0, nodesList.get(s.getId()), 0, nodesList.get(s.getCopiedId()).length);
            System.arraycopy(tetrahedronsList.get(s.getCopiedId()), 0, tetrahedronsList.get(s.getId()), 0, tetrahedronsList.get(s.getCopiedId()).length);
            System.arraycopy(facesList.get(s.getCopiedId()), 0, facesList.get(s.getId()), 0, facesList.get(s.getCopiedId()).length);
            colorsList.put(s.getId(), s.getDiffuseColor());
            MeshView newMeshView = new MeshView(shapeMap.get(s.getCopiedId()).getMesh());
            final PhongMaterial phongMaterial = new PhongMaterial();
            phongMaterial.setDiffuseColor(s.getDiffuseColor());
            phongMaterial.setSpecularColor(s.getSpecularColor());
            newMeshView.setMaterial(phongMaterial);
            shapeGroup.getChildren().add(newMeshView);
            shapeMap.put(s.getId(), newMeshView);
            s.setCopiedId((long)-1);
            return;
        }
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
        
        ObservableList<TetgenPoint> pointData = FXCollections.observableArrayList();
        ObservableList<TetgenPoint> holeData = FXCollections.observableArrayList();
        ObservableList<TetgenFacetHole> holesInFacetData = FXCollections.observableArrayList();
        
        for (int i = 0; i < s.getPointData().size(); i++)
            pointData.add(new TetgenPoint(s.getPointData().get(i)));
        for (int i = 0; i < s.getHoleData().size(); i++)
            holeData.add(new TetgenPoint(s.getHoleData().get(i)));
        for (int i = 0; i < s.getHolesInFacetData().size(); i++)
            holesInFacetData.add(new TetgenFacetHole(s.getHolesInFacetData().get(i)));
        
        double xRot = s.getXRotate();
        double yRot = s.getYRotate();
        double xTran = s.getXCoordinate();
        double yTran = s.getYCoordinate();
        double zTran = s.getZCoordinate();
        applyTransformations(s, xRot, yRot, xTran, yTran, zTran, s.getNodeAvg(), pointData, holeData, holesInFacetData);
        
        dataSize = 0;
        nodeAvg = new Node(0,0,0);
        Cell c = hm.getHistionMap().get(s.getHistionId()).getItemMap().get(s.getCellId());
        c.getItems().forEach(p -> {
            ObservableList<TetgenPoint> data = sm.getShapeMap().get(p.getId()).getPointData();
            dataSize += data.size();
            for (int i = 0; i < data.size(); i++) {
                nodeAvg.x += data.get(i).getX() + p.getXCoordinate();
                nodeAvg.y += data.get(i).getY() + p.getYCoordinate();
                nodeAvg.z += data.get(i).getZ() + p.getZCoordinate();
            }
        });
        nodeAvg.x /= dataSize;
        nodeAvg.y /= dataSize;
        nodeAvg.z /= dataSize;
        xRot = c.getXRotate();
        yRot = c.getYRotate();
        xTran = c.getXCoordinate();
        yTran = c.getYCoordinate();
        zTran = c.getZCoordinate();
        applyTransformations(s, xRot, yRot, xTran, yTran, zTran, nodeAvg, pointData, holeData, holesInFacetData);
        
        dataSize = 0;
        nodeAvg = new Node(0,0,0);
        Histion h = hm.getHistionMap().get(s.getHistionId());
        h.getItems().forEach(cell -> {
            cell.getItems().forEach(p -> {
                ObservableList<TetgenPoint> data = sm.getShapeMap().get(p.getId()).getPointData();
                dataSize += data.size();
                for (int i = 0; i < data.size(); i++) {
                    nodeAvg.x += data.get(i).getX() + p.getXCoordinate();
                    nodeAvg.y += data.get(i).getY() + p.getYCoordinate();
                    nodeAvg.z += data.get(i).getZ() + p.getZCoordinate();
                }
            });
        });
        nodeAvg.x /= dataSize;
        nodeAvg.y /= dataSize;
        nodeAvg.z /= dataSize;
        xRot = h.getXRotate();
        yRot = h.getYRotate();
        xTran = h.getXCoordinate();
        yTran = h.getYCoordinate();
        zTran = h.getZCoordinate();
        applyTransformations(s, xRot, yRot, xTran, yTran, zTran, new Node(0, 0, 0), pointData, holeData, holesInFacetData);
        
        int numberOfNodes = pointData.size();
        double[] nodeList = new double[numberOfNodes * 3];
        int count = 0;
        for (int i = 0; i < numberOfNodes; i++) {
            nodeList[count] = pointData.get(i).getX();
            nodeList[count + 1] = pointData.get(i).getY();
            nodeList[count + 2] = pointData.get(i).getZ();
            count += 3;
        }
        
        int numberOfFacets = s.getFacetNumber();
        
        int[] numberOfPolygonsInFacet = new int[numberOfFacets];
        for (int i = 0; i < numberOfFacets; i++)
            numberOfPolygonsInFacet[i] = 0;
        ObservableList<TetgenFacetPolygon> polygonsDataSorted = s.getPolygonsInFacetData();
        int[] numberOfVerticesInPolygon = new int[polygonsDataSorted.size()];
        FXCollections.sort(polygonsDataSorted, new TetgenFacetPolygonComparator());
        count = 0;
        for (int i = 0; i < polygonsDataSorted.size(); i++) {
            numberOfPolygonsInFacet[polygonsDataSorted.get(i).getFacetNumber() - 1]++;
            ////////////////////////////////
            for (int j = 0; j < 30; j++) {
                if (polygonsDataSorted.get(i).getVertex(j + 1) == 0)
                    break;
                numberOfVerticesInPolygon[i]++;
                count++;
            }
        }
        
        int[] vertexList = new int[count];
        count = 0;
        for (int i = 0; i < polygonsDataSorted.size(); i++) {
            ////////////////////////////////
            for (int j = 0; j < 30; j++) {
                if (polygonsDataSorted.get(i).getVertex(j + 1) == 0)
                    break;
                vertexList[count] = polygonsDataSorted.get(i).getVertex(j + 1);
                count ++;
            }
        }
        
        int[] numberOfHolesInFacet = new int[numberOfFacets];
        for (int i = 0; i < numberOfFacets; i++)
            numberOfHolesInFacet[i] = 0;
        ObservableList<TetgenFacetHole> holesDataSorted = holesInFacetData; 
        FXCollections.sort(holesDataSorted, new TetgenFacetHoleComparator());
        double[] holeListInFacet = new double[holesDataSorted.size() * 3];
        count = 0;
        for (int i = 0; i < holesDataSorted.size(); i++) {
            numberOfHolesInFacet[holesDataSorted.get(i).getFacetNumber() - 1]++;
            holeListInFacet[count] = holesDataSorted.get(i).getX();
            holeListInFacet[count + 1] = holesDataSorted.get(i).getY();
            holeListInFacet[count + 2] = holesDataSorted.get(i).getZ();
            count += 3;
        }
        
        int numberOfHoles = holeData.size();
        double[] holeList = new double[numberOfHoles * 3];
        count = 0;
        for (int i = 0; i < numberOfHoles; i++) {
            holeList[count] = holeData.get(i).getX();
            holeList[count + 1] = holeData.get(i).getY();
            holeList[count + 2] = holeData.get(i).getZ();
            count += 3;
        }
        
        int numberOfRegions = 0;
        double[] regionList = new double[numberOfRegions * 5]; 
        
        TetgenResult tr = Tetgen.tetrahedralization(numberOfNodes, nodeList, 
                numberOfFacets, numberOfPolygonsInFacet, numberOfHolesInFacet, 
                holeListInFacet, numberOfVerticesInPolygon, vertexList, 
                numberOfHoles, holeList, numberOfRegions, regionList, "pq10000a1000000.0");
        
        nodesList.put(s.getId(), new double[tr.getNodeList().length]);
        tetrahedronsList.put(s.getId(), new int[tr.getTetrahedronList().length]);
        facesList.put(s.getId(), new int[tr.getFaceList().length]);
        System.arraycopy(tr.getNodeList(), 0, nodesList.get(s.getId()), 0, tr.getNodeList().length);
        System.arraycopy(tr.getTetrahedronList(), 0, tetrahedronsList.get(s.getId()), 0, tr.getTetrahedronList().length);
        System.arraycopy(tr.getFaceList(), 0, facesList.get(s.getId()), 0, tr.getFaceList().length);
        
        final PhongMaterial phongMaterial = new PhongMaterial();
        phongMaterial.setDiffuseColor(s.getDiffuseColor());
        colorsList.put(s.getId(), s.getDiffuseColor());
        phongMaterial.setSpecularColor(s.getSpecularColor());
        TriangleMesh shapeMesh = new TriangleMesh();
        shapeMesh.getTexCoords().addAll(0,0);
        for (int i = 0; i < tr.getNodeList().length; i++)
            shapeMesh.getPoints().addAll((float)tr.getNodeList()[i]);
        for (int i = 0; i < tr.getFaceList().length; i++)
            shapeMesh.getFaces().addAll(tr.getFaceList()[i] - 1,0);
        for (int i = tr.getFaceList().length - 1; i >= 0; i--)
            shapeMesh.getFaces().addAll(tr.getFaceList()[i] - 1,0);
        MeshView shape= new MeshView(shapeMesh);
        shape.setDrawMode(DrawMode.FILL);
        shape.setMaterial(phongMaterial);
        //xRotateList.put(s.getId(), new Rotate(0, Rotate.X_AXIS));
        //yRotateList.put(s.getId(), new Rotate(0, Rotate.Y_AXIS));

        shapeGroup.getChildren().add(shape);
        shapeMap.put(s.getId(), shape);
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
        
        Text xText = new Text("X");
        xText.setStyle("-fx-font-size: 80;");
        xText.setFill(Color.RED);
        xText.setCache(true);
        xText.translateXProperty().set(axisLen / 2);
        xText.translateYProperty().set(60);
        xText.translateZProperty().set(0);
        
        Text yText = new Text("Y");
        yText.setStyle("-fx-font-size: 80;");
        yText.setFill(Color.GREEN);
        yText.setCache(true);
        yText.translateXProperty().set(10);
        yText.translateYProperty().set(axisLen / 2);
        yText.translateZProperty().set(0);
        
        Text zText = new Text("Z");
        zText.setStyle("-fx-font-size: 80;");
        zText.setFill(Color.BLUE);
        zText.setCache(true);
        zText.translateXProperty().set(10);
        zText.translateYProperty().set(0);
        zText.translateZProperty().set(axisLen / 2);
        
        pyrYRot = new Rotate(90, Rotate.X_AXIS);
        zText.getTransforms().add(pyrYRot);
        
        axisGroup.getChildren().addAll(xText, yText, zText);
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
        });
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
                    case SHIFT:
                        CameraView.setYCoordinate(String.valueOf(camera.getTranslateY() + 10));
                        break;
                    case SPACE:
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
    
    ListChangeListener<Long> hideShapeListChangeListener = (change) -> {
        while (change.next()) {
            if (change.wasAdded()) {
                for(Long id : change.getAddedSubList()) {
                    shapeGroup.getChildren().remove(shapeMap.get(id));
                }
            } else {
                for(Long id : change.getRemoved()) {
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
            sm.getAllShapes().forEach(s -> intersectionsWithEdges(s.getId()));
            CrossSection.setChanged(false);
        }
    };
    
    ChangeListener<Boolean> updateListener = (v, oldValue, newValue) -> {
        if (newValue) {
            CrossSectionViewerTopComponent.clear();
            sm.getAllShapes().forEach(s -> intersectionsWithEdges(s.getId()));
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
