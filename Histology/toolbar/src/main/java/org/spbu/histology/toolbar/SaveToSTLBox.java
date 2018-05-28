package org.spbu.histology.toolbar;

import java.util.ArrayList;
import javafx.beans.binding.Bindings;
import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.geometry.*;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.shape.Mesh;
import org.openide.LifecycleManager;
import org.openide.util.Lookup;
import org.spbu.histology.model.HistionManager;
import org.spbu.histology.util.MeshUtils;

public class SaveToSTLBox {
    
    public static void display() {
        
        HistionManager hm = Lookup.getDefault().lookup(HistionManager.class);
        if (hm == null) {
            LifecycleManager.getDefault().exit();
        }
        
        Stage window = new Stage();
        window.setTitle("To STL");

        
        HBox hBox = new HBox();
        Label label = new Label("STL file name");
        TextField field = new TextField();
        hBox.getChildren().addAll(label, field);
        hBox.setPadding(new Insets(10, 10, 10, 10));
        hBox.setSpacing(20);
        Button closeButton = new Button("OK");
        closeButton.setOnAction(e -> {
            ArrayList<Mesh> meshList = new ArrayList<>();
            hm.getShapeMap().forEach((i, m) -> {
                meshList.add(m.getMesh());
            });

            if (hm.getShapeMap().isEmpty()) {
                return;
            }

            String dir = System.getProperty("user.dir");
            for (int i = 0; i < 3; i++) {
                dir = dir.substring(0, dir.lastIndexOf('\\'));
            }
            dir = dir + "\\util\\src\\main\\resources\\org\\spbu\\histology\\util\\3D Printer\\";

            try {
                MeshUtils.mesh2STL(dir + field.getText() + ".stl", meshList);
            } catch (Exception ex) {

            }
            window.close();
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
