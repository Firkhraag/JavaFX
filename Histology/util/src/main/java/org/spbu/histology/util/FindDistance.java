package org.spbu.histology.util;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.spbu.histology.model.CopiedPoint;
import org.spbu.histology.model.EditCell;
import org.spbu.histology.model.MyDoubleStringConverter;
import org.spbu.histology.model.TetgenPoint;

public class FindDistance {

    public static void display() {
        TableView<TetgenPoint> table = new TableView();
        table.setPrefWidth(233);
        table.setMaxWidth(233);
        table.setPrefHeight(103);

        MenuItem pastePoint = new MenuItem("Вставить");
        pastePoint.setOnAction((ActionEvent event) -> {
            if (CopiedPoint.getCopied()) {
                if (!(table.getSelectionModel().getSelectedItem() == null)) {
                    TetgenPoint item = table.getSelectionModel().getSelectedItem();
                    item.setX(CopiedPoint.getPoint().getX());
                    item.setZ(CopiedPoint.getPoint().getZ());
                    table.refresh();
                }
            }
        });

        ContextMenu menu = new ContextMenu();
        menu.getItems().addAll(pastePoint);
        table.setContextMenu(menu);
        table.setStyle("-fx-focus-color: transparent;\n"
                + "    -fx-faint-focus-color: transparent;");

        TableColumn< TetgenPoint, Integer> idColumn = new TableColumn("#");
        idColumn.setCellValueFactory(new PropertyValueFactory("id"));
        idColumn.setPrefWidth(30);
        TableColumn< TetgenPoint, Double> xColumn = new TableColumn("X");
        xColumn.setCellValueFactory(new PropertyValueFactory("x"));
        xColumn.setPrefWidth(100);
        TableColumn< TetgenPoint, Double> zColumn = new TableColumn("Z");
        zColumn.setCellValueFactory(new PropertyValueFactory("z"));
        zColumn.setPrefWidth(100);
        table.getColumns().addAll(idColumn, xColumn, zColumn);
        setupXColumn(table, xColumn);
        setupZColumn(table, zColumn);
        setTableEditable(table);
        ObservableList<TetgenPoint> data = FXCollections.observableArrayList();
        data.add(new TetgenPoint(1, 0, 0, 0));
        data.add(new TetgenPoint(2, 0, 0, 0));
        table.setItems(data);

        Stage window = new Stage();
        window.setTitle("Расстояние");

        Label labelResult = new Label("Расстояние");
        TextField fieldResult = new TextField();
        fieldResult.setPrefWidth(208);
        fieldResult.setMaxWidth(208);

        Button resultButton = new Button("OK");
        resultButton.setPadding(new Insets(0, 20, 0, 20));
        resultButton.setOnAction(e -> {
            fieldResult.setText(String.valueOf(Math.sqrt(
                    (data.get(0).getX() - data.get(1).getX())
                    * (data.get(0).getX() - data.get(1).getX())
                    + (data.get(0).getZ() - data.get(1).getZ())
                    * (data.get(0).getZ() - data.get(1).getZ()))));
        });

        VBox layout = new VBox(10);
        layout.getChildren().addAll(table, resultButton, labelResult, fieldResult);
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout, 300, 300);
        window.setScene(scene);
        window.show();
    }

    static private void setTableEditable(TableView<TetgenPoint> table) {
        table.setEditable(true);
        table.getSelectionModel().cellSelectionEnabledProperty().set(true);
        table.setOnKeyPressed(event -> {
            if (event.getCode().isDigitKey()) {
                editFocusedCell(table);
            } else if (event.getCode() == KeyCode.RIGHT
                    || event.getCode() == KeyCode.TAB) {
                table.getSelectionModel().selectNext();
                event.consume();
            } else if (event.getCode() == KeyCode.LEFT) {
                selectPrevious(table);
                event.consume();
            }
        });
    }

    static private void setupXColumn(TableView<TetgenPoint> table,
            TableColumn< TetgenPoint, Double> xColumn) {
        xColumn.setCellFactory(
                EditCell.<TetgenPoint, Double>forTableColumn(
                        new MyDoubleStringConverter()));
        xColumn.setOnEditCommit(event -> {
            TetgenPoint item = table.getSelectionModel().getSelectedItem();
            final Double value = event.getNewValue() != null
                    ? event.getNewValue() : event.getOldValue();
            TetgenPoint tp = ((TetgenPoint) event.getTableView().getItems()
                    .get(event.getTablePosition().getRow()));
            tp.setX(value);
            table.refresh();
        });
    }

    static private void setupZColumn(TableView<TetgenPoint> table,
            TableColumn< TetgenPoint, Double> zColumn) {
        zColumn.setCellFactory(
                EditCell.<TetgenPoint, Double>forTableColumn(
                        new MyDoubleStringConverter()));
        zColumn.setOnEditCommit(event -> {
            TetgenPoint item = table.getSelectionModel().getSelectedItem();
            final Double value = event.getNewValue() != null
                    ? event.getNewValue() : event.getOldValue();
            TetgenPoint tp = ((TetgenPoint) event.getTableView().getItems()
                    .get(event.getTablePosition().getRow()));
            tp.setZ(value);
            table.refresh();
        });
    }

    static private void editFocusedCell(TableView<TetgenPoint> table) {
        final TablePosition<TetgenPoint, ?> focusedCell = table
                .focusModelProperty().get().focusedCellProperty().get();
        table.edit(focusedCell.getRow(), focusedCell.getTableColumn());
    }

    static private void selectPrevious(TableView<TetgenPoint> table) {
        if (table.getSelectionModel().isCellSelectionEnabled()) {
            TablePosition<TetgenPoint, ?> pos = table.getFocusModel()
                    .getFocusedCell();
            if (pos.getColumn() - 1 >= 0) {
                table.getSelectionModel().select(pos.getRow(),
                        getTableColumn(table, pos.getTableColumn(), -1));
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

    static private TableColumn<TetgenPoint, ?> getTableColumn(TableView<TetgenPoint> table,
            final TableColumn<TetgenPoint, ?> column, int offset) {
        int columnIndex = table.getVisibleLeafIndex(column);
        int newColumnIndex = columnIndex + offset;
        return table.getVisibleLeafColumn(newColumnIndex);
    }

}
