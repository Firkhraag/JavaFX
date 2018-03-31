package org.spbu.histology.shape.information;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.property.BooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import org.spbu.histology.model.Shape;

public class FacetTabController implements Initializable {

    @FXML
    private PolygonsInFacetTabController PolygonsInFacetTabController;
    
    @FXML
    private HolesInFacetTabController HolesInFacetTabController;
    
    public void setShape(Shape s, BooleanProperty change) {
        PolygonsInFacetTabController.setShape(s, change);
        HolesInFacetTabController.setShape(s, change);
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }
}
