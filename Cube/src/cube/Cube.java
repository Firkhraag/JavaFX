package cube;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
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
import javafx.util.Duration;

public class Cube extends Application {
    
    private Scene scene;
    private Box myBox;
    private Group boxGroup;
    private Group root = new Group();
    private PerspectiveCamera camera;
    private Rotate rotateX = new Rotate(0, Rotate.X_AXIS);
    private Rotate rotateY = new Rotate(0, Rotate.Y_AXIS);
    private Rotate rotateZ = new Rotate(0, Rotate.Z_AXIS);
    private double mousePosX, mousePosY = 0;
    final Xform cameraXform = new Xform();
    final Xform cameraXform2 = new Xform();
    final Xform cameraXform3 = new Xform();
    
    @Override
    public void start(Stage primaryStage) {
        final PhongMaterial blueMaterial = new PhongMaterial();
        blueMaterial.setDiffuseColor(Color.DARKBLUE);
        blueMaterial.setSpecularColor(Color.BLUE);
        
        camera = new PerspectiveCamera(false);
        camera.setTranslateX(-390.0);
        camera.setTranslateY(-280.0);
        camera.setTranslateZ(0.0);
        
        myBox = new Box(200, 200, 200);
        myBox.setMaterial(blueMaterial);
        myBox.setTranslateX(0.0);
        myBox.setTranslateY(0.0);
        myBox.setTranslateZ(0.0);
        myBox.getTransforms().addAll(rotateZ, rotateY, rotateX);
        
        boxGroup = new Group(myBox);
        
        cameraXform.getChildren().add(cameraXform2);
        cameraXform2.getChildren().add(cameraXform3);
        cameraXform3.getChildren().add(camera);
        cameraXform3.setRotateZ(180.0);
        root.getChildren().addAll(cameraXform, boxGroup);
        scene = new Scene(root, 800, 600, true);
        scene.setCamera(camera);
        
        handleMouseEvents();
        
        Duration rotateDuration = Duration.seconds(5);
        Timeline timeline = new Timeline(
            new KeyFrame(Duration.ZERO, new KeyValue(myBox.rotateProperty(), 0)),
            new KeyFrame(rotateDuration, new KeyValue(myBox.rotateProperty(), 360))
        );
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
        
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
            double modifier = 1.0;
            double modifierFactor = 0.1;
            double dx = (mousePosX - e.getSceneX()) ;
            double dy = (mousePosY - e.getSceneY());
            if (e.isPrimaryButtonDown()) {
                    cameraXform.ry.setAngle(cameraXform.ry.getAngle() - dx*modifierFactor*modifier*2.0);  // +
                    cameraXform.rx.setAngle(cameraXform.rx.getAngle() + dy*modifierFactor*modifier*2.0);  // -
            }
            if (e.isSecondaryButtonDown()) {
                cameraXform2.t.setX(cameraXform2.t.getX() + dx*modifierFactor*modifier);  // -
                cameraXform2.t.setY(cameraXform2.t.getY() + dy*modifierFactor*modifier);
            }
            mousePosX = e.getSceneX();
            mousePosY = e.getSceneY();
        });
        
        scene.setOnScroll((ScrollEvent e) -> {
                double zoom = 1.05;
                double dy = e.getDeltaY();
                if (dy < 0)
                  zoom = 2.0 - zoom;
                boxGroup.setScaleX(boxGroup.getScaleX() * zoom);
                boxGroup.setScaleY(boxGroup.getScaleY() * zoom);
                boxGroup.setScaleZ(boxGroup.getScaleZ() * zoom);
            });
    }
    
}
