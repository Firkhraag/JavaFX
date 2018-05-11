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
        id = "org.spbu.histology.toolbar.SaveAction"
)
@ActionRegistration(
        iconBase = "org/spbu/histology/toolbar/save-file-option.png",
        displayName = "#CTL_SaveAction"
)
@ActionReference(path = "Toolbars/File", position = 375)
@Messages("CTL_SaveAction=Save")
public final class SaveAction implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
        //Platform.runLater(() -> SaveBox.display());
    }
}
