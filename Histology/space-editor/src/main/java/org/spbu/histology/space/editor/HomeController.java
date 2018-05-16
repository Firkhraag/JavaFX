package org.spbu.histology.space.editor;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import org.spbu.histology.shape.information.CellInformationInitialization;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.geometry.Point3D;
import javafx.scene.control.CheckMenuItem;
import javafx.util.Callback;
import org.openide.LifecycleManager;
import org.openide.util.Lookup;
import org.spbu.histology.model.Cell;
import org.spbu.histology.model.HideCells;
import org.spbu.histology.model.Histion;
import org.spbu.histology.model.HistionManager;
import org.spbu.histology.model.HistologyObject;
import org.spbu.histology.model.Line;
import org.spbu.histology.model.LineEquations;
import org.spbu.histology.model.Names;
import org.spbu.histology.model.Node;
import org.spbu.histology.model.Part;
import org.spbu.histology.model.TetgenPoint;
import org.spbu.histology.shape.information.PartInformationInitialization;
import org.spbu.histology.shape.information.HistionInformationInitialization;

public class HomeController implements Initializable {
    
    private HistionManager hm = null;
    
    @FXML
    private TreeView<HistologyObject<?>> shapeTreeView;
    
    private Integer cellId;
    private Integer partId;
    private final BooleanProperty pasteCellDisabledProperty = new SimpleBooleanProperty(true);
    private final BooleanProperty pastePartDisabledProperty = new SimpleBooleanProperty(true);
    private final BooleanProperty disableEverything = new SimpleBooleanProperty(false);
    
    int dataSize;
    Point3D nodeAvg;
        
    HistionTreeItem histion;
    private final ObservableMap<Integer, CellTreeItem> cellTreeItemMap = 
            FXCollections.observableMap(new ConcurrentHashMap());
    private final ObservableMap<Integer, PartTreeItem> partTreeItemMap = 
            FXCollections.observableMap(new ConcurrentHashMap());
    
    private final MapChangeListener<Integer, Part> partListener
            = (change) -> {
                if (change.wasRemoved() && change.wasAdded()) {
                    Part p = (Part) change.getValueAdded();
                    Part removedPart = (Part) change.getValueRemoved();
                    if (!(p.getName().equals(removedPart.getName()))) {
                        partTreeItemMap.get(p.getId()).getValue().setName(p.getName());
                        shapeTreeView.setCellFactory(new Callback<TreeView<HistologyObject<?>>, TreeCell<HistologyObject<?>>>() {
                            @Override
                            public TreeCell<HistologyObject<?>> call(TreeView<HistologyObject<?>> p) {
                                return new TreeCellImpl();
                            }
                        });
                    }
                } else if (change.wasAdded()) {
                    Part addedPart = (Part) change.getValueAdded();
                    PartTreeItem pti = new PartTreeItem(addedPart);
                    histion.getChildren().forEach(c -> {
                        if (c.getValue().getId() == addedPart.getCellId()) {
                            c.getChildren().add(pti);
                        }
                    });
                    partTreeItemMap.put(addedPart.getId(), pti);
                } else if (change.wasRemoved()) {
                    Part removedPart = (Part) change.getValueRemoved();
                    histion.getChildren().forEach(c -> {
                        if (c.getValue().getId() == removedPart.getCellId()) {
                            c.getChildren().remove(partTreeItemMap.get(removedPart.getId()));
                        }
                    });
                    partTreeItemMap.remove(removedPart.getId());
                }
            };
    
    private final MapChangeListener<Integer, Cell> cellListener
            = (change) -> {
                
                if (change.wasRemoved() && change.wasAdded()) {
                    Cell c = (Cell) change.getValueAdded();
                    Cell removedShape = (Cell) change.getValueRemoved();
                    if (!(c.getName().equals(removedShape.getName()))) {
                        cellTreeItemMap.get(c.getId()).getValue().setName(c.getName());
                        shapeTreeView.setCellFactory(new Callback<TreeView<HistologyObject<?>>, TreeCell<HistologyObject<?>>>() {
                            @Override
                            public TreeCell<HistologyObject<?>> call(TreeView<HistologyObject<?>> p) {
                                return new TreeCellImpl();
                            }
                        });
                    }
                    removedShape.getItemMap().removeListener(partListener);
                    c.getItemMap().addListener(partListener);
                    
                    /*else {
                        c.getItems().forEach(p -> {
                            if ()
                        });
                    }*/
                    /*if (!(c.getName().equals(removedShape.getName()))) {
                        histion = new HistionTreeItem(hm.getHistionMap().get(0));

                        hm.getHistionMap().get(0).getItemMap().addListener(cellListener);
                        hm.getHistionMap().get(0).getItems().forEach(c -> {
                            CellTreeItem cti = new CellTreeItem(c);
                            histion.getChildren().add(cti);
                            cellTreeItemMap.put(c.getId(), cti);
                            c.getItemMap().addListener(partListener);
                            c.getItems().forEach(p -> {
                                PartTreeItem pti = new PartTreeItem(p);
                                histion.getChildren().get(c.getId()).getChildren().add(pti);
                                partTreeItemMap.put(p.getId(), pti);
                            });
                        });
                    }*/
                    
                    /*if (!(c.getName().equals(removedShape.getName()))) {
                        if (ChosenTool.getToolNumber() == 0) {
                            ChosenTool.setToolNumber(-1);
                        } else if (ChosenTool.getToolNumber() == -1) {
                            ChosenTool.setToolNumber(0);
                        }
                    } else if (!c.getShow()) {
                        if (ChosenTool.getToolNumber() == 0) {
                            ChosenTool.setToolNumber(-1);
                        } else if (ChosenTool.getToolNumber() == -1) {
                            ChosenTool.setToolNumber(0);
                        }
                    }*/
                } else if (change.wasAdded()) {
                    Cell addedCell = (Cell) change.getValueAdded();
                    CellTreeItem cti = new CellTreeItem(addedCell);
                    histion.getChildren().add(cti);
                    cellTreeItemMap.put(addedCell.getId(), cti);
                    addedCell.getItemMap().addListener(partListener);
                    addedCell.getItems().forEach(p -> {
                        PartTreeItem pti = new PartTreeItem(p);
                        histion.getChildren().forEach(c -> {
                            if (c.getValue().getId() == addedCell.getId()) {
                                c.getChildren().add(pti);
                            }
                        });
                        partTreeItemMap.put(p.getId(), pti);
                    });
                } else if (change.wasRemoved()) {
                    Cell removedShape = (Cell) change.getValueRemoved();
                    histion.getChildren().remove(cellTreeItemMap.get(removedShape.getId()));
                    cellTreeItemMap.remove(removedShape.getId());
                    removedShape.getItemMap().removeListener(partListener);
                }
            };
    
    public abstract class AbstractTreeItem extends TreeItem<HistologyObject<?>> {
        public abstract ContextMenu getMenu();
    }

    /*ArrayList<ArrayList<Double>> lineList = new ArrayList<>();
    private void findLineEquation(TetgenPoint point1, TetgenPoint point2) {
        double a = point2.getX() - point1.getX();
        double b = point2.getY() - point1.getY();
        double c = point2.getZ() - point1.getZ();
        
        ArrayList<Double> arr = new ArrayList<>();
        
        arr.add(b);
        arr.add(-a);
        arr.add(-b * point1.getX() + a * point1.getY());
        arr.add(c);
        arr.add(-a);
        arr.add(-c * point1.getX() + a * point1.getZ());
        lineList.add(arr);
    }*/
    
