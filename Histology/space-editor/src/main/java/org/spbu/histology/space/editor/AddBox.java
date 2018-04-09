package org.spbu.histology.space.editor;

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
import org.spbu.histology.model.Cell;
import org.spbu.histology.model.Histion;
import org.spbu.histology.model.HistionManager;

public class AddBox {
    
    public static void display(String title, String message, Integer histionId) {
        
        HistionManager hm = Lookup.getDefault().lookup(HistionManager.class);
        if (hm == null) {
            LifecycleManager.getDefault().exit();
        }
        
        Stage window = new Stage();
        //window.initStyle(StageStyle.UTILITY);

        //window.initModality(Modality.APPLICATION_MODAL);
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
            //hm.addHistion(new Histion("Histion <" + field.getText() + ">", 0, 0, 0, 0, 0));
            if (histionId == -1)
                hm.addHistion(new Histion("Histion <" + field.getText() + ">", 0, 0, 0, 0, 0, FXCollections.emptyObservableMap()));
            else {
                hm.getHistionMap().get(histionId).addChild(new Cell("Cell <" + field.getText() + ">",
                        0, 0, 0, 0, 0, FXCollections.observableArrayList(), 3,
                        Color.RED, Color.RED, -1, histionId, false, FXCollections.emptyObservableMap()));
            }
            window.close();
        });

        VBox layout = new VBox(10);
        layout.getChildren().addAll(hBox, closeButton);
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout, 350, 150);
        window.setScene(scene);
        window.showAndWait();
    }

}
