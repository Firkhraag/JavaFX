package org.spbu.histology.shape.information;

import org.spbu.histology.model.CopiedPoint;
import org.spbu.histology.model.MyDoubleStringConverter;
import org.spbu.histology.model.EditCell;
import java.net.URL;
import java.util.Collections;
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
    
    private void findAvgNode() {
        avgNode = new Node(0, 0, 0);
        for (TetgenPoint point : data) {
            avgNode.x += point.getX();
            avgNode.z += point.getZ();
        }
        avgNode.x /= data.size();
        avgNode.z /= data.size();
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        hm = Lookup.getDefault().lookup(HistionManager.class);
        if (hm == null) {
            LifecycleManager.getDefault().exit();
        }
        //xColumn.setSortable(false);
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
                //rectangleList.get(oldSelection.getId() - 1).setStroke(Color.BLACK);
                rectangleList.get(oldSelection.getId() - 1).setFill(Color.BLACK);
            }
            
            if (newSelection != null) {
                //rectangleList.get(newSelection.getId() - 1).setStroke(Color.RED);
                rectangleList.get(newSelection.getId() - 1).setFill(Color.RED);
            }
        });
        MenuItem deletePoint = new MenuItem("Удалить точку");
        deletePoint.setOnAction((ActionEvent event) -> {
            if (!(table.getSelectionModel().getSelectedItem() == null)) {
                TetgenPoint item = table.getSelectionModel().getSelectedItem();
                data.remove(item.getId() - 1);
                root.getChildren().remove(rectangleList.get(item.getId() - 1));
                rectangleList.remove(item.getId() - 1);
                count.set(count.get() - 1);
                for (int i = 0; i < data.size(); i++)
                    data.get(i).setId(i + 1);
                table.getSelectionModel().clearSelection();
                findAvgNode();
            }
        });
        
        MenuItem addToX = new MenuItem("Сдвинуть точки по оси X");
        addToX.setOnAction((ActionEvent event) -> {
            DoubleProperty value = new SimpleDoubleProperty(0);
            ChangePoints.display(value);
            for (TetgenPoint p : data) {
                p.setX(p.getX() + value.get());
            }
            for (Rectangle r : rectangleList) {
                r.setX(r.getX() + value.get());
            }
            findAvgNode();
            table.refresh();
        });
        
        MenuItem addToZ = new MenuItem("Сдвинуть точки по оси Z");
        addToZ.setOnAction((ActionEvent event) -> {
            DoubleProperty value = new SimpleDoubleProperty(0);
            ChangePoints.display(value);
            for (TetgenPoint p : data) {
                p.setZ(p.getZ() + value.get());
            }
            for (Rectangle r : rectangleList) {
                r.setY(r.getY() - value.get());
            }
            findAvgNode();
            table.refresh();
        });
        
        MenuItem scale = new MenuItem("Масштабировать точки");
        scale.setOnAction((ActionEvent event) -> {
            DoubleProperty value = new SimpleDoubleProperty(1);
            ChangePoints.display(value);
            for (TetgenPoint p : data) {
                p.setX(p.getX() * value.get());
                p.setZ(p.getZ() * value.get());
            }
            for (int i = 0; i < data.size(); i++) {
                rectangleList.get(i).setX((data.get(i).getX() + width / 2 - 2));
                rectangleList.get(i).setY(((-1) * (data.get(i).getZ() - height / 2) - 2));
            }
            findAvgNode();
            table.refresh();
        });
        
        MenuItem rotate = new MenuItem("Повернуть точки");
        rotate.setOnAction((ActionEvent event) -> {
            DoubleProperty value = new SimpleDoubleProperty(0);
            DoubleProperty k = new SimpleDoubleProperty(0);
            //RotateBox.display(value, k);
            ChangePoints.display(value);
            if (k.get() != 0) {
                value.set(Math.toDegrees(Math.atan(k.get())));
            }
            if (value.get() != 0) {
                double ang = Math.toRadians(value.get());
                for (TetgenPoint p : data) {
                    double x = p.getX() - avgNode.x;
                    double z = p.getZ() - avgNode.z;
                    double temp = x;
                    x = x * Math.cos(ang) - z * Math.sin(ang);
                    z = temp * Math.sin(ang) + z * Math.cos(ang);
                    p.setX(x + avgNode.x);
                    p.setZ(z + avgNode.z);
                }
                for (int i = 0; i < data.size(); i++) {
                    rectangleList.get(i).setX((data.get(i).getX() + width / 2 - 2));
                    rectangleList.get(i).setY(((-1) * (data.get(i).getZ() - height / 2) - 2));
                }
                findAvgNode();
                table.refresh();
            }
        });
        
        MenuItem allignWithClosestLayer = new MenuItem("Выравнять точки по ближайшему слою");
        allignWithClosestLayer.setOnAction((ActionEvent event) -> {
            if (data.size() > 0) {
                double y = data.get(0).getY();
                DoubleProperty diff = new SimpleDoubleProperty(10000);
                IntegerProperty otherPart = new SimpleIntegerProperty(-1);
                hm.getHistionMap().get(0).getItemMap().get(cellId).getItems().forEach(p -> {
                    if (p.getId() != partId) {
                        if (Math.abs(p.getPointData().get(0).getY() - y) < diff.get()) {
                            diff.set(Math.abs(p.getPointData().get(0).getY() - y));
                            otherPart.set(p.getId());
                        }
                    }
                });
                if (otherPart.get() != -1) {
                    Node avg = hm.getHistionMap().get(0).getItemMap().get(cellId).getItemMap().get(otherPart.get()).getAvgNode();
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
            findAvgNode();
        });
        
        MenuItem showCentralPointCoordinates = new MenuItem("Показать координаты центра");
        showCentralPointCoordinates.setOnAction((ActionEvent event) -> {
            CentralPointBox.display(avgNode.x, avgNode.z);
        });
        
        MenuItem copyPoint = new MenuItem("Копировать точку");
        copyPoint.setOnAction((ActionEvent event) -> {
            if (!(table.getSelectionModel().getSelectedItem() == null)) {
                TetgenPoint item = new TetgenPoint(table.getSelectionModel().getSelectedItem());
                CopiedPoint.setPoint(item);
                CopiedPoint.setCopied(true);
            }
        });
        
        MenuItem pastePoint = new MenuItem("Вставить точку");
        pastePoint.setOnAction((ActionEvent event) -> {
            if (CopiedPoint.getCopied()) {
                CopiedPoint.getPoint().setId(count.get());
                count.set(count.get() + 1);
                addPoint(new TetgenPoint(CopiedPoint.getPoint()));
                Rectangle r = new Rectangle();
                r.setX(CopiedPoint.getPoint().getX() + width / 2 - 2);
                r.setY(-CopiedPoint.getPoint().getZ() + height / 2 - 2);
                r.setWidth(5);
                r.setHeight(5);
                root.getChildren().add(r);
                rectangleList.add(r);
                findAvgNode();
            }
        });

        ContextMenu menu = new ContextMenu();
        menu.getItems().addAll(copyPoint, pastePoint, deletePoint, addToX, addToZ, scale, rotate, allignWithClosestLayer, showCentralPointCoordinates);
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
        findAvgNode();
    }
    
    @FXML
    private void doneAction() {
        if (partId == - 1) {
            hm.getHistionMap().get(0).getItemMap().get(cellId).addChild(new Part("Слой <" + nameField.getText() + ">", data, cellId));
        }
        else {
            Collections.sort(data, (TetgenPoint o1, TetgenPoint o2) -> {
                int temp1 = o1.getId();
                int temp2 = o2.getId();
                if (temp1 == temp2) {
                    return 0;
                }
                return temp1 < temp2 ? -1 : 1;
            });
            hm.getHistionMap().get(0).getItemMap().get(cellId).addChild(
                    new Part(partId, "Слой <" + nameField.getText() + ">", data, cellId));
            if ((count.get() - 1) != initialSize)
                hm.getHistionMap().get(0).getItemMap().get(cellId).setFacetData(FXCollections.observableArrayList());
            Cell newCell = new Cell(cellId, hm.getHistionMap().get(0).getItemMap().get(cellId));
            newCell.setShow(false);
            hm.getHistionMap().get(0).getItemMap().get(cellId).getItems().forEach(p -> {
                newCell.addChild(p);
            });
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
            findAvgNode();
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
            findAvgNode();
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
