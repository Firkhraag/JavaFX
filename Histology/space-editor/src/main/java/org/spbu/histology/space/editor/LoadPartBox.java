package org.spbu.histology.space.editor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.geometry.*;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import org.openide.LifecycleManager;
import org.openide.util.Lookup;
import org.spbu.histology.model.Histion;
import org.spbu.histology.model.HistionManager;
import org.spbu.histology.model.Part;
import org.spbu.histology.model.TetgenPoint;

public class LoadPartBox {

    public static void display(Integer cellId) {

        HistionManager hm = Lookup.getDefault().lookup(HistionManager.class);
        if (hm == null) {
            LifecycleManager.getDefault().exit();
        }

        Stage window = new Stage();
        window.setTitle("Load part");

        String tempdir = System.getProperty("user.dir");
        for (int i = 0; i < 3; i++) {
            tempdir = tempdir.substring(0, tempdir.lastIndexOf('\\'));
        }
        tempdir = tempdir + "\\util\\src\\main\\resources\\org\\spbu\\histology\\util\\Parts\\";
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

                    double x, y, z;

                    String line = br.readLine();

                    ObservableList<TetgenPoint> pointData = FXCollections.observableArrayList();
                    Part p = new Part("Part", FXCollections.observableArrayList(), cellId);
                    p.setName(line);

                    line = br.readLine();
                    int pointNum = Integer.parseInt(line);
                    for (int q = 0; q < pointNum; q++) {
                        line = br.readLine();
                        x = Double.parseDouble(line.substring(0, line.indexOf(" ")));
                        line = line.substring(line.indexOf(" ") + 1, line.length());
                        y = Double.parseDouble(line.substring(0, line.indexOf(" ")));
                        line = line.substring(line.indexOf(" ") + 1, line.length());
                        z = Double.parseDouble(line);
                        pointData.add(new TetgenPoint(q + 1, x, y, z));
                    }
                    p.setPointData(pointData);
                    p.setAvgNode();
                    hm.getHistionMap().get(0).getItemMap().get(cellId).addChild(p);
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
