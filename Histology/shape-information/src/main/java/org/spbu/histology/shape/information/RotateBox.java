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
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import org.openide.LifecycleManager;
import org.openide.util.Lookup;
import org.spbu.histology.model.HistionManager;

public class RotateBox {
    
    public static void display(DoubleProperty value, DoubleProperty k) {
        
        HistionManager hm = Lookup.getDefault().lookup(HistionManager.class);
        if (hm == null) {
            LifecycleManager.getDefault().exit();
        }
        
        Stage window = new Stage();
        //window.initStyle(StageStyle.UTILITY);
        //window.setTitle("Change");
        window.setTitle("Rotate");

        
        HBox hBox = new HBox();
        
        HBox hBox2 = new HBox();
        //HBox hBox3 = new HBox();
        
        Label label = new Label("Change");
        TextField field = new TextField();
        field.setDisable(true);
        field.setPrefWidth(100);
        
        Label labelK = new Label("k");
        TextField fieldK = new TextField();
        fieldK.setDisable(true);
        fieldK.setPrefWidth(100);
        fieldK.setDisable(true);
        
        /*Label labelB = new Label("b");
        TextField fieldB = new TextField();
        fieldB.setDisable(true);
        fieldB.setPrefWidth(100);
        fieldB.setDisable(true);*/
        
        hBox.getChildren().addAll(label, field);
        hBox.setPadding(new Insets(10, 10, 10, 10));
        hBox.setSpacing(20);
        
        hBox2.getChildren().addAll(labelK, fieldK);
        hBox2.setPadding(new Insets(10, 10, 10, 10));
        hBox2.setSpacing(20);
        
        /*hBox3.getChildren().addAll(labelB, fieldB);
        hBox3.setPadding(new Insets(10, 10, 10, 10));
        hBox3.setSpacing(20);*/
        
        final ToggleGroup group = new ToggleGroup();

        RadioButton rb1 = new RadioButton("Usual rotate");
        rb1.setToggleGroup(group);
        rb1.setSelected(true);

        RadioButton rb2 = new RadioButton("Rotate horizontal line till y = kx");
        rb2.setToggleGroup(group);
        
        rb1.selectedProperty().addListener((o, ov, nv) -> {
            if (nv) {
                field.setDisable(false);
            } else {
                field.setDisable(true);
            }
        });
        
        rb2.selectedProperty().addListener((o, ov, nv) -> {
            if (nv) {
                fieldK.setDisable(false);
                //fieldB.setDisable(false);
            } else {
                fieldK.setDisable(true);
                //fieldB.setDisable(true);
            }
        });
        
        Button closeButton = new Button("OK");
        closeButton.setOnAction(e -> {
            try {
                if (rb1.isSelected()) {
                    double v = Double.parseDouble(field.getText());
                    value.set(v);
                } else if (rb2.isSelected()) {
                    double vk = Double.parseDouble(fieldK.getText());
                    //double vb = Double.parseDouble(fieldB.getText());
                    k.set(vk);
                    //b.set(vb);
                }
                window.close();
            } catch (Exception ex) {
                
            }
        });
        //closeButton.disableProperty().bind(Bindings.isEmpty(field.textProperty()));

        VBox layout = new VBox(10);
        layout.getChildren().addAll(rb1, hBox, rb2, hBox2, closeButton);
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout, 300, 350); //220
        window.setScene(scene);
        window.showAndWait();
    }

}
