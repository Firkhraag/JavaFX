package org.spbu.histology.fxyz;

import java.util.List;
import javafx.geometry.Point3D;
import javafx.scene.AmbientLight;
import javafx.scene.DepthTest;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.DrawMode;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;

public class Line3D extends Group {
    
    public List<Point3D> points;
    public float width = 2.0f;
    public Color color = Color.WHITE;
    private TriangleMesh mesh;
    public MeshView meshView;
    public PhongMaterial material;
    public static enum LineType {RIBBON, TRIANGLE};
    
    public Line3D(List<Point3D> points, float width, Color color) {
        this(points, width, color, LineType.TRIANGLE);   
    }
    
    public Line3D(List<Point3D> points, float width, Color color, LineType lineType ) {
        this.points = points;
        this.width = width;
        this.color = color;
        setDepthTest(DepthTest.ENABLE);        
        mesh  = new TriangleMesh();
        switch(lineType) {
            case TRIANGLE: buildTriangleTube(); break;
            case RIBBON: 
            default: buildRibbon(); break;
        }
        //Need to add the mesh to a MeshView before adding to our 3D scene 
        meshView = new MeshView(mesh);
        meshView.setDrawMode(DrawMode.FILL);  //Fill so that the line shows width
        material = new PhongMaterial(color);
        material.setDiffuseColor(color);
        material.setSpecularColor(color);
        meshView.setMaterial(material); 
        //Make sure you Cull the Back so that no black shows through
        meshView.setCullFace(CullFace.BACK);

        //Add some ambient light so folks can see it
        getChildren().add(meshView);         
    }
    
    public void setColor(Color color) {
        material = new PhongMaterial(color);
        material.setDiffuseColor(color);
        material.setSpecularColor(color);
        meshView.setMaterial(material);
    }
    
    private void buildTriangleTube() {
        //For each data point add three mesh points as an equilateral triangle
        float half = new Float(width / 2.0);
        for(Point3D point: points) {
            //-0.288675f*hw, -0.5f*hw, -0.204124f*hw,
            mesh.getPoints().addAll((float)point.getX() - 0.288675f*half, (float)point.getY() - 0.5f*half, (float)point.getZ() - 0.204124f*half);
            //-0.288675f*hw, 0.5f*hw, -0.204124f*hw, 
            mesh.getPoints().addAll((float)point.getX() - 0.288675f*half, (float)point.getY() + 0.5f*half, (float)point.getZ() - 0.204124f*half);
            //0.57735f*hw, 0f, -0.204124f*hw
            mesh.getPoints().addAll((float)point.getX() + 0.57735f*half, (float)point.getY() + 0.5f*half, (float)point.getZ() - 0.204124f*half);
        }
        //add dummy Texture Coordinate
        mesh.getTexCoords().addAll(0,0); 
        //Beginning End Cap
        mesh.getFaces().addAll(0,0, 1,0, 2,0);
        //Now generate trianglestrips between each point 
        for(int i=3;i<points.size()*3;i+=3) {  //add each triangle tube segment 
            //Vertices wound counter-clockwise which is the default front face of any Triange
            //Triangle Tube Face 1
            mesh.getFaces().addAll(i+2,0, i-2,0, i+1,0); //add secondary Width face
            mesh.getFaces().addAll(i+2,0, i-1,0, i-2,0); //add primary face
            //Triangle Tube Face 2
            mesh.getFaces().addAll(i+2,0, i-3,0, i-1,0); //add secondary Width face
            mesh.getFaces().addAll(i,0, i-3,0, i+2,0); //add primary face
            //Triangle Tube Face 3
            mesh.getFaces().addAll(i,0, i+1,0, i-3,0); //add primary face
            mesh.getFaces().addAll(i+1,0, i-2,0, i-3,0); //add secondary Width face
        }        
        //Final End Cap
        int last = points.size()*3 -1;
        mesh.getFaces().addAll(last,0, last-1,0, last-2,0);
    }
    private void buildRibbon() {
        //add each point. For each point add another point shifted on Z axis by width
        //This extra point allows us to build triangles later
        for(Point3D point: points) {
            mesh.getPoints().addAll((float)point.getX(),(float)point.getY(),(float)point.getZ());
            mesh.getPoints().addAll((float)point.getX(),(float)point.getY(),(float)point.getZ()+width);
        }
        //add dummy Texture Coordinate
        mesh.getTexCoords().addAll(0,0); 
        //Now generate trianglestrips for each line segment
        for(int i=2;i<points.size()*2;i+=2) {  //add each segment
            //Vertices wound counter-clockwise which is the default front face of any Triange
            //These triangles live on the frontside of the line facing the camera
            mesh.getFaces().addAll(i,0,i-2,0,i+1,0); //add primary face
            mesh.getFaces().addAll(i+1,0,i-2,0,i-1,0); //add secondary Width face
            //Add the same faces but wind them clockwise so that the color looks correct when camera is rotated
            //These triangles live on the backside of the line facing away from initial the camera
            mesh.getFaces().addAll(i+1,0,i-2,0,i,0); //add primary face
            mesh.getFaces().addAll(i-1,0,i-2,0,i+1,0); //add secondary Width face
        }        
    }   
}
