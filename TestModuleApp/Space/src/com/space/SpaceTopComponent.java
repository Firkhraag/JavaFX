/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.space;

import java.awt.BorderLayout;
import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.concurrent.CountDownLatch;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.PerspectiveCamera;
import javafx.scene.PointLight;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
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
import org.openide.LifecycleManager;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.windows.TopComponent;
import org.openide.util.NbBundle.Messages;
import org.openide.util.Utilities;
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
    
    
        private double mousePosX;
        private double mousePosY;
        private double mouseOldX;
        private double mouseOldY;
        
    
    
    private Lookup.Result<Shape> lookupResult = null;
    
    private static JFXPanel fxPanel;
    private Scene scene;
    private PerspectiveCamera camera;
    /*private final Xform cameraXform = new Xform();
    private final Xform cameraXform2 = new Xform();
    private final Xform cameraXform3 = new Xform();*/
    //private double mousePosX, mousePosY = 0;
    private Group root = new Group();
    private Group shapeGroup;
    //Cylinder cylinder;
    //Box myBox;
    //Sphere sphere;

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
        /*camera = new PerspectiveCamera(false);
        camera.setTranslateX(-500.0);
        camera.setTranslateY(-290.0);
        camera.setTranslateZ(0.0);
        cameraXform.getChildren().add(cameraXform2);
        cameraXform2.getChildren().add(cameraXform3);
        cameraXform3.getChildren().add(camera);
        cameraXform3.setRotateZ(180.0);      
        root.getChildren().addAll(cameraXform);*/
        
        PointLight light = new PointLight(Color.WHITE);
        light.setTranslateX(-1000);
        light.setTranslateY(100);
        light.setTranslateZ(-1000);
        root.getChildren().add(light);
        
        
        scene = new Scene(root);
        //scene.setCamera(camera);
        buildCamera();
        //handleMouseEvents();
        fxPanel.setScene(scene);       
    }
    
    private void buildCamera() {
        /*camera.setNearClip(1.0);
        camera.setFarClip(10000.0);
        camera.setFieldOfView(2d*dimModel/3d);
        camera.getTransforms().addAll(yUpRotate,cameraPosition,
                                      cameraLookXRotate,cameraLookZRotate);
        cameraXform.getChildren().add(cameraXform2);
        cameraXform2.getChildren().add(camera);
        cameraPosition.setZ(-2d*dimModel);
        root.getChildren().add(cameraXform);
         
        // Rotate camera to show isometric view X right, Y top, Z 120º left-down from each
        cameraXform.setRx(-30.0);
        cameraXform.setRy(30);*/
        
        PerspectiveCamera camera = new PerspectiveCamera(true);
        camera.setTranslateZ(-1000);
        camera.setNearClip(0.1);
        camera.setFarClip(2000.0);
        camera.setFieldOfView(35);
        scene.setCamera(camera);
 
        
    }
    
    private void handleMouseEvents() {
        Rotate rotateX = new Rotate(30, 0, 0, 0, Rotate.X_AXIS);
        Rotate rotateY = new Rotate(20, 0, 0, 0, Rotate.Y_AXIS);
        shapeGroup.getTransforms().addAll(rotateX, rotateY);
        /*scene.setOnMousePressed((MouseEvent e) -> {
            mousePosX = e.getSceneX();
            mousePosY = e.getSceneY();
            //System.out.println(e.getSceneX());
            //System.out.println(e.getSceneY());
        });

        scene.setOnMouseDragged((MouseEvent e) -> {
            double modifier = 1.0;
            double modifierFactor = 0.1;
            double dx = (mousePosX - e.getSceneX()) ;
            double dy = (mousePosY - e.getSceneY());
            if (e.isPrimaryButtonDown()) {
                    cameraXform.ry.setAngle(cameraXform.ry.getAngle() - dx*modifierFactor*modifier);  // +
                    cameraXform.rx.setAngle(cameraXform.rx.getAngle() + dy*modifierFactor*modifier);  // -
            }
        });*/
        
        scene.setOnMousePressed(me -> {
            mouseOldX = me.getSceneX();
            mouseOldY = me.getSceneY();
        });
        scene.setOnMouseDragged(me -> {
            mousePosX = me.getSceneX();
            mousePosY = me.getSceneY();
            rotateX.setAngle(rotateX.getAngle()-(mousePosY - mouseOldY));
            rotateY.setAngle(rotateY.getAngle()+(mousePosX - mouseOldX));
            mouseOldX = mousePosX;
            mouseOldY = mousePosY;
        });
        
        scene.setOnScroll((ScrollEvent e) -> {
                double zoom = 1.05;
                double dy = e.getDeltaY();
                if (dy < 0)
                  zoom = 2.0 - zoom;
                shapeGroup.setScaleX(shapeGroup.getScaleX() * zoom);
                shapeGroup.setScaleY(shapeGroup.getScaleY() * zoom);
                shapeGroup.setScaleZ(shapeGroup.getScaleZ() * zoom);
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
