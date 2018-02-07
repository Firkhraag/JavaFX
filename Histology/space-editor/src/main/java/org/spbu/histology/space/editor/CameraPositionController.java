package org.spbu.histology.space.editor;

import org.spbu.histology.model.CameraView;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class CameraPositionController implements Initializable  {
    
    private CameraView theCamera;
    
    @FXML
    private VBox vBox;
    
    @FXML
    private Label xPositionLabel;
    
    @FXML
    private Label yPositionLabel;
    
    @FXML
    private Label zPositionLabel;
    
    @FXML
    private TextField xPosition;
    
    @FXML
    private TextField yPosition;
    
    @FXML
    private TextField zPosition;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) { 
        vBox.setPadding(new Insets(10, 10, 10, 10));
        xPositionLabel.setPadding(new Insets(0, 0, 0, 0));
        yPositionLabel.setPadding(new Insets(10, 0, 0, 0));
        zPositionLabel.setPadding(new Insets(10, 0, 0, 0));
        setBindings();
    }
    
    private void setBindings() {
        xPosition.textProperty().bindBidirectional(theCamera.xCoordinateProperty());
        yPosition.textProperty().bindBidirectional(theCamera.yCoordinateProperty());
        zPosition.textProperty().bindBidirectional(theCamera.zCoordinateProperty());
    }
    
}
