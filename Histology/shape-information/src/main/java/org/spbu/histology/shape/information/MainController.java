package org.spbu.histology.shape.information;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.PerspectiveCamera;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.PickResult;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import org.openide.LifecycleManager;
import org.openide.util.Lookup;
import org.spbu.histology.fxyz.Line3D;
import org.spbu.histology.fxyz.Text3DMesh;
import org.spbu.histology.model.Cell;
import org.spbu.histology.model.DigitMeshes;
import org.spbu.histology.model.HistionManager;
import org.spbu.histology.model.TetgenFacet;
//import org.fxyz3d.shapes.primitives.Text3DMesh;
import org.spbu.histology.model.TetgenPoint;
import org.spbu.histology.model.TwoIntegers;
import org.spbu.histology.model.TwoPoints;

public class MainController implements Initializable {
    
    private HistionManager hm = null;
    
    /*@FXML
    private FacetTabController FacetTabController;*/
    
    @FXML
    private GeneralTabController GeneralTabController;
    
    @FXML
    private Tab viewTab;
    
    private ObservableList<Rectangle> rectangleList = FXCollections.observableArrayList();
    private IntegerProperty count = new SimpleIntegerProperty(1);
    private FacetTabController facetTabController;
    private SubScene scene;
    private PerspectiveCamera camera;
    private Group root = new Group();
    private Group shapeGroup = new Group();
    private final double fov = 35.0;
    private final double nearClip = 0.1;
    private final double farClip = 4000.0;
    private double mousePosX;
    private double mousePosY;
    private double mouseOldX;
    private double mouseOldY;
    private Rotate rotateXCam = new Rotate(0, Rotate.X_AXIS);
    private Rotate rotateYCam = new Rotate(0, Rotate.Y_AXIS);
    //private ObservableList<TetgenFacet> facetData = FXCollections.observableArrayList();
    private ObservableList<ArrayList<Integer>> facetData = FXCollections.observableArrayList();
    private ObservableList<TwoIntegers> lineData = FXCollections.observableArrayList();
    private ObservableList<TetgenPoint> pointData = FXCollections.observableArrayList();
    //private int facetNumber = 0;
    
    private ArrayList<Line3D> lineList = new ArrayList<>();
    private ArrayList<Box> boxList = new ArrayList<>();
    //ArrayList<Point3D> linePointsList = new ArrayList<>();
    ArrayList<Point3D> linePointsList = new ArrayList<>();
    ArrayList<Integer> indexList = new ArrayList<>();
    
    /*public void setShape(Shape s) {
        BooleanProperty change = new SimpleBooleanProperty(false);
        FacetTabController.setShape(s, change);
        GeneralTabController.setShape(s, change);
    }*/
    
    BooleanProperty change = new SimpleBooleanProperty(false);
    
    /*private final ChangeListener<Boolean> changeListener = (o, ov, nv) -> {
        if (nv)
            buildLines();
    };*/
    
    //IntegerProperty maxNumOfVertices = new SimpleIntegerProperty(3);
    //IntegerProperty facetNumber = new SimpleIntegerProperty(0);
    
    public void setCell(Cell c) {
        //BooleanProperty change = new SimpleBooleanProperty(false);
        //FacetTabController.setShape(c, change);
        //GeneralTabController.setShape(c, change);
        
        facetData = c.getFacetData();
        
        //GeneralTabController.setCell(c, change, maxNumOfVertices, facetNumber);
        GeneralTabController.setCell(c, change, lineData, pointData);
        //pointData = c.getPointData();
        final IntegerProperty num = new SimpleIntegerProperty(1);
        hm.getHistionMap().get(c.getHistionId()).getItemMap().get(c.getId()).getItems().forEach(p -> {
            System.out.println(p.getId() + " " + p.getName());
            for (TetgenPoint point : p.getPointData()) {
                pointData.add(new TetgenPoint(num.get(), point));
                num.set(num.get() + 1);
            }
        });
        System.out.println("--------------");
        buildPoints();
        //FacetTabController.setCell(c, change, maxNumOfVertices, facetNumber, pointData);
        //FacetTabController.setLineList(lineList);
        buildLines();
    }
    
    public void setTableHeight(double height) {
        facetTabController.setTableHeight(height);
        scene.setHeight(height);
    }
    
