package org.spbu.histology.shape.information;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import org.spbu.histology.fxyz.Line3D;
import org.spbu.histology.model.Cell;
import org.spbu.histology.model.TetgenFacet;


public class FacetTabController implements Initializable {
    
    @FXML
    private VBox polygonVBox;
      
    @FXML
    private HBox headerHBox;
    
    @FXML
    private TableView<TetgenFacet> table;
    
    @FXML
    private Button button;
    
    /*@FXML
    private TextField numberOfFacetsField;*/
    
    @FXML
    private TextField numberOfVerticesField;
    
    @FXML
    private TableColumn < TetgenFacet, Integer > vertex1Column;
    
    @FXML
    private TableColumn < TetgenFacet, Integer > vertex2Column;
    
    @FXML
    private TableColumn < TetgenFacet, Integer > vertex3Column;
    
    private int vertSize = 3;
    
    private ObservableList<TetgenFacet> data = FXCollections.observableArrayList();
    
    BooleanProperty change;
    IntegerProperty maxNumOfVertices;
    IntegerProperty tableFacetsNum;
    
    /*public void setShape(Shape s, BooleanProperty c) {
        change = c;
        data = s.getFacetData();
        numberOfFacetsField.setText(String.valueOf(data.size()));
        numberOfVerticesField.setText(String.valueOf(s.getMaxNumberOfPoints()));
        table.setItems(data);
        button.fire();
    }*/
    public void setCell(Cell c, BooleanProperty change, IntegerProperty maxNumOfVertices, IntegerProperty tableFacetsNum) {
        this.change = change;
        this.maxNumOfVertices = maxNumOfVertices;
        this.tableFacetsNum = tableFacetsNum;
        data = c.getFacetData();
        tableFacetsNum.set(data.size());
        //numberOfFacetsField.setText(String.valueOf(data.size()));
        
        //numberOfVerticesField.setText(String.valueOf(c.getMaxNumberOfPoints()));
        
        //this.maxNumOfVertices.set(c.getMaxNumberOfPoints());
        table.setItems(data);
        this.maxNumOfVertices.addListener((o, ov, nv) -> {
            if (maxNumOfVertices.get() != Integer.parseInt(numberOfVerticesField.getText())) {
                numberOfVerticesField.setText(String.valueOf(this.maxNumOfVertices.get()));
                button.fire();
            }
        });
        this.maxNumOfVertices.set(c.getMaxNumberOfPoints());
        //button.fire();
        this.change.set(true);
    }
    
