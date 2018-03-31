package org.spbu.histology.space.editor;

import org.spbu.histology.model.Shape;
import org.spbu.histology.model.ShapeManager;
import java.net.URL;
import java.util.ResourceBundle;
import static java.util.stream.Collectors.toList;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import org.spbu.histology.shape.information.ShapeInformationInitialization;
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
import org.spbu.histology.shape.information.ShapeStructureInformationInitialization;

public class HomeController implements Initializable {
    
    private ShapeManager sm = null;
    
    private HistionManager hm = null;
    
    @FXML
    private TreeView<HistologyObject<?>> shapeTreeView;
    
    private TreeItem<String> rootNode;
    
    private final MapChangeListener<Long, Histion> histionListener =
        (change) -> {
            updateTree();
        };
    
    private long histionId;
    private long cellId;
    private long partId;
    private BooleanProperty pasteHistionDisabledProperty = new SimpleBooleanProperty(true);
    private BooleanProperty pasteCellDisabledProperty = new SimpleBooleanProperty(true);
    private BooleanProperty pastePartDisabledProperty = new SimpleBooleanProperty(true);
    
    private void updateTree() {
        shapeTreeView.setRoot(null);
            HistologyObject<?> root = new HistologyObject<Histion>((long)-1, "Tissue", 0, 0, 0, 0, 0) {

                @Override
                public ObservableList<Histion> getItems() {
                    return getObservableList();
                }

                @Override
                public void addChild(Histion h) {
                    getItemMap().put(h.getId(), new Histion(h.getId(), h));
                }
                
                @Override
                public void deleteChild(long id) {
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
        sm = Lookup.getDefault().lookup(ShapeManager.class);
        if (sm == null) {
            LifecycleManager.getDefault().exit();
        }
        
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
                hm.addHistion(newHistion);
                hm.getHistionMap().get(histionId).getItems().forEach(c -> {
                    Cell newCell = new Cell(c);
                    hm.getHistionMap().get(newHistion.getId()).addChild(newCell);
                    c.getItems().forEach(p -> {
                        hm.getHistionMap().get(newHistion.getId()).getItemMap().
                                get(newCell.getId()).addChild(new Part(p));
                        Shape newShape = new Shape(sm.getShapeMap().get(p.getId()), 
                                newHistion.getId(), newCell.getId());
                        newShape.setCopiedId(p.getId());
                        sm.addShape(newShape);
                    });
                });
                updateTree();
            });
            pasteHistion.disableProperty().bind(pasteHistionDisabledProperty);
            tissueContextMenu.getItems().addAll(addHistion, pasteHistion);
            
            MenuItem editHistion = new MenuItem();
            editHistion.setText("Edit histion");
            editHistion.setOnAction(event -> {
                ShapeStructureInformationInitialization.createScene(cell.getTreeItem().getValue().getId(), -1, -1);
            });
            MenuItem addCell = new MenuItem();
            addCell.setText("Add cell");
            addCell.setOnAction(event -> {
                AddBox.display("Add Cell", "Cell name", cell.getTreeItem().getValue().getId());
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
            MenuItem pasteCell = new MenuItem();
            pasteCell.setText("Paste cell");
            pasteCell.setOnAction(event -> {
                Cell newCell = new Cell(hm.getHistionMap().get(histionId).getItemMap().get(cellId));
                hm.getHistionMap().get(cell.getTreeItem().getValue().getId()).addChild(newCell);
                hm.getHistionMap().get(histionId).getItemMap().get(cellId).getItems().forEach(p -> {
                    long newHistionId = cell.getTreeItem().getValue().getId();
                    long newCellId = newCell.getId();
                    hm.getHistionMap().get(newHistionId).getItemMap().get(newCellId).addChild(new Part(p));
                    Shape newShape = new Shape(sm.getShapeMap().get(p.getId()), newHistionId, newCellId);
                    newShape.setCopiedId(p.getId());
                    sm.addShape(newShape);
                });
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
                ShapeStructureInformationInitialization.createScene(cell.
                        getTreeItem().getParent().getValue().getId(), 
                        cell.getTreeItem().getValue().getId(), -1);
            });
            MenuItem addPart = new MenuItem();
            addPart.setText("Add part");
            addPart.setOnAction(event -> {
                ShapeInformationInitialization.createScene(new Shape(cell.getTreeItem().
                        getParent().getValue().getId(), cell.getTreeItem().getValue().getId()));
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
                System.out.println("Pasted");
                long newHistionId = cell.getTreeItem().getParent().getValue().getId();
                long newCellId = cell.getTreeItem().getValue().getId();
                Shape newShape = new Shape(sm.getShapeMap().get(partId), newHistionId, newCellId);
                newShape.setCopiedId(partId);
                hm.getHistionMap().get(newHistionId).getItemMap().get(newCellId).
                        addChild(new Part(hm.getHistionMap().get(histionId).
                                getItemMap().get(cellId).getItemMap().get(partId)));
                sm.addShape(newShape);
                updateTree();
            });
            pastePart.disableProperty().bind(pastePartDisabledProperty);
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
            cellContextMenu.getItems().addAll(editCell, addPart, copyCell, pastePart, deleteCell);
            
            MenuItem editPart = new MenuItem();
            editPart.setText("Edit part");
            editPart.setOnAction(event -> {
                ShapeInformationInitialization.createScene(sm.getShapeMap().
                        get(cell.getTreeItem().getValue().getId()));
                
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
            CheckMenuItem hidePart = new CheckMenuItem();
            hidePart.setText("Hide part");
            hidePart.setOnAction(event -> {
                if (hidePart.isSelected()) {
                    CameraView.addShapeIdToHide(cell.getTreeItem().getValue().getId());
                } else {
                    CameraView.removeShapeIdToHide(cell.getTreeItem().getValue().getId());
                }
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
            shapeContextMenu.getItems().addAll(editPart, copyPart, hidePart, deletePart);
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
