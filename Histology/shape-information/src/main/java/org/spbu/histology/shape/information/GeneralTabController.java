package org.spbu.histology.shape.information;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import org.openide.LifecycleManager;
import org.openide.util.Lookup;
import org.spbu.histology.model.HistionManager;
import org.spbu.histology.model.Node;
import org.spbu.histology.model.Part;
import org.spbu.histology.model.Shape;
import org.spbu.histology.model.ShapeManager;
import org.spbu.histology.model.TetgenFacetHole;
import org.spbu.histology.model.TetgenFacetPolygon;
import org.spbu.histology.model.TetgenPoint;

public class GeneralTabController implements Initializable {
    
    @FXML
    private GridPane gridPane;
    
    @FXML
    private TextField nameField;
    
    @FXML
    private TextField xRotationField;
    
    @FXML
    private TextField yRotationField;
    
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
    
    @FXML
    private Button createButton;
    
    private ShapeManager sm = null;
    
    private HistionManager hm = null;
    
    private final int shapeVertexSize = 30;
    
    private long id;
    
    private long histionId;
    private long cellId;
    
    private ObservableList<TetgenPoint> pointData = FXCollections.observableArrayList();
    private ObservableList<TetgenPoint> holeData = FXCollections.observableArrayList();
    private ObservableList<TetgenFacetPolygon> facetPolygonData = FXCollections.observableArrayList();
    private ObservableList<TetgenFacetHole> facetHoleData = FXCollections.observableArrayList();
    
    BooleanProperty change;
    
    public void setShape(Shape s, BooleanProperty c) {
        change = c;
        id = s.getId();
        pointData = s.getPointData();
        holeData = s.getHoleData();
        facetPolygonData = s.getPolygonsInFacetData();
        facetHoleData = s.getHolesInFacetData();
        nameField.setText(s.getName());
        if (!s.getName().equals(""))
            createButton.setText("Update");
        xRotationField.setText(String.valueOf(s.getXRotate()));
        yRotationField.setText(String.valueOf(s.getYRotate()));
        xPositionField.setText(String.valueOf(s.getXCoordinate()));
        yPositionField.setText(String.valueOf(s.getYCoordinate()));
        zPositionField.setText(String.valueOf(s.getZCoordinate()));
        diffuseColorPicker.setValue(s.getDiffuseColor());
        specularColorPicker.setValue(s.getSpecularColor());
        histionId = s.getHistionId();
        cellId = s.getCellId();
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        sm = Lookup.getDefault().lookup(ShapeManager.class);
        if (sm == null) {
            LifecycleManager.getDefault().exit();
        }
        
        hm = Lookup.getDefault().lookup(HistionManager.class);
        if (hm == null) {
            LifecycleManager.getDefault().exit();
        }
        gridPane.setVgap(20);
        gridPane.setHgap(20);
        gridPane.setPadding(new Insets(20, 20, 20, 20));
        bindButtonDisabled();
    }
    
    private void bindButtonDisabled() {
        createButton.disableProperty().bind(Bindings.isEmpty(nameField.textProperty())
                .or(Bindings.isEmpty(xRotationField.textProperty()))
                .or(Bindings.isEmpty(yRotationField.textProperty()))
                .or(Bindings.isEmpty(xPositionField.textProperty()))
                .or(Bindings.isEmpty(yPositionField.textProperty()))
                .or(Bindings.isEmpty(zPositionField.textProperty())));
    }
    
