package org.spbu.histology.cross.section.viewer;

import java.awt.BorderLayout;
import java.util.ArrayList;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.embed.swing.JFXPanel;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
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
    public static BooleanProperty initialized = new SimpleBooleanProperty(false);
    private static final double paneSize = 4000;
    private double scale = 1.0;

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
        scrollPane.addEventFilter(ScrollEvent.ANY, new EventHandler<ScrollEvent>() {
            @Override
            public void handle(ScrollEvent event) {
                if (event.getDeltaY() > 0) {
                    if (scale < 2.45) {
                        scale += 0.05;
                        root.setScaleX(scale);
                        root.setScaleY(scale);
                    }
                } else {
                    if (scale > 0.05) {
                        scale -= 0.05;
                        root.setScaleX(scale);
                        root.setScaleY(scale);
                    }
                }
                event.consume();
        }});
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
        initialized.set(true);
    }
    
    public static void clear() {
        root.getChildren().clear();
    }
    
    public static void clearPolygonArray(ArrayList<Polygon> polygons) {
        polygons.forEach(p -> root.getChildren().remove(p));
    }
    
    public static void show(ArrayList<Polygon> polygons) {
        if (fxPanel == null)
            return;  
        polygons.forEach(polygon -> {
            polygon.setTranslateX(paneSize / 2);
            polygon.setTranslateY(paneSize / 2);
            root.getChildren().add(polygon);
        });
    }
    
    /*public static void showLines(ArrayList<TwoPoints> lineList) {
        if (fxPanel == null)
            return;  
        lineList.forEach(tp -> {
            Line line = new Line();
            line.setStartX(tp.getPoint1().getX() + paneSize / 2);
            line.setStartY(tp.getPoint1().getZ() + paneSize / 2);
            line.setEndX(tp.getPoint2().getX() + paneSize / 2);
            line.setEndY(tp.getPoint2().getZ() + paneSize / 2);
            root.getChildren().add(line);
        });
    }*/

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
