/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.space;

import com.model.BoxShape;
import com.model.CameraView;
import com.model.CylinderShape;
import java.awt.BorderLayout;
import java.util.Collection;
import javafx.application.Platform;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
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
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.DrawMode;
import javafx.scene.shape.Line;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Sphere;
import javafx.scene.shape.TriangleMesh;
import javafx.scene.transform.Rotate;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.windows.TopComponent;
import org.openide.util.NbBundle.Messages;
import org.openide.windows.WindowManager;
import com.model.Shape;
import com.model.ShapeManager;
import com.model.SphereShape;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;
import javafx.scene.input.KeyCode;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import org.openide.LifecycleManager;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;

/**
 * Top component which displays something.
 */
@ConvertAsProperties(
        dtd = "-//com.space//Space//EN",
        autostore = false
)
@TopComponent.Description(
        preferredID = "SpaceTopComponent",
        //iconBase="SET/PATH/TO/ICON/HERE", 
        persistenceType = TopComponent.PERSISTENCE_ALWAYS
)
@TopComponent.Registration(mode = "editor", openAtStartup = true)
@ActionID(category = "Window", id = "com.space.SpaceTopComponent")
@ActionReference(path = "Menu/Window" /*, position = 333 */)
@TopComponent.OpenActionRegistration(
        displayName = "#CTL_SpaceAction",
        preferredID = "SpaceTopComponent"
)
@Messages({
    "CTL_SpaceAction=Space",
    "CTL_SpaceTopComponent=Space Window",
    "HINT_SpaceTopComponent=This is a Space window"
})

public final class SpaceTopComponent extends TopComponent {
    
    //private final InstanceContent instanceContent = new InstanceContent();
    private CameraView theCamera;
    
    //ArrayList<Object> shapes = new ArrayList<>();
    
    private final ObservableMap<Long, Object> shapeMap = 
            FXCollections.observableMap(new ConcurrentHashMap<Long, Object>());
    
    ArrayList<PhongMaterial> phongMaterials = new ArrayList<>();
    ArrayList<Rotate> Xrotates = new ArrayList<>();
    ArrayList<Rotate> Yrotates = new ArrayList<>();
    ArrayList<Rotate> Zrotates = new ArrayList<>();
    
    private ShapeManager sm = null;
    
    final Group axisGroup = new Group();
    
    private double mousePosX;
    private double mousePosY;
    private double mouseOldX;
    private double mouseOldY;
    
    Rotate rotateXCam = new Rotate(0, Rotate.X_AXIS);
    Rotate rotateYCam = new Rotate(0, Rotate.Y_AXIS);
    Rotate rotateZCam = new Rotate(0, Rotate.Z_AXIS);
  
    //private Lookup.Result<Shape> lookupResult = null;
    
    private static JFXPanel fxPanel;
    private Scene scene;
    private PerspectiveCamera camera;
    private Group root = new Group();
    private Group shapeGroup = new Group();
    private final double fov = 35.0;
    private final double nearClip = 0.1;
    private final double farClip = 5000.0;  
    private final double axisLen = 1500.0;
    
