package org.spbu.histology.shape.information;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import org.openide.LifecycleManager;
import org.openide.util.Lookup;
import org.spbu.histology.model.AlertBox;
import org.spbu.histology.model.Cell;
import org.spbu.histology.model.HistionManager;
import org.spbu.histology.model.Node;
import org.spbu.histology.model.Part;
import org.spbu.histology.model.TetgenPoint;

public class PointTabController implements Initializable {
    
    @FXML
    private VBox pointVBox;
    
    @FXML
    private HBox headerHBox;
    
    @FXML
    private TableView<TetgenPoint> table;
    
    @FXML
    private TableColumn < TetgenPoint, Double > xColumn;
    
    @FXML
    private TableColumn < TetgenPoint, Double > yColumn;
    
    @FXML
    private TableColumn < TetgenPoint, Double > zColumn;
    
    @FXML
    private Button doneButton;
    
    @FXML
    private TextField nameField;
    
    private ObservableList<TetgenPoint> data;
    
    private HistionManager hm = null;
    
    BooleanProperty change = new SimpleBooleanProperty(false);
    
    Group root;
    ObservableList<Rectangle> rectangleList = FXCollections.observableArrayList();
    double width, height;
    int initialSize;
    IntegerProperty count;
    Integer cellId, partId;
    
    Node avgNode = new Node(0, 0, 0);
    
    public void setIds(Integer cellId, Integer partId) {
        this.cellId = cellId;
        this.partId = partId;
        if (partId != -1) {
            String name = hm.getHistionMap().get(0).getItemMap().get(cellId).getItemMap().get(partId).getName();
            nameField.setText(name.substring(name.indexOf("<") + 1, name.lastIndexOf(">")));
        }
    }
    
    public void setCount(IntegerProperty count) {
        this.count = count;
    }
    
    public void setInitialSize(Integer initialSize) {
        this.initialSize = initialSize;
    }
    
    public void setRoot(Group root) {
        this.root = root;
    }
    
    public void setPaneSize(double width, double height) {
        this.width = width;
        this.height = height;
    }
    
    public void setTableHeight(double height) {
        table.setPrefHeight(height);
    }
    
