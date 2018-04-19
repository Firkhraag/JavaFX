/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.spbu.histology.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class HideCells {
    
    //private static BooleanProperty disableContextMenu = new SimpleBooleanProperty(false);
    
    /*private static ObservableList<Integer> cellIdToHideList = FXCollections.observableArrayList();
    
    public static ObservableList<Integer> getCellIdToHideList() {
        return cellIdToHideList;
    }
    
    public static void addCellIdToHide(Integer id) {
        cellIdToHideList.add(id);
    }
    
    public static void removeCellIdToHide(Integer id) {
        if (cellIdToHideList.contains(id))
            cellIdToHideList.remove(id);
    }*/
    
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
    
    /*private static ObservableList<Integer> cellIdToHideList = FXCollections.observableArrayList();
    
    public static ObservableList<Integer> getCellIdToHideList() {
        return cellIdToHideList;
    }
    
    public static void addCellIdToHide(Integer id) {
        cellIdToHideList.add(id);
    }
    
    public static void removeCellIdToHide(Integer id) {
        if (cellIdToHideList.contains(id))
            cellIdToHideList.remove(id);
    }*/
    
}
