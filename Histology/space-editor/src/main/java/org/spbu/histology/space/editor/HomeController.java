package org.spbu.histology.space.editor;

import org.spbu.histology.model.ShapeManager;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import static java.util.stream.Collectors.toList;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
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
import javafx.collections.ListChangeListener;
import javafx.geometry.Point3D;
import javafx.scene.control.CheckMenuItem;
import org.openide.LifecycleManager;
import org.openide.util.Lookup;
import org.spbu.histology.model.CameraView;
import org.spbu.histology.model.Cell;
import org.spbu.histology.model.HideCells;
import org.spbu.histology.model.Histion;
import org.spbu.histology.model.HistionManager;
import org.spbu.histology.model.HistologyObject;
import org.spbu.histology.model.Names;
import org.spbu.histology.model.Part;
import org.spbu.histology.model.UpdateTree;
import org.spbu.histology.model.PolygonList;
import org.spbu.histology.model.TetgenPoint;
import org.spbu.histology.shape.information.PartInformationInitialization;
import org.spbu.histology.shape.information.HistionInformationInitialization;
import org.spbu.histology.toolbar.ChosenTool;

public class HomeController implements Initializable {
    
    //private ShapeManager sm = null;
    
    private HistionManager hm = null;
    
    @FXML
    private TreeView<HistologyObject<?>> shapeTreeView;
    
    private TreeItem<String> rootNode;
    
    private final MapChangeListener<Integer, Histion> histionListener =
        (change) -> {
            if (change.wasAdded()) {
                //Histion addedHistion = change.getValueAdded();
                if (change.getValueAdded().getItems().isEmpty())
                    updateTree();
            }
        };
    
    /*private static BooleanProperty update = new SimpleBooleanProperty(false);
    public static boolean getUpdate() {
        return update.get();
    }
    public static void setUpdate(boolean upd) {
        update.set(upd);
    }*/
    
    private Integer histionId;
    private Integer cellId;
    private Integer partId;
    private BooleanProperty pasteCellDisabledProperty = new SimpleBooleanProperty(true);
    private BooleanProperty pastePartDisabledProperty = new SimpleBooleanProperty(true);
    
    /*private void updateTree() {
        if (ChosenTool.getToolNumber() == -1)
            ChosenTool.setToolNumber(-2);
        else
            ChosenTool.setToolNumber(-1);
    }*/
    
    public void updateTree() {
    //private void createTree() {
        shapeTreeView.setRoot(null);
            HistologyObject<?> root = new HistologyObject<Histion>(-1, "Tissue") {

                @Override
                public ObservableList<Histion> getItems() {
                    return getObservableList();
                }

                @Override
                public void addChild(Histion h) {
                    getItemMap().put(h.getId(), new Histion(h.getId(), h));
                }
                
                @Override
                public void deleteChild(Integer id) {
                    getItemMap().remove(id);
                }

            };
        TreeItem<HistologyObject<?>> treeRoot = createItem(root);

        shapeTreeView.setRoot(treeRoot);
        shapeTreeView.setShowRoot(false);
    }
    
    private ObservableList<Histion> getObservableList() {
        
        ObservableList<Histion> histionList = FXCollections.observableArrayList();
        
        if (hm.getAllHistions().size() > 0)
            histionList.add(hm.getHistionMap().get(0));
        
        hm.getHistionMap().get(0).getItems().forEach(c -> {
            System.out.println(c.getId());
        });
        
        /*hm.getAllHistions().forEach(h -> {
            histionList.add(h);
        });*/
        
        return histionList;
    }
    
    private TreeItem<HistologyObject<?>> createItem(HistologyObject<?> object) {

        // create tree item with children from game object's list:

        TreeItem<HistologyObject<?>> item = new TreeItem<>(object);
        //if (item.getValue().getId())
        item.setExpanded(true);
        //item.setExpanded(false);
        //item.getChildren().addAll(object.getItems().stream().map(this::createItem).collect(toList()));
        //object.getItems().stream().map(this::createItem).
        item.getChildren().addAll(object.getItems().stream().map(this::createItem).collect(toList()));

        // update tree item's children list if game object's list changes:

        /*object.getItems().addListener((ListChangeListener.Change<? extends HistologyObject<?>> c) -> {
            while (c.next()) {
                if (c.wasAdded()) {
                    item.getChildren().addAll(c.getAddedSubList().stream().map(this::createItem).collect(toList()));
                }
                if (c.wasRemoved()) {
                    item.getChildren().removeIf(treeItem -> c.getRemoved().contains(treeItem.getValue()));
                }
            }
        });*/

        return item ;
    }
    
    boolean SameSide(Point3D v1, Point3D v2, Point3D v3, Point3D v4, Point3D p)
    {
        Point3D p1 = v2.subtract(v1);
        Point3D p2 = v3.subtract(v1);
        Point3D normal = p1.crossProduct(p2);
        double dotV4 = normal.dotProduct(v4.subtract(v1));
        double dotP = normal.dotProduct(p.subtract(v1));
        //System.out.println(Math.signum(dotV4));
        //System.out.println(Math.signum(dotP));
        if (Math.signum(dotV4) * Math.signum(dotP) < 0)
            return false;
        return true;
        //return Math.signum(dotV4) == Math.signum(dotP);
    }

