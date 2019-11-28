package org.spbu.histology.model;

public class RecurrenceShifts {
    
    private static double xShift = 0;
    private static double zShift = 0;
    
    public static double getXShift() {
        return xShift;
    }
    public static double getZShift() {
        return zShift;
    }

    public static void setXShift(double shift) {
        xShift = shift;
    }
    public static void setZShift(double shift) {
        zShift = shift;
    }
    
}
