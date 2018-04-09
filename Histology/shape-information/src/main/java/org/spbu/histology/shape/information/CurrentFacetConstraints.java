package org.spbu.histology.shape.information;

import java.util.ArrayList;

public class CurrentFacetConstraints {
    
    private static int maxNumOfPoints;
    
    public int getMaxNumOfPoints() {
        return maxNumOfPoints;
    }
    
    public void setMaxNumOfPoints(int maxNumOfPoints) {
        this.maxNumOfPoints = maxNumOfPoints;
    }
    
    private static ArrayList<Integer> alreadyUsedPoints;
    
    public ArrayList<Integer> getAlreadyUsedPoints() {
        return alreadyUsedPoints;
    }
    
    /*public void setMaxNumOfPoints(int maxNumOfPoints) {
        this.maxNumOfPoints = maxNumOfPoints;
    }*/
    
}
