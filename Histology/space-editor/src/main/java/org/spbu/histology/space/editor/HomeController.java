package org.spbu.histology.space.editor;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
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
import org.spbu.histology.model.Names;
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
    private BooleanProperty pasteCellDisabledProperty = new SimpleBooleanProperty(true);
    private BooleanProperty pastePartDisabledProperty = new SimpleBooleanProperty(true);
    private BooleanProperty disableEverything = new SimpleBooleanProperty(false);
    
    int dataSize;
    Point3D nodeAvg;
    
    public abstract class AbstractTreeItem extends TreeItem<HistologyObject<?>> {
        public abstract ContextMenu getMenu();
    }
    
    
    public class HistionTreeItem extends AbstractTreeItem {

        public HistionTreeItem(HistologyObject<?> object) {
            this.setValue(object);
            this.setExpanded(true);
        }

        @Override
        public ContextMenu getMenu() {
            MenuItem editHistion = new MenuItem();
            editHistion.setText("Edit histion");
            editHistion.setOnAction(event -> {
                HistionInformationInitialization.createScene(this.getValue().getId());
            });
            MenuItem addCell = new MenuItem();
            addCell.setText("Add cell");
            addCell.setOnAction(event -> {
                AddBox.display("Add Cell", "Cell name", this.getValue().getId());
            });
            CheckMenuItem fillModel = new CheckMenuItem();
            fillModel.setText("Fill model");
            fillModel.setOnAction(event -> {
                if (fillModel.isSelected()) {
                    int hId = this.getValue().getId();
                    final DoubleProperty leftX = new SimpleDoubleProperty(10000);
                    final DoubleProperty rightX = new SimpleDoubleProperty(-10000);
                    final DoubleProperty upperZ = new SimpleDoubleProperty(-10000);
                    final DoubleProperty bottomZ = new SimpleDoubleProperty(10000);
                    final DoubleProperty upperY = new SimpleDoubleProperty(-10000);
                    final DoubleProperty bottomY = new SimpleDoubleProperty(10000);
                    
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
                    
                    DoubleProperty xSpace = new SimpleDoubleProperty(rightX.get() - leftX.get());
                    DoubleProperty ySpace = new SimpleDoubleProperty(upperY.get() - bottomY.get());
                    DoubleProperty zSpace = new SimpleDoubleProperty(upperZ.get() - bottomZ.get());
                    
                    DoubleProperty xBoundary = new SimpleDoubleProperty(500);
                    DoubleProperty yBoundary = new SimpleDoubleProperty(300);
                    DoubleProperty zBoundary = new SimpleDoubleProperty(500);
                    
                    BooleanProperty buttonPressed = new SimpleBooleanProperty(false);
                    
                    HistionRecurrence.display("Spacing", xSpace, ySpace, zSpace,
                            xBoundary, yBoundary, zBoundary, buttonPressed);
                    
                    if (!buttonPressed.get()) {
                        fillModel.setSelected(false);
                        return;
                    }
                    
                    double deltaX = xSpace.get();
                    double deltaY = ySpace.get();
                    double deltaZ = zSpace.get();
                    
                    double hZ = hm.getHistionMap().get(hId).getZCoordinate();
                    double hY = hm.getHistionMap().get(hId).getYCoordinate();
                    double hX = hm.getHistionMap().get(hId).getXCoordinate();
                    
                    if (deltaX > 0) {
                        while (hX < xBoundary.get()) {
                            Histion newHistion = new Histion(hm.getHistionMap().get(hId));
                            newHistion.setName("Histion <X: " + String.valueOf(hX + deltaX) + " ; Z: " + String.valueOf(hZ) + ">");
                            hm.getHistionMap().get(hId).getItems().forEach(c -> {
                                Cell newCell = new Cell(c, newHistion.getId());
                                c.getItems().forEach(p -> {
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
                    
                    if ((deltaX > 0) && (deltaY > 0)) {
                        while (hX < xBoundary.get()) {
                            hY = hm.getHistionMap().get(hId).getYCoordinate();
                            while (hY < yBoundary.get()) {
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
            
            editHistion.disableProperty().bind(disableEverything);
            addCell.disableProperty().bind(disableEverything);
            return new ContextMenu(editHistion, addCell, pasteCell, fillModel);
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
            MenuItem editCell = new MenuItem();
            editCell.setText("Edit cell");
            editCell.setOnAction(event -> {
                CellInformationInitialization.createScene(hm.getHistionMap().
                        get(c.getHistionId()).getItemMap().
                        get(c.getId()));
            });
            MenuItem addPart = new MenuItem();
            addPart.setText("Add part");
            addPart.setOnAction(event -> {
                //PartInformationInitialization.show(c.getId(), -1);
                System.out.println(c.getId());
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
            
            editCell.disableProperty().bind(disableEverything);
            addPart.disableProperty().bind(disableEverything);
            deleteCell.disableProperty().bind(disableEverything);
            copyCell.disableProperty().bind(disableEverything);
            
            return new ContextMenu(editCell, addPart, copyCell, pastePart, hideCell, deleteCell);
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
            
            return new ContextMenu(editPart, copyPart, deletePart);
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
    
    HistionTreeItem histion;
    private final ObservableMap<Integer, CellTreeItem> cellTreeItemMap = 
            FXCollections.observableMap(new ConcurrentHashMap());
    private final ObservableMap<Integer, PartTreeItem> partTreeItemMap = 
            FXCollections.observableMap(new ConcurrentHashMap());
    
    private final MapChangeListener<Integer, Part> partListener
            = (change) -> {
                if (change.wasRemoved() && change.wasAdded()) {
                    Part p = (Part) change.getValueAdded();
                    partTreeItemMap.get(p.getId()).getValue().setName(p.getName());
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
                    cellTreeItemMap.get(c.getId()).getValue().setName(c.getName());
                    removedShape.getItemMap().removeListener(partListener);
                    c.getItemMap().addListener(partListener);
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
                        //histion.getChildren().get(addedCell.getId()).getChildren().add(pti);
                        partTreeItemMap.put(p.getId(), pti);
                    });
                } else if (change.wasRemoved()) {
                    Cell removedShape = (Cell) change.getValueRemoved();
                    histion.getChildren().remove(cellTreeItemMap.get(removedShape.getId()));
                    cellTreeItemMap.remove(removedShape.getId());
                    removedShape.getItemMap().removeListener(partListener);
                }
            };
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        hm = Lookup.getDefault().lookup(HistionManager.class);
        if (hm == null) {
            LifecycleManager.getDefault().exit();
        }
        
        if (hm.getHistionMap().isEmpty())
            hm.addHistion(new Histion("Main histion",0,0,0,0,0));
        
        if (hm.getAllHistions().size() > 1) {
            disableEverything.set(true);
            pastePartDisabledProperty.set(true);
            pasteCellDisabledProperty.set(true);
        }
        
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
            
            pointData.set(i, pd);
        }
    }
    
    public void setTreeViewSize(int width, int height) {
        shapeTreeView.setPrefSize(width, height);
    }
    
}
