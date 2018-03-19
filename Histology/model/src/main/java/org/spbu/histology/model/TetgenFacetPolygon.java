package org.spbu.histology.model;

import javafx.beans.property.SimpleIntegerProperty;

public class TetgenFacetPolygon {
    
    private final SimpleIntegerProperty id;
    private final SimpleIntegerProperty facetNumber;
    private final SimpleIntegerProperty polygonNumber;
    private final SimpleIntegerProperty vertex1;
    private final SimpleIntegerProperty vertex2;
    private final SimpleIntegerProperty vertex3;
    private final SimpleIntegerProperty vertex4;
    private final SimpleIntegerProperty vertex5;
    private final SimpleIntegerProperty vertex6;
    private final SimpleIntegerProperty vertex7;
    private final SimpleIntegerProperty vertex8;
    private final SimpleIntegerProperty vertex9;
    private final SimpleIntegerProperty vertex10;
    private final SimpleIntegerProperty vertex11;
    private final SimpleIntegerProperty vertex12;
    private final SimpleIntegerProperty vertex13;
    private final SimpleIntegerProperty vertex14;
    private final SimpleIntegerProperty vertex15;
    private final SimpleIntegerProperty vertex16;
    private final SimpleIntegerProperty vertex17;
    private final SimpleIntegerProperty vertex18;
    private final SimpleIntegerProperty vertex19;
    private final SimpleIntegerProperty vertex20;
    private final SimpleIntegerProperty vertex21;
    private final SimpleIntegerProperty vertex22;
    private final SimpleIntegerProperty vertex23;
    private final SimpleIntegerProperty vertex24;
    private final SimpleIntegerProperty vertex25;
    private final SimpleIntegerProperty vertex26;
    private final SimpleIntegerProperty vertex27;
    private final SimpleIntegerProperty vertex28;
    private final SimpleIntegerProperty vertex29;
    private final SimpleIntegerProperty vertex30;
    
    public TetgenFacetPolygon(TetgenFacetPolygon fp) {
        this.id = new SimpleIntegerProperty(fp.getId());
        this.facetNumber = new SimpleIntegerProperty(fp.getFacetNumber());
        this.polygonNumber = new SimpleIntegerProperty(fp.getPolygonNumber());
        this.vertex1 = new SimpleIntegerProperty(fp.getVertex1());
        this.vertex2 = new SimpleIntegerProperty(fp.getVertex2());
        this.vertex3 = new SimpleIntegerProperty(fp.getVertex3());
        this.vertex4 = new SimpleIntegerProperty(fp.getVertex4());
        this.vertex5 = new SimpleIntegerProperty(fp.getVertex5());
        this.vertex6 = new SimpleIntegerProperty(fp.getVertex6());
        this.vertex7 = new SimpleIntegerProperty(fp.getVertex7());
        this.vertex8 = new SimpleIntegerProperty(fp.getVertex8());
        this.vertex9 = new SimpleIntegerProperty(fp.getVertex9());
        this.vertex10 = new SimpleIntegerProperty(fp.getVertex10());
        this.vertex11 = new SimpleIntegerProperty(fp.getVertex11());
        this.vertex12 = new SimpleIntegerProperty(fp.getVertex12());
        this.vertex13 = new SimpleIntegerProperty(fp.getVertex13());
        this.vertex14 = new SimpleIntegerProperty(fp.getVertex14());
        this.vertex15 = new SimpleIntegerProperty(fp.getVertex15());
        this.vertex16 = new SimpleIntegerProperty(fp.getVertex16());
        this.vertex17 = new SimpleIntegerProperty(fp.getVertex17());
        this.vertex18 = new SimpleIntegerProperty(fp.getVertex18());
        this.vertex19 = new SimpleIntegerProperty(fp.getVertex19());
        this.vertex20 = new SimpleIntegerProperty(fp.getVertex20());
        this.vertex21 = new SimpleIntegerProperty(fp.getVertex21());
        this.vertex22 = new SimpleIntegerProperty(fp.getVertex22());
        this.vertex23 = new SimpleIntegerProperty(fp.getVertex23());
        this.vertex24 = new SimpleIntegerProperty(fp.getVertex24());
        this.vertex25 = new SimpleIntegerProperty(fp.getVertex25());
        this.vertex26 = new SimpleIntegerProperty(fp.getVertex26());
        this.vertex27 = new SimpleIntegerProperty(fp.getVertex27());
        this.vertex28 = new SimpleIntegerProperty(fp.getVertex28());
        this.vertex29 = new SimpleIntegerProperty(fp.getVertex29());
        this.vertex30 = new SimpleIntegerProperty(fp.getVertex30());
    }
    
