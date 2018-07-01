package org.spbu.histology.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javafx.application.Platform;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;
import org.spbu.histology.util.Manual;

@ActionID(
        category = "Edit",
        id = "org.spbu.histology.menu.InstructionAction"
)
@ActionRegistration(
        displayName = "#CTL_InstructionAction"
)
@ActionReference(path = "Menu/Help", position = 100, separatorAfter = 150)
@Messages("CTL_InstructionAction=Инструкция")
public final class InstructionAction implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
        Platform.runLater(() -> Manual.display());
    }
}
