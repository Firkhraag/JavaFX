package org.spbu.histology.shape.information;

import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.geometry.*;

public class CentralPointBox {

    public static void display(double x, double z) {
        Stage window = new Stage();
        window.initStyle(StageStyle.UTILITY);

        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Central point");

        Label xLabel = new Label("X:");
        TextField xField = new TextField(String.valueOf(x));
        //xField.setEditable(false);
        
        Label zLabel = new Label("Z:");
        TextField zField = new TextField(String.valueOf(z));
        //xField.setEditable(false);
        
        HBox hBox = new HBox();
        HBox hBox2 = new HBox();
        
        hBox.getChildren().addAll(xLabel, xField);
        hBox.setPadding(new Insets(10, 10, 10, 10));
        hBox.setSpacing(20);
        
        hBox2.getChildren().addAll(zLabel, zField);
        hBox2.setPadding(new Insets(10, 10, 10, 10));
        hBox2.setSpacing(20);
        
        
        Button closeButton = new Button("OK");
        closeButton.setPadding(new Insets(0,20,0,20));
        closeButton.setOnAction(e -> window.close());

        VBox layout = new VBox(10);
        layout.getChildren().addAll(hBox, hBox2, closeButton);
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout, 280, 200);
        window.setScene(scene);
        window.showAndWait();
    }

}
