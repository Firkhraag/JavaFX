package org.spbu.histology.model;

public class TwoPoints {

    private Node p1;
    private Node p2;

    public TwoPoints(Node p1, Node p2) {
        this.p1 = p1;
        this.p2 = p2;
    }

    public Node getPoint1() {
        return p1;
    }

    public Node getPoint2() {
        return p2;
    }

    @Override
    public boolean equals(Object o) {

        if (o == this) {
            return true;
        }
        if (!(o instanceof TwoPoints)) {
            return false;
        }

        TwoPoints tp = (TwoPoints) o;

        return (tp.p1.equals(p1)
                && tp.p2.equals(p2))
                || (tp.p1.equals(p2)
                && tp.p2.equals(p1));
    }

}
