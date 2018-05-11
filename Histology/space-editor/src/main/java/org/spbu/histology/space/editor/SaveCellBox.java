package org.spbu.histology.space.editor;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.geometry.*;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import org.openide.LifecycleManager;
import org.openide.util.Lookup;
import org.spbu.histology.model.Cell;
import org.spbu.histology.model.HistionManager;

public class SaveCellBox {
    
    public static void display(Integer id) {
        
        HistionManager hm = Lookup.getDefault().lookup(HistionManager.class);
        if (hm == null) {
            LifecycleManager.getDefault().exit();
        }
        
        Stage window = new Stage();
        //window.initStyle(StageStyle.UTILITY);
        window.setTitle("Save cell");

        
        HBox hBox = new HBox();
        Label label = new Label("File name");
        TextField field = new TextField();
        
        //final ComboBox comboBox = new ComboBox(textFiles);
        
        final ToggleGroup group = new ToggleGroup();

        RadioButton rb1 = new RadioButton("Save the whole cell");
        rb1.setToggleGroup(group);
        rb1.setSelected(true);

        RadioButton rb2 = new RadioButton("Save the part between two layers");
        rb2.setToggleGroup(group);
        
        ObservableList<String> layers1 = FXCollections.observableArrayList();
        ObservableList<String> layers2 = FXCollections.observableArrayList();
        
        hm.getHistionMap().get(0).getItemMap().get(id).getItems().forEach(p -> {
            String y = String.valueOf(p.getPointData().get(0).getY());
            if (!layers1.contains(y))
                layers1.add(y);
            if (!layers2.contains(y))
                layers2.add(y);
        });
        
        FXCollections.sort(layers1);
        FXCollections.sort(layers2);
        
        HBox hBox2 = new HBox();
        HBox hBox3 = new HBox();
        Label firstLayerLabel = new Label("First layer");
        ComboBox<String> firstLayerComboBox = new ComboBox(layers1);
        firstLayerComboBox.setDisable(true);
        Label secondLayerLabel = new Label("Second layer");
        ComboBox<String> secondLayerComboBox = new ComboBox(layers2);
        secondLayerComboBox.setDisable(true);
        
        
        
        firstLayerComboBox.valueProperty().addListener((o, ov, nv) -> {
            layers2.remove(nv);
            if (ov != null) {
                layers2.add(ov);
                FXCollections.sort(layers2);
            }
        });
        secondLayerComboBox.valueProperty().addListener((o, ov, nv) -> {
            layers1.remove(nv);
            if (ov != null) {
                layers1.add(ov);
                FXCollections.sort(layers1);
            }
        });
        
        hBox2.getChildren().addAll(firstLayerLabel, firstLayerComboBox);
        hBox2.setPadding(new Insets(10, 10, 10, 10));
        hBox2.setSpacing(35);
        hBox3.getChildren().addAll(secondLayerLabel, secondLayerComboBox);
        hBox3.setPadding(new Insets(10, 10, 10, 10));
        hBox3.setSpacing(20);
        
        rb1.selectedProperty().addListener((o, ov, nv) -> {
            if (nv) {
                firstLayerComboBox.setDisable(true);
                secondLayerComboBox.setDisable(true);
            }
        });
        rb2.selectedProperty().addListener((o, ov, nv) -> {
            if (nv) {
                firstLayerComboBox.setDisable(false);
                secondLayerComboBox.setDisable(false);
            }
        });
        
        //hBox.getChildren().addAll(label, comboBox);
        
        hBox.getChildren().addAll(label, field);
        hBox.setPadding(new Insets(10, 10, 10, 10));
        hBox.setSpacing(20);
        Button closeButton = new Button("OK");
        closeButton.setOnAction(e -> {
            if (rb2.isSelected()) {
                if ((firstLayerComboBox.getSelectionModel().getSelectedItem() == null)
                    || (secondLayerComboBox.getSelectionModel().getSelectedItem() == null))
                    return;
            }
            try {
                String dir = System.getProperty("user.dir");
                for (int i = 0; i < 3; i++)
                    dir = dir.substring(0, dir.lastIndexOf('\\'));
                dir = dir + "\\util\\src\\main\\resources\\org\\spbu\\histology\\util\\Cells\\";
                BufferedWriter writer = new BufferedWriter(new FileWriter(dir
                                + field.getText() + ".txt"));
                
                writer.write(0 + " " +
                    0 + " " +
                    hm.getHistionMap().get(0).getXCoordinate() + " " +
                    hm.getHistionMap().get(0).getYCoordinate() + " " +
                    hm.getHistionMap().get(0).getZCoordinate());
                writer.newLine();
                        
                //writer.write(hm.getHistionMap().get(0).getItems().size() + "");
                writer.write("1");
                writer.newLine();
                Cell c = hm.getHistionMap().get(0).getItemMap().get(id);
                ArrayList<Integer> pointIds = new ArrayList<>();
                try {
                    writer.write(c.getName());
                    writer.newLine();
                    writer.write(c.getXRotate() + " " + c.getYRotate() + " "
                            + c.getXCoordinate() + " " + c.getYCoordinate() + " "
                            + c.getZCoordinate());
                    writer.newLine();
                    writer.write(c.getDiffuseColor().getRed() + " "
                            + c.getDiffuseColor().getGreen() + " "
                            + c.getDiffuseColor().getBlue());
                    writer.newLine();
                    writer.write(c.getSpecularColor().getRed() + " "
                            + c.getSpecularColor().getGreen() + " "
                            + c.getSpecularColor().getBlue());
                    writer.newLine();
                    writer.write(c.getShow() + "");
                    writer.newLine();
                    
                    IntegerProperty num = new SimpleIntegerProperty(1);
                    ArrayList<Integer> partIds = new ArrayList<>();
                    if (rb1.isSelected()) {
                        writer.write(c.getItems().size() + "");
                    } else {
                        if (rb2.isSelected()) {
                            c.getItems().forEach(p -> {
                                double y1 = Double.parseDouble(firstLayerComboBox.getSelectionModel().getSelectedItem());
                                double y2 = Double.parseDouble(secondLayerComboBox.getSelectionModel().getSelectedItem());
                                double y = p.getPointData().get(0).getY();
                                if ((((y < y1) || (Math.abs(y - y1) < 0.000001))
                                        && ((y > y2) || (Math.abs(y - y2) < 0.000001)))
                                        || (((y < y2) || (Math.abs(y - y2) < 0.000001))
                                        && ((y > y1) || (Math.abs(y - y1) < 0.000001)))) {
                                    partIds.add(num.get());
                                }
                                num.set(num.get() + 1);
                            });
                        }
                        writer.write(partIds.size() + "");
                    }
                    writer.newLine();
                    
                    IntegerProperty num2 = new SimpleIntegerProperty(1);
                    num.set(1);
                    //IntegerProperty num = new SimpleIntegerProperty(1);
                    c.getItems().forEach(p -> {
                        //if (p.getPointData().get(0).getY())
                        if (rb1.isSelected()) {
                            try {
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
                            } catch (Exception ex) {

                            }
                        } else {
                            double y1 = Double.parseDouble(firstLayerComboBox.getSelectionModel().getSelectedItem());
                            double y2 = Double.parseDouble(secondLayerComboBox.getSelectionModel().getSelectedItem());
                            double y = p.getPointData().get(0).getY();
                            if (partIds.contains(num2.get())) {
                                try {
                                    writer.write(p.getName());
                                    writer.newLine();
                                    writer.write(p.getPointData().size() + "");
                                    writer.newLine();
                                    for (int i = 0; i < p.getPointData().size(); i++) {
                                        writer.write(p.getPointData().get(i).getX() + " "
                                                + p.getPointData().get(i).getY() + " "
                                                + p.getPointData().get(i).getZ());
                                        writer.newLine();
                                        pointIds.add(num.get());
                                        num.set(num.get() + 1);
                                    }
                                } catch (Exception ex) {

                                }
                            } else {
                                num.set(num.get() + p.getPointData().size());
                            }
                            num2.set(num2.get() + 1);
                        }
                    });

                    if (rb2.isSelected()) {
                        ArrayList<Integer> facetsThatShouldBeIncluded = new ArrayList<>();
                        num.set(0);
                        c.getFacetData().forEach(list -> {
                            boolean should = true;
                            for (int i = 0; i < list.size(); i++) {
                                if (!pointIds.contains(list.get(i))) {
                                    should = false;
                                    break;
                                }
                            }
                            if (should) {
                                facetsThatShouldBeIncluded.add(num.get());
                            }
                            num.set(num.get() + 1);
                        });
                        writer.write(facetsThatShouldBeIncluded.size() + "");
                        writer.newLine();

                        num.set(0);
                        c.getFacetData().forEach(list -> {
                            if (facetsThatShouldBeIncluded.contains(num.get())) {
                                try {
                                    for (int i = 0; i < list.size(); i++) {
                                        for (int k = 0; k < pointIds.size(); k++) {
                                            if (pointIds.get(k) == list.get(i)) {
                                                if (i == list.size() - 1) {
                                                    writer.write((k + 1) + "");
                                                } else {
                                                    writer.write((k + 1) + " ");
                                                }
                                            }
                                        }
                                        /*if (i == list.size() - 1) {
                                            writer.write(list.get(i) + "");
                                        } else {
                                            writer.write(list.get(i) + " ");
                                        }*/
                                    }
                                    writer.newLine();
                                } catch (Exception ex) {

                                }
                            }
                            num.set(num.get() + 1);
                        });

                    } else {

                        writer.write(c.getFacetData().size() + "");
                        writer.newLine();

                        c.getFacetData().forEach(list -> {
                            try {

                                for (int i = 0; i < list.size(); i++) {
                                    if (i == list.size() - 1) {
                                        writer.write(list.get(i) + "");
                                    } else {
                                        writer.write(list.get(i) + " ");
                                    }
                                }
                                writer.newLine();
                            } catch (Exception ex) {

                            }
                        });
                    }
                } catch (Exception ex) {

                }
                writer.close();
            } catch (Exception ex) {
                
            }
            window.close();
        });
        closeButton.disableProperty().bind(Bindings.isEmpty(field.textProperty()));

        VBox layout = new VBox(10);
        layout.getChildren().addAll(hBox, rb1, rb2, hBox2, hBox3, closeButton);
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout, 350, 300);  //150
        window.setScene(scene);
        window.showAndWait();
    }

}
