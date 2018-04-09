package org.spbu.histology.model;

import java.util.concurrent.ConcurrentHashMap;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

public abstract class HistologyObject<T extends HistologyObject<?>> {

    //public HistologyObject(Integer id, String name, ObservableMap<Integer, T> itemMap) {
    public HistologyObject(Integer id, String name) {
        //System.out.println(itemMap.size());
        setName(name);
        this.id = id;
        /*itemMap.forEach((i, t) -> {
            this.itemMap.put(i, t);
        });*/
    }

    private final StringProperty name = new SimpleStringProperty();

    public final StringProperty nameProperty() {
        return this.name;
    }


    public final String getName() {
        return this.nameProperty().get();
    }


    public final void setName(final String name) {
        this.nameProperty().set(name);
    }

    private Integer id;
    
    public Integer getId() {
        return this.id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    private final ObservableMap<Integer, T> itemMap = 
            FXCollections.observableMap(new ConcurrentHashMap());

    public ObservableMap<Integer, T> getItemMap() {
        return itemMap;
    }
    
    public ObservableList<T> getItems() {
        ObservableList<T> copyList = FXCollections.observableArrayList();
        itemMap.values().stream().forEach(s ->
            copyList.add(s));
        return copyList;
    }
    
    public abstract void addChild(T obj);
    public abstract void deleteChild(Integer id);
}
