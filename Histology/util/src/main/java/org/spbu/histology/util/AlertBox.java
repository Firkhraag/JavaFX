package org.spbu.histology.util;

import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.geometry.*;

public class AlertBox {

    public static void display(String title, String message) {
        Stage window = new Stage();
        window.initStyle(StageStyle.UTILITY);

        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle(title);

        Label label = new Label();
        label.setText(message);
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
