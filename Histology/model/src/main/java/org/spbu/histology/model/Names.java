package org.spbu.histology.model;

import java.util.ArrayList;

public class Names {
    
    private static final ArrayList<String> cellNameList = new ArrayList<>();
    //private static final ArrayList<String> partNameList = new ArrayList<>();
    
    public static void addCellName(String name) {
        cellNameList.add(name);
    }
    
    public static void removeCellName(String name) {
        //System.out.println(name);
        cellNameList.remove(name);
        //System.out.println(cellNameList);
    }
    
    public static boolean containsCellName(String name) {
        //System.out.println(cellNameList);
        return cellNameList.contains(name);
    }
    
    /*public static void addPartName(String name) {
        partNameList.add(name);
    }
    
    public static void removePartName(String name) {
        partNameList.remove(name);
    }
    
    public static boolean containsPartName(String name) {
        return partNameList.contains(name);
    }*/
    
}
