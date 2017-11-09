package cube;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;

public class Cube extends Application {
    
    private Scene scene;
    private Box myBox;
    private Group rotationGroup;
    private PerspectiveCamera camera;
    private Rotate rotateX = new Rotate(0, Rotate.X_AXIS);
    private Rotate rotateY = new Rotate(0, Rotate.Y_AXIS);
    private Rotate rotateZ = new Rotate(0, Rotate.Z_AXIS);
    private double mousePosX, mousePosY = 0;
    
    @Override
    public void start(Stage primaryStage) {
        final PhongMaterial blueMaterial = new PhongMaterial();
        blueMaterial.setDiffuseColor(Color.DARKBLUE);
        blueMaterial.setSpecularColor(Color.BLUE);
        
        camera = new PerspectiveCamera();
        
        myBox = new Box(200, 200, 200);
        myBox.setMaterial(blueMaterial);
        myBox.setTranslateX(0);
        myBox.setTranslateY(0);
        myBox.setTranslateZ(0);
        myBox.getTransforms().addAll(rotateZ, rotateY, rotateX);
        
        rotationGroup = new Group(myBox);
        rotationGroup.setTranslateX(400);
        rotationGroup.setTranslateY(280);
        rotationGroup.setTranslateZ(0);
        
        scene = new Scene(rotationGroup, 800, 600, true);
        scene.setCamera(camera);
        
        handleMouseEvents();
        
        primaryStage.setTitle("Cube");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
    
    private void handleMouseEvents() {
        scene.setOnMousePressed((MouseEvent e) -> {
            mousePosX = e.getSceneX();
            mousePosY = e.getSceneY();
        });

        scene.setOnMouseDragged((MouseEvent e) -> {
            double dx = (mousePosX - e.getSceneX()) ;
            double dy = (mousePosY - e.getSceneY());
            if (e.isPrimaryButtonDown()) {
                rotateX.setAngle(rotateX.getAngle() - 
                        dy / (myBox.getHeight() / 2) * 360 * Math.PI / 180);
                rotateY.setAngle(rotateY.getAngle() - 
                        dx / (myBox.getWidth() / 2) * (-360) * Math.PI / 180);
            }
             if (e.isSecondaryButtonDown()) {
                camera.setTranslateX(camera.getTranslateX() + dx);
                camera.setTranslateY(camera.getTranslateY() + dy);
            }
            mousePosX = e.getSceneX();
            mousePosY = e.getSceneY();
        });
        
        scene.setOnScroll((ScrollEvent e) -> {
                double zoom = 1.05;
                double dy = e.getDeltaY();
                if (dy < 0)
                  zoom = 2.0 - zoom;
                rotationGroup.setScaleX(rotationGroup.getScaleX() * zoom);
                rotationGroup.setScaleY(rotationGroup.getScaleY() * zoom);
                rotationGroup.setScaleZ(rotationGroup.getScaleZ() * zoom);
            });
    }
    
}
