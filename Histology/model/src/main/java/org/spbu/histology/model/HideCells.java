package org.spbu.histology.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class HideCells {
    
    private static ObservableList<Integer> cellIdToHideList = FXCollections.observableArrayList();

    public static ObservableList<Integer> getCellIdToHideList() {
        return cellIdToHideList;
    }

    public static void addCellIdToHide(Integer id) {
        cellIdToHideList.add(id);
    }

    public static void removeCellIdToHide(Integer id) {
        if (cellIdToHideList.contains(id)) {
            cellIdToHideList.remove(id);
        }
    }

}
