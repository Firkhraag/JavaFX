package org.spbu.histology.model;

import javafx.beans.property.SimpleIntegerProperty;

public class TwoIntegers {

    private final SimpleIntegerProperty id;
    private final SimpleIntegerProperty point1;
    private final SimpleIntegerProperty point2;

    public TwoIntegers(int id, int p1, int p2) {
        this.id = new SimpleIntegerProperty(id);
        this.point1 = new SimpleIntegerProperty(p1);
        this.point2 = new SimpleIntegerProperty(p2);
    }

    public int getId() {
        return id.get();
    }

    public void setId(int v) {
        id.set(v);
    }

    public int getPoint1() {
        return point1.get();
    }

    public void setPoint1(int v) {
        point1.set(v);
    }

    public int getPoint2() {
        return point2.get();
    }

    public void setPoint2(int v) {
        point2.set(v);
    }

    @Override
    public boolean equals(Object o) {

        if (o == this) {
            return true;
        }
        if (!(o instanceof TwoIntegers)) {
            return false;
        }

        TwoIntegers t = (TwoIntegers) o;

        return ((t.point1.get() == point1.get())
                && (t.point2.get() == point2.get()))
                || ((t.point1.get() == point2.get())
                && (t.point2.get() == point1.get()));
    }

}
