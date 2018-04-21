package org.spbu.histology.toolbar;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;

@ActionID(
        category = "Edit",
        id = "org.spbu.histology.toolbar.HomeAction"
)
@ActionRegistration(
        iconBase = "org/spbu/histology/toolbar/home.png",
        displayName = "#CTL_HomeAction"
)
@ActionReference(path = "Toolbars/File", position = 300)
@Messages("CTL_HomeAction=Home")
public final class HomeAction implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
        ChosenTool.setToolNumber(0);
    }
}
