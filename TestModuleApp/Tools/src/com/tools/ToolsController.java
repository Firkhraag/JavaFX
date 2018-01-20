package com.tools;

import java.net.URL;
import java.util.Collections;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import org.openide.util.lookup.InstanceContent;
import org.shape.Shape;

public class ToolsController implements Initializable {
    
    private final InstanceContent instanceContent = new InstanceContent();
    private Shape theShape;
    
    @FXML
    private ComboBox shapeBox;
    
    @FXML
    private VBox vBox;
    
    @FXML
    private Label xLabel;
    
    @FXML
    private Label yLabel;
    
    @FXML
    private Label zLabel;
    
    @FXML
    private TextField xField;
    
    @FXML
    private TextField yField;
    
    @FXML
    private TextField zField;
    
    @FXML
    private Button updateButton;
    
    /*@FXML
    private Label parLabel1;
    
    @FXML
    private Label parLabel2;
    
    @FXML
    private Label parLabel3;
    
    @FXML
    private TextField parField1;
    
    @FXML
    private TextField parField2;
    
    @FXML
    private TextField parField3;*/
    
    @Override
    public void initialize(URL url, ResourceBundle rb) { 
        
        xLabel.setVisible(false);
        xField.setVisible(false);
        yLabel.setVisible(false);
        yField.setVisible(false);
        zLabel.setVisible(false);
        zField.setVisible(false);
        updateButton.setVisible(false);
        
        shapeBox.getSelectionModel().selectedItemProperty().addListener((v, oldValue, newValue) -> {
            xLabel.setVisible(true);
            xField.setVisible(true);
            yLabel.setVisible(true);
            yField.setVisible(true);
            zLabel.setVisible(true);
            zField.setVisible(true);
            updateButton.setVisible(true);
            //String shape = newValue.toString();
            theShape = new Shape(newValue.toString());
            System.out.println(theShape.getName());
            setBindings();
            instanceContent.set(Collections.singleton(theShape), null);
        });
        vBox.setPadding(new Insets(10, 10, 10, 10));
        //shapeBox.getSelectionModel().select(0);
    }
    
    public InstanceContent getInstanceContent() {
        return instanceContent;
    }
    
    private void setBindings() {
        xField.textProperty().bindBidirectional(theShape.xCoordinateProperty());
        yField.textProperty().bindBidirectional(theShape.yCoordinateProperty());
        zField.textProperty().bindBidirectional(theShape.zCoordinateProperty());
    }
    
    @FXML
    private void updateAction() {
        /*theShape.setXCoordinate(xField.getText());
        theShape.setYCoordinate(yField.getText());
        theShape.setZCoordinate(zField.getText());*/
        //instanceContent.remove(Collections.singleton(theShape));
        String name = theShape.getName();
        theShape = new Shape(name, xField.getText(), yField.getText(), zField.getText());
        setBindings();
        instanceContent.set(Collections.singleton(theShape), null);
        //System.out.println(theShape.getXCoordinate());
    }
}