    @FXML
    private void buttonAction() {
        Integer temp;
        ArrayList<Node> nodeList = new ArrayList();
        ArrayList<Node> holeList = new ArrayList();
        ArrayList<Integer> facetList = new ArrayList();
        ArrayList<Integer> pointList;
        ArrayList<CheckFacetPolygons> checkFacetPolygonsList = new ArrayList();
        ArrayList<CheckFacetPolygonVertices> checkFacetPolygonVerticesList = new ArrayList();
        int verticesNumber;
        int maxNumberOfVertices = 0;
        int pointSize = pointData.size();
        if (pointSize < 3) {
            AlertBox.display("Error", "There should be at least 3 points");
            return;
        }
        Node nodeAvg = new Node(0,0,0);
        for (int i = 0; i < pointSize; i++) {
            Node n = new Node(pointData.get(i).getX(), 
                    pointData.get(i).getY(),
                    pointData.get(i).getZ());
            nodeAvg.x += n.x;
            nodeAvg.y += n.y;
            nodeAvg.z += n.z;
            if (nodeList.contains(n)) {
                AlertBox.display("Error", "There are two same points. \nPoint table record #" + (i + 1));
                return;
            }
            nodeList.add(n);
        }
        nodeAvg.x /= pointSize;
        nodeAvg.y /= pointSize;
        nodeAvg.z /= pointSize;
        
        for (int i = 0; i < holeData.size(); i++) {
            Node n = new Node(holeData.get(i).getX(), 
                    holeData.get(i).getY(),
                    holeData.get(i).getZ());
            if (holeList.contains(n)) {
                AlertBox.display("Error", "There are two same holes. \nHole table record #" + (i + 1));
                return;
            }
            holeList.add(n);
        }
        
        for (int i = 0; i < facetPolygonData.size(); i++) {
            verticesNumber = 0;
            pointList = new ArrayList();
            temp = facetPolygonData.get(i).getFacetNumber();
            
            CheckFacetPolygons cfp = new CheckFacetPolygons(temp, facetPolygonData.get(i).getPolygonNumber());
            if (checkFacetPolygonsList.contains(cfp)) {
                AlertBox.display("Error", "There are two same polygon numbers in facet. \nFacet table record #" + (i + 1));
                return;
            }
            checkFacetPolygonsList.add(cfp);
            
            CheckFacetPolygonVertices cfpv = new CheckFacetPolygonVertices(
                    facetPolygonData.get(i).getVertex1(),
                    facetPolygonData.get(i).getVertex2(),
                    facetPolygonData.get(i).getVertex3(),
                    facetPolygonData.get(i).getVertex4(),
                    facetPolygonData.get(i).getVertex5(),
                    facetPolygonData.get(i).getVertex6(),
                    facetPolygonData.get(i).getVertex7(),
                    facetPolygonData.get(i).getVertex8(),
                    facetPolygonData.get(i).getVertex9(),
                    facetPolygonData.get(i).getVertex10(),
                    facetPolygonData.get(i).getVertex11(),
                    facetPolygonData.get(i).getVertex12(),
                    facetPolygonData.get(i).getVertex13(),
                    facetPolygonData.get(i).getVertex14(),
                    facetPolygonData.get(i).getVertex15(),
                    facetPolygonData.get(i).getVertex16(),
                    facetPolygonData.get(i).getVertex17(),
                    facetPolygonData.get(i).getVertex18(),
                    facetPolygonData.get(i).getVertex19(),
                    facetPolygonData.get(i).getVertex20(),
                    facetPolygonData.get(i).getVertex21(),
                    facetPolygonData.get(i).getVertex22(),
                    facetPolygonData.get(i).getVertex23(),
                    facetPolygonData.get(i).getVertex24(),
                    facetPolygonData.get(i).getVertex25(),
                    facetPolygonData.get(i).getVertex26(),
                    facetPolygonData.get(i).getVertex27(),
                    facetPolygonData.get(i).getVertex28(),
                    facetPolygonData.get(i).getVertex29(),
                    facetPolygonData.get(i).getVertex30()
            );
            if (checkFacetPolygonVerticesList.contains(cfpv)) {
                AlertBox.display("Error", "There are two same polygons in facet. \nFacet table record #" + (i + 1));
                return;
            }
            checkFacetPolygonVerticesList.add(cfpv);
            
            int max = 0;
            for (int j = 0; j < shapeVertexSize; j++) {
                temp = facetPolygonData.get(i).getVertex(j + 1);
                if (temp == 0)
                    break;
                
                if (pointList.contains(temp)) {
                    AlertBox.display("Error", "Two points are referenced twice in one polygon. \nFacet table record #" + (i + 1));
                    return;
                }
                pointList.add(temp);
                
                if (temp > max)
                    max = temp;
                verticesNumber++;
                if (verticesNumber > maxNumberOfVertices)
                    maxNumberOfVertices = verticesNumber;
            }
            
            if (verticesNumber < 3) {
                AlertBox.display("Error", "There should be at least 3 points in a polygon. \nFacet table record #" + (i + 1));
                return;
            }
            
            if (max > pointSize) {
                AlertBox.display("Error", "There is no such point as a point #" + max + ". \nFacet table record #" + (i + 1));
                return;
            }
            
            temp = facetPolygonData.get(i).getFacetNumber();
            if (!facetList.contains(temp))
                facetList.add(temp);
        }
        if (facetList.size() < 4) {
            AlertBox.display("Error", "There should be at least 4 facets");
            return;
        }
        
        for (int i = 0; i < facetHoleData.size(); i++) {
            temp = facetHoleData.get(i).getFacetNumber();
            if (!facetList.contains(temp)) {
                AlertBox.display("Error", "There is no such facet as a facet #" + temp + ". \nFacet holes table record #" + (i + 1));
                return;
            }
        }
        
        double xRot, yRot, xTran, yTran, zTran;
        
        try {
            xRot = Double.parseDouble(xRotationField.getText());
            yRot = Double.parseDouble(yRotationField.getText());
            xTran = Double.parseDouble(xPositionField.getText());
            yTran = Double.parseDouble(yPositionField.getText());
            zTran = Double.parseDouble(zPositionField.getText());
        } catch (Exception ex) {
            AlertBox.display("Error", "Please enter valid numbers in general tab");
            return;
        }
        if (createButton.getText().equals("Update")) {
            /*if (!change.get())
                sm.updateShape(new Shape(id, nameField.getText(), xRot, yRot, xTran, yTran, zTran,
                    pointData, holeData, facetPolygonData, 
                    facetHoleData, facetList.size(), maxNumberOfVertices, 
                    diffuseColorPicker.getValue(), specularColorPicker.getValue(), -2, histionId, cellId), id);
            else*/
                sm.updateShape(new Shape(id, nameField.getText(), xRot, yRot, xTran, yTran, zTran,
                    pointData, holeData, facetPolygonData, 
                    facetHoleData, facetList.size(), maxNumberOfVertices, 
                    diffuseColorPicker.getValue(), specularColorPicker.getValue(), nodeAvg, -1, histionId, cellId), id);
                hm.getHistionMap().get(histionId).getItemMap().get(cellId).getItemMap().get(id).setXRotate(xRot);
                hm.getHistionMap().get(histionId).getItemMap().get(cellId).getItemMap().get(id).setYRotate(yRot);
                hm.getHistionMap().get(histionId).getItemMap().get(cellId).getItemMap().get(id).setXCoordinate(xTran);
                hm.getHistionMap().get(histionId).getItemMap().get(cellId).getItemMap().get(id).setYCoordinate(yTran);
                hm.getHistionMap().get(histionId).getItemMap().get(cellId).getItemMap().get(id).setZCoordinate(zTran);
            if (!hm.getHistionMap().get(histionId).getItemMap().get(cellId).getItemMap().get(id).getName().equals(nameField.getText()))
                hm.getHistionMap().get(histionId).getItemMap().get(cellId).getItemMap().get(id).setName("Part <" + nameField.getText() + ">");
        } else {
            sm.addShape(new Shape(nameField.getText(), xRot, yRot, xTran, yTran, zTran,
                pointData, holeData, facetPolygonData, 
                facetHoleData, facetList.size(), maxNumberOfVertices,
                diffuseColorPicker.getValue(), specularColorPicker.getValue(), nodeAvg, -1, histionId, cellId));
            //hm.getAllHistions().get(histionId).getItems().get(cellId).createAndAddChild("Part <" + nameField.getText() + ">");
            hm.getHistionMap().get(histionId).getItemMap().get(cellId).addChild(
                    new Part("Part <" + nameField.getText() + ">", xRot, yRot, xTran, yTran, zTran));
        }
        Stage stage = (Stage) createButton.getScene().getWindow();
        stage.close();
    }
}