    Node intersect(Line line1, Line line2) {
        double x, y, z;
        x = 10000;
        y = 10000;
        z = -1;
        if ((!line1.vert) && (line2.vert)) {
            x = line2.b;
            y = line1.k * x + line1.b;
            if ((((y < line2.p1.y) || (Math.abs(y - line2.p1.y) < 0.000001)) && ((y > line2.p2.y) || (Math.abs(y - line2.p2.y) < 0.000001)))
                || (((y > line2.p1.y) || (Math.abs(y - line2.p1.y) < 0.000001)) && ((y < line2.p2.y) || (Math.abs(y - line2.p2.y) < 0.000001)))) {
                z = 0;
            }
            /*if (((((y < line1.p1.y) || (Math.abs(y - line1.p1.y) < 0.0001)) && ((y > line1.p2.y) || (Math.abs(y - line1.p2.y) < 0.0001)))
                || (((y > line1.p1.y) || (Math.abs(y - line1.p1.y) < 0.0001)) && ((y < line1.p2.y) || (Math.abs(y - line1.p2.y) < 0.0001))))
                && ((((x < line1.p1.x) || (Math.abs(x - line1.p1.x) < 0.0001)) && ((x > line1.p2.x) || (Math.abs(y - line1.p2.x) < 0.0001)))
                        || (((x > line1.p1.x) || (Math.abs(x - line1.p1.x) < 0.0001)) && ((x < line1.p2.x) || (Math.abs(y - line1.p2.x) < 0.0001))))) {
                z = 0;
            }*/
        }
        else if ((!line1.vert) && (!line2.vert)) {
            if (Math.abs(line1.k - line2.k) > 0.001) {
                x = (line1.b - line2.b) / (line2.k - line1.k);
                y = line1.k * x + line1.b;
                /*System.out.println("---");
                System.out.println(line1.p1.x + " " + line1.p1.y);
                System.out.println(line1.p2.x + " " + line1.p2.y);
                System.out.println(x + " " + y);*/
                /*System.out.println(((((y < line1.p1.y) || (Math.abs(y - line1.p1.y) < 0.0001)) && ((y > line1.p2.y) || (Math.abs(y - line1.p2.y) < 0.0001)))
                        || (((y > line1.p1.y) || (Math.abs(y - line1.p1.y) < 0.0001)) && ((y < line1.p2.y) || (Math.abs(y - line1.p2.y) < 0.0001)))));*/
                //System.out.println((((x > line1.p1.x) || (Math.abs(x - line1.p1.x) < 0.0001)) && ((x < line1.p2.x) || (Math.abs(x - line1.p2.x) < 0.0001)))
                //        );
                //System.out.println(((((y < line1.p1.y) || (Math.abs(y - line1.p1.y) < 0.0001)) && ((y > line1.p2.y) || (Math.abs(y - line1.p2.y) < 0.0001)))
                //        || (((y > line1.p1.y) || (Math.abs(y - line1.p1.y) < 0.0001)) && ((y < line1.p2.y) || (Math.abs(y - line1.p2.y) < 0.0001)))));
                //System.out.println((Math.abs(x - line1.p1.x)));
                //System.out.println((Math.abs(x - line1.p2.x)));
                //System.out.println(((((x < line1.p1.x) || (Math.abs(x - line1.p1.x) < 0.0001)) && ((x > line1.p2.x) || (Math.abs(x - line1.p2.x) < 0.0001)))
                //        || (((x > line1.p1.x) || (Math.abs(x - line1.p1.x) < 0.0001)) && ((x < line1.p2.x) || (Math.abs(x - line1.p2.x) < 0.0001)))));
                if (((((y < line2.p1.y) || (Math.abs(y - line2.p1.y) < 0.000001)) && ((y > line2.p2.y) || (Math.abs(y - line2.p2.y) < 0.000001)))
                        || (((y > line2.p1.y) || (Math.abs(y - line2.p1.y) < 0.000001)) && ((y < line2.p2.y) || (Math.abs(y - line2.p2.y) < 0.000001))))
                        && ((((x < line2.p1.x) || (Math.abs(x - line2.p1.x) < 0.000001)) && ((x > line2.p2.x) || (Math.abs(x - line2.p2.x) < 0.000001)))
                        || (((x > line2.p1.x) || (Math.abs(x - line2.p1.x) < 0.000001)) && ((x < line2.p2.x) || (Math.abs(x - line2.p2.x) < 0.000001))))) {
                    z = 0;
                    //System.out.println("True");
                }
            }
        } else if ((line1.vert) && (!line2.vert)) {
            x = line1.b;
            y = line2.k * x + line2.b;
            //System.out.println(((((y < line1.p1.y) || (Math.abs(y - line1.p1.y) < 0.0001)) && ((y > line1.p2.y) || (Math.abs(y - line1.p2.y) < 0.0001)))
            //    || (((y > line1.p1.y) || (Math.abs(y - line1.p1.y) < 0.0001)) && ((y < line1.p2.y) || (Math.abs(y - line1.p2.y) < 0.0001)))));
            /*if ((((y < line2.p1.y) || (Math.abs(y - line2.p1.y) < 0.0001)) && ((y > line2.p2.y) || (Math.abs(y - line2.p2.y) < 0.0001)))
                || (((y > line2.p1.y) || (Math.abs(y - line2.p1.y) < 0.0001)) && ((y < line2.p2.y) || (Math.abs(y - line2.p2.y) < 0.0001)))) {
                z = 0;
            }*/
            if (((((y < line2.p1.y) || (Math.abs(y - line2.p1.y) < 0.000001)) && ((y > line2.p2.y) || (Math.abs(y - line2.p2.y) < 0.000001)))
                    || (((y > line2.p1.y) || (Math.abs(y - line2.p1.y) < 0.000001)) && ((y < line2.p2.y) || (Math.abs(y - line2.p2.y) < 0.000001))))
                    && ((((x < line2.p1.x) || (Math.abs(x - line2.p1.x) < 0.000001)) && ((x > line2.p2.x) || (Math.abs(x - line2.p2.x) < 0.000001)))
                    || (((x > line2.p1.x) || (Math.abs(x - line2.p1.x) < 0.000001)) && ((x < line2.p2.x) || (Math.abs(x - line2.p2.x) < 0.000001))))) {
                z = 0;
                //System.out.println("True");
            }
        }
        return new Node(x, y, z);
    }
    
    Node intersect2(Line line1, Line line2) {
        double x, y, z;
        x = 10000;
        y = 10000;
        z = -1;
        if ((!line1.vert) && (line2.vert)) {
            x = line2.b;
            y = line1.k * x + line1.b;
            if ((((y < line2.p1.y) || (Math.abs(y - line2.p1.y) < 0.000001)) && ((y > line2.p2.y) || (Math.abs(y - line2.p2.y) < 0.000001)))
                || (((y > line2.p1.y) || (Math.abs(y - line2.p1.y) < 0.000001)) && ((y < line2.p2.y) || (Math.abs(y - line2.p2.y) < 0.000001)))) {
                if ((x > line1.p1.x) || (Math.abs(x - line1.p1.x) < 0.000001)) {
                        z = 0;
                    }
            }
            /*if (((((y < line1.p1.y) || (Math.abs(y - line1.p1.y) < 0.0001)) && ((y > line1.p2.y) || (Math.abs(y - line1.p2.y) < 0.0001)))
                || (((y > line1.p1.y) || (Math.abs(y - line1.p1.y) < 0.0001)) && ((y < line1.p2.y) || (Math.abs(y - line1.p2.y) < 0.0001))))
                && ((((x < line1.p1.x) || (Math.abs(x - line1.p1.x) < 0.0001)) && ((x > line1.p2.x) || (Math.abs(y - line1.p2.x) < 0.0001)))
                        || (((x > line1.p1.x) || (Math.abs(x - line1.p1.x) < 0.0001)) && ((x < line1.p2.x) || (Math.abs(y - line1.p2.x) < 0.0001))))) {
                z = 0;
            }*/
        }
        else if ((!line1.vert) && (!line2.vert)) {
            if (Math.abs(line1.k - line2.k) > 0.001) {
                x = (line1.b - line2.b) / (line2.k - line1.k);
                y = line1.k * x + line1.b;
                /*System.out.println("---");
                System.out.println(line1.p1.x + " " + line1.p1.y);
                System.out.println(line1.p2.x + " " + line1.p2.y);
                System.out.println(x + " " + y);*/
                /*System.out.println(((((y < line1.p1.y) || (Math.abs(y - line1.p1.y) < 0.0001)) && ((y > line1.p2.y) || (Math.abs(y - line1.p2.y) < 0.0001)))
                        || (((y > line1.p1.y) || (Math.abs(y - line1.p1.y) < 0.0001)) && ((y < line1.p2.y) || (Math.abs(y - line1.p2.y) < 0.0001)))));*/
                //System.out.println((((x > line1.p1.x) || (Math.abs(x - line1.p1.x) < 0.0001)) && ((x < line1.p2.x) || (Math.abs(x - line1.p2.x) < 0.0001)))
                //        );
                //System.out.println(((((y < line1.p1.y) || (Math.abs(y - line1.p1.y) < 0.0001)) && ((y > line1.p2.y) || (Math.abs(y - line1.p2.y) < 0.0001)))
                //        || (((y > line1.p1.y) || (Math.abs(y - line1.p1.y) < 0.0001)) && ((y < line1.p2.y) || (Math.abs(y - line1.p2.y) < 0.0001)))));
                //System.out.println((Math.abs(x - line1.p1.x)));
                //System.out.println((Math.abs(x - line1.p2.x)));
                //System.out.println(((((x < line1.p1.x) || (Math.abs(x - line1.p1.x) < 0.0001)) && ((x > line1.p2.x) || (Math.abs(x - line1.p2.x) < 0.0001)))
                //        || (((x > line1.p1.x) || (Math.abs(x - line1.p1.x) < 0.0001)) && ((x < line1.p2.x) || (Math.abs(x - line1.p2.x) < 0.0001)))));
                if (((((y < line2.p1.y) || (Math.abs(y - line2.p1.y) < 0.000001)) && ((y > line2.p2.y) || (Math.abs(y - line2.p2.y) < 0.000001)))
                        || (((y > line2.p1.y) || (Math.abs(y - line2.p1.y) < 0.000001)) && ((y < line2.p2.y) || (Math.abs(y - line2.p2.y) < 0.000001))))
                        && ((((x < line2.p1.x) || (Math.abs(x - line2.p1.x) < 0.000001)) && ((x > line2.p2.x) || (Math.abs(x - line2.p2.x) < 0.000001)))
                        || (((x > line2.p1.x) || (Math.abs(x - line2.p1.x) < 0.000001)) && ((x < line2.p2.x) || (Math.abs(x - line2.p2.x) < 0.000001))))) {
                    if ((x > line1.p1.x) || (Math.abs(x - line1.p1.x) < 0.000001)) {
                        z = 0;
                    }
                    //System.out.println("True");
                }
            }
        } else if ((line1.vert) && (!line2.vert)) {
            x = line1.b;
            y = line2.k * x + line2.b;
            //System.out.println(((((y < line1.p1.y) || (Math.abs(y - line1.p1.y) < 0.0001)) && ((y > line1.p2.y) || (Math.abs(y - line1.p2.y) < 0.0001)))
            //    || (((y > line1.p1.y) || (Math.abs(y - line1.p1.y) < 0.0001)) && ((y < line1.p2.y) || (Math.abs(y - line1.p2.y) < 0.0001)))));
            /*if ((((y < line2.p1.y) || (Math.abs(y - line2.p1.y) < 0.0001)) && ((y > line2.p2.y) || (Math.abs(y - line2.p2.y) < 0.0001)))
                || (((y > line2.p1.y) || (Math.abs(y - line2.p1.y) < 0.0001)) && ((y < line2.p2.y) || (Math.abs(y - line2.p2.y) < 0.0001)))) {
                z = 0;
            }*/
            if (((((y < line2.p1.y) || (Math.abs(y - line2.p1.y) < 0.000001)) && ((y > line2.p2.y) || (Math.abs(y - line2.p2.y) < 0.000001)))
                    || (((y > line2.p1.y) || (Math.abs(y - line2.p1.y) < 0.000001)) && ((y < line2.p2.y) || (Math.abs(y - line2.p2.y) < 0.000001))))
                    && ((((x < line2.p1.x) || (Math.abs(x - line2.p1.x) < 0.000001)) && ((x > line2.p2.x) || (Math.abs(x - line2.p2.x) < 0.000001)))
                    || (((x > line2.p1.x) || (Math.abs(x - line2.p1.x) < 0.000001)) && ((x < line2.p2.x) || (Math.abs(x - line2.p2.x) < 0.000001))))) {
                if ((x > line1.p1.x) || (Math.abs(x - line1.p1.x) < 0.000001)) {
                        z = 0;
                    }
                //System.out.println("True");
            }
        }
        return new Node(x, y, z);
    }
    
