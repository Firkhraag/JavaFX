package org.spbu.histology.space.editor;

import org.spbu.histology.model.CrossSectionPlane;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.Slider;
import javafx.scene.layout.VBox;

public class CrossSectionController implements Initializable {

    @FXML
    private VBox vBox;

    @FXML
    private Label xRotationLabel;

    @FXML
    private Label yRotationLabel;

    @FXML
    private Label xPositionLabel;

    @FXML
    private Label yPositionLabel;

    @FXML
    private Label zPositionLabel;

    @FXML
    private TextField xRotation;

    @FXML
    private TextField yRotation;

    @FXML
    private TextField xPosition;

    @FXML
    private TextField yPosition;

    @FXML
    private TextField zPosition;

    @FXML
    private Slider xRotSlider;

    @FXML
    private Slider yRotSlider;

    @FXML
    private Slider xPosSlider;

    @FXML
    private Slider yPosSlider;

    @FXML
    private Slider zPosSlider;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private Label opaquenessLabel;

    @FXML
    private TextField opaqueness;

    @FXML
    private Slider opaquenessSlider;

    private boolean change = true;

    private final double crossSectPosLim = 900;

    ChangeListener xRotationChangeListener = (v, oldValue, newValue) -> {
        double newVal = (double) newValue;
        if (change) {
            xRotation.setText(newValue + "");
        }
    };
    ChangeListener yRotationChangeListener = (v, oldValue, newValue) -> {
        double newVal = (double) newValue;
        if (change) {
            if (change) {
                yRotation.setText(newValue + "");
            }
        }
    };

    ChangeListener xPositionChangeListener = (v, oldValue, newValue) -> {
        double newVal = (double) newValue;
        if (change) {
            if (change) {
                xPosition.setText(newValue + "");
            }
        }
    };
    ChangeListener yPositionChangeListener = (v, oldValue, newValue) -> {
        double newVal = (double) newValue;
        if (change) {
            if (change) {
                yPosition.setText(newValue + "");
            }
        }
    };
    ChangeListener zPositionChangeListener = (v, oldValue, newValue) -> {
        double newVal = (double) newValue;
        if (change) {
            if (change) {
                zPosition.setText(newValue + "");
            }
        }
    };

    ChangeListener opaquenessChangeListener = (v, oldValue, newValue) -> {
        if (change) {
            opaqueness.setText(newValue + "");
        }
    };

    ChangeListener<String> xRotationTextListener = (v, oldValue, newValue) -> {
        try {
            double ang = Double.parseDouble(newValue);
            if ((ang >= 0) && (ang <= 90)) {
                change = false;
                xRotSlider.setValue(ang);
                change = true;
                CrossSectionPlane.setXRotate(newValue);
            }
        } catch (Exception ex) {

        }
    };
    ChangeListener<String> yRotationTextListener = (v, oldValue, newValue) -> {
        try {
            double ang = Double.parseDouble(newValue);
            if ((ang > 360) || (ang < -360)) {
                ang %= 360;
            }
            change = false;
            yRotSlider.setValue(ang);
            change = true;
            CrossSectionPlane.setYRotate(newValue);
        } catch (Exception ex) {

        }
    };

    ChangeListener<String> xPositionTextListener = (v, oldValue, newValue) -> {
        try {
            double pos = Double.parseDouble(newValue);
            if ((pos <= crossSectPosLim) && (pos >= -crossSectPosLim)) {
                change = false;
                xPosSlider.setValue(pos);
                change = true;
                CrossSectionPlane.setXCoordinate(newValue);
            }
        } catch (Exception ex) {

        }
    };
    ChangeListener<String> yPositionTextListener = (v, oldValue, newValue) -> {
        try {
            double pos = Double.parseDouble(yPosition.getText());
            if ((pos <= crossSectPosLim) && (pos >= -crossSectPosLim)) {
                change = false;
                yPosSlider.setValue(pos);
                change = true;
                CrossSectionPlane.setYCoordinate(newValue);
            }
        } catch (Exception ex) {

        }
    };
    ChangeListener<String> zPositionTextListener = (v, oldValue, newValue) -> {
        try {
            double pos = Double.parseDouble(zPosition.getText());
            if ((pos <= crossSectPosLim) && (pos >= -crossSectPosLim)) {
                change = false;
                zPosSlider.setValue(pos);
                change = true;
                CrossSectionPlane.setZCoordinate(newValue);
            }
        } catch (Exception ex) {

        }
    };

    ChangeListener<String> opaquenessTextListener = (v, oldValue, newValue) -> {
        try {
            double opq = Double.parseDouble(opaqueness.getText());
            if ((opq <= 1) && (opq >= 0)) {
                change = false;
                opaquenessSlider.setValue(opq);
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
        opaquenessSlider.valueProperty().addListener(opaquenessChangeListener);
        opaqueness.textProperty().addListener(opaquenessTextListener);

        scrollPane.setStyle("-fx-background-color:transparent;");
        vBox.setPadding(new Insets(10, 10, 10, 10));
        xRotationLabel.setPadding(new Insets(0, 0, 0, 0));
        yRotationLabel.setPadding(new Insets(10, 0, 0, 0));
        xPositionLabel.setPadding(new Insets(10, 0, 0, 0));
        yPositionLabel.setPadding(new Insets(10, 0, 0, 0));
        zPositionLabel.setPadding(new Insets(10, 0, 0, 0));
        opaquenessLabel.setPadding(new Insets(10, 0, 0, 0));
        setBindings();
    }

    private void setBindings() {
        xRotation.textProperty().bindBidirectional(CrossSectionPlane.xRotateProperty());
        yRotation.textProperty().bindBidirectional(CrossSectionPlane.yRotateProperty());
        xPosition.textProperty().bindBidirectional(CrossSectionPlane.xCoordinateProperty());
        yPosition.textProperty().bindBidirectional(CrossSectionPlane.yCoordinateProperty());
        zPosition.textProperty().bindBidirectional(CrossSectionPlane.zCoordinateProperty());
        opaqueness.textProperty().bindBidirectional(CrossSectionPlane.opaquenessProperty());
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
        opaquenessSlider.valueProperty().removeListener(opaquenessChangeListener);
        opaqueness.textProperty().removeListener(opaquenessTextListener);
        opaqueness.textProperty().unbind();
    }

}
