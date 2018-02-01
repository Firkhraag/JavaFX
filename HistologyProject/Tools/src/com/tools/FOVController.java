/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tools;

import com.model.CameraView;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

/**
 *
 * @author Sigl
 */
public class FOVController implements Initializable  {
    
    private CameraView theCamera;
    
    @FXML
    private VBox vBox;
    
    @FXML
    private Label FOVLabel;
    
    @FXML
    private Label nearClipLabel;
    
    @FXML
    private Label farClipLabel;
    
    @FXML
    private TextField FOV;
    
    @FXML
    private TextField nearClip;
    
    @FXML
    private TextField farClip;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) { 
        vBox.setPadding(new Insets(10, 10, 10, 10));
        FOVLabel.setPadding(new Insets(0, 0, 0, 0));
        nearClipLabel.setPadding(new Insets(10, 0, 0, 0));
        farClipLabel.setPadding(new Insets(10, 0, 0, 0));
        setBindings();
    }
    
    private void setBindings() {
        FOV.textProperty().bindBidirectional(theCamera.FOVProperty());
        nearClip.textProperty().bindBidirectional(theCamera.nearClipProperty());
        farClip.textProperty().bindBidirectional(theCamera.farClipProperty());
    }
    
}
