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
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.DrawMode;
import javafx.scene.shape.Line;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.TriangleMesh;
import javafx.scene.transform.Rotate;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.util.Lookup;
import org.openide.windows.TopComponent;
import org.openide.util.NbBundle.Messages;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.scene.text.Text;
import org.openide.LifecycleManager;
import org.spbu.histology.cross.section.viewer.CrossSectionViewerTopComponent;
import org.spbu.histology.model.CrossSection;
import org.spbu.histology.model.Node;
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
    
    private final ObservableMap<Long, Object> shapeMap = 
            FXCollections.observableMap(new ConcurrentHashMap());
    
    private ShapeManager sm = null;
    
    private final Group axisGroup = new Group();
    
    private Box crossSectionPlane;
    
    ArrayList<PhongMaterial> phongMaterials = new ArrayList<>();
    
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
    private final double gridSize = 2000;
    private final double camPosLim = 2000;
    private final double crossSectSize = 3000;
    private final double crossSectPosLim = 900;
    private final PhongMaterial transparentMaterial = new PhongMaterial();
    private final PhongMaterial transparentXAxisMaterial = new PhongMaterial();
    private final PhongMaterial transparentYAxisMaterial = new PhongMaterial();
    
    private Box crossSectionXAxis;
    private Box crossSectionYAxis;
    
    private ArrayList<Color> colorsList = new ArrayList();
    private ArrayList<double[]> nodesList = new ArrayList();
    private ArrayList<int[]> tetrahedronsList = new ArrayList();
    private ArrayList<int[]> facesList = new ArrayList();

    private ArrayList<Node> intersectionNodes = new ArrayList();
    
    private final double EPS = 0.0000001;
    
    private final MapChangeListener<Long, Shape> shapeListener =
            (change) -> {
                if (change.wasRemoved()) {  
                    Shape removedShape = (Shape)change.getValueRemoved();
                    shapeGroup.getChildren().remove(shapeMap.get(removedShape.getId()));
                    shapeMap.remove(removedShape.getId());
                    if (change.wasAdded()) {
                        Shape addedShape = (Shape)change.getValueAdded();
                        addShape(addedShape);
                    }
                }
                else if (change.wasAdded()) {
                    Shape addedShape = (Shape)change.getValueAdded();
                    addShape(addedShape);
                }      
            };
    
    public static class Grid extends Pane {

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
    }

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
        sm.addListener(shapeListener);        
        buildData();        
        if (!sm.getAllShapes().isEmpty())
            handleMouseEvents();  
        
        buildCrossSectionPlane();
        fxPanel.setScene(scene);  
    }

    private void intersectionsWithEdges() {
        double[] nl;
        int[] tl;
        if ((tetrahedronsList == null) || (nodesList == null))
            return;
        CrossSectionViewerTopComponent.clear();
        for (int k = 0; k < shapeMap.size(); k++) {
            nl = nodesList.get(k);
            tl = tetrahedronsList.get(k);
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
                    CrossSectionViewerTopComponent.show(intersectionNodes, colorsList.get(k));
                }
            }
        }
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
            double t = 1 / (CrossSection.getA() * (p2.x - p1.x) + CrossSection.getB() * (p2.y - p1.y) + CrossSection.getC() * (p2.z - p1.z)) * (-1) * firstEquation;
            double x = (p2.x - p1.x) * t + p1.x;
            double y = (p2.y - p1.y) * t + p1.y;
            double z = (p2.z - p1.z) * t + p1.z;
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

        sm.addShape(new Shape("Shape1", pointData, holeData, polygonsInFacetData, holesInFacetData, facetNumber, maxNumberOfVertices, Color.BLUE, Color.LIGHTBLUE));
        
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
        
        sm.addShape(new Shape("Shape2", pointData, holeData, polygonsInFacetData, holesInFacetData, facetNumber, maxNumberOfVertices, Color.DARKRED, Color.RED));
    }
    
    private void addShape(Shape s) {
        int numberOfNodes = s.getPointData().size();
        double[] nodeList = new double[numberOfNodes * 3];
        int count = 0;
        for (int i = 0; i < numberOfNodes; i++) {
            nodeList[count] = s.getPointData().get(i).getX();
            nodeList[count + 1] = s.getPointData().get(i).getY();
            nodeList[count + 2] = s.getPointData().get(i).getZ();
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
        ObservableList<TetgenFacetHole> holesDataSorted = s.getHolesInFacetData(); 
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
        
        int numberOfHoles = s.getHoleData().size();
        double[] holeList = new double[numberOfHoles * 3];
        count = 0;
        for (int i = 0; i < numberOfHoles; i++) {
            holeList[count] = s.getHoleData().get(i).getX();
            holeList[count + 1] = s.getHoleData().get(i).getY();
            holeList[count + 2] = s.getHoleData().get(i).getZ();
            count += 3;
        }
        int numberOfRegions = 0;
        double[] regionList = new double[numberOfRegions * 5];  
        
        TetgenResult tr = Tetgen.tetrahedralization(numberOfNodes, nodeList, 
                numberOfFacets, numberOfPolygonsInFacet, numberOfHolesInFacet, 
                holeListInFacet, numberOfVerticesInPolygon, vertexList, 
                numberOfHoles, holeList, numberOfRegions, regionList, "pq10000a1000000.0");
        
        nodesList.add(new double[tr.getNodeList().length]);
        tetrahedronsList.add(new int[tr.getTetrahedronList().length]);
        facesList.add(new int[tr.getFaceList().length]);
        System.arraycopy(tr.getNodeList(), 0, nodesList.get(shapeMap.size()), 0, tr.getNodeList().length);
        System.arraycopy(tr.getTetrahedronList(), 0, tetrahedronsList.get(shapeMap.size()), 0, tr.getTetrahedronList().length);
        System.arraycopy(tr.getFaceList(), 0, facesList.get(shapeMap.size()), 0, tr.getFaceList().length);
        
        final PhongMaterial phongMaterial = new PhongMaterial();
        phongMaterial.setDiffuseColor(s.getDiffuseColor());
        colorsList.add(s.getDiffuseColor());
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
        shapeGroup.getChildren().add(shape);
        shapeMap.put(s.getId(), shape);
    }
    
    private void buildGrid() {
        Group cubeFaces = new Group();
        //bottom face
        Grid r = new Grid(gridSize);
        r.setFill(Color.WHITE);
        r.setTranslateX(-0.5 * gridSize);
        r.setTranslateY(0);
        r.setRotationAxis(Rotate.X_AXIS);
        r.setRotate(90);
        
        cubeFaces.getChildren().add(r);
        
        // back face
        r = new Grid(gridSize);
        r.setFill(Color.WHITE);
        r.setTranslateX(-0.5 * gridSize);
        r.setTranslateY(-0.5 * gridSize);
        r.setTranslateZ(0.5 * gridSize);
        
        cubeFaces.getChildren().add(r);
        
        // left face
        r = new Grid(gridSize);
        r.setFill(Color.WHITE);
        r.setTranslateX(-1 * gridSize);
        r.setTranslateY(-0.5 * gridSize);
        r.setRotationAxis(Rotate.Y_AXIS);
        r.setRotate(90);
        
        //cubeFaces.getChildren().add(r);
        
        // right face
        r = new Grid(gridSize);
        r.setFill(Color.WHITE);
        r.setTranslateX(0);
        r.setTranslateY(-0.5 * gridSize);
        r.setRotationAxis(Rotate.Y_AXIS);
        r.setRotate(90);
        
        cubeFaces.getChildren().add(r);
        
        // top face
        r = new Grid(gridSize);
        r.setFill(Color.WHITE);
        r.setTranslateX(-0.5 * gridSize);
        r.setTranslateY(-1 * gridSize);
        r.setRotationAxis(Rotate.X_AXIS);
        r.setRotate(90);
        
        //cubeFaces.getChildren().add(r);
        
        // front face
        r = new Grid(gridSize);
        r.setFill(Color.WHITE);
        r.setTranslateX(-0.5 * gridSize);
        r.setTranslateY(-0.5 * gridSize);
        r.setTranslateZ(-0.5 * gridSize);

        //cubeFaces.getChildren().add(r);
        root.getChildren().add(cubeFaces);
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
        
        Rotate rotateX = new Rotate(0, Rotate.X_AXIS);
        Rotate rotateY = new Rotate(0, Rotate.Y_AXIS);
        if (shapeGroup != null)
            shapeGroup.getTransforms().addAll(rotateX, rotateY);
        axisGroup.getTransforms().addAll(rotateX, rotateY);
        
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
                CameraView.setCamera(String.valueOf(angX), 
                        String.valueOf(angY), 
                        String.valueOf(camera.getTranslateX()), 
                        String.valueOf(camera.getTranslateY()), 
                        String.valueOf(camera.getTranslateZ()), 
                        String.valueOf(camera.getFieldOfView()));
            }
        });
        
        scene.setOnScroll((ScrollEvent e) -> {
                double dy = e.getDeltaY();
                if (dy < 0) {
                    if (camera.getFieldOfView() >= 80)
                        return;
                    camera.setFieldOfView(camera.getFieldOfView() + 0.5);
                } else {
                    if (camera.getFieldOfView() <= 1)
                        return;
                    camera.setFieldOfView(camera.getFieldOfView() - 0.5);
                }
                CameraView.setCamera(String.valueOf(rotateXCam.getAngle()), 
                        String.valueOf(rotateYCam.getAngle()), 
                        String.valueOf(camera.getTranslateX()), 
                        String.valueOf(camera.getTranslateY()), 
                        String.valueOf(camera.getTranslateZ()), 
                        String.valueOf(camera.getFieldOfView()));
            });
    }
    
    private void handleKeyboard() {   
        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                double value;

                switch (event.getCode()) {
                    case A:
                        value = camera.getTranslateZ() + Math.sin(Math.toRadians(rotateYCam.getAngle())) * 12;
                        if ((value <= camPosLim) && (value >= -camPosLim))
                            camera.setTranslateZ(value);
                        value = camera.getTranslateX() - Math.cos(Math.toRadians(rotateYCam.getAngle())) * 12;
                        if ((value <= camPosLim) && (value >= -camPosLim))
                            camera.setTranslateX(value); 
                        break;
                    case D:
                        value = camera.getTranslateZ() - Math.sin(Math.toRadians(rotateYCam.getAngle())) * 12;
                        if ((value <= camPosLim) && (value >= -camPosLim))
                            camera.setTranslateZ(value);
                        value = camera.getTranslateX() + Math.cos(Math.toRadians(rotateYCam.getAngle())) * 12;
                        if ((value <= camPosLim) && (value >= -camPosLim))
                            camera.setTranslateX(value); 
                        break;
                    case W:
                        value = camera.getTranslateZ() + Math.cos(Math.toRadians(rotateYCam.getAngle())) * 12;
                        if ((value <= camPosLim) && (value >= -camPosLim))
                            camera.setTranslateZ(value);
                        value = camera.getTranslateX() + Math.sin(Math.toRadians(rotateYCam.getAngle())) * 12;
                        if ((value <= camPosLim) && (value >= -camPosLim))
                            camera.setTranslateX(value);  
                        break;
                    case S:
                        value = camera.getTranslateZ() - Math.cos(Math.toRadians(rotateYCam.getAngle())) * 12;
                        if ((value <= camPosLim) && (value >= -camPosLim))
                            camera.setTranslateZ(value);
                        value = camera.getTranslateX() - Math.sin(Math.toRadians(rotateYCam.getAngle())) * 12;
                        if ((value <= camPosLim) && (value >= -camPosLim))
                            camera.setTranslateX(value);
                        break;
                    case SHIFT:
                        value = camera.getTranslateY() + 10;
                        if (value <= camPosLim)
                            camera.setTranslateY(value);
                        break;
                    case SPACE:
                        value = camera.getTranslateY() - 10;
                        if (value >= -camPosLim)
                            camera.setTranslateY(value);
                        break;
                }
                CameraView.setCamera(String.valueOf(rotateXCam.getAngle()), 
                        String.valueOf(rotateYCam.getAngle()), 
                        String.valueOf(camera.getTranslateX()), 
                        String.valueOf(camera.getTranslateY()), 
                        String.valueOf(camera.getTranslateZ()), 
                        String.valueOf(camera.getFieldOfView()));
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
    
    private void addCameraViewListener() {
        CameraView.xRotateProperty().addListener(xRotListener);
        CameraView.yRotateProperty().addListener(yRotListener);
        CameraView.xCoordinateProperty().addListener(xPosListener);
        CameraView.yCoordinateProperty().addListener(yPosListener);
        CameraView.zCoordinateProperty().addListener(zPosListener);
        CameraView.FOVProperty().addListener(FOVListener); 
    }
    
    private void removeCameraViewListener() {
        CameraView.xRotateProperty().removeListener(xRotListener);
        CameraView.yRotateProperty().removeListener(yRotListener);
        CameraView.xCoordinateProperty().removeListener(xPosListener);
        CameraView.yCoordinateProperty().removeListener(yPosListener);
        CameraView.zCoordinateProperty().removeListener(zPosListener);
        CameraView.FOVProperty().removeListener(FOVListener); 
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
            intersectionsWithEdges();
            CrossSection.setChanged(false);
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
    }
    
    private void removeCrossSectionListener() {
        CrossSection.xRotateProperty().removeListener(crossSectionXRotListener);
        CrossSection.yRotateProperty().removeListener(crossSectionYRotListener);
        CrossSection.xCoordinateProperty().removeListener(crossSectionXPosListener);
        CrossSection.yCoordinateProperty().removeListener(crossSectionYPosListener);
        CrossSection.zCoordinateProperty().removeListener(crossSectionZPosListener);
        CrossSection.opaquenessProperty().removeListener(opaquenessListener);
        CrossSection.changedProperty().removeListener(changeListener);
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
