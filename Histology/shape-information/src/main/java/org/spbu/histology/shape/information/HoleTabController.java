/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.spbu.histology.shape.information;

import java.net.URL;
import java.util.ResourceBundle;
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
import org.spbu.histology.model.TetgenPoint;


public class HoleTabController implements Initializable {
    
    @FXML
    private VBox holeVBox;
      
    @FXML
    private HBox headerHBox;
    
    @FXML
    private TableView<TetgenPoint> table;
    
    @FXML
    private TextField numberOfHolesField;
    
    @FXML
    private TableColumn < TetgenPoint, Double > xColumn;
    
    @FXML
    private TableColumn < TetgenPoint, Double > yColumn;
    
    @FXML
    private TableColumn < TetgenPoint, Double > zColumn;
    
    @FXML
    private TableColumn idColumn;

    public static ObservableList<TetgenPoint> data;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        headerHBox.setSpacing(20);
        holeVBox.setSpacing(10);
        headerHBox.setPadding(new Insets(10, 10, 10, 10));
        setupXColumn();
        setupYColumn();
        setupZColumn();
        setTableEditable();
        
        data = FXCollections.observableArrayList();
        if (ShapeInformationInitialization.mode.equals("Edit")) {
            data = ShapeInformationInitialization.getShape().getHoleData();
            numberOfHolesField.setText(String.valueOf(data.size()));
        }
        table.setItems(data);
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
                    TetgenPoint n = new TetgenPoint(i + 1,0,0,0);
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
    
    private void setupXColumn() {
        xColumn.setCellFactory(
            EditCell. <TetgenPoint, Double > forTableColumn(
                new MyDoubleStringConverter()));
        xColumn.setOnEditCommit(event -> {
            final Double value = event.getNewValue() != null ?
            event.getNewValue() : event.getOldValue();
            ((TetgenPoint) event.getTableView().getItems()
                .get(event.getTablePosition().getRow())).setX(value);
            table.refresh();
        });
    }
    
    private void setupYColumn() {
        yColumn.setCellFactory(
            EditCell. <TetgenPoint, Double > forTableColumn(
                new MyDoubleStringConverter()));
        yColumn.setOnEditCommit(event -> {
            final Double value = event.getNewValue() != null ?
            event.getNewValue() : event.getOldValue();
            ((TetgenPoint) event.getTableView().getItems()
                .get(event.getTablePosition().getRow())).setY(value);
            table.refresh();
        });
    }
    
    private void setupZColumn() {
        zColumn.setCellFactory(
            EditCell. <TetgenPoint, Double > forTableColumn(
                new MyDoubleStringConverter()));
        zColumn.setOnEditCommit(event -> {
            final Double value = event.getNewValue() != null ?
            event.getNewValue() : event.getOldValue();
            ((TetgenPoint) event.getTableView().getItems()
                .get(event.getTablePosition().getRow())).setZ(value);
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
