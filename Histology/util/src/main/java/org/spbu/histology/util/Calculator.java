package org.spbu.histology.util;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Calculator {

    public static void display() {
        Stage window = new Stage();
        window.setTitle("Сумматор");

        VBox vBox = new VBox(10);
        vBox.setAlignment(Pos.CENTER);
        //vBox.setSpacing(10);
        Label label1 = new Label("Первое число");
        TextField field1 = new TextField();
        field1.setPrefWidth(200);
        field1.setMaxWidth(200);
        Label label2 = new Label("Второе число");
        TextField field2 = new TextField();
        field2.setPrefWidth(200);
        field2.setMaxWidth(200);
        Button plusButton = new Button("+");
        plusButton.setPadding(new Insets(5, 30, 5, 30));
        Button minusButton = new Button("-");
        minusButton.setPadding(new Insets(5, 30, 5, 30));
        HBox hBox = new HBox(10);
        hBox.setAlignment(Pos.CENTER);
        hBox.getChildren().addAll(plusButton, minusButton);
        Label labelResult = new Label("Результат");
        TextField fieldResult = new TextField();
        fieldResult.setPrefWidth(200);
        fieldResult.setMaxWidth(200);
        vBox.getChildren().addAll(label1, field1, label2, field2, hBox, labelResult, fieldResult);
        plusButton.setOnAction(e -> {
            fieldResult.setText(String.valueOf(Double.parseDouble(field1.getText())
                    + Double.parseDouble(field2.getText())));
        });
        minusButton.setOnAction(e -> {
            fieldResult.setText(String.valueOf(Double.parseDouble(field1.getText())
                    - Double.parseDouble(field2.getText())));
        });
        /*closeButton.setPadding(new Insets(0, 20, 0, 20));
        closeButton.setOnAction(e -> window.close());*/

 /*VBox layout = new VBox(10);
        layout.getChildren().addAll(vBox, hBox);
        layout.setAlignment(Pos.CENTER);*/
        Scene scene = new Scene(vBox, 300, 300);
        window.setScene(scene);
        window.showAndWait();
    }

}
