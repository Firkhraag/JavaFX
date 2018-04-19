package org.spbu.histology.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class UpdateTree {
    
    private static BooleanProperty shouldBeUpdated = new SimpleBooleanProperty(false);
    public static final BooleanProperty shouldBeUpdatedProperty() {
        return shouldBeUpdated;
    }
    
    public static boolean getShouldBeUpdated() {
        return shouldBeUpdated.get();
    }
    
    public static void setShouldBeUpdated(boolean v) {
        shouldBeUpdated.set(v);
    }
    
}
