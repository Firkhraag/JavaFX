/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tools;

import com.toolbar.ChosenTool;
import java.awt.BorderLayout;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.CountDownLatch;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.embed.swing.JFXPanel;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javax.swing.AbstractAction;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.LifecycleManager;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.windows.TopComponent;
import org.openide.util.NbBundle.Messages;
import org.openide.util.Utilities;
import org.openide.util.lookup.AbstractLookup;

/**
 * Top component which displays something.
 */
@ConvertAsProperties(
        dtd = "-//com.tools//Tools//EN",
        autostore = false
)
@TopComponent.Description(
        preferredID = "ToolsTopComponent",
        //iconBase="SET/PATH/TO/ICON/HERE", 
        persistenceType = TopComponent.PERSISTENCE_ALWAYS
)
@TopComponent.Registration(mode = "explorer", openAtStartup = true)
@ActionID(category = "Window", id = "com.tools.ToolsTopComponent")
@ActionReference(path = "Menu/Window" /*, position = 333 */)
@TopComponent.OpenActionRegistration(
        displayName = "#CTL_ToolsAction",
        preferredID = "ToolsTopComponent"
)
@Messages({
    "CTL_ToolsAction=Tools",
    "CTL_ToolsTopComponent=Tools Window",
    "HINT_ToolsTopComponent=This is a Tools window"
})
public final class ToolsTopComponent extends TopComponent {
    
    private static JFXPanel fxPanel;
    private HomeController homeController;
    //private FOVController FOVController;
    //private CameraViewController cameraViewController;
    //private CameraPositionController cameraPositionController;
    
    //public static boolean first = true;

    public ToolsTopComponent() {
        initComponents();
        setName(Bundle.CTL_ToolsTopComponent());
        setToolTipText(Bundle.HINT_ToolsTopComponent());
        
        /*assosiateLookup(ExplorerUtils.createLookup(em, this.getActionMap()));
        getActionMap.put("ZoomAction", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createScene2();
            }
        });*/
        //System.out.println(ChosenTool.getToolNumber());
        setLayout(new BorderLayout());
        init();
    }
    
    private void init() {
        fxPanel = new JFXPanel();
        add(fxPanel, BorderLayout.CENTER);
        Platform.setImplicitExit(false);
        Platform.runLater(() -> {
            createScene(0);
        });
    }
    
    ChangeListener<Integer> cl = (v, oldValue, newValue) -> {
        //createScene(Integer.parseInt(newValue.toString()));
        createScene(newValue);
    };
    
    private void addToolBarListener(ChangeListener cl) {
        ChosenTool.toolProperty().addListener(cl); 
    }
    
    private void removeToolBarListener(ChangeListener cl) {
        ChosenTool.toolProperty().removeListener(cl);
    }
    
    private void createScene(Integer toolNum) {
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
                    location = getClass().getResource("CameraPosition.fxml");
                    break;
                case 3:
                    location = getClass().getResource("FOV.fxml");
                    break;
                default:
                    location = getClass().getResource("Home.fxml");
                    break;
            }
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(location);
            fxmlLoader.setBuilderFactory(new JavaFXBuilderFactory());

            Parent root = (Parent)fxmlLoader.load(location.openStream());
            Scene scene = new Scene(root);
            fxPanel.setScene(scene);
            switch (toolNum) {
                case 0:
                    homeController = (HomeController)fxmlLoader.getController();
                    break;
                case 1:
                    //cameraViewController = (CameraViewController)fxmlLoader.getController();
                    break;
                case 2:
                    //cameraPositionController = (CameraPositionController)fxmlLoader.getController();
                    break;
                case 3:
                    //FOVController = (FOVController)fxmlLoader.getController();
                    break;
                default:
                    homeController = (HomeController)fxmlLoader.getController();
                    break;
            }
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }
    
    /*private void createHomeScene() {
        try {
            URL location = getClass().getResource("Home.fxml");
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(location);
            fxmlLoader.setBuilderFactory(new JavaFXBuilderFactory());

            Parent root = (Parent)fxmlLoader.load(location.openStream());
            Scene scene = new Scene(root);
            fxPanel.setScene(scene);
            homeController = (HomeController)fxmlLoader.getController();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }
    
    private void createFOVScene() {
        try {
            URL location = getClass().getResource("FOV.fxml");
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(location);
            fxmlLoader.setBuilderFactory(new JavaFXBuilderFactory());

            Parent root = (Parent)fxmlLoader.load(location.openStream());
            Scene scene = new Scene(root);
            fxPanel.setScene(scene);
            FOVController = (FOVController)fxmlLoader.getController();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
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