    /*private ArrayList<Line3D> lineList;
    public void setLineList(ArrayList<Line3D> lineList) {
        this.lineList = lineList;
    }*/
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        headerHBox.setSpacing(20);
        polygonVBox.setSpacing(10);
        headerHBox.setPadding(new Insets(10, 10, 10, 10));
        setupVertexColumn(vertex1Column, 1);
        setupVertexColumn(vertex2Column, 2);
        setupVertexColumn(vertex3Column, 3);
        setTableEditable();
        setContextMenu();
        //PhongMaterial redMaterial = new PhongMaterial(Color.RED);
        /*table.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (oldSelection != null) {
                lineList.get(oldSelection.getId() - 1).setColor(Color.BLACK);
                lineList.get(oldSelection.getId() - 1).setTranslateZ(lineList.get(oldSelection.getId() - 1).getTranslateZ() + 2);
                lineList.get(oldSelection.getId() - 1).setTranslateX(lineList.get(oldSelection.getId() - 1).getTranslateX() + 2);
                //lineList.get(oldSelection.getId() - 1).material = redMaterial;
            }
            
            if (newSelection != null) {
                //System.out.println("1111");
                //lineList.set(newSelection.getId() - 1, new Line3D(lineList.get(newSelection.getId() - 1).points, 10f, Color.RED));
                lineList.get(newSelection.getId() - 1).setColor(Color.RED);
                //lineList.get(newSelection.getId() - 1).setScaleX(1.1);
                //lineList.get(newSelection.getId() - 1).setScaleY(1.1);
                //lineList.get(newSelection.getId() - 1).setScaleZ(1.1);
                lineList.get(newSelection.getId() - 1).setTranslateZ(lineList.get(newSelection.getId() - 1).getTranslateZ() - 2);
                lineList.get(newSelection.getId() - 1).setTranslateX(lineList.get(newSelection.getId() - 1).getTranslateX() - 2);
                //lineList.get(newSelection.getId() - 1) = new Line3D(lineList.get(newSelection.getId() - 1));
            }
        });*/
    }
    
    /*public void doUpdate(Shape theShape) {
        data = theShape.getFacetData();
        numberOfFacetsField.setText(String.valueOf(data.size()));
        numberOfVerticesField.setText(String.valueOf(theShape.getMaxNumberOfPoints()));
        button.fire();
    }*/
    
    private void setContextMenu() {
        MenuItem deletePoint = new MenuItem("Delete facet");
        deletePoint.setOnAction((ActionEvent event) -> {
            TetgenFacet item = table.getSelectionModel().getSelectedItem();
            data.remove(item.getId() - 1);
            /*root.getChildren().remove(rectangleList.get(item.getId() - 1));
            rectangleList.remove(item.getId() - 1);
            count.set(count.get() - 1);*/
            for (int i = 0; i < data.size(); i++)
                data.get(i).setId(i + 1);
            //table.getSelectionModel().clearSelection();
            tableFacetsNum.set(tableFacetsNum.get() - 1);
            change.set(true);
        });

        ContextMenu menu = new ContextMenu();
        menu.getItems().add(deletePoint);
        table.setContextMenu(menu);
    }
    
    @FXML
    private void addAction() {
        TetgenFacet n = new TetgenFacet(tableFacetsNum.get() + 1,0,0,0,0,0,0,0,0,0,0,
                            0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0);
        data.add(n);
        tableFacetsNum.set(tableFacetsNum.get() + 1);
    }
    
    @FXML
    private void updateAction() {
        try {
            int colNum = Integer.parseInt(numberOfVerticesField.getText());
            //int colNum = maxNumOfVertices.get();
            if (colNum > 30) {
                numberOfVerticesField.setText("30");
                colNum = 30;
            }
            if (colNum < 3) {
                numberOfVerticesField.setText("3");
                colNum = 3;
            }
            if (colNum > vertSize) {
                for (int i = vertSize; i < colNum; i++) {
                    TableColumn < TetgenFacet, Integer > vertexColumn = new TableColumn("Vertex " + (i + 1));
                    vertexColumn.setPrefWidth(100);
                    vertexColumn.setCellValueFactory(new PropertyValueFactory<>("vertex" + (i + 1)));
                    setupVertexColumn(vertexColumn, i + 1);
                    table.getColumns().add(vertexColumn);
                    vertSize++;
                }
            } else if (colNum < vertSize) {
                int max = 0;
                for (TetgenFacet tf : data) {
                    int temp = 0;
                    for (int j = 1; j <= 30; j++) {
                        if (tf.getVertex(j) == 0)
                            break;
                        temp++;
                    }
                    if (temp > max)
                        max = temp;
                }
                if (colNum < max) {
                    colNum = max;
                    numberOfVerticesField.setText(String.valueOf(colNum));
                }
                int tempSize = vertSize;
                //System.out.println(tempSize);
                //System.out.println(colNum);
                int i;
                for (i = colNum; i < tempSize; i++) {
                    table.getColumns().remove(colNum + 1);
                    vertSize--;
                    //System.out.println(i);
                }
            }
            
            /*int num = Integer.parseInt(numberOfFacetsField.getText());
            int size = data.size();
            if (num < size) {
                System.out.println("456464");
                for (int i = num; i < size; i++) {
                    data.remove(num);
                }
            } else if (num > size) {
                for (int i = size; i < num; i++) {
                    TetgenFacet n = new TetgenFacet(i + 1,0,0,0,0,0,0,0,0,0,0,
                            0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0);
                    data.add(n);
                }
            }*/
            //change.set(true);
            maxNumOfVertices.set(colNum);
            //numberOfVerticesField.setText(String.valueOf(colNum));
        } catch (Exception ex) {
            System.out.println("Error");
        }
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
    
    private void setupVertexColumn(TableColumn < TetgenFacet, Integer > col, int n) {
        col.setCellFactory(EditCell. <TetgenFacet, Integer > forTableColumn(
                new MyIntegerStringConverter()));
        col.setOnEditCommit(event -> {
            final Integer value = event.getNewValue() != null ?
            event.getNewValue() : event.getOldValue();
            ((TetgenFacet) event.getTableView().getItems()
                .get(event.getTablePosition().getRow())).setVertex(value, n);
            table.refresh();
            change.set(true);
        });
    }
    
    private void editFocusedCell() {
        final TablePosition <TetgenFacet, ? > focusedCell = table
            .focusModelProperty().get().focusedCellProperty().get();
        table.edit(focusedCell.getRow(), focusedCell.getTableColumn());
    }
    
    private void selectPrevious() {
        if (table.getSelectionModel().isCellSelectionEnabled()) {
            TablePosition <TetgenFacet, ? > pos = table.getFocusModel()
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
    
    private TableColumn <TetgenFacet, ? > getTableColumn(
        final TableColumn <TetgenFacet, ? > column, int offset) {
        int columnIndex = table.getVisibleLeafIndex(column);
        int newColumnIndex = columnIndex + offset;
        return table.getVisibleLeafColumn(newColumnIndex);
    }
    
}
