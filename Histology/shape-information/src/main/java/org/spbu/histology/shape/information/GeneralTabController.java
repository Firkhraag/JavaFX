package org.spbu.histology.shape.information;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Point3D;
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
import org.spbu.histology.model.Names;
import org.spbu.histology.model.Node;
import org.spbu.histology.model.Part;
import org.spbu.histology.model.ShapeManager;
import org.spbu.histology.model.TetgenFacet;
import org.spbu.histology.model.TetgenPoint;
import org.spbu.histology.model.TwoIntegers;
import org.spbu.histology.model.TwoPoints;

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
    
    //private ShapeManager sm = null;
    
    private HistionManager hm = null;
    
    //private final int shapeVertexSize = 30;
    
    private Integer cellId;
    String name;
    //private Integer histionId;

    //private ObservableList<TetgenFacet> facetData = FXCollections.observableArrayList();
    private ObservableList<ArrayList<Integer>> facetData = FXCollections.observableArrayList();
    private ObservableList<TetgenPoint> pointData = FXCollections.observableArrayList();
    ObservableList<TwoIntegers> lineList;
    
    BooleanProperty change;
    //IntegerProperty maxNumOfVertices;
    
    /*public void setShape(Shape s, BooleanProperty c) {
        change = c;
        id = s.getId();
        facetData = s.getFacetData();
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
    }*/
    
    public void setCell(Cell c, BooleanProperty change,
            ObservableList<TwoIntegers> lineData, ObservableList<TetgenPoint> pointData) {
        this.change = change;
        //this.maxNumOfVertices = maxNumOfVertices;
        cellId = c.getId();
        //facetData = c.getFacetData();
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
        //histionId = c.getHistionId();
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        /*sm = Lookup.getDefault().lookup(ShapeManager.class);
        if (sm == null) {
            LifecycleManager.getDefault().exit();
        }*/
        
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
            /*if ((list.containsAll(l)) && (l.containsAll(list))) {
                contains = true;
                break;
            }*/
            if ((list.containsAll(l)) || (l.containsAll(list))) {
                contains = true;
                break;
            }
        }
        if (!contains) {
            //list.remove(list.size() - 1);
            facetData.add(list);
        }
        /*Double[] polPoints = new Double[pl.size() * 2];
        int k = 0;
        for (int i = 0; i < pl.size(); i++) {
        polPoints[k] = pl.get(i).getX();
        polPoints[k + 1] = pl.get(i).getZ();
        k += 2;
        }*/
    }
    
    private void goThroughLines(ArrayList<TetgenPoint> planePointsList, int initialP, int p, int cur, ArrayList<Integer> pl) {
        int newP;
        ArrayList<Integer> newPl = new ArrayList<>(pl);
        
        for (int i = 0; i < lineList.size(); i++) {
            //System.out.println("^^^^^^^^^^^^^^^^^^^^^^^");
            if (i == cur) {
                continue;
            }
            if (p == lineList.get(i).getPoint1()) {
                if (onPlane(pointData.get(lineList.get(i).getPoint2() - 1))) {
                    if (pl.contains(lineList.get(i).getPoint2())) {
                        return;
                    }
                    
                    System.out.println("-");
                    System.out.println(lineList.get(i).getPoint1());
                    System.out.println(lineList.get(i).getPoint2());
                    
                    if ((Math.abs(planePointsList.get(0).getY() - pointData.get(lineList.get(i).getPoint2() - 1).getY()) > 0.0001) &&
                            (Math.abs(planePointsList.get(1).getY() - pointData.get(lineList.get(i).getPoint2() - 1).getY()) > 0.0001) &&
                            (Math.abs(planePointsList.get(2).getY() - pointData.get(lineList.get(i).getPoint2() - 1).getY()) > 0.0001)) {
                        continue;
                    }
                    
                    /*boolean good = true;
                    
                    Point3D p0 = new Point3D(planePointsList.get(1).getX(),
                            planePointsList.get(1).getY(),
                            planePointsList.get(1).getZ());
                    Point3D p1 = new Point3D(pointData.get(lineList.get(i).getPoint2() - 1).getX(),
                            pointData.get(lineList.get(i).getPoint2() - 1).getY(),
                            pointData.get(lineList.get(i).getPoint2() - 1).getZ());
                    for (int q = 0; q < lineList.size(); q++) {
                        if (q == cur)
                            continue;
                        if (q == i)
                            continue;
                        if (p == lineList.get(q).getPoint1()) {
                            System.out.println("True1");
                            System.out.println(lineList.get(q).getPoint1());
                            System.out.println(lineList.get(q).getPoint2());
                            if (onPlane(pointData.get(lineList.get(q).getPoint2() - 1))) {
                                //System.out.println("True2");
                                Point3D p2 = new Point3D(pointData.get(lineList.get(q).getPoint2() - 1).getX(),
                                        pointData.get(lineList.get(q).getPoint2() - 1).getY(),
                                        pointData.get(lineList.get(q).getPoint2() - 1).getZ());
                                if (p2.distance(p0) < p1.distance(p0)) {
                                    //System.out.println("Better " + q);
                                    System.out.println(p0.getX() + " " + p0.getY() + " " + p0.getZ());
                                    System.out.println(p1.getX() + " " + p1.getY() + " " + p1.getZ());
                                    System.out.println(p2.getX() + " " + p2.getY() + " " + p2.getZ());
                                    good = false;
                                    break;
                                }
                            }
                        } else if (p == lineList.get(q).getPoint2()) {
                            System.out.println("True1");
                            System.out.println(lineList.get(q).getPoint1());
                            System.out.println(lineList.get(q).getPoint2());
                            if (onPlane(pointData.get(lineList.get(q).getPoint1() - 1))) {
                                //System.out.println("True2");
                                Point3D p2 = new Point3D(pointData.get(lineList.get(q).getPoint1() - 1).getX(),
                                        pointData.get(lineList.get(q).getPoint1() - 1).getY(),
                                        pointData.get(lineList.get(q).getPoint1() - 1).getZ());
                                if (p2.distance(p0) < p1.distance(p0)) {
                                    //System.out.println("Better " + q);
                                    //return;
                                    System.out.println(p0.getX() + " " + p0.getY() + " " + p0.getZ());
                                    System.out.println(p1.getX() + " " + p1.getY() + " " + p1.getZ());
                                    System.out.println(p2.getX() + " " + p2.getY() + " " + p2.getZ());
                                    good = false;
                                    break;
                                }
                            }
                        }
                    }
                    
                    if (!good)
                        continue;*/

                    System.out.println("--3--");
                    
                    newPl.add(lineList.get(i).getPoint2());
                    newP = lineList.get(i).getPoint2();
                    if (newP == initialP) {
                        addFacet(newPl);
                        //pl.clear();
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
                    
                    System.out.println("-");
                    System.out.println(lineList.get(i).getPoint1());
                    System.out.println(lineList.get(i).getPoint2());
                    
                    if ((Math.abs(planePointsList.get(0).getY() - pointData.get(lineList.get(i).getPoint1() - 1).getY()) > 0.0001) &&
                            (Math.abs(planePointsList.get(1).getY() - pointData.get(lineList.get(i).getPoint1() - 1).getY()) > 0.0001) &&
                            (Math.abs(planePointsList.get(2).getY() - pointData.get(lineList.get(i).getPoint1() - 1).getY()) > 0.0001)) {
                        continue;
                    }
                    
                    /*boolean good = true;
                    
                    Point3D p0 = new Point3D(planePointsList.get(1).getX(),
                            planePointsList.get(1).getY(),
                            planePointsList.get(1).getZ());
                    Point3D p1 = new Point3D(pointData.get(lineList.get(i).getPoint1() - 1).getX(),
                            pointData.get(lineList.get(i).getPoint1() - 1).getY(),
                            pointData.get(lineList.get(i).getPoint1() - 1).getZ());
                    for (int q = 0; q < lineList.size(); q++) {
                        if (q == cur)
                            continue;
                        if (q == i)
                            continue;
                        if (p == lineList.get(q).getPoint1()) {
                            System.out.println("True");
                            System.out.println(lineList.get(q).getPoint1());
                            System.out.println(lineList.get(q).getPoint2());
                            if (onPlane(pointData.get(lineList.get(q).getPoint2() - 1))) {
                                //System.out.println("True2");
                                Point3D p2 = new Point3D(pointData.get(lineList.get(q).getPoint2() - 1).getX(),
                                        pointData.get(lineList.get(q).getPoint2() - 1).getY(),
                                        pointData.get(lineList.get(q).getPoint2() - 1).getZ());
                                if (p2.distance(p0) < p1.distance(p0)) {
                                    //System.out.println("Better " + q);
                                    //return;
                                    System.out.println(p0.getX() + " " + p0.getY() + " " + p0.getZ());
                                    System.out.println(p1.getX() + " " + p1.getY() + " " + p1.getZ());
                                    System.out.println(p2.getX() + " " + p2.getY() + " " + p2.getZ());
                                    good = false;
                                    break;
                                }
                            }
                        } else if (p == lineList.get(q).getPoint2()) {
                            System.out.println("True");
                            System.out.println(lineList.get(q).getPoint1());
                            System.out.println(lineList.get(q).getPoint2());
                            if (onPlane(pointData.get(lineList.get(q).getPoint1() - 1))) {
                                //System.out.println("True2");
                                Point3D p2 = new Point3D(pointData.get(lineList.get(q).getPoint1() - 1).getX(),
                                        pointData.get(lineList.get(q).getPoint1() - 1).getY(),
                                        pointData.get(lineList.get(q).getPoint1() - 1).getZ());
                                if (p2.distance(p0) < p1.distance(p0)) {
                                    //System.out.println("Better " + q);
                                    //return;
                                    System.out.println(p0.getX() + " " + p0.getY() + " " + p0.getZ());
                                    System.out.println(p1.getX() + " " + p1.getY() + " " + p1.getZ());
                                    System.out.println(p2.getX() + " " + p2.getY() + " " + p2.getZ());
                                    good = false;
                                    break;
                                }
                            }
                        }
                    }
                    
                    if (!good)
                        continue;*/
                    
                    System.out.println("--4--");
                    newPl.add(lineList.get(i).getPoint1());
                    newP = lineList.get(i).getPoint1();
                    if (newP == initialP) {
                        addFacet(newPl);
                        //pl.clear();
                        return;
                    }
                    goThroughLines(planePointsList, initialP, newP, i, newPl);
                    newPl.remove(newPl.size() - 1);
                }
            }
        }
    }
    
    /*private boolean sameLine(TetgenPoint p1, TetgenPoint p2, TetgenPoint p3) {
        Math.round(p1.getX() * (p2.getY() - Cy) + Bx * (Cy - p1.getY()) + Cx * (p1.getY() - By)) / 2;
    }*/
    
    private void findFacets() {
        int p, initialP;
        ArrayList<Integer> pl = new ArrayList<>();
        ArrayList<TetgenPoint> planePointsList = new ArrayList<>();
        
        int cur;
        for (int j = 0; j < lineList.size(); j++) {
            System.out.println("**********************");
            System.out.println(lineList.get(j).getPoint2());
            p = lineList.get(j).getPoint1();
            planePointsList.add(pointData.get(p - 1));
            planePointsList.add(pointData.get(lineList.get(j).getPoint2() - 1));
            initialP = lineList.get(j).getPoint1();
            cur = j;
            for (int i = 0; i < lineList.size(); i++) {
                System.out.println("------------------------");
                if (i == cur)
                    continue;
                if (p == lineList.get(i).getPoint1()) {
                    
                    /*boolean good = true;
                    
                    Point3D p0 = new Point3D(planePointsList.get(1).getX(),
                            planePointsList.get(1).getY(),
                            planePointsList.get(1).getZ());
                    Point3D p1 = new Point3D(planePointsList.get(0).getX(),
                            planePointsList.get(0).getY(),
                            planePointsList.get(0).getZ());
                    for (int q = 0; q < lineList.size(); q++) {
                        if (q == cur)
                            continue;
                        if (q == i)
                            continue;
                        if (p == lineList.get(q).getPoint1()) {
                            /*System.out.println("True1");
                            System.out.println(lineList.get(q).getPoint1());
                            System.out.println(lineList.get(q).getPoint2());*/
                            /*if (onPlane(pointData.get(lineList.get(q).getPoint2() - 1))) {
                                //System.out.println("True2");
                                Point3D p2 = new Point3D(pointData.get(lineList.get(q).getPoint2() - 1).getX(),
                                        pointData.get(lineList.get(q).getPoint2() - 1).getY(),
                                        pointData.get(lineList.get(q).getPoint2() - 1).getZ());
                                if (p2.distance(p0) < p1.distance(p0)) {
                                    //System.out.println("Better " + q);
                                    System.out.println(p0.getX() + " " + p0.getY() + " " + p0.getZ());
                                    System.out.println(p1.getX() + " " + p1.getY() + " " + p1.getZ());
                                    System.out.println(p2.getX() + " " + p2.getY() + " " + p2.getZ());
                                    good = false;
                                    break;
                                }
                            }
                        } else if (p == lineList.get(q).getPoint2()) {
                            System.out.println("True1");
                            System.out.println(lineList.get(q).getPoint1());
                            System.out.println(lineList.get(q).getPoint2());
                            if (onPlane(pointData.get(lineList.get(q).getPoint1() - 1))) {
                                //System.out.println("True2");
                                Point3D p2 = new Point3D(pointData.get(lineList.get(q).getPoint1() - 1).getX(),
                                        pointData.get(lineList.get(q).getPoint1() - 1).getY(),
                                        pointData.get(lineList.get(q).getPoint1() - 1).getZ());
                                if (p2.distance(p0) < p1.distance(p0)) {
                                    //System.out.println("Better " + q);
                                    //return;
                                    System.out.println(p0.getX() + " " + p0.getY() + " " + p0.getZ());
                                    System.out.println(p1.getX() + " " + p1.getY() + " " + p1.getZ());
                                    System.out.println(p2.getX() + " " + p2.getY() + " " + p2.getZ());
                                    good = false;
                                    break;
                                }
                            }
                        }
                    }
                    
                    if (!good)
                        continue;*/
                    
                    planePointsList.add(pointData.get(lineList.get(i).getPoint2() - 1));
                    if ((Math.abs(planePointsList.get(0).getY() - planePointsList.get(1).getY()) > 0.0001) &&
                            (Math.abs(planePointsList.get(0).getY() - planePointsList.get(2).getY()) > 0.0001) &&
                            (Math.abs(planePointsList.get(1).getY() - planePointsList.get(2).getY()) > 0.0001)) {
                        planePointsList.remove(2);
                        continue;
                    }
                    findPlane(planePointsList.get(0), planePointsList.get(1), planePointsList.get(2));
                    System.out.println("A " + A + "B " + B + "C " + C + "D " + D);
                    if ((Math.abs(A) < 0.0001)
                            && (Math.abs(B) < 0.0001)
                            && (Math.abs(C) < 0.0001)
                            && (Math.abs(D) < 0.0001)) {
                        planePointsList.remove(2);
                        continue;
                    }
                    //next = false;
                    pl.add(lineList.get(i).getPoint2());
                    //p = lineList.get(i).getPoint2();
                    //cur = i;
                    System.out.println("--1--");
                    System.out.println(lineList.get(i).getPoint1());
                    System.out.println(lineList.get(i).getPoint2());
                    goThroughLines(planePointsList, initialP, lineList.get(i).getPoint2(), i, pl);
                    planePointsList.remove(2);
                    pl.clear();
                    /*while (!next) {
                        next = true;
                        for (int q = 0; q < lineList.size(); q++) {
                            
                        }
                    }*/
                } else if (p == lineList.get(i).getPoint2()) {
                    planePointsList.add(pointData.get(lineList.get(i).getPoint1() - 1));
                    if ((Math.abs(planePointsList.get(0).getY() - planePointsList.get(1).getY()) > 0.0001) &&
                            (Math.abs(planePointsList.get(0).getY() - planePointsList.get(2).getY()) > 0.0001) &&
                            (Math.abs(planePointsList.get(1).getY() - planePointsList.get(2).getY()) > 0.0001)) {
                        planePointsList.remove(2);
                        continue;
                    }
                    findPlane(planePointsList.get(0), planePointsList.get(1), planePointsList.get(2));
                    System.out.println("A " + A + "B " + B + "C " + C + "D " + D);
                    if ((Math.abs(A) < 0.0001)
                            && (Math.abs(B) < 0.0001)
                            && (Math.abs(C) < 0.0001)
                            && (Math.abs(D) < 0.0001)) {
                        planePointsList.remove(2);
                        continue;
                    }
                    pl.add(lineList.get(i).getPoint1());
                    //p = lineList.get(i).getPoint1();
                    //cur = i;
                    System.out.println("--2--");
                    System.out.println(lineList.get(i).getPoint1());
                    System.out.println(lineList.get(i).getPoint2());
                    goThroughLines(planePointsList, initialP, lineList.get(i).getPoint1(), i, pl);
                    planePointsList.remove(2);
                    pl.clear();
                }
            }
            planePointsList.clear();
            pl.clear();
        }
    }
    
    private void findFacets2() {
        int p, initialP;
        ArrayList<TwoIntegers> marked = new ArrayList<>();
        ArrayList<Integer> pl = new ArrayList<>();
        ArrayList<TetgenPoint> planePointsList = new ArrayList<>();
        
        boolean next;
        int cur;
        for (int j = 0; j < lineList.size(); j++) {
            /*System.out.println("*");
            System.out.println(j);
            System.out.println("*");*/
            next = false;
            //pl.add(lineList.get(j).getPoint1());
            p = lineList.get(j).getPoint1();
            planePointsList.add(pointData.get(p - 1));
            planePointsList.add(pointData.get(lineList.get(j).getPoint2() - 1));
            initialP = lineList.get(j).getPoint1();
            System.out.println("*********************");
            System.out.println(initialP);
            cur = j;
            while (!next) {
                next = true;
                //System.out.println(p.getX() + " " + p.getZ());
                //stop = true;
                for (int i = 0; i < lineList.size(); i++) {
                    //System.out.println("*");
                    //System.out.println(i);
                    //System.out.println(cur);
                    if (i == cur)
                        continue;
                    if (p == lineList.get(i).getPoint1()) {
                        if (planePointsList.size() < 3) {
                            if (!marked.contains(lineList.get(i))) {
                                System.out.println("--1--");
                                System.out.println(lineList.get(i).getPoint1());
                                System.out.println(lineList.get(i).getPoint2());
                                planePointsList.add(pointData.get(lineList.get(i).getPoint2() - 1));
                                findPlane(planePointsList.get(0), planePointsList.get(1), planePointsList.get(2));
                                if ((Math.abs(A) < 0.0001) &&
                                        (Math.abs(B) < 0.0001) &&
                                        (Math.abs(C) < 0.0001) &&
                                        (Math.abs(D) < 0.0001)) {
                                    planePointsList.remove(2);
                                    continue;
                                }
                                next = false;
                                //marked.add(lineList.get(i));
                                pl.add(lineList.get(i).getPoint2());
                                p = lineList.get(i).getPoint2();
                                cur = i;
                            }
                        } else {
                            if (onPlane(pointData.get(lineList.get(i).getPoint2() - 1))) {
                                if (pl.contains(lineList.get(i).getPoint2()))
                                    continue;
                                /*for (int q = i + 1; q < lineList.size(); q++) {
                                    //if (q == i)
                                    //    continue;
                                    if (p == lineList.get(q).getPoint1()) {
                                        if (onPlane(pointData.get(lineList.get(q).getPoint2() - 1))) {
                                        }
                                    } else if (p == lineList.get(q).getPoint2()) {
                                        if (onPlane(pointData.get(lineList.get(q).getPoint1() - 1))) {
                                        }
                                    }
                                }*/
                                /*boolean good = true;
                                Point3D p0 = new Point3D(planePointsList.get(1).getX(),
                                        planePointsList.get(1).getY(),
                                        planePointsList.get(1).getZ());
                                Point3D p1 = new Point3D(pointData.get(lineList.get(i).getPoint2() - 1).getX(),
                                    pointData.get(lineList.get(i).getPoint2() - 1).getY(),
                                    pointData.get(lineList.get(i).getPoint2() - 1).getZ());
                                for (int q =  i + 1; q < lineList.size(); q++) {
                                    if (p == lineList.get(q).getPoint1()) {
                                        System.out.println("True1");
                                        if (onPlane(pointData.get(lineList.get(q).getPoint2() - 1))) {
                                            System.out.println("True2");
                                            Point3D p2 = new Point3D(pointData.get(lineList.get(q).getPoint2() - 1).getX(),
                                                pointData.get(lineList.get(q).getPoint2() - 1).getY(),
                                                pointData.get(lineList.get(q).getPoint2() - 1).getZ());
                                            if (p2.distance(p0) < p1.distance(p0)) {
                                                System.out.println("Better " + q);
                                                good = false;
                                                break;
                                            }
                                        }
                                    } else if (p == lineList.get(q).getPoint2()) {
                                        System.out.println("True1");
                                        if (onPlane(pointData.get(lineList.get(q).getPoint1() - 1))) {
                                            System.out.println("True2");
                                            Point3D p2 = new Point3D(pointData.get(lineList.get(q).getPoint1() - 1).getX(),
                                                pointData.get(lineList.get(q).getPoint1() - 1).getY(),
                                                pointData.get(lineList.get(q).getPoint1() - 1).getZ());
                                            if (p2.distance(p0) < p1.distance(p0)) {
                                                System.out.println("Better " + q);
                                                good = false;
                                                break;
                                            }
                                        }
                                    }
                                }
                                if (!good)
                                    continue;*/
                                System.out.println("--2--");
                                System.out.println(lineList.get(i).getPoint1());
                                System.out.println(lineList.get(i).getPoint2());
                                next = false;
                                /*if (!marked.contains(lineList.get(i)))
                                    marked.add(lineList.get(i));*/
                                pl.add(lineList.get(i).getPoint2());
                                p = lineList.get(i).getPoint2();
                                cur = i;
                                if (p == initialP)  {
                                    planePointsList.remove(2);
                                    addFacet(pl);
                                    pl.clear();
                                    break;
                                }
                            }
                        }
                    }
                    else if (p == lineList.get(i).getPoint2()) {
                        if (planePointsList.size() < 3) {
                            if (!marked.contains(lineList.get(i))) {
                                System.out.println("--3--");
                                System.out.println(lineList.get(i).getPoint1());
                                System.out.println(lineList.get(i).getPoint2());
                                planePointsList.add(pointData.get(lineList.get(i).getPoint1() - 1));
                                findPlane(planePointsList.get(0), planePointsList.get(1), planePointsList.get(2));
                                if ((Math.abs(A) < 0.0001) &&
                                        (Math.abs(B) < 0.0001) &&
                                        (Math.abs(C) < 0.0001) &&
                                        (Math.abs(D) < 0.0001)) {
                                    planePointsList.remove(2);
                                    continue;
                                }
                                next = false;
                                //marked.add(lineList.get(i));
                                pl.add(lineList.get(i).getPoint1());
                                p = lineList.get(i).getPoint1();
                                cur = i;
                            }
                        } else {
                            if (onPlane(pointData.get(lineList.get(i).getPoint1() - 1))) {
                                if (pl.contains(lineList.get(i).getPoint1()))
                                    continue;
                                /*boolean good = true;
                                Point3D p0 = new Point3D(planePointsList.get(1).getX(),
                                        planePointsList.get(1).getY(),
                                        planePointsList.get(1).getZ());
                                Point3D p1 = new Point3D(pointData.get(lineList.get(i).getPoint1() - 1).getX(),
                                    pointData.get(lineList.get(i).getPoint1() - 1).getY(),
                                    pointData.get(lineList.get(i).getPoint1() - 1).getZ());
                                for (int q =  i + 1; q < lineList.size(); q++) {
                                    System.out.println("True1");
                                    if (p == lineList.get(q).getPoint1()) {
                                        if (onPlane(pointData.get(lineList.get(q).getPoint2() - 1))) {
                                            System.out.println("True2");
                                            Point3D p2 = new Point3D(pointData.get(lineList.get(q).getPoint2() - 1).getX(),
                                                pointData.get(lineList.get(q).getPoint2() - 1).getY(),
                                                pointData.get(lineList.get(q).getPoint2() - 1).getZ());
                                            if (p2.distance(p0) < p1.distance(p0)) {
                                                System.out.println("Better " + q);
                                                good = false;
                                                break;
                                            }
                                        }
                                    } else if (p == lineList.get(q).getPoint2()) {
                                        System.out.println("True1");
                                        if (onPlane(pointData.get(lineList.get(q).getPoint1() - 1))) {
                                            System.out.println("True2");
                                            Point3D p2 = new Point3D(pointData.get(lineList.get(q).getPoint1() - 1).getX(),
                                                pointData.get(lineList.get(q).getPoint1() - 1).getY(),
                                                pointData.get(lineList.get(q).getPoint1() - 1).getZ());
                                            if (p2.distance(p0) < p1.distance(p0)) {
                                                System.out.println("Better " + q);
                                                good = false;
                                                break;
                                            }
                                        }
                                    }
                                }
                                if (!good)
                                    continue;*/
                                System.out.println("--4--");
                                System.out.println(lineList.get(i).getPoint1());
                                System.out.println(lineList.get(i).getPoint2());
                                next = false;
                                /*if (!marked.contains(lineList.get(i)))
                                    marked.add(lineList.get(i));*/
                                pl.add(lineList.get(i).getPoint1());
                                p = lineList.get(i).getPoint1();
                                cur = i;
                                if (p == initialP)  {
                                    planePointsList.remove(2);
                                    addFacet(pl);
                                    pl.clear();
                                    break;
                                }
                            }
                        }
                    }
                }
            }
            marked.clear();
            planePointsList.clear();
            pl.clear();
        }
    }
    
    @FXML
    private void buttonAction() {
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
        findFacets();
        for (ArrayList<Integer> ar : facetData) {
            System.out.println(ar);
        }
        Names.removeCellName(name);
        if (Names.containsCellName(nameField.getText())) {
            Names.addCellName(name);
            AlertBox.display("Error", "This name is already used");
        } else {
            Cell c = new Cell(cellId, "Cell <" + nameField.getText() + ">",
                    xRot, yRot, xTran, yTran, zTran, facetData,
                    diffuseColorPicker.getValue(), specularColorPicker.getValue(), 0, true);
            hm.getHistionMap().get(0).getItemMap().get(cellId).getItems().forEach(p -> {
                //System.out.println(p.getId() + " " + p.getName());
                //c.addChild(new Part(p));
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
            Stage stage = (Stage) createButton.getScene().getWindow();
            stage.close();
        }
    }
}
