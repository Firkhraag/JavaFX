package org.spbu.histology.tetgen;

import java.util.Arrays;

public class TetgenResult {
    
    private double[] nodeList;
    private int[] tetrahedronList;
    private int[] faceList;
    
    public double[] getNodeList() {
        return nodeList;
    }
    
    public int[] getTetrahedronList() {
        return tetrahedronList;
    }
    
    public int[] getFaceList() {
        return faceList;
    }
    
    public TetgenResult(double[] nodeList, int[] tetrahedronList, int[] faceList) {
        this.nodeList = Arrays.copyOf(nodeList, nodeList.length);
        this.tetrahedronList = Arrays.copyOf(tetrahedronList, tetrahedronList.length);
        this.faceList = Arrays.copyOf(faceList, faceList.length);
    }
}
