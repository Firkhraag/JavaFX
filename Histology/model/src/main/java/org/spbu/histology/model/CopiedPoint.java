package org.spbu.histology.model;

public class CopiedPoint {
    
    private static TetgenPoint point;
    
    private static boolean copied = false;
    
    public static TetgenPoint getPoint() {
        return point;
    }
    
    public static boolean getCopied() {
        return copied;
    }
    
    public static void setPoint(TetgenPoint p) {
        point = p;
    }
    
    public static void setCopied(boolean c) {
        copied = c;
    }
    
}
