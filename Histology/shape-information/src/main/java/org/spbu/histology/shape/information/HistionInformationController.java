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
import org.spbu.histology.model.AlertBox;
import org.spbu.histology.model.Cell;
import org.spbu.histology.model.HistionManager;

public class HistionInformationController implements Initializable  {
    
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
    
    private HistionManager hm = null;
    
    private Integer hId;
    
     @Override
    public void initialize(URL url, ResourceBundle rb) {
        
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
    
    public void setHistionId(Integer histionId) {
        hId = histionId;
        String name = hm.getHistionMap().get(histionId).getName();
        nameField.setText(name);
        //xRotationField.setText(String.valueOf(hm.getHistionMap().get(histionId).getXRotate()));
        //yRotationField.setText(String.valueOf(hm.getHistionMap().get(histionId).getYRotate()));
        xPositionField.setText(String.valueOf(hm.getHistionMap().get(histionId).getXCoordinate()));
        yPositionField.setText(String.valueOf(hm.getHistionMap().get(histionId).getYCoordinate()));
        zPositionField.setText(String.valueOf(hm.getHistionMap().get(histionId).getZCoordinate()));
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
        hm.getHistionMap().get(hId).setName(nameField.getText());
        //hm.getHistionMap().get(hId).setXRotate(xRot);
        //hm.getHistionMap().get(hId).setYRotate(yRot);
        hm.getHistionMap().get(hId).setXCoordinate(xTran);
        hm.getHistionMap().get(hId).setYCoordinate(yTran);
        hm.getHistionMap().get(hId).setZCoordinate(zTran);
        hm.getHistionMap().get(hId).getItems().forEach(c -> {
            Cell newCell = new Cell(c.getId(), c);
            c.getItems().forEach(p -> {
                newCell.addChild(p);
            });
            hm.getHistionMap().get(hId).addChild(newCell);
        });
        Stage stage = (Stage) updateButton.getScene().getWindow();
        stage.close();
    }
    
}
