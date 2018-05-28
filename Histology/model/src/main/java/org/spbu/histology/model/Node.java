package org.spbu.histology.model;

public class Node {

    public double x;
    public double y;
    public double z;

    public Node(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Node(Node node) {
        this.x = node.x;
        this.y = node.y;
        this.z = node.z;
    }

    @Override
    public boolean equals(Object o) {

        if (o == this) {
            return true;
        }
        if (!(o instanceof Node)) {
            return false;
        }

        Node node = (Node) o;

        return Math.abs(node.x - x) < 0.000001
                && Math.abs(node.y - y) < 0.000001
                && Math.abs(node.z - z) < 0.000001;
    }

}
