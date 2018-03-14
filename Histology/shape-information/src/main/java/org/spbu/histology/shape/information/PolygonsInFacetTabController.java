package org.spbu.histology.shape.information;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.spbu.histology.model.TetgenFacetPolygon;


public class PolygonsInFacetTabController implements Initializable {
    
    @FXML
    private VBox polygonVBox;
      
    @FXML
    private HBox headerHBox;
    
    @FXML
    private TableView<TetgenFacetPolygon> table;
    
    @FXML
    private Button button;
    
    @FXML
    private TextField numberOfRowsField;
    
    @FXML
    private TextField numberOfVerticesField;
    
    @FXML
    private TableColumn < TetgenFacetPolygon, Integer > vertex1Column;
    
    @FXML
    private TableColumn < TetgenFacetPolygon, Integer > vertex2Column;
    
    @FXML
    private TableColumn < TetgenFacetPolygon, Integer > vertex3Column;
    
    private int vertSize = 3;
    
    @FXML
    private TableColumn < TetgenFacetPolygon, Integer > facetNumberColumn;
    
    @FXML
    private TableColumn < TetgenFacetPolygon, Integer > polygonNumberColumn;
    
    public static ObservableList<TetgenFacetPolygon> data;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        headerHBox.setSpacing(20);
        polygonVBox.setSpacing(10);
        headerHBox.setPadding(new Insets(10, 10, 10, 10));
        setupVertexColumn(vertex1Column, 1);
        setupVertexColumn(vertex2Column, 2);
        setupVertexColumn(vertex3Column, 3);
        setupFacetNumberColumn();
        setupPolygonNumberColumn();
        setTableEditable();
        
        data = FXCollections.observableArrayList();
        if (ShapeInformationInitialization.mode.equals("Edit")) {
            data = ShapeInformationInitialization.getShape().getPolygonsInFacetData();
            numberOfRowsField.setText(String.valueOf(data.size()));
            numberOfVerticesField.setText(String.valueOf(ShapeInformationInitialization.getShape().getMaxNumberOfPoints()));
            button.fire();
        }
        table.setItems(data);
    }
    
    @FXML
    private void updateAction() {
        try {
            int colNum = Integer.parseInt(numberOfVerticesField.getText());
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
                    TableColumn < TetgenFacetPolygon, Integer > vertexColumn = new TableColumn("Vertex " + (i + 1));
                    vertexColumn.setPrefWidth(100);
                    vertexColumn.setCellValueFactory(new PropertyValueFactory<TetgenFacetPolygon,Integer>("vertex" + (i + 1)));
                    setupVertexColumn(vertexColumn, i + 1);
                    table.getColumns().add(vertexColumn);
                    vertSize++;
                }
            } else if (colNum < vertSize) {
                int tempSize = vertSize;
                for (int i = colNum; i < tempSize; i++) {
                    table.getColumns().remove(colNum + 3);
                    vertSize--;
                }
            }
            
            int num = Integer.parseInt(numberOfRowsField.getText());
            int size = data.size();
            if (num < size) {
                for (int i = num; i < size; i++) {
                    data.remove(num);
                }
            } else if (num > size) {
                for (int i = size; i < num; i++) {
                    TetgenFacetPolygon n = new TetgenFacetPolygon(i + 1,i + 1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0);
                    data.add(n);
                }
            }
        } catch (Exception ex) {
            
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
    
    private void setupFacetNumberColumn() {
        facetNumberColumn.setCellFactory(EditCell. <TetgenFacetPolygon, Integer > forTableColumn(
                new MyIntegerStringConverter()));
        facetNumberColumn.setOnEditCommit(event -> {
            final Integer value = event.getNewValue() != null ?
            event.getNewValue() : event.getOldValue();
            ((TetgenFacetPolygon) event.getTableView().getItems()
                .get(event.getTablePosition().getRow())).setFacetNumber(value);
            table.refresh();
        });
    }
    
    private void setupPolygonNumberColumn() {
        polygonNumberColumn.setCellFactory(EditCell. <TetgenFacetPolygon, Integer > forTableColumn(
                new MyIntegerStringConverter()));
        polygonNumberColumn.setOnEditCommit(event -> {
            final Integer value = event.getNewValue() != null ?
            event.getNewValue() : event.getOldValue();
            ((TetgenFacetPolygon) event.getTableView().getItems()
                .get(event.getTablePosition().getRow())).setPolygonNumber(value);
            table.refresh();
        });
    }
    
    private void setupVertexColumn(TableColumn < TetgenFacetPolygon, Integer > col, int n) {
        col.setCellFactory(EditCell. <TetgenFacetPolygon, Integer > forTableColumn(
                new MyIntegerStringConverter()));
        col.setOnEditCommit(event -> {
            final Integer value = event.getNewValue() != null ?
            event.getNewValue() : event.getOldValue();
            ((TetgenFacetPolygon) event.getTableView().getItems()
                .get(event.getTablePosition().getRow())).setVertex(value, n);
            table.refresh();
        });
    }
    
    private void editFocusedCell() {
        final TablePosition <TetgenFacetPolygon, ? > focusedCell = table
            .focusModelProperty().get().focusedCellProperty().get();
        table.edit(focusedCell.getRow(), focusedCell.getTableColumn());
    }
    
    private void selectPrevious() {
        if (table.getSelectionModel().isCellSelectionEnabled()) {
            TablePosition <TetgenFacetPolygon, ? > pos = table.getFocusModel()
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
    
    private TableColumn <TetgenFacetPolygon, ? > getTableColumn(
        final TableColumn <TetgenFacetPolygon, ? > column, int offset) {
        int columnIndex = table.getVisibleLeafIndex(column);
        int newColumnIndex = columnIndex + offset;
        return table.getVisibleLeafColumn(newColumnIndex);
    }
    
}
