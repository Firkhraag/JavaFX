package org.spbu.histology.shape.information;

import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import org.openide.LifecycleManager;
import org.openide.util.Lookup;
import org.spbu.histology.model.HistionManager;
import org.spbu.histology.model.TetgenPoint;

public class PartInformationInitialization {
    
    public static void show(Integer cellId, Integer partId) {
        
        HistionManager hm = Lookup.getDefault().lookup(HistionManager.class);
        if (hm == null) {
            LifecycleManager.getDefault().exit();
        }
        
        final int width = 1200;
        final int height = 800;
        Stage primaryStage = new Stage();
        primaryStage.setTitle("Part");
        BorderPane borderPane = new BorderPane();
        Group root = new Group();
        Parent leftPart;
        PointTabController pointTabController;
        ObservableList<Rectangle> rectangleList = FXCollections.observableArrayList();
        IntegerProperty count = new SimpleIntegerProperty(1);
        try {
            URL location = PartInformationInitialization.class.getResource("PointTab.fxml");
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(location);
            fxmlLoader.setBuilderFactory(new JavaFXBuilderFactory());

            leftPart = (Parent)fxmlLoader.load(location.openStream());
            pointTabController = (PointTabController)fxmlLoader.getController();
            pointTabController.setRectangleList(rectangleList);
            pointTabController.setCount(count);
            if (partId == -1)
                pointTabController.setInitialSize(0);
            else
                pointTabController.setInitialSize(hm.getHistionMap().get(0).
                    getItemMap().get(cellId).getItemMap().get(partId).getPointData().size());
            pointTabController.setIds(cellId, partId);       
        } catch (Exception ex) {
            Logger.getLogger(PartInformationInitialization.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }
        borderPane.setCenter(root);
        borderPane.setLeft(leftPart);
        Axes axes = new Axes(
                width, height,
                -600, 600, 100,
                -400, 400, 100
        );
        pointTabController.setPaneSize(1200, 800);
        axes.setStyle("-fx-background-color: white");
        root.getChildren().add(axes);
        pointTabController.setRoot(root);
        Scene scene = new Scene(borderPane);
        primaryStage.setScene(scene);
        if (partId != -1) {
            for (TetgenPoint p : hm.getHistionMap().get(0).getItemMap().get(cellId).getItemMap().get(partId).getPointData()) {
                Rectangle r = new Rectangle();
                r.setX(p.getX() + width / 2 - 2);
                r.setY((-1) * (p.getZ() - height / 2) - 2);
                r.setWidth(5);
                r.setHeight(5);
                root.getChildren().add(r);
                rectangleList.add(r);
                pointTabController.addPoint(new TetgenPoint(p));
                count.set(count.get() + 1);
            }
        }
        axes.setOnMousePressed((MouseEvent e) -> {
            if (e.isPrimaryButtonDown()) {
                int xPos = (int)e.getX();
                int yPos = (int)e.getY();
                if (xPos >= 0 && xPos <= width && yPos >= 0 && yPos <= height) {
                    Rectangle r = new Rectangle();
                    r.setStroke(Color.BLACK);
                    r.setX(xPos - 2);
                    r.setY(yPos - 2);
                    r.setWidth(5);
                    r.setHeight(5);
                    root.getChildren().add(r);
                    rectangleList.add(r);
                    double y = 0;
                    if (partId != -1)
                        if (hm.getHistionMap().get(0).getItemMap().get(cellId).getItemMap().get(partId).getPointData().size() > 0)
                            y = hm.getHistionMap().get(0).getItemMap().get(cellId).getItemMap().get(partId).getPointData().get(0).getY();
                    TetgenPoint p = new TetgenPoint(count.get(), xPos - width / 2, y, -yPos + height / 2);
                    pointTabController.addPoint(p);
                    count.set(count.get() + 1);
                }
            }
        });

        primaryStage.heightProperty().addListener((obs, oldVal, newVal) -> {
            pointTabController.setTableHeight(primaryStage.getHeight() - 120);
        });
        primaryStage.showAndWait();
    }
}
