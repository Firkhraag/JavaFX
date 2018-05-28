package org.spbu.histology.space.editor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.geometry.*;
import javafx.scene.control.Label;

import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.paint.Color;
import org.openide.LifecycleManager;
import org.openide.util.Lookup;
import org.spbu.histology.model.Cell;
import org.spbu.histology.model.Histion;
import org.spbu.histology.model.HistionManager;
import org.spbu.histology.model.Line;
import org.spbu.histology.model.LineEquations;
import org.spbu.histology.model.Names;
import org.spbu.histology.model.Part;
import org.spbu.histology.model.TetgenPoint;
import org.spbu.histology.model.TwoIntegers;

public class LoadCellBox {

    public static void display() {

        HistionManager hm = Lookup.getDefault().lookup(HistionManager.class);
        if (hm == null) {
            LifecycleManager.getDefault().exit();
        }

        Stage window = new Stage();
        window.setTitle("Load cell");

        String tempdir = System.getProperty("user.dir");
        for (int i = 0; i < 3; i++) {
            tempdir = tempdir.substring(0, tempdir.lastIndexOf('\\'));
        }
        tempdir = tempdir + "\\util\\src\\main\\resources\\org\\spbu\\histology\\util\\Cells\\";
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
                    Histion main = hm.getHistionMap().get(0);
                    BufferedReader br = new BufferedReader(new FileReader(dir + selected));
                    String line = br.readLine();

                    line = line.substring(line.indexOf(" ") + 1, line.length());
                    line = line.substring(line.indexOf(" ") + 1, line.length());
                    main.setXCoordinate(Double.parseDouble(line.substring(0, line.indexOf(" "))));
                    line = line.substring(line.indexOf(" ") + 1, line.length());
                    main.setYCoordinate(Double.parseDouble(line.substring(0, line.indexOf(" "))));
                    line = line.substring(line.indexOf(" ") + 1, line.length());
                    main.setZCoordinate(Double.parseDouble(line));
                    line = br.readLine();
                    int cellNum = Integer.parseInt(line);
                    for (int i = 0; i < cellNum; i++) {
                        ObservableList<ArrayList<Integer>> facetData = FXCollections.observableArrayList();
                        Cell c = new Cell("Name", 0, 0, 0, 0, 0, FXCollections.observableArrayList(),
                                Color.BLUE, Color.LIGHTBLUE, 0, true);
                        double r, g, b;
                        line = br.readLine();

                        String name = line;
                        name = name.substring(name.indexOf("<") + 1, name.lastIndexOf(">"));
                        int count = 1;
                        while (Names.containsCellName(name)) {
                            name = line;
                            name = name.substring(name.indexOf("<") + 1, name.lastIndexOf(">"));
                            name += "(" + count + ")";
                            count++;
                        }
                        c.setName("Cell <" + name + ">");

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

                        ArrayList<TetgenPoint> pd = new ArrayList<>();
                        int num = 1;
                        for (int j = 0; j < partNum; j++) {
                            ObservableList<TetgenPoint> pointData = FXCollections.observableArrayList();
                            Part p = new Part("Part", FXCollections.observableArrayList(), c.getId());
                            line = br.readLine();
                            p.setName(line);

                            line = br.readLine();
                            int pointNum = Integer.parseInt(line);
                            for (int q = 0; q < pointNum; q++) {
                                line = br.readLine();
                                r = Double.parseDouble(line.substring(0, line.indexOf(" ")));
                                line = line.substring(line.indexOf(" ") + 1, line.length());
                                g = Double.parseDouble(line.substring(0, line.indexOf(" ")));
                                line = line.substring(line.indexOf(" ") + 1, line.length());
                                b = Double.parseDouble(line);
                                pointData.add(new TetgenPoint(q + 1, r, g, b));
                                pd.add(new TetgenPoint(num, r, g, b));
                                num++;
                            }
                            p.setPointData(pointData);
                            p.setAvgNode();
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
                            }
                            list.add(Integer.parseInt(line));
                            facetData.add(list);
                        }
                        c.setFacetData(facetData);

                        ArrayList<TwoIntegers> lineList = new ArrayList<>();
                        for (ArrayList<Integer> f : facetData) {
                            for (int j = 1; j < f.size(); j++) {
                                TwoIntegers ti = new TwoIntegers(j, f.get(j - 1), f.get(j));
                                if (!lineList.contains(ti)) {
                                    lineList.add(ti);
                                }
                            }
                            TwoIntegers ti = new TwoIntegers(f.size(), f.get(f.size() - 1), f.get(0));
                            if (!lineList.contains(ti)) {
                                lineList.add(ti);
                            }
                        }

                        ArrayList<Line> lines = new ArrayList<>();
                        for (int j = 0; j < lineList.size(); j++) {

                            TetgenPoint point1 = pd.get(lineList.get(j).getPoint1() - 1);
                            TetgenPoint point2 = pd.get(lineList.get(j).getPoint2() - 1);
                            if (Math.abs(point1.getY() - point2.getY()) < 0.0001) {
                                lines.add(new Line(new org.spbu.histology.model.Node(point1.getX(), point1.getZ(), point1.getY()),
                                        new org.spbu.histology.model.Node(point2.getX(), point2.getZ(), point2.getY())));
                            }
                        }
                        LineEquations.addLine(c.getId(), lines);

                        main.addChild(c);
                        name = c.getName();
                        Names.addCellName(name.substring(name.indexOf("<") + 1, name.lastIndexOf(">")));
                    }
                    br.close();
                } catch (Exception ex) {
                    System.out.println("error");
                }
                window.close();
            }
        });

        VBox layout = new VBox(10);
        layout.getChildren().addAll(hBox, closeButton);
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout, 300, 150);
        window.setScene(scene);
        window.showAndWait();
    }

}
