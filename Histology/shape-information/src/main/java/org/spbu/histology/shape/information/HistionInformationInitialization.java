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

public class HistionInformationInitialization {
    
    public static void createScene(Integer histionId) {
        Parent root;
        try {
            URL location = CellInformationInitialization.class.getResource("HistionInformation.fxml");
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(location);
            fxmlLoader.setBuilderFactory(new JavaFXBuilderFactory());

            root = (Parent)fxmlLoader.load(location.openStream());
            HistionInformationController histionInformationController = (HistionInformationController)fxmlLoader.getController();
            histionInformationController.setHistionId(histionId);          
        } catch (Exception ex) {
            Logger.getLogger(CellInformationInitialization.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }
        displayScene(root);
    }
    
    private static void displayScene(Parent root) {
        Stage stage = new Stage();
        stage.getIcons().add(new Image(CellInformationInitialization.class.getResourceAsStream("cube-with-arrows.png")));
        Scene scene = new Scene(root, 480, 420);
        stage.setScene(scene);
        stage.setTitle("Histion information");
        stage.showAndWait();
    }
    
}
