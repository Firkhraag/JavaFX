package org.spbu.histology.space.editor;

import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.geometry.*;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import org.openide.LifecycleManager;
import org.openide.util.Lookup;
import org.spbu.histology.model.Cell;
import org.spbu.histology.model.Histion;
import org.spbu.histology.model.HistionManager;

public class AddBox {
    
    public static void display(String title, String message, long hierarchyId) {
        
        HistionManager hm = Lookup.getDefault().lookup(HistionManager.class);
        if (hm == null) {
            LifecycleManager.getDefault().exit();
        }
        
        Stage window = new Stage();
        window.initStyle(StageStyle.UTILITY);

        window.initModality(Modality.APPLICATION_MODAL);
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
            if (hierarchyId == -1)
                hm.addHistion(new Histion("Histion <" + field.getText() + ">", 0, 0, 0, 0, 0));
            else {
                hm.getHistionMap().get(hierarchyId).addChild(new Cell("Cell <" + field.getText() + ">", 0, 0, 0, 0, 0));
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
