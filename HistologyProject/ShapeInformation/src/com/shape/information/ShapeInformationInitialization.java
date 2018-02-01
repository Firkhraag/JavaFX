package com.shape.information;

import com.model.Shape;
import java.net.URL;
import java.util.Collection;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javax.swing.SwingUtilities;
import org.openide.LifecycleManager;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

public class ShapeInformationInitialization {
    
    public static Stage stage;
    public static String mode;
    private static Shape theShape;
    private static Collection<? extends Shape> allShapes = null;
    private static Lookup.Result<Shape> lookupResult = null;
    private static Parent root;
    private static ShapeInformationController controller;
    
    public static void setShape(Shape s) {
        theShape = s;
    }
    
    public static void createScene(String curMode) {   
        mode = curMode;
        try {
            URL location = ShapeInformationInitialization.class.getResource("ShapeInformation.fxml");
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(location);
            fxmlLoader.setBuilderFactory(new JavaFXBuilderFactory());
            root = (Parent)fxmlLoader.load(location.openStream());
            controller = (ShapeInformationController)fxmlLoader.getController();
        } catch (Exception ex) {
            Logger.getLogger(ShapeInformationInitialization.class.getName()).log(Level.SEVERE, null, ex);
        }
        displayScene();
        if (mode == "Edit") {
            controller.doUpdate(theShape);
        }
    }
    
    /*private static void loadLookup() {
        System.out.println("SWT");
        TopComponent tc = WindowManager.getDefault().findTopComponent("ToolsTopComponent");
        System.out.println("SWT2");
        System.out.println(tc);
        Lookup tcLookup = tc.getLookup();
        System.out.println("SWT3");
        System.out.println(tcLookup);
        lookupResult = tcLookup.lookupResult(Shape.class);
        System.out.println("SWT4");
        System.out.println(lookupResult);
        allShapes = lookupResult.allInstances();
        System.out.println("SWT5");
        System.out.println(allShapes);
    }
    
    private static void test() {
        System.out.println("Normal thread");
        //Collection<? extends Shape> allShapes = lookupResult.allInstances();
        System.out.println(allShapes.size());
    }
    
    private static void loadScene() {
        try {
            URL location = ShapeInformationInitialization.class.getResource("ShapeInformation.fxml");
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(location);
            fxmlLoader.setBuilderFactory(new JavaFXBuilderFactory());
            root = (Parent)fxmlLoader.load(location.openStream());
            controller = (ShapeInformationController)fxmlLoader.getController();
        } catch (Exception ex) {
            Logger.getLogger(ShapeInformationInitialization.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private static LookupListener lookupListener = (LookupEvent le) -> checkLookup();
    
    private static synchronized void checkLookup() {
        Collection<? extends Shape> allShapes = lookupResult.allInstances();
        if (Platform.isFxApplicationThread()) {
            controller.doUpdate(allShapes);
        } else {
            Platform.runLater(() -> controller.doUpdate(allShapes));
        }
    }*/
    
    private static void displayScene() {
        stage = new Stage();
        stage.getIcons().add(new Image(ShapeInformationInitialization.class.getResourceAsStream("cube-with-arrows.png")));
        Scene scene = new Scene(root, 500, 800);
        stage.setScene(scene);
        stage.setTitle("Shape information");
        stage.show();
    }
    
}
