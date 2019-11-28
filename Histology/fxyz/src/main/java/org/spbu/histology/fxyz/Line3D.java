package org.spbu.histology.fxyz;

//import java.util.ArrayList;
import java.util.List;
import javafx.geometry.Point3D;
//import javafx.scene.AmbientLight;
//import javafx.scene.DepthTest;
//import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.DrawMode;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;

public class Line3D {
    
    private final List<Point3D> points;
    private final float width;
    private final Color color;
    private MeshView meshView;
    private PhongMaterial material;
    
    public Line3D(Line3D l) {
        this.points = l.getPoints();
        this.width = l.getWidth();
        this.color = l.getColor();
        this.meshView = new MeshView(l.getMeshView().getMesh());
        
        material = new PhongMaterial(color);
        material.setDiffuseColor(color);
        material.setSpecularColor(color);
        meshView.setMaterial(material);
    }
    
    public List<Point3D> getPoints() {
        return points;
    }
    
    public float getWidth() {
        return width;
    }
    
    public Color getColor() {
        return color;
    }
    
    public Line3D(List<Point3D> points, float width, Color color) {
        this.points = points;
        this.width = width;
        this.color = color;
        material = new PhongMaterial();
        material.setDiffuseColor(color);
        material.setSpecularColor(color);
        
        buildTriangleTube();   
    }
    
    public MeshView getMeshView() {
        return meshView;
    }
    
    public void setColor(Color color) {
        material = new PhongMaterial(color);
        material.setDiffuseColor(color);
        material.setSpecularColor(color);
        meshView.setMaterial(material);
    }
    
