package org.spbu.histology.model;

public class Line {
    public Node p1;
    public Node p2;
    public double k;
    public double b;
    public boolean vert;

    /*Line() {
    }*/

    public Line(Node x, Node y) {
        p1 = x;
        p2 = y;
        if (Math.abs(p1.x - p2.x) > 0.0001) {
            k = (p2.y - p1.y) / (p2.x - p1.x);
            b = p1.y - k * p1.x;
            vert = false;
        } else {
            vert = true;
            b = p1.x;
            k = 0;
        }
    }
    /*const Line operator = (
    const Line
    & rv

    
        )
	{
		p1 = rv.p1;
        p2 = rv.p2;
        k = rv.k;
        b = rv.b;
        vert = rv.vert;
        return  * this;
    }*/
};