    Node intersect3(Line line1, Line line2) {
        double x, y, z;
        x = 10000;
        y = 10000;
        z = -1;
        if ((!line1.vert) && (line2.vert)) {
            x = line2.b;
            y = line1.k * x + line1.b;
            if ((((y < line2.p1.y) || (Math.abs(y - line2.p1.y) < 0.000001)) && ((y > line2.p2.y) || (Math.abs(y - line2.p2.y) < 0.000001)))
                || (((y > line2.p1.y) || (Math.abs(y - line2.p1.y) < 0.000001)) && ((y < line2.p2.y) || (Math.abs(y - line2.p2.y) < 0.000001)))) {
                if ((x > line1.p1.x) || (Math.abs(x - line1.p1.x) < 0.000001)) {
                        z = 0;
                    }
            }
            /*if (((((y < line1.p1.y) || (Math.abs(y - line1.p1.y) < 0.0001)) && ((y > line1.p2.y) || (Math.abs(y - line1.p2.y) < 0.0001)))
                || (((y > line1.p1.y) || (Math.abs(y - line1.p1.y) < 0.0001)) && ((y < line1.p2.y) || (Math.abs(y - line1.p2.y) < 0.0001))))
                && ((((x < line1.p1.x) || (Math.abs(x - line1.p1.x) < 0.0001)) && ((x > line1.p2.x) || (Math.abs(y - line1.p2.x) < 0.0001)))
                        || (((x > line1.p1.x) || (Math.abs(x - line1.p1.x) < 0.0001)) && ((x < line1.p2.x) || (Math.abs(y - line1.p2.x) < 0.0001))))) {
                z = 0;
            }*/
        }
        else if ((!line1.vert) && (!line2.vert)) {
            if (Math.abs(line1.k - line2.k) > 0.001) {
                x = (line1.b - line2.b) / (line2.k - line1.k);
                y = line1.k * x + line1.b;
                /*System.out.println("---");
                System.out.println(line1.p1.x + " " + line1.p1.y);
                System.out.println(line1.p2.x + " " + line1.p2.y);
                System.out.println(x + " " + y);*/
                /*System.out.println(((((y < line1.p1.y) || (Math.abs(y - line1.p1.y) < 0.0001)) && ((y > line1.p2.y) || (Math.abs(y - line1.p2.y) < 0.0001)))
                        || (((y > line1.p1.y) || (Math.abs(y - line1.p1.y) < 0.0001)) && ((y < line1.p2.y) || (Math.abs(y - line1.p2.y) < 0.0001)))));*/
                //System.out.println((((x > line1.p1.x) || (Math.abs(x - line1.p1.x) < 0.0001)) && ((x < line1.p2.x) || (Math.abs(x - line1.p2.x) < 0.0001)))
                //        );
                //System.out.println(((((y < line1.p1.y) || (Math.abs(y - line1.p1.y) < 0.0001)) && ((y > line1.p2.y) || (Math.abs(y - line1.p2.y) < 0.0001)))
                //        || (((y > line1.p1.y) || (Math.abs(y - line1.p1.y) < 0.0001)) && ((y < line1.p2.y) || (Math.abs(y - line1.p2.y) < 0.0001)))));
                //System.out.println((Math.abs(x - line1.p1.x)));
                //System.out.println((Math.abs(x - line1.p2.x)));
                //System.out.println(((((x < line1.p1.x) || (Math.abs(x - line1.p1.x) < 0.0001)) && ((x > line1.p2.x) || (Math.abs(x - line1.p2.x) < 0.0001)))
                //        || (((x > line1.p1.x) || (Math.abs(x - line1.p1.x) < 0.0001)) && ((x < line1.p2.x) || (Math.abs(x - line1.p2.x) < 0.0001)))));
                if (((((y < line2.p1.y) || (Math.abs(y - line2.p1.y) < 0.000001)) && ((y > line2.p2.y) || (Math.abs(y - line2.p2.y) < 0.000001)))
                        || (((y > line2.p1.y) || (Math.abs(y - line2.p1.y) < 0.000001)) && ((y < line2.p2.y) || (Math.abs(y - line2.p2.y) < 0.000001))))
                        && ((((x < line2.p1.x) || (Math.abs(x - line2.p1.x) < 0.000001)) && ((x > line2.p2.x) || (Math.abs(x - line2.p2.x) < 0.000001)))
                        || (((x > line2.p1.x) || (Math.abs(x - line2.p1.x) < 0.000001)) && ((x < line2.p2.x) || (Math.abs(x - line2.p2.x) < 0.000001))))) {
                    if ((x < line1.p1.x) || (Math.abs(x - line1.p1.x) < 0.000001)) {
                        z = 0;
                    }
                    //System.out.println("True");
                }
            }
        } else if ((line1.vert) && (!line2.vert)) {
            x = line1.b;
            y = line2.k * x + line2.b;
            //System.out.println(((((y < line1.p1.y) || (Math.abs(y - line1.p1.y) < 0.0001)) && ((y > line1.p2.y) || (Math.abs(y - line1.p2.y) < 0.0001)))
            //    || (((y > line1.p1.y) || (Math.abs(y - line1.p1.y) < 0.0001)) && ((y < line1.p2.y) || (Math.abs(y - line1.p2.y) < 0.0001)))));
            /*if ((((y < line2.p1.y) || (Math.abs(y - line2.p1.y) < 0.0001)) && ((y > line2.p2.y) || (Math.abs(y - line2.p2.y) < 0.0001)))
                || (((y > line2.p1.y) || (Math.abs(y - line2.p1.y) < 0.0001)) && ((y < line2.p2.y) || (Math.abs(y - line2.p2.y) < 0.0001)))) {
                z = 0;
            }*/
            if (((((y < line2.p1.y) || (Math.abs(y - line2.p1.y) < 0.000001)) && ((y > line2.p2.y) || (Math.abs(y - line2.p2.y) < 0.000001)))
                    || (((y > line2.p1.y) || (Math.abs(y - line2.p1.y) < 0.000001)) && ((y < line2.p2.y) || (Math.abs(y - line2.p2.y) < 0.000001))))
                    && ((((x < line2.p1.x) || (Math.abs(x - line2.p1.x) < 0.000001)) && ((x > line2.p2.x) || (Math.abs(x - line2.p2.x) < 0.000001)))
                    || (((x > line2.p1.x) || (Math.abs(x - line2.p1.x) < 0.000001)) && ((x < line2.p2.x) || (Math.abs(x - line2.p2.x) < 0.000001))))) {
                if ((x < line1.p1.x) || (Math.abs(x - line1.p1.x) < 0.000001)) {
                        z = 0;
                    }
                //System.out.println("True");
            }
        }
        return new Node(x, y, z);
    }
    
    /*public boolean contains(Node test) {
        int i;
        int j;
        boolean result = false;
        for (i = 0, j = points.length - 1; i < points.length; j = i++) {
            if ((points[i].y > test.y) != (points[j].y > test.y)
                    && (test.x < (points[j].x - points[i].x) * (test.y - points[i].y) / (points[j].y - points[i].y) + points[i].x)) {
                result = !result;
            }
        }
        return result;
    }*/
    
    public class HistionTreeItem extends AbstractTreeItem {

        ContextMenu histionMenu = new ContextMenu();
        
        public HistionTreeItem(HistologyObject<?> object) {
            this.setValue(object);
            this.setExpanded(true);
        }

