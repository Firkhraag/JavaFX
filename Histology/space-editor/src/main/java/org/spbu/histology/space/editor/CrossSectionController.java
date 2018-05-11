package org.spbu.histology.space.editor;

import org.spbu.histology.model.Node;
import org.spbu.histology.model.CrossSection;
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

public class CrossSectionController implements Initializable  {
    
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
    
    private final double EPS = 0.000001;
    
    Node p1;
    Node p2;
    Node p3;
    
    private final double crossSectPosLim = 900;
    
    /*double oldValXRot = 0;
    double oldValYRot = 0;
    double oldValXPos = 0;
    double oldValYPos = 0;
    double oldValZPos = 0;*/
    
    ChangeListener xRotationChangeListener = (v, oldValue, newValue) -> {
        double newVal = (double)newValue;
        if (change) {
            //if (Math.abs(newVal - oldValXRot) > 5) {
                //oldValXRot = newVal;
                xRotation.setText(newValue + "");
            //}
        }
    };
    ChangeListener yRotationChangeListener = (v, oldValue, newValue) -> {
        double newVal = (double)newValue;
        if (change) {
            if (change) {
                //if (Math.abs(newVal - oldValYRot) > 5) {
                   // oldValYRot = newVal;
                    yRotation.setText(newValue + "");
                //}
            }
        }
    };
    
    ChangeListener xPositionChangeListener = (v, oldValue, newValue) -> {
        double newVal = (double)newValue;
        if (change) {
            if (change) {
                //if (Math.abs(newVal - oldValXPos) > 5) {
                   // oldValXPos = newVal;
                    xPosition.setText(newValue + "");
                //}
            }
        }
    };
    ChangeListener yPositionChangeListener = (v, oldValue, newValue) -> {
        double newVal = (double)newValue;
        if (change) {
            if (change) {
                //if (Math.abs(newVal - oldValYPos) > 5) {
                   // oldValYPos = newVal;
                    yPosition.setText(newValue + "");
                //}
            }
        }
    };
    ChangeListener zPositionChangeListener = (v, oldValue, newValue) -> {
        double newVal = (double)newValue;
        if (change) {
            if (change) {
                //if (Math.abs(newVal - oldValZPos) > 5) {
                    //oldValZPos = newVal;
                    zPosition.setText(newValue + "");
                //}
            }
        }
    };
    
    ChangeListener opaquenessChangeListener = (v, oldValue, newValue) -> {
        if (change)
            opaqueness.setText(newValue + "");
    };
    
