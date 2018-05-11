package org.spbu.histology.space.editor;

import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
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
import org.spbu.histology.model.AlertBox;
import org.spbu.histology.model.HistionManager;

public class HistionRecurrence {
    
    //public static void display(String title, DoubleProperty xSpace, DoubleProperty ySpace, DoubleProperty zSpace,
    //        DoubleProperty xBoundary, DoubleProperty yBoundary, DoubleProperty zBoundary, BooleanProperty buttonPressed) {
    
    /*public static void display(String title, DoubleProperty xSpace, DoubleProperty ySpace,
            DoubleProperty xzSpaceX, DoubleProperty xzSpaceZ, DoubleProperty zSpace, IntegerProperty xBoundary,
            IntegerProperty yBoundary, IntegerProperty xyBoundary, IntegerProperty zBoundary, BooleanProperty buttonPressed) {*/
    public static void display(String title, IntegerProperty xBoundary, IntegerProperty yBoundary,
           // IntegerProperty xzBoundary, IntegerProperty zBoundary, BooleanProperty buttonPressed) {
            IntegerProperty xzBoundary, IntegerProperty zBoundary, BooleanProperty buttonPressed, DoubleProperty xShift, DoubleProperty zShift) {
        
        HistionManager hm = Lookup.getDefault().lookup(HistionManager.class);
        if (hm == null) {
            LifecycleManager.getDefault().exit();
        }
        
        Stage window = new Stage();
        //window.initStyle(StageStyle.UTILITY);
        window.setTitle(title);

        CheckBox xCheckBox = new CheckBox("X axis");
        CheckBox yCheckBox = new CheckBox("Y axis");
        CheckBox xyCheckBox = new CheckBox("XZ axis");
        CheckBox zCheckBox = new CheckBox("Z axis");
        
        HBox hBox = new HBox();
        HBox hBox2 = new HBox();
        /*HBox hBox = new HBox();
        HBox hBox2 = new HBox();
        HBox hBox3 = new HBox();
        HBox hBox4 = new HBox();*/
        
        HBox hBox5 = new HBox();
        HBox hBox6 = new HBox();
        //HBox hBox7 = new HBox();
        HBox hBox8 = new HBox();
        
        /*Label labelX = new Label("X spacing");
        TextField fieldX = new TextField();
        fieldX.setDisable(true);
        fieldX.setText(String.valueOf(xSpace.get()));*/
        
        Label boundaryLabelX = new Label("Boundary");
        TextField boundaryFieldX = new TextField();
        boundaryFieldX.setPrefWidth(100);
        boundaryFieldX.setDisable(true);
        boundaryFieldX.setText(String.valueOf(xBoundary.get()));
        
        Label labelXShift = new Label("X shift");
        TextField fieldXShift = new TextField();
        fieldXShift.setPrefWidth(100);
        fieldXShift.setDisable(true);
        fieldXShift.setText("0");
        
        Label labelZShift = new Label("Z shift");
        TextField fieldZShift = new TextField();
        fieldZShift.setPrefWidth(100);
        fieldZShift.setDisable(true);
        fieldZShift.setText("0");
        
        /*Label labelY = new Label("Y spacing");
        TextField fieldY = new TextField();
        fieldY.setText(String.valueOf(ySpace.get()));
        fieldY.setDisable(true);*/
        Label boundaryLabelY = new Label("Boundary");
        TextField boundaryFieldY = new TextField();
        boundaryFieldY.setPrefWidth(100);
        boundaryFieldY.setDisable(true);
        boundaryFieldY.setText(String.valueOf(yBoundary.get()));
        
        /*Label labelXY = new Label("XY spacing");
        TextField fieldXY = new TextField();
        fieldXY.setDisable(true);
        fieldXY.setText(String.valueOf(xySpace.get()));*/
        /*Label boundaryLabelXY = new Label("Boundary");
        TextField boundaryFieldXY = new TextField();
        boundaryFieldXY.setDisable(true);
        boundaryFieldXY.setText(String.valueOf(xzBoundary.get()));
        
        /*Label labelZ = new Label("Z spacing");
        TextField fieldZ = new TextField();
        fieldZ.setText(String.valueOf(zSpace.get()));
        fieldZ.setDisable(true);*/
        Label boundaryLabelZ = new Label("Boundary");
        TextField boundaryFieldZ = new TextField();
        boundaryFieldZ.setPrefWidth(100);
        boundaryFieldZ.setDisable(true);
        boundaryFieldZ.setText(String.valueOf(zBoundary.get()));
        
        
        hBox.getChildren().addAll(labelXShift, fieldXShift);
        //hBox.setPadding(new Insets(10, 10, 10, 10));
        hBox.setPadding(new Insets(0, 0, 0, 10));
        hBox.setSpacing(20);
        
        hBox2.getChildren().addAll(labelZShift, fieldZShift);
        //hBox2.setPadding(new Insets(10, 10, 10, 10));
        hBox2.setPadding(new Insets(0, 0, 0, 10));
        hBox2.setSpacing(20);
        /*hBox.getChildren().addAll(labelX, fieldX);
        hBox.setPadding(new Insets(10, 10, 10, 10));
        hBox.setSpacing(20);
        hBox2.getChildren().addAll(labelZ, fieldZ);
        hBox2.setPadding(new Insets(10, 10, 10, 10));
        hBox2.setSpacing(20);
        hBox3.getChildren().addAll(labelXY, fieldXY);
        hBox3.setPadding(new Insets(10, 10, 10, 10));
        hBox3.setSpacing(20);
        hBox4.getChildren().addAll(labelY, fieldY);
        hBox4.setPadding(new Insets(10, 10, 10, 10));
        hBox4.setSpacing(20);*/
        
        hBox5.getChildren().addAll(boundaryLabelX, boundaryFieldX);
        hBox5.setPadding(new Insets(0, 0, 0, 10));
        hBox5.setSpacing(20);
        hBox6.getChildren().addAll(boundaryLabelZ, boundaryFieldZ);
        hBox6.setPadding(new Insets(0, 0, 0, 10));
        hBox6.setSpacing(20);
        /*hBox7.getChildren().addAll(boundaryLabelXY, boundaryFieldXY);
        hBox7.setPadding(new Insets(0, 0, 0, 10));
        hBox7.setSpacing(20);*/
        hBox8.getChildren().addAll(boundaryLabelY, boundaryFieldY);
        hBox8.setPadding(new Insets(0, 0, 0, 10));
        hBox8.setSpacing(20);
        
        Button closeButton = new Button("OK");
        
        closeButton.setOnAction(e -> {
            try {
                xShift.set(Double.parseDouble(fieldXShift.getText()));
                zShift.set(Double.parseDouble(fieldZShift.getText()));
                if (xCheckBox.isSelected()) {
                    /*if ((Double.parseDouble(fieldX.getText()) < 0) ||
                            (Double.parseDouble(boundaryFieldX.getText()) < 0))
                        xSpace.set(-1);
                    else {
                        xSpace.set(Double.parseDouble(fieldX.getText()));
                        xBoundary.set(Integer.parseInt(boundaryFieldX.getText()));
                    }*/
                    xBoundary.set(Integer.parseInt(boundaryFieldX.getText()));
                } else //xSpace.set(-1);
                {
                    xBoundary.set(-1);
                }
                
                if (yCheckBox.isSelected()) {
                    //ySpace.set(Double.parseDouble(fieldY.getText()));
                    yBoundary.set(Integer.parseInt(boundaryFieldY.getText()));
                } else //ySpace.set(-1);
                {
                    yBoundary.set(-1);
                }
                
                /*if (xyCheckBox.isSelected()) {
                    //zSpace.set(Double.parseDouble(fieldZ.getText()));
                    xzBoundary.set(Integer.parseInt(boundaryFieldXY.getText()));
                } else {
                    xzBoundary.set(-1);
                }*/
                
                if (zCheckBox.isSelected()) {
                    //zSpace.set(Double.parseDouble(fieldZ.getText()));
                    zBoundary.set(Integer.parseInt(boundaryFieldZ.getText()));
                } else {
                    zBoundary.set(-1);
                }
                buttonPressed.set(true);
                window.close();
            } catch (Exception ex) {
                AlertBox.display("Error", "Wrong type");
            }
            //window.close();
        });
        
        /*closeButton.disableProperty().bind(Bindings.isEmpty(fieldX.textProperty()).
                or(Bindings.isEmpty(fieldY.textProperty())).
                or(Bindings.isEmpty(fieldZ.textProperty())));*/

        VBox layout = new VBox(10);
        //layout.getChildren().addAll(xCheckBox, hBox, hBox5, zCheckBox, hBox2, hBox6,
        //        xyCheckBox, hBox3, hBox7, yCheckBox, hBox4, hBox8, closeButton);
        
        /*layout.getChildren().addAll(xCheckBox, hBox5, zCheckBox, hBox6,
                xyCheckBox, hBox7, yCheckBox, hBox8, closeButton);*/
        layout.getChildren().addAll(xCheckBox, hBox5, hBox, hBox2, zCheckBox, hBox6, yCheckBox, hBox8, closeButton);
        
        xCheckBox.selectedProperty().addListener((o, ov, nv) -> {
            if (nv) {
                //fieldX.setDisable(false);
                boundaryFieldX.setDisable(false);
                fieldXShift.setDisable(false);
                fieldZShift.setDisable(false);
            }
            else {
                //fieldX.setDisable(true);
                boundaryFieldX.setDisable(true);
                fieldXShift.setDisable(true);
                fieldZShift.setDisable(true);
            }
        });
        yCheckBox.selectedProperty().addListener((o, ov, nv) -> {
            if (nv) {
                //fieldY.setDisable(false);
                boundaryFieldY.setDisable(false);
            }
            else {
                //fieldY.setDisable(true);
                boundaryFieldY.setDisable(true);
            }
        });
        /*xyCheckBox.selectedProperty().addListener((o, ov, nv) -> {
            if (nv) {
                //fieldXY.setDisable(false);
                boundaryFieldXY.setDisable(false);
                xCheckBox.setSelected(false);
                xCheckBox.setDisable(true);
            }
            else {
                //fieldXY.setDisable(true);
                boundaryFieldXY.setDisable(true);
                xCheckBox.setDisable(false);
            }
        });*/
        zCheckBox.selectedProperty().addListener((o, ov, nv) -> {
            if (nv) {
                //fieldZ.setDisable(false);
                boundaryFieldZ.setDisable(false);
            }
            else {
                //fieldZ.setDisable(true);
                boundaryFieldZ.setDisable(true);
            }
        });
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout, 260, 380); //340         //470
        window.setScene(scene);
        window.showAndWait();
    }

}
