package org.spbu.histology.model;

import java.util.Comparator;

public class TetgenFacetHoleComparator implements Comparator<TetgenFacetHole> {
    @Override
    public int compare(TetgenFacetHole o1, TetgenFacetHole o2) {
        return ((Integer)o1.getFacetNumber()).compareTo((Integer)o2.getFacetNumber());
    }
}