        @Override
        public ContextMenu getMenu() {
            
            //ContextMenu histionMenu = new ContextMenu();
            
            MenuItem editHistion = new MenuItem();
            editHistion.setText("Edit histion");
            editHistion.setOnAction(event -> {
                HistionInformationInitialization.createScene(this.getValue().getId());
            });
            MenuItem saveHistion = new MenuItem();
            saveHistion.setText("Save histion");
            saveHistion.setOnAction(event -> {
                SaveBox.display();
            });
            MenuItem loadHistion = new MenuItem();
            loadHistion.setText("Load histion");
            loadHistion.setOnAction(event -> {
                LoadBox.display();
            });
            MenuItem loadCell = new MenuItem();
            loadCell.setText("Load cell");
            loadCell.setOnAction(event -> {
                LoadCellBox.display();
            });
            MenuItem addCell = new MenuItem();
            addCell.setText("Add cell");
            addCell.setOnAction(event -> {
                AddBox.display("Add Cell", "Cell name", this.getValue().getId());
            });
            CheckMenuItem fillModel = new CheckMenuItem();
            fillModel.setText("Fill model");
            fillModel.setOnAction(event -> {
                histionMenu.hide();
                if (fillModel.isSelected()) {
                    int hId = this.getValue().getId();
                    final DoubleProperty leftX = new SimpleDoubleProperty(10000);
                    final DoubleProperty rightX = new SimpleDoubleProperty(-10000);
                    final DoubleProperty upperZ = new SimpleDoubleProperty(-10000);
                    final DoubleProperty bottomZ = new SimpleDoubleProperty(10000);
                    final DoubleProperty upperY = new SimpleDoubleProperty(-10000);
                    final DoubleProperty bottomY = new SimpleDoubleProperty(10000);
                    
                    ArrayList<TetgenPoint> pl = new ArrayList<>();
                    /*ObservableList<TetgenPoint> pl = FXCollections.observableArrayList();
                    
                    nodeAvg = new Point3D(0, 0, 0);
                    hm.getHistionMap().get(hId).getItems().forEach(c -> {
                        if (c.getShow()) {
                            for (TetgenPoint point : c.getTransformedPointData()) {
                                pl.add(new TetgenPoint(point));
                                nodeAvg = new Point3D(nodeAvg.getX() + point.getX(), nodeAvg.getY() + point.getY(), nodeAvg.getZ() + point.getZ());
                            }
                            dataSize += c.getTransformedPointData().size();
                        }
                    });
                    nodeAvg = new Point3D(nodeAvg.getX() / dataSize, nodeAvg.getY() / dataSize, nodeAvg.getZ() / dataSize);
                    
                    applyTransformations(hm.getHistionMap().get(hId).getXRotate(),
                            hm.getHistionMap().get(hId).getYRotate(), nodeAvg, pl);*/
                    
                    hm.getHistionMap().get(hId).getItems().forEach(c -> {
                        if (c.getShow()) {
                            c.getTransformedPointData().forEach(point -> {
                                pl.add(point);
                                if (point.getX() < leftX.get())
                                    leftX.set(point.getX());
                                if (point.getX() > rightX.get())
                                    rightX.set(point.getX());
                                if (point.getZ() < bottomZ.get())
                                    bottomZ.set(point.getZ());
                                if (point.getZ() > upperZ.get())
                                    upperZ.set(point.getZ());
                                if (point.getY() < bottomY.get())
                                    bottomY.set(point.getY());
                                if (point.getY() > upperY.get())
                                    upperY.set(point.getY());
                            });
                        }
                    });
                    
                    //ArrayList<Double> y = new ArrayList();
                    DoubleProperty xSpace = new SimpleDoubleProperty(0);
                    DoubleProperty ySpace = new SimpleDoubleProperty(0);
                    //DoubleProperty xzSpaceX = new SimpleDoubleProperty(0);
                    //DoubleProperty xzSpaceZ = new SimpleDoubleProperty(0);
                    DoubleProperty xzSpace = new SimpleDoubleProperty(0);
                    DoubleProperty zSpace = new SimpleDoubleProperty(0);
                    
                    /*lineList = new ArrayList<>();
                    
                    for (int i = 0; i < pl.size(); i++) {
                        TetgenPoint point1 = pl.get(i);
                        for (int j = 0; j < pl.size(); j++) {
                            if (i == j) {
                                continue;
                            }
                            TetgenPoint point2 = pl.get(j);
                            findLineEquation(point1, point2);
                        }
                    }*/
                    
                    /*ArrayList<Line> lines = new ArrayList();
                    
                    for (int i = 0; i < pl.size(); i++) {
                        TetgenPoint point1 = pl.get(i);
                        for (int j = i + 1; j < pl.size(); j++) {
                            TetgenPoint point2 = pl.get(j);
                            if (Math.abs(point1.getY() - point2.getY()) < 0.0001) {
                                lines.add(new Line(new Node(point1.getX(), point1.getZ(), point1.getY()),
                                        new Node(point2.getX(), point2.getZ(), point2.getY())));
                            }
                        }
                    }*/
                    
                    for (TetgenPoint point : pl) {
                        
                        /*if (point.getY() < bottomY.get()) {
                            bottomY.set(point.getY());
                        }
                        if (point.getY() > upperY.get()) {
                            upperY.set(point.getY());
                        }*/
                        
                        //System.out.println("**************");
                        //System.out.println("Point " + point.getX() + " " + point.getZ());
                        hm.getHistionMap().get(hId).getItems().forEach(c -> {
                            //System.out.println("------------");
                            if (c.getShow()) {
                                double minY = 10000;
                                double maxY = -10000;
                                for (TetgenPoint p : c.getTransformedPointData()) {
                                    if (p.getY() < minY) {
                                        minY = p.getY();
                                    }
                                    if (p.getY() > maxY) {
                                        maxY = p.getY();
                                    }
                                }
                                if (maxY - minY > ySpace.get()) {
                                    ySpace.set(maxY - minY);
                                }
                                for (Line line : LineEquations.getLineMap().get(c.getId())) {
                                    if (Math.abs(point.getY() - line.p1.z) < 0.000001) {
                                        Node p1 = intersect(new Line(new Node(point.getX(), point.getZ(), point.getY()), new Node(point.getX() + 1, point.getZ(), point.getY())), line);
                                        Node p2 = intersect(new Line(new Node(point.getX(), point.getZ(), point.getY()), new Node(point.getX(), point.getZ() + 1, point.getY())), line);
                                        //Node p3 = intersect(new Line(new Node(point.getX(), point.getZ(), point.getY()), new Node(point.getX() + 1, point.getZ() + 1, point.getY())), line);
                                        //System.out.println("*");
                                        /*System.out.println("Line p1 " + line.p1.x + " " + line.p1.y);
                                        System.out.println("Line p2 " + line.p2.x + " " + line.p2.y);
                                        System.out.println("X inter " + p3.x + " " + p3.y);*/
                                        //System.out.println("Z inter " + p2.x + " " + p2.y);
                                        /*System.out.println("Difference");
                                        if (p1.z == 0)
                                        System.out.println(Math.abs(point.getX() - p1.x));
                                        if (p2.z == 0)
                                        System.out.println(Math.abs(point.getZ() - p2.y));
                                        System.out.println("----------");*/
                                        //System.out.println(point.getX() + " " + p1.x);
                                        //System.out.println(point.getZ() + " " + p1.y);
                                        
                                        
                                        if (p1.z == 0) {
                                            if (Math.abs(point.getX() - p1.x) > xSpace.get()) {
                                                xSpace.set(Math.abs(point.getX() - p1.x));
                                            }
                                        }
                                        if (p2.z == 0) {
                                            if (Math.abs(point.getZ() - p2.y) > zSpace.get()) {
                                                zSpace.set(Math.abs(point.getZ() - p2.y));
                                            }
                                        }
                                        //if (p3.z == 0) {
                                            /*if (Math.sqrt((point.getX() - p2.x) * (point.getX() - p2.x) 
                                                    + (point.getZ() - p2.y) * (point.getZ() - p2.y)) > xzSpace.get()) {
                                                xzSpace.set(Math.sqrt((point.getX() - p2.x) * (point.getX() - p2.x) 
                                                    + (point.getZ() - p2.y) * (point.getZ() - p2.y)));
                                            }*/
                                            /*if (Math.abs(point.getZ() - p3.y) > xzSpace.get()) {
                                                xzSpace.set(Math.abs(point.getZ() - p3.y));
                                            }*/
                                            /*if (Math.abs(point.getZ() - p2.y) > xzSpaceZ.get()) {
                                                xzSpaceZ.set(Math.abs(point.getZ() - p2.y));
                                            }
                                            if (Math.abs(point.getX() - p2.x) > xzSpaceX.get()) {
                                                xzSpaceX.set(Math.abs(point.getX() - p2.x));
                                            }*/
                                        //}
                                        /*if (p1.z == 0) {
                                            System.out.println(point.getX() + " " + p1.x);
                                            if (Math.abs(point.getX() - p1.x) > xSpace.get()) {
                                                xSpace.set(Math.abs(point.getX() - p1.x));
                                            }
                                        }
                                        if (p2.z == 0) {
                                            System.out.println(point.getZ() + " " + p2.y);
                                            if (Math.abs(point.getZ() - p2.y) > ySpace.get()) {
                                                ySpace.set(Math.abs(point.getZ() - p2.y));
                                            }
                                        }*/
                                    }
                                }
                            }
                        });
                    }
                    
                    //System.out.println(xzSpace.get());
                    ArrayList<Double> yArr = new ArrayList<>();
                    for (TetgenPoint point : pl) {
                        if (!yArr.contains(point.getY()))
                            yArr.add(point.getY());
                    }
                    //ArrayList<Double> usedYs = new ArrayList<>();
                    final IntegerProperty count = new SimpleIntegerProperty(0);
                    final IntegerProperty count2 = new SimpleIntegerProperty(0);
                    for (TetgenPoint point : pl) {
                        //usedYs.add(point.getY());
                        for (Double y : yArr) {
                            count.set(0);
                            count2.set(0);
                            if (Math.abs(point.getY() - y) < 0.000001)
                                continue;
                            hm.getHistionMap().get(hId).getItems().forEach(c -> {
                                if (c.getShow()) {
                                    for (Line line : LineEquations.getLineMap().get(c.getId())) {
                                        if (Math.abs(y - line.p1.z) < 0.000001) {
                                            {
                                                Node p1 = intersect2(new Line(new Node(point.getX(), point.getZ(), point.getY()), new Node(point.getX() + 1, point.getZ(), point.getY())), line);
                                                Node p2 = intersect3(new Line(new Node(point.getX(), point.getZ(), point.getY()), new Node(point.getX() - 1, point.getZ(), point.getY())), line);
                                                if (p1.z == 0) {
                                                    count.set(count.get() + 1);
                                                    //count++;
                                                    //if (Math.abs(point.getX() - p1.x) > xSpace.get()) {
                                                    //    xSpace.set(Math.abs(point.getX() - p1.x));
                                                    //}
                                                }
                                                if (p2.z == 0) {
                                                    count2.set(count2.get() + 1);
                                                    //count++;
                                                    //if (Math.abs(point.getX() - p1.x) > xSpace.get()) {
                                                    //    xSpace.set(Math.abs(point.getX() - p1.x));
                                                    //}
                                                }
                                            }
                                        }
                                    }
                                }
                            });
                            /*System.out.println("-----");
                            System.out.println(count.get());
                            System.out.println(count2.get());*/
                            if ((count.get() % 2 == 1) || (count2.get() % 2 == 1)) {
                            //if (count.get() % 2 == 1) {
                                if (Math.abs(point.getY() - y) > ySpace.get()) {
                                    //if (Math.abs(Math.abs(point.getY() - y) - 700) > 0.001 )
                                    ySpace.set(Math.abs(point.getY() - y));
                                    System.out.println("-------");
                                    //System.out.println(count.get());
                                    System.out.println(ySpace.get());
                                    System.out.println(point.getX() + " " + point.getY() + " " + point.getZ());
                                }
                            }
                        }
                    }
                    System.out.println("****");
                    System.out.println(ySpace.get());
                    
                    /*for (TetgenPoint point : pl) {
                        
                        //System.out.println("**************");
                        //System.out.println("Point " + point.getX() + " " + point.getZ());
                        hm.getHistionMap().get(hId).getItems().forEach(c -> {
                            //System.out.println("------------");
                            if (c.getShow()) {
                                for (Line line : LineEquations.getLineMap().get(c.getId())) {
                                    if (Math.abs(point.getY() - line.p1.z) > 0.0001) {
                                        Node p1 = intersect(new Line(new Node(point.getX(), point.getZ(), point.getY()), new Node(point.getX() + 1, point.getZ(), point.getY())), line);
                                        Node p2 = intersect(new Line(new Node(point.getX(), point.getZ(), point.getY()), new Node(point.getX(), point.getZ() + 1, point.getY())), line);
                                        if (p1.z == 0) {
                                            if (Math.abs(point.getX() - p1.x) > xSpace.get()) {
                                                xSpace.set(Math.abs(point.getX() - p1.x));
                                            }
                                        }
                                        if (p2.z == 0) {
                                            if (Math.abs(point.getZ() - p2.y) > zSpace.get()) {
                                                zSpace.set(Math.abs(point.getZ() - p2.y));
                                            }
                                        }
                                    }
                                }
                            }
                        });
                    }*/
                    
                    /*for (TetgenPoint point :  pl) {
                        for (Line line : lines) {
                            if (Math.abs(point.getY() - line.p1.z) < 0.0001) {
                                Node p1 = intersect(new Line(new Node(point.getX(), point.getZ(), point.getY()), new Node(point.getX() + 1, point.getZ(), point.getY())), line);
                                Node p2 = intersect(new Line(new Node(point.getX(), point.getZ(), point.getY()), new Node(point.getX(), point.getZ() + 1, point.getY())), line);
                                System.out.println("*");
                                //System.out.println(point.getX() + " " + p1.x);
                                //System.out.println(point.getZ() + " " + p1.y);
                                if (p1.z == 0) {
                                    System.out.println(point.getX() + " " + p1.x);
                                    if (Math.abs(point.getX() - p1.x) > xSpace.get())
                                        xSpace.set(Math.abs(point.getX() - p1.x));
                                }
                                if (p2.z == 0) {
                                    System.out.println(point.getZ() + " " + p2.y);
                                    if (Math.abs(point.getZ() - p2.y) > ySpace.get())
                                        ySpace.set(Math.abs(point.getZ() - p2.y));
                                }
                            }
                        }
                    }*/
                    /*for (TetgenPoint point :  pl) {
                        
                        if (point.getX() < leftX.get()) {
                            leftX.set(point.getX());
                        }
                        if (point.getX() > rightX.get()) {
                            rightX.set(point.getX());
                        }
                        if (point.getZ() < bottomZ.get()) {
                            bottomZ.set(point.getZ());
                        }
                        if (point.getZ() > upperZ.get()) {
                            upperZ.set(point.getZ());
                        }
                        if (point.getY() < bottomY.get()) {
                            bottomY.set(point.getY());
                        }
                        if (point.getY() > upperY.get()) {
                            upperY.set(point.getY());
                        }
                    }*/
                    
                    //ySpace.set(upperY.get() - bottomY.get());
                    
                    /*DoubleProperty xSpace = new SimpleDoubleProperty(rightX.get() - leftX.get());
                    DoubleProperty ySpace = new SimpleDoubleProperty(upperY.get() - bottomY.get());
                    DoubleProperty zSpace = new SimpleDoubleProperty(upperZ.get() - bottomZ.get());*/
                    
                    /*DoubleProperty xBoundary = new SimpleDoubleProperty(300);
                    DoubleProperty yBoundary = new SimpleDoubleProperty(300);
                    DoubleProperty zBoundary = new SimpleDoubleProperty(300);*/
                    
                    /*IntegerProperty xBoundary = new SimpleIntegerProperty(3);
                    IntegerProperty yBoundary = new SimpleIntegerProperty(3);
                    IntegerProperty zBoundary = new SimpleIntegerProperty(3);*/
                    
                    //ySpace.set(upperY.get() - bottomY.get());
                    
                    IntegerProperty xLimit = new SimpleIntegerProperty(2);
                    IntegerProperty yLimit = new SimpleIntegerProperty(1);
                    IntegerProperty xzLimit = new SimpleIntegerProperty(2);
                    IntegerProperty zLimit = new SimpleIntegerProperty(2);
                    
                    DoubleProperty xShift = new SimpleDoubleProperty(0);
                    DoubleProperty zShift = new SimpleDoubleProperty(0);
                    
                    BooleanProperty buttonPressed = new SimpleBooleanProperty(false);
                    
                    //HistionRecurrence.display("Spacing", xSpace, ySpace, xzSpaceX, xzSpaceZ, zSpace,
                    //        xLimit, yLimit, xyLimit, zLimit, buttonPressed);
                    HistionRecurrence.display("Spacing", xLimit, yLimit, xzLimit, zLimit, buttonPressed, xShift, zShift);
                    
                    if (!buttonPressed.get()) {
                        fillModel.setSelected(false);
                        return;
                    }
                    
                    double deltaX = xSpace.get();
                    double deltaY = ySpace.get();
                    double deltaZ = zSpace.get();
                    double deltaXZ = xzSpace.get();
                    
                    double hZ = hm.getHistionMap().get(hId).getZCoordinate();
                    double hY = hm.getHistionMap().get(hId).getYCoordinate();
                    double hX = hm.getHistionMap().get(hId).getXCoordinate();
                    
                    double xBoundary = deltaX * xLimit.get();
                    double yBoundary = deltaY * yLimit.get();
                    double zBoundary = deltaZ * zLimit.get();
                    double xzBoundary = deltaXZ * xzLimit.get();
                    
                    if (xLimit.get() > 0) {
                        while (hX < xBoundary) {
                            Histion newHistion = new Histion(hm.getHistionMap().get(hId));
                            newHistion.setName("Histion <X: " + String.valueOf(hX + deltaX) + " ; Z: " + String.valueOf(hZ) + ">");
                            hm.getHistionMap().get(hId).getItems().forEach(c -> {
                                Cell newCell = new Cell(c, newHistion.getId());
                                c.getItems().forEach(p -> {
                                    newCell.addChild(p);
                                });
                                newHistion.addChild(newCell);
                            });
                            newHistion.setXCoordinate(hX + deltaX + xShift.get());
                            newHistion.setZCoordinate(hZ + zShift.get());
                            hm.addHistion(newHistion);
                            hX += deltaX + xShift.get();
                            hZ += zShift.get();
                        }
                        hX = hm.getHistionMap().get(hId).getXCoordinate();
                        hZ = hm.getHistionMap().get(hId).getZCoordinate();
                        while (hX > -xBoundary) {
                            Histion newHistion = new Histion(hm.getHistionMap().get(hId));
                            newHistion.setName("Histion <X: " + String.valueOf(hX - deltaX) + " ; Z: " + String.valueOf(hZ) + ">");
                            hm.getHistionMap().get(hId).getItems().forEach(c -> {
                                Cell newCell = new Cell(c, newHistion.getId());
                                c.getItems().forEach(p -> {
                                    newCell.addChild(p);
                                });
                                newHistion.addChild(newCell);
                            });
                            newHistion.setXCoordinate(hX - deltaX - xShift.get());
                            newHistion.setZCoordinate(hZ - zShift.get());
                            hm.addHistion(newHistion);
                            hX -= deltaX + xShift.get();
                            hZ -= zShift.get();
                        }
                        hX = hm.getHistionMap().get(hId).getXCoordinate();
                        hZ = hm.getHistionMap().get(hId).getZCoordinate();
                    }
                    //hX = hm.getHistionMap().get(hId).getXCoordinate();
                    //hZ = hm.getHistionMap().get(hId).getZCoordinate();
                    
                    if (yLimit.get() > 0) {
                        while (hY < yBoundary) {
                            Histion newHistion = new Histion(hm.getHistionMap().get(hId));
                            newHistion.setName("Histion <X: " + String.valueOf(hX) + " ; Y: "
                                    + String.valueOf(hY + deltaY) + " ; Z: "
                                    + String.valueOf(hZ) + ">");
                            hm.getHistionMap().get(hId).getItems().forEach(c -> {
                                Cell newCell = new Cell(c, newHistion.getId());
                                c.getItems().forEach(p -> {
                                    newCell.addChild(p);
                                });
                                newHistion.addChild(newCell);
                            });
                            newHistion.setYCoordinate(hY + deltaY);
                            hm.addHistion(newHistion);
                            hY += deltaY;
                        }
                        hY = hm.getHistionMap().get(hId).getYCoordinate();
                        while (hY > -yBoundary) {
                            Histion newHistion = new Histion(hm.getHistionMap().get(hId));
                            newHistion.setName("Histion <X: " + String.valueOf(hX) + " ; Y: "
                                    + String.valueOf(hY - deltaY) + " ; Z: "
                                    + String.valueOf(hZ) + ">");
                            hm.getHistionMap().get(hId).getItems().forEach(c -> {
                                Cell newCell = new Cell(c, newHistion.getId());
                                c.getItems().forEach(p -> {
                                    newCell.addChild(p);
                                });
                                newHistion.addChild(newCell);
                            });
                            newHistion.setYCoordinate(hY - deltaY);
                            hm.addHistion(newHistion);
                            hY -= deltaY;
                        }
                        hY = hm.getHistionMap().get(hId).getYCoordinate();
                    }
                    //hY = hm.getHistionMap().get(hId).getYCoordinate();
                    
                    if (zLimit.get() > 0) {
                        while (hZ < zBoundary) {
                            Histion newHistion = new Histion(hm.getHistionMap().get(hId));
                            newHistion.setName("Histion <X: " + String.valueOf(hX) + " ; Z: " + String.valueOf(hZ + deltaZ) + ">");
                            hm.getHistionMap().get(hId).getItems().forEach(c -> {
                                Cell newCell = new Cell(c, newHistion.getId());
                                c.getItems().forEach(p -> {
                                    newCell.addChild(p);
                                });
                                newHistion.addChild(newCell);
                            });
                            newHistion.setZCoordinate(hZ + deltaZ);
                            hm.addHistion(newHistion);
                            hZ += deltaZ;
                        }
                        hZ = hm.getHistionMap().get(hId).getZCoordinate();
                        while (hZ > -zBoundary) {
                            Histion newHistion = new Histion(hm.getHistionMap().get(hId));
                            newHistion.setName("Histion <X: " + String.valueOf(hX) + " ; Z: " + String.valueOf(hZ - deltaZ) + ">");
                            hm.getHistionMap().get(hId).getItems().forEach(c -> {
                                Cell newCell = new Cell(c, newHistion.getId());
                                c.getItems().forEach(p -> {
                                    newCell.addChild(p);
                                });
                                newHistion.addChild(newCell);
                            });
                            newHistion.setZCoordinate(hZ - deltaZ);
                            hm.addHistion(newHistion);
                            hZ -= deltaZ;
                        }
                        hZ = hm.getHistionMap().get(hId).getZCoordinate();
                    }
                    
                    int num = 0;
                    if ((xLimit.get() > 0) && (zLimit.get() > 0)) {
                        while (hX < xBoundary) {
                            hZ = hm.getHistionMap().get(hId).getZCoordinate() + num * zShift.get();
                            while (hZ - num * zShift.get() < zBoundary) {
                                Histion newHistion = new Histion(hm.getHistionMap().get(hId));
                                newHistion.setName("Histion <X: " + String.valueOf(hX + deltaX) + " ; Z: " + String.valueOf(hZ + deltaZ) + ">");
                                hm.getHistionMap().get(hId).getItems().forEach(c -> {
                                    Cell newCell = new Cell(c, newHistion.getId());
                                    c.getItems().forEach(p -> {
                                        newCell.addChild(p);
                                    });
                                    newHistion.addChild(newCell);
                                });
                                newHistion.setZCoordinate(hZ + deltaZ + zShift.get());
                                newHistion.setXCoordinate(hX + deltaX + xShift.get());
                                hm.addHistion(newHistion);
                                hZ += deltaZ;
                            }
                            hZ = hm.getHistionMap().get(hId).getZCoordinate() + num * zShift.get();
                            while (hZ - num * zShift.get() > -zBoundary) {
                                Histion newHistion = new Histion(hm.getHistionMap().get(hId));
                                newHistion.setName("Histion <X: " + String.valueOf(hX + deltaX) + " ; Z: " + String.valueOf(hZ - deltaZ) + ">");
                                hm.getHistionMap().get(hId).getItems().forEach(c -> {
                                    Cell newCell = new Cell(c, newHistion.getId());
                                    c.getItems().forEach(p -> {
                                        newCell.addChild(p);
                                    });
                                    newHistion.addChild(newCell);
                                });
                                newHistion.setZCoordinate(hZ - deltaZ + zShift.get());
                                newHistion.setXCoordinate(hX + deltaX + xShift.get());
                                hm.addHistion(newHistion);
                                hZ -= deltaZ;
                            }
                            hX += deltaX + xShift.get();
                            num++;
                            //hZ = hm.getHistionMap().get(hId).getZCoordinate();
                            //hZ += zShift.get();
                        }
                        hX = hm.getHistionMap().get(hId).getXCoordinate();
                        num = 0;
                        while (hX > -xBoundary) {
                            hZ = hm.getHistionMap().get(hId).getZCoordinate() - num * zShift.get();
                            while (hZ + num * zShift.get() < zBoundary) {
                                Histion newHistion = new Histion(hm.getHistionMap().get(hId));
                                newHistion.setName("Histion <X: " + String.valueOf(hX - deltaX) + " ; Z: " + String.valueOf(hZ + deltaZ) + ">");
                                hm.getHistionMap().get(hId).getItems().forEach(c -> {
                                    Cell newCell = new Cell(c, newHistion.getId());
                                    c.getItems().forEach(p -> {
                                        newCell.addChild(p);
                                    });
                                    newHistion.addChild(newCell);
                                });
                                newHistion.setZCoordinate(hZ + deltaZ - zShift.get());
                                newHistion.setXCoordinate(hX - deltaX - xShift.get());
                                hm.addHistion(newHistion);
                                hZ += deltaZ;
                            }
                            hZ = hm.getHistionMap().get(hId).getZCoordinate() - num * zShift.get();
                            while (hZ + num * zShift.get() > -zBoundary) {
                                Histion newHistion = new Histion(hm.getHistionMap().get(hId));
                                newHistion.setName("Histion <X: " + String.valueOf(hX - deltaX) + " ; Z: " + String.valueOf(hZ - deltaZ) + ">");
                                hm.getHistionMap().get(hId).getItems().forEach(c -> {
                                    Cell newCell = new Cell(c, newHistion.getId());
                                    c.getItems().forEach(p -> {
                                        newCell.addChild(p);
                                    });
                                    newHistion.addChild(newCell);
                                });
                                newHistion.setZCoordinate(hZ - deltaZ - zShift.get());
                                newHistion.setXCoordinate(hX - deltaX - xShift.get());
                                hm.addHistion(newHistion);
                                hZ -= deltaZ;
                            }
                            hX -= deltaX + xShift.get();
                            num++;
                            //hZ -= zShift.get();
                        }
                    }
                    
                    if ((yLimit.get() > 0) && (zLimit.get() > 0)) {
                        num = 0;
                        while (hY < yBoundary) {
                            hZ = hm.getHistionMap().get(hId).getZCoordinate();
                            while (hZ < zBoundary) {
                                //num++;
                                Histion newHistion = new Histion(hm.getHistionMap().get(hId));
                                newHistion.setName("Histion <X: " + String.valueOf(hX) + " ; Z: " + String.valueOf(hZ + deltaZ) + ">");
                                hm.getHistionMap().get(hId).getItems().forEach(c -> {
                                    Cell newCell = new Cell(c, newHistion.getId());
                                    c.getItems().forEach(p -> {
                                        newCell.addChild(p);
                                    });
                                    newHistion.addChild(newCell);
                                });
                                newHistion.setZCoordinate(hZ + deltaZ);
                                newHistion.setYCoordinate(hY + deltaY);
                                hm.addHistion(newHistion);
                                
                                if (xLimit.get() > 0) {
                                    hX = hm.getHistionMap().get(hId).getXCoordinate();
                                    while (hX < xBoundary) {
                                        Histion newHist = new Histion(hm.getHistionMap().get(hId));
                                        newHist.setName("Histion <X: " + String.valueOf(hX + deltaX) + " ; Z: " + String.valueOf(hZ + deltaZ) + ">");
                                        hm.getHistionMap().get(hId).getItems().forEach(c -> {
                                            Cell newCell = new Cell(c, newHist.getId());
                                            c.getItems().forEach(p -> {
                                                newCell.addChild(p);
                                            });
                                            newHist.addChild(newCell);
                                        });
                                        newHist.setXCoordinate(hX + deltaX + xShift.get());
                                        newHist.setZCoordinate(hZ + deltaZ + zShift.get());
                                        newHist.setYCoordinate(hY + deltaY);
                                        hm.addHistion(newHist);
                                        hX += deltaX + xShift.get();
                                        hZ += zShift.get();
                                    }
                                    hX = hm.getHistionMap().get(hId).getXCoordinate();
                                    hZ = hm.getHistionMap().get(hId).getZCoordinate() + deltaZ * num;
                                    while (hX > -xBoundary) {
                                        Histion newHist = new Histion(hm.getHistionMap().get(hId));
                                        newHist.setName("Histion <X: " + String.valueOf(hX - deltaX) + " ; Z: " + String.valueOf(hZ + deltaZ) + ">");
                                        hm.getHistionMap().get(hId).getItems().forEach(c -> {
                                            Cell newCell = new Cell(c, newHist.getId());
                                            c.getItems().forEach(p -> {
                                                newCell.addChild(p);
                                            });
                                            newHist.addChild(newCell);
                                        });
                                        newHist.setXCoordinate(hX - deltaX - xShift.get());
                                        newHist.setZCoordinate(hZ + deltaZ - zShift.get());
                                        newHist.setYCoordinate(hY + deltaY);
                                        hm.addHistion(newHist);
                                        hX -= deltaX + xShift.get();
                                        hZ -= zShift.get();
                                    }
                                }
                                num++;
                                hZ = hm.getHistionMap().get(hId).getZCoordinate() + deltaZ * num;
                                //hZ += deltaZ;
                            }
                            hZ = hm.getHistionMap().get(hId).getZCoordinate();
                            num = 0;
                            while (hZ > -zBoundary) {
                                //num++;
                                Histion newHistion = new Histion(hm.getHistionMap().get(hId));
                                newHistion.setName("Histion <X: " + String.valueOf(hX) + " ; Z: " + String.valueOf(hZ - deltaZ) + ">");
                                hm.getHistionMap().get(hId).getItems().forEach(c -> {
                                    Cell newCell = new Cell(c, newHistion.getId());
                                    c.getItems().forEach(p -> {
                                        newCell.addChild(p);
                                    });
                                    newHistion.addChild(newCell);
                                });
                                newHistion.setZCoordinate(hZ - deltaZ);
                                newHistion.setYCoordinate(hY + deltaY);
                                hm.addHistion(newHistion);
                                if (xLimit.get() > 0) {
                                    hX = hm.getHistionMap().get(hId).getXCoordinate();
                                    while (hX < xBoundary) {
                                        Histion newHist = new Histion(hm.getHistionMap().get(hId));
                                        newHist.setName("Histion <X: " + String.valueOf(hX + deltaX) + " ; Z: " + String.valueOf(hZ - deltaZ) + ">");
                                        hm.getHistionMap().get(hId).getItems().forEach(c -> {
                                            Cell newCell = new Cell(c, newHist.getId());
                                            c.getItems().forEach(p -> {
                                                newCell.addChild(p);
                                            });
                                            newHist.addChild(newCell);
                                        });
                                        newHist.setXCoordinate(hX + deltaX + xShift.get());
                                        newHist.setZCoordinate(hZ - deltaZ + zShift.get());
                                        newHist.setYCoordinate(hY + deltaY);
                                        hm.addHistion(newHist);
                                        hX += deltaX + xShift.get();
                                        hZ += zShift.get();
                                    }
                                    hX = hm.getHistionMap().get(hId).getXCoordinate();
                                    hZ = hm.getHistionMap().get(hId).getZCoordinate() - deltaZ * num;
                                    while (hX > -xBoundary) {
                                        Histion newHist = new Histion(hm.getHistionMap().get(hId));
                                        newHist.setName("Histion <X: " + String.valueOf(hX - deltaX) + " ; Z: " + String.valueOf(hZ - deltaZ) + ">");
                                        hm.getHistionMap().get(hId).getItems().forEach(c -> {
                                            Cell newCell = new Cell(c, newHist.getId());
                                            c.getItems().forEach(p -> {
                                                newCell.addChild(p);
                                            });
                                            newHist.addChild(newCell);
                                        });
                                        newHist.setXCoordinate(hX - deltaX - xShift.get());
                                        newHist.setZCoordinate(hZ - deltaZ - zShift.get());
                                        newHist.setYCoordinate(hY + deltaY);
                                        hm.addHistion(newHist);
                                        hX -= deltaX + xShift.get();
                                        hZ -= zShift.get();
                                    }
                                }
                                //hZ -= deltaZ;
                                num++;
                                hZ = hm.getHistionMap().get(hId).getZCoordinate() - deltaZ * num;
                            }
                            hY += deltaY;
                        }
                        hY = hm.getHistionMap().get(hId).getYCoordinate();
                        while (hY > -yBoundary) {

                            num = 0;
                            hZ = hm.getHistionMap().get(hId).getZCoordinate();
                            while (hZ < zBoundary) {
                                Histion newHistion = new Histion(hm.getHistionMap().get(hId));
                                newHistion.setName("Histion <X: " + String.valueOf(hX) + " ; Z: " + String.valueOf(hZ + deltaZ) + ">");
                                hm.getHistionMap().get(hId).getItems().forEach(c -> {
                                    Cell newCell = new Cell(c, newHistion.getId());
                                    c.getItems().forEach(p -> {
                                        newCell.addChild(p);
                                    });
                                    newHistion.addChild(newCell);
                                });
                                newHistion.setZCoordinate(hZ + deltaZ);
                                newHistion.setYCoordinate(hY - deltaY);
                                hm.addHistion(newHistion);
                                
                                if (xLimit.get() > 0) {
                                    hX = hm.getHistionMap().get(hId).getXCoordinate();
                                    while (hX < xBoundary) {
                                        Histion newHist = new Histion(hm.getHistionMap().get(hId));
                                        newHist.setName("Histion <X: " + String.valueOf(hX + deltaX) + " ; Z: " + String.valueOf(hZ + deltaZ) + ">");
                                        hm.getHistionMap().get(hId).getItems().forEach(c -> {
                                            Cell newCell = new Cell(c, newHist.getId());
                                            c.getItems().forEach(p -> {
                                                newCell.addChild(p);
                                            });
                                            newHist.addChild(newCell);
                                        });
                                        newHist.setXCoordinate(hX + deltaX + xShift.get());
                                        newHist.setZCoordinate(hZ + deltaZ + zShift.get());
                                        newHist.setYCoordinate(hY - deltaY);
                                        hm.addHistion(newHist);
                                        hX += deltaX + xShift.get();
                                        hZ += zShift.get();
                                    }
                                    hX = hm.getHistionMap().get(hId).getXCoordinate();
                                    hZ = hm.getHistionMap().get(hId).getZCoordinate() + deltaZ * num;
                                    while (hX > -xBoundary) {
                                        Histion newHist = new Histion(hm.getHistionMap().get(hId));
                                        newHist.setName("Histion <X: " + String.valueOf(hX - deltaX) + " ; Z: " + String.valueOf(hZ + deltaZ) + ">");
                                        hm.getHistionMap().get(hId).getItems().forEach(c -> {
                                            Cell newCell = new Cell(c, newHist.getId());
                                            c.getItems().forEach(p -> {
                                                newCell.addChild(p);
                                            });
                                            newHist.addChild(newCell);
                                        });
                                        newHist.setXCoordinate(hX - deltaX - xShift.get());
                                        newHist.setZCoordinate(hZ + deltaZ - zShift.get());
                                        newHist.setYCoordinate(hY - deltaY);
                                        hm.addHistion(newHist);
                                        hX -= deltaX + xShift.get();
                                        hZ -= zShift.get();
                                    }
                                }
                                
                                //hZ += deltaZ;
                                num++;
                                hZ = hm.getHistionMap().get(hId).getZCoordinate() + deltaZ * num;
                                
                            }
                            hZ = hm.getHistionMap().get(hId).getZCoordinate();
                            num = 0;
                            while (hZ > -zBoundary) {
                                Histion newHistion = new Histion(hm.getHistionMap().get(hId));
                                newHistion.setName("Histion <X: " + String.valueOf(hX) + " ; Z: " + String.valueOf(hZ - deltaZ) + ">");
                                hm.getHistionMap().get(hId).getItems().forEach(c -> {
                                    Cell newCell = new Cell(c, newHistion.getId());
                                    c.getItems().forEach(p -> {
                                        newCell.addChild(p);
                                    });
                                    newHistion.addChild(newCell);
                                });
                                newHistion.setZCoordinate(hZ - deltaZ);
                                newHistion.setYCoordinate(hY - deltaY);
                                hm.addHistion(newHistion);
                                
                                if (xLimit.get() > 0) {
                                    hX = hm.getHistionMap().get(hId).getXCoordinate();
                                    while (hX < xBoundary) {
                                        Histion newHist = new Histion(hm.getHistionMap().get(hId));
                                        newHist.setName("Histion <X: " + String.valueOf(hX + deltaX) + " ; Z: " + String.valueOf(hZ - deltaZ) + ">");
                                        hm.getHistionMap().get(hId).getItems().forEach(c -> {
                                            Cell newCell = new Cell(c, newHist.getId());
                                            c.getItems().forEach(p -> {
                                                newCell.addChild(p);
                                            });
                                            newHist.addChild(newCell);
                                        });
                                        newHist.setXCoordinate(hX + deltaX + xShift.get());
                                        newHist.setZCoordinate(hZ - deltaZ + zShift.get());
                                        newHist.setYCoordinate(hY - deltaY);
                                        hm.addHistion(newHist);
                                        hX += deltaX + xShift.get();
                                        hZ += zShift.get();
                                    }
                                    hX = hm.getHistionMap().get(hId).getXCoordinate();
                                    hZ = hm.getHistionMap().get(hId).getZCoordinate() - deltaZ * num;
                                    while (hX > -xBoundary) {
                                        Histion newHist = new Histion(hm.getHistionMap().get(hId));
                                        newHist.setName("Histion <X: " + String.valueOf(hX - deltaX) + " ; Z: " + String.valueOf(hZ - deltaZ) + ">");
                                        hm.getHistionMap().get(hId).getItems().forEach(c -> {
                                            Cell newCell = new Cell(c, newHist.getId());
                                            c.getItems().forEach(p -> {
                                                newCell.addChild(p);
                                            });
                                            newHist.addChild(newCell);
                                        });
                                        newHist.setXCoordinate(hX - deltaX - xShift.get());
                                        newHist.setZCoordinate(hZ - deltaZ - zShift.get());
                                        newHist.setYCoordinate(hY - deltaY);
                                        hm.addHistion(newHist);
                                        hX -= deltaX + xShift.get();
                                        hZ -= zShift.get();
                                    }
                                }
                                
                                //hZ -= deltaZ;
                                num++;
                                hZ = hm.getHistionMap().get(hId).getZCoordinate() - deltaZ * num;
                            }
                            hY -= deltaY;
                        }
                        hX = hm.getHistionMap().get(hId).getXCoordinate();
                        hZ = hm.getHistionMap().get(hId).getZCoordinate();
                    }
                    
                    if ((xLimit.get() > 0) && (yLimit.get() > 0)) {
                        while (hX < xBoundary) {
                            hY = hm.getHistionMap().get(hId).getYCoordinate();
                            while (hY < yBoundary) {
                                Histion newHistion = new Histion(hm.getHistionMap().get(hId));
                                newHistion.setName("Histion <X: " + String.valueOf(hX + deltaX) + " ; Z: " + String.valueOf(hZ) + ">");
                                hm.getHistionMap().get(hId).getItems().forEach(c -> {
                                    Cell newCell = new Cell(c, newHistion.getId());
                                    c.getItems().forEach(p -> {
                                        newCell.addChild(p);
                                    });
                                    newHistion.addChild(newCell);
                                });
                                newHistion.setYCoordinate(hY + deltaY);
                                newHistion.setXCoordinate(hX + deltaX  + xShift.get());
                                newHistion.setZCoordinate(hZ + zShift.get());
                                hm.addHistion(newHistion);
                                hY += deltaY;
                            }
                            hY = hm.getHistionMap().get(hId).getYCoordinate();
                            while (hY > -yBoundary) {
                                Histion newHistion = new Histion(hm.getHistionMap().get(hId));
                                newHistion.setName("Histion <X: " + String.valueOf(hX + deltaX) + " ; Z: " + String.valueOf(hZ) + ">");
                                hm.getHistionMap().get(hId).getItems().forEach(c -> {
                                    Cell newCell = new Cell(c, newHistion.getId());
                                    c.getItems().forEach(p -> {
                                        newCell.addChild(p);
                                    });
                                    newHistion.addChild(newCell);
                                });
                                newHistion.setYCoordinate(hY - deltaY);
                                newHistion.setXCoordinate(hX + deltaX + xShift.get());
                                newHistion.setZCoordinate(hZ + zShift.get());
                                hm.addHistion(newHistion);
                                hY -= deltaY;
                            }
                            hX += deltaX + xShift.get();
                            hZ += zShift.get();
                        }
                        hX = hm.getHistionMap().get(hId).getXCoordinate();
                        hZ = hm.getHistionMap().get(hId).getXCoordinate();
                        while (hX > -xBoundary) {

                            hY = hm.getHistionMap().get(hId).getYCoordinate();
                            while (hY < yBoundary) {
                                Histion newHistion = new Histion(hm.getHistionMap().get(hId));
                                newHistion.setName("Histion <X: " + String.valueOf(hX - deltaX) + " ; Z: " + String.valueOf(hZ) + ">");
                                hm.getHistionMap().get(hId).getItems().forEach(c -> {
                                    Cell newCell = new Cell(c, newHistion.getId());
                                    c.getItems().forEach(p -> {
                                        newCell.addChild(p);
                                    });
                                    newHistion.addChild(newCell);
                                });
                                newHistion.setYCoordinate(hY + deltaY);
                                newHistion.setXCoordinate(hX - deltaX - xShift.get());
                                newHistion.setZCoordinate(hZ - zShift.get());
                                hm.addHistion(newHistion);
                                hY += deltaY;
                            }
                            hY = hm.getHistionMap().get(hId).getYCoordinate();
                            while (hY > -yBoundary) {
                                Histion newHistion = new Histion(hm.getHistionMap().get(hId));
                                newHistion.setName("Histion <X: " + String.valueOf(hX - deltaX) + " ; Z: " + String.valueOf(hZ) + ">");
                                hm.getHistionMap().get(hId).getItems().forEach(c -> {
                                    Cell newCell = new Cell(c, newHistion.getId());
                                    c.getItems().forEach(p -> {
                                        newCell.addChild(p);
                                    });
                                    newHistion.addChild(newCell);
                                });
                                newHistion.setYCoordinate(hY - deltaY);
                                newHistion.setXCoordinate(hX - deltaX - xShift.get());
                                newHistion.setZCoordinate(hZ - zShift.get());
                                hm.addHistion(newHistion);
                                hY -= deltaY;
                            }
                            hX -= deltaX + xShift.get();
                            hZ -= zShift.get();
                        }
                    }
                    disableEverything.set(true);
                    pastePartDisabledProperty.set(true);
                    pasteCellDisabledProperty.set(true);
                } else {
                    hm.getAllHistions().forEach(h -> {
                        if (h.getId() != 0) {
                            h.getItems().forEach(c -> {
                                hm.getHistionMap().get(h.getId()).deleteChild(c.getId());
                            });
                            hm.deleteHistion(h.getId());
                        }
                    });
                    disableEverything.set(false);
                }
            });
            MenuItem pasteCell = new MenuItem();
            pasteCell.setText("Paste cell");
            pasteCell.setOnAction(event -> {
                Cell newCell = new Cell(hm.getHistionMap().get(0).getItemMap().get(cellId), 0);
                String name = newCell.getName();
                name = name.substring(name.indexOf("<") + 1, name.lastIndexOf(">"));
                int count = 1;
                while(Names.containsCellName(name)) {
                    name = newCell.getName();
                    name = name.substring(name.indexOf("<") + 1, name.lastIndexOf(">"));
                    name += "(" + count + ")";
                    count++;
                }
                newCell.setName("Cell <" + name + ">");
                Names.addCellName(name);
                hm.getHistionMap().get(0).getItemMap().get(cellId).getItems().forEach(p -> {
                    newCell.addChild(new Part(p, newCell.getId()));
                });
                hm.getHistionMap().get(this.getValue().getId()).addChild(newCell);
            });
            pasteCell.disableProperty().bind(pasteCellDisabledProperty);
            
            loadHistion.disableProperty().bind(disableEverything);
            loadCell.disableProperty().bind(disableEverything);
            editHistion.disableProperty().bind(disableEverything);
            addCell.disableProperty().bind(disableEverything);
            if (hm.getAllHistions().size() > 1) {
                fillModel.setSelected(true);
            }
            histionMenu = new ContextMenu(loadHistion, saveHistion, editHistion, loadCell, addCell, pasteCell, fillModel);
            return histionMenu;
            //return new ContextMenu(loadHistion, saveHistion, editHistion, loadCell, addCell, pasteCell, fillModel);
        }
    }

