package race.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.PathTransition;
import javafx.beans.binding.When;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.shape.Path;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class RaceTrackController implements Initializable {
    
    private final double minRate = .3;
    private final double maxRate = 7.0;
    private final double rateDelta = .3;
    
    private final IntegerProperty lapCounterProperty = new SimpleIntegerProperty(0);
    
    @FXML
    private Rectangle rectangle;
    
    @FXML
    private Path path;
    
    @FXML
    private Text text;
    
    @FXML
    private Button startPauseButton;
    
    @FXML
    private Button restartButton;
    
    @FXML
    private Button slowerButton;
    
    @FXML
    private Button fasterButton;
    
    private PathTransition pathTransition;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        pathTransition = new PathTransition(Duration.seconds(6),
                path, rectangle);
        pathTransition.setOrientation(
                PathTransition.OrientationType.ORTHOGONAL_TO_TANGENT);
        pathTransition.setCycleCount(Animation.INDEFINITE);
        pathTransition.setInterpolator(Interpolator.LINEAR);
        
        pathTransition.currentTimeProperty().addListener(
                (ObservableValue<? extends Duration> ov, Duration oldValue,
                Duration newValue) -> {
                    if (oldValue.greaterThan(newValue))
                        lapCounterProperty.set(lapCounterProperty.get() + 1);
                });
        
        text.textProperty().bind(lapCounterProperty.asString("Lap Counter: %s"));
        
        restartButton.disableProperty().bind(pathTransition.statusProperty()
                .isEqualTo(Animation.Status.STOPPED));
             
        /*startPauseButton.textProperty().bind(new When(pathTransition
                .statusProperty().isEqualTo(Animation.Status.RUNNING))
                .then("Pause").otherwise("Start"));*/
        
        fasterButton.disableProperty().bind(pathTransition.statusProperty()
                .isNotEqualTo(Animation.Status.RUNNING));
        fasterButton.setText(" >> ");
        slowerButton.disableProperty().bind(pathTransition.statusProperty()
                .isNotEqualTo(Animation.Status.RUNNING));
        slowerButton.setText(" << ");
    }
    
    @FXML
    private void startPauseAction(ActionEvent event) {
        if (pathTransition.getStatus() == Animation.Status.RUNNING)
            pathTransition.pause();
        else
            pathTransition.play();
        
        if (pathTransition.getStatus() == Animation.Status.RUNNING)
            startPauseButton.setText("Stop");
        else
            startPauseButton.setText("Continue");
    }
    
    @FXML
    private void slowerAction(ActionEvent event) {
        double currentRate = pathTransition.getRate();
        if (currentRate <= minRate)
            return;
        pathTransition.setRate(currentRate - rateDelta);
    }
    
    @FXML
    private void fasterAction(ActionEvent event) {
        double currentRate = pathTransition.getRate();
        if (currentRate >= maxRate)
            return;
        pathTransition.setRate(currentRate + rateDelta);
    }
    
    @FXML
    private void restartAction(ActionEvent event) {
        startPauseButton.setText("Start");
        pathTransition.setRate(1.0);
        lapCounterProperty.set(-1);
        pathTransition.stop();
        rectangle.setTranslateX(0);
        rectangle.setTranslateY(0);
        rectangle.rotateProperty().set(0);
    }
}
