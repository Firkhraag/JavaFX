package org.spbu.histology.tetgen;
import javax.swing.filechooser.FileSystemView;
import org.openide.LifecycleManager;

public class Tetgen {
    
    public static native TetgenResult tetrahedralization(int numberOfNodes, 
            double[] nodeList, int numberOfFacets, int[] numberOfPolygonsInFacet,
            int[] numberOfHolesInFacet, double[] holeListInFacet,
            int[] numberOfVerticesInPolygon, int[] vertexList, int numberOfHoles,
            double[] holeList, int numberOfRegions, double[] regionList, String switches);

    static {
        try {
            String dir = FileSystemView.getFileSystemView().getDefaultDirectory().getPath();
            /*for (int i = 0; i < 3; i++)
                dir = dir.substring(0, dir.lastIndexOf('\\'));
            dir = dir + "\\tetgen\\src\\main\\resources\\org\\spbu\\histology\\tetgen";*/
            dir = dir + "\\HistologyApp" + System.getProperty("sun.arch.data.model") + "\\Tetgen\\";
            System.load(dir + "\\Tetgen.dll");
        } catch (Exception e) {
            LifecycleManager.getDefault().exit();
        }
    }
}
