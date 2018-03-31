package org.spbu.histology.space.editor;

import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.geometry.*;
import org.openide.LifecycleManager;
import org.openide.util.Lookup;
import org.spbu.histology.model.HistionManager;
import org.spbu.histology.model.ShapeManager;

public class ConfirmBox {

    public static void display(String title, String message, long histionId, long cellId, long partId) {
        
        HistionManager hm = Lookup.getDefault().lookup(HistionManager.class);
        if (hm == null) {
            LifecycleManager.getDefault().exit();
        }
        
        ShapeManager sm = Lookup.getDefault().lookup(ShapeManager.class);
        if (sm == null) {
            LifecycleManager.getDefault().exit();
        }
        
        Stage window = new Stage();
        window.initStyle(StageStyle.UTILITY);

        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle(title);

        Label label = new Label();
        label.setText(message);
        HBox hBox = new HBox();
        hBox.setPadding(new Insets(10, 10, 10, 150));
        hBox.setSpacing(20);
        
        Button confirmButton = new Button("Yes");
        confirmButton.setOnAction(e -> {
            if (partId != -1) {
                hm.getHistionMap().get(histionId).getItemMap().get(cellId).deleteChild(partId);
                sm.deleteShape(partId);
            } else if (cellId != -1) {
                hm.getHistionMap().get(histionId).getItemMap().get(cellId).getItems().forEach(p -> 
                        sm.deleteShape(p.getId()));
                hm.getHistionMap().get(histionId).deleteChild(cellId);
            } else {
                hm.getHistionMap().get(histionId).getItems().forEach(c -> {
                    c.getItems().forEach(p -> sm.deleteShape(p.getId()));
                });
                hm.deleteHistion(histionId);
            }
            window.close();
        });
        
        Button closeButton = new Button("Cancel");
        closeButton.setOnAction(e -> window.close());
        
        hBox.getChildren().addAll(confirmButton, closeButton);

        VBox layout = new VBox(10);
        layout.getChildren().addAll(label, hBox);
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout, 400, 125);
        window.setScene(scene);
        window.showAndWait();
    }

}