    private void createScene() {
        root.getChildren().add(shapeGroup);
        scene = new SubScene(root, 1200, 800, true, SceneAntialiasing.BALANCED);
        scene.setFill(Color.WHITE);
        
        buildCamera();
        handleKeyboard();
        handleMouseEvents();        
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
        
        final PhongMaterial blackMaterial = new PhongMaterial();
        blackMaterial.setDiffuseColor(Color.BLACK);
        blackMaterial.setSpecularColor(Color.BLACK);
        
        final PhongMaterial redMaterial = new PhongMaterial();
        redMaterial.setDiffuseColor(Color.RED);
        redMaterial.setSpecularColor(Color.RED);
        
        camera.getTransforms().clear();
        camera.getTransforms().addAll(rotateYCam, rotateXCam);
        
        scene.setOnMousePressed(me -> {  
            PickResult pr = me.getPickResult();
            if(pr!=null && pr.getIntersectedNode() != null && pr.getIntersectedNode() instanceof Box){
                Box b = (Box) pr.getIntersectedNode();
                Point3D pickedPoint = new Point3D(b.getTranslateX(), b.getTranslateY(), b.getTranslateZ());
                if (!linePointsList.contains(pickedPoint)) {
                    if (linePointsList.size() == 1) {
                        TwoIntegers tp = new TwoIntegers(count.get(), indexList.get(0), boxList.indexOf(b) + 1);
                        if (!lineData.contains(tp)) {
                            lineData.add(tp);
                            linePointsList.add(pickedPoint);
                            Line3D line = new Line3D(linePointsList, 5f, Color.BLACK);
                            lineList.add(line);
                            shapeGroup.getChildren().add(line);
                            linePointsList.clear();
                            boxList.get(indexList.get(0) - 1).setMaterial(blackMaterial);
                            indexList.clear();
                            facetTabController.addRecord(tp);
                            count.set(count.get() + 1);
                        }
                    } else {
                        linePointsList.add(pickedPoint);
                        indexList.add(boxList.indexOf(b) + 1);
                        //boxList.get(b).setMaterial();
                        b.setMaterial(redMaterial);
                    }
                    /*if (lineList.size() > facetNumber.get()) {
                        shapeGroup.getChildren().remove(lineList.get(facetNumber.get()));
                        lineList.remove(facetNumber.get());
                    }*/
                    //if (linePointsList.size() > 1) {
                        //if ()
                        
                        /*Line3D line = new Line3D(linePointsList, 10f, Color.YELLOW);
                        lineList.add(line);
                        shapeGroup.getChildren().add(line);*/
                        
                    //}
                }
                //System.out.println(b.getTranslateX());
            }
            scene.requestFocus();
            mouseOldX = me.getSceneX();
            mouseOldY = me.getSceneY();
        });
        
        scene.setOnMouseDragged(me -> {
            if (me.isPrimaryButtonDown()) {
                scene.requestFocus();
                mousePosX = me.getSceneX();
                mousePosY = me.getSceneY();
                
                double angX = rotateXCam.getAngle()-(mousePosY - mouseOldY)*0.05;
                double angY = rotateYCam.getAngle()+(mousePosX - mouseOldX)*0.05;
                
                mouseOldX = mousePosX;
                mouseOldY = mousePosY;
                rotateXCam.setAngle(angX);
                rotateYCam.setAngle(angY);
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
        });
        
         scene.setOnMouseReleased(me -> {         
            scene.requestFocus();
        });
    }
    
    private void handleKeyboard() {   
        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                Line3D line;
                switch (event.getCode()) {
                    case A:
                        camera.setTranslateZ(
                                camera.getTranslateZ() + Math.sin(Math.toRadians(rotateYCam.getAngle())) * 12);
                        camera.setTranslateX(
                                camera.getTranslateX() - Math.cos(Math.toRadians(rotateYCam.getAngle())) * 12);
                        break;
                    case D:
                        camera.setTranslateZ(
                                camera.getTranslateZ() - Math.sin(Math.toRadians(rotateYCam.getAngle())) * 12);
                        camera.setTranslateX(
                                camera.getTranslateX() + Math.cos(Math.toRadians(rotateYCam.getAngle())) * 12);
                        break;
                    case W:
                        camera.setTranslateZ(
                                camera.getTranslateZ() + Math.cos(Math.toRadians(rotateYCam.getAngle())) * 12);
                        camera.setTranslateX(
                                camera.getTranslateX() + Math.sin(Math.toRadians(rotateYCam.getAngle())) * 12);
                        break;
                    case S:
                        camera.setTranslateZ(
                                camera.getTranslateZ() - Math.cos(Math.toRadians(rotateYCam.getAngle())) * 12);
                        camera.setTranslateX(
                                camera.getTranslateX() - Math.sin(Math.toRadians(rotateYCam.getAngle())) * 12);
                        break;
                    case Q:
                        camera.setTranslateY(camera.getTranslateY() + 10);
                        scene.requestFocus();
                        break;
                    case E:
                        camera.setTranslateY(camera.getTranslateY() - 10);
                        break;
                    /*case BACK_SPACE:
                        if (linePointsList.size() > 0) {
                            shapeGroup.getChildren().remove(lineList.get(facetNumber.get()));
                            lineList.remove(facetNumber.get());
                            linePointsList.remove(linePointsList.size() - 1);
                            indexList.remove(indexList.size() - 1);
                        }
                        line = new Line3D(linePointsList, 10f, Color.YELLOW);
                        lineList.add(line);
                        shapeGroup.getChildren().add(line);
                        break;
                    case ENTER:
                        if (lineList.size() > 0) {
                            if (indexList.size() <= 30) { 
                                shapeGroup.getChildren().remove(lineList.get(facetNumber.get()));
                                lineList.remove(facetNumber.get());
                                linePointsList.add(linePointsList.get(0));
                                line = new Line3D(linePointsList, 5f, Color.BLACK);
                                lineList.add(line);
                                shapeGroup.getChildren().add(line);
                                linePointsList.clear();
                                TetgenFacet tf = new TetgenFacet(facetNumber.get() + 1, 0,
                                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
                                if (indexList.size() > maxNumOfVertices.get())
                                    maxNumOfVertices.set(indexList.size());
                                for (int i = 0; i < indexList.size(); i++)
                                    tf.setVertex(indexList.get(i), i + 1);
                                facetData.add(tf);
                                indexList.clear();
                                facetNumber.set(facetNumber.get() + 1);
                            }
                        }
                        break;*/
                    /*case ESCAPE:
                        if (lineList.size() > 0) {
                            lineList.forEach(l -> {
                                shapeGroup.getChildren().remove(l);
                            });
                            lineList.clear();
                            linePointsList.clear();
                            indexList.clear();
                            facetNumber = 0;
                        }
                        break;*/
                }
            }
        });
    }
    
    private void buildPoints() {
        final PhongMaterial blackMaterial = new PhongMaterial();
        blackMaterial.setDiffuseColor(Color.BLACK);
        blackMaterial.setSpecularColor(Color.BLACK);
        final PhongMaterial redMaterial = new PhongMaterial();
        redMaterial.setDiffuseColor(Color.RED);
        redMaterial.setSpecularColor(Color.RED);
        int num = 1;
        //System.out.println(pointData.size());
        for (TetgenPoint p : pointData) {
            Box c = new Box(3,3,3);
            c.setTranslateX(p.getX());
            c.setTranslateY(p.getY());
            c.setTranslateZ(p.getZ());
            c.setMaterial(blackMaterial);
            boxList.add(c);
            shapeGroup.getChildren().add(c);
            
            int xSpacing = 15;
            if (num < 10)
                xSpacing = 0;
            else if (num < 100)
                xSpacing = 5;
            else if (num < 1000)
                xSpacing = 10;
            
            int number = num;
            while (number > 0) {
                for (MeshView mv : DigitMeshes.getMeshList(number % 10)) {
                    MeshView meshView = new MeshView(mv.getMesh());
                    meshView.setMaterial(redMaterial);
                    meshView.setTranslateX(p.getX() - 5 + xSpacing);
                    meshView.setTranslateY(p.getY() - 5);
                    meshView.setTranslateZ(p.getZ() - 5);
                    shapeGroup.getChildren().add(meshView);
                    xSpacing -= 5;
                }
                number = number / 10;
            }
            num++;
        }
    }
    
    private void buildLines() {
        for (Line3D l : lineList)
            shapeGroup.getChildren().remove(l);
        lineList.clear();
        ArrayList<Point3D> linePointsList = new ArrayList<>();
        for (ArrayList<Integer> f : facetData) {
            for (int i = 1; i < f.size(); i++) {
                    TwoIntegers ti = new TwoIntegers(count.get(), f.get(i - 1), f.get(i));
                    if (!lineData.contains(ti)) {
                        linePointsList.clear();
                        linePointsList.add(new Point3D(pointData.get(ti.getPoint1() - 1).getX(),
                            pointData.get(ti.getPoint1() - 1).getY(),
                            pointData.get(ti.getPoint1() - 1).getZ()));
                        linePointsList.add(new Point3D(pointData.get(ti.getPoint2() - 1).getX(),
                            pointData.get(ti.getPoint2() - 1).getY(),
                            pointData.get(ti.getPoint2() - 1).getZ()));
                        Line3D line = new Line3D(linePointsList, 5f, Color.BLACK);
                        lineList.add(line);
                        shapeGroup.getChildren().add(line);
                        facetTabController.addRecord(ti);
                        lineData.add(ti);
                        count.set(count.get() + 1);
                    }
            }
            TwoIntegers ti = new TwoIntegers(count.get(), f.get(f.size() - 1), f.get(0));
            if (!lineData.contains(ti)) {
                linePointsList.clear();
                linePointsList.add(new Point3D(pointData.get(ti.getPoint1() - 1).getX(),
                    pointData.get(ti.getPoint1() - 1).getY(),
                    pointData.get(ti.getPoint1() - 1).getZ()));
                linePointsList.add(new Point3D(pointData.get(ti.getPoint2() - 1).getX(),
                    pointData.get(ti.getPoint2() - 1).getY(),
                    pointData.get(ti.getPoint2() - 1).getZ()));
                Line3D line = new Line3D(linePointsList, 5f, Color.BLACK);
                lineList.add(line);
                shapeGroup.getChildren().add(line);
                facetTabController.addRecord(ti);
                lineData.add(ti);
                count.set(count.get() + 1);
            }
            //linePointsList.clear();
            //int num = 0;
            /*for (int i = 1; i <= 30; i++) {
                int vert = f.getVertex(i);
                if (vert > pointData.size()) {
                    num = 0;
                    break;
                }
                if (vert == 0)
                    break;
                num++;
                linePointsList.add(new Point3D(pointData.get(vert - 1).getX(),
                        pointData.get(vert - 1).getY(),
                        pointData.get(vert - 1).getZ()));
            }
            if (num > 2) {
                linePointsList.add(new Point3D(pointData.get(f.getVertex(1) - 1).getX(),
                            pointData.get(f.getVertex(1) - 1).getY(),
                            pointData.get(f.getVertex(1) - 1).getZ()));
                Line3D line = new Line3D(linePointsList, 5f, Color.BLACK);
                lineList.add(line);
                shapeGroup.getChildren().add(line);
            }*/
        }
    }
    
    /*private void buildLines() {
        for (Line3D l : lineList)
            shapeGroup.getChildren().remove(l);
        lineList.clear();
        ArrayList<Point3D> linePointsList = new ArrayList<>();
        for (TetgenFacet f : facetData) {
            linePointsList.clear();
            int num = 0;
            for (int i = 1; i <= 30; i++) {
                int vert = f.getVertex(i);
                if (vert > pointData.size()) {
                    num = 0;
                    break;
                }
                if (vert == 0)
                    break;
                num++;
                linePointsList.add(new Point3D(pointData.get(vert - 1).getX(),
                        pointData.get(vert - 1).getY(),
                        pointData.get(vert - 1).getZ()));
            }
            if (num > 2) {
                linePointsList.add(new Point3D(pointData.get(f.getVertex(1) - 1).getX(),
                            pointData.get(f.getVertex(1) - 1).getY(),
                            pointData.get(f.getVertex(1) - 1).getZ()));
                Line3D line = new Line3D(linePointsList, 5f, Color.BLACK);
                lineList.add(line);
                shapeGroup.getChildren().add(line);
            }
        }
    }*/
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        change.addListener((o, ov, nv) -> {
            if (nv) {
                //buildLines();
                change.set(false);
            }
        });
        
        hm = Lookup.getDefault().lookup(HistionManager.class);
        if (hm == null) {
            LifecycleManager.getDefault().exit();
        }
        
        Parent leftPart;
        try {
            URL location = PartInformationInitialization.class.getResource("FacetTab.fxml");
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(location);
            fxmlLoader.setBuilderFactory(new JavaFXBuilderFactory());

            leftPart = (Parent)fxmlLoader.load(location.openStream());
            facetTabController = (FacetTabController)fxmlLoader.getController();
            facetTabController.setLineList(lineList);
            facetTabController.setRoot(shapeGroup);
            facetTabController.setCount(count);
            facetTabController.setLineData(lineData);
            //shapeStructureInformationController.setInformation(histionId, cellId, partId);          
        } catch (Exception ex) {
            Logger.getLogger(PartInformationInitialization.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }
        
        createScene();
        BorderPane borderPane = new BorderPane();
        borderPane.setLeft(leftPart);
        borderPane.setCenter(scene);
        //viewTab.setContent(scene);
        viewTab.setContent(borderPane);
    }
}
