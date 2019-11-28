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

    ChangeListener<String> xPositionTextListener = (v, oldValue, newValue) -> {
        try {
            double pos = Double.parseDouble(xPosition.getText());
            if (Math.abs(pos) < 0.00001) {
                xPosition.setText(0 + "");
            } else {
                if ((pos <= camPosLim) && (pos >= -camPosLim)) {
                    change = false;
                    xPosSlider.setValue(pos);
                    change = true;
                }
            }
        } catch (Exception ex) {

        }
    };
    ChangeListener<String> yPositionTextListener = (v, oldValue, newValue) -> {
        try {
            double pos = Double.parseDouble(yPosition.getText());
            if (Math.abs(pos) < 0.00001) {
                yPosition.setText(0 + "");
            } else {
                if ((pos <= camPosLim) && (pos >= -camPosLim)) {
                    change = false;
                    yPosSlider.setValue(pos);
                    change = true;
                }
            }
        } catch (Exception ex) {

        }
    };
    ChangeListener<String> zPositionTextListener = (v, oldValue, newValue) -> {
        try {
            double pos = Double.parseDouble(zPosition.getText());
            if (Math.abs(pos) < 0.00001) {
                zPosition.setText(0 + "");
            } else {
                if ((pos <= camPosLim) && (pos >= -camPosLim)) {
                    change = false;
                    zPosSlider.setValue(pos);
                    change = true;
                }
            }
        } catch (Exception ex) {

        }
    };

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        xPosSlider.valueProperty().addListener(xPositionChangeListener);
        xPosition.textProperty().addListener(xPositionTextListener);
        yPosSlider.valueProperty().addListener(yPositionChangeListener);
        yPosition.textProperty().addListener(yPositionTextListener);
        zPosSlider.valueProperty().addListener(zPositionChangeListener);
        zPosition.textProperty().addListener(zPositionTextListener);

        scrollPane.setStyle("-fx-background-color:transparent;");
        vBox.setPadding(new Insets(10, 10, 10, 10));
        xPositionLabel.setPadding(new Insets(0, 0, 0, 0));
        yPositionLabel.setPadding(new Insets(10, 0, 0, 0));
        zPositionLabel.setPadding(new Insets(10, 0, 0, 0));
        setBindings();
    }

    private void setBindings() {
        xPosition.textProperty().bindBidirectional(GroupPosition.xCoordinateProperty());
        yPosition.textProperty().bindBidirectional(GroupPosition.yCoordinateProperty());
        zPosition.textProperty().bindBidirectional(GroupPosition.zCoordinateProperty());
    }

    public void setScrollPanel(int width, int height) {
        scrollPane.setPrefSize(width, height);
    }

    public void removeListeners() {
        xPosSlider.valueProperty().removeListener(xPositionChangeListener);
        xPosition.textProperty().removeListener(xPositionTextListener);
        yPosSlider.valueProperty().removeListener(yPositionChangeListener);
        yPosition.textProperty().removeListener(yPositionTextListener);
        zPosSlider.valueProperty().removeListener(zPositionChangeListener);
        zPosition.textProperty().removeListener(zPositionTextListener);
        xPosition.textProperty().unbind();
        yPosition.textProperty().unbind();
        zPosition.textProperty().unbind();
    }

}
