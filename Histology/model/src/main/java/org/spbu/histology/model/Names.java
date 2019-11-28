package org.spbu.histology.model;

import java.util.ArrayList;

public class Names {

    private static final ArrayList<String> cellNameList = new ArrayList<>();

    public static void addCellName(String name) {
        cellNameList.add(name);
    }

    public static void removeCellName(String name) {
        cellNameList.remove(name);
    }

    public static boolean containsCellName(String name) {
        return cellNameList.contains(name);
    }

}
