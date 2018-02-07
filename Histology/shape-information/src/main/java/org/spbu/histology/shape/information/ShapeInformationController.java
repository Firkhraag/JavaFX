package org.spbu.histology.shape.information;

import org.spbu.histology.model.BoxShape;
import org.spbu.histology.model.CylinderShape;
import org.spbu.histology.model.Shape;
import org.spbu.histology.model.ShapeManager;
import org.spbu.histology.model.SphereShape;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import org.openide.LifecycleManager;
import org.openide.util.Lookup;


public class ShapeInformationController implements Initializable {
    
    private Shape theShape;
    
    private ShapeManager sm = null;    
    private String shapeType;

    @FXML
    private GridPane gridPane;
    
    @FXML
    private ComboBox<String> shapeBox;
    
    @FXML
    private Button button;
    
    @FXML
    private TextField nameField;
    
    @FXML
    private TextField xRotationField;
    
    @FXML
    private TextField yRotationField;
    
    @FXML
    private TextField zRotationField;
    
    @FXML
    private TextField xPositionField;
    
    @FXML
    private TextField yPositionField;
    
    @FXML
    private TextField zPositionField;
    
    @FXML
    private ColorPicker diffuseColorPicker;
    
    @FXML
    private ColorPicker specularColorPicker;
    
