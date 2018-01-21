/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.space;

import java.awt.BorderLayout;
import java.util.Collection;
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
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.DrawMode;
import javafx.scene.shape.MeshView;
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
import org.shape.Shape;

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
    
    final Group axisGroup = new Group();
    
    private double mousePosX;
    private double mousePosY;
    private double mouseOldX;
    private double mouseOldY;
  
    private Lookup.Result<Shape> lookupResult = null;
    
    private static JFXPanel fxPanel;
    private Scene scene;
    private PerspectiveCamera camera;
    private Group root = new Group();
    private Group shapeGroup;
    double fov = 35.0;

    public SpaceTopComponent() {
        initComponents();
        setName(Bundle.CTL_SpaceTopComponent());
        setToolTipText(Bundle.HINT_SpaceTopComponent());
        
        setLayout(new BorderLayout());
        init();
    }
    
     private void init() {
        fxPanel = new JFXPanel();
        add(fxPanel, BorderLayout.CENTER);
        Platform.setImplicitExit(false);
        Platform.runLater(() -> createScene());
    }
    
    private void createScene() {        
        buildAxes();
        scene = new Scene(root, 1000, 1000, true, SceneAntialiasing.BALANCED);
        buildCamera();
        handleKeyboard();
        fxPanel.setScene(scene);       
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

        final Box xAxis = new Box(1000.0, 1, 1);
        final Box yAxis = new Box(1, 1000.0, 1);
        final Box zAxis = new Box(1, 1, 1000.0);
        
        xAxis.setTranslateX(0);
        xAxis.setTranslateY(0);
        xAxis.setTranslateZ(0);

        xAxis.setMaterial(redMaterial);
        yAxis.setMaterial(greenMaterial);
        zAxis.setMaterial(blueMaterial);

        axisGroup.getChildren().addAll(xAxis, yAxis, zAxis);
        root.getChildren().addAll(axisGroup);
    }
    
    private void buildCamera() {
        camera = new PerspectiveCamera(true);
        camera.setTranslateZ(-1000);
        camera.setNearClip(0.1);
        camera.setFarClip(2000.0);
        camera.setFieldOfView(fov);
        scene.setCamera(camera);          
    }
    
    private void handleMouseEvents() {
        
        Rotate rotateX = new Rotate(0, Rotate.X_AXIS);
        Rotate rotateY = new Rotate(0, Rotate.Y_AXIS);
        shapeGroup.getTransforms().addAll(rotateX, rotateY);
        axisGroup.getTransforms().clear();
        axisGroup.getTransforms().addAll(rotateX, rotateY);
        
        
        Rotate rotateXCam = new Rotate(0, Rotate.X_AXIS);
        Rotate rotateYCam = new Rotate(0, Rotate.Y_AXIS);
        camera.getTransforms().clear();
        camera.getTransforms().addAll(rotateXCam, rotateYCam);
        
        scene.setOnMousePressed(me -> {         
            mouseOldX = me.getSceneX();
            mouseOldY = me.getSceneY();
        });
        scene.setOnMouseDragged(me -> {
            
            if (me.isPrimaryButtonDown()) {
                mousePosX = me.getSceneX();
                mousePosY = me.getSceneY();
                rotateX.setAngle(rotateX.getAngle()-(mousePosY - mouseOldY)*0.1);
                rotateY.setAngle(rotateY.getAngle()+(mousePosX - mouseOldX)*0.1);
                mouseOldX = mousePosX;
                mouseOldY = mousePosY;
            }
            if (me.isSecondaryButtonDown()) {
                mousePosX = me.getSceneX();
                mousePosY = me.getSceneY();
                rotateXCam.setAngle(rotateXCam.getAngle()-(mousePosY - mouseOldY)*0.1);
                rotateYCam.setAngle(rotateYCam.getAngle()+(mousePosX - mouseOldX)*0.1);
                mouseOldX = mousePosX;
                mouseOldY = mousePosY;
            }
        });
        
        scene.setOnScroll((ScrollEvent e) -> {
                double dy = e.getDeltaY();
                if (dy < 0)
                    fov += 0.5;
                else
                    fov -= 0.5;
                camera.setFieldOfView(fov);
            });
    }
    
    private void handleKeyboard() {
        final boolean moveCamera = true;
        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                switch (event.getCode()) {
                    case A:
                        camera.setTranslateX(camera.getTranslateX() - 5);
                        break;
                    case D:
                        camera.setTranslateX(camera.getTranslateX() + 5);
                        break;
                    case S:
                        camera.setTranslateY(camera.getTranslateY() + 5);
                        break;
                    case W:
                        camera.setTranslateY(camera.getTranslateY() - 5);
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
    // <editor-fold defaultstate="collapsed" desc="Generated Code">                          
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
    }// </editor-fold>                        

    // Variables declaration - do not modify                     
    // End of variables declaration                   
    LookupListener lookupListener = (LookupEvent le) -> checkLookup();
    
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
    };
    
    private void removeShapes() {
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
            System.out.println(shape.getXCoordinate());
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
            pyramid.setTranslateZ(Integer.parseInt(shape.getZCoordinate()));;
            shapeGroup = new Group(pyramid);
        }
        handleMouseEvents();
        root.getChildren().addAll(shapeGroup);
    }
    
    @Override
    public void componentOpened() {
        TopComponent tc = WindowManager.getDefault().findTopComponent("ToolsTopComponent");
        Lookup tcLookup = tc.getLookup();
        lookupResult = tcLookup.lookupResult(Shape.class);
        lookupResult.addLookupListener(lookupListener);
    }

    @Override
    public void componentClosed() {
        lookupResult.removeLookupListener(lookupListener);
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
