package org.spbu.histology.shape.information;

import java.io.BufferedWriter;
import java.io.FileWriter;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.geometry.*;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import org.openide.LifecycleManager;
import org.openide.util.Lookup;
import org.spbu.histology.model.HistionManager;

public class ChangePoints {
    
    public static void display(DoubleProperty value) {
        
        HistionManager hm = Lookup.getDefault().lookup(HistionManager.class);
        if (hm == null) {
            LifecycleManager.getDefault().exit();
        }
        
        Stage window = new Stage();
        window.initStyle(StageStyle.UTILITY);
        window.setTitle("Change");

        
        HBox hBox = new HBox();
        Label label = new Label("Change");
        TextField field = new TextField();
        field.setPrefWidth(100);
        hBox.getChildren().addAll(label, field);
        hBox.setPadding(new Insets(10, 10, 10, 10));
        hBox.setSpacing(20);
        Button closeButton = new Button("OK");
        closeButton.setOnAction(e -> {
            try {
                double v = Double.parseDouble(field.getText());
                value.set(v);
                window.close();
            } catch (Exception ex) {
                
            }
        });
        closeButton.disableProperty().bind(Bindings.isEmpty(field.textProperty()));

        VBox layout = new VBox(10);
        layout.getChildren().addAll(hBox, closeButton);
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout, 220, 150);
        window.setScene(scene);
        window.showAndWait();
    }

}
