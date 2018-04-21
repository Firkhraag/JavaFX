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
import org.spbu.histology.model.Cell;

public class CellInformationInitialization {
    
    public static void createScene(Cell c) {
        Parent root;
        try {
            URL location = CellInformationInitialization.class.getResource("Main.fxml");
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(location);
            fxmlLoader.setBuilderFactory(new JavaFXBuilderFactory());

            root = (Parent)fxmlLoader.load(location.openStream());
            MainController mainController = (MainController)fxmlLoader.getController();
            mainController.setCell(new Cell(c.getId(), c));
            Stage stage = new Stage();
            stage.getIcons().add(new Image(CellInformationInitialization.class.getResourceAsStream("cube-with-arrows.png")));
            Scene scene = new Scene(root, 1000, 700);
            stage.setScene(scene);
            stage.setTitle("Cell");
            stage.heightProperty().addListener((obs, oldVal, newVal) -> {
                mainController.setTableHeight(stage.getHeight() - 100);
            });
            stage.showAndWait();
        } catch (Exception ex) {
            Logger.getLogger(CellInformationInitialization.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }
    }
    
}
