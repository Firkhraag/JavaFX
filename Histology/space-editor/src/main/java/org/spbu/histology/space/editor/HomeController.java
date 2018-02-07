package org.spbu.histology.space.editor;

import org.spbu.histology.model.Shape;
import org.spbu.histology.model.ShapeManager;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.spbu.histology.shape.information.ShapeInformationInitialization;
import org.spbu.histology.toolbar.ChosenTool;
import java.util.ArrayList;
import javafx.collections.MapChangeListener;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import org.openide.LifecycleManager;
import org.openide.util.Lookup;

public class HomeController implements Initializable {
    
    private ShapeManager sm = null;
    
    private static int count;
    
    @FXML
    private VBox vBox;
    
    @FXML
    private GridPane gridPane;
    
    @FXML
    private Button addButton;
    
    @FXML
    private ScrollPane scrollPane;
    
    
    private final MapChangeListener<Long, Shape> shapeListener =
            (change) -> {
                if (change.wasAdded()) {
                    if (ChosenTool.getToolNumber() == -1)
                        ChosenTool.setToolNumber(-2);
                    else
                        ChosenTool.setToolNumber(-1);
                }       
            };
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        sm = Lookup.getDefault().lookup(ShapeManager.class);
        if (sm == null) {
            LifecycleManager.getDefault().exit();
        }
        
        vBox.setPadding(new Insets(10, 10, 10, 10));
        addButton.setPadding(new Insets(5, 5, 5, 5));
        scrollPane.setPrefSize(405, 600);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        gridPane.setVgap(20);
        gridPane.setHgap(20);
        Image imageDecline = new Image(getClass().getResourceAsStream("add-plus-button.png"));
        addButton.setGraphic(new ImageView(imageDecline));
        
        loadShapes();
        sm.addListener(shapeListener);
    }
    
    private void loadShapes() {
        count = 0;
        ArrayList<Label> labels = new ArrayList<Label>();
        sm.getAllShapes().forEach(s -> {
            System.out.println("----");
            System.out.println(s.getId());
            System.out.println("----");
            labels.add(new Label(s.getName()));
            Button editButton = new Button();
            Button deleteButton = new Button();
            Image imageDecline = new Image(getClass().getResourceAsStream("rubbish-bin.png"));
            deleteButton.setGraphic(new ImageView(imageDecline));
            deleteButton.setOnAction(e -> {
                sm.deleteShape(s);
                if (ChosenTool.getToolNumber() == -1)
                    ChosenTool.setToolNumber(-2);
                else
                    ChosenTool.setToolNumber(-1);
            });
            imageDecline = new Image(getClass().getResourceAsStream("edit.png"));
            editButton.setGraphic(new ImageView(imageDecline));
            editButton.setOnAction(e -> {
                ShapeInformationInitialization.setShape(s);
                ShapeInformationInitialization.createScene("Edit");
            });
            GridPane.setConstraints(labels.get(count), 0, count + 1);
            GridPane.setConstraints(editButton, 1, count + 1);
            GridPane.setConstraints(deleteButton, 2, count + 1);
            gridPane.getChildren().addAll(labels.get(count), editButton, deleteButton);
            count++;
        });
    }
    
    @FXML
    private void addShape() {
        ShapeInformationInitialization.createScene("Add");
    }
}
