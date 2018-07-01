package org.spbu.histology.toolbar;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;

@ActionID(
        category = "Edit",
        id = "org.spbu.histology.toolbar.CameraView"
)
@ActionRegistration(
        iconBase = "org/spbu/histology/toolbar/eye.png",
        displayName = "#CTL_CameraView"
)
@ActionReference(path = "Toolbars/File", position = 325)
@Messages("CTL_CameraView=Управление камерой")
public final class CameraViewAction implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
        ChosenTool.setToolNumber(1);
    }
}
