package org.spbu.histology.model;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.scene.shape.Polygon;

public class PolygonList {
    /*private static ObservableMap<Integer, ArrayList<Polygon>> polygonList = 
            FXCollections.observableMap(new ConcurrentHashMap());*/
    private static ObservableMap<Integer, ArrayList<Polygon>> polygonList = 
            FXCollections.observableMap(new ConcurrentHashMap());
    
    public static void setPolygonList(ObservableMap<Integer, ArrayList<Polygon>> pl) {
        polygonList = pl;
    }
    
    public static ObservableMap<Integer, ArrayList<Polygon>> getPolygonList() {
        return polygonList;
    }
}
