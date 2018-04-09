package org.spbu.histology.model;

import java.util.ArrayList;
import javafx.scene.shape.MeshView;
import org.spbu.histology.fxyz.Text3DMesh;

public class DigitMeshes {
    
    final static ArrayList<MeshView> digit0Meshes = new ArrayList<>();
    final static ArrayList<MeshView> digit1Meshes = new ArrayList<>();
    final static ArrayList<MeshView> digit2Meshes = new ArrayList<>();
    final static ArrayList<MeshView> digit3Meshes = new ArrayList<>();
    final static ArrayList<MeshView> digit4Meshes = new ArrayList<>();
    final static ArrayList<MeshView> digit5Meshes = new ArrayList<>();
    final static ArrayList<MeshView> digit6Meshes = new ArrayList<>();
    final static ArrayList<MeshView> digit7Meshes = new ArrayList<>();
    final static ArrayList<MeshView> digit8Meshes = new ArrayList<>();
    final static ArrayList<MeshView> digit9Meshes = new ArrayList<>();
    
    public static ArrayList<MeshView> getMeshList(int num) {
        
        switch (num) {
            case 0:
                if (digit0Meshes.isEmpty()) {
                    Text3DMesh text = new Text3DMesh("0", "Arial", 11, true, 1, 0d, 1);
                    for (MeshView m : text.getMeshes()) {
                        digit0Meshes.add(new MeshView(m.getMesh()));
                    }
                }
                return digit0Meshes;
            case 1:
                if (digit1Meshes.isEmpty()) {
                    Text3DMesh text = new Text3DMesh("1", "Arial", 11, true, 1, 0d, 1);
                    for (MeshView m : text.getMeshes()) {
                        digit1Meshes.add(new MeshView(m.getMesh()));
                    }
                }
                return digit1Meshes;
            case 2:
                if (digit2Meshes.isEmpty()) {
                    Text3DMesh text = new Text3DMesh("2", "Arial", 11, true, 1, 0d, 1);
                    for (MeshView m : text.getMeshes()) {
                        digit2Meshes.add(new MeshView(m.getMesh()));
                    }
                }
                return digit2Meshes;
            case 3:
                if (digit3Meshes.isEmpty()) {
                    Text3DMesh text = new Text3DMesh("3", "Arial", 11, true, 1, 0d, 1);
                    for (MeshView m : text.getMeshes()) {
                        digit3Meshes.add(new MeshView(m.getMesh()));
                    }
                }
                return digit3Meshes;
            case 4:
                if (digit4Meshes.isEmpty()) {
                    Text3DMesh text = new Text3DMesh("4", "Arial", 11, true, 1, 0d, 1);
                    for (MeshView m : text.getMeshes()) {
                        digit4Meshes.add(new MeshView(m.getMesh()));
                    }
                }
                return digit4Meshes;
            case 5:
                if (digit5Meshes.isEmpty()) {
                    Text3DMesh text = new Text3DMesh("5", "Arial", 11, true, 1, 0d, 1);
                    for (MeshView m : text.getMeshes()) {
                        digit5Meshes.add(new MeshView(m.getMesh()));
                    }
                }
                return digit5Meshes;
            case 6:
                if (digit6Meshes.isEmpty()) {
                    Text3DMesh text = new Text3DMesh("6", "Arial", 11, true, 1, 0d, 1);
                    for (MeshView m : text.getMeshes()) {
                        digit6Meshes.add(new MeshView(m.getMesh()));
                    }
                }
                return digit6Meshes;
            case 7:
                if (digit7Meshes.isEmpty()) {
                    Text3DMesh text = new Text3DMesh("7", "Arial", 11, true, 1, 0d, 1);
                    for (MeshView m : text.getMeshes()) {
                        digit7Meshes.add(new MeshView(m.getMesh()));
                    }
                }
                return digit7Meshes;
            case 8:
                if (digit8Meshes.isEmpty()) {
                    Text3DMesh text = new Text3DMesh("8", "Arial", 11, true, 1, 0d, 1);
                    for (MeshView m : text.getMeshes()) {
                        digit8Meshes.add(new MeshView(m.getMesh()));
                    }
                }
                return digit8Meshes;
            case 9:
                if (digit9Meshes.isEmpty()) {
                    Text3DMesh text = new Text3DMesh("9", "Arial", 11, true, 1, 0d, 1);
                    for (MeshView m : text.getMeshes()) {
                        digit9Meshes.add(new MeshView(m.getMesh()));
                    }
                }
                return digit9Meshes;
        }
        return new ArrayList<>();
    }
    
}
