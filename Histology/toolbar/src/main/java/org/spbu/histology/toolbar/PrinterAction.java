package org.spbu.histology.toolbar;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javafx.application.Platform;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;

@ActionID(
        category = "Edit",
        id = "org.spbu.histology.toolbar.PrinterAction"
)
@ActionRegistration(
        iconBase = "org/spbu/histology/toolbar/3d-printer.png",
        displayName = "#CTL_PrinterAction"
)
@ActionReference(path = "Toolbars/File", position = 393)
@Messages("CTL_PrinterAction=3D Printer")
public final class PrinterAction implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
        Platform.runLater(() -> SaveToSTLBox.display());
    }
}
