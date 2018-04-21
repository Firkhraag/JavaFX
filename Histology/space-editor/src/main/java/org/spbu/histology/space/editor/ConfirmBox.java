package org.spbu.histology.space.editor;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.openide.LifecycleManager;
import org.openide.util.Lookup;
import org.spbu.histology.model.HideCells;
import org.spbu.histology.model.HistionManager;
import org.spbu.histology.model.Names;

public class ConfirmBox {

    public static void display(String title, String message, Integer histionId, Integer cellId, Integer partId) {
        
        HistionManager hm = Lookup.getDefault().lookup(HistionManager.class);
        if (hm == null) {
            LifecycleManager.getDefault().exit();
        }
        
        Stage window = new Stage();
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
                hm.getHistionMap().get(histionId).getItemMap().get(cellId).setFacetData(FXCollections.observableArrayList());
                hm.getHistionMap().get(histionId).getItemMap().get(cellId).setShow(false);
            } else if (cellId != -1) {
                String name = hm.getHistionMap().get(histionId).getItemMap().get(cellId).getName();
                name = name.substring(name.indexOf("<") + 1, name.lastIndexOf(">"));
                Names.removeCellName(name);
                HideCells.removeCellNameToHide(name);
                hm.getHistionMap().get(histionId).deleteChild(cellId);
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
