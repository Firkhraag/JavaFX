package org.spbu.histology.model;

import java.util.Comparator;

public class TetgenFacetPolygonComparator implements Comparator<TetgenFacetPolygon> {
    @Override
    public int compare(TetgenFacetPolygon o1, TetgenFacetPolygon o2) {
        return ((Integer)o1.getFacetNumber()).compareTo((Integer)o2.getFacetNumber());
    }
}
