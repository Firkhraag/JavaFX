package org.spbu.histology.shape.information;

import org.spbu.histology.model.Shape;
import java.net.URL;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.openide.util.Lookup;

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
        if (mode.equals("Edit")) {
            controller.doUpdate(theShape);
        }
    }
    
    private static void displayScene() {
        stage = new Stage();
        stage.getIcons().add(new Image(ShapeInformationInitialization.class.getResourceAsStream("cube-with-arrows.png")));
        Scene scene = new Scene(root, 500, 800);
        stage.setScene(scene);
        stage.setTitle("Shape information");
        stage.show();
    }
    
}
