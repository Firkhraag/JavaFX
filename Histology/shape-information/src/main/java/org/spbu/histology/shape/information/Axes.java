package org.spbu.histology.shape.information;

import javafx.beans.binding.Bindings;
import javafx.geometry.Side;
import javafx.scene.chart.NumberAxis;
import javafx.scene.layout.Pane;

public class Axes extends Pane {
    private NumberAxis xAxis;
    private NumberAxis yAxis;

    public Axes(
            int width, int height,
            double xLow, double xHi, double xTickUnit,
            double yLow, double yHi, double yTickUnit) {
        setMinSize(Pane.USE_PREF_SIZE, Pane.USE_PREF_SIZE);
        setPrefSize(width, height);
        setMaxSize(Pane.USE_PREF_SIZE, Pane.USE_PREF_SIZE);
        
        xAxis = new NumberAxis(xLow, xHi, xTickUnit);
        xAxis.setSide(Side.BOTTOM);
        xAxis.setMinorTickVisible(false);
        xAxis.setPrefWidth(width);
        xAxis.setLayoutY(height / 2);
        xAxis.setLabel("X");
        xAxis.lookup(".axis-label").setStyle("-fx-label-padding: 0 0 0 1191;");

        yAxis = new NumberAxis(yLow, yHi, yTickUnit);
        yAxis.setSide(Side.LEFT);
        yAxis.setMinorTickVisible(false);
        yAxis.setPrefHeight(height);
        yAxis.layoutXProperty().bind(
                Bindings.subtract(
                    (width / 2) + 1,
                    yAxis.widthProperty()
                )
        );
        yAxis.setLabel("Z");
        yAxis.lookup(".axis-label").setStyle("-fx-label-padding: 0 0 0 791;");
        getChildren().setAll(xAxis, yAxis);
    }

    public NumberAxis getXAxis() {
        return xAxis;
    }

    public NumberAxis getYAxis() {
        return yAxis;
    }
}
