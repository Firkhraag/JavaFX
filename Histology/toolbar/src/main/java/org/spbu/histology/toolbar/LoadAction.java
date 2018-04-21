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
        id = "org.spbu.histology.toolbar.LoadAction"
)
@ActionRegistration(
        iconBase = "org/spbu/histology/toolbar/load.png",
        displayName = "#CTL_LoadAction"
)
@ActionReference(path = "Toolbars/File", position = 387)
@Messages("CTL_LoadAction=Load")
public final class LoadAction implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
        Platform.runLater(() -> LoadBox.display());
    }
}
