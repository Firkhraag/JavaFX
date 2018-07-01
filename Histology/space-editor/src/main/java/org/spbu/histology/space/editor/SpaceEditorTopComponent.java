package org.spbu.histology.space.editor;

import org.spbu.histology.toolbar.ChosenTool;
import java.awt.BorderLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.IOException;
import java.net.URL;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.embed.swing.JFXPanel;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.util.Exceptions;
import org.openide.windows.TopComponent;
import org.openide.util.NbBundle.Messages;

/**
 * Top component which displays something.
 */
@ConvertAsProperties(
        dtd = "-//org.spbu.histology.space.editor//SpaceEditor//EN",
        autostore = false
)
@TopComponent.Description(
        preferredID = "SpaceEditorTopComponent",
        //iconBase="SET/PATH/TO/ICON/HERE", 
        persistenceType = TopComponent.PERSISTENCE_ALWAYS
)
@TopComponent.Registration(mode = "explorer", openAtStartup = true)
@ActionID(category = "Window", id = "org.spbu.histology.space.editor.SpaceEditorTopComponent")
@ActionReference(path = "Menu/Window" /*, position = 333 */)
@TopComponent.OpenActionRegistration(
        displayName = "#CTL_ToolsAction",
        preferredID = "SpaceEditorTopComponent"
)
@Messages({
    "CTL_ToolsAction=SpaceEditor",
    "CTL_ToolsTopComponent=SpaceEditor Window",
    "HINT_ToolsTopComponent=This is a SpaceEditor window"
})
public final class SpaceEditorTopComponent extends TopComponent {

    private JFXPanel fxPanel;
    private HomeController homeController;
    private CameraViewController cameraViewController;
    private CrossSectionController crossSectionController;
    private GroupPositionController groupPositionController;

    public SpaceEditorTopComponent() {
        initComponents();
        setName(Bundle.CTL_ToolsTopComponent());
        setToolTipText(Bundle.HINT_ToolsTopComponent());

        setLayout(new BorderLayout());
        init();
    }

    private void init() {
        fxPanel = new JFXPanel();
        add(fxPanel, BorderLayout.CENTER);
        Platform.setImplicitExit(false);
        Platform.runLater(() -> {
            createScene(0);
            fxPanel.addComponentListener(new ComponentAdapter() {
                public void componentResized(ComponentEvent e) {
                    switch (ChosenTool.getToolNumber()) {
                        case 0:
                            homeController.setTreeViewSize(getWidth(), getHeight());
                            break;
                        case 1:
                            cameraViewController.setScrollPanel(getWidth(), getHeight());
                            break;
                        case 2:
                            crossSectionController.setScrollPanel(getWidth(), getHeight());
                            break;
                        case 3:
                            groupPositionController.setScrollPanel(getWidth(), getHeight());
                            break;
                        default:
                            homeController.setTreeViewSize(getWidth(), getHeight());
                            break;
                    }
                }
            });
        });
    }

    ChangeListener<Integer> cl = (v, oldValue, newValue) -> {
        Platform.runLater(() -> {
            createScene(newValue);
        });
    };

    private void addToolBarListener(ChangeListener cl) {
        ChosenTool.toolProperty().addListener(cl);
    }

    private void removeToolBarListener(ChangeListener cl) {
        ChosenTool.toolProperty().removeListener(cl);
    }

    private void createScene(Integer toolNum) {
        if (!(cameraViewController == null)) {
            cameraViewController.removeListeners();
            cameraViewController = null;
        }
        if (!(crossSectionController == null)) {
            crossSectionController.removeListeners();
            crossSectionController = null;
        }
        if (!(homeController == null)) {
            homeController.removeListeners();
            homeController = null;
        }
        try {
            URL location;
            switch (toolNum) {
                case 0:
                    location = getClass().getResource("Home.fxml");
                    break;
                case 1:
                    location = getClass().getResource("CameraView.fxml");
                    break;
                case 2:
                    location = getClass().getResource("CrossSection.fxml");
                    break;
                case 3:
                    location = getClass().getResource("GroupPosition.fxml");
                    break;
                default:
                    location = getClass().getResource("Home.fxml");
                    break;
            }
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(location);

            Parent root = (Parent) fxmlLoader.load(location.openStream());
            Scene scene = new Scene(root);
            fxPanel.setScene(scene);
            switch (toolNum) {
                case 0:
                    homeController = (HomeController) fxmlLoader.getController();
                    break;
                case 1:
                    cameraViewController = (CameraViewController) fxmlLoader.getController();
                    break;
                case 2:
                    crossSectionController = (CrossSectionController) fxmlLoader.getController();
                    break;
                case 3:
                    groupPositionController = (GroupPositionController) fxmlLoader.getController();
                    break;
                default:
                    homeController = (HomeController) fxmlLoader.getController();
                    break;
            }
            switch (toolNum) {
                case 0:
                    homeController.setTreeViewSize(getWidth(), getHeight());
                    break;
                case 1:
                    cameraViewController.setScrollPanel(getWidth(), getHeight());
                    break;
                case 2:
                    crossSectionController.setScrollPanel(getWidth(), getHeight());
                    break;
                case 3:
                    groupPositionController.setScrollPanel(getWidth(), getHeight());
                    break;
                default:
                    homeController.setTreeViewSize(getWidth(), getHeight());
                    break;
            }
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
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
        addToolBarListener(cl);
        // TODO add custom code on component opening
    }

    @Override
    public void componentClosed() {
        removeToolBarListener(cl);
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
