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

public class SaveBox {
    
    public static void display() {
        
        HistionManager hm = Lookup.getDefault().lookup(HistionManager.class);
        if (hm == null) {
            LifecycleManager.getDefault().exit();
        }
        
        Stage window = new Stage();
        //window.initStyle(StageStyle.UTILITY);
        window.setTitle("Save histion");

        
        HBox hBox = new HBox();
        Label label = new Label("File name");
        TextField field = new TextField();
        hBox.getChildren().addAll(label, field);
        hBox.setPadding(new Insets(10, 10, 10, 10));
        hBox.setSpacing(20);
        Button closeButton = new Button("OK");
        closeButton.setOnAction(e -> {
            if (hm.getShapeMap().isEmpty())
                return;
            try {
                String dir = System.getProperty("user.dir");
                for (int i = 0; i < 3; i++)
                    dir = dir.substring(0, dir.lastIndexOf('\\'));
                dir = dir + "\\util\\src\\main\\resources\\org\\spbu\\histology\\util\\Histions\\";
                BufferedWriter writer = new BufferedWriter(new FileWriter(dir
                                + field.getText() + ".txt"));
                
                writer.write(0 + " " +
                    0 + " " +
                    hm.getHistionMap().get(0).getXCoordinate() + " " +
                    hm.getHistionMap().get(0).getYCoordinate() + " " +
                    hm.getHistionMap().get(0).getZCoordinate());
                /*writer.write(hm.getHistionMap().get(0).getXRotate() + " " +
                    hm.getHistionMap().get(0).getYRotate() + " " +
                    hm.getHistionMap().get(0).getXCoordinate() + " " +
                    hm.getHistionMap().get(0).getYCoordinate() + " " +
                    hm.getHistionMap().get(0).getZCoordinate());*/
                writer.newLine();
                        
                writer.write(hm.getHistionMap().get(0).getItems().size() + "");
                writer.newLine();
                
                hm.getHistionMap().get(0).getItems().forEach(c -> {
                    try {
                        writer.write(c.getName());
                        writer.newLine();
                        writer.write(c.getXRotate() + " " + c.getYRotate() + " " +
                                c.getXCoordinate() + " " + c.getYCoordinate() + " " +
                                c.getZCoordinate());
                        writer.newLine();
                        writer.write(c.getDiffuseColor().getRed() + " " +
                                c.getDiffuseColor().getGreen() + " " +
                                c.getDiffuseColor().getBlue());
                        writer.newLine();
                        writer.write(c.getSpecularColor().getRed() + " " +
                                c.getSpecularColor().getGreen() + " " +
                                c.getSpecularColor().getBlue());
                        writer.newLine();
                        writer.write(c.getShow() + "");
                        writer.newLine();
                        writer.write(c.getItems().size() + "");
                        writer.newLine();
                        
                        c.getItems().forEach(p -> {
                            try {
                                writer.write(p.getName());
                                writer.newLine();
                                writer.write(p.getPointData().size() + "");
                                writer.newLine();
                                for(int i = 0; i < p.getPointData().size(); i++) {
                                    writer.write(p.getPointData().get(i).getX() + " " +
                                            p.getPointData().get(i).getY() + " " +
                                            p.getPointData().get(i).getZ());
                                    writer.newLine();
                                }
                            } catch (Exception ex) {

                            }
                        });
                        
                        writer.write(c.getFacetData().size() + "");
                        writer.newLine();
                        
                        c.getFacetData().forEach(list -> {
                            try {
                                for(int i = 0; i < list.size(); i++) {
                                    if (i == list.size() - 1)
                                        writer.write(list.get(i) + "");
                                    else
                                        writer.write(list.get(i) + " ");
                                }
                                writer.newLine();
                            } catch (Exception ex) {
                
                            }
                        });
                    } catch (Exception ex) {
                
                    }
                });
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
