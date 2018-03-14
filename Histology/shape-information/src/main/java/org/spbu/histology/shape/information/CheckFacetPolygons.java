package org.spbu.histology.shape.information;

public class CheckFacetPolygons {
    
    public int facetNumber;
    public int polygonNumber;
    
    public CheckFacetPolygons(int facetNumber, int polygonNumber) {
        this.facetNumber = facetNumber;
        this.polygonNumber = polygonNumber;
    }
    
    @Override
    public boolean equals(Object o) {

        if (o == this) return true;
        if (!(o instanceof CheckFacetPolygons)) {
            return false;
        }

        CheckFacetPolygons check = (CheckFacetPolygons) o;

        return check.facetNumber == facetNumber &&
                check.polygonNumber == polygonNumber;
    }
}
