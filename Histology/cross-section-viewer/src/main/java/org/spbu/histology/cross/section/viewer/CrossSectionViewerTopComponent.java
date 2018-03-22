package org.spbu.histology.cross.section.viewer;

import org.spbu.histology.model.Node;
import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.Collections;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.windows.TopComponent;
import org.openide.util.NbBundle.Messages;

/**
 * Top component which displays something.
 */
@ConvertAsProperties(
        dtd = "-//org.spbu.histology.cross.section.viewer//CrossSectionViewer//EN",
        autostore = false
)
@TopComponent.Description(
        preferredID = "CrossSectionViewerTopComponent",
        //iconBase="SET/PATH/TO/ICON/HERE", 
        persistenceType = TopComponent.PERSISTENCE_ALWAYS
)
@TopComponent.Registration(mode = "editor", openAtStartup = true)
@ActionID(category = "Window", id = "org.spbu.histology.cross.section.viewer.CrossSectionViewerTopComponent")
@ActionReference(path = "Menu/Window" /*, position = 333 */)
@TopComponent.OpenActionRegistration(
        displayName = "#CTL_CrossSectionViewerAction",
        preferredID = "CrossSectionViewerTopComponent"
)
@Messages({
    "CTL_CrossSectionViewerAction=CrossSectionViewer",
    "CTL_CrossSectionViewerTopComponent=CrossSectionViewer Window",
    "HINT_CrossSectionViewerTopComponent=This is a CrossSectionViewer window"
})
public final class CrossSectionViewerTopComponent extends TopComponent {
    
    private static JFXPanel fxPanel;
    private static Group root = new Group();
    private static BooleanProperty updateViewer = new SimpleBooleanProperty(false);
    private static final double paneSize = 4000;
    
    public static BooleanProperty updateViewerProperty() {
        return updateViewer;
    }
    public static void setUpdateViewer(boolean v) {
        updateViewer.set(v);
    }
    public static boolean getUpdateViewer() {
        return updateViewer.get();
    }

    public CrossSectionViewerTopComponent() {
        initComponents();
        setName(Bundle.CTL_CrossSectionViewerTopComponent());
        setToolTipText(Bundle.HINT_CrossSectionViewerTopComponent());

        setLayout(new BorderLayout());
        init();
    }
    
    private void init() {
        fxPanel = new JFXPanel();
        add(fxPanel, BorderLayout.CENTER);
        Platform.setImplicitExit(false);
        Platform.runLater(() -> {
            createScene();
        });
    }

    private void createScene() {
        Pane drawingPane = new Pane();
        drawingPane.getChildren().add(root);
        drawingPane.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        ScrollPane scrollPane = new ScrollPane(drawingPane);
        scrollPane.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setStyle("-fx-focus-color: transparent;");
        drawingPane.setMinHeight(paneSize);
        drawingPane.setMinWidth(paneSize);
        scrollPane.setHvalue(0.5);
        scrollPane.setVvalue(0.5);
        Scene scene = new Scene(scrollPane);
        fxPanel.setScene(scene);
        setUpdateViewer(true);
    }
    
    public static void clear() {
        root.getChildren().clear();
    }
    
    private static double findAngle(double x, double y) {
        double angle;
        if (x > 0) {
            if (y >= 0)
                angle = Math.atan(y/x);
            else
                angle = Math.atan(y/x) + 2* Math.PI;
        } else if (x == 0) {
            if (y >= 0)
                angle = 0;
            else 
                angle = 3* Math.PI/2;
        } else
            angle = Math.atan(y/x) + Math.PI;
        return angle;
    }
    
    public static void show(ArrayList<Node> intersectionNodes, Color color) {
        if (fxPanel == null)
            return;  
        if (intersectionNodes.size() == 4) {
            double avgX = (intersectionNodes.get(0).x + intersectionNodes.get(1).x +
                    intersectionNodes.get(2).x + intersectionNodes.get(3).x) / 4;
            
            double avgZ = (intersectionNodes.get(0).z + intersectionNodes.get(1).z +
                    intersectionNodes.get(2).z + intersectionNodes.get(3).z) / 4;
            
            Collections.sort(intersectionNodes, (Node o1, Node o2) -> {
                double temp1 = Math.atan2(o1.z - avgZ, o1.x - avgX);
                double temp2 = Math.atan2(o2.z - avgZ, o2.x - avgX);
                if(temp1 == temp2)
                    return 0;
                return temp1 < temp2 ? -1 : 1;
            }); 
            
            Polygon polygon = new Polygon();
            polygon.getPoints().addAll(new Double[]{
                intersectionNodes.get(0).x, intersectionNodes.get(0).z,
                intersectionNodes.get(1).x, intersectionNodes.get(1).z,
                intersectionNodes.get(2).x, intersectionNodes.get(2).z,
                intersectionNodes.get(3).x, intersectionNodes.get(3).z
            });
            
            polygon.setFill(color);
            polygon.setTranslateX(paneSize / 2);
            polygon.setTranslateY(paneSize / 2);
            root.getChildren().add(polygon);
            return;
        }
        Polygon polygon = new Polygon();
        polygon.getPoints().addAll(new Double[]{
            intersectionNodes.get(0).x, intersectionNodes.get(0).z,
            intersectionNodes.get(1).x, intersectionNodes.get(1).z,
            intersectionNodes.get(2).x, intersectionNodes.get(2).z
        });
        polygon.setFill(color);
        polygon.setTranslateX(paneSize / 2);
        polygon.setTranslateY(paneSize / 2);
        root.getChildren().add(polygon);
    }
    

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
    @Override
    public void componentOpened() {
        // TODO add custom code on component opening
    }

    @Override
    public void componentClosed() {
        // TODO add custom code on component closing
    }

    void writeProperties(java.util.Properties p) {
        // better to version settings since initial version as advocated at
        // http://wiki.apidesign.org/wiki/PropertyFiles
        p.setProperty("version", "1.0");
        // TODO store your settings
    }

    void readProperties(java.util.Properties p) {
        String version = p.getProperty("version");
        // TODO read your settings according to their version
    }
}
