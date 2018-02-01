package com.tools;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import org.openide.util.lookup.InstanceContent;
import com.model.CameraView;
import java.util.Collection;
import java.util.Collections;
import org.openide.util.Lookup;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

public class CameraViewController implements Initializable  {
    
    //private final InstanceContent instanceContent = new InstanceContent();
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
        /*TopComponent tc = WindowManager.getDefault().findTopComponent("SpaceTopComponent");
        Lookup tcLookup = tc.getLookup();
        LookupResult lookupResult = tcLookup.lookupResult(CameraView.class);
        Collection<? extends CameraView> cam = lookupResult.allInstances();
        theCamera = cam.iterator().next();
        setBindings();*/
    }
    
    /*public InstanceContent getInstanceContent() {
        return instanceContent;
    }*/
    
    private void setBindings() {
        xRotation.textProperty().bindBidirectional(theCamera.xRotateProperty());
        yRotation.textProperty().bindBidirectional(theCamera.yRotateProperty());
        zRotation.textProperty().bindBidirectional(theCamera.zRotateProperty());
    }
    
}
