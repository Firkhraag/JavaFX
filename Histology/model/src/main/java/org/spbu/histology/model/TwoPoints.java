package org.spbu.histology.model;

import javafx.geometry.Point3D;

public class TwoPoints {
    
    private Point3D p1;
    private Point3D p2;
    
    public TwoPoints(Point3D p1, Point3D p2) {
        this.p1 = p1;
        this.p2 = p2;
    }
    
    public Point3D getPoint1() {
        return p1;
    }
    
    public Point3D getPoint2() {
        return p2;
    }
    
    @Override
    public boolean equals(Object o) {

        if (o == this) return true;
        if (!(o instanceof TwoPoints)) {
            return false;
        }

        TwoPoints tp = (TwoPoints) o;

        return ((tp.p1.distance(p1) < 0.0001) &&
                (tp.p2.distance(p2) < 0.0001)) ||
                ((tp.p1.distance(p2) < 0.0001) &&
                (tp.p2.distance(p1) < 0.0001));
    }
    
}