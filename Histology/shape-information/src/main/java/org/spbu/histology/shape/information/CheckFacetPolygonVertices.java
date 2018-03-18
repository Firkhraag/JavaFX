package org.spbu.histology.shape.information;

public class CheckFacetPolygonVertices {
    
    public int vertex1;
    public int vertex2;
    public int vertex3;
    public int vertex4;
    public int vertex5;
    public int vertex6;
    public int vertex7;
    public int vertex8;
    public int vertex9;
    public int vertex10;
    public int vertex11;
    public int vertex12;
    public int vertex13;
    public int vertex14;
    public int vertex15;
    public int vertex16;
    public int vertex17;
    public int vertex18;
    public int vertex19;
    public int vertex20;
    public int vertex21;
    public int vertex22;
    public int vertex23;
    public int vertex24;
    public int vertex25;
    public int vertex26;
    public int vertex27;
    public int vertex28;
    public int vertex29;
    public int vertex30;
    
    public CheckFacetPolygonVertices(int vertex1, int vertex2, int vertex3, int vertex4,
            int vertex5, int vertex6, int vertex7, int vertex8, int vertex9,
            int vertex10, int vertex11, int vertex12, int vertex13, int vertex14,
            int vertex15, int vertex16, int vertex17, int vertex18, int vertex19,
            int vertex20, int vertex21, int vertex22, int vertex23, int vertex24,
            int vertex25, int vertex26, int vertex27, int vertex28, int vertex29,
            int vertex30) {
        this.vertex1 = (vertex1);
        this.vertex2 = (vertex2);
        this.vertex3 = (vertex3);
        this.vertex4 = (vertex4);
        this.vertex5 = (vertex5);
        this.vertex6 = (vertex6);
        this.vertex7 = (vertex7);
        this.vertex8 = (vertex8);
        this.vertex9 = (vertex9);
        this.vertex10 = (vertex10);
        this.vertex11 = (vertex11);
        this.vertex12 = (vertex12);
        this.vertex13 = (vertex13);
        this.vertex14 = (vertex14);
        this.vertex15 = (vertex15);
        this.vertex16 = (vertex16);
        this.vertex17 = (vertex17);
        this.vertex18 = (vertex18);
        this.vertex19 = (vertex19);
        this.vertex20 = (vertex20);
        this.vertex21 = (vertex21);
        this.vertex22 = (vertex22);
        this.vertex23 = (vertex23);
        this.vertex24 = (vertex24);
        this.vertex25 = (vertex25);
        this.vertex26 = (vertex26);
        this.vertex27 = (vertex27);
        this.vertex28 = (vertex28);
        this.vertex29 = (vertex29);
        this.vertex30 = (vertex30);
    }
    
    @Override
    public boolean equals(Object o) {

        if (o == this) return true;
        if (!(o instanceof CheckFacetPolygonVertices)) {
            return false;
        }

        CheckFacetPolygonVertices check = (CheckFacetPolygonVertices) o;

        return check.vertex1 == vertex1 &&
                check.vertex2 == vertex2 &&
                check.vertex3 == vertex3 &&
                check.vertex4 == vertex4 &&
                check.vertex5 == vertex5 &&
                check.vertex6 == vertex6 &&
                check.vertex7 == vertex7 &&
                check.vertex8 == vertex8 &&
                check.vertex9 == vertex9 &&
                check.vertex10 == vertex10 &&
                check.vertex11 == vertex11 &&
                check.vertex12 == vertex12 &&
                check.vertex13 == vertex13 &&
                check.vertex14 == vertex14 &&
                check.vertex15 == vertex15 &&
                check.vertex16 == vertex16 &&
                check.vertex17 == vertex17 &&
                check.vertex18 == vertex18 &&
                check.vertex19 == vertex19 &&
                check.vertex20 == vertex20 &&
                check.vertex21 == vertex21 &&
                check.vertex22 == vertex22 &&
                check.vertex23 == vertex23 &&
                check.vertex24 == vertex24 &&
                check.vertex25 == vertex25 &&
                check.vertex26 == vertex26 &&
                check.vertex27 == vertex27 &&
                check.vertex28 == vertex28 &&
                check.vertex29 == vertex29 &&
                check.vertex30 == vertex30;
    }
}
