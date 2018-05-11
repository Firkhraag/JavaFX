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
import org.spbu.histology.model.AlertBox;
import org.spbu.histology.model.Cell;
import org.spbu.histology.model.HistionManager;
import org.spbu.histology.model.Line;
import org.spbu.histology.model.LineEquations;
import org.spbu.histology.model.Names;
import org.spbu.histology.model.Node;
import org.spbu.histology.model.TetgenPoint;
import org.spbu.histology.model.TwoIntegers;

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
    
    private HistionManager hm = null;
    
    private Integer cellId;
    String name;

    private ObservableList<ArrayList<Integer>> facetData = FXCollections.observableArrayList();
    private ObservableList<TetgenPoint> pointData = FXCollections.observableArrayList();
    private ObservableList<TwoIntegers> lineList;
    
    //private ObservableList<TwoIntegers> data;
    
    private BooleanProperty change;
    
    public void setCell(Cell c, BooleanProperty change,
            ObservableList<TwoIntegers> lineData, ObservableList<TetgenPoint> pointData) {
        this.change = change;
        cellId = c.getId();
        this.pointData = pointData;
        lineList = lineData;
        name = c.getName();
        name = name.substring(name.indexOf("<") + 1, name.lastIndexOf(">"));
        nameField.setText(name);
        xRotationField.setText(String.valueOf(c.getXRotate()));
        yRotationField.setText(String.valueOf(c.getYRotate()));
        xPositionField.setText(String.valueOf(c.getXCoordinate()));
        yPositionField.setText(String.valueOf(c.getYCoordinate()));
        zPositionField.setText(String.valueOf(c.getZCoordinate()));
        diffuseColorPicker.setValue(c.getDiffuseColor());
        specularColorPicker.setValue(c.getSpecularColor());
    }
    
    /*public void setData(ObservableList<TwoIntegers> data) {
        this.data = data;
    }*/
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
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
    
    double A, B, C, D;
    
    private boolean onPlane(TetgenPoint p) {
        if (Math.abs(A * p.getX() + B * p.getY() + C * p.getZ() + D) < 0.0001)
            return true;
        return false;
    }
    
    private void findPlane(TetgenPoint p1, TetgenPoint p2, TetgenPoint p3) {
        A = ((p2.getY() - p1.getY()) * (p3.getZ() - p1.getZ()) - (p3.getY() - p1.getY()) * (p2.getZ() - p1.getZ()));
        B = -((p2.getX() - p1.getX()) * (p3.getZ() - p1.getZ()) - (p3.getX() - p1.getX()) * (p2.getZ() - p1.getZ()));
        C = ((p2.getX() - p1.getX()) * (p3.getY() - p1.getY()) - (p3.getX() - p1.getX()) * (p2.getY() - p1.getY()));
        D = -p1.getX() * A - p1.getY() * B - p1.getZ() * C;
    }
    
    private void addFacet(ArrayList<Integer> pl) {
        System.out.println("Add facet");
        System.out.println(pl);
        ArrayList<Integer> list = new ArrayList<>();
        for (Integer num : pl) {
            list.add(num);
        }
        boolean contains = false;
        for (ArrayList l : facetData) {
            if ((list.containsAll(l)) || (l.containsAll(list))) {
                contains = true;
                break;
            }
        }
        if (!contains) {
            facetData.add(list);
        }
    }
    
    private void goThroughLines(ArrayList<TetgenPoint> planePointsList, int initialP, int p, int cur, ArrayList<Integer> pl) {
        int newP;
        ArrayList<Integer> newPl = new ArrayList<>(pl);
        
        for (int i = 0; i < lineList.size(); i++) {
            if (i == cur) {
                continue;
            }
            if (p == lineList.get(i).getPoint1()) {
                if (onPlane(pointData.get(lineList.get(i).getPoint2() - 1))) {
                    if (pl.contains(lineList.get(i).getPoint2())) {
                        return;
                    }
                    
                    if ((Math.abs(planePointsList.get(0).getY() - pointData.get(lineList.get(i).getPoint2() - 1).getY()) > 0.0001) &&
                            (Math.abs(planePointsList.get(1).getY() - pointData.get(lineList.get(i).getPoint2() - 1).getY()) > 0.0001) &&
                            (Math.abs(planePointsList.get(2).getY() - pointData.get(lineList.get(i).getPoint2() - 1).getY()) > 0.0001)) {
                        continue;
                    }
                    
                    newPl.add(lineList.get(i).getPoint2());
                    newP = lineList.get(i).getPoint2();
                    if (newP == initialP) {
                        addFacet(newPl);
                        return;
                    }
                    goThroughLines(planePointsList, initialP, newP, i, newPl);
                    newPl.remove(newPl.size() - 1);
                }
            } else if (p == lineList.get(i).getPoint2()) {
                if (onPlane(pointData.get(lineList.get(i).getPoint1() - 1))) {
                    if (pl.contains(lineList.get(i).getPoint1())) {
                        return;
                    }
                    
                    if ((Math.abs(planePointsList.get(0).getY() - pointData.get(lineList.get(i).getPoint1() - 1).getY()) > 0.0001) &&
                            (Math.abs(planePointsList.get(1).getY() - pointData.get(lineList.get(i).getPoint1() - 1).getY()) > 0.0001) &&
                            (Math.abs(planePointsList.get(2).getY() - pointData.get(lineList.get(i).getPoint1() - 1).getY()) > 0.0001)) {
                        continue;
                    }
                    
                    newPl.add(lineList.get(i).getPoint1());
                    newP = lineList.get(i).getPoint1();
                    if (newP == initialP) {
                        addFacet(newPl);
                        return;
                    }
                    goThroughLines(planePointsList, initialP, newP, i, newPl);
                    newPl.remove(newPl.size() - 1);
                }
            }
        }
    }
    
    private void findFacets(ArrayList<Line> lines) {
        int p, initialP;
        ArrayList<Integer> pl = new ArrayList<>();
        ArrayList<TetgenPoint> planePointsList = new ArrayList<>();
        //ArrayList<Line> lines = new ArrayList<>();
        
        int cur;
        for (int j = 0; j < lineList.size(); j++) {
   
            TetgenPoint point1 = pointData.get(lineList.get(j).getPoint1() - 1);
            TetgenPoint point2 = pointData.get(lineList.get(j).getPoint2() - 1);
            if (Math.abs(point1.getY() - point2.getY()) < 0.0001) {
                lines.add(new Line(new Node(point1.getX(), point1.getZ(), point1.getY()),
                        new Node(point2.getX(), point2.getZ(), point2.getY())));
            }
                        /*for (int j = i + 1; j < pl.size(); j++) {
                            TetgenPoint point2 = pl.get(j);
                            if (Math.abs(point1.getY() - point2.getY()) < 0.0001) {
                                lines.add(new Line(new Node(point1.getX(), point1.getZ(), point1.getY()),
                                        new Node(point2.getX(), point2.getZ(), point2.getY())));
                            }
                        }*/
            
            p = lineList.get(j).getPoint1();
            planePointsList.add(pointData.get(p - 1));
            planePointsList.add(pointData.get(lineList.get(j).getPoint2() - 1));
            initialP = lineList.get(j).getPoint1();
            cur = j;
            for (int i = 0; i < lineList.size(); i++) {
                if (i == cur)
                    continue;
                if (p == lineList.get(i).getPoint1()) {
                    
                    planePointsList.add(pointData.get(lineList.get(i).getPoint2() - 1));
                    if ((Math.abs(planePointsList.get(0).getY() - planePointsList.get(1).getY()) > 0.0001) &&
                            (Math.abs(planePointsList.get(0).getY() - planePointsList.get(2).getY()) > 0.0001) &&
                            (Math.abs(planePointsList.get(1).getY() - planePointsList.get(2).getY()) > 0.0001)) {
                        planePointsList.remove(2);
                        continue;
                    }
                    findPlane(planePointsList.get(0), planePointsList.get(1), planePointsList.get(2));
                    if ((Math.abs(A) < 0.0001)
                            && (Math.abs(B) < 0.0001)
                            && (Math.abs(C) < 0.0001)
                            && (Math.abs(D) < 0.0001)) {
                        planePointsList.remove(2);
                        continue;
                    }
                    pl.add(lineList.get(i).getPoint2());
                    goThroughLines(planePointsList, initialP, lineList.get(i).getPoint2(), i, pl);
                    planePointsList.remove(2);
                    pl.clear();
                } else if (p == lineList.get(i).getPoint2()) {
                    planePointsList.add(pointData.get(lineList.get(i).getPoint1() - 1));
                    if ((Math.abs(planePointsList.get(0).getY() - planePointsList.get(1).getY()) > 0.0001) &&
                            (Math.abs(planePointsList.get(0).getY() - planePointsList.get(2).getY()) > 0.0001) &&
                            (Math.abs(planePointsList.get(1).getY() - planePointsList.get(2).getY()) > 0.0001)) {
                        planePointsList.remove(2);
                        continue;
                    }
                    findPlane(planePointsList.get(0), planePointsList.get(1), planePointsList.get(2));
                    if ((Math.abs(A) < 0.0001)
                            && (Math.abs(B) < 0.0001)
                            && (Math.abs(C) < 0.0001)
                            && (Math.abs(D) < 0.0001)) {
                        planePointsList.remove(2);
                        continue;
                    }
                    pl.add(lineList.get(i).getPoint1());
                    goThroughLines(planePointsList, initialP, lineList.get(i).getPoint1(), i, pl);
                    planePointsList.remove(2);
                    pl.clear();
                }
            }
            planePointsList.clear();
            pl.clear();
        }
    }
    
    @FXML
    private void buttonAction() {
        double xRot, yRot, xTran, yTran, zTran;
        ArrayList<Line> lines = new ArrayList<>();
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
        Names.removeCellName(name);
        if (Names.containsCellName(nameField.getText())) {
            Names.addCellName(name);
            AlertBox.display("Error", "This name is already used");
        } else {
            findFacets(lines);
            for (ArrayList<Integer> ar : facetData) {
                System.out.println(ar);
            }
            Cell c = new Cell(cellId, "Cell <" + nameField.getText() + ">",
                    xRot, yRot, xTran, yTran, zTran, facetData, lineList,
                    diffuseColorPicker.getValue(), specularColorPicker.getValue(), 0, true);
            
            LineEquations.addLine(c.getId(), lines);
            
            hm.getHistionMap().get(0).getItemMap().get(cellId).getItems().forEach(p -> {
                System.out.println(p.getId() + " " + p.getName());
                c.addChild(p);
            });
            hm.getHistionMap().get(0).addChild(c);
            hm.getHistionMap().get(0).getItems().forEach(cell -> {
                Cell newCell = new Cell(cell.getId(), cell);
                cell.getItems().forEach(p -> {
                    newCell.addChild(p);
                });
                hm.getHistionMap().get(0).addChild(newCell);
            });
            Names.addCellName(nameField.getText());
            
            for (TwoIntegers ti : lineList) {
                pointData.get(ti.getPoint2() - 1);
                pointData.get(ti.getPoint1() - 1);
            }
            
            
            Stage stage = (Stage) createButton.getScene().getWindow();
            stage.close();
        }
    }
}
