package org.spbu.histology.shape.information;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import org.openide.LifecycleManager;
import org.openide.util.Lookup;
import org.spbu.histology.model.HistionManager;
import org.spbu.histology.model.Shape;
import org.spbu.histology.model.ShapeManager;

public class ShapeStructureInformationController implements Initializable  {
    
    @FXML
    private GridPane gridPane;
    
    @FXML
    private TextField nameField;
    
    @FXML
    private TextField xRotationField;
    
    @FXML
    private TextField yRotationField;
    
    @FXML
    private TextField xPositionField;
    
    @FXML
    private TextField yPositionField;
    
    @FXML
    private TextField zPositionField;
    
    @FXML
    private Button updateButton;
    
    private ShapeManager sm = null;
    
    private HistionManager hm = null;
    
    private long pId;
    private long hId;
    private long cId;
    
     @Override
    public void initialize(URL url, ResourceBundle rb) {
        sm = Lookup.getDefault().lookup(ShapeManager.class);
        if (sm == null) {
            LifecycleManager.getDefault().exit();
        }
        
        hm = Lookup.getDefault().lookup(HistionManager.class);
        if (hm == null) {
            LifecycleManager.getDefault().exit();
        }
        gridPane.setVgap(20);
        gridPane.setHgap(20);
        gridPane.setPadding(new Insets(20, 20, 20, 20));
        bindButtonDisabled();
    }
    
    private void bindButtonDisabled() {
        updateButton.disableProperty().bind(Bindings.isEmpty(nameField.textProperty())
                .or(Bindings.isEmpty(xRotationField.textProperty()))
                .or(Bindings.isEmpty(yRotationField.textProperty()))
                .or(Bindings.isEmpty(xPositionField.textProperty()))
                .or(Bindings.isEmpty(yPositionField.textProperty()))
                .or(Bindings.isEmpty(zPositionField.textProperty())));
    }
    
    public void setInformation(long histionId, long cellId, long partId) {
        pId = partId;
        hId = histionId;
        cId = cellId;
        if (partId != -1) {
            String name = sm.getShapeMap().get(partId).getName();
            nameField.setText(name.substring(name.indexOf("<") + 1, name.lastIndexOf(">")));
            xRotationField.setText(String.valueOf(sm.getShapeMap().get(partId).getXRotate()));
            yRotationField.setText(String.valueOf(sm.getShapeMap().get(partId).getYRotate()));
            xPositionField.setText(String.valueOf(sm.getShapeMap().get(partId).getXCoordinate()));
            yPositionField.setText(String.valueOf(sm.getShapeMap().get(partId).getYCoordinate()));
            zPositionField.setText(String.valueOf(sm.getShapeMap().get(partId).getZCoordinate()));
        } else if (cellId != -1) {
            String name = hm.getHistionMap().get(histionId).getItemMap().get(cellId).getName();
            nameField.setText(name.substring(name.indexOf("<") + 1, name.lastIndexOf(">")));
            xRotationField.setText(String.valueOf(hm.getHistionMap().get(histionId).getItemMap().get(cellId).getXRotate()));
            yRotationField.setText(String.valueOf(hm.getHistionMap().get(histionId).getItemMap().get(cellId).getYRotate()));
            xPositionField.setText(String.valueOf(hm.getHistionMap().get(histionId).getItemMap().get(cellId).getXCoordinate()));
            yPositionField.setText(String.valueOf(hm.getHistionMap().get(histionId).getItemMap().get(cellId).getYCoordinate()));
            zPositionField.setText(String.valueOf(hm.getHistionMap().get(histionId).getItemMap().get(cellId).getZCoordinate()));
        } else {
            String name = hm.getHistionMap().get(histionId).getName();
            nameField.setText(name.substring(name.indexOf("<") + 1, name.lastIndexOf(">")));
            xRotationField.setText(String.valueOf(hm.getHistionMap().get(histionId).getXRotate()));
            yRotationField.setText(String.valueOf(hm.getHistionMap().get(histionId).getYRotate()));
            xPositionField.setText(String.valueOf(hm.getHistionMap().get(histionId).getXCoordinate()));
            yPositionField.setText(String.valueOf(hm.getHistionMap().get(histionId).getYCoordinate()));
            zPositionField.setText(String.valueOf(hm.getHistionMap().get(histionId).getZCoordinate()));
        }
    }
    
    @FXML
    private void buttonAction() {
        double xRot, yRot, xTran, yTran, zTran;
        try {
            xRot = Double.parseDouble(xRotationField.getText());
            yRot = Double.parseDouble(yRotationField.getText());
            xTran = Double.parseDouble(xPositionField.getText());
            yTran = Double.parseDouble(yPositionField.getText());
            zTran = Double.parseDouble(zPositionField.getText());
        } catch (Exception ex) {
            AlertBox.display("Error", "Please enter valid numbers in general tab");
            return;
        }
        if (pId != -1) {
            
        } else if (cId != -1) {
            hm.getHistionMap().get(hId).getItemMap().get(cId).setName("Cell <" + nameField.getText() + ">");
            hm.getHistionMap().get(hId).getItemMap().get(cId).setXRotate(xRot);
            hm.getHistionMap().get(hId).getItemMap().get(cId).setYRotate(yRot);
            hm.getHistionMap().get(hId).getItemMap().get(cId).setXCoordinate(xTran);
            hm.getHistionMap().get(hId).getItemMap().get(cId).setYCoordinate(yTran);
            hm.getHistionMap().get(hId).getItemMap().get(cId).setZCoordinate(zTran);
            hm.getHistionMap().get(hId).getItemMap().get(cId).getItems().forEach(p -> {
                sm.updateShape(new Shape(p.getId(), sm.getShapeMap().get(p.getId())), p.getId());
            });
        } else {
            hm.getHistionMap().get(hId).setName("Histion <" + nameField.getText() + ">");
            hm.getHistionMap().get(hId).setXRotate(xRot);
            hm.getHistionMap().get(hId).setYRotate(yRot);
            hm.getHistionMap().get(hId).setXCoordinate(xTran);
            hm.getHistionMap().get(hId).setYCoordinate(yTran);
            hm.getHistionMap().get(hId).setZCoordinate(zTran);
            hm.getHistionMap().get(hId).getItems().forEach(c -> {
                c.getItems().forEach(p -> {
                    sm.updateShape(new Shape(p.getId(), sm.getShapeMap().get(p.getId())), p.getId());
                });
            });
        }
        Stage stage = (Stage) updateButton.getScene().getWindow();
        stage.close();
    }
    
}
