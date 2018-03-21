package org.spbu.histology.shape.information;

import org.spbu.histology.model.Shape;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class ShapeInformationInitialization {
    
    public static Stage stage;
    public static String mode;
    private static Shape theShape;
    private static Parent root;
    
    public static void setShape(Shape s) {
        theShape = s;
    }
    
    public static Shape getShape() {
        return theShape;
    }
    
    public static void createScene(String curMode) {
        mode = curMode;
        try {
            URL location = ShapeInformationInitialization.class.getResource("Main.fxml");
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(location);
            fxmlLoader.setBuilderFactory(new JavaFXBuilderFactory());
            root = (Parent)fxmlLoader.load(location.openStream());
        } catch (Exception ex) {
            Logger.getLogger(ShapeInformationInitialization.class.getName()).log(Level.SEVERE, null, ex);
        }
        displayScene();
    }
    
    private static void displayScene() {
        stage = new Stage();
        stage.getIcons().add(new Image(ShapeInformationInitialization.class.getResourceAsStream("cube-with-arrows.png")));
        Scene scene = new Scene(root, 1000, 700);
        stage.setScene(scene);
        stage.setTitle("Shape information");
        stage.show();
    }
    
}
