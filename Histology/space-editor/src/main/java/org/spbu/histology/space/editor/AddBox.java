package org.spbu.histology.space.editor;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.geometry.*;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import org.openide.LifecycleManager;
import org.openide.util.Lookup;
import org.spbu.histology.model.AlertBox;
import org.spbu.histology.model.Cell;
import org.spbu.histology.model.HistionManager;
import org.spbu.histology.model.Names;

public class AddBox {
    
    public static void display(String title, String message, Integer histionId) {
        
        HistionManager hm = Lookup.getDefault().lookup(HistionManager.class);
        if (hm == null) {
            LifecycleManager.getDefault().exit();
        }
        
        Stage window = new Stage();
        window.setTitle(title);

        
        HBox hBox = new HBox();
        Label label = new Label();
        label.setText(message);
        TextField field = new TextField();
        hBox.getChildren().addAll(label, field);
        hBox.setPadding(new Insets(10, 10, 10, 10));
        hBox.setSpacing(20);
        Button closeButton = new Button("OK");
        closeButton.setOnAction(e -> {
            if (!Names.containsCellName(field.getText())) {
                hm.getHistionMap().get(histionId).addChild(new Cell("Cell <" + field.getText() + ">",
                        0, 0, 0, 0, 0, FXCollections.observableArrayList(),
                        Color.RED, Color.RED, histionId, false));
                Names.addCellName(field.getText());
                window.close();
            } else
                AlertBox.display("Error", "This name is already used");
        });
        closeButton.disableProperty().bind(Bindings.isEmpty(field.textProperty()));

        VBox layout = new VBox(10);
        layout.getChildren().addAll(hBox, closeButton);
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout, 350, 150);
        window.setScene(scene);
        window.showAndWait();
    }

}