    private Label lengthLabelCube = new Label("Length");
    private TextField lengthFieldCube = new TextField();
    private Label widthLabelCube = new Label("Height");
    private TextField widthFieldCube = new TextField();
    private Label heightLabelCube = new Label("Width");
    private TextField heightFieldCube = new TextField();
    private Label radiusLabelCylinder = new Label("Height");
    private TextField radiusFieldCylinder = new TextField();
    private Label heightLabelCylinder = new Label("Radius");
    private TextField heightFieldCylinder = new TextField();
    private Label radiusLabelSphere = new Label("Radius");
    private TextField radiusFieldSphere = new TextField();
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        sm = Lookup.getDefault().lookup(ShapeManager.class);
        if (sm == null) {
            LifecycleManager.getDefault().exit();
        }
        gridPane.setVgap(20);
        gridPane.setHgap(20);
        gridPane.setPadding(new Insets(20, 20, 20, 20));
        shapeBox.getSelectionModel().selectedItemProperty().addListener((v, oldValue, newValue) -> {
            switch (newValue) {
                case "Cube":
                    gridPane.getChildren().removeAll(radiusLabelCylinder, radiusFieldCylinder, 
                            heightLabelCylinder, heightFieldCylinder, radiusLabelSphere,
                            radiusFieldSphere);
                    GridPane.setConstraints(lengthLabelCube, 0, 10);
                    GridPane.setConstraints(lengthFieldCube, 1, 10);
                    GridPane.setConstraints(widthLabelCube, 0, 11);
                    GridPane.setConstraints(widthFieldCube, 1, 11);
                    GridPane.setConstraints(heightLabelCube, 0, 12);
                    GridPane.setConstraints(heightFieldCube, 1, 12);
                    GridPane.setConstraints(button, 0, 13);
                    gridPane.getChildren().addAll(lengthLabelCube, lengthFieldCube,
                            widthLabelCube, widthFieldCube, heightLabelCube, heightFieldCube);
                    shapeType = "Cube";
                    enablePropertyBindings("Cube");
                    break;
                case "Cylinder":
                    gridPane.getChildren().removeAll(lengthLabelCube, lengthFieldCube,
                            widthLabelCube, widthFieldCube, heightLabelCube, heightFieldCube,
                            radiusLabelSphere, radiusFieldSphere);
                    GridPane.setConstraints(radiusLabelCylinder, 0, 10);
                    GridPane.setConstraints(radiusFieldCylinder, 1, 10);
                    GridPane.setConstraints(heightLabelCylinder, 0, 11);
                    GridPane.setConstraints(heightFieldCylinder, 1, 11);
                    GridPane.setConstraints(button, 0, 12);
                    gridPane.getChildren().addAll(radiusLabelCylinder, radiusFieldCylinder,
                            heightLabelCylinder, heightFieldCylinder);
                    shapeType = "Cylinder";
                    enablePropertyBindings("Cylinder");
                    break;
                case "Sphere":
                    gridPane.getChildren().removeAll(lengthLabelCube, lengthFieldCube,
                            widthLabelCube, widthFieldCube, heightLabelCube, heightFieldCube,
                            radiusLabelCylinder, radiusFieldCylinder, heightLabelCylinder,
                            heightFieldCylinder);
                    GridPane.setConstraints(radiusLabelSphere, 0, 10);
                    GridPane.setConstraints(radiusFieldSphere, 1, 10);
                    GridPane.setConstraints(button, 0, 11);
                    gridPane.getChildren().addAll(radiusLabelSphere, radiusFieldSphere);
                    shapeType = "Sphere";
                    enablePropertyBindings("Sphere");
                    break;
            }
        });
        shapeBox.getSelectionModel().select(0);
    }

    private void enablePropertyBindings(String type) {
        switch (type) {
            case "Cube":
                button.disableProperty().bind(Bindings.isEmpty(nameField.textProperty())
                .or(Bindings.isEmpty(xRotationField.textProperty()))
                .or(Bindings.isEmpty(yRotationField.textProperty()))
                .or(Bindings.isEmpty(zRotationField.textProperty()))
                .or(Bindings.isEmpty(xPositionField.textProperty()))
                .or(Bindings.isEmpty(yPositionField.textProperty()))
                .or(Bindings.isEmpty(zPositionField.textProperty()))
                .or(Bindings.isEmpty(lengthFieldCube.textProperty()))
                .or(Bindings.isEmpty(widthFieldCube.textProperty()))
                .or(Bindings.isEmpty(heightFieldCube.textProperty())));
                break;
            case "Cylinder":
                button.disableProperty().bind(Bindings.isEmpty(nameField.textProperty())
                .or(Bindings.isEmpty(xRotationField.textProperty()))
                .or(Bindings.isEmpty(yRotationField.textProperty()))
                .or(Bindings.isEmpty(zRotationField.textProperty()))
                .or(Bindings.isEmpty(xPositionField.textProperty()))
                .or(Bindings.isEmpty(yPositionField.textProperty()))
                .or(Bindings.isEmpty(zPositionField.textProperty()))
                .or(Bindings.isEmpty(heightFieldCylinder.textProperty()))
                .or(Bindings.isEmpty(radiusFieldCylinder.textProperty())));
                break;
            case "Sphere":
                button.disableProperty().bind(Bindings.isEmpty(nameField.textProperty())
                .or(Bindings.isEmpty(xRotationField.textProperty()))
                .or(Bindings.isEmpty(yRotationField.textProperty()))
                .or(Bindings.isEmpty(zRotationField.textProperty()))
                .or(Bindings.isEmpty(xPositionField.textProperty()))
                .or(Bindings.isEmpty(yPositionField.textProperty()))
                .or(Bindings.isEmpty(zPositionField.textProperty()))
                .or(Bindings.isEmpty(radiusFieldSphere.textProperty())));
                break;
        }
    }
    
    @FXML
    private void buttonAction() {
        switch (shapeType) {
            case "Cube":   
                try {
                    if ((Double.parseDouble(lengthFieldCube.getText()) <= 0) ||
                            (Double.parseDouble(widthFieldCube.getText()) <= 0) ||
                            (Double.parseDouble(widthFieldCube.getText()) <= 0)) {
                        throw new Exception();
                    }
                    if (ShapeInformationInitialization.mode.equals("Edit"))
                        sm.updateShape(new BoxShape(theShape.getId(), nameField.getText(), shapeType, 
                            String.valueOf(Double.parseDouble(xRotationField.getText())), 
                            String.valueOf(Double.parseDouble(yRotationField.getText())), 
                            String.valueOf(Double.parseDouble(zRotationField.getText())), 
                            String.valueOf(Double.parseDouble(xPositionField.getText())), 
                            String.valueOf(Double.parseDouble(yPositionField.getText())), 
                            String.valueOf(Double.parseDouble(zPositionField.getText())), 
                            diffuseColorPicker.getValue(), specularColorPicker.getValue(), 
                            String.valueOf(Double.parseDouble(lengthFieldCube.getText())), 
                            String.valueOf(Double.parseDouble(widthFieldCube.getText())),
                            String.valueOf(Double.parseDouble(heightFieldCube.getText()))), theShape.getId());
                    else
                        sm.addShape(new BoxShape(nameField.getText(), shapeType, 
                            String.valueOf(Double.parseDouble(xRotationField.getText())), 
                            String.valueOf(Double.parseDouble(yRotationField.getText())), 
                            String.valueOf(Double.parseDouble(zRotationField.getText())), 
                            String.valueOf(Double.parseDouble(xPositionField.getText())), 
                            String.valueOf(Double.parseDouble(yPositionField.getText())), 
                            String.valueOf(Double.parseDouble(zPositionField.getText())), 
                            diffuseColorPicker.getValue(), specularColorPicker.getValue(), 
                            String.valueOf(Double.parseDouble(lengthFieldCube.getText())), 
                            String.valueOf(Double.parseDouble(widthFieldCube.getText())),
                            String.valueOf(Double.parseDouble(heightFieldCube.getText()))));
                    System.out.println("Cube---");
                    System.out.println(String.valueOf(Double.parseDouble(yRotationField.getText())));
                    ShapeInformationInitialization.stage.close();
                } catch (Exception ex) {
                    Alert alert = new Alert(AlertType.INFORMATION);
                    alert.setTitle("Error");
                    alert.setHeaderText(null);
                    alert.setContentText("Wrong input");
                    alert.showAndWait();
                }
                break;
            case "Cylinder":   
                try {
                    if (ShapeInformationInitialization.mode.equals("Edit"))
                        sm.updateShape(new CylinderShape(theShape.getId(), nameField.getText(), shapeType, 
                            String.valueOf(Double.parseDouble(xRotationField.getText())), 
                            String.valueOf(Double.parseDouble(yRotationField.getText())), 
                            String.valueOf(Double.parseDouble(zRotationField.getText())), 
                            String.valueOf(Double.parseDouble(xPositionField.getText())), 
                            String.valueOf(Double.parseDouble(yPositionField.getText())), 
                            String.valueOf(Double.parseDouble(zPositionField.getText())), 
                            diffuseColorPicker.getValue(), specularColorPicker.getValue(), 
                            String.valueOf(Double.parseDouble(heightFieldCylinder.getText())),
                            String.valueOf(Double.parseDouble(radiusFieldCylinder.getText()))), theShape.getId());
                    else
                        sm.addShape(new CylinderShape(nameField.getText(), shapeType, 
                            String.valueOf(Double.parseDouble(xRotationField.getText())), 
                            String.valueOf(Double.parseDouble(yRotationField.getText())), 
                            String.valueOf(Double.parseDouble(zRotationField.getText())), 
                            String.valueOf(Double.parseDouble(xPositionField.getText())), 
                            String.valueOf(Double.parseDouble(yPositionField.getText())), 
                            String.valueOf(Double.parseDouble(zPositionField.getText())), 
                            diffuseColorPicker.getValue(), specularColorPicker.getValue(), 
                            String.valueOf(Double.parseDouble(heightFieldCylinder.getText())),
                            String.valueOf(Double.parseDouble(radiusFieldCylinder.getText()))));
                    ShapeInformationInitialization.stage.close();
                } catch (Exception ex) {
                    Alert alert = new Alert(AlertType.INFORMATION);
                    alert.setTitle("Error");
                    alert.setHeaderText(null);
                    alert.setContentText("Wrong input");
                    alert.showAndWait();
                }
                break;
            case "Sphere":   
                try {
                    if (ShapeInformationInitialization.mode.equals("Edit"))
                        sm.updateShape(new SphereShape(theShape.getId(), nameField.getText(), shapeType, 
                            String.valueOf(Double.parseDouble(xRotationField.getText())), 
                            String.valueOf(Double.parseDouble(yRotationField.getText())), 
                            String.valueOf(Double.parseDouble(zRotationField.getText())), 
                            String.valueOf(Double.parseDouble(xPositionField.getText())), 
                            String.valueOf(Double.parseDouble(yPositionField.getText())), 
                            String.valueOf(Double.parseDouble(zPositionField.getText())), 
                            diffuseColorPicker.getValue(), specularColorPicker.getValue(),
                            String.valueOf(Double.parseDouble(radiusFieldSphere.getText()))), theShape.getId());
                    else
                        sm.addShape(new SphereShape(nameField.getText(), shapeType, 
                            String.valueOf(Double.parseDouble(xRotationField.getText())), 
                            String.valueOf(Double.parseDouble(yRotationField.getText())), 
                            String.valueOf(Double.parseDouble(zRotationField.getText())), 
                            String.valueOf(Double.parseDouble(xPositionField.getText())), 
                            String.valueOf(Double.parseDouble(yPositionField.getText())), 
                            String.valueOf(Double.parseDouble(zPositionField.getText())), 
                            diffuseColorPicker.getValue(), specularColorPicker.getValue(),
                            String.valueOf(Double.parseDouble(radiusFieldSphere.getText()))));
                    ShapeInformationInitialization.stage.close();
                } catch (Exception ex) {
                    Alert alert = new Alert(AlertType.INFORMATION);
                    alert.setTitle("Error");
                    alert.setHeaderText(null);
                    alert.setContentText("Wrong input");
                    alert.showAndWait();
                }
                break;
        }
    }
    
    private void configureEditPanelBindings(Shape p, String type) {
        nameField.textProperty().bindBidirectional(theShape.nameProperty());
        xRotationField.textProperty().bindBidirectional(theShape.xRotateProperty());
        yRotationField.textProperty().bindBidirectional(theShape.yRotateProperty());
        zRotationField.textProperty().bindBidirectional(theShape.zRotateProperty());
        xPositionField.textProperty().bindBidirectional(theShape.xCoordinateProperty());
        yPositionField.textProperty().bindBidirectional(theShape.yCoordinateProperty());
        zPositionField.textProperty().bindBidirectional(theShape.zCoordinateProperty());
        switch (type) {
            case "Cube":
                BoxShape theBoxShape = (BoxShape) p;
                lengthFieldCube.textProperty().bindBidirectional(theBoxShape.lengthProperty());
                widthFieldCube.textProperty().bindBidirectional(theBoxShape.widthProperty());
                heightFieldCube.textProperty().bindBidirectional(theBoxShape.heightProperty());
                break;
            case "Cylinder":
                CylinderShape theCylinderShape = (CylinderShape) p;
                radiusFieldCylinder.textProperty().bindBidirectional(theCylinderShape.radiusProperty());
                heightFieldCylinder.textProperty().bindBidirectional(theCylinderShape.heightProperty());
                break;
            case "Sphere":
                SphereShape theSphereShape = (SphereShape) p;
                radiusFieldSphere.textProperty().bindBidirectional(theSphereShape.radiusProperty());
                break;
        }
    }
    
    public void doUpdate(Shape shape) {
        button.setText("Update");
        theShape = shape;
        switch (shape.getType()) {
            case "Cube":
                shapeBox.getSelectionModel().select(0);
                break;
            case "Cylinder":
                shapeBox.getSelectionModel().select(1);
                break;
            case "Sphere":
                shapeBox.getSelectionModel().select(2);
                break;
        }
        configureEditPanelBindings(shape, shape.getType());
        diffuseColorPicker.setValue(shape.getDiffuseColor());
        specularColorPicker.setValue(shape.getSpecularColor());
    }
    
}
