package com.tools;

import com.model.BoxShape;
import com.model.CylinderShape;
import java.net.URL;
import java.util.Collections;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import org.openide.util.lookup.InstanceContent;
import com.model.Shape;
import com.model.ShapeManager;
import com.model.SphereShape;
import com.shape.information.ShapeInformationController;
import com.shape.information.ShapeInformationInitialization;
import com.toolbar.ChosenTool;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.MapChangeListener;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import org.openide.LifecycleManager;
import org.openide.util.Lookup;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

public class HomeController implements Initializable {
    
    private final InstanceContent instanceContent = new InstanceContent();
    private Shape theShape;
    
    private ShapeManager sm = null;
    
    private static int count;
    
    /*@FXML
    private ComboBox shapeBox;*/
    
    @FXML
    private VBox vBox;
    
    @FXML
    private GridPane gridPane;
    
    @FXML
    private Button addButton;
    
    /*@FXML
    private Label xLabel;
    
    @FXML
    private Label yLabel;
    
    @FXML
    private Label zLabel;
    
    @FXML
    private TextField xField;
    
    @FXML
    private TextField yField;
    
    @FXML
    private TextField zField;
    
    @FXML
    private Button updateButton;*/
    
    @FXML
    private ScrollPane scrollPane;
    
    /*@FXML
    private Label parLabel1;
    
    @FXML
    private Label parLabel2;
    
    @FXML
    private Label parLabel3;
    
    @FXML
    private TextField parField1;
    
    @FXML
    private TextField parField2;
    
    @FXML
    private TextField parField3;*/
    
    private final MapChangeListener<Long, Shape> shapeListener =
            (change) -> {
                if (change.wasAdded()) {
                    if (ChosenTool.getToolNumber() == -1)
                        ChosenTool.setToolNumber(-2);
                    else
                        ChosenTool.setToolNumber(-1);
                }
                /*if (ChosenTool.getToolNumber() == -1)
                    ChosenTool.setToolNumber(-2);
                else
                    ChosenTool.setToolNumber(-1);*/
                
                //System.out.println("Deleted");
                //loadShapes();
                /*if(change.getValueAdded() != null) {
                    System.out.println("1");
                    loadShapes();
                }  */             
            };
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        sm = Lookup.getDefault().lookup(ShapeManager.class);
        if (sm == null) {
            LifecycleManager.getDefault().exit();
        }
        
        /*xLabel.setVisible(false);
        xField.setVisible(false);
        yLabel.setVisible(false);
        yField.setVisible(false);
        zLabel.setVisible(false);
        zField.setVisible(false);
        updateButton.setVisible(false);
        
        shapeBox.getSelectionModel().selectedItemProperty().addListener((v, oldValue, newValue) -> {
            xLabel.setVisible(true);
            xField.setVisible(true);
            yLabel.setVisible(true);
            yField.setVisible(true);
            zLabel.setVisible(true);
            zField.setVisible(true);
            updateButton.setVisible(true);
            //String shape = newValue.toString();
            theShape = new Shape(newValue.toString());
            System.out.println(theShape.getName());
            setBindings();
            instanceContent.set(Collections.singleton(theShape), null);
        });*/
        vBox.setPadding(new Insets(10, 10, 10, 10));
        addButton.setPadding(new Insets(5, 5, 5, 5));
        scrollPane.setPrefSize(405, 600);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        gridPane.setVgap(20);
        gridPane.setHgap(20);
        Image imageDecline = new Image(getClass().getResourceAsStream("add-plus-button.png"));
        addButton.setGraphic(new ImageView(imageDecline));
        
        //buildData();
        loadShapes();
        sm.addListener(shapeListener);
        //shapeBox.getSelectionModel().select(0);
    }
    
    private void loadShapes() {
        count = 0;
        //instanceContent.set(Collections.singleton(sm), null);
        //sm.getAllShapes().forEach(s -> vBox.getChildren().add(new Label("Test")));
        ArrayList<Label> labels = new ArrayList<Label>();
        //int count = 0;
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
                //instanceContent.set(Collections.singleton(s), null);
                ShapeInformationInitialization.setShape(s);
                ShapeInformationInitialization.createScene("Edit");
            });
            GridPane.setConstraints(labels.get(count), 0, count + 1);
            GridPane.setConstraints(editButton, 1, count + 1);
            GridPane.setConstraints(deleteButton, 2, count + 1);
            gridPane.getChildren().addAll(labels.get(count), editButton, deleteButton);
            count++;
        /*layout.setConstraints(login, 1, 0);
        layout.setConstraints(passwordLabel, 0, 1);
        layout.setConstraints(password, 1, 1);
        layout.setConstraints(logIn, 1, 2);
        layout.add(bottom, 0, 4, 2, 1);
        layout.getChildren().addAll(loginLabel, login, passwordLabel, password, logIn);*/
            //labels.get(i).GridPane.columnIndex="0";
        });
    }
    
    public InstanceContent getInstanceContent() {
        return instanceContent;
    }
    
    /*private void setBindings() {
        xField.textProperty().bindBidirectional(theShape.xCoordinateProperty());
        yField.textProperty().bindBidirectional(theShape.yCoordinateProperty());
        zField.textProperty().bindBidirectional(theShape.zCoordinateProperty());
    }
    
    @FXML
    private void updateAction() {
        /*theShape.setXCoordinate(xField.getText());
        theShape.setYCoordinate(yField.getText());
        theShape.setZCoordinate(zField.getText());*/
        //instanceContent.remove(Collections.singleton(theShape));
        /*String name = theShape.getName();
        theShape = new Shape(name, xField.getText(), yField.getText(), zField.getText());
        setBindings();
        instanceContent.set(Collections.singleton(theShape), null);
        //System.out.println(theShape.getXCoordinate());
    } */
    
    /*private void buildData() {
        //if ((sm.getAllShapes().size() == 0) && (sm.getAddPredefinedShapes())) {
        if (sm.getAddPredefinedShapes()) {
            //System.out.println("Tool builded");
            sm.setAddPredefinedShapes(false);
            sm.addShape(new BoxShape("Shape1", "Cube", "0", "0", "0", "0", "0", "0", Color.BLUE, Color.LIGHTBLUE, "100", "200", "300"));
            sm.addShape(new CylinderShape("Shape2", "Cylinder", "0", "0", "0", "100", "100", "100", Color.BLUE, Color.LIGHTBLUE, "50", "80"));
            sm.addShape(new SphereShape("Shape3", "Sphere", "0", "0", "0", "-200", "-200", "-200", Color.BLUE, Color.LIGHTBLUE, "90"));
        }
    }*/
    
    @FXML
    private void addShape() {
        ShapeInformationInitialization.createScene("Add");
    }
}