    ChangeListener<String> xRotationTextListener = (v, oldValue, newValue) -> {
        try {
            double ang = Double.parseDouble(newValue);
            if ((ang >= 0) && (ang <= 90)) {
                change = false;
                xRotSlider.setValue(ang);
                change = true;
                CrossSection.setXRotate(newValue);
                findCoordinates();
                findPlane(p1, p2, p3);
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
            CrossSection.setYRotate(newValue);
            findCoordinates();
            findPlane(p1, p2, p3);
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
                CrossSection.setXCoordinate(newValue);
                findCoordinates();
                findPlane(p1, p2, p3);
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
                CrossSection.setYCoordinate(newValue);
                findCoordinates();
                findPlane(p1, p2, p3);
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
                CrossSection.setZCoordinate(newValue);
                findCoordinates();
                findPlane(p1, p2, p3);
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
        /*xRotation.textProperty().addListener( (observableValue, oldValue, newValue) -> {
            try {
                double ang = Double.parseDouble(newValue);
                if ((ang >= 0) && (ang <= 90)) {
                    change = false;
                    xRotSlider.setValue(ang);
                    change = true;
                    CrossSection.setXRotate(newValue);
                    findCoordinates();
                    findPlane(p1, p2, p3);
                }
            } catch (Exception ex) {
                
            }
        });*/
        yRotSlider.valueProperty().addListener(yRotationChangeListener);
        yRotation.textProperty().addListener(yRotationTextListener);
        /*yRotation.textProperty().addListener( (observableValue, oldValue, newValue) -> {
            try {
                double ang = Double.parseDouble(newValue);
                if ((ang > 360) || (ang < -360))
                    ang %= 360;
                change = false;
                yRotSlider.setValue(ang);
                change = true;
                CrossSection.setYRotate(newValue);
                findCoordinates();
                findPlane(p1, p2, p3);
            } catch (Exception ex) {
                
            }
        });*/
        xPosSlider.valueProperty().addListener(xPositionChangeListener);
        xPosition.textProperty().addListener(xPositionTextListener);
        /*xPosition.textProperty().addListener( (observableValue, oldValue, newValue) -> {
            try {
                double pos = Double.parseDouble(newValue);
                if ((pos <= crossSectPosLim) && (pos >= -crossSectPosLim)) {
                    change = false;
                    xPosSlider.setValue(pos);
                    change = true;
                    CrossSection.setXCoordinate(newValue);
                    findCoordinates();
                    findPlane(p1, p2, p3);
                }
            } catch (Exception ex) {
                
            }
        });*/
        yPosSlider.valueProperty().addListener(yPositionChangeListener);
        yPosition.textProperty().addListener(yPositionTextListener);
        /*yPosition.textProperty().addListener( (observableValue, oldValue, newValue) -> {
            try {
                double pos = Double.parseDouble(yPosition.getText());
                if ((pos <= crossSectPosLim) && (pos >= -crossSectPosLim)) {
                    change = false;
                    yPosSlider.setValue(pos);
                    change = true;
                    CrossSection.setYCoordinate(newValue);
                    findCoordinates();
                    findPlane(p1, p2, p3);
                }
            } catch (Exception ex) {
                
            }
        });*/
        zPosSlider.valueProperty().addListener(zPositionChangeListener);
        zPosition.textProperty().addListener(zPositionTextListener);
        /*zPosition.textProperty().addListener( (observableValue, oldValue, newValue) -> {
            try {
                double pos = Double.parseDouble(zPosition.getText());
                if ((pos <= crossSectPosLim) && (pos >= -crossSectPosLim)) {
                    change = false;
                    zPosSlider.setValue(pos);
                    change = true;
                    CrossSection.setZCoordinate(newValue);
                    findCoordinates();
                    findPlane(p1, p2, p3);
                }
            } catch (Exception ex) {
                
            }
        });*/
        opaquenessSlider.valueProperty().addListener(opaquenessChangeListener);
        opaqueness.textProperty().addListener(opaquenessTextListener);
        /*opaqueness.textProperty().addListener( (observableValue, oldValue, newValue) -> {
            try {
                double opq = Double.parseDouble(opaqueness.getText());
                if ((opq <= 1) && (opq >= 0)) {
                    change = false;
                    opaquenessSlider.setValue(opq);
                    change = true;
                }
            } catch (Exception ex) {
                
            }
        });*/
        
        scrollPane.setStyle("-fx-background-color:transparent;");
        vBox.setPadding(new Insets(10, 10, 10, 10));
        xRotationLabel.setPadding(new Insets(0, 0, 0, 0));
        yRotationLabel.setPadding(new Insets(10, 0, 0, 0));
        xPositionLabel.setPadding(new Insets(10, 0, 0, 0));
        yPositionLabel.setPadding(new Insets(10, 0, 0, 0));
        zPositionLabel.setPadding(new Insets(10, 0, 0, 0));
        opaquenessLabel.setPadding(new Insets(10, 0, 0, 0));
        setInitialValues();
        opaqueness.textProperty().bindBidirectional(CrossSection.opaquenessProperty());
    }
    
    private void setInitialValues() {
        xRotation.setText(CrossSection.getXRotate());
        yRotation.setText(CrossSection.getYRotate());
        xPosition.setText(CrossSection.getXCoordinate());
        yPosition.setText(CrossSection.getYCoordinate());
        zPosition.setText(CrossSection.getZCoordinate());
    }
                    
    private void findPlane(Node p1, Node p2, Node p3) {
        try {
            double A = ((p2.y - p1.y) * (p3.z - p1.z) - (p3.y - p1.y) * (p2.z - p1.z));
            double B = -((p2.x - p1.x) * (p3.z - p1.z) - (p3.x - p1.x) * (p2.z - p1.z));
            double C = ((p2.x - p1.x) * (p3.y - p1.y) - (p3.x - p1.x) * (p2.y - p1.y));
            double D = -p1.x * A - p1.y * B - p1.z * C;
            if ((Math.abs(A - CrossSection.getA()) > EPS) || (Math.abs(B - CrossSection.getB()) > EPS) ||
                    (Math.abs(C - CrossSection.getC()) > EPS) || (Math.abs(D - CrossSection.getD()) > EPS)) {
                CrossSection.setA(A);
                CrossSection.setB(B);
                CrossSection.setC(C);
                CrossSection.setD(D);
                CrossSection.setChanged(true);
            }
        } catch (Exception ex) {
            
        }
    }
    
    private void findCoordinates() {
        try {
            double temp;
            double ang;
            double xPos = Double.parseDouble(xPosition.getText());
            double yPos = Double.parseDouble(yPosition.getText());
            double zPos = Double.parseDouble(zPosition.getText());
            
            p1 = new Node(0,0,0);
            p2 = new Node(1,0,0);
            p3 = new Node(0,0,1);

            ang = Math.toRadians(Double.parseDouble(xRotation.getText()));
            temp = p1.y;
            p1.y = p1.y * Math.cos(ang) - p1.z * Math.sin(ang);
            p1.z = temp * Math.sin(ang) + p1.z * Math.cos(ang);
            temp = p2.y;
            p2.y = p2.y * Math.cos(ang) - p2.z * Math.sin(ang);
            p2.z = temp * Math.sin(ang) + p2.z * Math.cos(ang);
            temp = p3.y;
            p3.y = p3.y * Math.cos(ang) - p3.z * Math.sin(ang);
            p3.z = temp * Math.sin(ang) + p3.z * Math.cos(ang);
            
            ang = Math.toRadians(Double.parseDouble(yRotation.getText()));
            temp = p1.x;
            p1.x = p1.x * Math.cos(ang) + p1.z * Math.sin(ang);
            p1.z = -temp * Math.sin(ang) + p1.z * Math.cos(ang);
            temp = p2.x;
            p2.x = p2.x * Math.cos(ang) + p2.z * Math.sin(ang);
            p2.z = -temp * Math.sin(ang) + p2.z * Math.cos(ang);
            temp = p3.x;
            p3.x = p3.x * Math.cos(ang) + p3.z * Math.sin(ang);
            p3.z = -temp * Math.sin(ang) + p3.z * Math.cos(ang);
            
            p1.x += xPos;
            p2.x += xPos;
            p3.x += xPos;
            p1.y += yPos;
            p2.y += yPos;
            p3.y += yPos;
            p1.z += zPos;
            p2.z += zPos;
            p3.z += zPos;

        } catch (Exception ex) {
            
        }
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
