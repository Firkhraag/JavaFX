package org.spbu.histology.util;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Manual {
    
    public static void display() {
        Stage window = new Stage();
        window.setTitle("Инструкция");

        Label label = new Label();
        label.setText("Under construction...");
        Button closeButton = new Button("OK");
        closeButton.setPadding(new Insets(0, 20, 0, 20));
        closeButton.setOnAction(e -> window.close());

        VBox layout = new VBox(10);
        layout.getChildren().addAll(label, closeButton);
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout, 450, 150);
        window.setScene(scene);
        window.showAndWait();
    }
    
}