    private void buildTriangleTube() {
        
        float half = width / 2.0f;
        float coef1 = half * 0.288675f;
        float coef2 = half * 0.57735f;
        float coef3 = half * 0.5f;
        float coef4 = half * 0.204124f;
        float coef5 = half * 0.493f;
        
        TriangleMesh mesh1 = new TriangleMesh();
        
        mesh1.getTexCoords().addAll(0,0); 
        
        //Beginning End Cap
        mesh1.getFaces().addAll(0,0, 1,0, 2,0);
        mesh1.getFaces().addAll(points.size()*3,0, points.size()*3 + 1,0, points.size()*3 + 2,0);
        mesh1.getFaces().addAll(points.size()*6,0, points.size()*6 + 1,0, points.size()*6 + 2,0);
        mesh1.getFaces().addAll(points.size()*9,0, points.size()*9 + 1,0, points.size()*9 + 2,0);
        //Now generate trianglestrips between each point 
        for (int i=3;i<points.size()*3;i+=3) {  //add each triangle tube segment 
            //Vertices wound counter-clockwise which is the default front face of any Triange
            //Triangle Tube Face 1
            mesh1.getFaces().addAll(i+2,0, i-2,0, i+1,0); //add secondary Width face
            mesh1.getFaces().addAll(i+2,0, i-1,0, i-2,0); //add primary face
            //Triangle Tube Face 2
            mesh1.getFaces().addAll(i+2,0, i-3,0, i-1,0); //add secondary Width face
            mesh1.getFaces().addAll(i,0, i-3,0, i+2,0); //add primary face
            //Triangle Tube Face 3
            mesh1.getFaces().addAll(i,0, i+1,0, i-3,0); //add primary face
            mesh1.getFaces().addAll(i+1,0, i-2,0, i-3,0); //add secondary Width face
            
            mesh1.getFaces().addAll(i+2+points.size()*3,0, i-2+points.size()*3,0, i+1+points.size()*3,0); //add secondary Width face
            mesh1.getFaces().addAll(i+2+points.size()*3,0, i-1+points.size()*3,0, i-2+points.size()*3,0); //add primary face
            //Triangle Tube Face 2
            mesh1.getFaces().addAll(i+2+points.size()*3,0, i-3+points.size()*3,0, i-1+points.size()*3,0); //add secondary Width face
            mesh1.getFaces().addAll(i+points.size()*3,0, i-3+points.size()*3,0, i+2+points.size()*3,0); //add primary face
            //Triangle Tube Face 3
            mesh1.getFaces().addAll(i+points.size()*3,0, i+1+points.size()*3,0, i-3+points.size()*3,0); //add primary face
            mesh1.getFaces().addAll(i+1+points.size()*3,0, i-2+points.size()*3,0, i-3+points.size()*3,0); //add secondary Width face
            
            mesh1.getFaces().addAll(i+2+points.size()*6,0, i-2+points.size()*6,0, i+1+points.size()*6,0); //add secondary Width face
            mesh1.getFaces().addAll(i+2+points.size()*6,0, i-1+points.size()*6,0, i-2+points.size()*6,0); //add primary face
            //Triangle Tube Face 2
            mesh1.getFaces().addAll(i+2+points.size()*6,0, i-3+points.size()*6,0, i-1+points.size()*6,0); //add secondary Width face
            mesh1.getFaces().addAll(i+points.size()*6,0, i-3+points.size()*6,0, i+2+points.size()*6,0); //add primary face
            //Triangle Tube Face 3
            mesh1.getFaces().addAll(i+points.size()*6,0, i+1+points.size()*6,0, i-3+points.size()*6,0); //add primary face
            mesh1.getFaces().addAll(i+1+points.size()*6,0, i-2+points.size()*6,0, i-3+points.size()*6,0); //add secondary Width face
            
            mesh1.getFaces().addAll(i+2+points.size()*9,0, i-2+points.size()*9,0, i+1+points.size()*9,0); //add secondary Width face
            mesh1.getFaces().addAll(i+2+points.size()*9,0, i-1+points.size()*9,0, i-2+points.size()*9,0); //add primary face
            //Triangle Tube Face 2
            mesh1.getFaces().addAll(i+2+points.size()*9,0, i-3+points.size()*9,0, i-1+points.size()*9,0); //add secondary Width face
            mesh1.getFaces().addAll(i+points.size()*9,0, i-3+points.size()*9,0, i+2+points.size()*9,0); //add primary face
            //Triangle Tube Face 3
            mesh1.getFaces().addAll(i+points.size()*9,0, i+1+points.size()*9,0, i-3+points.size()*9,0); //add primary face
            mesh1.getFaces().addAll(i+1+points.size()*9,0, i-2+points.size()*9,0, i-3+points.size()*9,0); //add secondary Width face
        }        
        
        //Final End Cap
        int last = points.size()*3 -1;
        mesh1.getFaces().addAll(last,0, last-1,0, last-2,0);
        mesh1.getFaces().addAll(last+points.size()*3,0, last-1+points.size()*3,0, last-2+points.size()*3,0);
        mesh1.getFaces().addAll(last+points.size()*6,0, last-1+points.size()*6,0, last-2+points.size()*6,0);
        mesh1.getFaces().addAll(last+points.size()*9,0, last-1+points.size()*9,0, last-2+points.size()*9,0);
        
        for (Point3D point : points) {
            float x = (float) point.getX();
            float y = (float) point.getY();
            float z = (float) point.getZ();
            
            mesh1.getPoints().addAll(x - coef1, y - coef3, z - coef4);
            mesh1.getPoints().addAll(x - coef1, y + coef3, z - coef4);
            mesh1.getPoints().addAll(x + coef2, y + coef3, z - coef4);
        }
        
        for (Point3D point : points) {
            float x = (float) point.getX();
            float y = (float) point.getY();
            float z = (float) point.getZ();
            
            mesh1.getPoints().addAll(x - coef1, y - coef3, z + coef4);
            mesh1.getPoints().addAll(x - coef1, y + coef3, z + coef4);
            mesh1.getPoints().addAll(x + coef2, y + coef3, z + coef4);
        }
        
        for (Point3D point : points) {
            float x = (float) point.getX();
            float y = (float) point.getY();
            float z = (float) point.getZ();
            
            mesh1.getPoints().addAll(x - coef1, y - coef3, z - coef4);
            mesh1.getPoints().addAll(x - coef1, y + coef3, z - coef4);
            mesh1.getPoints().addAll(x - coef2, y + coef3, z + coef5);
        }
        
        for (Point3D point : points) {
            float x = (float) point.getX();
            float y = (float) point.getY();
            float z = (float) point.getZ();
            
            mesh1.getPoints().addAll(x + coef1, y - coef3, z - coef4);
            mesh1.getPoints().addAll(x + coef1, y + coef3, z - coef4);
            mesh1.getPoints().addAll(x + coef2, y + coef3, z + coef5);
        }
        
        meshView = new MeshView(mesh1);
        meshView.setDrawMode(DrawMode.FILL);
        meshView.setMaterial(material); 
        meshView.setCullFace(CullFace.BACK);
    }
}
