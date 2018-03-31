package org.spbu.histology.shape.information;

import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class ShapeStructureInformationInitialization {
    
    public static void createScene(long histionId, long cellId, long partId) {
        Parent root;
        try {
            URL location = ShapeInformationInitialization.class.getResource("ShapeStructureInformation.fxml");
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(location);
            fxmlLoader.setBuilderFactory(new JavaFXBuilderFactory());

            root = (Parent)fxmlLoader.load(location.openStream());
            ShapeStructureInformationController shapeStructureInformationController = (ShapeStructureInformationController)fxmlLoader.getController();
            shapeStructureInformationController.setInformation(histionId, cellId, partId);          
        } catch (Exception ex) {
            Logger.getLogger(ShapeInformationInitialization.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }
        displayScene(root, histionId, cellId, partId);
    }
    
    private static void displayScene(Parent root, long histionId, long cellId, long partId) {
        Stage stage = new Stage();
        stage.getIcons().add(new Image(ShapeInformationInitialization.class.getResourceAsStream("cube-with-arrows.png")));
        Scene scene = new Scene(root, 480, 420);
        stage.setScene(scene);
        if (partId != -1) {
            stage.setTitle("Part information");
        } else if (cellId != -1) {
            stage.setTitle("Cell information");
        } else {
            stage.setTitle("Histion information");
        }
        stage.show();
    }
    
}
