package org.spbu.histology.shape.information;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import org.spbu.histology.model.Shape;

public class MainController implements Initializable {

    @FXML
    private PointTabController PointTabController;
    
    @FXML
    private FacetTabController FacetTabController;
    
    @FXML
    private HoleTabController HoleTabController;
    
    @FXML
    private GeneralTabController GeneralTabController;
    
    public void setShape(Shape s) {
        BooleanProperty change = new SimpleBooleanProperty(false);
        PointTabController.setShape(s, change);
        FacetTabController.setShape(s, change);
        HoleTabController.setShape(s, change);
        GeneralTabController.setShape(s, change);
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }
}
