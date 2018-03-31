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
    
    public static void createScene(Shape s) {
        Parent root;
        try {
            URL location = ShapeInformationInitialization.class.getResource("Main.fxml");
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(location);
            fxmlLoader.setBuilderFactory(new JavaFXBuilderFactory());

            root = (Parent)fxmlLoader.load(location.openStream());
            MainController mainController = (MainController)fxmlLoader.getController();
            mainController.setShape(s);
        } catch (Exception ex) {
            Logger.getLogger(ShapeInformationInitialization.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }
        displayScene(root);
    }
    
    private static void displayScene(Parent root) {
        Stage stage = new Stage();
        stage.getIcons().add(new Image(ShapeInformationInitialization.class.getResourceAsStream("cube-with-arrows.png")));
        Scene scene = new Scene(root, 1000, 700);
        stage.setScene(scene);
        stage.setTitle("Shape information");
        stage.show();
    }
    
}
