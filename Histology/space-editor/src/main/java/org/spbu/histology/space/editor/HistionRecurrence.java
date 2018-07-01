package org.spbu.histology.space.editor;

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
import org.spbu.histology.util.AlertBox;
import org.spbu.histology.model.HistionManager;
import org.spbu.histology.model.RecurrenceShifts;

public class HistionRecurrence {

    public static void display(String title, IntegerProperty xUpperBoundary,
            IntegerProperty xLowerBoundary, IntegerProperty yUpperBoundary,
            IntegerProperty yLowerBoundary, IntegerProperty zUpperBoundary,
            IntegerProperty zLowerBoundary, BooleanProperty buttonPressed,
            DoubleProperty xShift, DoubleProperty zShift) {

        HistionManager hm = Lookup.getDefault().lookup(HistionManager.class);
        if (hm == null) {
            LifecycleManager.getDefault().exit();
        }

        Stage window = new Stage();
        window.setTitle("Модель");

        CheckBox xCheckBox = new CheckBox("Ось X");
        CheckBox yCheckBox = new CheckBox("Ось Y");
        CheckBox zCheckBox = new CheckBox("Ось Z");

        HBox hBox = new HBox();
        HBox hBox2 = new HBox();

        HBox hBox5 = new HBox();
        HBox hBox6 = new HBox();
        HBox hBox7 = new HBox();
        HBox hBox8 = new HBox();
        HBox hBox9 = new HBox();
        HBox hBox10 = new HBox();

        Label boundaryUpperLabelX = new Label("Верхняя граница");
        TextField boundaryUpperFieldX = new TextField();
        boundaryUpperFieldX.setPrefWidth(100);
        boundaryUpperFieldX.setDisable(true);
        boundaryUpperFieldX.setText(String.valueOf(xUpperBoundary.get()));

        Label boundaryLowerLabelX = new Label("Нижняя граница");
        TextField boundaryLowerFieldX = new TextField();
        boundaryLowerFieldX.setPrefWidth(100);
        boundaryLowerFieldX.setDisable(true);
        boundaryLowerFieldX.setText(String.valueOf(xLowerBoundary.get()));

        Label labelXShift = new Label("Сдвиг по X");
        TextField fieldXShift = new TextField();
        fieldXShift.setPrefWidth(100);
        fieldXShift.setDisable(true);
        fieldXShift.setText(String.valueOf(RecurrenceShifts.getXShift()));

        Label labelZShift = new Label("Сдвиг по Z");
        TextField fieldZShift = new TextField();
        fieldZShift.setPrefWidth(100);
        fieldZShift.setDisable(true);
        fieldZShift.setText(String.valueOf(RecurrenceShifts.getZShift()));

        Label boundaryUpperLabelY = new Label("Верхняя граница");
        TextField boundaryUpperFieldY = new TextField();
        boundaryUpperFieldY.setPrefWidth(100);
        boundaryUpperFieldY.setDisable(true);
        boundaryUpperFieldY.setText(String.valueOf(yUpperBoundary.get()));

        Label boundaryLowerLabelY = new Label("Нижняя граница");
        TextField boundaryLowerFieldY = new TextField();
        boundaryLowerFieldY.setPrefWidth(100);
        boundaryLowerFieldY.setDisable(true);
        boundaryLowerFieldY.setText(String.valueOf(yLowerBoundary.get()));

        Label boundaryUpperLabelZ = new Label("Верхняя граница");
        TextField boundaryUpperFieldZ = new TextField();
        boundaryUpperFieldZ.setPrefWidth(100);
        boundaryUpperFieldZ.setDisable(true);
        boundaryUpperFieldZ.setText(String.valueOf(zUpperBoundary.get()));

        Label boundaryLowerLabelZ = new Label("Нижняя граница");
        TextField boundaryLowerFieldZ = new TextField();
        boundaryLowerFieldZ.setPrefWidth(100);
        boundaryLowerFieldZ.setDisable(true);
        boundaryLowerFieldZ.setText(String.valueOf(zLowerBoundary.get()));

        hBox.getChildren().addAll(labelXShift, fieldXShift);
        hBox.setPadding(new Insets(0, 0, 0, 10));
        hBox.setSpacing(20);

        hBox2.getChildren().addAll(labelZShift, fieldZShift);
        hBox2.setPadding(new Insets(0, 0, 0, 10));
        hBox2.setSpacing(20);

        hBox5.getChildren().addAll(boundaryUpperLabelX, boundaryUpperFieldX);
        hBox5.setPadding(new Insets(0, 0, 0, 10));
        hBox5.setSpacing(20);

        hBox6.getChildren().addAll(boundaryLowerLabelX, boundaryLowerFieldX);
        hBox6.setPadding(new Insets(0, 0, 0, 10));
        hBox6.setSpacing(20);

        hBox7.getChildren().addAll(boundaryUpperLabelZ, boundaryUpperFieldZ);
        hBox7.setPadding(new Insets(0, 0, 0, 10));
        hBox7.setSpacing(20);

        hBox8.getChildren().addAll(boundaryLowerLabelZ, boundaryLowerFieldZ);
        hBox8.setPadding(new Insets(0, 0, 0, 10));
        hBox8.setSpacing(20);

        hBox9.getChildren().addAll(boundaryUpperLabelY, boundaryUpperFieldY);
        hBox9.setPadding(new Insets(0, 0, 0, 10));
        hBox9.setSpacing(20);

        hBox10.getChildren().addAll(boundaryLowerLabelY, boundaryLowerFieldY);
        hBox10.setPadding(new Insets(0, 0, 0, 10));
        hBox10.setSpacing(20);

        Button closeButton = new Button("OK");

        closeButton.setOnAction(e -> {
            try {
                xShift.set(Double.parseDouble(fieldXShift.getText()));
                zShift.set(Double.parseDouble(fieldZShift.getText()));
                if (xCheckBox.isSelected()) {
                    xUpperBoundary.set(Integer.parseInt(boundaryUpperFieldX.getText()));
                    xLowerBoundary.set(Integer.parseInt(boundaryLowerFieldX.getText()));
                } else {
                    xUpperBoundary.set(-1);
                    xLowerBoundary.set(-1);
                }

                if (yCheckBox.isSelected()) {
                    yUpperBoundary.set(Integer.parseInt(boundaryUpperFieldY.getText()));
                    yLowerBoundary.set(Integer.parseInt(boundaryLowerFieldY.getText()));
                } else {
                    yUpperBoundary.set(-1);
                    yLowerBoundary.set(-1);
                }

                if (zCheckBox.isSelected()) {
                    zUpperBoundary.set(Integer.parseInt(boundaryUpperFieldZ.getText()));
                    zLowerBoundary.set(Integer.parseInt(boundaryLowerFieldZ.getText()));
                } else {
                    zUpperBoundary.set(-1);
                    zLowerBoundary.set(-1);
                }
                buttonPressed.set(true);
                RecurrenceShifts.setXShift(xShift.get());
                RecurrenceShifts.setZShift(zShift.get());
                window.close();
            } catch (Exception ex) {
                AlertBox.display("Ошибка", "Неверный тип");
            }
        });

        VBox layout = new VBox(10);
        layout.getChildren().addAll(xCheckBox, hBox5, hBox6, hBox, hBox2, zCheckBox, hBox7, hBox8, yCheckBox, hBox9, hBox10, closeButton);

        xCheckBox.selectedProperty().addListener((o, ov, nv) -> {
            if (nv) {
                boundaryUpperFieldX.setDisable(false);
                boundaryLowerFieldX.setDisable(false);
                fieldXShift.setDisable(false);
                fieldZShift.setDisable(false);
            } else {
                boundaryUpperFieldX.setDisable(true);
                boundaryLowerFieldX.setDisable(true);
                fieldXShift.setDisable(true);
                fieldZShift.setDisable(true);
            }
        });
        yCheckBox.selectedProperty().addListener((o, ov, nv) -> {
            if (nv) {
                boundaryUpperFieldY.setDisable(false);
                boundaryLowerFieldY.setDisable(false);
            } else {
                boundaryUpperFieldY.setDisable(true);
                boundaryLowerFieldY.setDisable(true);
            }
        });
        zCheckBox.selectedProperty().addListener((o, ov, nv) -> {
            if (nv) {
                boundaryUpperFieldZ.setDisable(false);
                boundaryLowerFieldZ.setDisable(false);
            } else {
                boundaryUpperFieldZ.setDisable(true);
                boundaryLowerFieldZ.setDisable(true);
            }
        });
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout, 280, 520);
        window.setScene(scene);
        window.showAndWait();
    }
}