    private final MapChangeListener<Long, Shape> shapeListener =
            (change) -> {
                System.out.println(change);
                if (change.wasRemoved()) {  
                        //System.out.println("---");
                        //if (change.getValueRemoved() instanceof )
                    Shape removedShape = (Shape)change.getValueRemoved();
                        //System.out.println(removedShape.getId());
                    shapeGroup.getChildren().remove(shapeMap.get(removedShape.getId()));
                    shapeMap.remove(removedShape.getId());
                    if (change.wasAdded()) {
                        Shape addedShape = (Shape)change.getValueAdded();
                        addShape(addedShape);
                    }
                    //shapes.remove(Math.toIntExact(removedShape.getId()));
                    //System.out.println(change.getValueRemoved());
                    //System.out.println("here");
                }
                else if (change.wasAdded()) {
                    Shape addedShape = (Shape)change.getValueAdded();
                    //System.out.println(addedShape.getId());
                    addShape(addedShape);
                }
                /*if (ChosenTool.getToolNumber() == -1)
                    ChosenTool.setToolNumber(-2);
                else
                    ChosenTool.setToolNumber(-1);*/
                
                //System.out.println("Deleted");
                //loadShapes();
                /*if(change.getValueAdded() != null) {
                    System.out.println("1");
                    loadShapes();
                }  */             
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

    public SpaceTopComponent() {
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
        //Platform.runLater(() -> createScene());
        
        Platform.runLater(() -> {
            try {
                createScene();
            } finally {
                latch.countDown();
            }
        });
        
        try {
            latch.await();
            /*theCamera = new CameraView("0", "0", "0", "0", "0", "0", String.valueOf(fov), String.valueOf(nearClip), String.valueOf(farClip));
            instanceContent.set(Collections.singleton(theCamera), null);
            associateLookup(new AbstractLookup(instanceContent));*/
            CameraView.setCamera("0", "0", "0", "0", "0", "-1000", String.valueOf(fov), String.valueOf(nearClip), String.valueOf(farClip));
        } catch (InterruptedException ex) {
            LifecycleManager.getDefault().exit();
        }
        
        
        //instanceContent.set(Collections.singleton(theCamera), null);
        //associateLookup(new AbstractLookup(homeController.getInstanceContent()));
    }
    
    private void createScene() {
        root.getChildren().add(shapeGroup);
        scene = new Scene(root, 1000, 1000, true, SceneAntialiasing.BALANCED);
        buildAxes();
        buildCamera();
        handleKeyboard();
        handleMouseEvents();
        
        buildGrid();
        
        sm = Lookup.getDefault().lookup(ShapeManager.class);
        if (sm == null) {
            LifecycleManager.getDefault().exit();
        }
        sm.addListener(shapeListener);
        
        buildData();
        
        /*sm.getAllShapes().forEach(s -> {
            addShape(s);
        });*/
        if (sm.getAllShapes().size() != 0)
            handleMouseEvents();
        //buildShapes();
                
        fxPanel.setScene(scene);       
    }
    
     private void buildData() {
        //if (sm.getAllShapes().size() == 0) {
        //if (sm.getAddPredefinedShapes()) {
            //sm.setAddPredefinedShapes(false);
            //ToolsTopComponent.first = false;
            sm.addShape(new BoxShape("Shape1", "Cube", "0", "0", "0", "0", "0", "0", Color.BLUE, Color.LIGHTBLUE, "100", "200", "300"));
            sm.addShape(new CylinderShape("Shape2", "Cylinder", "0", "0", "0", "100", "100", "100", Color.BLUE, Color.LIGHTBLUE, "50", "80"));
            sm.addShape(new SphereShape("Shape3", "Sphere", "0", "0", "0", "-200", "-200", "-200", Color.BLUE, Color.LIGHTBLUE, "90"));
        //}
    }
    
    private void buildShapes() {
        /*sm = Lookup.getDefault().lookup(ShapeManager.class);
        if (sm == null) {
            LifecycleManager.getDefault().exit();
        }*/
        /*List<Shape> copyList = sm.getAllShapes();
        if (copyList.get(0) instanceof BoxShape) {
            System.out.println("Buuuux");
        }*/
        sm.getAllShapes().forEach(s -> {
            addShape(s);
            //System.out.println("1");
            /*switch (s.getType()) {
                case "Cube":
                    if (s instanceof BoxShape) {
                        //System.out.println("1");
                        BoxShape sh = (BoxShape) s;
                        Box b = new Box(Double.parseDouble(sh.getLength()), Double.parseDouble(sh.getWidth()), Double.parseDouble(sh.getHeight()));
                        b.setTranslateX(Double.parseDouble(sh.getXCoordinate()));
                        b.setTranslateY(Double.parseDouble(sh.getYCoordinate()));
                        b.setTranslateZ(Double.parseDouble(sh.getZCoordinate()));
                        PhongMaterial pm = new PhongMaterial();
                        pm.setDiffuseColor(sh.getDiffuseColor());
                        pm.setSpecularColor(sh.getSpecularColor());
                        b.setMaterial(pm);
                        Rotate rotateX = new Rotate(Double.parseDouble(sh.getXRotate()), Rotate.X_AXIS);
                        Rotate rotateY = new Rotate(Double.parseDouble(sh.getYRotate()), Rotate.Y_AXIS);
                        Rotate rotateZ = new Rotate(Double.parseDouble(sh.getZRotate()), Rotate.Y_AXIS);
                        b.getTransforms().addAll(rotateX, rotateY, rotateZ);
                        shapeGroup.getChildren().add(b);
                        //handleMouseEvents();
                        shapes.add(b);
                        phongMaterials.add(pm);
                        Xrotates.add(rotateX);
                        Yrotates.add(rotateY);
                        Zrotates.add(rotateZ);
                    }
                    break;
                case "Cylinder":
                    if (s instanceof CylinderShape) {
                        //System.out.println("1");
                        CylinderShape sh = (CylinderShape) s;
                        Cylinder c = new Cylinder(Double.parseDouble(sh.getHeight()), Double.parseDouble(sh.getRadius()));
                        c.setTranslateX(Double.parseDouble(sh.getXCoordinate()));
                        c.setTranslateY(Double.parseDouble(sh.getYCoordinate()));
                        c.setTranslateZ(Double.parseDouble(sh.getZCoordinate()));
                        PhongMaterial pm = new PhongMaterial();
                        pm.setDiffuseColor(sh.getDiffuseColor());
                        pm.setSpecularColor(sh.getSpecularColor());
                        c.setMaterial(pm);
                        Rotate rotateX = new Rotate(Double.parseDouble(sh.getXRotate()), Rotate.X_AXIS);
                        Rotate rotateY = new Rotate(Double.parseDouble(sh.getYRotate()), Rotate.Y_AXIS);
                        Rotate rotateZ = new Rotate(Double.parseDouble(sh.getZRotate()), Rotate.Y_AXIS);
                        c.getTransforms().addAll(rotateX, rotateY, rotateZ);
                        shapeGroup.getChildren().add(c);
                        //handleMouseEvents();
                        shapes.add(c);
                        phongMaterials.add(pm);
                        Xrotates.add(rotateX);
                        Yrotates.add(rotateY);
                        Zrotates.add(rotateZ);
                    }
                    break;
                case "Sphere":
                    if (s instanceof SphereShape) {
                        //System.out.println("1");
                        SphereShape sh = (SphereShape) s;
                        Sphere sp = new Sphere(Double.parseDouble(sh.getRadius()));
                        sp.setTranslateX(Double.parseDouble(sh.getXCoordinate()));
                        sp.setTranslateY(Double.parseDouble(sh.getYCoordinate()));
                        sp.setTranslateZ(Double.parseDouble(sh.getZCoordinate()));
                        PhongMaterial pm = new PhongMaterial();
                        pm.setDiffuseColor(sh.getDiffuseColor());
                        pm.setSpecularColor(sh.getSpecularColor());
                        sp.setMaterial(pm);
                        Rotate rotateX = new Rotate(Double.parseDouble(sh.getXRotate()), Rotate.X_AXIS);
                        Rotate rotateY = new Rotate(Double.parseDouble(sh.getYRotate()), Rotate.Y_AXIS);
                        Rotate rotateZ = new Rotate(Double.parseDouble(sh.getZRotate()), Rotate.Y_AXIS);
                        sp.getTransforms().addAll(rotateX, rotateY, rotateZ);
                        shapeGroup.getChildren().add(sp);
                        //handleMouseEvents();
                        shapes.add(sp);
                        phongMaterials.add(pm);
                        Xrotates.add(rotateX);
                        Yrotates.add(rotateY);
                        Zrotates.add(rotateZ);
                    }
                    break;
            }*/
        });
        if (sm.getAllShapes().size() != 0)
            handleMouseEvents();
        
        //Good
        //Box test = (Box)shapes.get(0);
        //test.setHeight(500);
        
    }
    
    private void addShape(Shape s) {
        switch (s.getType()) {
                case "Cube":
                    if (s instanceof BoxShape) {
                        //System.out.println("1");
                        BoxShape sh = (BoxShape) s;
                        Box b = new Box(Double.parseDouble(sh.getLength()), Double.parseDouble(sh.getWidth()), Double.parseDouble(sh.getHeight()));
                        b.setTranslateX(Double.parseDouble(sh.getXCoordinate()));
                        b.setTranslateY(Double.parseDouble(sh.getYCoordinate()));
                        b.setTranslateZ(Double.parseDouble(sh.getZCoordinate()));
                        PhongMaterial pm = new PhongMaterial();
                        pm.setDiffuseColor(sh.getDiffuseColor());
                        pm.setSpecularColor(sh.getSpecularColor());
                        b.setMaterial(pm);
                        Rotate rotateX = new Rotate(Double.parseDouble(sh.getXRotate()), Rotate.X_AXIS);
                        Rotate rotateY = new Rotate(Double.parseDouble(sh.getYRotate()), Rotate.Y_AXIS);
                        Rotate rotateZ = new Rotate(Double.parseDouble(sh.getZRotate()), Rotate.Y_AXIS);
                        b.getTransforms().addAll(rotateX, rotateY, rotateZ);
                        shapeGroup.getChildren().add(b);
                        //handleMouseEvents();
                        //shapes.add(b);
                        shapeMap.put(s.getId(), b);
                        phongMaterials.add(pm);
                        Xrotates.add(rotateX);
                        Yrotates.add(rotateY);
                        Zrotates.add(rotateZ);
                    }
                    break;
                case "Cylinder":
                    if (s instanceof CylinderShape) {
                        //System.out.println("1");
                        CylinderShape sh = (CylinderShape) s;
                        Cylinder c = new Cylinder(Double.parseDouble(sh.getHeight()), Double.parseDouble(sh.getRadius()));
                        c.setTranslateX(Double.parseDouble(sh.getXCoordinate()));
                        c.setTranslateY(Double.parseDouble(sh.getYCoordinate()));
                        c.setTranslateZ(Double.parseDouble(sh.getZCoordinate()));
                        PhongMaterial pm = new PhongMaterial();
                        pm.setDiffuseColor(sh.getDiffuseColor());
                        pm.setSpecularColor(sh.getSpecularColor());
                        c.setMaterial(pm);
                        Rotate rotateX = new Rotate(Double.parseDouble(sh.getXRotate()), Rotate.X_AXIS);
                        Rotate rotateY = new Rotate(Double.parseDouble(sh.getYRotate()), Rotate.Y_AXIS);
                        Rotate rotateZ = new Rotate(Double.parseDouble(sh.getZRotate()), Rotate.Y_AXIS);
                        c.getTransforms().addAll(rotateX, rotateY, rotateZ);
                        shapeGroup.getChildren().add(c);
                        //handleMouseEvents();
                        shapeMap.put(s.getId(), c);
                        phongMaterials.add(pm);
                        Xrotates.add(rotateX);
                        Yrotates.add(rotateY);
                        Zrotates.add(rotateZ);
                    }
                    break;
                case "Sphere":
                    if (s instanceof SphereShape) {
                        //System.out.println("1");
                        SphereShape sh = (SphereShape) s;
                        Sphere sp = new Sphere(Double.parseDouble(sh.getRadius()));
                        sp.setTranslateX(Double.parseDouble(sh.getXCoordinate()));
                        sp.setTranslateY(Double.parseDouble(sh.getYCoordinate()));
                        sp.setTranslateZ(Double.parseDouble(sh.getZCoordinate()));
                        PhongMaterial pm = new PhongMaterial();
                        pm.setDiffuseColor(sh.getDiffuseColor());
                        pm.setSpecularColor(sh.getSpecularColor());
                        sp.setMaterial(pm);
                        Rotate rotateX = new Rotate(Double.parseDouble(sh.getXRotate()), Rotate.X_AXIS);
                        Rotate rotateY = new Rotate(Double.parseDouble(sh.getYRotate()), Rotate.Y_AXIS);
                        Rotate rotateZ = new Rotate(Double.parseDouble(sh.getZRotate()), Rotate.Y_AXIS);
                        sp.getTransforms().addAll(rotateX, rotateY, rotateZ);
                        shapeGroup.getChildren().add(sp);
                        //handleMouseEvents();
                        //shapes.add(sp);
                        shapeMap.put(s.getId(), sp);
                        phongMaterials.add(pm);
                        Xrotates.add(rotateX);
                        Yrotates.add(rotateY);
                        Zrotates.add(rotateZ);
                    }
                    break;
            }
    }
    
    private void buildGrid() {
        double size = 2000;
        Group cubeFaces = new Group();
        //bottom face
        Grid r = new Grid(size);
        //r.setFill(Color.deriveColor(0.0, 1.0, (1 - 0.4 * 1), 1.0));
        r.setFill(Color.WHITE);
        r.setTranslateX(-0.5 * size);
        r.setTranslateY(0);
        r.setRotationAxis(Rotate.X_AXIS);
        r.setRotate(90);
        
        cubeFaces.getChildren().add(r);
        
        // back face
        r = new Grid(size);
        r.setFill(Color.WHITE);
        r.setTranslateX(-0.5 * size);
        r.setTranslateY(-0.5 * size);
        r.setTranslateZ(0.5 * size);
        
        cubeFaces.getChildren().add(r);
        
        // left face
        r = new Grid(size);
        r.setFill(Color.WHITE);
        r.setTranslateX(-1 * size);
        r.setTranslateY(-0.5 * size);
        r.setRotationAxis(Rotate.Y_AXIS);
        r.setRotate(90);
        
        //cubeFaces.getChildren().add(r);
        
        // right face
        r = new Grid(size);
        r.setFill(Color.WHITE);
        r.setTranslateX(0);
        r.setTranslateY(-0.5 * size);
        r.setRotationAxis(Rotate.Y_AXIS);
        r.setRotate(90);
        
        cubeFaces.getChildren().add(r);
        
        // top face
        r = new Grid(size);
        r.setFill(Color.WHITE);
        r.setTranslateX(-0.5 * size);
        r.setTranslateY(-1 * size);
        r.setRotationAxis(Rotate.X_AXIS);
        r.setRotate(90);
        
        //cubeFaces.getChildren().add(r);
        
        // front face
        r = new Grid(size);
        r.setFill(Color.WHITE);
        r.setTranslateX(-0.5 * size);
        r.setTranslateY(-0.5 * size);
        r.setTranslateZ(-0.5 * size);

        //cubeFaces.getChildren().add(r);
        root.getChildren().add(cubeFaces);
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
            0, 0, 0, // Point 0 - Top
            0, h, -s/2, // Point 1 - Front
            -s/2, h, 0, // Point 2 - Left
            s/2, h, 0, // Point 3 - Back
            0, h, s/2 // Point 4 - Right
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
        
        /*Sphere xSphere = new Sphere(10);
        Sphere ySphere = new Sphere(10);
        Sphere zSphere = new Sphere(10);
        xSphere.setMaterial(redMaterial);
        ySphere.setMaterial(greenMaterial);
        zSphere.setMaterial(blueMaterial);
         
        xSphere.setTranslateX(500);
        xSphere.setTranslateY(0);
        xSphere.setTranslateZ(0);
        ySphere.setTranslateY(1000);
        zSphere.setTranslateZ(1000);

        axisGroup.getChildren().addAll(xSphere, ySphere, zSphere);*/
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
    
    /*private void handleShapeMouseEvents() {

        Rotate rotateX = new Rotate(0, Rotate.X_AXIS);
        Rotate rotateY = new Rotate(0, Rotate.Y_AXIS);
            if (shapeGroup != null)
                shapeGroup.getTransforms().addAll(rotateX, rotateY);
        //axisGroup.getTransforms().clear();
        //axisGroup.getTransforms().addAll(rotateX, rotateY);

        scene.setOnMousePressed(me -> {         
            mouseOldX = me.getSceneX();
            mouseOldY = me.getSceneY();
        });
        scene.setOnMouseDragged(me -> {
            if (me.isSecondaryButtonDown()) {
                mousePosX = me.getSceneX();
                mousePosY = me.getSceneY();
                rotateX.setAngle(rotateX.getAngle()+(mousePosY - mouseOldY)*0.05);
                rotateY.setAngle(rotateY.getAngle()-(mousePosX - mouseOldX)*0.05);
                mouseOldX = mousePosX;
                mouseOldY = mousePosY;
            }
        });
    }*/
    
    private void handleMouseEvents() {
        
        Rotate rotateX = new Rotate(0, Rotate.X_AXIS);
        Rotate rotateY = new Rotate(0, Rotate.Y_AXIS);
        if (shapeGroup != null)
            shapeGroup.getTransforms().addAll(rotateX, rotateY);
        /*axisGroup.getTransforms().clear();
        axisGroup.getTransforms().addAll(rotateX, rotateY);*/
        
        
        //Rotate rotateXCam = new Rotate(0, Rotate.X_AXIS);
        //Rotate rotateYCam = new Rotate(0, Rotate.Y_AXIS);
        //rotateXCam = new Rotate(0, Rotate.X_AXIS);
        //rotateYCam = new Rotate(0, Rotate.Y_AXIS);
        //rotateZCam = new Rotate(0, Rotate.Z_AXIS);
        camera.getTransforms().clear();
        camera.getTransforms().addAll(rotateZCam, rotateXCam, rotateYCam);
        
        scene.setOnMousePressed(me -> {         
            mouseOldX = me.getSceneX();
            mouseOldY = me.getSceneY();
        });
        scene.setOnMouseDragged(me -> {
            
            if (me.isSecondaryButtonDown()) {
                mousePosX = me.getSceneX();
                mousePosY = me.getSceneY();
                rotateX.setAngle(rotateX.getAngle()+(mousePosY - mouseOldY)*0.05);
                rotateY.setAngle(rotateY.getAngle()-(mousePosX - mouseOldX)*0.05);
                mouseOldX = mousePosX;
                mouseOldY = mousePosY;
            }
            
            if (me.isPrimaryButtonDown()) {
                mousePosX = me.getSceneX();
                mousePosY = me.getSceneY();
                //rotateXCam.setAngle(rotateXCam.getAngle()-(mousePosY - mouseOldY)*0.1);
                //rotateZCam.setAngle(rotateZCam.getAngle()+(mousePosY - mouseOldY)*0.1);
                double angX = rotateXCam.getAngle()-(mousePosY - mouseOldY)*0.05*Math.cos(Math.toRadians(rotateYCam.getAngle()));
                double angZ = rotateZCam.getAngle()+(mousePosY - mouseOldY)*0.05*Math.sin(Math.toRadians(rotateYCam.getAngle()));
                if ((angX <= 90) && (angX >= -90))
                    rotateXCam.setAngle(angX);
                    //rotateXCam.setAngle(rotateXCam.getAngle()-(mousePosY - mouseOldY)*0.05*Math.cos(Math.toRadians(rotateYCam.getAngle())));
                if ((angZ <= 90) && (angZ >= -90))
                    rotateZCam.setAngle(angZ);
                    //rotateZCam.setAngle(rotateZCam.getAngle()+(mousePosY - mouseOldY)*0.05*Math.sin(Math.toRadians(rotateYCam.getAngle())));
                /*if (camera.getTranslateZ() > 0)
                    rotateXCam.setAngle(rotateXCam.getAngle()+(mousePosY - mouseOldY)*0.1);
                else 
                    rotateXCam.setAngle(rotateXCam.getAngle()-(mousePosY - mouseOldY)*0.1);*/
                double angY = rotateYCam.getAngle()+(mousePosX - mouseOldX)*0.05;
                if (angY >= 360)
                    angY = angY - 360;
                else if (angY <= -360)
                    angY = angY + 360;
                rotateYCam.setAngle(angY);
                //rotateYCam.setAngle(rotateYCam.getAngle()+(mousePosX - mouseOldX)*0.05);
                
                mouseOldX = mousePosX;
                mouseOldY = mousePosY;
                CameraView.setCamera(String.valueOf(rotateXCam.getAngle()), String.valueOf(rotateYCam.getAngle()), String.valueOf(rotateZCam.getAngle()), String.valueOf(camera.getTranslateX()), String.valueOf(camera.getTranslateY()), String.valueOf(camera.getTranslateZ()), String.valueOf(camera.getFieldOfView()), String.valueOf(camera.getNearClip()), String.valueOf(camera.getFarClip()));
                //theCamera = new CameraView(String.valueOf(rotateXCam.getAngle()), String.valueOf(rotateYCam.getAngle()), String.valueOf(rotateZCam.getAngle()), String.valueOf(camera.getTranslateX()), String.valueOf(camera.getTranslateY()), String.valueOf(camera.getTranslateZ()), String.valueOf(fov), String.valueOf(nearClip), String.valueOf(farClip));
                //instanceContent.set(Collections.singleton(theCamera), null);
            }
        });
        
        scene.setOnScroll((ScrollEvent e) -> {
                //System.out.println(fov);
                double dy = e.getDeltaY();
                if (dy < 0) {
                    if (camera.getFieldOfView() > 80)
                        return;
                    //fov += 0.5;
                    camera.setFieldOfView(camera.getFieldOfView() + 0.5);
                } else {
                    if (camera.getFieldOfView() < 1)
                        return;
                    camera.setFieldOfView(camera.getFieldOfView() - 0.5);
                }
                CameraView.setCamera(String.valueOf(rotateXCam.getAngle()), String.valueOf(rotateYCam.getAngle()), String.valueOf(rotateZCam.getAngle()), String.valueOf(camera.getTranslateX()), String.valueOf(camera.getTranslateY()), String.valueOf(camera.getTranslateZ()), String.valueOf(camera.getFieldOfView()), String.valueOf(camera.getNearClip()), String.valueOf(camera.getFarClip()));
                //theCamera = new CameraView(String.valueOf(rotateXCam.getAngle()), String.valueOf(rotateYCam.getAngle()), String.valueOf(rotateZCam.getAngle()), String.valueOf(camera.getTranslateX()), String.valueOf(camera.getTranslateY()), String.valueOf(camera.getTranslateZ()), String.valueOf(fov), String.valueOf(nearClip), String.valueOf(farClip));
                //instanceContent.set(Collections.singleton(theCamera), null);
                //camera.setFieldOfView(fov);
            });
    }
    
    private void handleKeyboard() {   
        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                double angY;
                double angX;
                double angZ;

                switch (event.getCode()) {
                    case A:
                        //camera.setTranslateX(camera.getTranslateX() - 5);
                        angY = rotateYCam.getAngle() - 3;
                        if (angY <= -360)
                            angY = angY + 360;
                        //rotateYCam.setAngle(rotateYCam.getAngle() - 1.5);
                        rotateYCam.setAngle(angY);
                        break;
                    case D:
                        //camera.setTranslateX(camera.getTranslateX() + 5);
                        angY = rotateYCam.getAngle() + 3;
                        if (angY >= 360)
                            angY = angY - 360;
                        //rotateYCam.setAngle(rotateYCam.getAngle() + 1.5);
                        rotateYCam.setAngle(angY);
                        break;
                    case W:
                        boolean minus = false;
                        boolean afterPlus = false;
                        if (camera.getTranslateZ() < 0)
                            minus = true;
                        camera.setTranslateZ(camera.getTranslateZ() + Math.cos(Math.toRadians(rotateYCam.getAngle())) * 12);
                        camera.setTranslateX(camera.getTranslateX() + Math.sin(Math.toRadians(rotateYCam.getAngle())) * 12);
                        /*camera.setTranslateZ(camera.getTranslateZ() + Math.cos(Math.toRadians(rotateYCam.getAngle())) * Math.cos(Math.toRadians(rotateXCam.getAngle())) * 10);
                        camera.setTranslateX(camera.getTranslateX() + Math.sin(Math.toRadians(rotateYCam.getAngle())) * Math.cos(Math.toRadians(rotateXCam.getAngle())) * 10);*/
                        if (camera.getTranslateZ() >= 0)
                            afterPlus = true;
                        /*if ((minus && afterPlus) || (!minus && !afterPlus))
                            rotateXCam.setAngle(rotateXCam.getAngle() * (-1));*/
                        /*if (camera.getTranslateZ() > 0)
                            camera.setTranslateY(camera.getTranslateY() + Math.sin(Math.toRadians(rotateXCam.getAngle())) * 10);
                        else
                            camera.setTranslateY(camera.getTranslateY() - Math.sin(Math.toRadians(rotateXCam.getAngle())) * 10);*/
                        //System.out.println(rotateXCam.getAngle());
                        //System.out.println(camera.getTranslateX());
                        //System.out.println(camera.getTranslateY());
                        //System.out.println(camera.getTranslateZ());
                        //System.out.println(rotateXCam.getAngle());
                        //System.out.println(rotateYCam.getAngle());
                        break;
                    case S:
                        //camera.setTranslateZ(camera.getTranslateZ() - 5);
                        camera.setTranslateZ(camera.getTranslateZ() - Math.cos(Math.toRadians(rotateYCam.getAngle())) * 12);
                        camera.setTranslateX(camera.getTranslateX() - Math.sin(Math.toRadians(rotateYCam.getAngle())) * 12);
                        /*if (camera.getTranslateZ() > 0)
                            camera.setTranslateY(camera.getTranslateY() - Math.sin(Math.toRadians(rotateXCam.getAngle())) * 10);
                        else
                            camera.setTranslateY(camera.getTranslateY() + Math.sin(Math.toRadians(rotateXCam.getAngle())) * 10);*/
                        break;
                    case SHIFT:
                        camera.setTranslateY(camera.getTranslateY() + 10);
                        break;
                    case SPACE:
                        camera.setTranslateY(camera.getTranslateY() - 10);
                        break;
                    case E:
                        angZ = rotateZCam.getAngle() + 1 * Math.cos(Math.toRadians(rotateYCam.getAngle()));
                        angX = rotateXCam.getAngle() + 1 * Math.sin(Math.toRadians(rotateYCam.getAngle()));
                        if ((angZ <= 90) && (angZ >= -90))
                            rotateZCam.setAngle(angZ);
                        //rotateZCam.setAngle(rotateZCam.getAngle() - 1 * Math.cos(Math.toRadians(rotateYCam.getAngle())));
                        if ((angX <= 90) && (angX >= -90))
                            rotateXCam.setAngle(angX);
                        //rotateXCam.setAngle(rotateXCam.getAngle() - 1 * Math.sin(Math.toRadians(rotateYCam.getAngle())));
                        break;
                    case Q:
                        angZ = rotateZCam.getAngle() - 1 * Math.cos(Math.toRadians(rotateYCam.getAngle()));
                        angX = rotateXCam.getAngle() - 1 * Math.sin(Math.toRadians(rotateYCam.getAngle()));
                        if ((angZ <= 90) && (angZ >= -90))
                            rotateZCam.setAngle(angZ);
                        //rotateZCam.setAngle(rotateZCam.getAngle() + 1 * Math.cos(Math.toRadians(rotateYCam.getAngle())));
                        if ((angX <= 90) && (angX >= -90))
                            rotateXCam.setAngle(angX);
                        //rotateXCam.setAngle(rotateXCam.getAngle() + 1 * Math.sin(Math.toRadians(rotateYCam.getAngle())));
                        break;
                }
                CameraView.setCamera(String.valueOf(rotateXCam.getAngle()), String.valueOf(rotateYCam.getAngle()), String.valueOf(rotateZCam.getAngle()), String.valueOf(camera.getTranslateX()), String.valueOf(camera.getTranslateY()), String.valueOf(camera.getTranslateZ()), String.valueOf(camera.getFieldOfView()), String.valueOf(camera.getNearClip()), String.valueOf(camera.getFarClip()));
                //theCamera = new CameraView(String.valueOf(rotateXCam.getAngle()), String.valueOf(rotateYCam.getAngle()), String.valueOf(rotateZCam.getAngle()), String.valueOf(camera.getTranslateX()), String.valueOf(camera.getTranslateY()), String.valueOf(camera.getTranslateZ()), String.valueOf(fov), String.valueOf(nearClip), String.valueOf(farClip));
                //instanceContent.set(Collections.singleton(theCamera), null);
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
    
    /*LookupListener lookupListener = (LookupEvent le) -> checkLookup();
    
    private void checkLookup() {
        TopComponent tc = TopComponent.getRegistry().getActivated();
        if (tc != null && tc.equals(this)) {
            return;
        }
        Collection<? extends Shape> allShapes = lookupResult.allInstances();
        if (Platform.isFxApplicationThread()) {
            addShape(allShapes);
        } else {
            Platform.runLater(() -> addShape(allShapes));
        }
    };*/
    
    /*private void removeShapes() {
        root.getChildren().remove(shapeGroup);
    }
    
    private void addShape(Collection<? extends Shape> allShapes) {
        removeShapes();
        final PhongMaterial blueMaterial = new PhongMaterial();
        blueMaterial.setDiffuseColor(Color.LIGHTBLUE);
        blueMaterial.setSpecularColor(Color.BLUE);
        Shape shape = new Shape(allShapes.iterator().next());
        if (shape.getName().equals("Cube")) {
            Box myBox = new Box(200, 200, 200);
            myBox.setMaterial(blueMaterial);
            //System.out.println(shape.getXCoordinate());
            myBox.setTranslateX(Integer.parseInt(shape.getXCoordinate()));
            myBox.setTranslateY(Integer.parseInt(shape.getYCoordinate()));
            myBox.setTranslateZ(Integer.parseInt(shape.getZCoordinate()));
            shapeGroup = new Group(myBox);
        }
        else if (shape.getName().equals("Sphere")) {
            int radius = 100;
            Sphere sphere = new Sphere(radius);
            sphere.setMaterial(blueMaterial);
            sphere.setTranslateX(Integer.parseInt(shape.getXCoordinate()));
            sphere.setTranslateY(Integer.parseInt(shape.getYCoordinate()));
            sphere.setTranslateZ(Integer.parseInt(shape.getZCoordinate()));
            shapeGroup = new Group(sphere);
        }
        else if (shape.getName().equals("Cylinder")) {
            int radius = 100;
            int height = 200;
            Cylinder cylinder = new Cylinder(radius, height);
            cylinder.setMaterial(blueMaterial);
            cylinder.setTranslateX(Integer.parseInt(shape.getXCoordinate()));
            cylinder.setTranslateY(Integer.parseInt(shape.getYCoordinate()));
            cylinder.setTranslateZ(Integer.parseInt(shape.getZCoordinate()));
            shapeGroup = new Group(cylinder);
        }
        else if (shape.getName().equals("Pyramid")) {
            TriangleMesh pyramidMesh = new TriangleMesh();
            pyramidMesh.getTexCoords().addAll(0,0);
            float h = 200; 
            float s = 300;
            pyramidMesh.getPoints().addAll(
                0, 0, 0, // Point 0 - Top
                0, h, -s/2, // Point 1 - Front
                -s/2, h, 0, // Point 2 - Left
                s/2, h, 0, // Point 3 - Back
                0, h, s/2 // Point 4 - Right
            );
            pyramidMesh.getFaces().addAll(
                0,0, 2,0, 1,0, // Front left face
                0,0, 1,0, 3,0, // Front right face
                0,0, 3,0, 4,0, // Back right face
                0,0, 4,0, 2,0, // Back left face
                4,0, 1,0, 2,0, // Bottom rear face
                4,0, 3,0, 1,0 // Bottom front face
            );
            MeshView pyramid = new MeshView(pyramidMesh);
            pyramid.setDrawMode(DrawMode.FILL);
            pyramid.setMaterial(blueMaterial);
            pyramid.setTranslateX(Integer.parseInt(shape.getXCoordinate()));
            pyramid.setTranslateY(Integer.parseInt(shape.getYCoordinate()));
            pyramid.setTranslateZ(Integer.parseInt(shape.getZCoordinate()));
            shapeGroup = new Group(pyramid);
        }
        root.getChildren().addAll(shapeGroup);
    }*/
    
    ChangeListener<String> xPosListener = (v, oldValue, newValue) -> {
        try {
            camera.setTranslateX(Double.parseDouble(newValue));
        } catch (Exception ex) {
            
        }
    };
    ChangeListener<String> yPosListener = (v, oldValue, newValue) -> {
        try {
            camera.setTranslateY(Double.parseDouble(newValue));
        } catch (Exception ex) {
            
        }
    };
    ChangeListener<String> zPosListener = (v, oldValue, newValue) -> {
        try {
            camera.setTranslateZ(Double.parseDouble(newValue));
        } catch (Exception ex) {
            
        }
    };
    
    private void addPositionListener() {
        CameraView.xCoordinateProperty().addListener(xPosListener); 
        CameraView.yCoordinateProperty().addListener(yPosListener); 
        CameraView.zCoordinateProperty().addListener(zPosListener); 
    }
    
    private void removePositionListener() {
        CameraView.xCoordinateProperty().removeListener(xPosListener); 
        CameraView.yCoordinateProperty().removeListener(yPosListener); 
        CameraView.zCoordinateProperty().removeListener(zPosListener);
    }
    
    ChangeListener<String> xRotListener = (v, oldValue, newValue) -> {
        try {
            if ((Double.parseDouble(newValue) <= 90) && (Double.parseDouble(newValue.toString()) >= -90))
                rotateXCam.setAngle(Double.parseDouble(newValue));
        } catch (Exception ex) {
            
        }
    };
    ChangeListener<String> yRotListener = (v, oldValue, newValue) -> {
        try {
            rotateYCam.setAngle(Double.parseDouble(newValue));
        } catch (Exception ex) {
            
        }
    };
    ChangeListener<String> zRotListener = (v, oldValue, newValue) -> {
        try {
            if ((Double.parseDouble(newValue) <= 90) && (Double.parseDouble(newValue.toString()) >= -90))
                rotateZCam.setAngle(Double.parseDouble(newValue));
        } catch (Exception ex) {
            
        }
    };
    
    private void addRotationListener() {
        CameraView.xRotateProperty().addListener(xRotListener); 
        CameraView.yRotateProperty().addListener(yRotListener); 
        CameraView.zRotateProperty().addListener(zRotListener); 
    }
    
    private void removeRotationListener() {
        CameraView.xRotateProperty().removeListener(xRotListener); 
        CameraView.yRotateProperty().removeListener(yRotListener); 
        CameraView.zRotateProperty().removeListener(zRotListener);
    }
    
    ChangeListener<String> FOVListener = (v, oldValue, newValue) -> {
        try {
            if ((Double.parseDouble(newValue) >= 1) && (Double.parseDouble(newValue) <= 80))
                camera.setFieldOfView(Double.parseDouble(newValue.toString()));
        } catch (Exception ex) {
            
        }
    };
    ChangeListener<String> nearClipListener = (v, oldValue, newValue) -> {
        try {
            if ((Double.parseDouble(newValue) >= 0.1) && (Double.parseDouble(newValue) <= 10))
                camera.setNearClip(Double.parseDouble(newValue.toString()));
        } catch (Exception ex) {
            
        }
    };
    ChangeListener<String> farClipListener = (v, oldValue, newValue) -> {
        try {
            if ((Double.parseDouble(newValue) >= 100) && (Double.parseDouble(newValue) <= 10000))
                camera.setFarClip(Double.parseDouble(newValue.toString()));
        } catch (Exception ex) {
            
        }
    };
    
    private void addFOVListener() {
        CameraView.FOVProperty().addListener(FOVListener); 
        CameraView.nearClipProperty().addListener(nearClipListener); 
        CameraView.farClipProperty().addListener(farClipListener); 
    }
    
    private void removeFOVListener() {
        CameraView.FOVProperty().removeListener(FOVListener); 
        CameraView.nearClipProperty().removeListener(nearClipListener); 
        CameraView.farClipProperty().removeListener(farClipListener);
    }
    
    @Override
    public void componentOpened() {
        /*TopComponent tc = WindowManager.getDefault().findTopComponent("ToolsTopComponent");
        Lookup tcLookup = tc.getLookup();
        lookupResult = tcLookup.lookupResult(Shape.class);*/
        //lookupResult.addLookupListener(lookupListener);
        addPositionListener();
        addRotationListener();
        addFOVListener();
    }

    @Override
    public void componentClosed() {
        //lookupResult.removeLookupListener(lookupListener);
        removePositionListener();
        removeRotationListener();
        removeFOVListener();
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
