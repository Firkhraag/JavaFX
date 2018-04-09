package org.spbu.histology.space.editor;

import org.spbu.histology.model.ShapeManager;
import java.net.URL;
import java.util.ResourceBundle;
import static java.util.stream.Collectors.toList;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
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
import javafx.scene.control.CheckMenuItem;
import org.openide.LifecycleManager;
import org.openide.util.Lookup;
import org.spbu.histology.model.CameraView;
import org.spbu.histology.model.Cell;
import org.spbu.histology.model.Histion;
import org.spbu.histology.model.HistionManager;
import org.spbu.histology.model.HistologyObject;
import org.spbu.histology.model.Part;
import org.spbu.histology.shape.information.PartInformationInitialization;
import org.spbu.histology.shape.information.HistionInformationInitialization;

public class HomeController implements Initializable {
    
    //private ShapeManager sm = null;
    
    private HistionManager hm = null;
    
    @FXML
    private TreeView<HistologyObject<?>> shapeTreeView;
    
    private TreeItem<String> rootNode;
    
    private final MapChangeListener<Integer, Histion> histionListener =
        (change) -> {
            updateTree();
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
    private BooleanProperty pasteHistionDisabledProperty = new SimpleBooleanProperty(true);
    private BooleanProperty pasteCellDisabledProperty = new SimpleBooleanProperty(true);
    private BooleanProperty pastePartDisabledProperty = new SimpleBooleanProperty(true);
    
    private void updateTree() {
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
        shapeTreeView.setShowRoot(true);
    }
    
    private ObservableList<Histion> getObservableList() {
        
        ObservableList<Histion> histionList = FXCollections.observableArrayList();
        
        hm.getAllHistions().forEach(h -> {
            histionList.add(h);
        });
        
        return histionList;
    }
    
    private TreeItem<HistologyObject<?>> createItem(HistologyObject<?> object) {

        // create tree item with children from game object's list:

        TreeItem<HistologyObject<?>> item = new TreeItem<>(object);
        item.setExpanded(true);
        item.getChildren().addAll(object.getItems().stream().map(this::createItem).collect(toList()));

        // update tree item's children list if game object's list changes:

        object.getItems().addListener((ListChangeListener.Change<? extends HistologyObject<?>> c) -> {
            while (c.next()) {
                if (c.wasAdded()) {
                    item.getChildren().addAll(c.getAddedSubList().stream().map(this::createItem).collect(toList()));
                }
                if (c.wasRemoved()) {
                    item.getChildren().removeIf(treeItem -> c.getRemoved().contains(treeItem.getValue()));
                }
            }
        });

        return item ;
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
        
        hm.addListener(histionListener);
        updateTree();
        
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
            
            ContextMenu shapeContextMenu = new ContextMenu();
            ContextMenu cellContextMenu = new ContextMenu();
            ContextMenu tissueContextMenu = new ContextMenu();
            ContextMenu histionContextMenu = new ContextMenu();
            
            MenuItem addHistion = new MenuItem();
            addHistion.setText("Add histion");
            addHistion.setOnAction(event -> {
                AddBox.display("Add Histion", "Histion name", -1);
            });
            MenuItem pasteHistion = new MenuItem();
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
                updateTree();
            });
            pasteHistion.disableProperty().bind(pasteHistionDisabledProperty);
            tissueContextMenu.getItems().addAll(addHistion, pasteHistion);
            
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
            MenuItem copyHistion = new MenuItem();
            copyHistion.setText("Copy histion");
            copyHistion.setOnAction(event -> {
                pastePartDisabledProperty.set(true);
                pasteCellDisabledProperty.set(true);
                pasteHistionDisabledProperty.set(false);
                histionId = cell.getTreeItem().getValue().getId();
                cellId = -1;
                partId = -1;
            });
            /*MenuItem fillHorizontal = new MenuItem();
            fillHorizontal.setText("Fill horizontal");
            fillHorizontal.setOnAction(event -> {
                histionId = cell.getTreeItem().getValue().getId();
                cellId = -1;
                partId = -1;
                Histion newHistion = new Histion(hm.getHistionMap().get(histionId));
                hm.addHistion(newHistion);
                hm.getHistionMap().get(histionId).getItems().forEach(c -> {
                    Cell newCell = new Cell(c);
                    hm.getHistionMap().get(newHistion.getId()).addChild(newCell);
                    c.getItems().forEach(p -> {
                        //for (int i = 0;sm.getShapeMap().get(p.getId()).getPointData();
                        /*hm.getHistionMap().get(newHistion.getId()).getItemMap().
                                get(newCell.getId()).addChild(new Part(p));
                        Shape newShape = new Shape(sm.getShapeMap().get(p.getId()), 
                                newHistion.getId(), newCell.getId());
                        newShape.setCopiedId(p.getId());
                        sm.addShape(newShape);*/
                    /*});
                });
                updateTree();
            });*/
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
            MenuItem deleteHistion = new MenuItem();
            deleteHistion.setText("Delete histion");
            deleteHistion.setOnAction(event -> {
                pastePartDisabledProperty.set(true);
                pasteCellDisabledProperty.set(true);
                pasteHistionDisabledProperty.set(true);
                ConfirmBox.display("Delete Confirmation", "Are you sure you want to delete " +
                        cell.getTreeItem().getValue().getName(), 
                        cell.getTreeItem().getValue().getId(), -1, -1);
            });
            histionContextMenu.getItems().addAll(editHistion, addCell, copyHistion, pasteCell, deleteHistion);

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
                pasteHistionDisabledProperty.set(true);
                histionId = cell.getTreeItem().getParent().getValue().getId();
                cellId = cell.getTreeItem().getValue().getId();
                partId = -1;
            });
            MenuItem pastePart = new MenuItem();
            pastePart.setText("Paste part");
            pastePart.setOnAction(event -> {
                Integer newHistionId = cell.getTreeItem().getParent().getValue().getId();
                Integer newCellId = cell.getTreeItem().getValue().getId();
                hm.getHistionMap().get(newHistionId).getItemMap().get(newCellId).
                        addChild(new Part(hm.getHistionMap().get(histionId).
                                getItemMap().get(cellId).getItemMap().get(partId)));
                updateTree();
            });
            pastePart.disableProperty().bind(pastePartDisabledProperty);
            CheckMenuItem hideCell = new CheckMenuItem();
            hideCell.setText("Hide cell");
            hideCell.setOnAction(event -> {
                if (hm.getHistionMap().get(cell.getTreeItem().getParent().
                        getValue().getId()).getItemMap().get(cell.getTreeItem().getValue().getId()).getFacetData().size() > 0) {
                    if (hideCell.isSelected()) {
                        CameraView.addShapeIdToHide(cell.getTreeItem().getValue().getId());
                    } else {
                        CameraView.removeShapeIdToHide(cell.getTreeItem().getValue().getId());
                    }
                } else if (hideCell.isSelected())
                    hideCell.setSelected(false);
            });
            MenuItem deleteCell = new MenuItem();
            deleteCell.setText("Delete cell");
            deleteCell.setOnAction(event -> {
                pastePartDisabledProperty.set(true);
                pasteCellDisabledProperty.set(true);
                pasteHistionDisabledProperty.set(true);
                ConfirmBox.display("Delete Confirmation", "Are you sure you want to delete " 
                        + cell.getTreeItem().getValue().getName(), 
                        cell.getTreeItem().getParent().getValue().getId(), 
                        cell.getTreeItem().getValue().getId(), -1);
                updateTree();
            });
            cellContextMenu.getItems().addAll(editCell, addPart, copyCell, pastePart, hideCell, deleteCell);
            
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
                pasteHistionDisabledProperty.set(true);
                histionId = cell.getTreeItem().getParent().getParent().getValue().getId();
                cellId = cell.getTreeItem().getParent().getValue().getId();
                partId = cell.getTreeItem().getValue().getId();
            });
            MenuItem deletePart = new MenuItem();
            deletePart.setText("Delete part");
            deletePart.setOnAction(event -> {
                pastePartDisabledProperty.set(true);
                pasteCellDisabledProperty.set(true);
                pasteHistionDisabledProperty.set(true);
                ConfirmBox.display("Delete Confirmation", "Are you sure you want to delete " +
                        cell.getTreeItem().getValue().getName(), 
                        cell.getTreeItem().getParent().getParent().getValue().getId(), 
                        cell.getTreeItem().getParent().getValue().getId(), cell.getTreeItem().getValue().getId());
                updateTree();
            });
            shapeContextMenu.getItems().addAll(editPart, copyPart, deletePart);
            cell.emptyProperty().addListener((obs, wasEmpty, isNowEmpty) -> {
                if (isNowEmpty) {
                    cell.setContextMenu(null);
                } else {
                    if (cell.getTreeItem().getParent() == null)
                        cell.setContextMenu(tissueContextMenu);
                    if (cell.getTreeItem().getValue() instanceof Histion)
                        cell.setContextMenu(histionContextMenu);
                    if (cell.getTreeItem().getValue() instanceof Cell)
                        cell.setContextMenu(cellContextMenu);
                    if (cell.getTreeItem().getValue() instanceof Part)
                        cell.setContextMenu(shapeContextMenu);
                }
            });

            return cell ;
        });
    }
    
    public void setTreeViewSize(int width, int height) {
        shapeTreeView.setPrefSize(width, height);
    }
    
}
