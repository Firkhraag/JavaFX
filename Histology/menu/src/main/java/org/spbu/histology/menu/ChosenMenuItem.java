package org.spbu.histology.menu;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class ChosenMenuItem {
    
    private static IntegerProperty menuItem = new SimpleIntegerProperty(0);
    public static final IntegerProperty menuItemProperty() {
        return menuItem;
    }
    
    public static Integer getMenuItem() {
        return menuItem.get();
    }
    
    public static void setMenuItem(Integer num) {
        menuItem.set(num);
    }
    
}
