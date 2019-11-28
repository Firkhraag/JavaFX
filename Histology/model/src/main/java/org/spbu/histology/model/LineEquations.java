package org.spbu.histology.model;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

public class LineEquations {

    private static final ObservableMap<Integer, ArrayList<Line>> lineMap
            = FXCollections.observableMap(new ConcurrentHashMap());

    public static ObservableMap<Integer, ArrayList<Line>> getLineMap() {
        return lineMap;
    }

    public static void addLine(Integer id, ArrayList<Line> lines) {
        lineMap.put(id, lines);
    }

    public static void removeLine(Integer id) {
        lineMap.remove(id);
    }

}
