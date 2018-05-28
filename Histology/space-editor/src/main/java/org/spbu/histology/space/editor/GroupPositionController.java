package org.spbu.histology.space.editor;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import org.spbu.histology.model.GroupPosition;

public class GroupPositionController implements Initializable {

    @FXML
    private VBox vBox;

    @FXML
    private Label xRotationLabel;

    @FXML
    private Label yRotationLabel;

    @FXML
    private TextField xRotation;

    @FXML
    private TextField yRotation;

    @FXML
    private Slider xRotSlider;

    @FXML
    private Slider yRotSlider;

    @FXML
    private Label xPositionLabel;

    @FXML
    private Label yPositionLabel;

    @FXML
    private Label zPositionLabel;

    @FXML
    private TextField xPosition;

    @FXML
    private TextField yPosition;

    @FXML
    private TextField zPosition;

    @FXML
    private Slider xPosSlider;

    @FXML
    private Slider yPosSlider;

    @FXML
    private Slider zPosSlider;

    @FXML
    private ScrollPane scrollPane;

    private boolean change = true;

    private final double camPosLim = 2000;

    ChangeListener xRotationChangeListener = (v, oldValue, newValue) -> {
        if (change) {
            xRotation.setText(newValue + "");
        }
    };
    ChangeListener yRotationChangeListener = (v, oldValue, newValue) -> {
        if (change) {
            yRotation.setText(newValue + "");
        }
    };

    ChangeListener xPositionChangeListener = (v, oldValue, newValue) -> {
        if (change) {
            xPosition.setText(newValue + "");
        }
    };
    ChangeListener yPositionChangeListener = (v, oldValue, newValue) -> {
        if (change) {
            yPosition.setText(newValue + "");
        }
    };
    ChangeListener zPositionChangeListener = (v, oldValue, newValue) -> {
        if (change) {
            zPosition.setText(newValue + "");
        }
    };

    ChangeListener<String> xRotationTextListener = (v, oldValue, newValue) -> {
        try {
            double ang = Double.parseDouble(xRotation.getText());
            if ((ang > 360) || (ang < -360)) {
                ang %= 360;
            }
            change = false;
            xRotSlider.setValue(ang);
            change = true;
        } catch (Exception ex) {

        }
    };
    ChangeListener<String> yRotationTextListener = (v, oldValue, newValue) -> {
        try {
            double ang = Double.parseDouble(yRotation.getText());
            if ((ang > 360) || (ang < -360)) {
                ang %= 360;
            }
            change = false;
            yRotSlider.setValue(ang);
            change = true;
        } catch (Exception ex) {

        }
    };

    ChangeListener<String> xPositionTextListener = (v, oldValue, newValue) -> {
        try {
            double pos = Double.parseDouble(xPosition.getText());
            if ((pos <= camPosLim) && (pos >= -camPosLim)) {
                change = false;
                xPosSlider.setValue(pos);
                change = true;
            }
        } catch (Exception ex) {

        }
    };
    ChangeListener<String> yPositionTextListener = (v, oldValue, newValue) -> {
        try {
            double pos = Double.parseDouble(yPosition.getText());
            if ((pos <= camPosLim) && (pos >= -camPosLim)) {
                change = false;
                yPosSlider.setValue(pos);
                change = true;
            }
        } catch (Exception ex) {

        }
    };
    ChangeListener<String> zPositionTextListener = (v, oldValue, newValue) -> {
        try {
            double pos = Double.parseDouble(zPosition.getText());
            if ((pos <= camPosLim) && (pos >= -camPosLim)) {
                change = false;
                zPosSlider.setValue(pos);
                change = true;
            }
        } catch (Exception ex) {

        }
    };

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        xRotSlider.valueProperty().addListener(xRotationChangeListener);
        xRotation.textProperty().addListener(xRotationTextListener);
        yRotSlider.valueProperty().addListener(yRotationChangeListener);
        yRotation.textProperty().addListener(yRotationTextListener);
        xPosSlider.valueProperty().addListener(xPositionChangeListener);
        xPosition.textProperty().addListener(xPositionTextListener);
        yPosSlider.valueProperty().addListener(yPositionChangeListener);
        yPosition.textProperty().addListener(yPositionTextListener);
        zPosSlider.valueProperty().addListener(zPositionChangeListener);
        zPosition.textProperty().addListener(zPositionTextListener);

        scrollPane.setStyle("-fx-background-color:transparent;");
        vBox.setPadding(new Insets(10, 10, 10, 10));
        xRotationLabel.setPadding(new Insets(0, 0, 0, 0));
        yRotationLabel.setPadding(new Insets(10, 0, 0, 0));
        xPositionLabel.setPadding(new Insets(10, 0, 0, 0));
        yPositionLabel.setPadding(new Insets(10, 0, 0, 0));
        zPositionLabel.setPadding(new Insets(10, 0, 0, 0));
        setBindings();
    }

    private void setBindings() {
        xRotation.textProperty().bindBidirectional(GroupPosition.xRotateProperty());
        yRotation.textProperty().bindBidirectional(GroupPosition.yRotateProperty());
        xPosition.textProperty().bindBidirectional(GroupPosition.xCoordinateProperty());
        yPosition.textProperty().bindBidirectional(GroupPosition.yCoordinateProperty());
        zPosition.textProperty().bindBidirectional(GroupPosition.zCoordinateProperty());
    }

    public void setScrollPanel(int width, int height) {
        scrollPane.setPrefSize(width, height);
    }

    public void removeListeners() {
        xRotSlider.valueProperty().removeListener(xRotationChangeListener);
        xRotation.textProperty().removeListener(xRotationTextListener);
        yRotSlider.valueProperty().removeListener(yRotationChangeListener);
        yRotation.textProperty().removeListener(yRotationTextListener);
        xPosSlider.valueProperty().removeListener(xPositionChangeListener);
        xPosition.textProperty().removeListener(xPositionTextListener);
        yPosSlider.valueProperty().removeListener(yPositionChangeListener);
        yPosition.textProperty().removeListener(yPositionTextListener);
        zPosSlider.valueProperty().removeListener(zPositionChangeListener);
        zPosition.textProperty().removeListener(zPositionTextListener);
        xRotation.textProperty().unbind();
        yRotation.textProperty().unbind();
        xPosition.textProperty().unbind();
        yPosition.textProperty().unbind();
        zPosition.textProperty().unbind();
    }

}