    public void setRectangleList(ObservableList<Rectangle> rectangleList) {
        this.rectangleList = rectangleList;
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        hm = Lookup.getDefault().lookup(HistionManager.class);
        if (hm == null) {
            LifecycleManager.getDefault().exit();
        }
        
        pointVBox.setSpacing(10);
        headerHBox.setSpacing(10);
        headerHBox.setPadding(new Insets(10, 10, 10, 10));
        setupXColumn();
        setupYColumn();
        setupZColumn();
        setTableEditable();
        data = FXCollections.observableArrayList();
        table.setItems(data);
        table.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ESCAPE) {
                table.getSelectionModel().clearSelection();
            }
        });
        table.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (oldSelection != null) {
                rectangleList.get(oldSelection.getId() - 1).setStroke(Color.BLACK);
            }
            
            if (newSelection != null) {
                rectangleList.get(newSelection.getId() - 1).setStroke(Color.RED);
            }
        });
        MenuItem deletePoint = new MenuItem("Delete point");
        deletePoint.setOnAction((ActionEvent event) -> {
            TetgenPoint item = table.getSelectionModel().getSelectedItem();
            data.remove(item.getId() - 1);
            root.getChildren().remove(rectangleList.get(item.getId() - 1));
            rectangleList.remove(item.getId() - 1);
            count.set(count.get() - 1);
            for (int i = 0; i < data.size(); i++)
                data.get(i).setId(i + 1);
            table.getSelectionModel().clearSelection();
        });
        
        MenuItem addToX = new MenuItem("Add to X Column");
        addToX.setOnAction((ActionEvent event) -> {
            DoubleProperty value = new SimpleDoubleProperty();
            ChangePoints.display(value);
            for (TetgenPoint p : data) {
                p.setX(p.getX() + value.get());
            }
            for (Rectangle r : rectangleList) {
                r.setX(r.getX() + value.get());
            }
            table.refresh();
        });
        
        MenuItem addToZ = new MenuItem("Add to Z Column");
        addToZ.setOnAction((ActionEvent event) -> {
            DoubleProperty value = new SimpleDoubleProperty();
            ChangePoints.display(value);
            for (TetgenPoint p : data) {
                p.setZ(p.getZ() + value.get());
            }
            for (Rectangle r : rectangleList) {
                r.setY(r.getY() - value.get());
            }
            table.refresh();
        });
        
        MenuItem scale = new MenuItem("Scale");
        scale.setOnAction((ActionEvent event) -> {
            DoubleProperty value = new SimpleDoubleProperty();
            ChangePoints.display(value);
            for (TetgenPoint p : data) {
                p.setX(p.getX() * value.get());
                p.setZ(p.getZ() * value.get());
            }
            for (int i = 0; i < data.size(); i++) {
                rectangleList.get(i).setX((data.get(i).getX() + width / 2 - 2));
                rectangleList.get(i).setY(((-1) * (data.get(i).getZ() - height / 2) - 2));
            }
            /*for (Rectangle r : rectangleList) {
                r.setX((r.getX() + width / 2 - 2) * value.get());
                r.setY(((-1) * (r.getY() - height / 2) - 2) * value.get());
            }*/
            table.refresh();
        });
        
        MenuItem rotate = new MenuItem("Rotate");
        rotate.setOnAction((ActionEvent event) -> {
            DoubleProperty value = new SimpleDoubleProperty();
            ChangePoints.display(value);
            double ang = Math.toRadians(value.get());
            for (TetgenPoint p : data) {
                double x = p.getX() - avgNode.x;
                double z = p.getZ() - avgNode.z;
                double temp = x;
                x = x*Math.cos(ang) - z*Math.sin(ang);
                z = temp * Math.sin(ang) + p.getZ() * Math.cos(ang);
                p.setX(x + avgNode.x);
                p.setZ(z + avgNode.z);
                //p.setX(p.getX()*Math.cos(ang) - p.getZ()*Math.sin(ang));
                //p.setZ(temp * Math.sin(ang) + p.getZ() * Math.cos(ang));
            }
            for (int i = 0; i < data.size(); i++) {
                rectangleList.get(i).setX((data.get(i).getX() + width / 2 - 2));
                rectangleList.get(i).setY(((-1) * (data.get(i).getZ() - height / 2) - 2));
            }
            /*for (Rectangle r : rectangleList) {
                r.setX((r.getX() + width / 2 - 2) * value.get());
                r.setY(((-1) * (r.getY() - height / 2) - 2) * value.get());
            }*/
            table.refresh();
        });
        
        MenuItem allignWithClosestLayer = new MenuItem("Allign with the closest layer");
        allignWithClosestLayer.setOnAction((ActionEvent event) -> {
            if (data.size() > 0) {
                double y = data.get(0).getY();
                DoubleProperty diff = new SimpleDoubleProperty(10000);
                IntegerProperty otherPart = new SimpleIntegerProperty(-1);
                hm.getHistionMap().get(0).getItemMap().get(cellId).getItems().forEach(p -> {
                    if (p.getId() != partId) {
                        //System.out.println("21343242423");
                        if (Math.abs(p.getPointData().get(0).getY() - y) < diff.get()) {
                            diff.set(Math.abs(p.getPointData().get(0).getY() - y));
                            System.out.println(diff.get());
                            otherPart.set(p.getId());
                        }
                    }
                });
                if (otherPart.get() != -1) {
                    Node avg = hm.getHistionMap().get(0).getItemMap().get(cellId).getItemMap().get(otherPart.get()).getAvgNode();
                    /*Node avgNode = new Node(0, 0, 0);
                    for (TetgenPoint p : data) {
                        avgNode.x += p.getX();
                        avgNode.z += p.getZ();
                    }
                    avgNode.x /= data.size();
                    avgNode.z /= data.size();*/
                    System.out.println("Avg " + avg.x + " " + avg.z);
                    for (TetgenPoint p : data) {
                        p.setX(p.getX() + (avg.x - avgNode.x));
                        p.setZ(p.getZ() + (avg.z - avgNode.z));
                    }
                    for (Rectangle r : rectangleList) {
                        r.setX(r.getX() + (avg.x - avgNode.x));
                        r.setY(r.getY() - (avg.z - avgNode.z));
                    }
                    table.refresh();
                }
            }
        });
        
        MenuItem showCentralPointCoordinates = new MenuItem("Show central point coordinates");
        showCentralPointCoordinates.setOnAction((ActionEvent event) -> {
            AlertBox.display("Central point", "X: " + avgNode.x + " ; Z: " + avgNode.z);
        });
        
        /*MenuItem allignWithLowerLayer = new MenuItem("Allign with lower layer");
        allignWithUpperLayer.setOnAction((ActionEvent event) -> {
            AlertBox.display("Avg", "");
        });*/
        
        //newX = centerX + (point2x-centerX)*Math.cos(x) - (point2y-centerY)*Math.sin(x);