    public class CellTreeItem extends AbstractTreeItem {

        public CellTreeItem(HistologyObject<?> object) {
            this.setValue(object);
            this.setExpanded(true);
        }

        @Override
        public ContextMenu getMenu() {
            Cell c = (Cell)this.getValue();
            MenuItem saveCell = new MenuItem();
            saveCell.setText("Save cell");
            saveCell.setOnAction(event -> {
                SaveCellBox.display(c.getId());
            });
            MenuItem editCell = new MenuItem();
            editCell.setText("Edit cell");
            editCell.setOnAction(event -> {
                CellInformationInitialization.createScene(hm.getHistionMap().
                        get(c.getHistionId()).getItemMap().
                        get(c.getId()));
            });
            MenuItem loadPart = new MenuItem();
            loadPart.setText("Load part");
            loadPart.setOnAction(event -> {
                LoadPartBox.display(c.getId());
                //SaveBox.display();
            });
            MenuItem addPart = new MenuItem();
            addPart.setText("Add part");
            addPart.setOnAction(event -> {
                PartInformationInitialization.show(c.getId(), -1);
            });
            MenuItem copyCell = new MenuItem();
            copyCell.setText("Copy cell");
            copyCell.setOnAction(event -> {
                pastePartDisabledProperty.set(true);
                pasteCellDisabledProperty.set(false);
                cellId = c.getId();
                partId = -1;
            });
            MenuItem pastePart = new MenuItem();
            pastePart.setText("Paste part");
            pastePart.setOnAction(event -> {
                Integer newHistionId = c.getHistionId();
                Integer newCellId = c.getId();
                Part newPart = new Part(hm.getHistionMap().get(0).
                                getItemMap().get(cellId).getItemMap().get(partId), newCellId);
                String name = newPart.getName();
                name = name.substring(name.indexOf("<") + 1, name.lastIndexOf(">"));
                name += "(Copy)";
                newPart.setName("Part <" + name + ">");
                hm.getHistionMap().get(newHistionId).getItemMap().get(newCellId).
                        addChild(newPart);
            });
            pastePart.disableProperty().bind(pastePartDisabledProperty);
            CheckMenuItem hideCell = new CheckMenuItem();
            hideCell.setText("Hide cell");
            hideCell.setOnAction(event -> {
                if (hm.getHistionMap().get(c.getHistionId()).getItemMap().get(c.getId()).getShow()) {
                    if (hideCell.isSelected()) {
                        String name = c.getName();
                        name = name.substring(name.indexOf("<") + 1, name.lastIndexOf(">"));
                        HideCells.addCellNameToHide(name);
                    } else {
                        String name = c.getName();
                        name = name.substring(name.indexOf("<") + 1, name.lastIndexOf(">"));
                        HideCells.removeCellNameToHide(name);
                    }
                } else if (hideCell.isSelected())
                    hideCell.setSelected(false);
            });
            MenuItem deleteCell = new MenuItem();
            deleteCell.setText("Delete cell");
            deleteCell.setOnAction(event -> {
                pastePartDisabledProperty.set(true);
                pasteCellDisabledProperty.set(true);
                ConfirmBox.display("Delete Confirmation", "Are you sure you want to delete " 
                        + c.getName(), c.getId(), -1);
            });
            
            String name = c.getName();
            name = name.substring(name.indexOf("<") + 1, name.lastIndexOf(">"));
            if (HideCells.getCellNameToHideList().contains(name)) {
                hideCell.setSelected(true);
            }
            
            loadPart.disableProperty().bind(disableEverything);
            editCell.disableProperty().bind(disableEverything);
            addPart.disableProperty().bind(disableEverything);
            deleteCell.disableProperty().bind(disableEverything);
            copyCell.disableProperty().bind(disableEverything);
            
            return new ContextMenu(editCell, saveCell, loadPart, addPart, copyCell, pastePart, hideCell, deleteCell);
        }
    }

