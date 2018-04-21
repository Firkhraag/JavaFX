package org.spbu.histology.space.editor;

import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.geometry.*;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import org.openide.LifecycleManager;
import org.openide.util.Lookup;
import org.spbu.histology.model.HistionManager;

public class HistionRecurrence {
    
    public static void display(String title, DoubleProperty xSpace, DoubleProperty ySpace, DoubleProperty zSpace,
            DoubleProperty xBoundary, DoubleProperty yBoundary, DoubleProperty zBoundary, BooleanProperty buttonPressed) {
        
        HistionManager hm = Lookup.getDefault().lookup(HistionManager.class);
        if (hm == null) {
            LifecycleManager.getDefault().exit();
        }
        
        Stage window = new Stage();
        window.setTitle(title);

        CheckBox xCheckBox = new CheckBox("X axis");
        CheckBox yCheckBox = new CheckBox("Y axis");
        CheckBox zCheckBox = new CheckBox("Z axis");
        
        
        HBox hBox = new HBox();
        HBox hBox2 = new HBox();
        HBox hBox3 = new HBox();
        HBox hBox4 = new HBox();
        HBox hBox5 = new HBox();
        HBox hBox6 = new HBox();
        
        Label labelX = new Label("X spacing");
        TextField fieldX = new TextField();
        fieldX.setDisable(true);
        fieldX.setText(String.valueOf(xSpace.get()));
        Label boundaryLabelX = new Label("Boundary");
        TextField boundaryFieldX = new TextField();
        boundaryFieldX.setDisable(true);
        boundaryFieldX.setText(String.valueOf(xBoundary.get()));
        
        Label labelY = new Label("Y spacing");
        TextField fieldY = new TextField();
        fieldY.setText(String.valueOf(ySpace.get()));
        fieldY.setDisable(true);
        Label boundaryLabelY = new Label("Boundary");
        TextField boundaryFieldY = new TextField();
        boundaryFieldY.setDisable(true);
        boundaryFieldY.setText(String.valueOf(yBoundary.get()));
        
        Label labelZ = new Label("Z spacing");
        TextField fieldZ = new TextField();
        fieldZ.setText(String.valueOf(zSpace.get()));
        fieldZ.setDisable(true);
        Label boundaryLabelZ = new Label("Boundary");
        TextField boundaryFieldZ = new TextField();
        boundaryFieldZ.setDisable(true);
        boundaryFieldZ.setText(String.valueOf(zBoundary.get()));
        
        hBox.getChildren().addAll(labelX, fieldX);
        hBox.setPadding(new Insets(10, 10, 10, 10));
        hBox.setSpacing(20);
        hBox2.getChildren().addAll(labelY, fieldY);
        hBox2.setPadding(new Insets(10, 10, 10, 10));
        hBox2.setSpacing(20);
        hBox3.getChildren().addAll(labelZ, fieldZ);
        hBox3.setPadding(new Insets(10, 10, 10, 10));
        hBox3.setSpacing(20);
        
        hBox4.getChildren().addAll(boundaryLabelX, boundaryFieldX);
        hBox4.setPadding(new Insets(0, 0, 0, 10));
        hBox4.setSpacing(20);
        hBox5.getChildren().addAll(boundaryLabelY, boundaryFieldY);
        hBox5.setPadding(new Insets(0, 0, 0, 10));
        hBox5.setSpacing(20);
        hBox6.getChildren().addAll(boundaryLabelZ, boundaryFieldZ);
        hBox6.setPadding(new Insets(0, 0, 0, 10));
        hBox6.setSpacing(20);
        Button closeButton = new Button("OK");
        
        closeButton.setOnAction(e -> {
            try {
                if (xCheckBox.isSelected()) {
                    xSpace.set(Double.parseDouble(fieldX.getText()));
                    xBoundary.set(Double.parseDouble(boundaryFieldX.getText()));
                }
                else
                    xSpace.set(-1);
                if (yCheckBox.isSelected()) {
                    ySpace.set(Double.parseDouble(fieldY.getText()));
                    yBoundary.set(Double.parseDouble(boundaryFieldY.getText()));
                }
                else
                    ySpace.set(-1);
                if (zCheckBox.isSelected()) {
                    zSpace.set(Double.parseDouble(fieldZ.getText()));
                    zBoundary.set(Double.parseDouble(boundaryFieldZ.getText()));
                }
                else
                    zSpace.set(-1);
                buttonPressed.set(true);
            } catch (Exception ex) {
                
            }
            window.close();
        });
        
        closeButton.disableProperty().bind(Bindings.isEmpty(fieldX.textProperty()).
                or(Bindings.isEmpty(fieldY.textProperty())).
                or(Bindings.isEmpty(fieldZ.textProperty())));

        VBox layout = new VBox(10);
        layout.getChildren().addAll(xCheckBox, hBox, hBox4, zCheckBox, hBox3, hBox6, yCheckBox, hBox2, hBox5, closeButton);
        
        xCheckBox.selectedProperty().addListener((o, ov, nv) -> {
            if (nv) {
                fieldX.setDisable(false);
                boundaryFieldX.setDisable(false);
            }
            else {
                fieldX.setDisable(true);
                boundaryFieldX.setDisable(true);
            }
        });
        yCheckBox.selectedProperty().addListener((o, ov, nv) -> {
            if (nv) {
                fieldY.setDisable(false);
                boundaryFieldY.setDisable(false);
            }
            else {
                fieldY.setDisable(true);
                boundaryFieldY.setDisable(true);
            }
        });
        zCheckBox.selectedProperty().addListener((o, ov, nv) -> {
            if (nv) {
                fieldZ.setDisable(false);
                boundaryFieldZ.setDisable(false);
            }
            else {
                fieldZ.setDisable(true);
                boundaryFieldZ.setDisable(true);
            }
        });
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout, 350, 470);
        window.setScene(scene);
        window.showAndWait();
    }

}
