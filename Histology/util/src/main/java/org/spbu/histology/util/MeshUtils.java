package org.spbu.histology.util;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableFloatArray;
import javafx.geometry.Point3D;
import javafx.scene.shape.Mesh;
import javafx.scene.shape.ObservableFaceArray;
import javafx.scene.shape.TriangleMesh;
import com.sun.javafx.scene.shape.ObservableFaceArrayImpl;

public class MeshUtils {
    public static void mesh2STL(String fileName, ArrayList<Mesh> meshList) throws IOException{
        
        ObservableFaceArray faces = new ObservableFaceArrayImpl();
        ObservableFloatArray points = FXCollections.observableFloatArray();
        
        int size = 0;
        for (Mesh mesh : meshList) {
            if(!(mesh instanceof TriangleMesh)){
                return;
            }
            // Get faces
            ObservableFaceArray f = new ObservableFaceArrayImpl();
            f.addAll(((TriangleMesh)mesh).getFaces());
            for (int i = 0; i < f.size(); i++) {
                f.set(i, f.get(i) + size);
            }

            // Get vertices
            points.addAll(((TriangleMesh)mesh).getPoints());
            size += ((TriangleMesh)mesh).getPoints().size() / 3;
            //System.out.println(f.size());
            //System.out.println(((TriangleMesh)mesh).getPoints().size());
            faces.addAll(f);
        }
        
        int[] f=new int[faces.size()];
        faces.toArray(f);
        
        float[] p = new float[points.size()];
        points.toArray(p);
        
        StringBuilder sb = new StringBuilder();
        sb.append("solid meshFX\n");
        
        // convert faces to polygons
        for(int i=0; i<faces.size()/6; i++){
            //System.out.println(i);
            int i0=f[6*i], i1=f[6*i+2], i2=f[6*i+4];
            Point3D pA=new Point3D(p[3*i0], p[3*i0+1], p[3*i0+2]);
            Point3D pB=new Point3D(p[3*i1], p[3*i1+1], p[3*i1+2]);
            Point3D pC=new Point3D(p[3*i2], p[3*i2+1], p[3*i2+2]);
            Point3D pN=pB.subtract(pA).crossProduct(pC.subtract(pA)).normalize();

            sb.append("  facet normal ").append(pN.getX()).append(" ").append(pN.getY()).append(" ").append(pN.getZ()).append("\n");
            sb.append("    outer loop\n");
            sb.append("      vertex ").append(pA.getX()).append(" ").append(pA.getY()).append(" ").append(pA.getZ()).append("\n");
            sb.append("      vertex ").append(pB.getX()).append(" ").append(pB.getY()).append(" ").append(pB.getZ()).append("\n");
            sb.append("      vertex ").append(pC.getX()).append(" ").append(pC.getY()).append(" ").append(pC.getZ()).append("\n");
            sb.append("    endloop\n");
            sb.append("  endfacet\n");
        }

        sb.append("endsolid meshFX\n");

        // write file
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(fileName), Charset.forName("UTF-8"),
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
            writer.write(sb.toString());
        }
    }
}
