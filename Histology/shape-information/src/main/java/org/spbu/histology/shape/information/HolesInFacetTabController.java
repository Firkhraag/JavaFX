package org.spbu.histology.shape.information;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.property.BooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.spbu.histology.model.Shape;
import org.spbu.histology.model.TetgenFacetHole;

public class HolesInFacetTabController implements Initializable {
    
    @FXML
    private VBox holeVBox;
      
    @FXML
    private HBox headerHBox;
    
    @FXML
    private TableView<TetgenFacetHole> table;
    
    @FXML
    private TextField numberOfHolesField;
    
    @FXML
    private TableColumn < TetgenFacetHole, Double > xColumn;
    
    @FXML
    private TableColumn < TetgenFacetHole, Double > yColumn;
    
    @FXML
    private TableColumn < TetgenFacetHole, Double > zColumn;
    
    @FXML
    private TableColumn < TetgenFacetHole, Integer > facetNumberColumn;
    
    @FXML
    private TableColumn idColumn;
    
    private ObservableList<TetgenFacetHole> data = FXCollections.observableArrayList();
    
    BooleanProperty change;
    
    public void setShape(Shape s, BooleanProperty c) {
        change = c;
        data = s.getHolesInFacetData();
        numberOfHolesField.setText(String.valueOf(data.size()));
        table.setItems(data);
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        headerHBox.setSpacing(20);
        holeVBox.setSpacing(10);
        headerHBox.setPadding(new Insets(10, 10, 10, 10));
        setupXColumn();
        setupYColumn();
        setupZColumn();
        setupFacetNumberColumn();
        setTableEditable();
    }
    
    @FXML
    private void updateAction() {
        try {
            int num = Integer.parseInt(numberOfHolesField.getText());
            int size = data.size();
            if (num < size) {
                for (int i = num; i < size; i++) {
                    data.remove(num);
                }
            } else if (num > size) {
                for (int i = size; i < num; i++) {
                    TetgenFacetHole n = new TetgenFacetHole(i + 1,i + 1,0,0,0);
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
        facetNumberColumn.setCellFactory(EditCell. <TetgenFacetHole, Integer > forTableColumn(
                new MyIntegerStringConverter()));
        facetNumberColumn.setOnEditCommit(event -> {
            final Integer value = event.getNewValue() != null ?
            event.getNewValue() : event.getOldValue();
            ((TetgenFacetHole) event.getTableView().getItems()
                .get(event.getTablePosition().getRow())).setFacetNumber(value);
            table.refresh();
        });
    }
    
    private void setupXColumn() {
        xColumn.setCellFactory(
            EditCell. <TetgenFacetHole, Double > forTableColumn(
                new MyDoubleStringConverter()));
        xColumn.setOnEditCommit(event -> {
            change.set(true);
            final Double value = event.getNewValue() != null ?
            event.getNewValue() : event.getOldValue();
            ((TetgenFacetHole) event.getTableView().getItems()
                .get(event.getTablePosition().getRow())).setX(value);
            table.refresh();
        });
    }
    
    private void setupYColumn() {
        yColumn.setCellFactory(
            EditCell. <TetgenFacetHole, Double > forTableColumn(
                new MyDoubleStringConverter()));
        yColumn.setOnEditCommit(event -> {
            change.set(true);
            final Double value = event.getNewValue() != null ?
            event.getNewValue() : event.getOldValue();
            ((TetgenFacetHole) event.getTableView().getItems()
                .get(event.getTablePosition().getRow())).setY(value);
            table.refresh();
        });
    }
    
    private void setupZColumn() {
        zColumn.setCellFactory(
            EditCell. <TetgenFacetHole, Double > forTableColumn(
                new MyDoubleStringConverter()));
        zColumn.setOnEditCommit(event -> {
            change.set(true);
            final Double value = event.getNewValue() != null ?
            event.getNewValue() : event.getOldValue();
            ((TetgenFacetHole) event.getTableView().getItems()
                .get(event.getTablePosition().getRow())).setZ(value);
            table.refresh();
        });
    }
    
    private void editFocusedCell() {
        final TablePosition <TetgenFacetHole, ? > focusedCell = table
            .focusModelProperty().get().focusedCellProperty().get();
        table.edit(focusedCell.getRow(), focusedCell.getTableColumn());
    }
    
    private void selectPrevious() {
        if (table.getSelectionModel().isCellSelectionEnabled()) {
            TablePosition <TetgenFacetHole, ? > pos = table.getFocusModel()
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
    
    private TableColumn <TetgenFacetHole, ? > getTableColumn(
        final TableColumn <TetgenFacetHole, ? > column, int offset) {
        int columnIndex = table.getVisibleLeafIndex(column);
        int newColumnIndex = columnIndex + offset;
        return table.getVisibleLeafColumn(newColumnIndex);
    }
    
}
