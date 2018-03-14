package org.spbu.histology.shape.information;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import org.openide.LifecycleManager;
import org.openide.util.Lookup;
import org.spbu.histology.model.Node;
import org.spbu.histology.model.Shape;
import org.spbu.histology.model.ShapeManager;

public class GeneralTabController implements Initializable {
    
    @FXML
    private GridPane gridPane;
    
    @FXML
    private TextField nameField;
    
    /*@FXML
    private TextField xRotationField;
    
    @FXML
    private TextField yRotationField;
    
    @FXML
    private TextField xPositionField;
    
    @FXML
    private TextField yPositionField;
    
    @FXML
    private TextField zPositionField;*/
    
    @FXML
    private ColorPicker diffuseColorPicker;
    
    @FXML
    private ColorPicker specularColorPicker;
    
    @FXML
    private Button createButton;
    
    private ShapeManager sm = null;
    
    private int shapeVertexSize = 30;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        sm = Lookup.getDefault().lookup(ShapeManager.class);
        if (sm == null) {
            LifecycleManager.getDefault().exit();
        }
        gridPane.setVgap(20);
        gridPane.setHgap(20);
        gridPane.setPadding(new Insets(20, 20, 20, 20));
        if (ShapeInformationInitialization.mode.equals("Edit"))
            doUpdate();
        createButton.disableProperty().bind(Bindings.isEmpty(nameField.textProperty()));
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
        int pointSize = PointTabController.data.size();
        if (pointSize < 3) {
            AlertBox.display("Error", "There should be at least 3 points");
            return;
        }
        for (int i = 0; i < pointSize; i++) {
            Node n = new Node(PointTabController.data.get(i).getX(), 
                    PointTabController.data.get(i).getY(),
                    PointTabController.data.get(i).getZ());
            if (nodeList.contains(n)) {
                AlertBox.display("Error", "There are two same points. \nPoint table record #" + (i + 1));
                return;
            }
            nodeList.add(n);
        }
        
        for (int i = 0; i < HoleTabController.data.size(); i++) {
            Node n = new Node(HoleTabController.data.get(i).getX(), 
                    HoleTabController.data.get(i).getY(),
                    HoleTabController.data.get(i).getZ());
            if (holeList.contains(n)) {
                AlertBox.display("Error", "There are two same holes. \nHole table record #" + (i + 1));
                return;
            }
            holeList.add(n);
        }
        
        for (int i = 0; i < PolygonsInFacetTabController.data.size(); i++) {
            verticesNumber = 0;
            pointList = new ArrayList();
            temp = PolygonsInFacetTabController.data.get(i).getFacetNumber();
            
            CheckFacetPolygons cfp = new CheckFacetPolygons(temp, PolygonsInFacetTabController.data.get(i).getPolygonNumber());
            if (checkFacetPolygonsList.contains(cfp)) {
                AlertBox.display("Error", "There are two same polygon numbers in facet. \nFacet table record #" + (i + 1));
                return;
            }
            checkFacetPolygonsList.add(cfp);
            
            CheckFacetPolygonVertices cfpv = new CheckFacetPolygonVertices(
                    PolygonsInFacetTabController.data.get(i).getVertex1(),
                    PolygonsInFacetTabController.data.get(i).getVertex2(),
                    PolygonsInFacetTabController.data.get(i).getVertex3(),
                    PolygonsInFacetTabController.data.get(i).getVertex4(),
                    PolygonsInFacetTabController.data.get(i).getVertex5(),
                    PolygonsInFacetTabController.data.get(i).getVertex6(),
                    PolygonsInFacetTabController.data.get(i).getVertex7(),
                    PolygonsInFacetTabController.data.get(i).getVertex8(),
                    PolygonsInFacetTabController.data.get(i).getVertex9(),
                    PolygonsInFacetTabController.data.get(i).getVertex10(),
                    PolygonsInFacetTabController.data.get(i).getVertex11(),
                    PolygonsInFacetTabController.data.get(i).getVertex12(),
                    PolygonsInFacetTabController.data.get(i).getVertex13(),
                    PolygonsInFacetTabController.data.get(i).getVertex14(),
                    PolygonsInFacetTabController.data.get(i).getVertex15(),
                    PolygonsInFacetTabController.data.get(i).getVertex16(),
                    PolygonsInFacetTabController.data.get(i).getVertex17(),
                    PolygonsInFacetTabController.data.get(i).getVertex18(),
                    PolygonsInFacetTabController.data.get(i).getVertex19(),
                    PolygonsInFacetTabController.data.get(i).getVertex20(),
                    PolygonsInFacetTabController.data.get(i).getVertex21(),
                    PolygonsInFacetTabController.data.get(i).getVertex22(),
                    PolygonsInFacetTabController.data.get(i).getVertex23(),
                    PolygonsInFacetTabController.data.get(i).getVertex24(),
                    PolygonsInFacetTabController.data.get(i).getVertex25(),
                    PolygonsInFacetTabController.data.get(i).getVertex26(),
                    PolygonsInFacetTabController.data.get(i).getVertex27(),
                    PolygonsInFacetTabController.data.get(i).getVertex28(),
                    PolygonsInFacetTabController.data.get(i).getVertex29(),
                    PolygonsInFacetTabController.data.get(i).getVertex30()
            );
            if (checkFacetPolygonVerticesList.contains(cfpv)) {
                AlertBox.display("Error", "There are two same polygons in facet. \nFacet table record #" + (i + 1));
                return;
            }
            checkFacetPolygonVerticesList.add(cfpv);
            System.out.println(checkFacetPolygonVerticesList);
            
            if (!facetList.contains(temp))
                facetList.add(temp);
            
            int max = 0;
            for (int j = 0; j < shapeVertexSize; j++) {
                temp = PolygonsInFacetTabController.data.get(i).getVertex(j + 1);
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
            
            temp = PolygonsInFacetTabController.data.get(i).getFacetNumber();
            if (!facetList.contains(temp))
                facetList.add(temp);
        }
        if (facetList.size() < 4) {
            AlertBox.display("Error", "There should be at least 4 facets");
            return;
        }
        
        for (int i = 0; i < HolesInFacetTabController.data.size(); i++) {
            temp = HolesInFacetTabController.data.get(i).getFacetNumber();
            if (!facetList.contains(temp)) {
                AlertBox.display("Error", "There is no such facet as a facet #" + temp + ". \nFacet holes table record #" + (i + 1));
                return;
            }
        }
        if (ShapeInformationInitialization.mode.equals("Edit"))
            sm.updateShape(new Shape(ShapeInformationInitialization.getShape().getId(), nameField.getText(), PointTabController.data, 
                HoleTabController.data, PolygonsInFacetTabController.data, 
                HolesInFacetTabController.data, facetList.size(), maxNumberOfVertices, 
                diffuseColorPicker.getValue(), specularColorPicker.getValue()),
                    ShapeInformationInitialization.getShape().getId());
        else
            sm.addShape(new Shape(nameField.getText(), PointTabController.data, 
                    HoleTabController.data, PolygonsInFacetTabController.data, 
                    HolesInFacetTabController.data, facetList.size(), maxNumberOfVertices,
                    diffuseColorPicker.getValue(), specularColorPicker.getValue()));
        ShapeInformationInitialization.stage.close();
    }
    
    public void doUpdate() {
        createButton.setText("Update");
        nameField.setText(ShapeInformationInitialization.getShape().getName());
        diffuseColorPicker.setValue(ShapeInformationInitialization.getShape().getDiffuseColor());
        specularColorPicker.setValue(ShapeInformationInitialization.getShape().getSpecularColor());
    }
}