    public TetgenFacetPolygon(int i, int facetNumber, int polygonNumber, 
            int vertex1, int vertex2, int vertex3, int vertex4,
            int vertex5, int vertex6, int vertex7, int vertex8, int vertex9,
            int vertex10, int vertex11, int vertex12, int vertex13, int vertex14,
            int vertex15, int vertex16, int vertex17, int vertex18, int vertex19,
            int vertex20, int vertex21, int vertex22, int vertex23, int vertex24,
            int vertex25, int vertex26, int vertex27, int vertex28, int vertex29,
            int vertex30) {
        this.id = new SimpleIntegerProperty(i);
        this.facetNumber = new SimpleIntegerProperty(facetNumber);
        this.polygonNumber = new SimpleIntegerProperty(polygonNumber);
        this.vertex1 = new SimpleIntegerProperty(vertex1);
        this.vertex2 = new SimpleIntegerProperty(vertex2);
        this.vertex3 = new SimpleIntegerProperty(vertex3);
        this.vertex4 = new SimpleIntegerProperty(vertex4);
        this.vertex5 = new SimpleIntegerProperty(vertex5);
        this.vertex6 = new SimpleIntegerProperty(vertex6);
        this.vertex7 = new SimpleIntegerProperty(vertex7);
        this.vertex8 = new SimpleIntegerProperty(vertex8);
        this.vertex9 = new SimpleIntegerProperty(vertex9);
        this.vertex10 = new SimpleIntegerProperty(vertex10);
        this.vertex11 = new SimpleIntegerProperty(vertex11);
        this.vertex12 = new SimpleIntegerProperty(vertex12);
        this.vertex13 = new SimpleIntegerProperty(vertex13);
        this.vertex14 = new SimpleIntegerProperty(vertex14);
        this.vertex15 = new SimpleIntegerProperty(vertex15);
        this.vertex16 = new SimpleIntegerProperty(vertex16);
        this.vertex17 = new SimpleIntegerProperty(vertex17);
        this.vertex18 = new SimpleIntegerProperty(vertex18);
        this.vertex19 = new SimpleIntegerProperty(vertex19);
        this.vertex20 = new SimpleIntegerProperty(vertex20);
        this.vertex21 = new SimpleIntegerProperty(vertex21);
        this.vertex22 = new SimpleIntegerProperty(vertex22);
        this.vertex23 = new SimpleIntegerProperty(vertex23);
        this.vertex24 = new SimpleIntegerProperty(vertex24);
        this.vertex25 = new SimpleIntegerProperty(vertex25);
        this.vertex26 = new SimpleIntegerProperty(vertex26);
        this.vertex27 = new SimpleIntegerProperty(vertex27);
        this.vertex28 = new SimpleIntegerProperty(vertex28);
        this.vertex29 = new SimpleIntegerProperty(vertex29);
        this.vertex30 = new SimpleIntegerProperty(vertex30);
    }
    
    public int getId() {
        return id.get();
    }
 
    public void setId(int v) {
        id.set(v);
    }
    
    public int getFacetNumber() {
        return facetNumber.get();
    }
 
    public void setFacetNumber(int v) {
        facetNumber.set(v);
    }
    
    public int getPolygonNumber() {
        return polygonNumber.get();
    }
 
    public void setPolygonNumber(int v) {
        polygonNumber.set(v);
    }
    
    public int getVertex1() {
        return vertex1.get();
    }
    
    public int getVertex2() {
        return vertex2.get();
    }

    public int getVertex3() {
        return vertex3.get();
    }
 
    public int getVertex4() {
        return vertex4.get();
    }
 
    public int getVertex5() {
        return vertex5.get();
    }
 
    public int getVertex6() {
        return vertex6.get();
    }
    
    public int getVertex7() {
        return vertex7.get();
    }
    
    public int getVertex8() {
        return vertex8.get();
    }
    
    public int getVertex9() {
        return vertex9.get();
    }
    
    public int getVertex10() {
        return vertex10.get();
    }
    
