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

public class CameraViewController implements Initializable  {
    
    private CameraView theCamera;
    
    @FXML
    private VBox vBox;
    
    @FXML
    private Label xRotationLabel;
    
    @FXML
    private Label yRotationLabel;
    
    @FXML
    private Label zRotationLabel;
    
    @FXML
    private TextField xRotation;
    
    @FXML
    private TextField yRotation;
    
    @FXML
    private TextField zRotation;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) { 
        vBox.setPadding(new Insets(10, 10, 10, 10));
        xRotationLabel.setPadding(new Insets(0, 0, 0, 0));
        yRotationLabel.setPadding(new Insets(10, 0, 0, 0));
        zRotationLabel.setPadding(new Insets(10, 0, 0, 0));
        setBindings();
    }
    
    private void setBindings() {
        xRotation.textProperty().bindBidirectional(theCamera.xRotateProperty());
        yRotation.textProperty().bindBidirectional(theCamera.yRotateProperty());
        zRotation.textProperty().bindBidirectional(theCamera.zRotateProperty());
    }
    
}