    public class PartTreeItem extends AbstractTreeItem {

        public PartTreeItem(HistologyObject<?> object) {
            this.setValue(object);
            this.setExpanded(true);
        }

        @Override
        public ContextMenu getMenu() {
            Part p = (Part)this.getValue();
            MenuItem savePart = new MenuItem();
            savePart.setText("Save part");
            savePart.setOnAction(event -> {
                SavePartBox.display(p.getCellId(), p.getId());
            });
            MenuItem editPart = new MenuItem();
            editPart.setText("Edit part");
            editPart.setOnAction(event -> {
                PartInformationInitialization.show(p.getCellId(), p.getId());
            });
            MenuItem copyPart = new MenuItem();
            copyPart.setText("Copy part");
            copyPart.setOnAction(event -> {
                pastePartDisabledProperty.set(false);
                pasteCellDisabledProperty.set(true);
                cellId = p.getCellId();
                partId = p.getId();
            });
            MenuItem deletePart = new MenuItem();
            deletePart.setText("Delete part");
            deletePart.setOnAction(event -> {
                pastePartDisabledProperty.set(true);
                pasteCellDisabledProperty.set(true);
                ConfirmBox.display("Delete Confirmation", "Are you sure you want to delete " +
                        p.getName(), p.getCellId(), p.getId());
            });
            
            editPart.disableProperty().bind(disableEverything);
            copyPart.disableProperty().bind(disableEverything);
            deletePart.disableProperty().bind(disableEverything);
            
            return new ContextMenu(editPart, savePart, copyPart, deletePart);
        }
    }
    