//newY = centerY + (point2x-centerX)*Math.sin(x) + (point2y-centerY)*Math.cos(x);
        
        /*MenuItem addValue = new MenuItem("Add value to column");
        deletePoint.setOnAction((ActionEvent event) -> {
            TetgenPoint item = table.getSelectionModel().getSelectedItem();
            data.remove(item.getId() - 1);
            root.getChildren().remove(rectangleList.get(item.getId() - 1));
            rectangleList.remove(item.getId() - 1);
            count.set(count.get() - 1);
            for (int i = 0; i < data.size(); i++)
                data.get(i).setId(i + 1);
            table.getSelectionModel().clearSelection();
        });*/

        ContextMenu menu = new ContextMenu();
        menu.getItems().addAll(deletePoint, addToX, addToZ, scale, rotate, allignWithClosestLayer, showCentralPointCoordinates);
        table.setContextMenu(menu);
        table.setStyle("-fx-focus-color: transparent;\n" +
            "    -fx-faint-focus-color: transparent;");
        doneButton.disableProperty().bind(Bindings.isEmpty(nameField.textProperty()));
    }
    
    public void addPoint(TetgenPoint p) {
        if (data.size() > 0) {
            p.setY(data.get(0).getY());
        }
        data.add(p);
        avgNode = new Node(0, 0, 0);
        for (TetgenPoint point : data) {
            avgNode.x += point.getX();
            avgNode.z += point.getZ();
        }
        avgNode.x /= data.size();
        avgNode.z /= data.size();
    }
    
    @FXML
    private void doneAction() {
        if (partId == - 1) {
            hm.getHistionMap().get(0).getItemMap().get(cellId).addChild(new Part("Part <" + nameField.getText() + ">", data, cellId));
        }
        else {
            hm.getHistionMap().get(0).getItemMap().get(cellId).addChild(
                    new Part(partId, "Part <" + nameField.getText() + ">", data, cellId));
            //hm.getHistionMap().get(0).getItemMap().get(cellId).setShow(false);
            if ((count.get() - 1) != initialSize)
                hm.getHistionMap().get(0).getItemMap().get(cellId).setFacetData(FXCollections.observableArrayList());
            Cell newCell = new Cell(cellId, hm.getHistionMap().get(0).getItemMap().get(cellId));
            newCell.setShow(false);
            hm.getHistionMap().get(0).getItemMap().get(cellId).getItems().forEach(p -> {
                newCell.addChild(p);
            });
            //newCell.addChild(new Part(partId, "Part <" + nameField.getText() + ">", data, cellId));
            hm.getHistionMap().get(0).addChild(newCell);
        }
        Stage stage = (Stage) doneButton.getScene().getWindow();
        stage.close();
    }
    
    private void setTableEditable() {
        table.setEditable(true);
        table.getSelectionModel().cellSelectionEnabledProperty().set(true);
        table.setOnKeyPressed(event -> {
            if (event.getCode().isDigitKey()) {
                editFocusedCell();
            } else if (event.getCode() == KeyCode.RIGHT ||
                event.getCode() == KeyCode.TAB) {
                table.getSelectionModel().selectNext();
                event.consume();
            } else if (event.getCode() == KeyCode.LEFT) {
                selectPrevious();
                event.consume();
            }
        });
    }
    
    private void setupXColumn() {
        xColumn.setCellFactory(
            EditCell. <TetgenPoint, Double > forTableColumn(
                new MyDoubleStringConverter()));
        xColumn.setOnEditCommit(event -> {
            TetgenPoint item = table.getSelectionModel().getSelectedItem();
            change.set(true);
            final Double value = event.getNewValue() != null ?
            event.getNewValue() : event.getOldValue();
            TetgenPoint tp = ((TetgenPoint) event.getTableView().getItems()
                .get(event.getTablePosition().getRow()));
            tp.setX(value);
            if (Math.abs(value) < width / 2)
                rectangleList.get(tp.getId() - 1).setX(value + width / 2 - 2);
            else
                rectangleList.get(tp.getId() - 1).setX(4000 + width / 2 - 2);
            table.refresh();
        });
    }
    
    private void setupYColumn() {
        yColumn.setCellFactory(
            EditCell. <TetgenPoint, Double > forTableColumn(
                new MyDoubleStringConverter()));
        yColumn.setOnEditCommit(event -> {
            change.set(true);
            final Double value = event.getNewValue() != null ?
            event.getNewValue() : event.getOldValue();
            for (TetgenPoint p : data) {
                p.setY(value);
            }
            table.refresh();
        });
    }
    
    private void setupZColumn() {
        zColumn.setCellFactory(
            EditCell. <TetgenPoint, Double > forTableColumn(
                new MyDoubleStringConverter()));
        zColumn.setOnEditCommit(event -> {
            TetgenPoint item = table.getSelectionModel().getSelectedItem();
            change.set(true);
            final Double value = event.getNewValue() != null ?
            event.getNewValue() : event.getOldValue();
            TetgenPoint tp = ((TetgenPoint) event.getTableView().getItems()
                .get(event.getTablePosition().getRow()));
            tp.setZ(value);
            if (Math.abs(value) < height / 2)
                rectangleList.get(tp.getId() - 1).setY((-1)*value + height / 2 - 2);
            else
                rectangleList.get(tp.getId() - 1).setY(4000 + height / 2 - 2);
            table.refresh();
        });
    }
    
    private void editFocusedCell() {
        final TablePosition <TetgenPoint, ? > focusedCell = table
            .focusModelProperty().get().focusedCellProperty().get();
        table.edit(focusedCell.getRow(), focusedCell.getTableColumn());
    }
    
    private void selectPrevious() {
        if (table.getSelectionModel().isCellSelectionEnabled()) {
            TablePosition <TetgenPoint, ? > pos = table.getFocusModel()
                .getFocusedCell();
            if (pos.getColumn() - 1 >= 0) {
                table.getSelectionModel().select(pos.getRow(),
                    getTableColumn(pos.getTableColumn(), -1));
            } else if (pos.getRow() < table.getItems().size()) {
                table.getSelectionModel().select(pos.getRow() - 1,
                    table.getVisibleLeafColumn(
                        table.getVisibleLeafColumns().size() - 1));
            }
        } else {
            int focusIndex = table.getFocusModel().getFocusedIndex();
            if (focusIndex == -1) {
                table.getSelectionModel().select(table.getItems().size() - 1);
            } else if (focusIndex > 0) {
                table.getSelectionModel().select(focusIndex - 1);
            }
        }
    }
    
    private TableColumn <TetgenPoint, ? > getTableColumn(
        final TableColumn <TetgenPoint, ? > column, int offset) {
        int columnIndex = table.getVisibleLeafIndex(column);
        int newColumnIndex = columnIndex + offset;
        return table.getVisibleLeafColumn(newColumnIndex);
    }
    
}