    public int getVertex11() {
        return vertex11.get();
    }
    
    public int getVertex12() {
        return vertex12.get();
    }
    
    public int getVertex13() {
        return vertex13.get();
    }
    
    public int getVertex14() {
        return vertex14.get();
    }
    
    public int getVertex15() {
        return vertex15.get();
    }
    
    public int getVertex16() {
        return vertex16.get();
    }
    
    public int getVertex17() {
        return vertex17.get();
    }
    
    public int getVertex18() {
        return vertex18.get();
    }
    
    public int getVertex19() {
        return vertex19.get();
    }
    
    public int getVertex20() {
        return vertex20.get();
    }
    
    public int getVertex21() {
        return vertex21.get();
    }
    
    public int getVertex22() {
        return vertex22.get();
    }
    
    public int getVertex23() {
        return vertex23.get();
    }
    
    public int getVertex24() {
        return vertex24.get();
    }
    
    public int getVertex25() {
        return vertex25.get();
    }
    
    public int getVertex26() {
        return vertex26.get();
    }
    
    public int getVertex27() {
        return vertex27.get();
    }
    
    public int getVertex28() {
        return vertex28.get();
    }
    
    public int getVertex29() {
        return vertex29.get();
    }
    
    public int getVertex30() {
        return vertex30.get();
    }
    
    public int getVertex(int n) {
        switch (n) {
            case 1:
                return vertex1.get();
            case 2:
                return vertex2.get();
            case 3:
                return vertex3.get();
            case 4:
                return vertex4.get();
            case 5:
                return vertex5.get();
            case 6:
                return vertex6.get();
            case 7:
                return vertex7.get();
            case 8:
                return vertex8.get();
            case 9:
                return vertex9.get();
            case 10:
                return vertex10.get();
            case 11:
                return vertex11.get();
            case 12:
                return vertex12.get();
            case 13:
                return vertex13.get();
            case 14:
                return vertex14.get();
            case 15:
                return vertex15.get();
            case 16:
                return vertex16.get();
            case 17:
                return vertex17.get();
            case 18:
                return vertex18.get();
            case 19:
                return vertex19.get();
            case 20:
                return vertex20.get();
            case 21:
                return vertex21.get();
            case 22:
                return vertex22.get();
            case 23:
                return vertex23.get();
            case 24:
                return vertex24.get();
            case 25:
                return vertex25.get();
            case 26:
                return vertex26.get();
            case 27:
                return vertex27.get();
            case 28:
                return vertex28.get();
            case 29:
                return vertex29.get();
            case 30:
                return vertex30.get();
            default:
                return 0;
        }
    }
    
    public void setVertex(int v, int n) {
        switch (n) {
            case 1:
                vertex1.set(v);
                break;
            case 2:
                vertex2.set(v);
                break;
            case 3:
                vertex3.set(v);
                break;
            case 4:
                vertex4.set(v);
                break;
            case 5:
                vertex5.set(v);
                break;
            case 6:
                vertex6.set(v);
                break;
            case 7:
                vertex7.set(v);
                break;
            case 8:
                vertex8.set(v);
                break;
            case 9:
                vertex9.set(v);
                break;
            case 10:
                vertex10.set(v);
                break;
            case 11:
                vertex11.set(v);
                break;
            case 12:
                vertex12.set(v);
                break;
            case 13:
                vertex13.set(v);
                break;
            case 14:
                vertex14.set(v);
                break;
            case 15:
                vertex15.set(v);
                break;
            case 16:
                vertex16.set(v);
                break;
            case 17:
                vertex17.set(v);
                break;
            case 18:
                vertex18.set(v);
                break;
            case 19:
                vertex19.set(v);
                break;
            case 20:
                vertex20.set(v);
                break;
            case 21:
                vertex21.set(v);
                break;
            case 22:
                vertex22.set(v);
                break;
            case 23:
                vertex23.set(v);
                break;
            case 24:
                vertex24.set(v);
                break;
            case 25:
                vertex25.set(v);
                break;
            case 26:
                vertex26.set(v);
                break;
            case 27:
                vertex27.set(v);
                break;
            case 28:
                vertex28.set(v);
                break;
            case 29:
                vertex29.set(v);
                break;
            case 30:
                vertex30.set(v);
                break;
        }
    }
}
