package org.spbu.histology.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class HideCells {
    
    private static ObservableList<String> cellNameToHideList = FXCollections.observableArrayList();
    
    public static ObservableList<String> getCellNameToHideList() {
        return cellNameToHideList;
    }
    
    public static void addCellNameToHide(String name) {
        cellNameToHideList.add(name);
    }
    
    public static void removeCellNameToHide(String name) {
        if (cellNameToHideList.contains(name))
            cellNameToHideList.remove(name);
    }
    
}
