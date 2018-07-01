package org.spbu.histology.model;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

public class CrossSectionVisualization {
    
    private static final ObservableMap<Integer, ArrayList<Polygon>> polygonMap
            = FXCollections.observableMap(new ConcurrentHashMap());
    private static final ObservableMap<Integer, Color> polygonColorMap
            = FXCollections.observableMap(new ConcurrentHashMap());
    private static final ObservableMap<Integer, ArrayList<javafx.scene.shape.Line>> lineMap
            = FXCollections.observableMap(new ConcurrentHashMap());

    public static ObservableMap<Integer, ArrayList<Polygon>> getPolygonMap() {
        return polygonMap;
    }

    public static void addPolygon(Integer id, ArrayList<Polygon> p) {
        polygonMap.put(id, p);
    }

    public static void removePolygon(Integer id) {
        polygonMap.remove(id);
    }
    
    public static ObservableMap<Integer, Color> getPolygonColorMap() {
        return polygonColorMap;
    }

    public static void addPolygonColor(Integer id, Color pc) {
        polygonColorMap.put(id, pc);
    }

    public static void removePolygonColor(Integer id) {
        polygonColorMap.remove(id);
    }

    public static ObservableMap<Integer, ArrayList<javafx.scene.shape.Line>> getLineMap() {
        return lineMap;
    }

    public static void addLine(Integer id, ArrayList<javafx.scene.shape.Line> l) {
        lineMap.put(id, l);
    }

    public static void removeLine(Integer id) {
        lineMap.remove(id);
    }
    
}