    boolean PointInTetrahedron(Point3D v1, Point3D v2, Point3D v3, Point3D v4, Point3D p)
    {
        //System.out.println("--------");
        return SameSide(v1, v2, v3, v4, p) &&
               SameSide(v2, v3, v4, v1, p) &&
               SameSide(v3, v4, v1, v2, p) &&
               SameSide(v4, v1, v2, v3, p);   
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        /*update.addListener((o, ov, nv) -> {
            if (nv) {
                updateTree();
                update.set(false);
            }
        });*/
        
        /*sm = Lookup.getDefault().lookup(ShapeManager.class);
        if (sm == null) {
            LifecycleManager.getDefault().exit();
        }*/
        
        hm = Lookup.getDefault().lookup(HistionManager.class);
        if (hm == null) {
            LifecycleManager.getDefault().exit();
        }
        
        if (hm.getHistionMap().isEmpty())
            hm.addHistion(new Histion("Main histion",0,0,0,0,0));
        
        //hm.addListener(histionListener);
        /*UpdateTree.shouldBeUpdated.addListener((o, ov, nv) -> {
            if (nv) {
                System.out.println("Updated");
                updateTree();
                UpdateTree.shouldBeUpdated.set(false);
            }
        });*/
        updateTree();
        //createTree();
        
        shapeTreeView.setCellFactory(tv -> {

            TreeCell<HistologyObject<?>> cell = new TreeCell<HistologyObject<?>>() {

                @Override
                protected void updateItem(HistologyObject<?> item, boolean empty) {
                    super.updateItem(item, empty);
                    textProperty().unbind();
                    if (empty) {
                        setText(null);
                    } else {
                        textProperty().bind(item.nameProperty());
                    }
                }
            };
            
            /*MenuItem addHistion = new MenuItem();
            addHistion.setText("Add histion");
            addHistion.setOnAction(event -> {
                AddBox.display("Add Histion", "Histion name", -1);
            });*/
            /*MenuItem pasteHistion = new MenuItem();
            pasteHistion.setText("Paste histion");
            pasteHistion.setOnAction(event -> {
                Histion newHistion = new Histion(hm.getHistionMap().get(histionId));
                hm.getHistionMap().get(histionId).getItems().forEach(c -> {
                    Cell newCell = new Cell(c, newHistion.getId());
                    c.getItems().forEach(p -> {
                        newCell.addChild(new Part(p));
                    });
                    newHistion.addChild(newCell);
                });
                hm.addHistion(newHistion);
                /*hm.getHistionMap().get(histionId).getItems().forEach(c -> {
                    Cell newCell = new Cell(c, newHistion.getId());
                    boolean sh = newCell.getShow();
                    newCell.setCopiedId(c.getId());
                    newCell.setShow(false);
                    hm.getHistionMap().get(newHistion.getId()).addChild(newCell);
                    c.getItems().forEach(p -> {
                        hm.getHistionMap().get(newHistion.getId()).getItemMap().
                                get(newCell.getId()).addChild(new Part(p));
                    });
                    newCell.setShow(sh);
                });*/
             //   updateTree();
            //});
            //pasteHistion.disableProperty().bind(pasteHistionDisabledProperty);
            /*MenuItem fillTissue = new MenuItem();
            fillTissue.setText("Fill tissue");
            fillTissue.setOnAction(event -> {
                //int hId = cell.getTreeItem().getValue().getId();
                final DoubleProperty sumX = new SimpleDoubleProperty(0);
                final DoubleProperty sumZ = new SimpleDoubleProperty(0);
                final DoubleProperty leftX = new SimpleDoubleProperty(10000);
                final DoubleProperty rightX = new SimpleDoubleProperty(-10000);
                final DoubleProperty upperZ = new SimpleDoubleProperty(-10000);
                final DoubleProperty bottomZ = new SimpleDoubleProperty(10000);
                hm.getAllHistions().forEach(h -> {
                    h.getItems().forEach(c -> {
                        if (c.getShow()) {
                            c.getItems().forEach(p -> {
                                p.getPointData().forEach(point -> {
                                    sumX.set(sumX.get() + point.getX());
                                    sumZ.set(sumZ.get() + point.getZ());
                                    if (point.getX() < leftX.get())
                                        leftX.set(point.getX());
                                    if (point.getX() > rightX.get())
                                        rightX.set(point.getX());
                                    if (point.getZ() < bottomZ.get())
                                        bottomZ.set(point.getZ());
                                    if (point.getZ() > upperZ.get())
                                        upperZ.set(point.getZ());
                                });
                            });
                        }
                    });
                });
                double deltaX = rightX.get() - leftX.get();
                double deltaZ = upperZ.get() - bottomZ.get();
                //int h = (int)Math.round((2000 / delta - 1));
                //System.out.println(h);
                double h = hm.getHistionMap().get(hId).getXCoordinate();
                while (h < 900) {
                    Histion newHistion = new Histion(hm.getHistionMap().get(hId));
                    hm.getHistionMap().get(hId).getItems().forEach(c -> {
                        Cell newCell = new Cell(c, newHistion.getId());
                        c.getItems().forEach(p -> {
                            newCell.addChild(new Part(p));
                        });
                        newHistion.addChild(newCell);
                    });
                    newHistion.setXCoordinate(h + delta);
                    hm.addHistion(newHistion);
                    h += delta;
                }
                h = hm.getHistionMap().get(hId).getXCoordinate();
                while (h > -900) {
                    Histion newHistion = new Histion(hm.getHistionMap().get(hId));
                    hm.getHistionMap().get(hId).getItems().forEach(c -> {
                        Cell newCell = new Cell(c, newHistion.getId());
                        c.getItems().forEach(p -> {
                            newCell.addChild(new Part(p));
                        });
                        newHistion.addChild(newCell);
                    });
                    newHistion.setXCoordinate(h - delta);
                    hm.addHistion(newHistion);
                    h -= delta;
                }
                updateTree();
            });*/
            //tissueContextMenu.getItems().addAll(addHistion, pasteHistion);
            
            MenuItem editHistion = new MenuItem();
            editHistion.setText("Edit histion");
            editHistion.setOnAction(event -> {
                HistionInformationInitialization.createScene(cell.getTreeItem().getValue().getId());
            });
            MenuItem addCell = new MenuItem();
            addCell.setText("Add cell");
            addCell.setOnAction(event -> {
                //AddBox.display("Add Cell", "Cell name", cell.getTreeItem().getValue().getId());
                /*HistionInformationInitialization.createScene(cell.
                        getTreeItem().getParent().getValue().getId(), 
                        cell.getTreeItem().getValue().getId(), -1);*/
                AddBox.display("Add Cell", "Cell name", cell.
                        getTreeItem().getValue().getId());
                updateTree();
            });
            /*MenuItem copyHistion = new MenuItem();
            copyHistion.setText("Copy histion");
            copyHistion.setOnAction(event -> {
                pastePartDisabledProperty.set(true);
                pasteCellDisabledProperty.set(true);
                pasteHistionDisabledProperty.set(false);
                histionId = cell.getTreeItem().getValue().getId();
                cellId = -1;
                partId = -1;
            });*/
            CheckMenuItem fillModel = new CheckMenuItem();
            fillModel.setText("Fill model");
            fillModel.setOnAction(event -> {
                if (fillModel.isSelected()) {
                    //ArrayList<Point3D> pointList = new ArrayList<>();
                    int hId = cell.getTreeItem().getValue().getId();
                    final DoubleProperty leftX = new SimpleDoubleProperty(10000);
                    final DoubleProperty rightX = new SimpleDoubleProperty(-10000);
                    final DoubleProperty upperZ = new SimpleDoubleProperty(-10000);
                    final DoubleProperty bottomZ = new SimpleDoubleProperty(10000);
                    final DoubleProperty upperY = new SimpleDoubleProperty(-10000);
                    final DoubleProperty bottomY = new SimpleDoubleProperty(10000);
                    /*ObservableList<TetgenPoint> pointData = FXCollections.observableArrayList();
                    double xRot = c.getXRotate();
                    double yRot = c.getYRotate();
                    double xTran = c.getXCoordinate();
                    double yTran = c.getYCoordinate();
                    double zTran = c.getZCoordinate();

                    final IntegerProperty dataSize = new SimpleIntegerProperty(0);
                    final Point3D nodeAvg = new Point3D(0, 0, 0);
                    Cell cellValue = hm.getHistionMap().get(c.getHistionId()).getItemMap().get(c.getId());
                    hm.getHistionMap().get(c.getHistionId()).getItemMap().get(c.getId()).getItems().forEach(p -> {
                        ObservableList<TetgenPoint> data = FXCollections.observableArrayList();
                        for (TetgenPoint point : p.getPointData()) {
                                data.add(new TetgenPoint(point));
                            }
                        dataSize += data.size();
                        for (int i = 0; i < data.size(); i++) {
                            nodeAvg.add(data.get(i).getX() + cellValue.getXCoordinate(), data.get(i).getY() + cellValue.getYCoordinate(), data.get(i).getZ() + cellValue.getZCoordinate());
                        }
                    });
                    nodeAvg = new Point3D(nodeAvg.getX() / dataSize, nodeAvg.getY() / dataSize, nodeAvg.getZ() / dataSize);

                    applyTransformations(xRot, yRot, xTran, yTran, zTran, nodeAvg, pointData);*/
                    
                    /*ObservableList<TetgenPoint> pointData = FXCollections.observableArrayList();
                    hm.getHistionMap().get(hId).getItems().forEach(c -> {
                        if (c.getShow()) {
                            c.getTransformedPointData().forEach(point -> {
                                pointData.add(new TetgenPoint(point));
                            });
                        }
                    });
                    dataSize = 0;
                    nodeAvg = new Point3D(0, 0, 0);
                    Histion h = hm.getHistionMap().get(hId);
                    h.getItems().forEach(c -> {
                        if (c.getShow()) {
                            for (TetgenPoint point : c.getTransformedPointData()) {
                                nodeAvg = new Point3D(nodeAvg.getX() + point.getX(), nodeAvg.getY() + point.getY(), nodeAvg.getZ() + point.getZ());
                            }
                            dataSize += c.getTransformedPointData().size();
                        }
                    });
                    nodeAvg = new Point3D(nodeAvg.getX() / dataSize, nodeAvg.getY() / dataSize, nodeAvg.getZ() / dataSize);
                    double xRot = h.getXRotate();
                    double yRot = h.getYRotate();
                    double xTran = h.getXCoordinate();
                    double yTran = h.getYCoordinate();
                    double zTran = h.getZCoordinate();
                    applyTransformations(xRot, yRot, xTran, yTran, zTran, nodeAvg, pointData);
                    for (TetgenPoint point : pointData) {
                        if (point.getX() < leftX.get())
                            leftX.set(point.getX());
                        if (point.getX() > rightX.get())
                            rightX.set(point.getX());
                        if (point.getZ() < bottomZ.get())
                            bottomZ.set(point.getZ());
                        if (point.getZ() > upperZ.get())
                            upperZ.set(point.getZ());
                    }*/
                    
                    ObservableList<TetgenPoint> pl = FXCollections.observableArrayList();
                    
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
                            hm.getHistionMap().get(hId).getYRotate(), nodeAvg, pl);
                    
                    for (TetgenPoint point :  pl) {
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
                    }
                    
                    /*hm.getHistionMap().get(hId).getItems().forEach(c -> {
                        if (c.getShow()) {
                            c.getTransformedPointData().forEach(point -> {
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
                    });*/
                            //dataSize.set(0);
                            /*c.getItems().forEach(p -> {
                                p.getPointData().forEach(point -> {
                                    pointData.add(new TetgenPoint(point));
                                    nodeAvg.add(point.getX() + c.getXCoordinate(), point.getY() + c.getYCoordinate(), point.getZ() + c.getZCoordinate());
                                    //dataSize.set(dataSize.get() + 1);
                                    
                                    //pointList.add(new Point3D(point.getX(), point.getY(), point.getZ()));
                                    /*if (point.getX() < leftX.get())
                                        leftX.set(point.getX());
                                    if (point.getX() > rightX.get())
                                        rightX.set(point.getX());
                                    if (point.getZ() < bottomZ.get())
                                            bottomZ.set(point.getZ());
                                    if (point.getZ() > upperZ.get())
                                        upperZ.set(point.getZ());*/
                            /*    });
                            });*/
                        //}
                    //});
                    
                    DoubleProperty xSpace = new SimpleDoubleProperty(rightX.get() - leftX.get());
                    DoubleProperty ySpace = new SimpleDoubleProperty(upperY.get() - bottomY.get());
                    DoubleProperty zSpace = new SimpleDoubleProperty(upperZ.get() - bottomZ.get());
                    
                    DoubleProperty xBoundary = new SimpleDoubleProperty(500);
                    DoubleProperty yBoundary = new SimpleDoubleProperty(300);
                    DoubleProperty zBoundary = new SimpleDoubleProperty(500);
                    
                    BooleanProperty buttonPressed = new SimpleBooleanProperty(false);
                    
                    //double deltaX = rightX.get() - leftX.get();
                    //double deltaZ = upperZ.get() - bottomZ.get();
                    
                    HistionRecurrence.display("Spacing", xSpace, ySpace, zSpace,
                            xBoundary, yBoundary, zBoundary, buttonPressed);
                    
                    if (!buttonPressed.get()) {
                        updateTree();
                        return;
                    }
                    
                    double deltaX = xSpace.get();
                    double deltaY = ySpace.get();
                    double deltaZ = zSpace.get();
                    
                    double hZ = hm.getHistionMap().get(hId).getZCoordinate();
                    double hY = hm.getHistionMap().get(hId).getYCoordinate();
                    double hX = hm.getHistionMap().get(hId).getXCoordinate();
                    
                    /*if ((deltaX > 0) && (deltaY > 0) && (deltaZ > 0))
                        return;*/
                    
                    if (deltaX > 0) {
                        while (hX < xBoundary.get()) {
                            Histion newHistion = new Histion(hm.getHistionMap().get(hId));
                            newHistion.setName("Histion <X: " + String.valueOf(hX + deltaX) + " ; Z: " + String.valueOf(hZ) + ">");
                            hm.getHistionMap().get(hId).getItems().forEach(c -> {
                                Cell newCell = new Cell(c, newHistion.getId());
                                c.getItems().forEach(p -> {
                                    //newCell.addChild(new Part(p));
                                    newCell.addChild(p);
                                });
                                newHistion.addChild(newCell);
                            });
                            newHistion.setXCoordinate(hX + deltaX);
                            hm.addHistion(newHistion);
                            hX += deltaX;
                        }
                        hX = hm.getHistionMap().get(hId).getXCoordinate();
                        while (hX > -xBoundary.get()) {
                            Histion newHistion = new Histion(hm.getHistionMap().get(hId));
                            newHistion.setName("Histion <X: " + String.valueOf(hX - deltaX) + " ; Z: " + String.valueOf(hZ) + ">");
                            hm.getHistionMap().get(hId).getItems().forEach(c -> {
                                Cell newCell = new Cell(c, newHistion.getId());
                                c.getItems().forEach(p -> {
                                    //newCell.addChild(new Part(p));
                                    newCell.addChild(p);
                                });
                                newHistion.addChild(newCell);
                            });
                            newHistion.setXCoordinate(hX - deltaX);
                            hm.addHistion(newHistion);
                            hX -= deltaX;
                        }
                    }
                    hX = hm.getHistionMap().get(hId).getXCoordinate();
                    
                    if (deltaY > 0) {
                        while (hY < yBoundary.get()) {
                            Histion newHistion = new Histion(hm.getHistionMap().get(hId));
                            newHistion.setName("Histion <X: " + String.valueOf(hX) + " ; Y: "
                                    + String.valueOf(hY + deltaY) + " ; Z: "
                                    + String.valueOf(hZ) + ">");
                            hm.getHistionMap().get(hId).getItems().forEach(c -> {
                                Cell newCell = new Cell(c, newHistion.getId());
                                c.getItems().forEach(p -> {
                                    //newCell.addChild(new Part(p));
                                    newCell.addChild(p);
                                });
                                newHistion.addChild(newCell);
                            });
                            newHistion.setYCoordinate(hY + deltaY);
                            hm.addHistion(newHistion);
                            hY += deltaY;
                        }
                        hY = hm.getHistionMap().get(hId).getYCoordinate();
                        while (hY > -yBoundary.get()) {
                            Histion newHistion = new Histion(hm.getHistionMap().get(hId));
                            newHistion.setName("Histion <X: " + String.valueOf(hX) + " ; Y: "
                                    + String.valueOf(hY - deltaY) + " ; Z: "
                                    + String.valueOf(hZ) + ">");
                            hm.getHistionMap().get(hId).getItems().forEach(c -> {
                                Cell newCell = new Cell(c, newHistion.getId());
                                c.getItems().forEach(p -> {
                                    //newCell.addChild(new Part(p));
                                    newCell.addChild(p);
                                });
                                newHistion.addChild(newCell);
                            });
                            newHistion.setYCoordinate(hY - deltaY);
                            hm.addHistion(newHistion);
                            hY -= deltaY;
                        }
                    }
                    hY = hm.getHistionMap().get(hId).getYCoordinate();
                    
                    if (deltaZ > 0) {
                        while (hZ < zBoundary.get()) {
                            Histion newHistion = new Histion(hm.getHistionMap().get(hId));
                            newHistion.setName("Histion <X: " + String.valueOf(hX) + " ; Z: " + String.valueOf(hZ + deltaZ) + ">");
                            hm.getHistionMap().get(hId).getItems().forEach(c -> {
                                Cell newCell = new Cell(c, newHistion.getId());
                                c.getItems().forEach(p -> {
                                    //newCell.addChild(new Part(p));
                                    newCell.addChild(p);
                                });
                                newHistion.addChild(newCell);
                            });
                            newHistion.setZCoordinate(hZ + deltaZ);
                            hm.addHistion(newHistion);
                            hZ += deltaZ;
                        }
                        hZ = hm.getHistionMap().get(hId).getZCoordinate();
                        while (hZ > -zBoundary.get()) {
                            Histion newHistion = new Histion(hm.getHistionMap().get(hId));
                            newHistion.setName("Histion <X: " + String.valueOf(hX) + " ; Z: " + String.valueOf(hZ - deltaZ) + ">");
                            hm.getHistionMap().get(hId).getItems().forEach(c -> {
                                Cell newCell = new Cell(c, newHistion.getId());
                                c.getItems().forEach(p -> {
                                    //newCell.addChild(new Part(p));
                                    newCell.addChild(p);
                                });
                                newHistion.addChild(newCell);
                            });
                            newHistion.setZCoordinate(hZ - deltaZ);
                            hm.addHistion(newHistion);
                            hZ -= deltaZ;
                        }
                    }
                    hZ = hm.getHistionMap().get(hId).getZCoordinate();
                    
                    if ((deltaX > 0) && (deltaZ > 0)) {
                        while (hX < xBoundary.get()) {
                            hZ = hm.getHistionMap().get(hId).getZCoordinate();
                            while (hZ < zBoundary.get()) {
                                Histion newHistion = new Histion(hm.getHistionMap().get(hId));
                                newHistion.setName("Histion <X: " + String.valueOf(hX + deltaX) + " ; Z: " + String.valueOf(hZ + deltaZ) + ">");
                                hm.getHistionMap().get(hId).getItems().forEach(c -> {
                                    Cell newCell = new Cell(c, newHistion.getId());
                                    c.getItems().forEach(p -> {
                                        //newCell.addChild(new Part(p));
                                        newCell.addChild(p);
                                    });
                                    newHistion.addChild(newCell);
                                });
                                newHistion.setZCoordinate(hZ + deltaZ);
                                newHistion.setXCoordinate(hX + deltaX);
                                hm.addHistion(newHistion);
                                hZ += deltaZ;
                            }
                            hZ = hm.getHistionMap().get(hId).getZCoordinate();
                            while (hZ > -zBoundary.get()) {
                                Histion newHistion = new Histion(hm.getHistionMap().get(hId));
                                newHistion.setName("Histion <X: " + String.valueOf(hX + deltaX) + " ; Z: " + String.valueOf(hZ - deltaZ) + ">");
                                hm.getHistionMap().get(hId).getItems().forEach(c -> {
                                    Cell newCell = new Cell(c, newHistion.getId());
                                    c.getItems().forEach(p -> {
                                        //newCell.addChild(new Part(p));
                                        newCell.addChild(p);
                                    });
                                    newHistion.addChild(newCell);
                                });
                                newHistion.setZCoordinate(hZ - deltaZ);
                                newHistion.setXCoordinate(hX + deltaX);
                                hm.addHistion(newHistion);
                                hZ -= deltaZ;
                            }
                            hX += deltaX;
                        }
                        hX = hm.getHistionMap().get(hId).getXCoordinate();
                        while (hX > -xBoundary.get()) {

                            hZ = hm.getHistionMap().get(hId).getZCoordinate();
                            while (hZ < zBoundary.get()) {
                                Histion newHistion = new Histion(hm.getHistionMap().get(hId));
                                newHistion.setName("Histion <X: " + String.valueOf(hX - deltaX) + " ; Z: " + String.valueOf(hZ + deltaZ) + ">");
                                hm.getHistionMap().get(hId).getItems().forEach(c -> {
                                    Cell newCell = new Cell(c, newHistion.getId());
                                    c.getItems().forEach(p -> {
                                        //newCell.addChild(new Part(p));
                                        newCell.addChild(p);
                                    });
                                    newHistion.addChild(newCell);
                                });
                                newHistion.setZCoordinate(hZ + deltaZ);
                                newHistion.setXCoordinate(hX - deltaX);
                                hm.addHistion(newHistion);
                                hZ += deltaZ;
                            }
                            hZ = hm.getHistionMap().get(hId).getZCoordinate();
                            while (hZ > -zBoundary.get()) {
                                Histion newHistion = new Histion(hm.getHistionMap().get(hId));
                                newHistion.setName("Histion <X: " + String.valueOf(hX - deltaX) + " ; Z: " + String.valueOf(hZ - deltaZ) + ">");
                                hm.getHistionMap().get(hId).getItems().forEach(c -> {
                                    Cell newCell = new Cell(c, newHistion.getId());
                                    c.getItems().forEach(p -> {
                                        //newCell.addChild(new Part(p));
                                        newCell.addChild(p);
                                    });
                                    newHistion.addChild(newCell);
                                });
                                newHistion.setZCoordinate(hZ - deltaZ);
                                newHistion.setXCoordinate(hX - deltaX);
                                hm.addHistion(newHistion);
                                hZ -= deltaZ;
                            }
                            hX -= deltaX;
                        }
                    }
                    
                    if ((deltaY > 0) && (deltaZ > 0)) {
                        while (hY < yBoundary.get()) {
                            hZ = hm.getHistionMap().get(hId).getZCoordinate();
                            while (hZ < zBoundary.get()) {
                                Histion newHistion = new Histion(hm.getHistionMap().get(hId));
                                newHistion.setName("Histion <X: " + String.valueOf(hX) + " ; Z: " + String.valueOf(hZ + deltaZ) + ">");
                                hm.getHistionMap().get(hId).getItems().forEach(c -> {
                                    Cell newCell = new Cell(c, newHistion.getId());
                                    c.getItems().forEach(p -> {
                                        //newCell.addChild(new Part(p));
                                        newCell.addChild(p);
                                    });
                                    newHistion.addChild(newCell);
                                });
                                newHistion.setZCoordinate(hZ + deltaZ);
                                newHistion.setYCoordinate(hY + deltaY);
                                hm.addHistion(newHistion);
                                
                                if (deltaX > 0) {
                                    hX = hm.getHistionMap().get(hId).getXCoordinate();
                                    while (hX < xBoundary.get()) {
                                        Histion newHist = new Histion(hm.getHistionMap().get(hId));
                                        newHist.setName("Histion <X: " + String.valueOf(hX + deltaX) + " ; Z: " + String.valueOf(hZ + deltaZ) + ">");
                                        hm.getHistionMap().get(hId).getItems().forEach(c -> {
                                            Cell newCell = new Cell(c, newHist.getId());
                                            c.getItems().forEach(p -> {
                                                //newCell.addChild(new Part(p));
                                                newCell.addChild(p);
                                            });
                                            newHist.addChild(newCell);
                                        });
                                        newHist.setXCoordinate(hX + deltaX);
                                        newHist.setZCoordinate(hZ + deltaZ);
                                        newHist.setYCoordinate(hY + deltaY);
                                        hm.addHistion(newHist);
                                        hX += deltaX;
                                    }
                                    hX = hm.getHistionMap().get(hId).getXCoordinate();
                                    while (hX > -xBoundary.get()) {
                                        Histion newHist = new Histion(hm.getHistionMap().get(hId));
                                        newHist.setName("Histion <X: " + String.valueOf(hX - deltaX) + " ; Z: " + String.valueOf(hZ + deltaZ) + ">");
                                        hm.getHistionMap().get(hId).getItems().forEach(c -> {
                                            Cell newCell = new Cell(c, newHist.getId());
                                            c.getItems().forEach(p -> {
                                                //newCell.addChild(new Part(p));
                                                newCell.addChild(p);
                                            });
                                            newHist.addChild(newCell);
                                        });
                                        newHist.setXCoordinate(hX - deltaX);
                                        newHist.setZCoordinate(hZ + deltaZ);
                                        newHist.setYCoordinate(hY + deltaY);
                                        hm.addHistion(newHist);
                                        hX -= deltaX;
                                    }
                                }
                                
                                hZ += deltaZ;
                            }
                            hZ = hm.getHistionMap().get(hId).getZCoordinate();
                            while (hZ > -zBoundary.get()) {
                                Histion newHistion = new Histion(hm.getHistionMap().get(hId));
                                newHistion.setName("Histion <X: " + String.valueOf(hX) + " ; Z: " + String.valueOf(hZ - deltaZ) + ">");
                                hm.getHistionMap().get(hId).getItems().forEach(c -> {
                                    Cell newCell = new Cell(c, newHistion.getId());
                                    c.getItems().forEach(p -> {
                                        //newCell.addChild(new Part(p));
                                        newCell.addChild(p);
                                    });
                                    newHistion.addChild(newCell);
                                });
                                newHistion.setZCoordinate(hZ - deltaZ);
                                newHistion.setYCoordinate(hY + deltaY);
                                hm.addHistion(newHistion);
                                if (deltaX > 0) {
                                    hX = hm.getHistionMap().get(hId).getXCoordinate();
                                    while (hX < xBoundary.get()) {
                                        Histion newHist = new Histion(hm.getHistionMap().get(hId));
                                        newHist.setName("Histion <X: " + String.valueOf(hX + deltaX) + " ; Z: " + String.valueOf(hZ - deltaZ) + ">");
                                        hm.getHistionMap().get(hId).getItems().forEach(c -> {
                                            Cell newCell = new Cell(c, newHist.getId());
                                            c.getItems().forEach(p -> {
                                                //newCell.addChild(new Part(p));
                                                newCell.addChild(p);
                                            });
                                            newHist.addChild(newCell);
                                        });
                                        newHist.setXCoordinate(hX + deltaX);
                                        newHist.setZCoordinate(hZ - deltaZ);
                                        newHist.setYCoordinate(hY + deltaY);
                                        hm.addHistion(newHist);
                                        hX += deltaX;
                                    }
                                    hX = hm.getHistionMap().get(hId).getXCoordinate();
                                    while (hX > -xBoundary.get()) {
                                        Histion newHist = new Histion(hm.getHistionMap().get(hId));
                                        newHist.setName("Histion <X: " + String.valueOf(hX - deltaX) + " ; Z: " + String.valueOf(hZ - deltaZ) + ">");
                                        hm.getHistionMap().get(hId).getItems().forEach(c -> {
                                            Cell newCell = new Cell(c, newHist.getId());
                                            c.getItems().forEach(p -> {
                                                //newCell.addChild(new Part(p));
                                                newCell.addChild(p);
                                            });
                                            newHist.addChild(newCell);
                                        });
                                        newHist.setXCoordinate(hX - deltaX);
                                        newHist.setZCoordinate(hZ - deltaZ);
                                        newHist.setYCoordinate(hY + deltaY);
                                        hm.addHistion(newHist);
                                        hX -= deltaX;
                                    }
                                }
                                hZ -= deltaZ;
                            }
                            hY += deltaY;
                        }
                        hY = hm.getHistionMap().get(hId).getYCoordinate();
                        while (hY > -yBoundary.get()) {

                            hZ = hm.getHistionMap().get(hId).getZCoordinate();
                            while (hZ < zBoundary.get()) {
                                Histion newHistion = new Histion(hm.getHistionMap().get(hId));
                                newHistion.setName("Histion <X: " + String.valueOf(hX) + " ; Z: " + String.valueOf(hZ + deltaZ) + ">");
                                hm.getHistionMap().get(hId).getItems().forEach(c -> {
                                    Cell newCell = new Cell(c, newHistion.getId());
                                    c.getItems().forEach(p -> {
                                        //newCell.addChild(new Part(p));
                                        newCell.addChild(p);
                                    });
                                    newHistion.addChild(newCell);
                                });
                                newHistion.setZCoordinate(hZ + deltaZ);
                                newHistion.setYCoordinate(hY - deltaY);
                                hm.addHistion(newHistion);
                                
                                if (deltaX > 0) {
                                    hX = hm.getHistionMap().get(hId).getXCoordinate();
                                    while (hX < xBoundary.get()) {
                                        Histion newHist = new Histion(hm.getHistionMap().get(hId));
                                        newHist.setName("Histion <X: " + String.valueOf(hX + deltaX) + " ; Z: " + String.valueOf(hZ + deltaZ) + ">");
                                        hm.getHistionMap().get(hId).getItems().forEach(c -> {
                                            Cell newCell = new Cell(c, newHist.getId());
                                            c.getItems().forEach(p -> {
                                                //newCell.addChild(new Part(p));
                                                newCell.addChild(p);
                                            });
                                            newHist.addChild(newCell);
                                        });
                                        newHist.setXCoordinate(hX + deltaX);
                                        newHist.setZCoordinate(hZ + deltaZ);
                                        newHist.setYCoordinate(hY - deltaY);
                                        hm.addHistion(newHist);
                                        hX += deltaX;
                                    }
                                    hX = hm.getHistionMap().get(hId).getXCoordinate();
                                    while (hX > -xBoundary.get()) {
                                        Histion newHist = new Histion(hm.getHistionMap().get(hId));
                                        newHist.setName("Histion <X: " + String.valueOf(hX - deltaX) + " ; Z: " + String.valueOf(hZ + deltaZ) + ">");
                                        hm.getHistionMap().get(hId).getItems().forEach(c -> {
                                            Cell newCell = new Cell(c, newHist.getId());
                                            c.getItems().forEach(p -> {
                                                //newCell.addChild(new Part(p));
                                                newCell.addChild(p);
                                            });
                                            newHist.addChild(newCell);
                                        });
                                        newHist.setXCoordinate(hX - deltaX);
                                        newHist.setZCoordinate(hZ + deltaZ);
                                        newHist.setYCoordinate(hY - deltaY);
                                        hm.addHistion(newHist);
                                        hX -= deltaX;
                                    }
                                }
                                
                                hZ += deltaZ;
                            }
                            hZ = hm.getHistionMap().get(hId).getZCoordinate();
                            while (hZ > -zBoundary.get()) {
                                Histion newHistion = new Histion(hm.getHistionMap().get(hId));
                                newHistion.setName("Histion <X: " + String.valueOf(hX) + " ; Z: " + String.valueOf(hZ - deltaZ) + ">");
                                hm.getHistionMap().get(hId).getItems().forEach(c -> {
                                    Cell newCell = new Cell(c, newHistion.getId());
                                    c.getItems().forEach(p -> {
                                        //newCell.addChild(new Part(p));
                                        newCell.addChild(p);
                                    });
                                    newHistion.addChild(newCell);
                                });
                                newHistion.setZCoordinate(hZ - deltaZ);
                                newHistion.setYCoordinate(hY - deltaY);
                                hm.addHistion(newHistion);
                                
                                if (deltaX > 0) {
                                    hX = hm.getHistionMap().get(hId).getXCoordinate();
                                    while (hX < xBoundary.get()) {
                                        Histion newHist = new Histion(hm.getHistionMap().get(hId));
                                        newHist.setName("Histion <X: " + String.valueOf(hX + deltaX) + " ; Z: " + String.valueOf(hZ - deltaZ) + ">");
                                        hm.getHistionMap().get(hId).getItems().forEach(c -> {
                                            Cell newCell = new Cell(c, newHist.getId());
                                            c.getItems().forEach(p -> {
                                                //newCell.addChild(new Part(p));
                                                newCell.addChild(p);
                                            });
                                            newHist.addChild(newCell);
                                        });
                                        newHist.setXCoordinate(hX + deltaX);
                                        newHist.setZCoordinate(hZ - deltaZ);
                                        newHist.setYCoordinate(hY - deltaY);
                                        hm.addHistion(newHist);
                                        hX += deltaX;
                                    }
                                    hX = hm.getHistionMap().get(hId).getXCoordinate();
                                    while (hX > -xBoundary.get()) {
                                        Histion newHist = new Histion(hm.getHistionMap().get(hId));
                                        newHist.setName("Histion <X: " + String.valueOf(hX - deltaX) + " ; Z: " + String.valueOf(hZ - deltaZ) + ">");
                                        hm.getHistionMap().get(hId).getItems().forEach(c -> {
                                            Cell newCell = new Cell(c, newHist.getId());
                                            c.getItems().forEach(p -> {
                                                //newCell.addChild(new Part(p));
                                                newCell.addChild(p);
                                            });
                                            newHist.addChild(newCell);
                                        });
                                        newHist.setXCoordinate(hX - deltaX);
                                        newHist.setZCoordinate(hZ - deltaZ);
                                        newHist.setYCoordinate(hY - deltaY);
                                        hm.addHistion(newHist);
                                        hX -= deltaX;
                                    }
                                }
                                
                                hZ -= deltaZ;
                            }
                            hY -= deltaY;
                        }
                    }
                    
                    /*else if ((deltaX > 0) && (deltaZ > 0)) {
                        while (hX < xBoundary.get()) {
                            hZ = hm.getHistionMap().get(hId).getZCoordinate();
                            while (hZ < zBoundary.get()) {
                                Histion newHistion = new Histion(hm.getHistionMap().get(hId));
                                newHistion.setName("Histion <X: " + String.valueOf(hX + deltaX) + " ; Z: " + String.valueOf(hZ + deltaZ) + ">");
                                hm.getHistionMap().get(hId).getItems().forEach(c -> {
                                    Cell newCell = new Cell(c, newHistion.getId());
                                    c.getItems().forEach(p -> {
                                        //newCell.addChild(new Part(p));
                                        newCell.addChild(p);
                                    });
                                    newHistion.addChild(newCell);
                                });
                                newHistion.setZCoordinate(hZ + deltaZ);
                                newHistion.setXCoordinate(hX + deltaX);
                                hm.addHistion(newHistion);
                                hZ += deltaZ;
                            }
                            hZ = hm.getHistionMap().get(hId).getZCoordinate();
                            while (hZ > -zBoundary.get()) {
                                Histion newHistion = new Histion(hm.getHistionMap().get(hId));
                                newHistion.setName("Histion <X: " + String.valueOf(hX + deltaX) + " ; Z: " + String.valueOf(hZ - deltaZ) + ">");
                                hm.getHistionMap().get(hId).getItems().forEach(c -> {
                                    Cell newCell = new Cell(c, newHistion.getId());
                                    c.getItems().forEach(p -> {
                                        //newCell.addChild(new Part(p));
                                        newCell.addChild(p);
                                    });
                                    newHistion.addChild(newCell);
                                });
                                newHistion.setZCoordinate(hZ - deltaZ);
                                newHistion.setXCoordinate(hX + deltaX);
                                hm.addHistion(newHistion);
                                hZ -= deltaZ;
                            }
                            hX += deltaX;
                        }
                        hX = hm.getHistionMap().get(hId).getXCoordinate();
                        while (hX > -xBoundary.get()) {

                            hZ = hm.getHistionMap().get(hId).getZCoordinate();
                            while (hZ < zBoundary.get()) {
                                Histion newHistion = new Histion(hm.getHistionMap().get(hId));
                                newHistion.setName("Histion <X: " + String.valueOf(hX - deltaX) + " ; Z: " + String.valueOf(hZ + deltaZ) + ">");
                                hm.getHistionMap().get(hId).getItems().forEach(c -> {
                                    Cell newCell = new Cell(c, newHistion.getId());
                                    c.getItems().forEach(p -> {
                                        //newCell.addChild(new Part(p));
                                        newCell.addChild(p);
                                    });
                                    newHistion.addChild(newCell);
                                });
                                newHistion.setZCoordinate(hZ + deltaZ);
                                newHistion.setXCoordinate(hX - deltaX);
                                hm.addHistion(newHistion);
                                hZ += deltaZ;
                            }
                            hZ = hm.getHistionMap().get(hId).getZCoordinate();
                            while (hZ > -zBoundary.get()) {
                                Histion newHistion = new Histion(hm.getHistionMap().get(hId));
                                newHistion.setName("Histion <X: " + String.valueOf(hX - deltaX) + " ; Z: " + String.valueOf(hZ - deltaZ) + ">");
                                hm.getHistionMap().get(hId).getItems().forEach(c -> {
                                    Cell newCell = new Cell(c, newHistion.getId());
                                    c.getItems().forEach(p -> {
                                        //newCell.addChild(new Part(p));
                                        newCell.addChild(p);
                                    });
                                    newHistion.addChild(newCell);
                                });
                                newHistion.setZCoordinate(hZ - deltaZ);
                                newHistion.setXCoordinate(hX - deltaX);
                                hm.addHistion(newHistion);
                                hZ -= deltaZ;
                            }
                            hX -= deltaX;
                        }
                    }*/
                    //hX = hm.getHistionMap().get(hId).getXCoordinate();
                    
                    //hY = hm.getHistionMap().get(hId).getYCoordinate();
                    //hZ = hm.getHistionMap().get(hId).getZCoordinate();
                    
                    if ((deltaX > 0) && (deltaY > 0)) {
                        while (hX < xBoundary.get()) {
                            hY = hm.getHistionMap().get(hId).getYCoordinate();
                            while (hY < yBoundary.get()) {
                                Histion newHistion = new Histion(hm.getHistionMap().get(hId));
                                newHistion.setName("Histion <X: " + String.valueOf(hX + deltaX) + " ; Z: " + String.valueOf(hZ) + ">");
                                hm.getHistionMap().get(hId).getItems().forEach(c -> {
                                    Cell newCell = new Cell(c, newHistion.getId());
                                    c.getItems().forEach(p -> {
                                        //newCell.addChild(new Part(p));
                                        newCell.addChild(p);
                                    });
                                    newHistion.addChild(newCell);
                                });
                                newHistion.setYCoordinate(hY + deltaY);
                                newHistion.setXCoordinate(hX + deltaX);
                                hm.addHistion(newHistion);
                                hY += deltaY;
                            }
                            hY = hm.getHistionMap().get(hId).getYCoordinate();
                            while (hY > -yBoundary.get()) {
                                Histion newHistion = new Histion(hm.getHistionMap().get(hId));
                                newHistion.setName("Histion <X: " + String.valueOf(hX + deltaX) + " ; Z: " + String.valueOf(hZ) + ">");
                                hm.getHistionMap().get(hId).getItems().forEach(c -> {
                                    Cell newCell = new Cell(c, newHistion.getId());
                                    c.getItems().forEach(p -> {
                                        //newCell.addChild(new Part(p));
                                        newCell.addChild(p);
                                    });
                                    newHistion.addChild(newCell);
                                });
                                newHistion.setYCoordinate(hY - deltaY);
                                newHistion.setXCoordinate(hX + deltaX);
                                hm.addHistion(newHistion);
                                hY -= deltaY;
                            }
                            hX += deltaX;
                        }
                        hX = hm.getHistionMap().get(hId).getXCoordinate();
                        while (hX > -xBoundary.get()) {

                            hY = hm.getHistionMap().get(hId).getYCoordinate();
                            while (hY < yBoundary.get()) {
                                Histion newHistion = new Histion(hm.getHistionMap().get(hId));
                                newHistion.setName("Histion <X: " + String.valueOf(hX - deltaX) + " ; Z: " + String.valueOf(hZ) + ">");
                                hm.getHistionMap().get(hId).getItems().forEach(c -> {
                                    Cell newCell = new Cell(c, newHistion.getId());
                                    c.getItems().forEach(p -> {
                                        //newCell.addChild(new Part(p));
                                        newCell.addChild(p);
                                    });
                                    newHistion.addChild(newCell);
                                });
                                newHistion.setYCoordinate(hY + deltaY);
                                newHistion.setXCoordinate(hX - deltaX);
                                hm.addHistion(newHistion);
                                hY += deltaY;
                            }
                            hY = hm.getHistionMap().get(hId).getYCoordinate();
                            while (hY > -yBoundary.get()) {
                                Histion newHistion = new Histion(hm.getHistionMap().get(hId));
                                newHistion.setName("Histion <X: " + String.valueOf(hX - deltaX) + " ; Z: " + String.valueOf(hZ) + ">");
                                hm.getHistionMap().get(hId).getItems().forEach(c -> {
                                    Cell newCell = new Cell(c, newHistion.getId());
                                    c.getItems().forEach(p -> {
                                        //newCell.addChild(new Part(p));
                                        newCell.addChild(p);
                                    });
                                    newHistion.addChild(newCell);
                                });
                                newHistion.setYCoordinate(hY - deltaY);
                                newHistion.setXCoordinate(hX - deltaX);
                                hm.addHistion(newHistion);
                                hY -= deltaY;
                            }
                            hX -= deltaX;
                        }
                    }
                } else {
                    hm.getAllHistions().forEach(h -> {
                        if (h.getId() != 0) {
                            h.getItems().forEach(c -> {
                                hm.getHistionMap().get(h.getId()).deleteChild(c.getId());
                            });
                            hm.deleteHistion(h.getId());
                        }
                    });
                    //hm.deleteHistion(histionId);
                }
                updateTree();
            });
            MenuItem pasteCell = new MenuItem();
            pasteCell.setText("Paste cell");
            pasteCell.setOnAction(event -> {
                //long newHistionId = cell.getTreeItem().getParent().getValue().getId();
                //long newCellId = cell.getTreeItem().getValue().getId();
                Integer newHistionId = cell.getTreeItem().getValue().getId();
                /*Shape newShape = new Shape(sm.getShapeMap().get(cellId), newHistionId);
                newShape.setCopiedId(cellId);
                sm.addShape(newShape);*/
                Cell newCell = new Cell(hm.getHistionMap().get(histionId).getItemMap().get(cellId), newHistionId);
                String name = newCell.getName();
                name = name.substring(name.indexOf("<") + 1, name.lastIndexOf(">"));
                //System.out.println("&");
                //System.out.println(name);
                int count = 1;
                while(Names.containsCellName(name)) {
                    //System.out.println("true");
                    name = newCell.getName();
                    name = name.substring(name.indexOf("<") + 1, name.lastIndexOf(">"));
                    name += "(" + count + ")";
                    count++;
                }
                newCell.setName("Cell <" + name + ">");
                Names.addCellName(name);
                //boolean sh = newCell.getShow();
                //newCell.setCopiedId(cellId);
                hm.getHistionMap().get(histionId).getItemMap().get(cellId).getItems().forEach(p -> {
                    newCell.addChild(new Part(p));
                });
                //newCell.setShow(false);
                hm.getHistionMap().get(cell.getTreeItem().getValue().getId()).addChild(newCell);
                /*hm.getHistionMap().get(histionId).getItemMap().get(cellId).getItems().forEach(p -> {
                    //hm.getHistionMap().get(newHistionId).getItemMap().get(newCellId).addChild(new Part(p));
                    hm.getHistionMap().get(newHistionId).getItemMap().get(newCell.getId()).addChild(new Part(p));
                });
                newCell.setShow(sh);*/
                updateTree();
            });
            pasteCell.disableProperty().bind(pasteCellDisabledProperty);
            /*CheckMenuItem hideHistion = new CheckMenuItem();
            hideHistion.setText("Hide cell");
            hideHistion.setOnAction(event -> {
                if (hm.getHistionMap().get(cell.getTreeItem().getParent().
                        getValue().getId()).getItemMap().get(cell.getTreeItem().getValue().getId()).getFacetData().size() > 0) {
                    if (hideHistion.isSelected()) {
                        CameraView.addShapeIdToHide(cell.getTreeItem().getValue().getId());
                    } else {
                        CameraView.removeShapeIdToHide(cell.getTreeItem().getValue().getId());
                    }
                } else if (hideHistion.isSelected())
                    hideHistion.setSelected(false);
            });*/
            
            /*MenuItem deleteHistion = new MenuItem();
            deleteHistion.setText("Delete histion");
            deleteHistion.setOnAction(event -> {
                pastePartDisabledProperty.set(true);
                pasteCellDisabledProperty.set(true);
                pasteHistionDisabledProperty.set(true);
                ConfirmBox.display("Delete Confirmation", "Are you sure you want to delete " +
                        cell.getTreeItem().getValue().getName(), 
                        cell.getTreeItem().getValue().getId(), -1, -1);
            });*/
            //histionContextMenu.getItems().addAll(editHistion, addCell, copyHistion, pasteCell, deleteHistion, fillTissue);

            MenuItem editCell = new MenuItem();
            editCell.setText("Edit cell");
            editCell.setOnAction(event -> {
                /*HistionInformationInitialization.createScene(cell.
                        getTreeItem().getParent().getValue().getId(), 
                        cell.getTreeItem().getValue().getId(), -1);*/
                /*CellInformationInitialization.createScene(sm.getShapeMap().
                        get(cell.getTreeItem().getValue().getId()));*/
                CellInformationInitialization.createScene(hm.getHistionMap().
                        get(cell.getTreeItem().getParent().getValue().getId()).getItemMap().
                        get(cell.getTreeItem().getValue().getId()));
                updateTree();
            });
            MenuItem addPart = new MenuItem();
            addPart.setText("Add part");
            addPart.setOnAction(event -> {
                /*CellInformationInitialization.createScene(new Shape(cell.getTreeItem().
                        getParent().getValue().getId(), cell.getTreeItem().getValue().getId()));*/
                //PartInformationInitialization.show(null);
                PartInformationInitialization.show(cell.getTreeItem().getParent().getValue().getId(),
                        cell.getTreeItem().getValue().getId(), -1);
                updateTree();
            });
            MenuItem copyCell = new MenuItem();
            copyCell.setText("Copy cell");
            copyCell.setOnAction(event -> {
                pastePartDisabledProperty.set(true);
                pasteCellDisabledProperty.set(false);
                histionId = cell.getTreeItem().getParent().getValue().getId();
                cellId = cell.getTreeItem().getValue().getId();
                partId = -1;
            });
            MenuItem pastePart = new MenuItem();
            pastePart.setText("Paste part");
            pastePart.setOnAction(event -> {
                Integer newHistionId = cell.getTreeItem().getParent().getValue().getId();
                Integer newCellId = cell.getTreeItem().getValue().getId();
                Part newPart = new Part(hm.getHistionMap().get(histionId).
                                getItemMap().get(cellId).getItemMap().get(partId));
                hm.getHistionMap().get(newHistionId).getItemMap().get(newCellId).
                        addChild(newPart);
                updateTree();
            });
            pastePart.disableProperty().bind(pastePartDisabledProperty);
            CheckMenuItem hideCell = new CheckMenuItem();
            hideCell.setText("Hide cell");
            hideCell.setOnAction(event -> {
                //if (hm.getHistionMap().get(cell.getTreeItem().getParent().
                //        getValue().getId()).getItemMap().get(cell.getTreeItem().getValue().getId()).getFacetData().size() > 0) {
                if (hm.getHistionMap().get(cell.getTreeItem().getParent().
                        getValue().getId()).getItemMap().get(cell.getTreeItem().getValue().getId()).getShow()) {
                    if (hideCell.isSelected()) {
                        //HideCells.addCellIdToHide(cell.getTreeItem().getValue().getId());
                        String name = cell.getTreeItem().getValue().getName();
                        name = name.substring(name.indexOf("<") + 1, name.lastIndexOf(">"));
                        HideCells.addCellNameToHide(name);
                    } else {
                        String name = cell.getTreeItem().getValue().getName();
                        name = name.substring(name.indexOf("<") + 1, name.lastIndexOf(">"));
                        HideCells.removeCellNameToHide(name);
                        //HideCells.removeCellIdToHide(cell.getTreeItem().getValue().getId());
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
                        + cell.getTreeItem().getValue().getName(), 
                        cell.getTreeItem().getParent().getValue().getId(), 
                        cell.getTreeItem().getValue().getId(), -1);
                updateTree();
            });
            
            MenuItem editPart = new MenuItem();
            editPart.setText("Edit part");
            editPart.setOnAction(event -> {
                /*CellInformationInitialization.createScene(sm.getShapeMap().
                        get(cell.getTreeItem().getValue().getId()));*/
                
                /*PartInformationInitialization.show(hm.getHistionMap().get(cell.
                        getTreeItem().getParent().getParent().getValue().getId()).
                        getItemMap().get(cell.getTreeItem().getParent().getValue().getId()).
                        getItemMap().get(cell.getTreeItem().getValue().getId()).getPointData());*/
                PartInformationInitialization.show(cell.getTreeItem().getParent().getParent().getValue().getId(),
                        cell.getTreeItem().getParent().getValue().getId(), cell.getTreeItem().getValue().getId());
                updateTree();
                
            });
            MenuItem copyPart = new MenuItem();
            copyPart.setText("Copy part");
            copyPart.setOnAction(event -> {
                pastePartDisabledProperty.set(false);
                pasteCellDisabledProperty.set(true);
                histionId = cell.getTreeItem().getParent().getParent().getValue().getId();
                cellId = cell.getTreeItem().getParent().getValue().getId();
                partId = cell.getTreeItem().getValue().getId();
            });
            MenuItem deletePart = new MenuItem();
            deletePart.setText("Delete part");
            deletePart.setOnAction(event -> {
                pastePartDisabledProperty.set(true);
                pasteCellDisabledProperty.set(true);
                ConfirmBox.display("Delete Confirmation", "Are you sure you want to delete " +
                        cell.getTreeItem().getValue().getName(), 
                        cell.getTreeItem().getParent().getParent().getValue().getId(), 
                        cell.getTreeItem().getParent().getValue().getId(), cell.getTreeItem().getValue().getId());
                updateTree();
            });
            cell.emptyProperty().addListener((obs, wasEmpty, isNowEmpty) -> {
                if (isNowEmpty) {
                    //System.out.println("Empty");
                    //if (cell.getTreeItem().getValue() == null)
                    //System.out.println(cell.getTreeItem().getValue().getId());
                    cell.setContextMenu(null);
                    //else System.out.println(cell.getTreeItem().getValue().getId());
                } else {
                    /*if (cell.getTreeItem().getParent() == null)
                        cell.setContextMenu(tissueContextMenu);*/
                    if (cell.getTreeItem().getValue() instanceof Histion) {
                        if (hm.getAllHistions().size() > 1) {
                            fillModel.setSelected(true);
                        }
                        /*if (hm.getAllHistions().size() > 1) {
                            editHistion.setDisable(true);
                            addCell.setDisable(true);
                            pasteCell.setDisable(true);
                        }*/
                        cell.setContextMenu(new ContextMenu(editHistion, addCell, pasteCell, fillModel));
                        /*if (hm.getAllHistions().size() > 1) {
                            editHistion.setDisable(true);
                            addCell.setDisable(true);
                            pasteCellDisabledProperty.set(true);
                        }*/
                    }
                    if (cell.getTreeItem().getValue() instanceof Cell) {
                        /*if (HideCells.getCellIdToHideList().contains(cell.getTreeItem().getValue().getId())) {
                            hideCell.setSelected(true);
                        }*/
                        String name = cell.getTreeItem().getValue().getName();
                        name = name.substring(name.indexOf("<") + 1, name.lastIndexOf(">"));
                        if (HideCells.getCellNameToHideList().contains(name)) {
                            hideCell.setSelected(true);
                        }
                        cell.setContextMenu(new ContextMenu(editCell, addPart, copyCell, pastePart, hideCell, deleteCell));
                        /*if (hm.getAllHistions().size() > 1) {
                            editCell.setDisable(true);
                            addPart.setDisable(true);
                            deleteCell.setDisable(true);
                            copyCell.setDisable(true);
                            pastePartDisabledProperty.set(true);
                        }*/
                    }
                    if (cell.getTreeItem().getValue() instanceof Part) {
                        cell.setContextMenu(new ContextMenu(editPart, copyPart, deletePart));
                        /*if (hm.getAllHistions().size() > 1) {
                            editPart.setDisable(true);
                            copyPart.setDisable(true);
                            deletePart.setDisable(true);
                        }*/
                        //cell.setContextMenu(shapeContextMenu);
                    }
                }
            });

            return cell ;
        });
    }
    
    int dataSize;
    Point3D nodeAvg;
    
    private void applyTransformations(double xRot, double yRot, 
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
            
            /*pd.setX(pd.getX() + nodeAvg.getX());
            pd.setY(pd.getY() + nodeAvg.getY());
            pd.setZ(pd.getZ() + nodeAvg.getZ());*/
            
            //pd.setX(pd.getX() + xTran + nodeAvg.getX());
            //pd.setY(pd.getY() + yTran + nodeAvg.getY());
            //pd.setZ(pd.getZ() + zTran + nodeAvg.getZ());
            
            pointData.set(i, pd);
        }
    }
    
    public void setTreeViewSize(int width, int height) {
        shapeTreeView.setPrefSize(width, height);
    }
    
}
