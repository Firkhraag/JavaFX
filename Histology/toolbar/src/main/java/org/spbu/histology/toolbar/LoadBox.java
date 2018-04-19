package org.spbu.histology.toolbar;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import javafx.beans.binding.Bindings;
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
import javafx.scene.paint.Color;
import org.openide.LifecycleManager;
import org.openide.util.Lookup;
import org.spbu.histology.model.Cell;
import org.spbu.histology.model.Histion;
import org.spbu.histology.model.HistionManager;
import org.spbu.histology.model.Names;
import org.spbu.histology.model.Part;
import org.spbu.histology.model.UpdateTree;
import org.spbu.histology.model.TetgenPoint;

public class LoadBox {
    
    public static void display() {
        
        HistionManager hm = Lookup.getDefault().lookup(HistionManager.class);
        if (hm == null) {
            LifecycleManager.getDefault().exit();
        }
        
        Stage window = new Stage();
        //window.initStyle(StageStyle.UTILITY);

        //window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Load");

        String tempdir = System.getProperty("user.dir");
        for (int i = 0; i < 3; i++)
            tempdir = tempdir.substring(0, tempdir.lastIndexOf('\\'));
        tempdir = tempdir + "\\util\\src\\main\\resources\\org\\spbu\\histology\\util\\Models\\";
        final String dir = tempdir;
        File directory = new File(dir);
        ObservableList<String> textFiles = FXCollections.observableArrayList();
        for (File file : directory.listFiles()) {
            if (file.getName().endsWith((".txt"))) {
              textFiles.add(file.getName());
            }
        }
        HBox hBox = new HBox();
        Label label = new Label("File name");
        final ComboBox comboBox = new ComboBox(textFiles);
        hBox.getChildren().addAll(label, comboBox);
        hBox.setPadding(new Insets(10, 10, 10, 10));
        hBox.setSpacing(20);
        Button closeButton = new Button("OK");
        closeButton.setOnAction(e -> {
            if (!comboBox.getSelectionModel().isEmpty()) {
                String selected = (String) comboBox.getSelectionModel().getSelectedItem();
                try {
                    if (hm.getHistionMap().isEmpty())
                        hm.addHistion(new Histion("Main histion",0,0,0,0,0));
                    else {
                        hm.getHistionMap().get(0).getItems().forEach(c -> {
                            String name = c.getName();
                            Names.removeCellName(name.substring(name.indexOf("<") + 1, name.lastIndexOf(">")));
                            hm.getHistionMap().get(0).deleteChild(c.getId());
                        });
                     }
                    Histion main = hm.getHistionMap().get(0);
                    BufferedReader br = new BufferedReader(new FileReader(dir + selected));
                    String line = br.readLine();
                    //System.out.println(line);
                    
                    //System.out.println(line.substring(0, line.indexOf(" ")));
                    //System.out.println(line);
                    main.setXRotate(Double.parseDouble(line.substring(0, line.indexOf(" "))));
                    //System.out.println(line);
                    line = line.substring(line.indexOf(" ") + 1, line.length());
                    //System.out.println(line);
                    main.setYRotate(Double.parseDouble(line.substring(0, line.indexOf(" "))));
                    //System.out.println(line);
                    line = line.substring(line.indexOf(" ") + 1, line.length());
                    //System.out.println(line);
                    main.setXCoordinate(Double.parseDouble(line.substring(0, line.indexOf(" "))));
                    //System.out.println(line);
                    line = line.substring(line.indexOf(" ") + 1, line.length());
                    //System.out.println(line);
                    main.setYCoordinate(Double.parseDouble(line.substring(0, line.indexOf(" "))));
                    //System.out.println(line);
                    line = line.substring(line.indexOf(" ") + 1, line.length());
                    //System.out.println(line);
                    main.setZCoordinate(Double.parseDouble(line));
                    line = br.readLine();
                    int cellNum = Integer.parseInt(line);
                    for (int i = 0; i < cellNum; i++) {
                        ObservableList<ArrayList<Integer>> facetData = FXCollections.observableArrayList();
                        Cell c = new Cell("Name", 0, 0, 0, 0, 0,
                            FXCollections.observableArrayList(), Color.BLUE, Color.LIGHTBLUE, 0, true);
                        double r,g,b;
                        line = br.readLine();
                        c.setName(line);
                            
                        line = br.readLine();
                        c.setXRotate(Double.parseDouble(line.substring(0, line.indexOf(" "))));
                        line = line.substring(line.indexOf(" ") + 1, line.length());
                        c.setYRotate(Double.parseDouble(line.substring(0, line.indexOf(" "))));
                        line = line.substring(line.indexOf(" ") + 1, line.length());
                        c.setXCoordinate(Double.parseDouble(line.substring(0, line.indexOf(" "))));
                        line = line.substring(line.indexOf(" ") + 1, line.length());
                        c.setYCoordinate(Double.parseDouble(line.substring(0, line.indexOf(" "))));
                        line = line.substring(line.indexOf(" ") + 1, line.length());
                        c.setZCoordinate(Double.parseDouble(line));
                            
                        line = br.readLine();
                        r = Double.parseDouble(line.substring(0, line.indexOf(" ")));
                        line = line.substring(line.indexOf(" ") + 1, line.length());
                        g = Double.parseDouble(line.substring(0, line.indexOf(" ")));
                        line = line.substring(line.indexOf(" ") + 1, line.length());
                        b = Double.parseDouble(line);
                        c.setDiffuseColor(Color.color(r, g, b));
                            
                        line = br.readLine();
                        r = Double.parseDouble(line.substring(0, line.indexOf(" ")));
                        line = line.substring(line.indexOf(" ") + 1, line.length());
                        g = Double.parseDouble(line.substring(0, line.indexOf(" ")));
                        line = line.substring(line.indexOf(" ") + 1, line.length());
                        b = Double.parseDouble(line);
                        c.setSpecularColor(Color.color(r, g, b));
                            
                        line = br.readLine();
                        c.setShow(Boolean.parseBoolean(line));
                        
                        line = br.readLine();
                        int partNum = Integer.parseInt(line);
                        for (int j = 0; j < partNum; j++) {
                            ObservableList<TetgenPoint> pointData = FXCollections.observableArrayList();
                            Part p = new Part("Part", FXCollections.observableArrayList());
                            line = br.readLine();
                            p.setName(line);
                            
                            line = br.readLine();
                            int pointNum = Integer.parseInt(line);
                            for (int q = 0; q < pointNum; q++) {
                                line = br.readLine();
                                r = Double.parseDouble(line.substring(0, line.indexOf(" ")));
                                //System.out.println(r);
                                line = line.substring(line.indexOf(" ") + 1, line.length());
                                g = Double.parseDouble(line.substring(0, line.indexOf(" ")));
                                //System.out.println(g);
                                line = line.substring(line.indexOf(" ") + 1, line.length());
                                b = Double.parseDouble(line);
                                pointData.add(new TetgenPoint(q + 1, r, g, b));
                            }
                            p.setPointData(pointData);
                            c.addChild(p);
                        }
                        line = br.readLine();
                        int facetNum = Integer.parseInt(line);
                        for (int j = 0; j < facetNum; j++) {
                            ArrayList<Integer> list = new ArrayList<>();
                            line = br.readLine();
                            while (line.contains(" ")) {
                                list.add(Integer.parseInt(line.substring(0, line.indexOf(" "))));
                                line = line.substring(line.indexOf(" ") + 1, line.length());
                                //System.out.println(line);
                            }
                            list.add(Integer.parseInt(line));
                            facetData.add(list);
                            //System.out.println(list);
                        }
                        c.setFacetData(facetData);
                        //System.out.println("Here");
                        main.addChild(c);
                        String name = c.getName();
                        Names.addCellName(name.substring(name.indexOf("<") + 1, name.lastIndexOf(">")));
                    }
                    /*while (line != null) {
                        if (line.equals("*")) {
                            Cell c = new Cell("Name", 0, 0, 0, 0, 0,
                                    FXCollections.observableArrayList(), Color.BLUE, Color.LIGHTBLUE, 0, true);
                            double r,g,b;
                            int num;
                            line = br.readLine();
                            c.setName(line);
                            
                            line = br.readLine();
                            c.setXRotate(Double.parseDouble(line.substring(0, line.indexOf(" "))));
                            line = line.substring(line.indexOf(" ") + 1, line.length());
                            c.setYRotate(Double.parseDouble(line.substring(0, line.indexOf(" "))));
                            line = line.substring(line.indexOf(" ") + 1, line.length());
                            c.setXCoordinate(Double.parseDouble(line.substring(0, line.indexOf(" "))));
                            line = line.substring(line.indexOf(" ") + 1, line.length());
                            c.setYCoordinate(Double.parseDouble(line.substring(0, line.indexOf(" "))));
                            line = line.substring(line.indexOf(" ") + 1, line.length());
                            c.setZCoordinate(Double.parseDouble(line.substring(0, line.indexOf(" "))));
                            
                            line = br.readLine();
                            r = Double.parseDouble(line.substring(0, line.indexOf(" ")));
                            line = line.substring(line.indexOf(" ") + 1, line.length());
                            g = Double.parseDouble(line.substring(0, line.indexOf(" ")));
                            line = line.substring(line.indexOf(" ") + 1, line.length());
                            b = Double.parseDouble(line.substring(0, line.indexOf(" ")));
                            c.setDiffuseColor(Color.color(r, g, b));
                            
                            line = br.readLine();
                            r = Double.parseDouble(line.substring(0, line.indexOf(" ")));
                            line = line.substring(line.indexOf(" ") + 1, line.length());
                            g = Double.parseDouble(line.substring(0, line.indexOf(" ")));
                            line = line.substring(line.indexOf(" ") + 1, line.length());
                            b = Double.parseDouble(line.substring(0, line.indexOf(" ")));
                            c.setSpecularColor(Color.color(r, g, b));
                            
                            line = br.readLine();
                            c.setShow(Boolean.parseBoolean(line));
                            
                            line = br.readLine();
                            while (!line.equals("Facets")) {
                                if (line.equals("^")) {
                                    ObservableList<TetgenPoint> pointData = FXCollections.observableArrayList();
                                    Part p = new Part("Part", FXCollections.observableArrayList());
                                    line = br.readLine();
                                    p.setName(line);
                                    
                                    line = br.readLine();
                                    num = 1;
                                    while ((!line.equals("^")) && (!line.equals("Facets"))) {
                                        r = Double.parseDouble(line.substring(0, line.indexOf(" ")));
                                        line = line.substring(line.indexOf(" ") + 1, line.length());
                                        g = Double.parseDouble(line.substring(0, line.indexOf(" ")));
                                        line = line.substring(line.indexOf(" ") + 1, line.length());
                                        b = Double.parseDouble(line.substring(0, line.indexOf(" ")));
                                        pointData.add(new TetgenPoint(num, r, g, b));
                                    }
                                    p.setPointData(pointData);
                                    c.addChild(p);
                                }
                            }
                            while ((!line.equals("*")) && (line != null)) {
                                line = br.readLine();
                                System.out.println(line);
                            //while (!line.equals("*")) {
                                
                            }
                        }
                        //line = br.readLine();
                    }*/
                    br.close();
                    hm.getHistionMap().get(0).getItems().forEach(c -> {
                        Cell newCell = new Cell(c.getId(), c);
                        c.getItems().forEach(p -> {
                            newCell.addChild(p);
                        });
                        hm.getHistionMap().get(0).addChild(newCell);
                    });
                    UpdateTree.setShouldBeUpdated(true);
                } catch (Exception ex) {
                    System.out.println("error");
                }
                window.close();
            }
            //if (comboBox.g)
            //System.out.println("");
            //window.close();
        });
        //closeButton.disableProperty().bind(Bindings.isEmpty(comboBox.selectionModelProperty()));

        VBox layout = new VBox(10);
        layout.getChildren().addAll(hBox, closeButton);
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout, 350, 150);
        window.setScene(scene);
        window.showAndWait();
    }

}