    private final class TreeCellImpl extends TreeCell<HistologyObject<?>> {

        @Override
        public void updateItem(HistologyObject<?> item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) {
                setText(null);
                setGraphic(null);
            } else {
                setText(getItem() == null ? "" : getItem().getName());
                setGraphic(getTreeItem().getGraphic());
                setContextMenu(((AbstractTreeItem) getTreeItem()).getMenu());
            }
        }
    }
    
    boolean SameSide(Point3D v1, Point3D v2, Point3D v3, Point3D v4, Point3D p)
    {
        Point3D p1 = v2.subtract(v1);
        Point3D p2 = v3.subtract(v1);
        Point3D normal = p1.crossProduct(p2);
        double dotV4 = normal.dotProduct(v4.subtract(v1));
        double dotP = normal.dotProduct(p.subtract(v1));
        if (Math.signum(dotV4) * Math.signum(dotP) < 0)
            return false;
        return true;
    }

    boolean PointInTetrahedron(Point3D v1, Point3D v2, Point3D v3, Point3D v4, Point3D p)
    {
        return SameSide(v1, v2, v3, v4, p) &&
               SameSide(v2, v3, v4, v1, p) &&
               SameSide(v3, v4, v1, v2, p) &&
               SameSide(v4, v1, v2, v3, p);   
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        hm = Lookup.getDefault().lookup(HistionManager.class);
        if (hm == null) {
            LifecycleManager.getDefault().exit();
        }
        
        if (hm.getHistionMap().isEmpty())
            //hm.addHistion(new Histion("Main histion",0,0,0,0,0));
            hm.addHistion(new Histion("Main histion",0,0,0));
        
        if (hm.getAllHistions().size() > 1) {
            disableEverything.set(true);
            pastePartDisabledProperty.set(true);
            pasteCellDisabledProperty.set(true);
        }
        
        //System.out.println(partTreeItemMap.size());
        
        histion = new HistionTreeItem(hm.getHistionMap().get(0));
        
        hm.getHistionMap().get(0).getItemMap().addListener(cellListener);
        hm.getHistionMap().get(0).getItems().forEach(c -> {
            CellTreeItem cti = new CellTreeItem(c);
            histion.getChildren().add(cti);
            cellTreeItemMap.put(c.getId(), cti);
            c.getItemMap().addListener(partListener);
            c.getItems().forEach(p -> {
                PartTreeItem pti = new PartTreeItem(p);
                //histion.getChildren().get(c.getId()).getChildren().add(pti);
                histion.getChildren().forEach(cell -> {
                    if (cell.getValue().getId() == c.getId()) {
                        cell.getChildren().add(pti);
                    }
                });
                partTreeItemMap.put(p.getId(), pti);
            });
        });
        
        TreeItem treeRoot = new TreeItem();
        treeRoot.setExpanded(true);
        treeRoot.getChildren().addAll(histion);
        shapeTreeView.setRoot(treeRoot);
        shapeTreeView.setShowRoot(false);
        shapeTreeView.setCellFactory(new Callback<TreeView<HistologyObject<?>>, TreeCell<HistologyObject<?>>>() {
            @Override
            public TreeCell<HistologyObject<?>> call(TreeView<HistologyObject<?>> p) {
                return new TreeCellImpl();
            }
        });
        
    }
    
    /*private void applyTransformations(double xRot, double yRot, 
            Point3D nodeAvg, ObservableList<TetgenPoint> pointData) {
        double ang, tempVal;
        for (int i = 0; i < pointData.size(); i++) {
            TetgenPoint pd = new TetgenPoint(pointData.get(i));
            
            pd.setX(pd.getX() - nodeAvg.getX());
            pd.setY(pd.getY() - nodeAvg.getY());
            pd.setZ(pd.getZ() - nodeAvg.getZ());
            
            ang = Math.toRadians(xRot);
            tempVal = pd.getY();
            pd.setY(pd.getY() * Math.cos(ang) - pd.getZ() * Math.sin(ang));
            pd.setZ(tempVal * Math.sin(ang) + pd.getZ() * Math.cos(ang));
            
            ang = Math.toRadians(yRot);
            tempVal = pd.getX();
            pd.setX(pd.getX() * Math.cos(ang) + pd.getZ() * Math.sin(ang));
            pd.setZ(-tempVal * Math.sin(ang) + pd.getZ() * Math.cos(ang));
            
            pointData.set(i, pd);
        }
    }*/
    
    public void setTreeViewSize(int width, int height) {
        shapeTreeView.setPrefSize(width, height);
    }
    
    public void removeListeners() {
        hm.getHistionMap().get(0).getItemMap().removeListener(cellListener);
        hm.getHistionMap().get(0).getItems().forEach(c -> {
            c.getItemMap().removeListener(partListener);
        });
    }
    
}
