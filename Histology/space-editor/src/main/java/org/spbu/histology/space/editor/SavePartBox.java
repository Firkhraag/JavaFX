package org.spbu.histology.space.editor;

import java.io.BufferedWriter;
import java.io.FileWriter;
import javafx.beans.binding.Bindings;
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
import org.spbu.histology.model.Part;

public class SavePartBox {
    
    public static void display(Integer cellId, Integer partId) {
        
        HistionManager hm = Lookup.getDefault().lookup(HistionManager.class);
        if (hm == null) {
            LifecycleManager.getDefault().exit();
        }
        
        Stage window = new Stage();
        //window.initStyle(StageStyle.UTILITY);
        window.setTitle("Save part");
        
        Part p = hm.getHistionMap().get(0).getItemMap().get(cellId).getItemMap().get(partId);

        //System.out.println("True");
        
        HBox hBox = new HBox();
        Label label = new Label("File name");
        TextField field = new TextField();
        hBox.getChildren().addAll(label, field);
        hBox.setPadding(new Insets(10, 10, 10, 10));
        hBox.setSpacing(20);
        Button closeButton = new Button("OK");
        closeButton.setOnAction(e -> {
            System.out.println("dfgddgdddgd");
            //if (hm.getShapeMap().isEmpty())
            if (hm.getAllHistions().isEmpty())
                return;
            try {
                //System.out.println("cliked");
                String dir = System.getProperty("user.dir");
                for (int i = 0; i < 3; i++) {
                    dir = dir.substring(0, dir.lastIndexOf('\\'));
                }
                dir = dir + "\\util\\src\\main\\resources\\org\\spbu\\histology\\util\\Parts\\";
                BufferedWriter writer = new BufferedWriter(new FileWriter(dir
                        + field.getText() + ".txt"));

                writer.write(p.getName());
                writer.newLine();
                writer.write(p.getPointData().size() + "");
                writer.newLine();
                for (int i = 0; i < p.getPointData().size(); i++) {
                    writer.write(p.getPointData().get(i).getX() + " "
                            + p.getPointData().get(i).getY() + " "
                            + p.getPointData().get(i).getZ());
                    writer.newLine();
                }
                writer.close();
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
