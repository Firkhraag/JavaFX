package com.toolbar;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class ChosenTool {
    
    private static IntegerProperty tool = new SimpleIntegerProperty(0);
    public static final IntegerProperty toolProperty() {
        return tool;
    }
    
    public static Integer getToolNumber() {
        return tool.get();
    }
    
    public static void setToolNumber(Integer num) {
        tool.set(num);
    }
    
}
