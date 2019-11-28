/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.spbu.histology.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import javafx.application.Platform;
import javafx.scene.shape.Mesh;
import javafx.stage.FileChooser;
import javax.swing.filechooser.FileSystemView;
import org.openide.LifecycleManager;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.util.Lookup;
import org.openide.util.NbBundle.Messages;
import org.spbu.histology.model.HistionManager;
import org.spbu.histology.util.MeshUtils;

@ActionID(
        category = "Edit",
        id = "org.spbu.histology.menu.PrinterAction"
)
@ActionRegistration(
        displayName = "#CTL_PrinterAction"
)
@ActionReference(path = "Menu/Tools", position = 75, separatorAfter = 87)
@Messages("CTL_PrinterAction=Сохранить в .stl формате")
public final class PrinterAction implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {

        Platform.runLater(() -> {

            HistionManager hm = Lookup.getDefault().lookup(HistionManager.class);
            if (hm == null) {
                LifecycleManager.getDefault().exit();
            }

            FileChooser fileChooser = new FileChooser();

            //Set extension filter
            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("STL files (*.stl)", "*.stl");
            fileChooser.getExtensionFilters().add(extFilter);

            String userDirectoryString = FileSystemView.getFileSystemView().getDefaultDirectory().getPath();
            userDirectoryString += "\\HistologyApp" + System.getProperty("sun.arch.data.model") + "\\3DPrinter";
            File userDirectory = new File(userDirectoryString);
            if (!userDirectory.exists()) {
                userDirectory.mkdirs();
            }
            fileChooser.setInitialDirectory(userDirectory);
            File file = fileChooser.showSaveDialog(null);

            ArrayList<Mesh> meshList = new ArrayList<>();
            hm.getShapeMap().forEach((i, m) -> {
                meshList.add(m.getMesh());
            });

            if (hm.getShapeMap().isEmpty()) {
                return;
            }

            try {
                MeshUtils.mesh2STL(userDirectoryString
                        + "\\" + file.getName(), meshList);  // + ".stl"
            } catch (Exception ex) {

            }
        });
    }
}
